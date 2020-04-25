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
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;

/**
 * ArrowNockEvent is fired when a player begins using a bow.<br>
 * This event is fired whenever a player begins using a bow in
 * {@link ItemBow#onItemRightClick(World, EntityPlayer, EnumHand)}.<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class ArrowNockEvent extends PlayerEvent
{
    private final ItemStack bow;
    private final Hand hand;
    private final World world;
    private final boolean hasAmmo;
    private ActionResult<ItemStack> action;

    public ArrowNockEvent(PlayerEntity player, @Nonnull ItemStack item, Hand hand, World world, boolean hasAmmo)
    {
        super(player);
        this.bow = item;
        this.hand = hand;
        this.world = world;
        this.hasAmmo = hasAmmo;
    }

    @Nonnull
    public ItemStack getBow() { return this.bow; }
    public World getWorld() { return this.world; }
    public Hand getHand() { return this.hand; }
    public boolean hasAmmo() { return this.hasAmmo; }
    public ActionResult<ItemStack> getAction()
    {
        return this.action;
    }

    public void setAction(ActionResult<ItemStack> action)
    {
        this.action = action;
    }
}
