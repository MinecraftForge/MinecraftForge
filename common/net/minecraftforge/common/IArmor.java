package net.minecraftforge.common;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IArmor
{
    public int getDamageReduceAmount(ItemStack stack);
}
