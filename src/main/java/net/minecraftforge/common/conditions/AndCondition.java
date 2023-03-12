/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.conditions;

import java.util.List;

import org.jetbrains.annotations.ApiStatus;

import com.google.common.base.Joiner;
import com.mojang.serialization.Codec;

import net.minecraftforge.common.ForgeMod;

/**
 * The AndCondition forms the logical and of one of more conditions.
 * 
 * @apiNote Internal. Use {@link ConditionBuilder}.
 */
@ApiStatus.Internal
public record AndCondition(List<ICondition> children) implements ICondition
{

    @Override
    public boolean test(IConditionContext context)
    {
        for (ICondition child : children)
        {
            if (!child.test(context))
                return false;
        }
        return true;
    }

    @Override
    public Codec<? extends ICondition> codec()
    {
        return ForgeMod.AND_CONDITION_TYPE.get();
    }

    @Override
    public String toString()
    {
        return Joiner.on(" && ").join(children);
    }
}
