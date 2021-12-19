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

package net.minecraftforge.server.permission;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.exceptions.UnregisteredPermissionException;
import net.minecraftforge.server.permission.handler.DefaultPermissionHandler;
import net.minecraftforge.server.permission.handler.IPermissionHandler;
import net.minecraftforge.server.permission.handler.IPermissionHandlerFactory;
import net.minecraftforge.server.permission.nodes.PermissionDynamicContext;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;

public final class PermissionAPI
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Collection<PermissionNode<?>> EMPTY_PERMISSIONS = Collections.EMPTY_LIST;
    private static IPermissionHandler activeHandler = null;

    public static Collection<PermissionNode<?>> getRegisteredNodes()
    {
        return activeHandler == null ? EMPTY_PERMISSIONS : activeHandler.getRegisteredNodes();
    }

    private PermissionAPI()
    {
    }

    /**
     * @return the Identifier of the currently active permission handler
     */
    @Nullable
    public static ResourceLocation getActivePermissionHandler()
    {
        return activeHandler == null ? null : activeHandler.getIdentifier();
    }

    /**
     * <p>Queries a player's permission for a given node and contexts</p>
     * <p><strong>Warning:</strong> PermissionNodes <strong>must</strong> be registered using the
     * {@link PermissionGatherEvent.Nodes} event before querying.</p>
     *
     * @param player  player for which you want to check permissions
     * @param node    the PermissionNode for which you want to query
     * @param context optional array of PermissionDynamicContext, single entries will be ignored if they weren't
     *                registered to the node
     * @param <T>     type of the queried PermissionNode
     * @return a value of type {@code <T>}, that the combination of Player and PermissionNode map to, defaults to the
     * PermissionNodes default handler.
     * @throws UnregisteredPermissionException when the PermissionNode wasn't registered properly
     */
    public static <T> T getPermission(ServerPlayer player, PermissionNode<T> node, PermissionDynamicContext<? extends StringRepresentable>... context)
    {
        if (!activeHandler.getRegisteredNodes().contains(node)) throw new UnregisteredPermissionException(node);
        return activeHandler.getPermission(player, node, context);
    }

    /**
     * See {@link PermissionAPI#getPermission(ServerPlayer, PermissionNode, PermissionDynamicContext[])}
     *
     * @param player  offline player for which you want to check permissions
     * @param node    the PermissionNode for which you want to query
     * @param context optional array of PermissionDynamicContext, single entries will be ignored if they weren't
     *                registered to the node
     * @param <T>     type of the queried PermissionNode
     * @return a value of type {@code <T>}, that the combination of Player and PermissionNode map to, defaults to the
     * PermissionNodes default handler.
     * @throws UnregisteredPermissionException when the PermissionNode wasn't registered properly
     */
    public static <T> T getOfflinePermission(UUID player, PermissionNode<T> node, PermissionDynamicContext<? extends StringRepresentable>... context)
    {
        if (!activeHandler.getRegisteredNodes().contains(node)) throw new UnregisteredPermissionException(node);
        return activeHandler.getOfflinePermission(player, node, context);
    }


    /**
     * <p>Helper method for internal use only!</p>
     * <p>Initializes the active permission handler based on the users config.</p>
     */
    public static void initializePermissionAPI()
    {
        if (PermissionAPI.activeHandler != null) throw new IllegalStateException("Tried to initialize PermissionAPI multiple times!");

        PermissionGatherEvent.Handler handlerEvent = new PermissionGatherEvent.Handler();
        MinecraftForge.EVENT_BUS.post(handlerEvent);
        Map<ResourceLocation, IPermissionHandlerFactory> availableHandlers = handlerEvent.getAvailablePermissionHandlerFactories();

        try
        {
            ResourceLocation selectedPermissionHandler = new ResourceLocation(ForgeConfig.SERVER.permissionHandler.get());
            if (!availableHandlers.containsKey(selectedPermissionHandler))
            {
                LOGGER.error("Unable to find configured permission handler {}, will use {}", selectedPermissionHandler, DefaultPermissionHandler.IDENTIFIER);
                selectedPermissionHandler = DefaultPermissionHandler.IDENTIFIER;
            }

            IPermissionHandlerFactory factory = availableHandlers.get(selectedPermissionHandler);

            PermissionGatherEvent.Nodes nodesEvent = new PermissionGatherEvent.Nodes();
            MinecraftForge.EVENT_BUS.post(nodesEvent);

            PermissionAPI.activeHandler = factory.create(nodesEvent.getNodes());

            if(!selectedPermissionHandler.equals(activeHandler.getIdentifier()))
                LOGGER.warn("Identifier for permission handler {} does not match registered one {}", activeHandler.getIdentifier(), selectedPermissionHandler);

            LOGGER.info("Successfully initialized permission handler {}", PermissionAPI.activeHandler.getIdentifier());
        }
        catch (ResourceLocationException e)
        {
            LOGGER.error("Error parsing config value 'permissionHandler'", e);
        }
    }

    /**
     * <p>Helper method for internal use only!</p>
     * <p>Resets the active permission handler.</p>
     */
    public static void resetPermissionAPI()
    {
        PermissionAPI.activeHandler = null;
        LOGGER.info("Reset PermissionAPI");
    }
}
