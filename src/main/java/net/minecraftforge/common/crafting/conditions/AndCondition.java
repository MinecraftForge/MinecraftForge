/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting.conditions;

import java.util.List;

import com.google.common.base.Joiner;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record AndCondition(List<ICondition> children) implements ICondition {
    public static final MapCodec<AndCondition> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
        ICondition.CODEC.listOf().fieldOf("values").forGetter(AndCondition::children)
    ).apply(b, AndCondition::new));

    @Override
    public boolean test(IContext context, DynamicOps<?> ops) {
        for (ICondition child : children) {
            if (!child.test(context, ops))
                return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return '(' + Joiner.on(" && ").join(children) + ')';
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
