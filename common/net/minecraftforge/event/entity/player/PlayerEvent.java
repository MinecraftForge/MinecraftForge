package net.minecraftforge.event.entity.player;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
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

    /**
     * Event called only when the player can NOT harvest the block with the current item.
     * Event will not always have the valid location, y == 0 is a good indication of this.
     */
    public static class HarvestCheck extends PlayerEvent
    {
        public final Block block;
        public final World world;
        public final int x;
        public final int y;
        public final int z;
        public boolean success;

        public HarvestCheck(EntityPlayer player, Block block, World world, int x, int y, int z, boolean success)
        {
            super(player);
            this.block = block;
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
            this.success = success;
        }
    }

    @Cancelable
    public static class BreakSpeed extends PlayerEvent
    {
        public final Block block;
        public final World world;
        public final int x;
        public final int y;
        public final int z;
        public final float originalSpeed;
        public float newSpeed = 0.0f;

        public BreakSpeed(EntityPlayer player, Block block, World world, int x, int y, int z, float original)
        {
            super(player);
            this.block = block;
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
            this.originalSpeed = original;
            this.newSpeed = original;
        }
    }
}
