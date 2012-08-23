package cpw.mods.fml.common.modloader;

import cpw.mods.fml.common.network.EntitySpawnPacket;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;

public interface IModLoaderSidedHelper
{

    void finishModLoading(ModLoaderModContainer mc);

    Object getClientGui(BaseModProxy mod, EntityPlayer player, int iD, int x, int y, int z);

    Entity spawnEntity(BaseModProxy mod, EntitySpawnPacket input, EntityRegistration registration);

}
