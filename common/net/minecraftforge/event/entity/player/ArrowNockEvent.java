package net.minecraftforge.event.entity.player;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class ArrowNockEvent extends PlayerEvent
{
    public ItemStack result;
    
    /**
     * Fired when a Player starts holding Rightclick with a bow equipped. If you set the event canceled,
     * the result ItemStack you specify here will end up in a player's hands (over the original bow).
     * You can then use that new ItemStack (eg. another bow) to do whatever. Note that if you intend for the
     * vanilla bow to return once you are finished, you must take care of that yourself.
     * 
     * @param player Player about to draw his bow
     * @param result ItemStack you wish to replace the player's bow with, if you cancel the event
     */
    public ArrowNockEvent(EntityPlayer player, ItemStack result)
    {
        super(player);
        this.result = result;
    }
}
