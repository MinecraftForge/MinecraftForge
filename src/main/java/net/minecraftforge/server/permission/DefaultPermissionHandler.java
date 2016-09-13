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

/**
 * Default implementation of PermissionAPI.
 * If the node isn't registred, handler assumes that it's level is OP
 * Returns returns true if it is a singleplayer world or the player is an OP
 *
 * @see PermissionAPI
 */
public enum DefaultPermissionHandler implements IPermissionHandler
{
    INSTANCE;

    @Override
    public boolean hasPermission(GameProfile profile, String permission, IContext context)
    {
        PermissionLevel nodeLevel = PermissionAPI.getPermissionLevel(permission);
        return nodeLevel.isPlayer() || PermissionAPI.getPlayerLevel(profile).isOP();
    }
}