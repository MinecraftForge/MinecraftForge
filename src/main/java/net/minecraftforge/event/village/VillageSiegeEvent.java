/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.village;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.ai.village.VillageSiege;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * VillageSiegeEvent is fired just before a zombie siege finds a successful location in
 * {@code VillageSiege#tryToSetupSiege(ServerLevel)}, to give mods the chance to stop the siege.<br>
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
    private final Level level;
    private final Player player;
    private final Vec3 attemptedSpawnPos;

    public VillageSiegeEvent(VillageSiege siege, Level level, Player player, Vec3 attemptedSpawnPos)
    {
       this.siege = siege;
       this.level = level;
       this.player = player;
       this.attemptedSpawnPos = attemptedSpawnPos;
    }

    public VillageSiege getSiege()
    {
        return siege;
    }

    public Level getLevel()
    {
        return level;
    }

    public Player getPlayer()
    {
        return player;
    }

    public Vec3 getAttemptedSpawnPos()
    {
        return attemptedSpawnPos;
    }
}
