/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.network;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import javax.annotation.Nonnull;

import io.netty.channel.ChannelHandler;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SCommandListPacket;
import net.minecraft.network.play.server.SEntityPropertiesPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.tree.RootCommandNode;

/**
 * A filter for network packets, used to filter/modify parts of vanilla network messages that
 * will cause errors or warnings on vanilla clients, for example entity attributes that are added by Forge or mods.
 */
@ChannelHandler.Sharable
public class VanillaConnectionNetworkFilter extends VanillaPacketFilter
{

    private static final Logger LOGGER = LogManager.getLogger();

    public VanillaConnectionNetworkFilter()
    {
        super(
                ImmutableMap.<Class<? extends IPacket<?>>, BiConsumer<IPacket<?>, List<? super IPacket<?>>>>builder()
                .put(handler(SEntityPropertiesPacket.class, VanillaConnectionNetworkFilter::filterEntityProperties))
                .put(handler(SCommandListPacket.class, VanillaConnectionNetworkFilter::filterCommandList))
                .build()
        );
    }

    @Override
    protected boolean isNecessary(NetworkManager manager)
    {
        return NetworkHooks.isVanillaConnection(manager);
    }

    /**
     * Filter for SEntityPropertiesPacket. Filters out any entity attributes that are not in the "minecraft" namespace.
     * A vanilla client would ignore these with an error log.
     */
    @Nonnull
    private static SEntityPropertiesPacket filterEntityProperties(SEntityPropertiesPacket msg)
    {
        SEntityPropertiesPacket newPacket = new SEntityPropertiesPacket(msg.getEntityId(), Collections.emptyList());
        msg.getValues().stream()
                .filter(snapshot -> {
                    ResourceLocation key = ForgeRegistries.ATTRIBUTES.getKey(snapshot.getAttribute());
                    return key != null && key.getNamespace().equals("minecraft");
                })
                .forEach(snapshot -> newPacket.getValues().add(snapshot));
        return newPacket;
    }

    /**
     * Filter for SCommandListPacket. Uses {@link CommandTreeCleaner} to filter out any ArgumentTypes that are not in the "minecraft" or "brigadier" namespace.
     * A vanilla client would fail to deserialize the packet and disconnect with an error message if these were sent.
     */
    @Nonnull
    private static SCommandListPacket filterCommandList(SCommandListPacket packet)
    {
        RootCommandNode<ISuggestionProvider> root = packet.getRoot();
        RootCommandNode<ISuggestionProvider> newRoot = CommandTreeCleaner.cleanArgumentTypes(root, argType -> {
            ResourceLocation id = ArgumentTypes.getId(argType);
            return id != null && (id.getNamespace().equals("minecraft") || id.getNamespace().equals("brigadier"));
        });
        return new SCommandListPacket(newRoot);
    }
}
