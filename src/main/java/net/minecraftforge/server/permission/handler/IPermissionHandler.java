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

package net.minecraftforge.server.permission.handler;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.nodes.PermissionDynamicContext;
import net.minecraftforge.server.permission.nodes.PermissionNode;

import java.util.Set;
import java.util.UUID;

/**
 * This is the Heart of the PermissionAPI, it manages {@link PermissionNode}s
 * as well as it handles all permission queries.
 *
 * <p>Note: You do not need to implement a PermissionHandler to query for permissions.</p>
 *
 * @implNote The {@link DefaultPermissionHandler} does forward all permission queries to the PermissionNodes default resolver.
 * @apiNote You can implement your own PermissionHandler using the {@link PermissionGatherEvent.Handler} event.
 */
public interface IPermissionHandler
{
    /**
     * {@return an identifier for the PermissionHandler}
     */
    ResourceLocation getIdentifier();

    /**
     * {@return an unmodifiable view of the collection of registered permission nodes}
     */
    Set<PermissionNode<?>> getRegisteredNodes();

    /**
     * <strong>Mods must use {@link PermissionAPI#getPermission(ServerPlayer, PermissionNode, PermissionDynamicContext[])}</strong>
     *
     * <p>Queries a player's permission for a given node and contexts</p>
     * <p><strong>Warning:</strong> PermissionNodes <strong>must</strong> be registered using the
     * {@link PermissionGatherEvent.Nodes} event before querying.</p>
     *
     * @param player  player for which you want to check permissions
     * @param node    the PermissionNode for which you want to query
     * @param context optional array of PermissionDynamicContext, single entries will be ignored if they weren't
     *                registered to the node
     * @param <T>     type of the queried PermissionNode
     * @return a value of type {@code <T>}, that the combination of Player and PermissionNode map to.
     */
    <T> T getPermission(ServerPlayer player, PermissionNode<T> node, PermissionDynamicContext<?>... context);

    /**
     * See {@link IPermissionHandler#getPermission(ServerPlayer, PermissionNode, PermissionDynamicContext[])}
     *
     * @param player  offline player for which you want to check permissions
     * @param node    the PermissionNode for which you want to query
     * @param context optional array of PermissionDynamicContext, single entries will be ignored if they weren't
     *                registered to the node
     * @param <T>     type of the queried PermissionNode
     * @return a value of type {@code <T>}, that the combination of Player and PermissionNode map to.
     */
    <T> T getOfflinePermission(UUID player, PermissionNode<T> node, PermissionDynamicContext<?>... context);
}
