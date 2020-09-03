/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml;

import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import net.minecraftforge.registries.GameData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public enum ModLoadingStage
{
    ERROR(),
    VALIDATE(),
    CONSTRUCT(FMLConstructModEvent.class),
    CREATE_REGISTRIES(()->Stream.of(EventGenerator.fromFunction(RegistryEvent.NewRegistry::new)), EventDispatcher.identity()),
    LOAD_REGISTRIES(GameData::generateRegistryEvents, GameData.buildRegistryEventDispatch()),
    COMMON_SETUP(FMLCommonSetupEvent.class),
    SIDED_SETUP(DistExecutor.unsafeRunForDist(()->()->FMLClientSetupEvent.class, ()->()->FMLDedicatedServerSetupEvent.class)),
    ENQUEUE_IMC(InterModEnqueueEvent.class),
    PROCESS_IMC(InterModProcessEvent.class),
    COMPLETE(FMLLoadCompleteEvent.class),
    DONE();

    private final Supplier<Stream<EventGenerator<?>>> eventFunctionStream;
    private final EventDispatcher<?> eventManager;
    private final Optional<Class<? extends ParallelDispatchEvent>> parallelEventClass;
    private final ThreadSelector threadSelector;
    private final BiFunction<Executor, CompletableFuture<List<Throwable>>, CompletableFuture<List<Throwable>>> finalActivityGenerator;
    private DeferredWorkQueue deferredWorkQueue;

    ModLoadingStage(Class<? extends ParallelDispatchEvent> parallelClass) {
        final EventGenerator<?> event = EventGenerator.fromFunction(LamdbaExceptionUtils.rethrowFunction((ModContainer mc) -> parallelClass.getConstructor(ModContainer.class).newInstance(mc)));
        this.eventFunctionStream = ()->Stream.of(event);
        this.threadSelector = ThreadSelector.PARALLEL;
        this.eventManager = EventDispatcher.identity();
        this.parallelEventClass = Optional.of(parallelClass);
        deferredWorkQueue = new DeferredWorkQueue(this, parallelClass);
        this.finalActivityGenerator = (e, prev) -> prev.thenApplyAsync((List<Throwable> t) -> {
            deferredWorkQueue.runTasks();
            return t;
        }, e);
    }

    <T extends Event & IModBusEvent> ModLoadingStage(Supplier<Stream<EventGenerator<?>>> eventStream, EventDispatcher<?> eventManager) {
        this.eventFunctionStream = eventStream;
        this.parallelEventClass = Optional.empty();
        this.eventManager = eventManager;
        this.threadSelector = ThreadSelector.SYNC;
        this.finalActivityGenerator = (e, prev) ->prev.thenApplyAsync(Function.identity(), e);
    }

    ModLoadingStage() {
        this(ParallelDispatchEvent.class);
    }

    public <T extends Event & IModBusEvent> CompletableFuture<List<Throwable>> buildTransition(final Executor syncExecutor, final Executor parallelExecutor) {
        return buildTransition(syncExecutor, parallelExecutor, e->CompletableFuture.runAsync(()->{}, e), e->CompletableFuture.runAsync(()->{}, e));
    }
    @SuppressWarnings("unchecked")
    public <T extends Event & IModBusEvent> CompletableFuture<List<Throwable>> buildTransition(final Executor syncExecutor, final Executor parallelExecutor, Function<Executor, CompletableFuture<Void>> preSyncTask, Function<Executor, CompletableFuture<Void>> postSyncTask) {
        List<CompletableFuture<List<Throwable>>> cfs = new ArrayList<>();
        EventDispatcher<T> em = (EventDispatcher<T>) this.eventManager;
        eventFunctionStream.get()
                .map(f->(EventGenerator<T>)f)
                .reduce((head, tail)->{
                    cfs.add(ModList.get()
                            .futureVisitor(head, em, ModLoadingStage::currentState)
                            .apply(threadSelector.apply(syncExecutor, parallelExecutor))
                    );
                    return tail;
                })
                .ifPresent(last->cfs.add(ModList.get().futureVisitor(last, em, ModLoadingStage::nextState).apply(threadSelector.apply(syncExecutor, parallelExecutor))));
        final CompletableFuture<Void> preSyncTaskCF = preSyncTask.apply(syncExecutor);
        final CompletableFuture<List<Throwable>> eventDispatchCF = ModList.gather(cfs).thenCompose(ModList::completableFutureFromExceptionList);
        final CompletableFuture<List<Throwable>> postEventDispatchCF = preSyncTaskCF.thenComposeAsync(n -> eventDispatchCF, parallelExecutor).thenApply(r -> {
            postSyncTask.apply(syncExecutor);
            return r;
        });
        return this.finalActivityGenerator.apply(syncExecutor, postEventDispatchCF);
    }

    ModLoadingStage nextState(Throwable exception) {
        return exception != null ? ERROR : values()[this.ordinal()+1];
    }

    ModLoadingStage currentState(Throwable exception) {
        return exception != null ? ERROR : this;
    }

    public Optional<Class<? extends ParallelDispatchEvent>> getParallelEventClass() {
        return parallelEventClass;
    }
    enum ThreadSelector implements BinaryOperator<Executor> {
        SYNC((sync, parallel)->sync),
        PARALLEL((sync, parallel)->parallel);

        private final BinaryOperator<Executor> selector;

        ThreadSelector(final BinaryOperator<Executor> selector) {
            this.selector = selector;
        }

        @Override
        public Executor apply(final Executor sync, final Executor parallel) {
            return this.selector.apply(sync, parallel);
        }
    }

    public DeferredWorkQueue getDeferredWorkQueue() {
        return deferredWorkQueue;
    }

    public interface EventGenerator<T extends Event & IModBusEvent> extends Function<ModContainer, T> {
        static <FN extends Event & IModBusEvent> EventGenerator<FN> fromFunction(Function<ModContainer, FN> fn) {
            return fn::apply;
        }
    }
    public interface EventDispatcher<T extends Event & IModBusEvent> extends Function<Consumer<? super T>, Consumer<? super T>> {
        static <FN extends Event & IModBusEvent> EventDispatcher<FN> identity() {
            return consumer -> consumer;
        }
    }
}
