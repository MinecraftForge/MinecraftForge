/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
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
