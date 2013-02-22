package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;

public class PlayerBlockBreakEvent extends PlayerBlockChangeEvent {

    public PlayerBlockBreakEvent(EntityPlayer player, World world, int x, int y, int z)
    {
        super(player, world, x, y, z, 0, 0);
    }
    
    /**
     * Called before the block is destroyed.
     * 
     */
    @Cancelable
    public static class Pre extends PlayerBlockBreakEvent {

        public Pre(EntityPlayer player, World world, int x, int y, int z)
        {
            super(player, world, x, y, z);
        }
        
    }
    
    /**
     * Called after the block is destroyed
     *
     */
    public static class Post extends PlayerBlockBreakEvent {

        public Post(EntityPlayer player, World world, int x, int y, int z)
        {
            super(player, world, x, y, z);
        }
        
    }

}
