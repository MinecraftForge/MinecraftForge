/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.server.permission;

/**
 * <table><thead><tr><th>Level</th><th>Player</th><th>OP</th></tr>
 * </thead><tbody>
 * <tr><td>ALL</td><td>true</td><td>true</td></tr>
 * <tr><td>OP</td><td>false</td><td>true</td></tr>
 * <tr><td>NONE</td><td>false</td><td>false</td></tr>
 * </tbody></table>
 */
public enum DefaultPermissionLevel
{
    ALL,
    OP,
    NONE
}
