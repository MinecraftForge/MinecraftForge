package net.minecraftforge.event.entity.player;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class FishBiteEvent extends PlayerEvent 
{
    /**
     * This event is called before checking if a fish bites the hook. It can be
     * canceled to stop fish from biting, or the chance of a fish biting can be
     * changed
     * 
     * If the result is set to 'ALLOW', then a the fish hook will use the event's
     * chance value to check if a fish can be caught.
     * 
     * If the result is set to 'DENY', then a fish cannot be caught.
     */

    public short chance;
    public final World world;
    public final double X;
    public final double Y;
    public final double Z;

    public FishBiteEvent(EntityPlayer player, World world, double x, double y, double z) 
    {
        super(player);
        this.world = world;
        this.X = x;
        this.Y = y;
        this.Z = z;
    }
}
