package net.minecraftforge.event.entity.player;

import net.minecraft.block.Block;
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
        public final World world;
        public final int x;
        public final int y;
        public final int z;
        public final int metadata;
        public final float originalSpeed;
        public float newSpeed = 0.0f;

        public BreakSpeed(EntityPlayer player, Block block, int metadata, float original)
        {
            this(player, block, null, 0, 0, 0, metadata, original);
        }

        public BreakSpeed(EntityPlayer player, Block block, World world, int x, int y, int z, int metadata, float original)
        {
            super(player);
            this.block = block;
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
            this.metadata = metadata;
            this.originalSpeed = original;
            this.newSpeed = original;
        }
    }
}
