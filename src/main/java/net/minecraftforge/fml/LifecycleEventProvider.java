/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

import net.minecraftforge.fml.event.lifecycle.ModLifecycleEvent;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.forgespi.language.ILifecycleEvent;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public enum LifecycleEventProvider
{
    CONSTRUCT(()->new LifecycleEvent(ModLoadingStage.CONSTRUCT)),
    SETUP(()->new LifecycleEvent(ModLoadingStage.COMMON_SETUP)),
    SIDED_SETUP(()->new LifecycleEvent(ModLoadingStage.SIDED_SETUP)),
    ENQUEUE_IMC(()->new LifecycleEvent(ModLoadingStage.ENQUEUE_IMC)),
    PROCESS_IMC(()->new LifecycleEvent(ModLoadingStage.PROCESS_IMC)),
    COMPLETE(()->new LifecycleEvent(ModLoadingStage.COMPLETE));

    public void dispatch(Consumer<List<ModLoadingException>> errorHandler) {
        ModList.get().dispatchLifeCycleEvent(this.event.get(), errorHandler);
    }
    private final Supplier<? extends LifecycleEvent> event;

    LifecycleEventProvider(Supplier<? extends LifecycleEvent> e)
    {
        event = e;
    }


    public static class LifecycleEvent implements ILifecycleEvent<LifecycleEvent> {
        private final ModLoadingStage stage;

        public LifecycleEvent(ModLoadingStage stage)
        {
            this.stage = stage;
        }

        public ModLoadingStage fromStage()
        {
            return this.stage;
        }

        public ModLoadingStage toStage()
        {
            return ModLoadingStage.values()[this.stage.ordinal()+1];
        }

        public ModLifecycleEvent buildModEvent(FMLModContainer fmlModContainer)
        {
            return stage.getModEvent(fmlModContainer);
        }
    }
}
