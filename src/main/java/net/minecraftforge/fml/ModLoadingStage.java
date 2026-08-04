/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
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
