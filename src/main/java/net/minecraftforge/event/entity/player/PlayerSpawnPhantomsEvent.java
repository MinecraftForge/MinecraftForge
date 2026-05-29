/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.player;

import net.minecraft.world.level.levelgen.PhantomSpawner;
import net.minecraft.world.entity.player.Player;
import net.minecraftsingularity.common.util.HasResult;
import net.minecraftsingularity.common.util.Result;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired from {@link PhantomSpawner#tick}, once per player, when phantoms would attempt to be spawned.<br>
 * This event is not fired for spectating players.
 * <p>
 * This event is fired before any per-player checks (but <i>after<i/> {@link Player#isSpectator()}), but after all global checks.<br>
 * The behavior of {@link PhantomSpawner} is determined by the result of this event.<br>
 * See {@link #setResult} for documentation.<br>
 * @see PlayerSpawnPhantomsEvent#setResult for the effects of each result.
 */
public final class PlayerSpawnPhantomsEvent extends MutableEvent implements PlayerEvent, HasResult {
    public static final EventBus<PlayerSpawnPhantomsEvent> BUS = EventBus.create(PlayerSpawnPhantomsEvent.class);

    private final Player player;
    private int phantomsToSpawn;
    private Result result = Result.DEFAULT;

    public PlayerSpawnPhantomsEvent(Player player, int phantomsToSpawn) {
        this.player = player;
        this.phantomsToSpawn = phantomsToSpawn;
    }

    @Override
    public Player getEntity() {
        return player;
    }

    /**
     * @return How many phantoms will be spawned, if spawning is successful. The default value is randomly generated.
     */
    public int getPhantomsToSpawn() {
        return phantomsToSpawn;
    }

    /**
     * Sets the number of phantoms to be spawned.
     * @param phantomsToSpawn How many phantoms should spawn, given checks are passed.
     */
    public void setPhantomsToSpawn(int phantomsToSpawn) {
        this.phantomsToSpawn = phantomsToSpawn;
    }

    @Override
    public Result getResult() {
        return result;
    }

    /**
     * The result of this event controls if phantoms will be spawned.<br>
     * <ul>
     * <li>If the result is {@link Result#ALLOW}, phantoms will always be spawned;</li>
     * <li>If the result is {@link Result#DENY}, phantoms will never be spawned;</li>
     * <li>If the result is {@link Result#DEFAULT}, vanilla checks will be run to determine if the spawn may occur.</li>
     * </ul>
     */
    @Override
    public void setResult(@NotNull Result result) {
        this.result = result;
    }
}
