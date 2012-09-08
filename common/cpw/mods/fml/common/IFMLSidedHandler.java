package cpw.mods.fml.common;

import java.util.List;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.Entity;
import net.minecraft.src.NetHandler;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet131MapData;
import cpw.mods.fml.common.network.EntitySpawnAdjustmentPacket;
import cpw.mods.fml.common.network.EntitySpawnPacket;
import cpw.mods.fml.common.network.ModMissingPacket;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;

public interface IFMLSidedHandler
{
    List<String> getAdditionalBrandingInformation();

    Side getSide();

    void haltGame(String message, Throwable exception);

    void showGuiScreen(Object clientGuiElement);

    Entity spawnEntityIntoClientWorld(EntityRegistration registration, EntitySpawnPacket packet);

    void adjustEntityLocationOnClient(EntitySpawnAdjustmentPacket entitySpawnAdjustmentPacket);

    void beginServerLoading(MinecraftServer server);

    void finishServerLoading();

    MinecraftServer getServer();

    void sendPacket(Packet packet);

    void displayMissingMods(ModMissingPacket modMissingPacket);

    void handleTinyPacket(NetHandler handler, Packet131MapData mapData);

    void setClientCompatibilityLevel(byte compatibilityLevel);

    byte getClientCompatibilityLevel();
}
