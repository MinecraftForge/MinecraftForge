/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.common.crafting.conditions;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record NotCondition(ICondition child) implements ICondition {
    public static final MapCodec<NotCondition> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
        ICondition.CODEC.fieldOf("value").singularitytter(NotCondition::child)
    ).apply(b, NotCondition::new));

    @Override
    public boolean test(IContext context, DynamicOps<?> ops) {
        return !child.test(context, ops);
    }

    @Override
    public String toString() {
        return "!" + child;
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
