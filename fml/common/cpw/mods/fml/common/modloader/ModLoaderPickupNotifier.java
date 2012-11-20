package cpw.mods.fml.common.modloader;

import net.minecraft.shared.*;
import cpw.mods.fml.common.IPickupNotifier;

public class ModLoaderPickupNotifier implements IPickupNotifier
{

    private BaseModProxy mod;

    public ModLoaderPickupNotifier(BaseModProxy mod)
    {
        this.mod = mod;
    }

    @Override
    public void notifyPickup(EntityItem item, EntityPlayer player)
    {
        mod.onItemPickup(player, item.field_70294_a);
    }

}
