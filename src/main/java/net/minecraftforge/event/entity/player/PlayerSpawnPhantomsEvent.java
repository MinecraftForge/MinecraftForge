/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;

/**
 * Fired from {@link net.minecraft.world.level.levelgen.PhantomSpawner} per player when it attempts to spawn phantoms.<br>
 * <br>
 * This event fires before any checks (but <i>after</i> check if player is a spectator and
 * <i>after</i> global checks, such as dimension being dark enough or doInsomnia is true)
 * and generally allow you to change properties of <i>potential</i> phantom spawns through
 * {@link PlayerSpawnPhantomsEvent#setPhantomsToSpawn}.<br>
 * <br>
 * Behavior of {@link net.minecraft.world.level.levelgen.PhantomSpawner} is determined by {@link PlayerSpawnPhantomsEvent#getResult()}.<br>
 * See {@link PlayerSpawnPhantomsEvent#setResult} for documentation.<br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 */
@Event.HasResult
public class PlayerSpawnPhantomsEvent extends PlayerEvent
{
    private int phantomsToSpawn;

    public PlayerSpawnPhantomsEvent(Player player, int phantomsToSpawn)
    {
        super(player);
        this.phantomsToSpawn = phantomsToSpawn;
    }

    /**
     * @return How many phantoms to spawn. This value varies from event to event because it represents random number game was about to spawn.
     */
    public int getPhantomsToSpawn()
    {
        return phantomsToSpawn;
    }

    /**
     * @param phantomsToSpawn How many phantoms should spawn, given checks are passed.
     */
    public void setPhantomsToSpawn(int phantomsToSpawn)
    {
        this.phantomsToSpawn = phantomsToSpawn;
    }

    /**
     * Behavior of {@link net.minecraft.world.level.levelgen.PhantomSpawner} is determined by result:<br>
     * <ul>
     *     <li>If result is {@link Result#DEFAULT}, phantoms will be spawned when passing vanilla checks</li>
     *     <li>If result is {@link Result#ALLOW}, phantoms will be spawned ignoring vanilla checks (except check whenever can phantoms fit in space)</li>
     *     <li>If result is {@link Result#DENY}, no phantoms will be spawned</li>
     * </ul>
     *
     * @param result The new result
     */
    @Override
    public void setResult(@NotNull Result result)
    {
        super.setResult(result);
    }
}
