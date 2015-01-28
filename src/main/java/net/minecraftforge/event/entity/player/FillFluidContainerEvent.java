package net.minecraftforge.event.entity.player;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

/**
 * This event is fired when a player attempts to use an empty fluid container, it 
 * can be canceled to completely prevent any further processing.
 * 
 * If you set the result to 'ALLOW', it means that you have processed 
 * the event and wants the basic functionality of adding the new 
 * ItemStack to your inventory and reducing the stack size to process.
 * setResult(ALLOW) is the same as the old setHandeled();
 */
@Cancelable
@Event.HasResult
public class FillFluidContainerEvent extends PlayerEvent {

    public final ItemStack current;
    public final World world;
    public final MovingObjectPosition target;

    private ItemStack filledContainer;

    public FillFluidContainerEvent(EntityPlayer player, ItemStack current, World world, MovingObjectPosition target)
    {
        super(player);
        this.current = current;
        this.world = world;
        this.target = target;
    }
        
    public ItemStack getFilledContainer() {
        return this.filledContainer;
    }
    
    public void setFilledContainer(ItemStack filled) {
        this.filledContainer = filled;
    }
}
