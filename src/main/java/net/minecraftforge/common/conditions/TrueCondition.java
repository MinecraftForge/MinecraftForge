/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.conditions;

import com.mojang.serialization.Codec;
import net.minecraftforge.common.ForgeMod;

public final class TrueCondition implements ICondition
{
    public static final TrueCondition INSTANCE = new TrueCondition();
    private TrueCondition() {}

    @Override
    public boolean test(IContext context)
    {
        return true;
    }

    @Override
    public String toString()
    {
        return "true";
    }

    @Override
    public Codec<? extends ICondition> codec()
    {
        return ForgeMod.TRUE_CONDITION_TYPE.get();
    }
}
