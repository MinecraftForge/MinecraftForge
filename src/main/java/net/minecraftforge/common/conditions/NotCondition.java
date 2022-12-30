/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.conditions;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;
import net.minecraftforge.common.ForgeMod;

/**
 * The not condition inverts another condition.
 * 
 * @apiNote Internal. Use {@link ConditionBuilder}.
 */
@ApiStatus.Internal
public record NotCondition(ICondition value) implements ICondition
{
    @Override
    public boolean test(IConditionContext context)
    {
        return !value.test(context);
    }

    @Override
    public String toString()
    {
        return "!(" + value + ")";
    }

    @Override
    public Codec<? extends ICondition> codec()
    {
        return ForgeMod.NOT_CONDITION_TYPE.get();
    }
}
