/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
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
