/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.conditions;

import com.google.common.base.Joiner;
import com.mojang.serialization.Codec;
import net.minecraftforge.common.ForgeMod;

import java.util.List;

/**
 * The AndCondition forms the logical and of one of more conditions.
 */
public record AndCondition(List<LoadingCondition> children) implements LoadingCondition
{

    @Override
    public boolean test(IConditionContext context)
    {
        for (LoadingCondition child : children)
        {
            if (!child.test(context))
                return false;
        }
        return true;
    }

    @Override
    public Codec<? extends LoadingCondition> codec()
    {
        return ForgeMod.AND_CONDITION_TYPE.get();
    }

    @Override
    public String toString()
    {
        return Joiner.on(" && ").join(children);
    }
}
