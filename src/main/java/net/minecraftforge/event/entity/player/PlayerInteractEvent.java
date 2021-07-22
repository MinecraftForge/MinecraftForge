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

import com.google.common.base.Preconditions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Cancelable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.minecraftforge.eventbus.api.Event.Result.DEFAULT;
import static net.minecraftforge.eventbus.api.Event.Result.DENY;

import net.minecraftforge.fml.LogicalSide;

/**
 * PlayerInteractEvent is fired when a player interacts in some way.
 * All subclasses are fired on {@link MinecraftForge#EVENT_BUS}.
 * See the individual documentation on each subevent for more details.
 **/
public class PlayerInteractEvent extends PlayerEvent
{
    private final InteractionHand hand;
    private final BlockPos pos;
    @Nullable
    private final Direction face;
    private InteractionResult cancellationResult = InteractionResult.PASS;

    private PlayerInteractEvent(Player player, InteractionHand hand, BlockPos pos, @Nullable Direction face)
    {
        super(Preconditions.checkNotNull(player, "Null player in PlayerInteractEvent!"));
        this.hand = Preconditions.checkNotNull(hand, "Null hand in PlayerInteractEvent!");
        this.pos = Preconditions.checkNotNull(pos, "Null position in PlayerInteractEvent!");
        this.face = face;
    }

    /**
     * This event is fired on both sides whenever a player right clicks an entity.
     *
     * "Interact at" is an interact where the local vector (which part of the entity you clicked) is known.
     * The state of this event affects whether {@link Entity#applyPlayerInteraction} is called.
     *
     * Let result be the return value of {@link Entity#applyPlayerInteraction}, or {@link #cancellationResult} if the event is cancelled.
     * If we are on the client and result is not {@link EnumActionResult#SUCCESS}, the client will then try {@link EntityInteract}.
     */
    @Cancelable
    public static class EntityInteractSpecific extends PlayerInteractEvent
    {
        private final Vec3 localPos;
        private final Entity target;

        public EntityInteractSpecific(Player player, InteractionHand hand, Entity target, Vec3 localPos)
        {
            super(player, hand, target.blockPosition(), null);
            this.localPos = localPos;
            this.target = target;
        }

        /**
         * Returns the local interaction position. This is a 3D vector, where (0, 0, 0) is centered exactly at the
         * center of the entity's bounding box at their feet. This means the X and Z values will be in the range
         * [-width / 2, width / 2] while Y values will be in the range [0, height]
         * @return The local position
         */
        public Vec3 getLocalPos()
        {
            return localPos;
        }

        public Entity getTarget()
        {
            return target;
        }
    }

    /**
     * This event is fired on both sides when the player right clicks an entity.
     * It is responsible for all general entity interactions.
     *
     * This event is fired only if the result of the above {@link EntityInteractSpecific} is not {@link EnumActionResult#SUCCESS}.
     * This event's state affects whether {@link Entity#processInitialInteract} and {@link net.minecraft.item.Item#itemInteractionForEntity} are called.
     *
     * Let result be {@link EnumActionResult#SUCCESS} if {@link Entity#processInitialInteract} or {@link net.minecraft.item.Item#itemInteractionForEntity} return true,
     * or {@link #cancellationResult} if the event is cancelled.
     * If we are on the client and result is not {@link EnumActionResult#SUCCESS}, the client will then try {@link RightClickItem}.
     */
    @Cancelable
    public static class EntityInteract extends PlayerInteractEvent
    {
        private final Entity target;

        public EntityInteract(Player player, InteractionHand hand, Entity target)
        {
            super(player, hand, target.blockPosition(), null);
            this.target = target;
        }

        public Entity getTarget()
        {
            return target;
        }
    }

