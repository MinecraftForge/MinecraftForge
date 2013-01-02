package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class PlayerPlaceBlock extends PlayerEvent 
{
    public final World world;
    public final int blockX;
    public final int blockY;
    public final int blockZ;
    public final  int side;
    public final  float hitx;
    public final  float hity;
    public final  float hitz;
    
    public PlayerPlaceBlock(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side, float hitx, float hity, float hitz) 
    {
        super(entityPlayer);
        this.world = world;
        blockX = x;
        blockY = y;
        blockZ = z;
        this.side = side;
        this.hitx = hitx;
        this.hity = hity;
        this.hitz = hitz;
    }
}
