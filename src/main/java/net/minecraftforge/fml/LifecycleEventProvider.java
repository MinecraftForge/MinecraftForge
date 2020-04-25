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

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.forgespi.language.ILifecycleEvent;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public enum LifecycleEventProvider
{
    CONSTRUCT(()->new LifecycleEvent(ModLoadingStage.CONSTRUCT)),
    CREATE_REGISTRIES(()->new LifecycleEvent(ModLoadingStage.CREATE_REGISTRIES), ModList.inlineDispatcher),
    LOAD_REGISTRIES(()->new LifecycleEvent(ModLoadingStage.LOAD_REGISTRIES, LifecycleEvent.Progression.STAY), ModList.inlineDispatcher),
    SETUP(()->new LifecycleEvent(ModLoadingStage.COMMON_SETUP)),
    SIDED_SETUP(()->new LifecycleEvent(ModLoadingStage.SIDED_SETUP)),
    ENQUEUE_IMC(()->new LifecycleEvent(ModLoadingStage.ENQUEUE_IMC)),
    PROCESS_IMC(()->new LifecycleEvent(ModLoadingStage.PROCESS_IMC)),
    COMPLETE(()->new LifecycleEvent(ModLoadingStage.COMPLETE)),
    GATHERDATA(()->new GatherDataLifecycleEvent(ModLoadingStage.GATHERDATA), ModList.inlineDispatcher);

    private final Supplier<? extends LifecycleEvent> event;
    private final EventHandler<LifecycleEvent, Consumer<List<ModLoadingException>>,Executor, Runnable> eventDispatcher;
    private Supplier<Event> customEventSupplier;
    private LifecycleEvent.Progression progression = LifecycleEvent.Progression.NEXT;

    LifecycleEventProvider(Supplier<? extends LifecycleEvent> e)
    {
        this(e, ModList.parallelDispatcher);
    }

    LifecycleEventProvider(Supplier<? extends LifecycleEvent> e, EventHandler<LifecycleEvent, Consumer<List<ModLoadingException>>,Executor, Runnable> eventDispatcher)
    {
        this.event = e;
        this.eventDispatcher = eventDispatcher;
    }

    public void setCustomEventSupplier(Supplier<Event> eventSupplier) {
        this.customEventSupplier = eventSupplier;
    }

    public void changeProgression(LifecycleEvent.Progression progression) {
        this.progression = progression;
    }

    public void dispatch(Consumer<List<ModLoadingException>> errorHandler, final Executor executor, final Runnable ticker) {
        final LifecycleEvent lifecycleEvent = this.event.get();
        lifecycleEvent.setCustomEventSupplier(this.customEventSupplier);
        lifecycleEvent.changeProgression(this.progression);
        this.eventDispatcher.dispatchEvent(lifecycleEvent, errorHandler, executor, ticker);
    }


    public static class LifecycleEvent implements ILifecycleEvent<LifecycleEvent> {
        private final ModLoadingStage stage;
        private Supplier<Event> customEventSupplier;
        private Progression progression;
        LifecycleEvent(final ModLoadingStage stage)
        {
            this(stage, Progression.NEXT);
        }

        LifecycleEvent(final ModLoadingStage stage, final Progression progression) {
            this.stage = stage;
            this.progression = progression;
        }

        public ModLoadingStage fromStage()
        {
            return this.stage;
        }

        public ModLoadingStage toStage()
        {
            return progression.apply(this.stage);
        }

        public void setCustomEventSupplier(Supplier<Event> customEventSupplier) {
            this.customEventSupplier = customEventSupplier;
        }

        public void changeProgression(Progression p) {
            this.progression = p;
        }

        public Event getOrBuildEvent(ModContainer modContainer)
        {
            if (customEventSupplier!=null) return customEventSupplier.get();

            return stage.getModEvent(modContainer);
        }

        @Override
        public String toString() {
            return "LifecycleEvent:"+stage;
        }

        public enum Progression {
            NEXT((current)-> ModLoadingStage.values()[current.ordinal()+1]),
            STAY(Function.identity());

            private final Function<ModLoadingStage, ModLoadingStage> edge;

            Progression(Function<ModLoadingStage, ModLoadingStage> edge) {
                this.edge = edge;
            }

            public ModLoadingStage apply(ModLoadingStage in) {
                return this.edge.apply(in);
            }
        }
    }

    private static class GatherDataLifecycleEvent extends LifecycleEvent {
        GatherDataLifecycleEvent(final ModLoadingStage stage) {
            super(stage);
        }

        @Override
        public ModLoadingStage fromStage() {
            return ModLoadingStage.COMMON_SETUP;
        }

        @Override
        public ModLoadingStage toStage() {
            return ModLoadingStage.DONE;
        }
    }

    public interface EventHandler<T extends LifecycleEvent, U extends Consumer<? extends List<? super ModLoadingException>>, V extends Executor, R extends Runnable>  {
        void dispatchEvent(T event, U exceptionHandler, V executor, R ticker);
    }
}
