package cpw.mods.fml.common;

import net.minecraft.src.ItemStack;

public interface IFuelHandler
{
    int getBurnTime(ItemStack fuel);
}