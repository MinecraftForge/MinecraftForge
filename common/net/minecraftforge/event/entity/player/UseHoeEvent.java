package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

@Cancelable
@Event.HasResult
public class UseHoeEvent extends PlayerEvent
{
    /**
     * This event is fired when a player attempts to use a Hoe on a block, it 
     * can be canceled to completely prevent any further processing.
     * 
     * You can also set the result to ALLOW to mark the event as processed 
     * and damage the hoe.
     * 
     * setResult(ALLOW) is the same as the old setHandeled();
     */

    public final ItemStack current;
    public final World world;
    public final int x;
    public final int y;
    public final int z;
    
    private boolean handeled = false;
    
    public UseHoeEvent(EntityPlayer player, ItemStack current, World world, int x, int y, int z)
    {
        super(player);
        this.current = current;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
