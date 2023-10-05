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
import net.minecraft.network.protocol.PacketFlow;
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
        var cookie = CommonListenerCookie.createInitial(new GameProfile(UUID.randomUUID(), "test-mock-player"));
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
        channel.attr(Connection.ATTRIBUTE_SERVERBOUND_PROTOCOL).set(ConnectionProtocol.PLAY.codec(PacketFlow.SERVERBOUND));
        // This sets the connection/listener in the player
        new ServerGamePacketListenerImpl(level.getServer(), connection, player, cookie);
        return player;
    }
}
