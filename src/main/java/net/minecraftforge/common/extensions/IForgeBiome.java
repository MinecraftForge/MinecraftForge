/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

public interface IForgeBiome {
    /**
     * {@return Cache of original biome data and biome data modified by biome modifiers}
     * Modified biome data is set by server after datapacks and serverconfigs load.
     * Climate and effects field reads are coremodded to redirect to this.
     **/
    ModifiableBiomeInfo modifiableBiomeInfo();

    /**
     * {@return The biome's climate settings, with modifications if called after modifiers are applied in server init.}
     */
    default Biome.ClimateSettings getModifiedClimateSettings() {
        return modifiableBiomeInfo().get().climateSettings();
    }

    /**
     * {@return The biome's client effects, with modifications if called after modifiers are applied in server init.}
     */
    default BiomeSpecialEffects getModifiedSpecialEffects() {
        return modifiableBiomeInfo().get().effects();
    }
}
