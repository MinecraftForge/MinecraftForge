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

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.context.IContext;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 * Default implementation of PermissionAPI.
 * {@link #hasPermission(GameProfile, String, IContext)} is based on DefaultPermissionLevel
 *
 * @see IPermissionHandler
 */
public enum DefaultPermissionHandler implements IPermissionHandler
{
    INSTANCE;
    private static final HashMap<String, DefaultPermissionLevel> PERMISSION_LEVEL_MAP = new HashMap<String, DefaultPermissionLevel>();
    private static final HashMap<String, String> DESCRIPTION_MAP = new HashMap<String, String>();

    @Override
    public void registerNode(String node, DefaultPermissionLevel level, String desc)
    {
        PERMISSION_LEVEL_MAP.put(node, level);

        if(!desc.isEmpty())
        {
            DESCRIPTION_MAP.put(node, desc);
        }
    }

    @Override
    public Collection<String> getRegisteredNodes()
    {
        return Collections.unmodifiableSet(PERMISSION_LEVEL_MAP.keySet());
    }

    @Override
    public boolean hasPermission(GameProfile profile, String node, @Nullable IContext context)
    {
        DefaultPermissionLevel level = getDefaultPermissionLevel(node);

        if(level == DefaultPermissionLevel.NONE)
        {
            return false;
        }
        else if(level == DefaultPermissionLevel.ALL)
        {
            return true;
        }

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server != null && server.getPlayerList().isOp(profile);
    }

    @Override
    public String getNodeDescription(String node)
    {
        String desc = DESCRIPTION_MAP.get(node);
        return desc == null ? "" : desc;
    }

    /**
     * @return The default permission level of a node. If the permission isn't registred, it will return NONE
     */
    public DefaultPermissionLevel getDefaultPermissionLevel(String node)
    {
        DefaultPermissionLevel level = PERMISSION_LEVEL_MAP.get(node);
        return level == null ? DefaultPermissionLevel.NONE : level;
    }
}
