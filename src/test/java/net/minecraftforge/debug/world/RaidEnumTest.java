/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.debug.world;

import net.minecraft.entity.EntityType;
import net.minecraft.world.raid.Raid;
import net.minecraftforge.fml.common.Mod;

@Mod("raid_enum_test")
public class RaidEnumTest
{
    private static final boolean ENABLE = false;

    public RaidEnumTest()
    {
        if (ENABLE)
            Raid.WaveMember.create("thebluemengroup", EntityType.ILLUSIONER, new int[]{0, 5, 0, 1, 0, 1, 0, 2});
    }
}
