package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ArmorChangedEvent extends PlayerEvent
{
    public final ItemStack armor;
    public final Slot slot;
    public ArmorChangedEvent(EntityPlayer player, ItemStack armor, Slot slot)
    {
        super(player);
        this.armor = armor;
        this.slot = slot;
    }
}
