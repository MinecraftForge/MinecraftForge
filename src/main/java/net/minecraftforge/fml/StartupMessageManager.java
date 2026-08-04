/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

public class StartupMessageManager {
    public static void addModMessage(final String message) {
        net.minecraftforge.fml.loading.progress.StartupMessageManager.addModMessage(message);
    }
}
