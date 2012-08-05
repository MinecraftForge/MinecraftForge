package net.minecraftforge.event.entity;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;

public class ArrowNockEvent extends PlayerEvent
{
    private ItemStack result;
    
    public ArrowNockEvent(EntityPlayer player, ItemStack result)
    {
        super(player);
        this.result = result;
    }
    
    @Override
    public boolean isCancelable()
    {
        return true;
    }

    public ItemStack getResultStack()
    {
        return result;
    }
    
    public void setResultStack(ItemStack result)
    {
        this.result = result;
    }
}
