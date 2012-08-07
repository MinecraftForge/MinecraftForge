package cpw.mods.fml.common.modloader;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import cpw.mods.fml.common.ICraftingHandler;

public class ModLoaderCraftingHelper implements ICraftingHandler
{

    private BaseModProxy mod;

    public ModLoaderCraftingHelper(BaseModProxy mod)
    {
        this.mod = mod;
    }

    @Override
    public void onCrafting(EntityPlayer player, ItemStack item, IInventory craftMatrix)
    {
        mod.takenFromCrafting(player, item, craftMatrix);
    }

    @Override
    public void onSmelting(EntityPlayer player, ItemStack item)
    {
        mod.takenFromFurnace(player, item);
    }

}
