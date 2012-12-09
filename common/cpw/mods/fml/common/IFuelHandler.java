package cpw.mods.fml.common;

import net.minecraft.item.ItemStack;

public interface IFuelHandler
{
    int getBurnTime(ItemStack fuel);
}