/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import java.util.UUID;
import java.util.function.Supplier;

import com.mojang.authlib.GameProfile;

import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.GameProtocols;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public interface IForgeGameTestHelper {
    private GameTestHelper self() {
        return (GameTestHelper)this;
    }

    default void assertTrue(boolean value, Supplier<String> message) {
       if (!value)
          throw new GameTestAssertException(message.get());
    }

    default void assertFalse(boolean value, Supplier<String> message) {
       if (value)
          throw new GameTestAssertException(message.get());
    }

    default ServerPlayer makeMockServerPlayer() {
        var level = self().getLevel();
        var cookie = CommonListenerCookie.createInitial(new GameProfile(UUID.randomUUID(), "test-mock-player"), false);
        var player = new ServerPlayer(level.getServer(), level, cookie.gameProfile(), cookie.clientInformation()) {
           public boolean isSpectator() {
              return false;
           }

           public boolean isCreative() {
              return true;
           }
        };
        var connection = new Connection(PacketFlow.SERVERBOUND);
        var channel = new EmbeddedChannel(connection);
        var server = level.getServer();

        var listener = new ServerGamePacketListenerImpl(server, connection, player, cookie);
        var info = GameProtocols.SERVERBOUND_TEMPLATE.bind(RegistryFriendlyByteBuf.decorator(server.registryAccess()));
        connection.setupInboundProtocol(info, listener);
        return player;
    }
}
