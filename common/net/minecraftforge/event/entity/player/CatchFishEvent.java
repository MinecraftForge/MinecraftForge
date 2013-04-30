package net.minecraftforge.event.entity.player;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class CatchFishEvent extends PlayerEvent 
{
    /**
     * This event is called when a player catches a fish. It can be canceled to
     * completely prevent any further processing.
     * 
     * If you set the result to 'ALLOW', it means that you have set the result
     * to the item that will be caught instead of the default fish. The rod will
     * be damaged base on the damage specified in the event.
     * 
     * If you set the result to 'DENY' no item will be caught and the rod will
     * be damaged by the amount specified in the event.
     */

    public ItemStack result;
    public byte damage;
    public final World world;
    public final double X;
    public final double Y;
    public final double Z;

    public CatchFishEvent(EntityPlayer player, World world, double x, double y, double z) 
    {
        super(player);
        this.damage = 1;
        this.world = world;
        this.X = x;
        this.Y = y;
        this.Z = z;
    }
}
