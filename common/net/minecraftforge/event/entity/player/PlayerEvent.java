package net.minecraftforge.event.entity.player;

import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
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
    
    public static class HarvestBlock extends PlayerEvent
    {
        public final Block block;
        public final int x;
        public final int y;
        public final int z;
        public final int metadata;

        public HarvestBlock(EntityPlayer player, Block block, int x, int y, int z, int metadata)
        {
            super(player);
            this.block = block;
            this.x = x;
            this.y = y;
            this.z = z;
            this.metadata = metadata;
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
}
