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
package net.minecraftforge.event.entity.living;

import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;


/**
 * EndermanLookAggroEvent is fired when an Enderman checks if a player is looking at it in {@link EntityEnderman#shouldAttackPlayer(EntityPlayer)}<br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * this event does have a result {@link HasResult}
 * {@link Event.Result#DEFAULT} keeps vanilla behavior
 * {@link Event.Result#ALLOW} makes the enderman attack
 * {@link Event.Result#DENY} stops the enderman from attacking
 *
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Event.HasResult
public class EndermanLookAggroEvent extends LivingSetAttackTargetEvent {

    public EndermanLookAggroEvent(EntityPlayer player, EntityEnderman enderman) {
        super(enderman, player);
    }
}
