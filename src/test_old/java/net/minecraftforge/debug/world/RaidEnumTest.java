/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.world;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraftforge.fml.common.Mod;

@Mod("raid_enum_test")
public class RaidEnumTest 
{
    private static final boolean ENABLE = false;
	
    public RaidEnumTest()
    {
        if (ENABLE)
            Raid.RaiderType.create("thebluemengroup", EntityType.ILLUSIONER, new int[]{0, 5, 0, 1, 0, 1, 0, 2});
    }
}
