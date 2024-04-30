/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting.conditions;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;

public final class TrueCondition implements ICondition {
    public static final TrueCondition INSTANCE = new TrueCondition();
    public static final MapCodec<TrueCondition> CODEC = MapCodec.unit(INSTANCE).stable();

    private TrueCondition() {}

    @Override
    public boolean test(IContext context, DynamicOps<?> ops) {
        return true;
    }

    @Override
    public String toString() {
        return "true";
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
