/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraftforge.fml.ModList;

public record ModLoadedCondition(String modid) implements ICondition {
    public static final Codec<ModLoadedCondition> CODEC = RecordCodecBuilder.create(b -> b.group(
        Codec.STRING.fieldOf("modid").forGetter(ModLoadedCondition::modid)
    ).apply(b, ModLoadedCondition::new));

    @Override
    public boolean test(IContext context) {
        return ModList.get().isLoaded(modid);
    }

    @Override
    public String toString() {
        return "mod_loaded(\"" + modid + "\")";
    }

    @Override
    public Codec<? extends ICondition> codec() {
        return CODEC;
    }

}
