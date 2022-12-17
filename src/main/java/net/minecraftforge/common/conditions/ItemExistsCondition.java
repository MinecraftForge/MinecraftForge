/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.conditions;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * The ItemExistsCondition can detect if a particular item is currently registered.
 */
public record ItemExistsCondition(ResourceLocation item) implements Condition
{
    @Override
    public boolean test(IContext context)
    {
        return ForgeRegistries.ITEMS.containsKey(item);
    }

    @Override
    public String toString()
    {
        return "item_exists(\"" + item + "\")";
    }

    @Override
    public Codec<? extends Condition> codec()
    {
        return ForgeMod.ITEM_EXISTS_CONDITION_TYPE.get();
    }
}
