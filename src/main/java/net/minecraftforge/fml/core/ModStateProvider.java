/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml.core;

import net.minecraftsingularity.api.distmarker.Dist;
import net.minecraftsingularity.fml.IModLoadingState;
import net.minecraftsingularity.fml.IModStateProvider;
import net.minecraftsingularity.fml.ModContainer;
import net.minecraftsingularity.fml.ModList;
import net.minecraftsingularity.fml.ModLoadingPhase;
import net.minecraftsingularity.fml.ModLoadingStage;
import net.minecraftsingularity.fml.ModLoadingState;
import net.minecraftsingularity.fml.config.ConfigTracker;
import net.minecraftsingularity.fml.config.ModConfig;
import net.minecraftsingularity.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftsingularity.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftsingularity.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftsingularity.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftsingularity.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftsingularity.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftsingularity.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftsingularity.fml.event.lifecycle.ParallelDispatchEvent;
import net.minecraftsingularity.fml.loading.FMLEnvironment;
import net.minecraftsingularity.fml.loading.FMLPaths;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Provider for the core FML mod loading states.
 */
public class ModStateProvider implements IModStateProvider {
    /**
     * The special mod loading state for exceptional situations and error handling.
     *
     * @see ModLoadingPhase#ERROR
     */
    public static final ModLoadingState ERROR = ModLoadingState.of("ERROR", ModLoadingPhase.ERROR).empty();

    /**
     * First {@linkplain ModLoadingPhase#GATHER gathering state}, for the validation of the mod list.
     * TODO: figure out where this is used and why this exists instead of CONSTRUCT being the first state
     */
    public static final ModLoadingState VALIDATE = ModLoadingState.of("VALIDATE", ModLoadingPhase.GATHER).empty();

    /**
     * {@linkplain ModLoadingPhase#GATHER Gathering state} after {@linkplain #VALIDATE validation}, for the construction
     * of mod containers and their backing mod instances.
     *
     * @see FMLConstructModEvent
     * @see ModLoadingStage#CONSTRUCT
     */
    public static final ModLoadingState CONSTRUCT = ModLoadingState.of("CONSTRUCT", ModLoadingPhase.GATHER)
        .after(VALIDATE)
        .message(() -> "Constructing %d mods".formatted(ModList.size()))
        .withTransition(new ParallelTransition(ModLoadingStage.CONSTRUCT, FMLConstructModEvent::new));

    /**
     * First {@linkplain ModLoadingPhase#LOAD loading state}, for loading of the common and (if applicable)
     * {@linkplain Dist#CLIENT client-side} mod configurations.
     */
    public static final ModLoadingState CONFIG_LOAD = ModLoadingState.of("CONFIG_LOAD", ModLoadingPhase.LOAD)
        .withInline(() -> {
            if (FMLEnvironment.dist.isClient()) ConfigTracker.loadConfigs(ModConfig.Type.CLIENT, FMLPaths.CONFIGDIR.get());
            ConfigTracker.loadConfigs(ModConfig.Type.COMMON, FMLPaths.CONFIGDIR.get());
        });

    /**
     * {@linkplain ModLoadingPhase#LOAD Loading state} after {@linkplain #CONFIG_LOAD configuration loading}, for
     * common (non-side-specific) setup and initialization.
     *
     * @see FMLCommonSetupEvent
     * @see ModLoadingStage#COMMON_SETUP
     */
    public static final ModLoadingState COMMON_SETUP = ModLoadingState.of("COMMON_SETUP", ModLoadingPhase.LOAD)
        .after(CONFIG_LOAD)
        .withTransition(new ParallelTransition(ModLoadingStage.COMMON_SETUP, FMLCommonSetupEvent::new));

    /**
     * {@linkplain ModLoadingPhase#LOAD Loading state} after {@linkplain #COMMON_SETUP common setup}, for side-specific
     * setup and initialization.
     *
     * @see FMLClientSetupEvent
     * @see FMLDedicatedServerSetupEvent
     * @see ModLoadingStage#SIDED_SETUP
     */
    public static final ModLoadingState SIDED_SETUP = ModLoadingState.of("SIDED_SETUP", ModLoadingPhase.LOAD)
        .after(COMMON_SETUP)
        .withTransition(new ParallelTransition(ModLoadingStage.SIDED_SETUP, createSidedSetupEvent()));

    private static BiFunction<ModContainer, ModLoadingStage, ParallelDispatchEvent> createSidedSetupEvent() {
        if (FMLEnvironment.dist.isClient())
            return FMLClientSetupEvent::new;
        else
            return FMLDedicatedServerSetupEvent::new;
    }

    /**
     * First {@linkplain ModLoadingPhase#COMPLETE completion state}, for enqueuing {@link net.minecraftsingularity.fml.InterModComms}
     * messages.
     *
     * @see InterModEnqueueEvent
     * @see ModLoadingStage#ENQUEUE_IMC
     */
    public static final ModLoadingState ENQUEUE_IMC = ModLoadingState.of("ENQUEUE_IMC", ModLoadingPhase.COMPLETE)
        .withTransition(new ParallelTransition(ModLoadingStage.ENQUEUE_IMC, InterModEnqueueEvent::new));

    /**
     * {@linkplain ModLoadingPhase#COMPLETE Completion state} after {@linkplain #ENQUEUE_IMC}, for processing of messages
     * received through {@link net.minecraftsingularity.fml.InterModComms}.
     *
     * @see InterModProcessEvent
     * @see ModLoadingStage#PROCESS_IMC
     */
    public static  final ModLoadingState PROCESS_IMC = ModLoadingState.of("PROCESS_IMC", ModLoadingPhase.COMPLETE)
        .after(ENQUEUE_IMC)
        .withTransition(new ParallelTransition(ModLoadingStage.PROCESS_IMC, InterModProcessEvent::new));

    /**
     * {@linkplain ModLoadingPhase#COMPLETE Completion state} after {@linkplain #PROCESS_IMC}, marking the completion
     * of the basic mod loading process; however, additional completion states may be present after this.
     *
     * @see FMLLoadCompleteEvent
     * @see ModLoadingStage#COMPLETE
     */
    public static  final ModLoadingState COMPLETE = ModLoadingState.of("COMPLETE", ModLoadingPhase.COMPLETE)
        .after(PROCESS_IMC)
        .message(() -> "completing load of %d mods".formatted(ModList.size()))
        .withTransition(new ParallelTransition(ModLoadingStage.COMPLETE, FMLLoadCompleteEvent::new));

    /**
     * The marker state for the completion of the full mod loading process.
     *
     * @see ModLoadingStage#DONE
     */
    public static final ModLoadingState DONE = ModLoadingState.of("DONE", ModLoadingPhase.DONE).empty();

    @Override
    public List<IModLoadingState> getAllStates() {
        return List.of(ERROR, VALIDATE, CONSTRUCT, CONFIG_LOAD, COMMON_SETUP, SIDED_SETUP, ENQUEUE_IMC, PROCESS_IMC, COMPLETE, DONE);
    }
}
