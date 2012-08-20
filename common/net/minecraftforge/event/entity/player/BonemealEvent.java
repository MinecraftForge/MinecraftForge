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
    
    /**
     * Called when a Player Rightclicks a Block with Bonemeal equipped. Setting this event canceled will prevent
     * anything from happening. Setting this event Handeled will prevent anything from happening and consume one
     * Stack of Bonemeal.
     * @param player Player doing the clicking
     * @param world World this is happening in
     * @param id Block ID of the targeted Block
     * @param x Coordinate of clicked Block
     * @param y Coordinate of clicked Block
     * @param z Coordinate of clicked Block
     */
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
