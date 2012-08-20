package net.minecraftforge.event.entity.player;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class ArrowLooseEvent extends PlayerEvent
{
    public final ItemStack bow;
    public int charge;
    
    /**
     * Fired when a Player Entity stops using a bow, regardless of other factors (how long drawn, does he even have arrows)
     * Setting this event canceled has the same effect as if the player didnt draw long enough.
     * You can change the charge value to something higher or lower and the change will take effect, unless you cancel.
     * 
     * @param player Player firing the arrow
     * @param bow Itemstack being used to fire
     * @param charge value for how long the bow was drawn, typically starts at 0 and goes up to 72000 (max power)
     */
    public ArrowLooseEvent(EntityPlayer player, ItemStack bow, int charge)
    {
        super(player);
        this.bow = bow;
        this.charge = charge;
    }
}
