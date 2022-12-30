/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.conditions;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * The ItemExistsCondition can detect if a particular item is currently registered.
 * 
 * @apiNote Internal. Use {@link ConditionBuilder}.
 */
@ApiStatus.Internal
public record ItemExistsCondition(ResourceLocation item) implements ICondition
{
    @Override
    public boolean test(IConditionContext context)
    {
        return ForgeRegistries.ITEMS.containsKey(item);
    }

    @Override
    public String toString()
    {
        return "item_exists(\"" + item + "\")";
    }

    @Override
    public Codec<? extends ICondition> codec()
    {
        return ForgeMod.ITEM_EXISTS_CONDITION_TYPE.get();
    }
}
