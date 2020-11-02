package net.minecraftforge.network;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SEntityPropertiesPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VanillaConnectionNetworkFilter extends MessageToMessageEncoder<IPacket<?>>
{

    private static final Logger LOGGER = LogManager.getLogger();

    public static void injectIfNecessary(NetworkManager manager)
    {
        if (NetworkHooks.isVanillaConnection(manager))
        {
            manager.channel().pipeline().addBefore("packet_handler", "forge:vanilla_filter", new VanillaConnectionNetworkFilter());
            LOGGER.debug("Injected into {}", manager);
        }
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, IPacket<?> msg, List<Object> out)
    {
        IPacket<?> filtered;
        if (msg instanceof SEntityPropertiesPacket)
        {
            SEntityPropertiesPacket newPacket = new SEntityPropertiesPacket();
            ((SEntityPropertiesPacket)msg).getSnapshots().stream()
                    .filter(snapshot -> {
                        ResourceLocation key = ForgeRegistries.ATTRIBUTES.getKey(snapshot.func_240834_a_());
                        return key != null && key.getNamespace().equals("minecraft");
                    })
                    .forEach(snapshot -> newPacket.getSnapshots().add(snapshot));
            filtered = newPacket;
        }
        else
        {
            filtered = msg;
        }

        out.add(filtered);
    }
}
