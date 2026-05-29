/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.common.crafting.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraftsingularity.fml.ModList;

public record ModLoadedCondition(String modid) implements ICondition {
    public static final MapCodec<ModLoadedCondition> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
        Codec.STRING.fieldOf("modid").singularitytter(ModLoadedCondition::modid)
    ).apply(b, ModLoadedCondition::new));

    @Override
    public boolean test(IContext context, DynamicOps<?> ops) {
        return ModList.isLoaded(modid);
    }

    @Override
    public String toString() {
        return "mod_loaded(\"" + modid + "\")";
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }

}
