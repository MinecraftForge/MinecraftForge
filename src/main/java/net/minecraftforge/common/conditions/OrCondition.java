/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.conditions;

import com.google.common.base.Joiner;
import com.mojang.serialization.Codec;
import net.minecraftforge.common.ForgeMod;

import java.util.List;

import org.jetbrains.annotations.ApiStatus;

/**
 * The OrCondition forms the logical or of one of more conditions.
 * 
 * @apiNote Internal. Use {@link ConditionBuilder}.
 */
@ApiStatus.Internal
public record OrCondition(List<ICondition> values) implements ICondition {

    @Override
    public boolean test(IConditionContext context)
    {
        for (ICondition child : values)
        {
            if (child.test(context))
                return true;
        }

        return false;
    }

    @Override
    public String toString()
    {
        return Joiner.on(" || ").join(values);
    }

    @Override
    public Codec<? extends ICondition> codec()
    {
        return ForgeMod.OR_CONDITION_TYPE.get();
    }
}
