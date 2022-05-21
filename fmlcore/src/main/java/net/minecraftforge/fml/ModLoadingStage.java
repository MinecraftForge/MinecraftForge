/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

public enum ModLoadingStage
{
    ERROR,
    VALIDATE,
    CONSTRUCT,
    COMMON_SETUP,
    SIDED_SETUP,
    ENQUEUE_IMC,
    PROCESS_IMC,
    COMPLETE,
    DONE;

    private final DeferredWorkQueue deferredWorkQueue;

    ModLoadingStage() {
        deferredWorkQueue = new DeferredWorkQueue(this);
    }

    ModLoadingStage nextState(Throwable exception) {
        return exception != null ? ERROR : values()[this.ordinal()+1];
    }

    public ModLoadingStage currentState(Throwable exception) {
        return exception != null ? ERROR : this;
    }

    public DeferredWorkQueue getDeferredWorkQueue() {
        return deferredWorkQueue;
    }
}
