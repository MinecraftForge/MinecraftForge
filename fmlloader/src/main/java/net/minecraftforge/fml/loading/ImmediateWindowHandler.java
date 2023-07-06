/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import net.minecraftforge.fml.loading.progress.ProgressMeter;
import net.minecraftforge.fml.loading.progress.StartupNotificationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

public class ImmediateWindowHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    private static ImmediateWindowProvider provider;

    private static ProgressMeter earlyProgress;
    public static void load(final String launchTarget, final String[] arguments) {
        if (!List.of("forgeclient", "forgeclientuserdev", "forgeclientdev").contains(launchTarget)) {
            provider = new DummyProvider();
            LOGGER.info("ImmediateWindowProvider not loading because launch target is {}", launchTarget);
        } else if (!FMLConfig.getBoolConfigValue(FMLConfig.ConfigValue.EARLY_WINDOW_CONTROL)) {
            provider = new DummyProvider();
            LOGGER.info("ImmediateWindowProvider not loading because splash screen is disabled");
        } else {
            final var providername = FMLConfig.getConfigValue(FMLConfig.ConfigValue.EARLY_WINDOW_PROVIDER);
            LOGGER.info("Loading ImmediateWindowProvider {}", providername);
            final var maybeProvider = ServiceLoader.load(ImmediateWindowProvider.class)
                    .stream()
                    .map(ServiceLoader.Provider::get)
                    .filter(p -> Objects.equals(p.name(), providername))
                    .findFirst();
            provider = maybeProvider.or(() -> {
                LOGGER.info("Failed to find ImmediateWindowProvider {}, disabling", providername);
                return Optional.of(new DummyProvider());
            }).orElseThrow();
        }
        // Only update config if the provider isn't the dummy provider
        if (!Objects.equals(provider.name(), "dummyprovider"))
            FMLConfig.updateConfig(FMLConfig.ConfigValue.EARLY_WINDOW_PROVIDER, provider.name());
        FMLLoader.progressWindowTick = provider.initialize(arguments);
        earlyProgress = StartupNotificationManager.addProgressBar("EARLY", 0);
        earlyProgress.label("Bootstrapping Minecraft");
    }

    public static long setupMinecraftWindow(final IntSupplier width, final IntSupplier height, final Supplier<String> title, final LongSupplier monitor) {
        return provider.setupMinecraftWindow(width, height, title, monitor);
    }

    public static boolean positionWindow(Optional<Object> monitor,IntConsumer widthSetter, IntConsumer heightSetter, IntConsumer xSetter, IntConsumer ySetter) {
        return provider.positionWindow(monitor, widthSetter, heightSetter, xSetter, ySetter);
    }

    public static void updateFBSize(IntConsumer width, IntConsumer height) {
        provider.updateFramebufferSize(width, height);
    }

    public static <T> Supplier<T> loadingOverlay(Supplier<?> mc, Supplier<?> ri, Consumer<Optional<Throwable>> ex, boolean fade) {
        earlyProgress.complete();
        return provider.loadingOverlay(mc, ri, ex, fade);
    }

    public static void acceptGameLayer(final ModuleLayer layer) {
        provider.updateModuleReads(layer);
    }

    public static void renderTick() {
        provider.periodicTick();
    }

    public static String getGLVersion() {
        return provider.getGLVersion();
    }
    public static void updateProgress(final String message) {
        earlyProgress.label(message);
    }

    private record DummyProvider() implements ImmediateWindowProvider {
        private static Method NV_HANDOFF;
        private static Method NV_POSITION;
        private static Method NV_OVERLAY;
        private static Method NV_VERSION;

        @Override
        public String name() {
            return "dummyprovider";
        }

        @Override
        public Runnable initialize(String[] args) {
            return () -> {};
        }

        @Override
        public void updateFramebufferSize(final IntConsumer width, final IntConsumer height) {
        }

        @Override
        public long setupMinecraftWindow(final IntSupplier width, final IntSupplier height, final Supplier<String> title, final LongSupplier monitor) {
            try {
                var longsupplier = (LongSupplier)NV_HANDOFF.invoke(null, width, height, title, monitor);
                return longsupplier.getAsLong();
            } catch (Throwable e) {
                throw new IllegalStateException("How did you get here?", e);
            }
        }

        public boolean positionWindow(Optional<Object> monitor, IntConsumer widthSetter, IntConsumer heightSetter, IntConsumer xSetter, IntConsumer ySetter) {
            try {
                return (boolean)NV_POSITION.invoke(null, monitor, widthSetter, heightSetter, xSetter, ySetter);
            } catch (Throwable e) {
                throw new IllegalStateException("How did you get here?", e);
            }
        }
        @SuppressWarnings("unchecked")
        public <T> Supplier<T> loadingOverlay(Supplier<?> mc, Supplier<?> ri, Consumer<Optional<Throwable>> ex, boolean fade) {
            try {
                return (Supplier<T>) NV_OVERLAY.invoke(null, mc, ri, ex, fade);
            } catch (Throwable e) {
                throw new IllegalStateException("How did you get here?", e);
            }
        }

        @Override
        public String getGLVersion() {
            try {
                return (String) NV_VERSION.invoke(null);
            } catch (Throwable e) {
                return "3.2"; // Vanilla sets 3.2 in com.mojang.blaze3d.platform.Window
            }
        }

        @Override
        public void updateModuleReads(final ModuleLayer layer) {
            var fm = layer.findModule("forge");
            if (fm.isPresent()) {
                getClass().getModule().addReads(fm.get());
                var clz = fm.map(l -> Class.forName(l, "net.minecraftforge.client.loading.NoVizFallback")).orElseThrow();
                var methods = Arrays.stream(clz.getMethods()).filter(m -> Modifier.isStatic(m.getModifiers())).collect(Collectors.toMap(Method::getName, Function.identity()));
                NV_HANDOFF = methods.get("windowHandoff");
                NV_OVERLAY = methods.get("loadingOverlay");
                NV_POSITION = methods.get("windowPositioning");
                NV_VERSION = methods.get("glVersion");
            }
        }

        @Override
        public void periodicTick() {
            // NOOP
        }
    }
}