    /**
     * This event is fired on both sides whenever the player right clicks while targeting a block. <br>
     * This event controls which of {@link Item#onItemUseFirst}, {@link Block#onBlockActivated}, and {@link Item#onItemUse}
     * will be called. <br>
     * Canceling the event will cause none of the above three to be called. <br>
     * <br>
     * Let result be the first non-pass return value of the above three methods, or pass, if they all pass. <br>
     * Or {@link #cancellationResult} if the event is cancelled. <br>
     * If result equals {@link ActionResultType#PASS}, we proceed to {@link RightClickItem}.  <br>
     * <br>
     * There are various results to this event, see the getters below.  <br>
     * Note that handling things differently on the client vs server may cause desynchronizations! 
     */
    @Cancelable
    public static class RightClickBlock extends PlayerInteractEvent
    {
        private Result useBlock = DEFAULT;
        private Result useItem = DEFAULT;
        private BlockHitResult hitVec;

        public RightClickBlock(Player player, InteractionHand hand, BlockPos pos, BlockHitResult hitVec) {
            super(player, hand, pos, hitVec.getDirection());
            this.hitVec = hitVec;
        }

        /**
         * @return If {@link net.minecraft.block.Block#onBlockActivated} should be called
         */
        public Result getUseBlock()
        {
            return useBlock;
        }

        /**
         * @return If {@link net.minecraft.item.Item#onItemUseFirst} and {@link net.minecraft.item.Item#onItemUse} should be called
         */
        public Result getUseItem()
        {
            return useItem;
        }

        /**
         * @return The ray trace result targeting the block.
         */
        public BlockHitResult getHitVec()
        {
            return hitVec;
        }

        /**
         * DENY: {@link Block#onBlockActivated} will never be called. <br>
         * DEFAULT: {@link Block#onBlockActivated} will be called if {@link Item#onItemUseFirst} passes. <br>
         * Note that default activation can be blocked if the user is sneaking and holding an item that does not return true to {@link Item#doesSneakBypassUse}. <br>
         * ALLOW: {@link Block#onBlockActivated} will always be called, unless {@link Item#onItemUseFirst} does not pass. <br>
         */
        public void setUseBlock(Result triggerBlock)
        {
            this.useBlock = triggerBlock;
        }

        /**
         * DENY: Neither {@link Item#onItemUse} or {@link Item#onItemUseFirst} will be called. <br>
         * DEFAULT: {@link Item#onItemUseFirst} will always be called, and {@link Item#onItemUse} will be called if the block passes. <br>
         * ALLOW: {@link Item#onItemUseFirst} will always be called, and {@link Item#onItemUse} will be called if the block passes, regardless of cooldowns or emptiness. <br>
         */
        public void setUseItem(Result triggerItem)
        {
            this.useItem = triggerItem;
        }

        @Override
        public void setCanceled(boolean canceled)
        {
            super.setCanceled(canceled);
            if (canceled)
            {
                useBlock = DENY;
                useItem = DENY;
            }
        }
    }

    /**
     * This event is fired on both sides before the player triggers {@link net.minecraft.item.Item#onItemRightClick}.
     * Note that this is NOT fired if the player is targeting a block {@link RightClickBlock} or entity {@link EntityInteract} {@link EntityInteractSpecific}.
     *
     * Let result be the return value of {@link net.minecraft.item.Item#onItemRightClick}, or {@link #cancellationResult} if the event is cancelled.
     * If we are on the client and result is not {@link EnumActionResult#SUCCESS}, the client will then continue to other hands.
     */
    @Cancelable
    public static class RightClickItem extends PlayerInteractEvent
    {
        public RightClickItem(Player player, InteractionHand hand)
        {
            super(player, hand, player.blockPosition(), null);
        }
    }

    /**
     * This event is fired on the client side when the player right clicks empty space with an empty hand.
     * The server is not aware of when the client right clicks empty space with an empty hand, you will need to tell the server yourself.
     * This event cannot be canceled.
     */
    public static class RightClickEmpty extends PlayerInteractEvent
    {
        public RightClickEmpty(Player player, InteractionHand hand)
        {
            super(player, hand, player.blockPosition(), null);
        }
    }

