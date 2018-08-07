/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

import com.mojang.authlib.GameProfile;
import net.minecraftforge.server.permission.context.IContext;

import javax.annotation.Nullable;
import java.util.Collection;

public interface IPermissionHandler
{
    /**
     * Use {@link PermissionAPI#registerNode(String, DefaultPermissionLevel, String)}
     */
    void registerNode(String node, DefaultPermissionLevel level, String desc);

    /**
     * @return Immutable collection of all registered nodes
     */
    Collection<String> getRegisteredNodes();

    /**
     * Use {@link PermissionAPI#hasPermission(GameProfile, String, IContext)}
     */
    boolean hasPermission(GameProfile profile, String node, @Nullable IContext context);

    /**
     * @param node Permission node
     * @return Description of the node. "" in case this node doesn't have a decription
     * @see #registerNode(String, DefaultPermissionLevel, String)
     */
    String getNodeDescription(String node);
}