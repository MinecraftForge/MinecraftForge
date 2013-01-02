package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class PlayerBreakBlock extends PlayerEvent 
{
    public final World world;
    public final int blockX;
    public final int blockY;
    public final int blockZ;
    
    public PlayerBreakBlock(World world, int x, int y, int z, EntityPlayer entityPlayer) 
    {
        super(entityPlayer);
        this.world = world;
        blockX = x;
        blockY = y;
        blockZ = z;
    }

}
