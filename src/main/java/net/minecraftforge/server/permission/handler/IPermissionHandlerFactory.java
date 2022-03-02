/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.server.permission.handler;

import net.minecraftforge.server.permission.nodes.PermissionNode;

import java.util.Collection;

@FunctionalInterface
public interface IPermissionHandlerFactory
{
    IPermissionHandler create(Collection<PermissionNode<?>> permissions);
}
