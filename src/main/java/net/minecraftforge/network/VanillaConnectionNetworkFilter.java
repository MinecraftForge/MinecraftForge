/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nonnull;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
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
public class VanillaConnectionNetworkFilter extends MessageToMessageEncoder<IPacket<?>>
{

    private static final Logger LOGGER = LogManager.getLogger();

    private static final Map<Class<? extends IPacket<?>>, Function<IPacket<?>, ? extends IPacket<?>>> handlers = ImmutableMap.<Class<? extends IPacket<?>>, Function<IPacket<?>, ? extends IPacket<?>>>builder()
            .put(handler(SEntityPropertiesPacket.class, VanillaConnectionNetworkFilter::filterEntityProperties))
            .put(handler(SCommandListPacket.class, VanillaConnectionNetworkFilter::filterCommandList))
            .build();


    public static void injectIfNecessary(NetworkManager manager)
    {
        if (NetworkHooks.isVanillaConnection(manager))
        {
            manager.channel().pipeline().addBefore("packet_handler", "forge:vanilla_filter", new VanillaConnectionNetworkFilter());
            LOGGER.debug("Injected into {}", manager);
        }
    }

    /**
     * Helper function for building the handler map.
     */
    @Nonnull
    private static <T extends IPacket<?>> Map.Entry<Class<? extends IPacket<?>>, Function<IPacket<?>, ? extends IPacket<?>>> handler(Class<T> cls, Function<T, ? extends IPacket<?>> function)
    {
        return new AbstractMap.SimpleEntry<>(cls, function.compose(cls::cast));
    }

    /**
     * Filter for SEntityPropertiesPacket. Filters out any entity attributes that are not in the "minecraft" namespace.
     * A vanilla client would ignore these with an error log.
     */
    @Nonnull
    private static SEntityPropertiesPacket filterEntityProperties(SEntityPropertiesPacket msg)
    {
        SEntityPropertiesPacket newPacket = new SEntityPropertiesPacket();
        msg.getSnapshots().stream()
                .filter(snapshot -> {
                    ResourceLocation key = ForgeRegistries.ATTRIBUTES.getKey(snapshot.func_240834_a_());
                    return key != null && key.getNamespace().equals("minecraft");
                })
                .forEach(snapshot -> newPacket.getSnapshots().add(snapshot));
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

    @Override
    protected void encode(ChannelHandlerContext ctx, IPacket<?> msg, List<Object> out)
    {
        Function<IPacket<?>, ? extends IPacket<?>> function = handlers.getOrDefault(msg.getClass(), Function.identity());
        out.add(function.apply(msg));
    }
}
