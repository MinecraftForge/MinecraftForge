/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

import java.util.List;

import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

/**
 * Child class of LivingDropEvent that is fired specifically when a
 * player dies.  Canceling the event will prevent ALL drops from entering the
 * world.
 */
@Cancelable
public class PlayerDropsEvent extends LivingDropsEvent
{
    private final EntityPlayer entityPlayer;

    /**
     * Creates a new event containing all the items that will drop into the
     * world when a player dies.
     * @param entity The dying player.
     * @param source The source of the damage which is killing the player.
     * @param drops List of all drops entering the world.
     */
    public PlayerDropsEvent(EntityPlayer entity, DamageSource source, List<EntityItem> drops, boolean recentlyHit)
    {
        super(entity, source, drops, ForgeHooks.getLootingLevel(entity, source.getTrueSource(), source), recentlyHit);

        this.entityPlayer = entity;
    }

    public EntityPlayer getEntityPlayer()
    {
        return entityPlayer;
    }
}