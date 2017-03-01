package net.minecraftforge.fml.common.network;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;
import java.util.List;

public interface IOutboundTarget {
    boolean isSideAllowed(Side side);

    void validateArgs(Object args);

    @Nullable
    List<NetworkDispatcher> selectNetworks(Object args, @Nullable EntityPlayer except, ChannelHandlerContext context, FMLProxyPacket packet);
}
