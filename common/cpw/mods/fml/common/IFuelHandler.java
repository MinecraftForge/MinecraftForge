package cpw.mods.fml.common;

import net.minecraft.shared.ItemStack;

public interface IFuelHandler
{
    int getBurnTime(ItemStack fuel);
}