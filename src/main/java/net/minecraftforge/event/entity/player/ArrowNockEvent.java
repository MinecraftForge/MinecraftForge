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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShootableItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.IAmmoHolder;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * ArrowNockEvent is fired when a player begins using either a {@link BowItem} or {@link CrossbowItem}.
 * This event is fired whenever a player begins using a BowItem or a CrossbowItem using {@link BowItem#onItemRightClick(World, PlayerEntity, Hand)} or {@link CrossbowItem#onItemRightClick(World, PlayerEntity, Hand)}.<br>
 * <br>
 * This event fires after the findAmmo
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class ArrowNockEvent extends PlayerEvent
{
    @Nonnull
    private final ItemStack shootable;
    @Nonnull
    private final ItemStack ammo;
    private final boolean hasAmmo;
    private final Hand hand;
    private ActionResult<ItemStack> action;

    public ArrowNockEvent(PlayerEntity player, @Nonnull ItemStack shootable, @Nonnull ItemStack ammo, Hand hand)
    {
        super(player);
        this.shootable = shootable;
        this.ammo = ammo;
        this.hasAmmo = !ammo.isEmpty();
        this.hand = hand;
        if (hasAmmo) this.action = ActionResult.resultSuccess(ammo);
        else this.action = ActionResult.resultFail(ammo);
    }

    @Nonnull
    public ItemStack getShootable() { return this.shootable; }
    @Nonnull
    public ItemStack getAmmo() { return this.ammo; }
    public boolean hasAmmo() { return this.hasAmmo; }
    public Hand getHand() { return this.hand; }
    public ActionResult<ItemStack> getAction()
    {
        return this.action;
    }
    public void setAction(ActionResult<ItemStack> action)
    {
        this.action = action;
    }
    public World getWorld() { return getPlayer().world; }
}
