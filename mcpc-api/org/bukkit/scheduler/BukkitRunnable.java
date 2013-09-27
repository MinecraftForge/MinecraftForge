package org.bukkit.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * This class is provided as an easy way to handle scheduling tasks.
 */
public abstract class BukkitRunnable implements Runnable {
    private int taskId = -1;

    /**
     * Attempts to cancel this task.
     *
     * @throws IllegalStateException if task was not scheduled yet
     */
    public synchronized void cancel() throws IllegalStateException {
        Bukkit.getScheduler().cancelTask(getTaskId());
    }

    /**
     * Schedules this in the Bukkit scheduler to run on next tick.
     *
     * @param plugin the reference to the plugin scheduling task
     * @return a BukkitTask that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalStateException if this was already scheduled
     * @see BukkitScheduler#runTask(Plugin, Runnable)
     */
    public synchronized BukkitTask runTask(Plugin plugin) throws IllegalArgumentException, IllegalStateException {
        checkState();
        return setupId(Bukkit.getScheduler().runTask(plugin, this));
    }

    /**
     * <b>Asynchronous tasks should never access any API in Bukkit.
     *    Great care should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <br>
     * <br>Schedules this in the Bukkit scheduler to run asynchronously.
     *
     * @param plugin the reference to the plugin scheduling task
     * @return a BukkitTask that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalStateException if this was already scheduled
     * @see BukkitScheduler#runTaskAsynchronously(Plugin, Runnable)
     */
    public synchronized BukkitTask runTaskAsynchronously(Plugin plugin) throws IllegalArgumentException, IllegalStateException  {
        checkState();
        return setupId(Bukkit.getScheduler().runTaskAsynchronously(plugin, this));
    }

    /**
     * Schedules this to run after the specified number of server ticks.
     *
     * @param plugin the reference to the plugin scheduling task
     * @param delay the ticks to wait before running the task
     * @return a BukkitTask that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalStateException if this was already scheduled
     * @see BukkitScheduler#runTaskLater(Plugin, Runnable, long)
     */
    public synchronized BukkitTask runTaskLater(Plugin plugin, long delay) throws IllegalArgumentException, IllegalStateException  {
        checkState();
        return setupId(Bukkit.getScheduler().runTaskLater(plugin, this, delay));
    }

    /**
     * <b>Asynchronous tasks should never access any API in Bukkit.
     *    Great care should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <br>
     * <br>Schedules this to run asynchronously after the specified number of server ticks.
     *
     * @param plugin the reference to the plugin scheduling task
     * @param delay the ticks to wait before running the task
     * @return a BukkitTask that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalStateException if this was already scheduled
     * @see BukkitScheduler#runTaskLaterAsynchronously(Plugin, Runnable, long)
     */
    public synchronized BukkitTask runTaskLaterAsynchronously(Plugin plugin, long delay) throws IllegalArgumentException, IllegalStateException  {
        checkState();
        return setupId(Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, this, delay));
    }

    /**
     * Schedules this to repeatedly run until cancelled, starting after the specified number of server ticks.
     *
     * @param plugin the reference to the plugin scheduling task
     * @param delay the ticks to wait before running the task
     * @param period the ticks to wait between runs
     * @return a BukkitTask that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalStateException if this was already scheduled
     * @see BukkitScheduler#runTaskTimer(Plugin, Runnable, long, long)
     */
    public synchronized BukkitTask runTaskTimer(Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException  {
        checkState();
        return setupId(Bukkit.getScheduler().runTaskTimer(plugin, this, delay, period));
    }

    /**
     * <b>Asynchronous tasks should never access any API in Bukkit.
     *    Great care should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <br>
     * <br>Schedules this to repeatedly run asynchronously until cancelled, starting after the specified number of server ticks.
     *
     * @param plugin the reference to the plugin scheduling task
     * @param delay the ticks to wait before running the task for the first time
     * @param period the ticks to wait between runs
     * @return a BukkitTask that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalStateException if this was already scheduled
     * @see BukkitScheduler#runTaskTimerAsynchronously(Plugin, Runnable, long, long)
     */
    public synchronized BukkitTask runTaskTimerAsynchronously(Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException  {
        checkState();
        return setupId(Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, delay, period));
    }

    /**
     * Gets the task id for this runnable.
     *
     * @return the task id that this runnable was scheduled as
     * @throws IllegalStateException if task was not scheduled yet
     */
    public synchronized int getTaskId() throws IllegalStateException {
        final int id = taskId;
        if (id == -1) {
            throw new IllegalStateException("Not scheduled yet");
        }
        return id;
    }

    private void checkState() {
        if (taskId != -1) {
            throw new IllegalStateException("Already scheduled as " + taskId);
        }
    }

    private BukkitTask setupId(final BukkitTask task) {
        this.taskId = task.getTaskId();
        return task;
    }
}
