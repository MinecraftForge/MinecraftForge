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
import net.minecraftforge.server.permission.nodes.PermissionDynamicContext;
import net.minecraftforge.server.permission.nodes.PermissionNode;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class DefaultPermissionHandler implements IPermissionHandler
{
    private final ResourceLocation name = new ResourceLocation("forge", "default_handler");;
    private final Set<PermissionNode<?>> registeredNodes = new HashSet<>();
    private Set<PermissionNode<?>> immutableRegisteredNodes = Collections.unmodifiableSet(this.registeredNodes);

    public DefaultPermissionHandler()
    {
    }

    @Override
    public ResourceLocation getIdentifier()
    {
        return this.name;
    }

    @Override
    public void registerNode(PermissionNode<?> node)
    {
        this.registeredNodes.add(node);
    }

    @Override
    public Set<PermissionNode<?>> getRegisteredNodes()
    {
        return immutableRegisteredNodes;
    }

    @Override
    public <T> T getPermission(ServerPlayer player, PermissionNode<T> node, PermissionDynamicContext<?>... context)
    {
        return node.getDefaultResolver().resolve(player, player.getUUID(), context);
    }

    @Override
    public <T> T getOfflinePermission(UUID player, PermissionNode<T> node, PermissionDynamicContext<?>... context)
    {
        return node.getDefaultResolver().resolve(null, player, context);
    }
}
