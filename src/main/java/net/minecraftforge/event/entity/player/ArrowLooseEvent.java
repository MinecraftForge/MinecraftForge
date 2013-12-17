package net.minecraftforge.event.entity.player;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

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
