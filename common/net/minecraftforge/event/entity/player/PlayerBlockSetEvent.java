package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;

public class PlayerBlockSetEvent extends PlayerBlockChangeEvent {
    
    public PlayerBlockSetEvent(EntityPlayer player, World world, int x, int y, int z, int newId, int newMeta)
    {
        super(player, world, x, y, z, newId, newMeta);
    }
    
    /**
     * if you want to cancel the placing or want to change the block being placed, use this.
     * The block has not yet been set.
     *
     */
    @Cancelable
    public static class Pre extends PlayerBlockSetEvent {

        public Pre(EntityPlayer player, World world, int x, int y, int z, int newId, int newMeta)
        {
            super(player, world, x, y, z, newId, newMeta);
        }
        
    }
    
    /**
     * Called after the block has been set. If you want to cancel the event or modify the block being placed, use {@link Pre}.
     * Changing the newId / newMeta on this event has no effect.
     *
     */
    public static class Post extends PlayerBlockSetEvent {

        public Post(EntityPlayer player, World world, int x, int y, int z, int newId, int newMeta)
        {
            super(player, world, x, y, z, newId, newMeta);
        }
        
    }

}
