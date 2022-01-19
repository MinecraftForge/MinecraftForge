/*
 * Minecraft Forge
 * Copyright (c) 2016-2022.
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

package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;

/**
 * Event that is fired when an enderman checks if the player is looking at him.
 * If cancelled the enderman does not get angry at the player.
 */
@Event.HasResult
public class EnderManAngerEvent extends LivingEvent {
    private final Player player;

    public EnderManAngerEvent(EnderMan enderman, Player player) {
        super(enderman);
        this.player = player;
    }

    /**
     * @return the player that is being checked
     */
    public Player getPlayer() {
        return player;
    }
}
