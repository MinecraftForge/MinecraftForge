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
import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class PermissionAPI
{
    private static IPermissionHandler permissionHandler = DefaultPermissionHandler.INSTANCE;

    public static void setPermissionHandler(IPermissionHandler handler)
    {
        FMLLog.log(Level.INFO, "Replacing " + permissionHandler.getClass().getName() + " with " + handler.getClass().getName());
        permissionHandler = handler;
    }

    /**
     * @param profile          GameProfile of the player who is requesting permission. The player doesn't have to be online
     * @param permission       Permission node, best if lowercase and contains '.' (example: "modid.subgroup.permission_id")
     * @param defaultForPlayer Default value for players. OPs should always have all permissions
     * @param context          Context for this permission. When there is no context available, use Context.EMPTY (not recommended)
     * @return true, if player has permission, false if he does not. Default implementation returns true if it is a singleplayer world or the player is an OP
     */
    public static boolean hasPermission(GameProfile profile, String permission, boolean defaultForPlayer, Context context)
    {
        if(permission.isEmpty())
        {
            throw new NullPointerException("Permission string can't be empty!");
        }

        return permissionHandler.hasPermission(profile, permission, defaultForPlayer, context);
    }
}