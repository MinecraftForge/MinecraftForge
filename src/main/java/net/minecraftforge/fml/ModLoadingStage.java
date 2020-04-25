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

import net.minecraftforge.fml.event.lifecycle.*;

import java.util.function.Function;
import java.util.function.Supplier;

public enum ModLoadingStage
{
    ERROR(null),
    VALIDATE(null),
    CONSTRUCT(null),
    CREATE_REGISTRIES(null),
    LOAD_REGISTRIES(null),
    COMMON_SETUP(()-> FMLCommonSetupEvent::new),
    SIDED_SETUP(SidedProvider.SIDED_SETUP_EVENT::get),
    ENQUEUE_IMC(()-> InterModEnqueueEvent::new),
    PROCESS_IMC(()-> InterModProcessEvent::new),
    COMPLETE(()-> FMLLoadCompleteEvent::new),
    DONE(null),
    GATHERDATA(ModLoader.get()::getDataGeneratorEvent);
    private final Supplier<Function<ModContainer, ModLifecycleEvent>> modLifecycleEventFunction;

    ModLoadingStage(Supplier<Function<ModContainer, ModLifecycleEvent>> modLifecycleEventFunction)
    {
        this.modLifecycleEventFunction = modLifecycleEventFunction;
    }

    public ModLifecycleEvent getModEvent(ModContainer modContainer)
    {
        return modLifecycleEventFunction.get().apply(modContainer);
    }
}
