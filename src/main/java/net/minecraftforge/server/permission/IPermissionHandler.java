/*
 * Minecraft Forge
 * Copyright (c) 2016.
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
     * @param node  Permission node, best if it's lowercase and contains '.' (e.g. <code>"modid.subgroup.permission_id"</code>)
     * @param level Default permission level for this node. If not isn't registered, it's level is going to be 'NONE'
     * @param desc  Optional description of the node
     */
    String registerNode(String node, DefaultPermissionLevel level, String desc);

    /**
     * @return Immutable collection of all registered nodes
     */
    Collection<String> getRegisteredNodes();

    /**
     * @param profile GameProfile of the player who is requesting permission. The player doesn't have to be online
     * @param node    Permission node. See {@link #registerNode(String, DefaultPermissionLevel, String)}
     * @param context Context for this permission. Highly recommended to not be null. See {@link IContext}
     * @return true, if player has permission, false if he does not.
     * @see DefaultPermissionHandler
     */
    boolean hasPermission(GameProfile profile, String node, @Nullable IContext context);

    /**
     * @param node Permission node
     * @return Description of the node
     * @see #registerNode(String, DefaultPermissionLevel, String)
     */
    String getNodeDescription(String node);
}