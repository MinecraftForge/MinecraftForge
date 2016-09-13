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
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

public class PermissionAPI
{
    private static IPermissionHandler permissionHandler = DefaultPermissionHandler.INSTANCE;
    private static final HashMap<String, DefaultPermissionLevel> PERMISSION_LEVEL_MAP = new HashMap<String, DefaultPermissionLevel>();
    private static final HashMap<String, String[]> DESCRIPTION_MAP = new HashMap<String, String[]>();

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
     * @param permission  Permission node, best if it's lowercase and contains '.' (e.g. <code>"modid.subgroup.permission_id"</code>)
     * @param level       Default permission level for this node. If not isn't registred, it's level is going to be 'NONE'
     * @param description Optional description
     */
    public static void registerPermission(String permission, DefaultPermissionLevel level, String... description)
    {
        Preconditions.checkNotNull(permission, "Permission node can't be null!");
        Preconditions.checkNotNull(level, "Permission level can't be null!");

        PERMISSION_LEVEL_MAP.put(permission, level);

        if(description.length > 0)
        {
            DESCRIPTION_MAP.put(permission, description);
        }
    }

    /**
     * @param profile    GameProfile of the player who is requesting permission. The player doesn't have to be online
     * @param permission Permission node. See {@link PermissionAPI#registerPermission(String, DefaultPermissionLevel, String...)}
     * @param context    Context for this permission. Highly recommended to not be null. See {@link IContext}
     * @return true, if player has permission, false if he does not.
     * @see DefaultPermissionHandler
     */
    public static boolean hasPermission(GameProfile profile, String permission, @Nullable IContext context)
    {
        Preconditions.checkNotNull(profile, "GameProfile can't be null!");
        Preconditions.checkNotNull(permission, "Permission node can't be null!");

        if(permission.isEmpty())
        {
            throw new NullPointerException("Permission node can't be empty!");
        }

        return permissionHandler.hasPermission(profile, permission, context);
    }

    /**
     * Shortcut method using EntityPlayer and creating PlayerContext
     *
     * @see PermissionAPI#hasPermission(GameProfile, String, IContext)
     */
    public static boolean hasPermission(EntityPlayer player, String permission)
    {
        return hasPermission(player.getGameProfile(), permission, new PlayerContext(player));
    }

    /**
     * @see PermissionAPI#registerPermission(String, DefaultPermissionLevel, String...)
     */
    public static DefaultPermissionLevel getDefaultPermissionLevel(String permission)
    {
        DefaultPermissionLevel level = PERMISSION_LEVEL_MAP.get(permission);
        return level == null ? DefaultPermissionLevel.NONE : level;
    }

    /**
     * @see PermissionAPI#registerPermission(String, DefaultPermissionLevel, String...)
     */
    public static String[] getPermissionDescription(String permission)
    {
        String[] desc = DESCRIPTION_MAP.get(permission);
        return desc == null ? new String[0] : desc;
    }

    public static Set<String> getRegistredPermissionNodes()
    {
        return Collections.unmodifiableSet(PERMISSION_LEVEL_MAP.keySet());
    }
}