/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.core;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.IModLoadingState;
import net.minecraftforge.fml.IModStateProvider;
import net.minecraftforge.fml.ModContainer;
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
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;

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
        .message(ml -> "Constructing %d mods".formatted(ml.size()))
        .withTransition(new ParallelTransition(ModLoadingStage.CONSTRUCT, FMLConstructModEvent::new));

    /**
     * First {@linkplain ModLoadingPhase#LOAD loading state}, for loading of the common and (if applicable)
     * {@linkplain Dist#CLIENT client-side} mod configurations.
     */
    public static final ModLoadingState CONFIG_LOAD = ModLoadingState.of("CONFIG_LOAD", ModLoadingPhase.LOAD)
        .withInline(ml -> {
            if (FMLEnvironment.dist.isClient()) ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.CLIENT, FMLPaths.CONFIGDIR.get());
            ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.COMMON, FMLPaths.CONFIGDIR.get());
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
     * First {@linkplain ModLoadingPhase#COMPLETE completion state}, for enqueuing {@link net.minecraftforge.fml.InterModComms}
     * messages.
     *
     * @see InterModEnqueueEvent
     * @see ModLoadingStage#ENQUEUE_IMC
     */
    public static final ModLoadingState ENQUEUE_IMC = ModLoadingState.of("ENQUEUE_IMC", ModLoadingPhase.COMPLETE)
        .withTransition(new ParallelTransition(ModLoadingStage.ENQUEUE_IMC, InterModEnqueueEvent::new));

    /**
     * {@linkplain ModLoadingPhase#COMPLETE Completion state} after {@linkplain #ENQUEUE_IMC}, for processing of messages
     * received through {@link net.minecraftforge.fml.InterModComms}.
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
        .message(ml -> "completing load of %d mods".formatted(ml.size()))
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
