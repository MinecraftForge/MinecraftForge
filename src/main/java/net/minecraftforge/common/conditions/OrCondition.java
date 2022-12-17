/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.conditions;

import com.google.common.base.Joiner;
import com.mojang.serialization.Codec;
import net.minecraftforge.common.ForgeMod;

import java.util.List;

public record OrCondition(List<Condition> values) implements Condition {

    @Override
    public boolean test(IContext context)
    {
        for (Condition child : values)
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
    public Codec<? extends Condition> codec()
    {
        return ForgeMod.OR_CONDITION_TYPE.get();
    }
}
