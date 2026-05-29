/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml;

import net.minecraftsingularity.fml.loading.progress.ProgressMeter;
import net.minecraftsingularity.fml.loading.progress.StartupNotificationManager;

import java.util.Optional;
import java.util.function.Consumer;

public class StartupMessageManager {
    public static void addModMessage(final String message) {
        StartupNotificationManager.addModMessage(message);
    }

    public static Optional<Consumer<String>> modLoaderConsumer() {
        return StartupNotificationManager.modLoaderConsumer();
    }

    public static Optional<Consumer<String>> mcLoaderConsumer() {
        return StartupNotificationManager.mcLoaderConsumer();
    }

    public static ProgressMeter addProgressBar(final String barName, final int count) {
        return StartupNotificationManager.addProgressBar(barName, count);
    }

    public static ProgressMeter prependProgressBar(final String barName, final int count) {
        return StartupNotificationManager.prependProgressBar(barName, count);
    }
}
