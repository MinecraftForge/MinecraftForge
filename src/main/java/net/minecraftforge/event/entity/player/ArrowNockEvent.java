package net.minecraftforge.event.entity.player;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

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
