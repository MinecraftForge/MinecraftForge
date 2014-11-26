package net.minecraftforge.event.entity.player;

import java.io.File;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.event.entity.living.LivingEvent;

/**
 * PlayerEvent is fired whenever an event involving Living entities occurs. <br>
 * If a method utilizes this {@link Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class PlayerEvent extends LivingEvent
{
    public final EntityPlayer entityPlayer;
    public PlayerEvent(EntityPlayer player)
    {
        super(player);
        entityPlayer = player;
    }

    /**
     * HarvestCheck is fired when a player attempts to harvest a block.<br>
     * This event is fired whenever a player attempts to harvest a block in
     * EntityPlayer#canHarvestBlock(Block).<br>
     * <br>
     * This event is fired via the {@link ForgeEventFactory#doPlayerHarvestCheck(EntityPlayer, Block, boolean)}.<br>
     * <br>
     * {@link #block} contains the Block that is being checked for harvesting. <br>
     * {@link #success} contains the boolean value for whether the Block will be successfully harvested. <br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    public static class HarvestCheck extends PlayerEvent
    {
        public final Block block;
        public boolean success;

        public HarvestCheck(EntityPlayer player, Block block, boolean success)
        {
            super(player);
            this.block = block;
            this.success = success;
        }
    }

    /**
     * BreakSpeed is fired when a player attempts to harvest a block.<br>
     * This event is fired whenever a player attempts to harvest a block in
     * EntityPlayer#canHarvestBlock(Block).<br>
     * <br>
     * This event is fired via the {@link ForgeEventFactory#getBreakSpeed(EntityPlayer, IBlockState, float, BlockPos)}.<br>
     * <br>
     * {@link #state} contains the block being broken. <br>
     * {@link #originalSpeed} contains the original speed at which the player broke the block. <br>
     * {@link #newSpeed} contains the newSpeed at which the player will break the block. <br>
     * {@link #pos} contains the coordinates at which this event is occurring. Y value -1 means location is unknown.<br>
     * <br>
     * This event is {@link Cancelable}.<br>
     * If it is canceled, the player is unable to break the block.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    @Cancelable
    public static class BreakSpeed extends PlayerEvent
    {
        public final IBlockState state;
        public final float originalSpeed;
        public float newSpeed = 0.0f;
        public final BlockPos pos; // Y position of -1 notes unknown location

        public BreakSpeed(EntityPlayer player, IBlockState state, float original, BlockPos pos)
        {
            super(player);
            this.state = state;
            this.originalSpeed = original;
            this.newSpeed = original;
            this.pos = pos;
        }
    }

    /**
     * NameFormat is fired when a player's display name is retrieved.<br>
     * This event is fired whenever a player's name is retrieved in
     * EntityPlayer#getDisplayName() or EntityPlayer#refreshDisplayName().<br>
     * <br>
     * This event is fired via the {@link ForgeEventFactory#getPlayerDisplayName(EntityPlayer, String)}.<br>
     * <br>
     * {@link #username} contains the username of the player.
     * {@link #displayname} contains the display name of the player.
     * <br>
     * This event is not {@link Cancelable}.
     * <br>
     * This event does not have a result. {@link HasResult}
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    public static class NameFormat extends PlayerEvent
    {
        public final String username;
        public String displayname;

        public NameFormat(EntityPlayer player, String username) {
            super(player);
            this.username = username;
            this.displayname = username;
        }
    }

    /**
     * Fired when the EntityPlayer is cloned, typically caused by the network sending a RESPAWN_PLAYER event.
     * Either caused by death, or by traveling from the End to the overworld.
     */
    public static class Clone extends PlayerEvent
    {
        /**
         * The old EntityPlayer that this new entity is a clone of.
         */
        public final EntityPlayer original;
        /**
         * True if this event was fired because the player died.
         * False if it was fired because the entity switched dimensions.
         */
        public final boolean wasDeath;

        public Clone(EntityPlayer _new, EntityPlayer oldPlayer, boolean wasDeath)
        {
            super(_new);
            this.original = oldPlayer;
            this.wasDeath = wasDeath;
        }
    }

    /**
     * Fired when an Entity is started to be "tracked" by this player (the player receives updates about this entity, e.g. motion).
     *
     */
    public static class StartTracking extends PlayerEvent {

        /**
         * The Entity now being tracked.
         */
        public final Entity target;

        public StartTracking(EntityPlayer player, Entity target)
        {
            super(player);
            this.target = target;
        }

    }

    /**
     * Fired when an Entity is stopped to be "tracked" by this player (the player no longer receives updates about this entity, e.g. motion).
     *
     */
    public static class StopTracking extends PlayerEvent {

        /**
         * The Entity no longer being tracked.
         */
        public final Entity target;

        public StopTracking(EntityPlayer player, Entity target)
        {
            super(player);
            this.target = target;
        }

    }

    /**
     * The player is being loaded from the world save. Note that the
     * player won't have been added to the world yet. Intended to
     * allow mods to load an additional file from the players directory
     * containing additional mod related player data.
     */
    public static class LoadFromFile extends PlayerEvent {
        /**
         * The directory where player data is being stored. Use this
         * to locate your mod additional file.
         */
        public final File playerDirectory;
        /**
         * The UUID is the standard for player related file storage.
         * It is broken out here for convenience for quick file generation.
         */
        public final String playerUUID;

        public LoadFromFile(EntityPlayer player, File originDirectory, String playerUUID)
        {
            super(player);
            this.playerDirectory = originDirectory;
            this.playerUUID = playerUUID;
        }

        /**
         * Construct and return a recommended file for the supplied suffix
         * @param suffix The suffix to use.
         * @return
         */
        public File getPlayerFile(String suffix)
        {
            if ("dat".equals(suffix)) throw new IllegalArgumentException("The suffix 'dat' is reserved");
            return new File(this.playerDirectory, this.playerUUID+"."+suffix);
        }
    }
    /**
     * The player is being saved to the world store. Note that the
     * player may be in the process of logging out or otherwise departing
     * from the world. Don't assume it's association with the world.
     * This allows mods to load an additional file from the players directory
     * containing additional mod related player data.
     * <br>
     * Use this event to save the additional mod related player data to the world.
     *
     * <br>
     * <em>WARNING</em>: Do not overwrite the player's .dat file here. You will
     * corrupt the world state.
     */
    public static class SaveToFile extends PlayerEvent {
        /**
         * The directory where player data is being stored. Use this
         * to locate your mod additional file.
         */
        public final File playerDirectory;
        /**
         * The UUID is the standard for player related file storage.
         * It is broken out here for convenience for quick file generation.
         */
        public final String playerUUID;

        public SaveToFile(EntityPlayer player, File originDirectory, String playerUUID)
        {
            super(player);
            this.playerDirectory = originDirectory;
            this.playerUUID = playerUUID;
        }

        /**
         * Construct and return a recommended file for the supplied suffix
         * @param suffix The suffix to use.
         * @return
         */
        public File getPlayerFile(String suffix)
        {
            if ("dat".equals(suffix)) throw new IllegalArgumentException("The suffix 'dat' is reserved");
            return new File(this.playerDirectory, this.playerUUID+"."+suffix);
        }
    }
}
