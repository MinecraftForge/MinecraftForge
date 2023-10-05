/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting.conditions;

import java.util.List;

import com.google.common.base.Joiner;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record AndCondition(List<ICondition> children) implements ICondition {
    public static final Codec<AndCondition> CODEC = RecordCodecBuilder.create(b -> b.group(
        ICondition.CODEC.listOf().fieldOf("values").forGetter(AndCondition::children)
    ).apply(b, AndCondition::new));

    @Override
    public boolean test(IContext context) {
        for (ICondition child : children) {
            if (!child.test(context))
                return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return '(' + Joiner.on(" && ").join(children) + ')';
    }

    @Override
    public Codec<? extends ICondition> codec() {
        return CODEC;
    }
}
