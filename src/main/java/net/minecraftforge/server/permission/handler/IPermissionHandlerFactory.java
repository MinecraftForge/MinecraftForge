/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.server.permission.handler;

import net.minecraftsingularity.server.permission.nodes.PermissionNode;

import java.util.Collection;

@FunctionalInterface
public interface IPermissionHandlerFactory
{
    IPermissionHandler create(Collection<PermissionNode<?>> permissions);
}
