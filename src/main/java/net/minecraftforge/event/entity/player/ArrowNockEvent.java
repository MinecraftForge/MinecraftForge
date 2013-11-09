package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
