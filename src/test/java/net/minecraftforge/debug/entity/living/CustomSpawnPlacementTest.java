/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.debug.entity.living;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.monster.EntityStray;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = CustomSpawnPlacementTest.MOD_ID, name = "Custom SpawnPlacementType test mod", version = "1.0", acceptableRemoteVersions = "*")
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
