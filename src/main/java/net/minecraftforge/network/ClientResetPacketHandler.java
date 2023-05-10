package net.minecraftforge.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.GameData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class ClientResetPacketHandler {

    private static final Logger LOGGER = LogManager.getLogger();

    static void handleClientReset(HandshakeMessages.S2CReset msg, final Supplier<NetworkEvent.Context> contextSupplier)
    {
        NetworkEvent.Context context = contextSupplier.get();
        Connection connection = context.getNetworkManager();

        if (context.getDirection() != NetworkDirection.LOGIN_TO_CLIENT && context.getDirection() != NetworkDirection.PLAY_TO_CLIENT)
        {
            connection.disconnect(Component.literal("Illegal packet received, terminating connection"));
            throw new IllegalStateException("Invalid packet received, aborting connection");
        }

        LOGGER.debug(HandshakeHandler.FMLHSMARKER, "Received reset from server");

        ServerData serverData = Minecraft.getInstance().getCurrentServer();
        Screen screen = Minecraft.getInstance().screen;

        if (!handleClear(context))
        {
            return;
        }

        NetworkHooks.registerClientLoginChannel(connection);
        connection.setProtocol(ConnectionProtocol.LOGIN);
        connection.setListener(new ClientHandshakePacketListenerImpl(
                connection, Minecraft.getInstance(), serverData, screen, true, null, statusMessage -> {}
        ));

        context.setPacketHandled(true);
        NetworkConstants.handshakeChannel.reply(
                new HandshakeMessages.C2SAcknowledge(),
                new NetworkEvent.Context(connection, NetworkDirection.LOGIN_TO_CLIENT, 98)
        );

        LOGGER.debug(HandshakeHandler.FMLHSMARKER, "Reset complete");
    }

    static boolean handleClear(NetworkEvent.Context context)
    {
        CompletableFuture<Void> future = context.enqueueWork(() ->
        {
            LOGGER.debug(HandshakeHandler.FMLHSMARKER, "Clearing");

            // Clear
            if (Minecraft.getInstance().level == null) {
                // Ensure the GameData is reverted in case the client is reset during the handshake.
                GameData.revertToFrozen();
            }

            // Clear
            Minecraft.getInstance().clearLevel(new GenericDirtMessageScreen(Component.translatable("connect.negotiating")));

            // Clear channel handlers so there isn't an error when they're added back (https://github.com/Just-Chaldea/Forge-Client-Reset-Packet/pull/12)
            try {
                context.getNetworkManager().channel().pipeline().remove("forge:forge_fixes");
            } catch (NoSuchElementException ignored) {
            }
            try {
                context.getNetworkManager().channel().pipeline().remove("forge:vanilla_filter");
            } catch (NoSuchElementException ignored) {
            }

            // Restore
//            Minecraft.getInstance().setCurrentServer(serverData); - This is commented out because I could not find an equivalent method in 1.19.X (from 1.16 & 1.18)
        });

        LOGGER.debug(HandshakeHandler.FMLHSMARKER, "Waiting for clear to complete");

        try
        {
            future.get();
            LOGGER.debug("Clear complete, continuing reset");
            return true;
        } catch (Exception ex)
        {
            LOGGER.error(HandshakeHandler.FMLHSMARKER, "Failed to clear, closing connection", ex);
            context.getNetworkManager().disconnect(Component.literal("Failed to clear, closing connection"));
            return false;
        }
    }
}
