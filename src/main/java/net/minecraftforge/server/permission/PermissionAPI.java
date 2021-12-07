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
import net.minecraftforge.server.permission.nodes.PermissionDynamicContext;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public final class PermissionAPI
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static IPermissionHandler activeHandler = new DefaultPermissionHandler();

    public static Collection<PermissionNode<?>> getRegisteredNodes()
    {
        return activeHandler.getRegisteredNodes();
    }

    private PermissionAPI()
    {
    }

    /**
     * @return the Identifier of the currently active permission handler
     */
    public static ResourceLocation getActivePermissionHandler(){
        return activeHandler.getIdentifier();
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
     * <p>Fires the {@link PermissionGatherEvent.Handler}  event,
     * and replaced the current PermissionHandler with the one specified by the user</p>
     */
    public static void gatherPermissionHandler()
    {
        PermissionGatherEvent.Handler event = new PermissionGatherEvent.Handler();
        MinecraftForge.EVENT_BUS.post(event);
        Set<IPermissionHandler> availableHandlers = event.getAvailablePermissionHandlers();

        try
        {
            ResourceLocation selectedPermissionHandler = new ResourceLocation(ForgeConfig.SERVER.permissionHandler.get());

            for(IPermissionHandler handler : availableHandlers)
            {
                if(selectedPermissionHandler.equals(handler.getIdentifier()))
                {
                    LOGGER.info("Replacing permission handler {} with {}", activeHandler.getIdentifier(), selectedPermissionHandler);
                    activeHandler = handler;
                    return;
                }
            }

            LOGGER.error("Unable to find configured permission handler {}, will use {}", selectedPermissionHandler, activeHandler.getIdentifier());
        }
        catch(ResourceLocationException e)
        {
            LOGGER.error("Error parsing config value 'permissionHandler'", e);
        }
    }

    /**
     * <p>Helper method for internal use only!</p>
     * <p>Fires the {@link PermissionGatherEvent.Nodes} event,
     * and registers them to the currently active PermissionHandler</p>
     */
    public static void gatherPermissionNodes()
    {
        PermissionGatherEvent.Nodes event = new PermissionGatherEvent.Nodes();
        MinecraftForge.EVENT_BUS.post(event);

        for (PermissionNode<?> node : event.getNodes())
        {
            activeHandler.registerNode(node);
        }
    }
}
