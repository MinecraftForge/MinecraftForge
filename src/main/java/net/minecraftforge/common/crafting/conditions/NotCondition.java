/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record NotCondition(ICondition child) implements ICondition {
    public static final Codec<NotCondition> CODEC = RecordCodecBuilder.create(b -> b.group(
        ICondition.CODEC.fieldOf("value").forGetter(NotCondition::child)
    ).apply(b, NotCondition::new));

    @Override
    public boolean test(IContext context) {
        return !child.test(context);
    }

    @Override
    public String toString() {
        return "!" + child;
    }

    @Override
    public Codec<? extends ICondition> codec() {
        return CODEC;
    }
}
