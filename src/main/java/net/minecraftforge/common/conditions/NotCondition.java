/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.conditions;

import com.mojang.serialization.Codec;
import net.minecraftforge.common.ForgeMod;

public record NotCondition(Condition value) implements Condition
{
    @Override
    public boolean test(IContext context)
    {
        return !value.test(context);
    }

    @Override
    public String toString()
    {
        return "!(" + value + ")";
    }

    @Override
    public Codec<? extends Condition> codec()
    {
        return ForgeMod.NOT_CONDITION_TYPE.get();
    }
}
