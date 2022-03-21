/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.gametest;

import net.minecraft.server.Main;

public class GameTestMain
{
    public static void main(String[] args)
    {
        System.setProperty("forge.enableGameTest", "true");
        System.setProperty("forge.gameTestServer", "true");
        Main.main(args);
    }
}
