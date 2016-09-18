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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.server.permission.context.IContext;
import net.minecraftforge.server.permission.context.PlayerContext;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;

public class PermissionAPI
{
    private static IPermissionHandler permissionHandler = DefaultPermissionHandler.INSTANCE;

    public static void setPermissionHandler(IPermissionHandler handler)
    {
        Preconditions.checkNotNull(handler, "Permission handler can't be null!");
        FMLLog.log(Level.WARN, "Replacing " + permissionHandler.getClass().getName() + " with " + handler.getClass().getName());
        permissionHandler = handler;
    }

    public static IPermissionHandler getPermissionHandler()
    {
        return permissionHandler;
    }

    /**
     * @see IPermissionHandler#registerNode(String, DefaultPermissionLevel, String)
     */
    public static String registerNode(String node, DefaultPermissionLevel level, String desc)
    {
        return permissionHandler.registerNode(node, level, desc);
    }

    /**
     * @see IPermissionHandler#hasPermission(GameProfile, String, IContext)
     */
    public static boolean hasPermission(GameProfile profile, String node, @Nullable IContext context)
    {
        return permissionHandler.hasPermission(profile, node, context);
    }

    /**
     * Shortcut method using EntityPlayer and creating PlayerContext
     *
     * @see PermissionAPI#hasPermission(GameProfile, String, IContext)
     */
    public static boolean hasPermission(EntityPlayer player, String node)
    {
        return hasPermission(player.getGameProfile(), node, new PlayerContext(player));
    }
}