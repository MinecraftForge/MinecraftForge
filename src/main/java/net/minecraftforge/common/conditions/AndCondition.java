/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.conditions;

import com.google.common.base.Joiner;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraftforge.common.ForgeMod;

import java.util.List;

/**
 * The AndCondition forms the logical and of one of more conditions.
 */
public record AndCondition(List<ICondition> children) implements ICondition
{

    @Override
    public boolean test(IContext context)
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
