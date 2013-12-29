package net.minecraftforge.event.entity.player;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.event.Cancelable;
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

        public BreakSpeed(EntityPlayer player, Block block, int metadata, float original)
        {
            super(player);
            this.block = block;
            this.metadata = metadata;
            this.originalSpeed = original;
            this.newSpeed = original;
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
     * Subscribe to this event to modify the spawnpoint of the player
     * The coordinates in this event are the coordinates where the player would normally spawn
     *  (like server spawnpoint or bed). Modify this to change the spawnpoint.
     * You can also modify the yaw and pitch the player is looking at after spawning
     *
     * SERVER ONLY
     */
    public static class Spawnpoint extends PlayerEvent
    {
        public ChunkCoordinates spawnPoint;
        public float yaw = 0.0F;
        public float pitch = 0.0F;

        public Spawnpoint(EntityPlayerMP player, ChunkCoordinates originalSpawn)
        {
            super(player);
            this.spawnPoint = originalSpawn;
        }
    }
}
