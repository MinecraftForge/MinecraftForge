package net.minecraftforge.event.entity.player;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

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
    public final Block block;
    public final int x;
    public final int y;
    public final int z;
    
    public BonemealEvent(EntityPlayer player, World world, Block block, int x, int y, int z)
    {
        super(player);
        this.world = world;
        this.block = block;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