    /**
     * This event is fired when a player left clicks while targeting a block.
     * This event controls which of {@link net.minecraft.block.Block#onBlockClicked} and/or the item harvesting methods will be called
     * Canceling the event will cause none of the above noted methods to be called.
     * There are various results to this event, see the getters below.
     *
     * Note that if the event is canceled and the player holds down left mouse, the event will continue to fire.
     * This is due to how vanilla calls the left click handler methods.
     *
     * Also note that creative mode directly breaks the block without running any other logic.
     * Therefore, in creative mode, {@link #setUseBlock} and {@link #setUseItem} have no effect.
     */
    @Cancelable
    public static class LeftClickBlock extends PlayerInteractEvent
    {
        private Result useBlock = DEFAULT;
        private Result useItem = DEFAULT;

        public LeftClickBlock(Player player, BlockPos pos, Direction face)
        {
            super(player, InteractionHand.MAIN_HAND, pos, face);
        }

        /**
         * @return If {@link net.minecraft.block.Block#onBlockClicked} should be called. Changing this has no effect in creative mode
         */
        public Result getUseBlock()
        {
            return useBlock;
        }

        /**
         * @return If the block should be attempted to be mined with the current item. Changing this has no effect in creative mode
         */
        public Result getUseItem()
        {
            return useItem;
        }

        public void setUseBlock(Result triggerBlock)
        {
            this.useBlock = triggerBlock;
        }

        public void setUseItem(Result triggerItem)
        {
            this.useItem = triggerItem;
        }

        @Override
        public void setCanceled(boolean canceled)
        {
            super.setCanceled(canceled);
            if (canceled)
            {
                useBlock = DENY;
                useItem = DENY;
            }
        }
    }

    /**
     * This event is fired on the client side when the player left clicks empty space with any ItemStack.
     * The server is not aware of when the client left clicks empty space, you will need to tell the server yourself.
     * This event cannot be canceled.
     */
    public static class LeftClickEmpty extends PlayerInteractEvent
    {
        public LeftClickEmpty(Player player)
        {
            super(player, InteractionHand.MAIN_HAND, player.blockPosition(), null);
        }
    }

    /**
     * @return The hand involved in this interaction. Will never be null.
     */
    @Nonnull
    public InteractionHand getHand()
    {
        return hand;
    }

    /**
     * @return The itemstack involved in this interaction, {@code ItemStack.EMPTY} if the hand was empty.
     */
    @Nonnull
    public ItemStack getItemStack()
    {
        return getPlayer().getItemInHand(hand);
    }

    /**
     * If the interaction was on an entity, will be a BlockPos centered on the entity.
     * If the interaction was on a block, will be the position of that block.
     * Otherwise, will be a BlockPos centered on the player.
     * Will never be null.
     * @return The position involved in this interaction.
     */
    @Nonnull
    public BlockPos getPos()
    {
        return pos;
    }

    /**
     * @return The face involved in this interaction. For all non-block interactions, this will return null.
     */
    @Nullable
    public Direction getFace()
    {
        return face;
    }

    /**
     * @return Convenience method to get the world of this interaction.
     */
    public Level getWorld()
    {
        return getPlayer().getCommandSenderWorld();
    }

    /**
     * @return The effective, i.e. logical, side of this interaction. This will be {@link LogicalSide#CLIENT} on the client thread, and {@link LogicalSide#SERVER} on the server thread.
     */
    public LogicalSide getSide()
    {
        return getWorld().isClientSide ? LogicalSide.CLIENT : LogicalSide.SERVER;
    }

    /**
     * @return The EnumActionResult that will be returned to vanilla if the event is cancelled, instead of calling the relevant
     * method of the event. By default, this is {@link EnumActionResult#PASS}, meaning cancelled events will cause
     * the client to keep trying more interactions until something works.
     */
    public InteractionResult getCancellationResult()
    {
        return cancellationResult;
    }

    /**
     * Set the EnumActionResult that will be returned to vanilla if the event is cancelled, instead of calling the relevant
     * method of the event.
     * Note that this only has an effect on {@link RightClickBlock}, {@link RightClickItem}, {@link EntityInteract}, and {@link EntityInteractSpecific}.
     */
    public void setCancellationResult(InteractionResult result)
    {
        this.cancellationResult = result;
    }

}
