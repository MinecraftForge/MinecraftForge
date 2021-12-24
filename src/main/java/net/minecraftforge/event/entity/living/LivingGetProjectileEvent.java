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

package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * This event is fired when a living entity attempts to get a projectile with the
 * {@link LivingEntity#getProjectile(ItemStack)} method. The item stack given is usually the item stack of a
 * {@link net.minecraft.world.item.ProjectileWeaponItem} and the item stack returned is usually the item stack of a
 * {@link net.minecraft.world.entity.projectile.Projectile}.
 * <p>
 * This event is {@link net.minecraftforge.eventbus.api.Cancelable}. If the event is cancelled, the original itemstack
 * provided to the event will be returned.
 * <p>
 * This event does not have a result. {@link net.minecraftforge.eventbus.api.Event.HasResult}
 * <p>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
@Cancelable
public class LivingGetProjectileEvent extends LivingEvent
{
    private final ItemStack projectileWeaponItemStack;
    private ItemStack projectileItemStack;

    public LivingGetProjectileEvent(LivingEntity livingEntity, ItemStack projectileWeaponItemStack, ItemStack ammo)
    {
        super(livingEntity);
        this.projectileWeaponItemStack = projectileWeaponItemStack;
        this.projectileItemStack = ammo;
    }

    /**
     * Gets the itemstack of the projectile weapon item that is looking for a projectile.
     */
    public ItemStack getProjectileWeaponItemStack()
    {
        return this.projectileWeaponItemStack;
    }

    /**
     * Gets the itemstack of the projectile found. Initially this is set to the projectile found by vanilla behaviour.
     */
    public ItemStack getProjectileItemStack()
    {
        return this.projectileItemStack;
    }

    /**
     * Sets the projectile itemstack to be used.
     * <p>
     * If the entity is a player: whenever the projectile is fired/consumed the stack will be shrunk by
     * one. To disable this behaviour you can copy the stack before giving it to the event. For bows, you can use
     * {@link net.minecraftforge.event.entity.player.ArrowLooseEvent} to remove the arrow yourself.
     */
    public void setProjectileItemStack(ItemStack projectileItemStack)
    {
        this.projectileItemStack = projectileItemStack;
    }
}
