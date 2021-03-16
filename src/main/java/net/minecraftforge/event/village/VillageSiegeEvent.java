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

package net.minecraftforge.event.village;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.village.VillageSiege;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * VillageSiegeEvent is fired just before a zombie siege finds a successful location in
 * {@link VillageSiege#trySetupSiege}, to give mods the chance to stop the siege.<br>
 * <br>
 * This event is {@link Cancelable}; canceling stops the siege.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
@Cancelable
public class VillageSiegeEvent extends Event
{
    private final VillageSiege siege;
    private final World world;
    private final PlayerEntity player;
    private final Vector3d attemptedSpawnPos;

    public VillageSiegeEvent(VillageSiege siege, World world, PlayerEntity player, Vector3d attemptedSpawnPos)
    {
       this.siege = siege;
       this.world = world;
       this.player = player;
       this.attemptedSpawnPos = attemptedSpawnPos;
    }

    public VillageSiege getSiege()
    {
        return siege;
    }

    public World getWorld()
    {
        return world;
    }

    public PlayerEntity getPlayer()
    {
        return player;
    }

    public Vector3d getAttemptedSpawnPos()
    {
        return attemptedSpawnPos;
    }
}
