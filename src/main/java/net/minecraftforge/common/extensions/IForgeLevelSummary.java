/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import net.minecraft.world.level.storage.LevelSummary;

public interface IForgeLevelSummary {
    private LevelSummary self() {
        return (LevelSummary) this;
    }

    /**
     * Checks if the Forge lifecycle of this level is experimental. This is used to render the experimental warning
     * tooltip on the level select screen.
     *
     * @return {@code true} if the level is experimental
     */
    default boolean isLifecycleExperimental() {
        // NOTE: Because SymlinkLevelSummary can have null settings, we need to check it
        var settings = this.self().getSettings();

        return settings != null && settings.getLifecycle().equals(com.mojang.serialization.Lifecycle.experimental());
    }
}
