/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting.conditions;

import net.minecraft.util.ResourceLocation;

public interface ICondition
{
    ResourceLocation getID();

    boolean test();
}
