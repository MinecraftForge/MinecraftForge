/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.common.world;

import java.util.Optional;
import java.util.OptionalInt;

import net.minecraft.world.level.biome.BiomeSpecialEffects;

/**
 * Extension of the vanilla builder but also provides read access and a copy-from-existing-data helper.
 * Also, the base builder crashes if certain values aren't specified on build, so this enforces the setting of those.
 */
public class BiomeSpecialEffectsBuilder extends BiomeSpecialEffects.Builder {
    public static BiomeSpecialEffectsBuilder copyOf(BiomeSpecialEffects baseEffects) {
        var ret = create()
            .waterColor(baseEffects.waterColor())
            .grassColorModifier(baseEffects.grassColorModifier())
            ;

        if (baseEffects.foliageColorOverride().isPresent())
            ret = ret.foliageColorOverride(baseEffects.foliageColorOverride().get());
        if (baseEffects.dryFoliageColorOverride().isPresent())
            ret = ret.dryFoliageColorOverride(baseEffects.dryFoliageColorOverride().get());
        if (baseEffects.grassColorOverride().isPresent())
            ret = ret.grassColorOverride(baseEffects.grassColorOverride().get());
        return ret;
    }

    public static BiomeSpecialEffectsBuilder create() {
        return new BiomeSpecialEffectsBuilder();
    }

    protected BiomeSpecialEffectsBuilder() {
    }

    public BiomeSpecialEffectsBuilder waterColor(int value) {
        super.waterColor(value);
        return this;
    }

    public OptionalInt waterColor() {
        return this.waterColor;
    }

    public BiomeSpecialEffectsBuilder foliageColorOverride(int value) {
        super.foliageColorOverride(value);
        return this;
    }

    public Optional<Integer> foliageColorOverride() {
        return this.foliageColorOverride;
    }

    public BiomeSpecialEffectsBuilder dryFoliageColorOverride(int value) {
        super.dryFoliageColorOverride(value);
        return this;
    }

    public Optional<Integer> dryFoliageColorOverride() {
        return this.dryFoliageColorOverride;
    }

    public BiomeSpecialEffectsBuilder grassColorOverride(int value) {
        super.grassColorOverride(value);
        return this;
    }

    public Optional<Integer> grassColorOverride() {
        return this.grassColorOverride;
    }

    public BiomeSpecialEffectsBuilder grassColorModifier(BiomeSpecialEffects.GrassColorModifier value) {
        super.grassColorModifier(value);
        return this;
    }

    public BiomeSpecialEffects.GrassColorModifier getGrassColorModifier() {
        return this.grassColorModifier;
    }
}
