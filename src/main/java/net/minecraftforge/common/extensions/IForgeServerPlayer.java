/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import java.util.function.Consumer;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.NetworkInitialization;
import net.minecraftforge.network.packets.OpenContainer;

public interface IForgeServerPlayer {
    private ServerPlayer self() {
        return (ServerPlayer)this;
    }

    /**
     * Request to open a GUI on the client, from the server
     *
     * Refer to {@link ConfigScreenHandler.ConfigScreenFactory} for how to provide a function to consume
     * these GUI requests on the client.
     *
     * @param player The player to open the GUI for
     * @param containerSupplier A supplier of container properties including the registry name of the container
     * @param pos A block pos, which will be encoded into the auxillary data for this request
     */
    default void openMenu(MenuProvider containerSupplier, BlockPos pos) {
        openMenu(containerSupplier, buf -> buf.writeBlockPos(pos));
    }

    /**
     * Request to open a GUI on the client, from the server
     *
     * Refer to {@link ConfigScreenHandler.ConfigScreenFactory} for how to provide a function to consume
     * these GUI requests on the client.
     *
     * The maximum size for #extraDataWriter is 32600 bytes.
     *
     * @param player The player to open the GUI for
     * @param containerSupplier A supplier of container properties including the registry name of the container
     * @param extraDataWriter Consumer to write any additional data the GUI needs
     */
    @SuppressWarnings("resource")
    default void openMenu(MenuProvider containerSupplier, Consumer<FriendlyByteBuf> extraDataWriter) {
        var player = self();
        if (player.level().isClientSide) return;
        player.doCloseContainer();
        player.nextContainerCounter();
        int openContainerId = player.containerCounter;
        FriendlyByteBuf extraData = new FriendlyByteBuf(Unpooled.buffer());
        extraDataWriter.accept(extraData);
        extraData.readerIndex(0); // reset to beginning in case modders read for whatever reason

        FriendlyByteBuf output = new FriendlyByteBuf(Unpooled.buffer());
        output.writeVarInt(extraData.readableBytes());
        output.writeBytes(extraData);

        if (output.readableBytes() > 32600 || output.readableBytes() < 1)
            throw new IllegalArgumentException("Invalid PacketBuffer for openGui, found "+ output.readableBytes()+ " bytes");
        var c = containerSupplier.createMenu(openContainerId, player.getInventory(), player);
        if (c == null)
            return;
        var msg = new OpenContainer(c.getType(), openContainerId, containerSupplier.getDisplayName(), output);
        NetworkInitialization.PLAY.send(msg, player.connection.getConnection());

        player.containerMenu = c;
        player.initMenu(player.containerMenu);
        ForgeEventFactory.onPlayerOpenContainer(player, c);
    }

}
