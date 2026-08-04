/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/*


package net.minecraftforge.debug.entity.living;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.monster.EntityStray;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.FMLPreInitializationEvent;

//@Mod(modid = CustomSpawnPlacementTest.MOD_ID, name = "Custom SpawnPlacementType test mod", version = "1.0", acceptableRemoteVersions = "*")
public class CustomSpawnPlacementTest
{
    static final String MOD_ID = "custom_spawn_placement_test";
    static final boolean ENABLED = false;
    static final EntityLiving.SpawnPlacementType CUSTOM = EnumHelper.addSpawnPlacementType("CUSTOM", (world, pos) -> world.getBlockState(pos.down()).getMaterial() == Material.ICE);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLED)
        {
            // needs edit to EntitySpawnPlacementRegistry to work
            EntitySpawnPlacementRegistry.setPlacementType(EntityStray.class, CUSTOM);
        }
    }
}
*/
