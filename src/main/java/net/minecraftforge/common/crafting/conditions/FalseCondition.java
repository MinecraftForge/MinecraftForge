/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting.conditions;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;

public final class FalseCondition implements ICondition {
    public static final FalseCondition INSTANCE = new FalseCondition();
    public static final MapCodec<FalseCondition> CODEC = MapCodec.unit(INSTANCE).stable();

    private FalseCondition() { }

    @Override
    public boolean test(IContext condition, DynamicOps<?> ops) {
        return false;
    }

    @Override
    public String toString() {
        return "false";
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
