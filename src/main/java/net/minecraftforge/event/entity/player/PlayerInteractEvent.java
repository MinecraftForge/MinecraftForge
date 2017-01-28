/*
 * Minecraft Forge
 * Copyright (c) 2016.
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
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.minecraftforge.fml.common.eventhandler.Event.Result.DEFAULT;
import static net.minecraftforge.fml.common.eventhandler.Event.Result.DENY;

/**
 * PlayerInteractEvent is fired when a player interacts in some way.
 * All subclasses are fired on {@link MinecraftForge#EVENT_BUS}.
 * See the individual documentation on each subevent for more details.
 **/
public class PlayerInteractEvent extends PlayerEvent
{
    private final EnumHand hand;
    private final BlockPos pos;
    @Nullable
    private final EnumFacing face;

    private PlayerInteractEvent(EntityPlayer player, EnumHand hand, BlockPos pos, @Nullable EnumFacing face)
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
     * If {@link Entity#applyPlayerInteraction} returns {@link net.minecraft.util.EnumActionResult#SUCCESS}, then processing ends.
     * Otherwise processing will continue to {@link EntityInteract}
     *
     * Canceling the event clientside will cause processing to continue to {@link EntityInteract},
     * while canceling serverside will simply do no further processing.
     */
    @Cancelable
    public static class EntityInteractSpecific extends PlayerInteractEvent
    {
        private final Vec3d localPos;
        private final Entity target;

        public EntityInteractSpecific(EntityPlayer player, EnumHand hand, Entity target, Vec3d localPos)
        {
            super(player, hand, new BlockPos(target), null);
            this.localPos = localPos;
            this.target = target;
        }

        /**
         * Returns the local interaction position. This is a 3D vector, where (0, 0, 0) is centered exactly at the
         * center of the entity's bounding box at their feet. This means the X and Z values will be in the range
         * [-width / 2, width / 2] while Y values will be in the range [0, height]
         * @return The local position
         */
        public Vec3d getLocalPos()
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
     * This event is fired completely independently of the above {@link EntityInteractSpecific}, except for the case
     * where the above call to {@link Entity#applyPlayerInteraction} returns {@link net.minecraft.util.EnumActionResult#SUCCESS}.
     * In that case, general entity interactions, and hence this event, will not be called. See the above javadoc for more details.
     *
     * This event's state affects whether {@link Entity#processInitialInteract} and {@link net.minecraft.item.Item#itemInteractionForEntity} are called.
     *
     * Canceling the event clientside will cause processing to continue to {@link RightClickItem},
     * while canceling serverside will simply do no further processing.
     */
    @Cancelable
    public static class EntityInteract extends PlayerInteractEvent
    {
        private final Entity target;

        public EntityInteract(EntityPlayer player, EnumHand hand, Entity target)
        {
            super(player, hand, new BlockPos(target), null);
            this.target = target;
        }

        public Entity getTarget()
        {
            return target;
        }
    }

    /**
     * This event is fired on both sides whenever the player right clicks while targeting a block.
     * This event controls which of {@link net.minecraft.block.Block#onBlockActivated} and/or {@link net.minecraft.item.Item#onItemUse}
     * will be called after {@link net.minecraft.item.Item#onItemUseFirst} is called.
     * Canceling the event will cause none of the above three to be called.
     * There are various results to this event, see the getters below.
     * Note that handling things differently on the client vs server may cause desynchronizations!
     */
    @Cancelable
    public static class RightClickBlock extends PlayerInteractEvent
    {
        private Result useBlock = DEFAULT;
        private Result useItem = DEFAULT;
        private final Vec3d hitVec;

        public RightClickBlock(EntityPlayer player, EnumHand hand, BlockPos pos, EnumFacing face, Vec3d hitVec) {
            super(player, hand, pos, face);
            this.hitVec = hitVec;
        }

        /**
         * @return The hit vector of this click
         */
        public Vec3d getHitVec()
        {
            return hitVec;
        }

        /**
         * @return If {@link net.minecraft.block.Block#onBlockActivated} should be called
         */
        public Result getUseBlock()
        {
            return useBlock;
        }

        /**
         * @return If {@link net.minecraft.item.Item#onItemUse} should be called
         */
        public Result getUseItem()
        {
            return useItem;
        }

        /**
         * DENY: Block will never be used.
         * DEFAULT: Default behaviour (sneak will not use block, unless all items return true in {@link net.minecraft.item.Item#doesSneakBypassUse}).
         * ALLOW: Block will always be used, regardless of sneaking and doesSneakBypassUse.
         */
        public void setUseBlock(Result triggerBlock)
        {
            this.useBlock = triggerBlock;
        }

        /**
         * DENY: The item will never be used.
         * DEFAULT: The item will be used if the block fails.
         * ALLOW: The item will always be used.
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
     * Note that this is NOT fired if the player is targeting a block. For that case, see {@link RightClickBlock}.
     * Canceling the event clientside causes processing to continue to the other hands,
     * while canceling serverside will simply do no further processing.
     */
    @Cancelable
    public static class RightClickItem extends PlayerInteractEvent
    {
        public RightClickItem(EntityPlayer player, EnumHand hand)
        {
            super(player, hand, new BlockPos(player), null);
        }
    }

    /**
     * This event is fired on the client side when the player right clicks empty space with an empty hand.
     * The server is not aware of when the client right clicks empty space with an empty hand, you will need to tell the server yourself.
     * This event cannot be canceled.
     */
    public static class RightClickEmpty extends PlayerInteractEvent
    {
        public RightClickEmpty(EntityPlayer player, EnumHand hand)
        {
            super(player, hand, new BlockPos(player), null);
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
        private final Vec3d hitVec;

        public LeftClickBlock(EntityPlayer player, BlockPos pos, EnumFacing face, Vec3d hitVec)
        {
            super(player, EnumHand.MAIN_HAND, pos, face);
            this.hitVec = hitVec;
        }

        /**
         * @return The local hit vector of this click
         */
        public Vec3d getHitVec()
        {
            return hitVec;
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
        public LeftClickEmpty(EntityPlayer player)
        {
            super(player, EnumHand.MAIN_HAND, new BlockPos(player), null);
        }

        // TODO: remove
        /** @deprecated use {@link LeftClickEmpty(EntityPlayer)} */
        @Deprecated
        public LeftClickEmpty(EntityPlayer player, @Nonnull ItemStack stack)
        {
            this(player);
        }
    }

    /**
     * This event is fired on the client side when a player middle clicks while aiming on a block or entity.
     * The server is not aware of when the client left clicks empty space, you will need to tell the server yourself.
     * You can change the Result ItemStack here or set it to {@link ItemStack#EMPTY} or cancel the event to prevent further updating.
     */
    @Cancelable
    public static class Pick extends PlayerInteractEvent
    {
        private RayTraceResult target;
        private ItemStack currentResult;
        public Pick(EntityPlayer player, RayTraceResult target, ItemStack currentResult) {
            super(player, EnumHand.MAIN_HAND, target.typeOfHit == RayTraceResult.Type.BLOCK ? target.getBlockPos() : new BlockPos(target.entityHit), target.typeOfHit == RayTraceResult.Type.BLOCK ? target.sideHit : null);
            this.target = target;
            this.currentResult = currentResult;
        }

        public RayTraceResult getRayTraceResult() {
            return target;
        }

        public ItemStack getCurrentResult() {
            return this.currentResult;
        }

        public void setCurrentResult(ItemStack newResult) {
            this.currentResult = newResult;
        }
    }

    /**
     * @return The hand involved in this interaction. Will never be null.
     */
    @Nonnull
    public EnumHand getHand()
    {
        return hand;
    }

    /**
     * @return The itemstack involved in this interaction, {@code ItemStack.EMPTY} if the hand was empty.
     */
    @Nonnull
    public ItemStack getItemStack()
    {
        return getEntityPlayer().getHeldItem(hand);
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
    public EnumFacing getFace()
    {
        return face;
    }

    /**
     * @return Convenience method to get the world of this interaction.
     */
    public World getWorld()
    {
        return getEntityPlayer().getEntityWorld();
    }

    /**
     * @return The effective, i.e. logical, side of this interaction. This will be {@link Side#CLIENT} on the client thread, and {@link Side#SERVER} on the server thread.
     */
    public Side getSide()
    {
        return getWorld().isRemote ? Side.CLIENT : Side.SERVER;
    }

}
