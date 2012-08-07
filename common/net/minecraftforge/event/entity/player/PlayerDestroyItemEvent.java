package net.minecraftforge.event.entity.player;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;

public class PlayerDestroyItemEvent extends PlayerEvent
{
    public final ItemStack original;
    public PlayerDestroyItemEvent(EntityPlayer player, ItemStack original)
    {
        super(player);
        this.original = original;
    }

}
