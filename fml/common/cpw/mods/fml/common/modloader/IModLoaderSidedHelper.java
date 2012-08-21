package cpw.mods.fml.common.modloader;

import net.minecraft.src.EntityPlayer;

public interface IModLoaderSidedHelper
{

    void finishModLoading(ModLoaderModContainer mc);

    Object getClientGui(BaseModProxy mod, EntityPlayer player, int iD, int x, int y, int z);

}
