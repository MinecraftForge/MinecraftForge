package cpw.mods.fml.common.modloader;

import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.IFuelHandler;

public class ModLoaderFuelHelper implements IFuelHandler
{

    private BaseModProxy mod;

    public ModLoaderFuelHelper(BaseModProxy mod)
    {
        this.mod = mod;
    }

    @Override
    public int getBurnTime(ItemStack fuel)
    {
        return mod.addFuel(fuel.field_77993_c, fuel.field_77993_c);
    }

}
