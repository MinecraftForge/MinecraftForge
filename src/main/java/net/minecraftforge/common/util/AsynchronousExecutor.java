package net.minecraftforge.common.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import net.minecraftforge.fml.common.FMLLog;


/**
 * Executes tasks using a multi-stage process executor. Synchronous executions are via {@link AsynchronousExecutor#finishActive()} or the {@link AsynchronousExecutor#get(Object)} methods.
 * <li \> Stage 1 creates the object from a parameter, and is usually called asynchronously.
 * <li \> Stage 2 takes the parameter and object from stage 1 and does any synchronous processing to prepare it.
 * <li \> Stage 3 takes the parameter and object from stage 1, as well as a callback that was registered, and performs any synchronous calculations.
 *
 * @param <P> The type of parameter you provide to make the object that will be created. It should implement {@link Object#hashCode()} and {@link Object#equals(Object)} if you want to get the value early.
 * @param <T> The type of object you provide. This is created in stage 1, and passed to stage 2, 3, and returned if get() is called.
 * @param <C> The type of callback you provide. You may register many of these to be passed to the provider in stage 3, one at a time.
 * @param <E> A type of exception you may throw and expect to be handled by the main thread
 * @author Wesley Wolfe (c) 2012, 2014
 */
public final class AsynchronousExecutor<P, T, C, E extends Throwable> {

    public static interface CallBackProvider<P, T, C, E extends Throwable> extends ThreadFactory {

        /**
         * Normally an asynchronous call, but can be synchronous
         *
         * @param parameter parameter object provided
         * @return the created object
         */
        T callStage1(P parameter) throws E;

        /**
         * Synchronous call
         *
         * @param parameter parameter object provided
         * @param object    the previously created object
         */
        void callStage2(P parameter, T object) throws E;

        /**
         * Synchronous call, called multiple times, once per registered callback
         *
         * @param parameter parameter object provided
         * @param object    the previously created object
         * @param callback  the current callback to execute
         */
        void callStage3(P parameter, T object, C callback) throws E;
    }

    @SuppressWarnings("rawtypes")
    static final AtomicIntegerFieldUpdater STATE_FIELD = AtomicIntegerFieldUpdater.newUpdater(AsynchronousExecutor.Task.class, "state");

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static boolean set(AsynchronousExecutor.Task $this, int expected, int value) {
        return STATE_FIELD.compareAndSet($this, expected, value);
    }

    class Task implements Runnable {
        static final int PENDING = 0x0;
        static final int STAGE_1_ASYNC = PENDING + 1;
        static final int STAGE_1_SYNC = STAGE_1_ASYNC + 1;
        static final int STAGE_1_COMPLETE = STAGE_1_SYNC + 1;
        static final int FINISHED = STAGE_1_COMPLETE + 1;

        volatile int state = PENDING;
        final P parameter;
        T object;
        final List<C> callbacks = new LinkedList<C>();
        E t = null;

        Task(final P parameter) {
            this.parameter = parameter;
        }

        public void run() {
            if (initAsync()) {
                finished.add(this);
            }
        }

        boolean initAsync() {
            if (set(this, PENDING, STAGE_1_ASYNC)) {
                boolean ret = true;

                try {
                    init();
                } finally {
                    if (set(this, STAGE_1_ASYNC, STAGE_1_COMPLETE)) {
                        // No one is/will be waiting
                    } else {
                        // We know that the sync thread will be waiting
                        synchronized (this) {
                            if (state != STAGE_1_SYNC) {
                                // They beat us to the synchronized block
                                this.notifyAll();
                            } else {
                                // We beat them to the synchronized block
                            }
                            state = STAGE_1_COMPLETE; // They're already synchronized, atomic locks are not needed
                        }
                        // We want to return false, because we know a synchronous task already handled the finish()
                        ret = false; // Don't return inside finally; VERY bad practice.
                    }
                }

                return ret;
            } else {
                return false;
            }
        }

        void initSync() {
            if (set(this, PENDING, STAGE_1_COMPLETE)) {
                // If we succeed that variable switch, good as done
                init();
            } else if (set(this, STAGE_1_ASYNC, STAGE_1_SYNC)) {
                // Async thread is running, but this shouldn't be likely; we need to sync to wait on them because of it.
                synchronized (this) {
                    if (set(this, STAGE_1_SYNC, PENDING)) { // They might NOT synchronized yet, atomic lock IS needed
                        // We are the first into the lock
                        while (state != STAGE_1_COMPLETE) {
                            try {
                                this.wait();
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                throw new RuntimeException("Unable to handle interruption on " + parameter, e);
                            }
                        }
                    } else {
                        // They beat us to the synchronized block
                    }
                }
            } else {
                // Async thread is not pending, the more likely situation for a task not pending
            }
        }

        @SuppressWarnings("unchecked")
        void init() {
            try {
                object = provider.callStage1(parameter);
            } catch (final Throwable t) {
                this.t = (E) t;
            }
        }

        @SuppressWarnings("unchecked")
        T get() throws E {
            initSync();
            if (callbacks.isEmpty()) {
                // 'this' is a placeholder to prevent callbacks from being empty during finish call
                // See get method below
                callbacks.add((C) this);
            }
            finish();
            return object;
        }

