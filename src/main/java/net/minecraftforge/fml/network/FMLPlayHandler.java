package net.minecraftforge.fml.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class FMLPlayHandler
{
    public static final SimpleChannel channel = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation("fml", "play"))
            .clientAcceptedVersions(a -> true)
            .serverAcceptedVersions(a -> true)
            .networkProtocolVersion(() -> NetworkHooks.NETVERSION)
            .simpleChannel();
    static
    {
        channel.registerMessage(0, FMLPlayMessages.SpawnEntity.class, FMLPlayMessages.SpawnEntity::encode, FMLPlayMessages.SpawnEntity::decode, FMLPlayMessages.SpawnEntity::handle);
    }
}
