package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class ArrowLooseEvent extends PlayerEvent
{
    public final ItemStack bow;
    public int charge;
    
    public ArrowLooseEvent(EntityPlayer player, ItemStack bow, int charge)
    {
        super(player);
        this.bow = bow;
        this.charge = charge;
    }
}
