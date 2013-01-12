package cpw.mods.fml.common.modloader;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
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
        mod.onItemPickup(player, item.func_92059_d());
    }

}
