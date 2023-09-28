/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug;

import net.minecraftforge.fml.common.Mod;

/**
 * Test that the mod loading error screen shows the cause of the crash when a mod constructor throws an exception.
 * <p>Enable or disable this test in the mods.toml</p>
 */
@Mod("constructor_throw_test")
public class ConstructorThrowTest {
    public ConstructorThrowTest() {
        throw new RuntimeException("Test");
    }
}
