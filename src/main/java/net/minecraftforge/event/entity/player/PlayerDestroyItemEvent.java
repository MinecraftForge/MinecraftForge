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

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.entity.Entity;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * PlayerDestroyItemEvent is fired when a player destroys an item.<br>
 * This event is fired whenever a player destroys an item in
 * {@link PlayerController#onPlayerDestroyBlock(BlockPos)},
 * {@link PlayerController#processRightClick(PlayerEntity, World, Hand)},
 * {@link PlayerController#processRightClickBlock(ClientPlayerEntity, ClientWorld, BlockPos, Direction, Vec3d, Hand)},
 * {@link PlayerEntity#attackTargetEntityWithCurrentItem(Entity)},
 * {@link PlayerEntity#damageShield(float)},
 * {@link PlayerEntity#interactOn(Entity, Hand)},
 * {@link ForgeHooks#getContainerItem(ItemStack)},
 * {@link PlayerInteractionManager#processRightClick(PlayerEntity, World, ItemStack, Hand)},
 * {@link PlayerInteractionManager#processRightClickBlock(PlayerEntity, World, ItemStack, Hand, BlockPos, Direction, float, float, float)}
 * and {@link PlayerInteractionManager#tryHarvestBlock(BlockPos)}.<br>
 * <br>
 * {@link #original} contains the original ItemStack before the item was destroyed. <br>
 * (@link #hand) contains the hand that the current item was held in.<br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired from {@link ForgeEventFactory#onPlayerDestroyItem(PlayerEntity, ItemStack, Hand)}.<br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class PlayerDestroyItemEvent extends PlayerEvent
{
    @Nonnull
    private final ItemStack original;
    @Nullable
    private final Hand hand; // May be null if this player destroys the item by any use besides holding it.
    public PlayerDestroyItemEvent(PlayerEntity player, @Nonnull ItemStack original, @Nullable Hand hand)
    {
        super(player);
        this.original = original;
        this.hand = hand;
    }

    @Nonnull
    public ItemStack getOriginal() { return this.original; }
    @Nullable
    public Hand getHand() { return this.hand; }

}
