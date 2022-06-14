/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.world;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.world.ModifiableStructureInfo.StructureInfo.Builder;

public class NoneStructureModifier implements StructureModifier
{
    public static final NoneStructureModifier INSTANCE = new NoneStructureModifier();

    @Override
    public void modify(Holder<Structure> structure, Phase phase, Builder builder)
    {
        // NOOP - intended for datapack makers who want to disable a structure modifier
    }

    @Override
    public Codec<? extends StructureModifier> codec()
    {
        return ForgeMod.NONE_STRUCTURE_MODIFIER_TYPE.get();
    }
}
