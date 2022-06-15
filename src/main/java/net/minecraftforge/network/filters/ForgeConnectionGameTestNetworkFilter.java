/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.filters;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.tree.RootCommandNode;
import io.netty.channel.ChannelHandler;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.RegistryAccess;
import net.minecraft.gametest.framework.TestClassNameArgument;
import net.minecraft.gametest.framework.TestFunctionArgument;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraftforge.gametest.ForgeGameTestHooks;
import net.minecraftforge.network.ConnectionData;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

/**
 * Network filter for connections where gametest is enabled on the server but not on the client.
 */
@ChannelHandler.Sharable
public class ForgeConnectionGameTestNetworkFilter extends VanillaPacketFilter
{
    public ForgeConnectionGameTestNetworkFilter()
    {
        super(ImmutableMap.<Class<? extends Packet<?>>, BiConsumer<Packet<?>, List<? super Packet<?>>>>builder()
                .put(handler(ClientboundCommandsPacket.class, ForgeConnectionGameTestNetworkFilter::filterCommandList))
                .build());
    }

    @Override
    protected boolean isNecessary(Connection manager)
    {
        // If the server doesn't have gametest enabled, then we have nothing to worry about
        if (!ForgeGameTestHooks.isGametestEnabled())
            return false;

        ConnectionData connectionData = NetworkHooks.getConnectionData(manager);
        // Can only skip if we know for sure that the client connection has gametest enabled as well
        return connectionData == null || connectionData.isGametestEnabled() != Boolean.TRUE;
    }

    /**
     * Filter for {@link ClientboundCommandsPacket} when gametest is enabled on the server.
     * Uses {@link CommandTreeCleaner} to filter out gametest argument type infos {@link TestFunctionArgument} and {@link TestClassNameArgument}
     * on connections where the client has gametest enabled (or we can't tell if it's enabled and play it safe).
     */
    @NotNull
    private static ClientboundCommandsPacket filterCommandList(ClientboundCommandsPacket packet)
    {
        CommandBuildContext commandBuildContext = new CommandBuildContext(RegistryAccess.BUILTIN.get());
        commandBuildContext.missingTagAccessPolicy(CommandBuildContext.MissingTagAccessPolicy.RETURN_EMPTY);
        RootCommandNode<SharedSuggestionProvider> root = packet.getRoot(commandBuildContext);
        RootCommandNode<SharedSuggestionProvider> newRoot = CommandTreeCleaner.cleanArgumentTypes(root, argType ->
                !(argType instanceof TestFunctionArgument) && !(argType instanceof TestClassNameArgument));
        return new ClientboundCommandsPacket(newRoot);
    }
}
