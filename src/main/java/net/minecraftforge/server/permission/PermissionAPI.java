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

import com.google.common.base.Preconditions;
import com.mojang.authlib.GameProfile;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.server.permission.context.IContext;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;

public class PermissionAPI
{
    private static IPermissionHandler permissionHandler = DefaultPermissionHandler.INSTANCE;

    public static void setPermissionHandler(@Nonnull IPermissionHandler handler)
    {
        Preconditions.checkNotNull(handler, "IPermissionHandler can't be null!");
        FMLLog.log(Level.WARN, "Replacing " + permissionHandler.getClass().getName() + " with " + handler.getClass().getName());
        permissionHandler = handler;
    }

    /**
     * @param profile          GameProfile of the player who is requesting permission. The player doesn't have to be online
     * @param permission       Permission node, best if it's lowercase and contains '.' (e.g. <code>"modid.subgroup.permission_id"</code>)
     * @param defaultForPlayer Default value for players. OPs should always have all permissions
     * @param context          Context for this permission. Use <tt>PlayerContext</tt> or <tt>PlayerBlockContext</tt> when possible. When there is no context available, use <tt>EmptyContext.INSTANCE</tt> (not recommended)
     * @return true, if player has permission, false if he does not.
     * @see DefaultPermissionHandler
     */
    public static boolean hasPermission(@Nonnull GameProfile profile, @Nonnull String permission, boolean defaultForPlayer, @Nonnull IContext context)
    {
        Preconditions.checkNotNull(profile, "GameProfile can't be null!");
        Preconditions.checkNotNull(permission, "Permission node can't be null!");
        Preconditions.checkNotNull(context, "Context can't be null!");

        if(permission.isEmpty())
        {
            throw new NullPointerException("Permission node can't be empty!");
        }

        return permissionHandler.hasPermission(profile, permission, defaultForPlayer, context);
    }
}