package net.minecraftforge.event.entity.player;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent;

public class PlayerEvent extends LivingEvent
{
    public final EntityPlayer entityPlayer;
    public PlayerEvent(EntityPlayer player)
    {
        super(player);
        entityPlayer = player;
    }
    
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

    @Cancelable
    public static class BreakSpeed extends PlayerEvent
    {
        public final Block block;
        public final int metadata;
        public final float originalSpeed;
        public float newSpeed = 0.0f;
        public final int x;
        public final int y; // -1 notes unknown location
        public final int z;

        @Deprecated
        public BreakSpeed(EntityPlayer player, Block block, int metadata, float original)
        {
            this(player, block, metadata, original, 0, -1, 0);
        }

        public BreakSpeed(EntityPlayer player, Block block, int metadata, float original, int x, int y, int z)
        {
            super(player);
            this.block = block;
            this.metadata = metadata;
            this.originalSpeed = original;
            this.newSpeed = original;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

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
}
