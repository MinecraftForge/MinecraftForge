package org.bukkit.event.block;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Called when a block is ignited. If you want to catch when a Player places fire, you need to use {@link BlockPlaceEvent}.
 * <p>
 * If a Block Ignite event is cancelled, the block will not be ignited.
 */
public class BlockIgniteEvent extends BlockEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final IgniteCause cause;
    private final Entity ignitingEntity;
    private final Block ignitingBlock;
    private boolean cancel;

    /**
     * Deprecated. Use {@link BlockIgniteEvent#BlockIgniteEvent(Block, IgniteCause, Entity)} instead.
     */
    @Deprecated
    public BlockIgniteEvent(final Block theBlock, final IgniteCause cause, final Player thePlayer) {
        this(theBlock, cause, (Entity) thePlayer);
    }

    public BlockIgniteEvent(final Block theBlock, final IgniteCause cause, final Entity ignitingEntity) {
        this(theBlock, cause, ignitingEntity, null);
    }

    public BlockIgniteEvent(final Block theBlock, final IgniteCause cause, final Block ignitingBlock) {
        this(theBlock, cause, null, ignitingBlock);
    }

    public BlockIgniteEvent(final Block theBlock, final IgniteCause cause, final Entity ignitingEntity, final Block ignitingBlock) {
        super(theBlock);
        this.cause = cause;
        this.ignitingEntity = ignitingEntity;
        this.ignitingBlock = ignitingBlock;
        this.cancel = false;
    }

    public boolean isCancelled() {
        return cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    /**
     * Gets the cause of block ignite.
     *
     * @return An IgniteCause value detailing the cause of block ignition
     */
    public IgniteCause getCause() {
        return cause;
    }

    /**
     * Gets the player who ignited this block
     *
     * @return The Player that placed/ignited the fire block, or null if not ignited by a Player.
     */
    public Player getPlayer() {
        if (ignitingEntity instanceof Player) {
            return (Player) ignitingEntity;
        }

        return null;
    }

    /**
     * Gets the entity who ignited this block
     *
     * @return The Entity that placed/ignited the fire block, or null if not ignited by a Entity.
     */
    public Entity getIgnitingEntity() {
        return ignitingEntity;
    }

    /**
     * Gets the block who ignited this block
     *
     * @return The Block that placed/ignited the fire block, or null if not ignited by a Block.
     */
    public Block getIgnitingBlock() {
        return ignitingBlock;
    }

    /**
     * An enum to specify the cause of the ignite
     */
    public enum IgniteCause {

        /**
         * Block ignition caused by lava.
         */
        LAVA,
        /**
         * Block ignition caused by a player or dispenser using flint-and-steel.
         */
        FLINT_AND_STEEL,
        /**
         * Block ignition caused by dynamic spreading of fire.
         */
        SPREAD,
        /**
         * Block ignition caused by lightning.
         */
        LIGHTNING,
        /**
         * Block ignition caused by an entity using a fireball.
         */
        FIREBALL,
        /**
         * Block ignition caused by an Ender Crystal.
         */
        ENDER_CRYSTAL,
        /**
         * Block ignition caused by explosion.
         */
        EXPLOSION,
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