        void finish() throws E {
            switch (state) {
                default:
                case PENDING:
                case STAGE_1_ASYNC:
                case STAGE_1_SYNC:
                    throw new IllegalStateException("Attempting to finish unprepared(" + state + ") task(" + parameter + ")");
                case STAGE_1_COMPLETE:
                    try {
                        if (t != null) {
                            throw t;
                        }
                        if (callbacks.isEmpty()) {
                            return;
                        }

                        final CallBackProvider<P, T, C, E> provider = AsynchronousExecutor.this.provider;
                        final P parameter = this.parameter;
                        final T object = this.object;

                        provider.callStage2(parameter, object);
                        for (C callback : callbacks) {
                            if (callback == this) {
                                // 'this' is a placeholder to prevent callbacks from being empty on a get() call
                                // See get method above
                                continue;
                            }
                            provider.callStage3(parameter, object, callback);
                        }
                    } finally {
                        tasks.remove(parameter);
                        state = FINISHED;
                    }
                case FINISHED:
            }
        }

        boolean drop() {
            if (set(this, PENDING, FINISHED)) {
                // If we succeed that variable switch, good as forgotten
                tasks.remove(parameter);
                return true;
            } else {
                // We need the async thread to finish normally to properly dispose of the task
                return false;
            }
        }
    }

    final CallBackProvider<P, T, C, E> provider;
    final Queue<Task> finished = new ConcurrentLinkedQueue<Task>();
    final Map<P, Task> tasks = new HashMap<P, Task>();
    final ThreadPoolExecutor pool;

    /**
     * Uses a thread pool to pass executions to the provider.
     * @see AsynchronousExecutor
     */
    public AsynchronousExecutor(final CallBackProvider<P, T, C, E> provider, final int coreSize) {
        if (provider == null) {
            throw new IllegalArgumentException("Provider cannot be null");
        }
        this.provider = provider;

        // We have an unbound queue size so do not need a max thread size
        pool = new ThreadPoolExecutor(coreSize, Integer.MAX_VALUE, 60l, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), provider);
    }

    /**
     * Adds a callback to the parameter provided, adding parameter to the queue if needed.
     * <p>
     * This should always be synchronous.
     */
    public void add(P parameter, C callback) {
        Task task = tasks.get(parameter);
        if (task == null) {
            tasks.put(parameter, task = new Task(parameter));
            pool.execute(task);
        }
        task.callbacks.add(callback);
    }

    /**
     * This removes a particular callback from the specified parameter.
     * <p>
     * If no callbacks remain for a given parameter, then the {@link CallBackProvider CallBackProvider's} stages may be omitted from execution.
     * Stage 3 will have no callbacks, stage 2 will be skipped unless a {@link #get(Object)} is used, and stage 1 will be avoided on a best-effort basis.
     * <p>
     * Subsequent calls to {@link #getSkipQueue(Object)} will always work.
     * <p>
     * Subsequent calls to {@link #get(Object)} might work.
     * <p>
     * This should always be synchronous
     * @return true if no further execution for the parameter is possible, such that, no exceptions will be thrown in {@link #finishActive()} for the parameter, and {@link #get(Object)} will throw an {@link IllegalStateException}, false otherwise
     * @throws IllegalStateException if parameter is not in the queue anymore
     * @throws IllegalStateException if the callback was not specified for given parameter
     */
    public boolean drop(P parameter, C callback) throws IllegalStateException {
        final Task task = tasks.get(parameter);
        if (task == null) {
            // Print debug info for QueuedChunk and avoid crash
            //throw new IllegalStateException("Unknown " + parameter);
            FMLLog.info("Unknown %s", parameter);
            FMLLog.info("This should not happen. Please report this error to Forge.");
            return false;
        }
        if (!task.callbacks.remove(callback)) {
            throw new IllegalStateException("Unknown " + callback + " for " + parameter);
        }
        if (task.callbacks.isEmpty()) {
            return task.drop();
        }
        return false;
    }

    /**
     * This method attempts to skip the waiting period for said parameter.
     * <p>
     * This should always be synchronous.
     * @throws IllegalStateException if the parameter is not in the queue anymore, or sometimes if called from asynchronous thread
     */
    public T get(P parameter) throws E, IllegalStateException {
        final Task task = tasks.get(parameter);
        if (task == null) {
            throw new IllegalStateException("Unknown " + parameter);
        }
        return task.get();
    }

    /**
     * Processes a parameter as if it was in the queue, without ever passing to another thread.
     */
    public T getSkipQueue(P parameter) throws E {
        return skipQueue(parameter);
    }

    /**
     * Processes a parameter as if it was in the queue, without ever passing to another thread.
     */
    public T getSkipQueue(P parameter, C callback) throws E {
        final T object = skipQueue(parameter);
        provider.callStage3(parameter, object, callback);
        return object;
    }

    /**
     * Processes a parameter as if it was in the queue, without ever passing to another thread.
     */
    public T getSkipQueue(P parameter, @SuppressWarnings("unchecked")C... callbacks) throws E {
        final CallBackProvider<P, T, C, E> provider = this.provider;
        final T object = skipQueue(parameter);
        for (C callback : callbacks) {
            provider.callStage3(parameter, object, callback);
        }
        return object;
    }

    /**
     * Processes a parameter as if it was in the queue, without ever passing to another thread.
     */
    public T getSkipQueue(P parameter, Iterable<C> callbacks) throws E {
        final CallBackProvider<P, T, C, E> provider = this.provider;
        final T object = skipQueue(parameter);
        for (C callback : callbacks) {
            provider.callStage3(parameter, object, callback);
        }
        return object;
    }

    private T skipQueue(P parameter) throws E {
        Task task = tasks.get(parameter);
        if (task != null) {
            return task.get();
        }
        T object = provider.callStage1(parameter);
        provider.callStage2(parameter, object);
        return object;
    }

    /**
     * This is the 'heartbeat' that should be called synchronously to finish any pending tasks
     */
    public void finishActive() throws E {
        final Queue<Task> finished = this.finished;
        while (!finished.isEmpty()) {
            finished.poll().finish();
        }
    }

    public void setActiveThreads(final int coreSize) {
        pool.setCorePoolSize(coreSize);
    }
}