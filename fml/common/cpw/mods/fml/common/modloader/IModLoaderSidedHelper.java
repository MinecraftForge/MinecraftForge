package cpw.mods.fml.common.modloader;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.EntitySpawnPacket;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;

public interface IModLoaderSidedHelper
{

    void finishModLoading(ModLoaderModContainer mc);

    Object getClientGui(BaseModProxy mod, EntityPlayer player, int iD, int x, int y, int z);

    Entity spawnEntity(BaseModProxy mod, EntitySpawnPacket input, EntityRegistration registration);

    void sendClientPacket(BaseModProxy mod, Packet250CustomPayload packet);

    void clientConnectionOpened(NetHandler netClientHandler, INetworkManager manager, BaseModProxy mod);

    boolean clientConnectionClosed(INetworkManager manager, BaseModProxy mod);

}
