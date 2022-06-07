/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.world;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.world.ModifiableBiomeInfo.BiomeInfo.Builder;

public class NoBiomeModifier extends BiomeModifier
{
    public static final NoBiomeModifier INSTANCE = new NoBiomeModifier();

    @Override
    public void modify(Holder<Biome> biome, Phase phase, Builder builder)
    {
        // noop
    }

    @Override
    public BiomeModifierSerializer<?> type()
    {
        return ForgeMod.NO_BIOME_MODIFIER.get();
    }
}
