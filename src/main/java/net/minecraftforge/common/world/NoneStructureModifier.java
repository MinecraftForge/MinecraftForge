/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.common.world;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftsingularity.common.world.ModifiableStructureInfo.StructureInfo.Builder;

/**
 * Noop structure modifier. Can be used in a structure modifier json with "type": "singularity:none".
 * intended for datapack makers who want to disable a structure modifier
 */
public class NoneStructureModifier implements StructureModifier {
    public static final NoneStructureModifier INSTANCE = new NoneStructureModifier();
    public static final MapCodec<NoneStructureModifier> CODEC = MapCodec.unit(NoneStructureModifier.INSTANCE);

    @Override
    public void modify(Holder<Structure> structure, Phase phase, Builder builder) {}

    @Override
    public MapCodec<? extends StructureModifier> codec() {
        return CODEC;
    }
}
