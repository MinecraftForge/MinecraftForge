package net.minecraftforge.event.entity.player;

import com.google.common.base.Preconditions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
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
 *
 * Note that in general, these events occur on the server independently from the client.
 * Thus, handling things differently on the two sides may result in desynchronization!
 **/
public class PlayerInteractEvent extends PlayerEvent
{
    private final EnumHand hand;
    private final ItemStack stack;
    private final BlockPos pos;
    private final EnumFacing face;

    private PlayerInteractEvent(EntityPlayer player, EnumHand hand, ItemStack stack, BlockPos pos, EnumFacing face)
    {
        super(Preconditions.checkNotNull(player, "Null player in PlayerInteractEvent!"));
        this.hand = Preconditions.checkNotNull(hand, "Null hand in PlayerInteractEvent!");
        this.stack = stack;
        this.pos = Preconditions.checkNotNull(pos, "Null position in PlayerInteractEvent!");
        this.face = face;
    }

    /**
     * Marker for all the events related to right click.
     */
    public static class RightClick extends PlayerInteractEvent {

        private EnumActionResult substituteResult;

        private RightClick(EntityPlayer player, EnumHand hand, ItemStack stack, BlockPos pos, EnumFacing face) {
            super(player, hand, stack, pos, face);
            this.substituteResult = EnumActionResult.PASS;
        }

        /**
         * @return The substitute result to use when a right click interaction is canceled. See each event for more information.
         */
        @Nonnull
        public EnumActionResult getSubstituteResult()
        {
            return substituteResult;
        }

        /**
         * Sets the substitute result to use when a right click interaction is canceled.
         * @param result The result to use when this event is canceled.
         */
        public void setSubstituteResult(EnumActionResult result)
        {
            this.substituteResult = result;
        }

    }

    /**
     * This event is fired on both sides whenever a player right clicks an entity.
     *
     * "Interact at" is an interact where the local vector (which part of the entity you clicked) is known.
     * The state of this event affects whether {@link Entity#applyPlayerInteraction} is called.
     *
     * If the event is canceled, the value of {@link #substituteResult}
     * is used as a result instead of calling the above method.
     *
     * If this result is {@link EnumActionResult#SUCCESS}, then processing ends.
     * Otherwise, the client will attempt the next interaction, {@link EntityInteract}.
     */
    @Cancelable
    public static class EntityInteractSpecific extends RightClick
    {
        private final Vec3d localPos;
        private final Entity target;

        public EntityInteractSpecific(EntityPlayer player, EnumHand hand, ItemStack stack, Entity target, Vec3d localPos)
        {
            super(player, hand, stack, new BlockPos(target), null);
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
     * The state of this event affects whether {@link Entity#processInitialInteract} and {@link net.minecraft.item.Item#itemInteractionForEntity} are called.
     *
     * If the event is canceled, the value of {@link #substituteResult}
     * is used as a result instead of calling the above two methods.
     *
     * If this result is {@link EnumActionResult#SUCCESS}, then processing ends.
     * Otherwise, the client will attempt the next interaction, {@link RightClickItem} or {@link RightClickEmpty}.
     */
    @Cancelable
    public static class EntityInteract extends RightClick
    {
        private final Entity target;

        public EntityInteract(EntityPlayer player, EnumHand hand, ItemStack stack, Entity target)
        {
            super(player, hand, stack, new BlockPos(target), null);
            this.target = target;
        }

        public Entity getTarget()
        {
            return target;
        }
    }

    /**
     * This event is fired on both sides whenever the player right clicks while targeting a block.
     *
     * The state of this event controls which of {@link net.minecraft.block.Block#onBlockActivated} and/or {@link net.minecraft.item.Item#onItemUse}
     * will be called after {@link net.minecraft.item.Item#onItemUseFirst} is called. See the getters below.
     *
     * If the event is canceled, the value of {@link #substituteResult}
     * is used as a result instead of calling the above three methods.
     *
     * If this result is {@link EnumActionResult#SUCCESS}, then processing ends.
     * Otherwise, the client will attempt the next interaction, {@link RightClickItem} or {@link RightClickEmpty}.
     */
    @Cancelable
    public static class RightClickBlock extends RightClick
    {
        private Result useBlock = DEFAULT;
        private Result useItem = DEFAULT;
        private final Vec3d hitVec;

        public RightClickBlock(EntityPlayer player, EnumHand hand, ItemStack stack,
                               BlockPos pos, EnumFacing face, Vec3d hitVec) {
            super(player, hand, stack, pos, face);
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
    }

    /**
     * This event is fired on both sides before the player triggers {@link net.minecraft.item.Item#onItemRightClick}.
     * Note that this is NOT fired if the player is targeting a block. For that case, see {@link RightClickBlock}.
     *
     * If the event is canceled, the value of {@link #substituteResult}
     * is used as a result instead of calling the above method.
     *
     * If this result is {@link EnumActionResult#SUCCESS}, then processing ends.
     * Otherwise, the client will attempt interactions anew with the other hand, or stop if this is the last hand.
     */
    @Cancelable
    public static class RightClickItem extends RightClick
    {
        public RightClickItem(EntityPlayer player, EnumHand hand, ItemStack stack)
        {
            super(player, hand, stack, new BlockPos(player), null);
        }
    }

    /**
     * This event is fired on the client side when the player right clicks empty space with an empty hand.
     * The server is not aware of when the client right clicks empty space with an empty hand, you will need to tell the server yourself.
     * This event cannot be canceled and thus does not consider {@link #substituteResult}.
     */
    public static class RightClickEmpty extends RightClick
    {
        public RightClickEmpty(EntityPlayer player, EnumHand hand)
        {
            super(player, hand, null, new BlockPos(player), null);
        }
    }

    /**
     * This event is fired when a player left clicks while targeting a block.
     * This event controls which of {@link net.minecraft.block.Block#onBlockClicked} and/or the item harvesting methods will be called
     * See the getters below.
     * Canceling the event will cause none of the above noted methods to be called.
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
            super(player, EnumHand.MAIN_HAND, player.getHeldItem(EnumHand.MAIN_HAND), pos, face);
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
        public LeftClickEmpty(EntityPlayer player, ItemStack stack)
        {
            super(player, EnumHand.MAIN_HAND, stack, new BlockPos(player), null);
        }
    }

    /**
     * @return The hand involved in this interaction. Will never be null.
     */
    public EnumHand getHand()
    {
        return hand;
    }

    /**
     * @return The itemstack involved in this interaction, or null if the hand was empty.
     */
    @Nullable
    public ItemStack getItemStack()
    {
        return stack;
    }

    /**
     * If the interaction was on an entity, will be a BlockPos centered on the entity.
     * If the interaction was on a block, will be the position of that block.
     * Otherwise, will be a BlockPos centered on the player.
     * Will never be null.
     * @return The position involved in this interaction.
     */
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
