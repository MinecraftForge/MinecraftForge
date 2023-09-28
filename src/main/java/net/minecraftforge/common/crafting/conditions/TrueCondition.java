/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting.conditions;

import com.mojang.serialization.Codec;

public final class TrueCondition implements ICondition {
    public static final TrueCondition INSTANCE = new TrueCondition();
    public static final Codec<TrueCondition> CODEC = Codec.unit(INSTANCE).stable();

    private TrueCondition() {}

    @Override
    public boolean test(IContext context) {
        return true;
    }

    @Override
    public String toString() {
        return "true";
    }

    @Override
    public Codec<? extends ICondition> codec() {
        return CODEC;
    }
}
