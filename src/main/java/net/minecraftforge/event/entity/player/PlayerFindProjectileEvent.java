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

package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * This event is fired when a player attempts to use an item that is an {@link net.minecraft.item.ShootableItem}
 * that requires some ammo. (For example a bow which will search for arrows in the players inventory)
 * <br>
 * This event is {@link net.minecraftforge.eventbus.api.Cancelable}. If the event is canceled, in survival mode
 * no projectile will be found and in creative mode a plain arrow will be returned. This equals vanilla behaviour
 * when no arrow is in the players inventory.
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
@Cancelable
public class PlayerFindProjectileEvent extends PlayerEvent
{
    private final ItemStack shootable;
    private ItemStack projectile;

    public PlayerFindProjectileEvent(PlayerEntity player, ItemStack shootable, ItemStack ammo)
    {
        super(player);
        this.shootable = shootable;
        this.projectile = ammo;
    }

    /**
     * Gets the shootable item that is looking for a projectile.
     */
    public ItemStack getShootable()
    {
        return this.shootable;
    }

    /**
     * Gets the projectile found. Initially this is set to the projectile found by vanilla behaviour.
     */
    public ItemStack getProjectile()
    {
        return this.projectile;
    }

    /**
     * Sets the projectile to be used. Whenever the projectile is fired/consumed the stack will be
     * shrunk by one. To disable this behaviour you can copy the stack before giving it to the event.
     * For bows you can use {@link ArrowLooseEvent} to remove the arrow yourself.
     */
    public void setProjectile(ItemStack projectile)
    {
        this.projectile = projectile;
    }
}
