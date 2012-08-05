package net.minecraftforge.event.entity;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;

public class ArrowLooseEvent extends PlayerEvent
{
    private final ItemStack bow;
    private int charge;
    
    public ArrowLooseEvent(EntityPlayer player, ItemStack bow, int charge)
    {
        super(player);
        this.bow = bow;
        this.charge = charge;
    }
    
    @Override
    public boolean isCancelable()
    {
        return true;
    }

    public ItemStack getBow()
    {
        return bow;
    }
    
    public int getCharge()
    {
        return charge;
    }
    
    public void setCharge(int charge)
    {
        this.charge = charge;
    }
}
