package net.minecraftforge.event.entity.player;

import java.io.File;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
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
    private final EntityPlayer entityPlayer;
    public PlayerEvent(EntityPlayer player)
    {
        super(player);
        entityPlayer = player;
    }

    public EntityPlayer getEntityPlayer()
    {
        return entityPlayer;
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
        private final IBlockState state;
        private boolean success;

        public HarvestCheck(EntityPlayer player, IBlockState state, boolean success)
        {
            super(player);
            this.state = state;
            this.success = success;
        }

        public IBlockState getTargetBlock() { return this.state; }
        public boolean canHarvest() { return this.success; }
        public void setCanHarvest(boolean success){ this.success = success; }
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
        private final IBlockState state;
        private final float originalSpeed;
        private float newSpeed = 0.0f;
        private final BlockPos pos; // Y position of -1 notes unknown location

        public BreakSpeed(EntityPlayer player, IBlockState state, float original, BlockPos pos)
        {
            super(player);
            this.state = state;
            this.originalSpeed = original;
            this.setNewSpeed(original);
            this.pos = pos;
        }

        public IBlockState getState() { return state; }
        public float getOriginalSpeed() { return originalSpeed; }
        public float getNewSpeed() { return newSpeed; }
        public void setNewSpeed(float newSpeed) { this.newSpeed = newSpeed; }
        public BlockPos getPos() { return pos; }
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
        private final String username;
        private String displayname;

        public NameFormat(EntityPlayer player, String username) {
            super(player);
            this.username = username;
            this.setDisplayname(username);
        }

        public String getUsername()
        {
            return username;
        }

        public String getDisplayname()
        {
            return displayname;
        }

        public void setDisplayname(String displayname)
        {
            this.displayname = displayname;
        }
    }

    /**
     * Fired when the EntityPlayer is cloned, typically caused by the network sending a RESPAWN_PLAYER event.
     * Either caused by death, or by traveling from the End to the overworld.
     */
    public static class Clone extends PlayerEvent
    {
        private final EntityPlayer original;
        private final boolean wasDeath;

        public Clone(EntityPlayer _new, EntityPlayer oldPlayer, boolean wasDeath)
        {
            super(_new);
            this.original = oldPlayer;
            this.wasDeath = wasDeath;
        }

        /**
         * The old EntityPlayer that this new entity is a clone of.
         */
        public EntityPlayer getOriginal()
        {
            return original;
        }

        /**
         * True if this event was fired because the player died.
         * False if it was fired because the entity switched dimensions.
         */
        public boolean isWasDeath()
        {
            return wasDeath;
        }
    }

    /**
     * Fired when an Entity is started to be "tracked" by this player (the player receives updates about this entity, e.g. motion).
     *
     */
    public static class StartTracking extends PlayerEvent {

        private final Entity target;

        public StartTracking(EntityPlayer player, Entity target)
        {
            super(player);
            this.target = target;
        }

        /**
         * The Entity now being tracked.
         */
        public Entity getTarget()
        {
            return target;
        }
    }

    /**
     * Fired when an Entity is stopped to be "tracked" by this player (the player no longer receives updates about this entity, e.g. motion).
     *
     */
    public static class StopTracking extends PlayerEvent {

        private final Entity target;

        public StopTracking(EntityPlayer player, Entity target)
        {
            super(player);
            this.target = target;
        }

        /**
         * The Entity no longer being tracked.
         */
        public Entity getTarget()
        {
            return target;
        }
    }

    /**
     * The player is being loaded from the world save. Note that the
     * player won't have been added to the world yet. Intended to
     * allow mods to load an additional file from the players directory
     * containing additional mod related player data.
     */
    public static class LoadFromFile extends PlayerEvent {
        private final File playerDirectory;
        private final String playerUUID;

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
            return new File(this.getPlayerDirectory(), this.getPlayerUUID() +"."+suffix);
        }

        /**
         * The directory where player data is being stored. Use this
         * to locate your mod additional file.
         */
        public File getPlayerDirectory()
        {
            return playerDirectory;
        }

        /**
         * The UUID is the standard for player related file storage.
         * It is broken out here for convenience for quick file generation.
         */
        public String getPlayerUUID()
        {
            return playerUUID;
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
        private final File playerDirectory;
        private final String playerUUID;

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
            return new File(this.getPlayerDirectory(), this.getPlayerUUID() +"."+suffix);
        }

        /**
         * The directory where player data is being stored. Use this
         * to locate your mod additional file.
         */
        public File getPlayerDirectory()
        {
            return playerDirectory;
        }

        /**
         * The UUID is the standard for player related file storage.
         * It is broken out here for convenience for quick file generation.
         */
        public String getPlayerUUID()
        {
            return playerUUID;
        }
    }
}
