/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting.conditions;

import net.minecraft.resources.ResourceLocation;

public interface ICondition
{
    ResourceLocation getID();

    boolean test();
}
