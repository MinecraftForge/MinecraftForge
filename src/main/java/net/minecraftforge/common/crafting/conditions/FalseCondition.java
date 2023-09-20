/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting.conditions;

import com.mojang.serialization.Codec;

public final class FalseCondition implements ICondition {
    public static final FalseCondition INSTANCE = new FalseCondition();
    public static final Codec<FalseCondition> CODEC = Codec.unit(INSTANCE).stable();

    private FalseCondition() { }

    @Override
    public boolean test(IContext condition) {
        return false;
    }

    @Override
    public String toString() {
        return "false";
    }

    @Override
    public Codec<? extends ICondition> codec() {
        return CODEC;
    }
}
