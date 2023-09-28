/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.world;

import net.minecraftforge.fml.common.Mod;

/**
 * Used to test that modded dimension types and dimensions are loaded on
 * a clean dedicated server launch.
 *
 * To test, start a clean dedicated server (no level.dat), and validate that the dimension
 * `server_world_creation_test:server_world_creation_test` exists.
 * This can be done with the `execute in` command. For example:
 * `execute in server_world_creation_test:server_world_creation_test run say working correctly`
 * will say `working correctly` in the chat if successful.
 */
@Mod(ServerWorldCreationTest.MODID)
public class ServerWorldCreationTest
{
    static final String MODID = "server_world_creation_test";
    public ServerWorldCreationTest()
    {
    }
}

