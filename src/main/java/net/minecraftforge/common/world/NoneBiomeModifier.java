/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.common.world;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraftsingularity.common.world.ModifiableBiomeInfo.BiomeInfo.Builder;

/**
 * Noop biome modifier. Can be used in a biome modifier json with "type": "singularity:none".
 */
public class NoneBiomeModifier implements BiomeModifier {
    public static final NoneBiomeModifier INSTANCE = new NoneBiomeModifier();
    public static final MapCodec<NoneBiomeModifier> CODEC = MapCodec.unit(NoneBiomeModifier.INSTANCE);

    @Override
    public void modify(Holder<Biome> biome, Phase phase, Builder builder) {
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return CODEC;
    }
}
