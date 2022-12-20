/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.conditions;

import com.mojang.serialization.Codec;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.ModList;

public record ModLoadedCondition(String modId) implements LoadingCondition
{
    @Override
    public boolean test(IConditionContext context)
    {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public String toString()
    {
        return "mod_loaded(\"" + modId + "\")";
    }

    @Override
    public Codec<? extends LoadingCondition> codec()
    {
        return ForgeMod.MOD_LOADED_CONDITION_TYPE.get();
    }
}
