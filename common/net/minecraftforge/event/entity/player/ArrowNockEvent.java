package net.minecraftforge.event.entity.player;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class ArrowNockEvent extends PlayerEvent
{
    public ItemStack result;
    
    public ArrowNockEvent(EntityPlayer player, ItemStack result)
    {
        super(player);
        this.result = result;
    }
}
