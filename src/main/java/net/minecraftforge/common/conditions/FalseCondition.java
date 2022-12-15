/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.conditions;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeMod;

/**
 * The FalseCondition always returns false.<p>
 * Useful when content is shipped disabled or when a datapack wants to remove something.
 */
public final class FalseCondition implements ICondition
{
    public static final FalseCondition INSTANCE = new FalseCondition();
    private FalseCondition() {}

    @Override
    public boolean test(IContext condition)
    {
        return false;
    }

    @Override
    public String toString()
    {
        return "false";
    }

    @Override
    public Codec<? extends ICondition> codec()
    {
        return ForgeMod.FALSE_CONDITION_TYPE.get();
    }
}
