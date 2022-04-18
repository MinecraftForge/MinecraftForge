/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

import java.util.Optional;
import java.util.function.Consumer;

public class StartupMessageManager {
    public static void addModMessage(final String message) {
        net.minecraftforge.fml.loading.progress.StartupMessageManager.addModMessage(message);
    }

    public static Optional<Consumer<String>> modLoaderConsumer() {
        return net.minecraftforge.fml.loading.progress.StartupMessageManager.modLoaderConsumer();
    }

    public static Optional<Consumer<String>> mcLoaderConsumer() {
        return net.minecraftforge.fml.loading.progress.StartupMessageManager.mcLoaderConsumer();
    }
}
