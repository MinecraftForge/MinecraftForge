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

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

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
    private final InteractionHand hand;
    private final Level world;
    private final boolean hasAmmo;
    private InteractionResultHolder<ItemStack> action;

    public ArrowNockEvent(Player player, @Nonnull ItemStack item, InteractionHand hand, Level world, boolean hasAmmo)
    {
        super(player);
        this.bow = item;
        this.hand = hand;
        this.world = world;
        this.hasAmmo = hasAmmo;
    }

    @Nonnull
    public ItemStack getBow() { return this.bow; }
    public Level getWorld() { return this.world; }
    public InteractionHand getHand() { return this.hand; }
    public boolean hasAmmo() { return this.hasAmmo; }
    public InteractionResultHolder<ItemStack> getAction()
    {
        return this.action;
    }

    public void setAction(InteractionResultHolder<ItemStack> action)
    {
        this.action = action;
    }
}
