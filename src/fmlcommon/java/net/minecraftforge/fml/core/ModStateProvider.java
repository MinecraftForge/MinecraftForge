/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.fml.core;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IModLoadingState;
import net.minecraftforge.fml.IModStateProvider;
import net.minecraftforge.fml.ModLoadingPhase;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.ModLoadingState;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.loading.FMLPaths;

import java.util.List;

public class ModStateProvider implements IModStateProvider {
    final ModLoadingState ERROR = ModLoadingState.empty("ERROR", "", ModLoadingPhase.ERROR);
    private final ModLoadingState VALIDATE = ModLoadingState.empty("VALIDATE", "", ModLoadingPhase.GATHER);
    final ModLoadingState CONSTRUCT = ModLoadingState.withTransition("CONSTRUCT", "VALIDATE", ml->"Constructing %d mods".formatted(ml.size()), ModLoadingPhase.GATHER, new ParallelTransition(ModLoadingStage.CONSTRUCT, FMLConstructModEvent.class));
    private final ModLoadingState CONFIG_LOAD = ModLoadingState.withInline("CONFIG_LOAD", "", ModLoadingPhase.LOAD, ml->{
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ()->()-> ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.CLIENT, FMLPaths.CONFIGDIR.get()));
        ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.COMMON, FMLPaths.CONFIGDIR.get());
    });
    private final ModLoadingState COMMON_SETUP = ModLoadingState.withTransition("COMMON_SETUP", "CONFIG_LOAD", ModLoadingPhase.LOAD, new ParallelTransition(ModLoadingStage.CONSTRUCT, FMLCommonSetupEvent.class));
    private final ModLoadingState SIDED_SETUP = ModLoadingState.withTransition("SIDED_SETUP", "COMMON_SETUP", ModLoadingPhase.LOAD, new ParallelTransition(ModLoadingStage.SIDED_SETUP, DistExecutor.unsafeRunForDist(()->()-> FMLClientSetupEvent.class, ()->()-> FMLDedicatedServerSetupEvent.class)));
    private final ModLoadingState ENQUEUE_IMC = ModLoadingState.withTransition("ENQUEUE_IMC", "", ModLoadingPhase.COMPLETE, new ParallelTransition(ModLoadingStage.ENQUEUE_IMC, InterModEnqueueEvent.class));
    private final ModLoadingState PROCESS_IMC = ModLoadingState.withTransition("PROCESS_IMC", "ENQUEUE_IMC",ModLoadingPhase.COMPLETE, new ParallelTransition(ModLoadingStage.PROCESS_IMC, InterModProcessEvent.class));
    private final ModLoadingState COMPLETE = ModLoadingState.withTransition("COMPLETE", "PROCESS_IMC", ml->"completing load of %d mods".formatted(ml.size()), ModLoadingPhase.COMPLETE, new ParallelTransition(ModLoadingStage.COMPLETE, FMLLoadCompleteEvent.class));
    private final ModLoadingState DONE = ModLoadingState.empty("DONE", "", ModLoadingPhase.DONE);

    @Override
    public List<IModLoadingState> getAllStates() {
        return List.of(ERROR, VALIDATE, CONSTRUCT, CONFIG_LOAD, COMMON_SETUP, SIDED_SETUP, ENQUEUE_IMC, PROCESS_IMC, COMPLETE, DONE);
    }
}
