/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;

import javax.annotation.Nonnull;

/**
 * ArrowLooseEvent is fired when a player stops using a {@link BowItem} or a {@link CrossbowItem}.<br>
 * This event is fired whenever a player stops using a BowItem or CrossbowItem in
 * {@link BowItem#onPlayerStoppedUsing(ItemStack, World, LivingEntity, int)} or {@linkplain CrossbowItem#fireProjectile(World, LivingEntity, Hand, ItemStack, ItemStack, float, boolean, float, float, float)}.<br>
 * <br>
 * {@link #shootable} contains either the {@link BowItem} or the {@link CrossbowItem} ItemStack that was used in this event.<br>
 * {@link #charge} contains the value for how much the player had charged before stopping the shot.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the player does not stop using the bow.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class ProjectileLooseEvent extends PlayerEvent
{
    @Nonnull
    private final ItemStack shootable;
    @Nonnull
    private final ItemStack ammo;
    private final boolean hasAmmo;
    private int charge;

    public ProjectileLooseEvent(PlayerEntity player, @Nonnull ItemStack shootable, @Nonnull ItemStack ammo)
    {
        super(player);
        this.shootable = shootable;
        this.ammo = ammo;
        this.hasAmmo = !ammo.isEmpty();
        this.charge = charge;
    }

    public ProjectileLooseEvent(PlayerEntity player, @Nonnull ItemStack shootable, @Nonnull ItemStack ammo, int charge)
    {
        super(player);
        this.shootable = shootable;
        this.ammo = ammo;
        this.hasAmmo = !ammo.isEmpty();
        this.charge = charge;
    }

    @Nonnull
    public ItemStack getShootable() { return this.shootable; }
    @Nonnull
    public ItemStack getAmmo() { return this.ammo; }
    public boolean hasAmmo() { return this.hasAmmo; }
    public int getCharge() { return this.charge; }
    public void setCharge(int charge) { this.charge = charge; }
    public World getWorld() { return getPlayer().world; }

    /**
     *
     */
    public static class Post extends ProjectileLooseEvent
    {
        private final ProjectileEntity projectileEntity;

        public Post(PlayerEntity player, @Nonnull ItemStack shootable, ItemStack ammo, ProjectileEntity projectileEntity)
        {
            super(player, shootable, ammo);
            this.projectileEntity = projectileEntity;
        }

        public Post(PlayerEntity player, @Nonnull ItemStack shootable, ItemStack ammo, int charge, ProjectileEntity projectileEntity)
        {
            super(player, shootable, ammo, charge);
            this.projectileEntity = projectileEntity;
        }

        public ProjectileEntity getProjectileEntity() { return this.projectileEntity; }
    }
}
