package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

@Cancelable
@Event.HasResult
public class BonemealEvent extends PlayerEvent
{
    /**
     * This event is called when a player attempts to use Bonemeal on a block.
     * It can be canceled to completely prevent any further processing.
     * 
     * You can also set the result to ALLOW to mark the event as processed 
     * and use up a bonemeal from the stack but do no further processing.
     * 
     * setResult(ALLOW) is the same as the old setHandeled()
     */

    public final World world;
    public final int ID;
    public final int X;
    public final int Y;
    public final int Z;
    
    public BonemealEvent(EntityPlayer player, World world, int id, int x, int y, int z)
    {
        super(player);
        this.world = world;
        this.ID = id;
        this.X = x;
        this.Y = y;
        this.Z = z;
    }
}
