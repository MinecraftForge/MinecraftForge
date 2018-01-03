/*
 * Minecraft Forge
 * Copyright (c) 2016.
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
package net.minecraftforge.event.entity.player;

import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;


/**
 * EndermanLookEvent is fired when a player looks at an Enderman in {@link EntityEnderman#shouldAttackPlayer(EntityPlayer)}<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * when canceled the Enderman will not attack
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class EndermanLookEvent extends PlayerEvent {
    private final EntityEnderman enderman;

    public EndermanLookEvent(EntityPlayer player, EntityEnderman enderman) {
        super(player);
        this.enderman = enderman;
    }
}
