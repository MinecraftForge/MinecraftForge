/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * This event is fired when a living entity attempts to get a projectile with the
 * {@link LivingEntity#getProjectile(ItemStack)} method. The item stack given is usually the item stack of a
 * {@link net.minecraft.world.item.ProjectileWeaponItem} and the item stack returned is usually the item stack of a
 * {@link net.minecraft.world.entity.projectile.Projectile}.
 * <p>
 * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.
 * <p>
 * This event does not have a result. {@link net.minecraftforge.eventbus.api.Event.HasResult}
 * <p>
 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.
 */
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
     * @return The itemstack of the itrm that is looking for a projectile. With vanilla behavior, this usually returns
     * an itemstack of a {@link net.minecraft.world.item.ProjectileWeaponItem}, but it's possible for that to not be the
     * case if modder uses a different implementation of {@link LivingEntity#getProjectile(ItemStack)}.
     */
    public ItemStack getProjectileWeaponItemStack()
    {
        return this.projectileWeaponItemStack;
    }

    /**
     * @return The itemstack of the projectile found. Initially this is set to the projectile found by vanilla
     * behaviour, but it's possible for thatnot to be the case if a modder uses a different implementation of
     * {@link LivingEntity#getProjectile(ItemStack)}.
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
     * <p>
     * Be aware that since this event fires every time a living entity gets a projectile, whether or not its
     * {@link LivingEntity#level} is client-side, you will want to make a conditional to always set the item stack to
     * what you'd want it to be to avoid client-server desyncs.
     */
    public void setProjectileItemStack(ItemStack projectileItemStack)
    {
        this.projectileItemStack = projectileItemStack;
    }
}
