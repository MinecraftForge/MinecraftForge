/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.extensions.IForgeItem;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * This event is fired on the forge bus before an Monster detects that a player is looking at them.
 * It will not be fired if the detection is already prevented by {@link IForgeItem#isEnderMask}
 * <p>
 * This event is {@link Cancelable}.
 * If this event is canceled, the Monster will not target the player.
 * <p>
 * This event does not have a {@link Result}.
 */
@Cancelable
public final class MonsterDisguiseEvent extends LivingEvent {
    private final Player player;

    public MonsterDisguiseEvent(Monster monster, Player player) {
        super(monster);
        this.player = player;
    }

    /**
     * The player that is being checked.
     */
    public Player getPlayer() {
        return player;
    }

    @Override
    public Monster getEntity() {
        return (Monster)super.getEntity();
    }
}
