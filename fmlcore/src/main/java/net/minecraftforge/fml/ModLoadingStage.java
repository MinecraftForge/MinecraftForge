/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

/**
 * Mod loading stage of mod containers during the mod loading process. These will have a corresponding {@link ModLoadingState}
 * in the basic mod loading process provided by FML.
 *
 * <p>Each mod loading stage has a global {@link DeferredWorkQueue}, which is populated during the execution of the state
 * associated with this stage and emptied at the end of the state's execution.</p>
 */
public enum ModLoadingStage
{
    /**
     * Special stage for exceptional situations and error handling.
     */
    ERROR,
    /**
     * Validation of the mod list.
     * TODO: figure out where this is used and why this exists instead of CONSTRUCT being the first normal stage
     */
    VALIDATE,
    /**
     * Default stage of mod containers after construction.
     */
    CONSTRUCT,
    /**
     * Common (non-side-specific) setup and initialization.
     */
    COMMON_SETUP,
    /**
     * Side-specific setup and initialization.
     *
     * @see net.minecraftforge.api.distmarker.Dist
     */
    SIDED_SETUP,
    /**
     * Stage for enqueuing {@link net.minecraftforge.fml.InterModComms} messages for later processing.
     */
    ENQUEUE_IMC,
    /**
     * Stage for processing received messages though {@link net.minecraftforge.fml.InterModComms}.
     */
    PROCESS_IMC,
    /**
     * Marks the completion of mod loading for this container.
     */
    COMPLETE,
    /**
     * Marks the completion of the full mod loading process.
     */
    DONE;

    private final DeferredWorkQueue deferredWorkQueue;

    ModLoadingStage() {
        deferredWorkQueue = new DeferredWorkQueue(this);
    }

    /**
     * {@return the next stage after this stage, or {@link #ERROR} if the exception is not {@code null}}
     *
     * @param exception the exception that occurred during this stage, may be {@code null}
     */
    ModLoadingStage nextState(Throwable exception) {
        return exception != null ? ERROR : values()[this.ordinal()+1];
    }

    /**
     * {@return this stage, or {@link #ERROR} if the exception is not {@code null}}
     *
     * @param exception the exception that occurred during this stage, may be {@code null}
     */
    public ModLoadingStage currentState(Throwable exception) {
        return exception != null ? ERROR : this;
    }

    /**
     * {@return the deferred work queue for this stage}
     */
    public DeferredWorkQueue getDeferredWorkQueue() {
        return deferredWorkQueue;
    }
}
