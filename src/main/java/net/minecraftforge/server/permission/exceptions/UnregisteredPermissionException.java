/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.server.permission.exceptions;

import net.minecraftforge.server.permission.nodes.PermissionNode;

import java.util.Locale;

public class UnregisteredPermissionException extends RuntimeException
{
    private PermissionNode<?> node;

    public UnregisteredPermissionException(PermissionNode<?> node)
    {
        super(String.format(Locale.ENGLISH, "Tried to query PermissionNode '%s' although it has not been Registered", node.getNodeName()));
        this.node = node;
    }

    public PermissionNode<?> getNode()
    {
        return node;
    }
}
