package net.minecraftforge.event.entity.player;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class BonemealEvent extends PlayerEvent
{
    public final World world;
    public final int ID;
    public final int X;
    public final int Y;
    public final int Z;
    private boolean handeled = false;
    
    public BonemealEvent(EntityPlayer player, World world, int id, int x, int y, int z)
    {
        super(player);
        this.world = world;
        this.ID = id;
        this.X = x;
        this.Y = y;
        this.Z = z;
    }
    
    public void setHandeled()
    {
        handeled = true;
    }
    
    public boolean isHandeled()
    {
        return handeled;
    }
}
