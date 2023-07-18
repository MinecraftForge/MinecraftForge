/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.loading;

import com.mojang.blaze3d.platform.Monitor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.server.packs.resources.ReloadInstance;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import org.lwjgl.glfw.GLFW;

public final class NoVizFallback {
    private static long WINDOW;
    public static LongSupplier windowHandoff(IntSupplier width, IntSupplier height, Supplier<String> title, LongSupplier monitor) {
        return () -> WINDOW = GLFW.glfwCreateWindow(width.getAsInt(), height.getAsInt(), title.get(), monitor.getAsLong(), 0L);
    }

    public static Supplier<LoadingOverlay> loadingOverlay(Supplier<Minecraft> mc, Supplier<ReloadInstance> ri, Consumer<Optional<Throwable>> ex, boolean fadein) {
        return () -> new LoadingOverlay(mc.get(), ri.get(), ex, fadein);
    }

    public static Boolean windowPositioning(Optional<Monitor> monitor, IntConsumer widthSetter, IntConsumer heightSetter, IntConsumer xSetter, IntConsumer ySetter) {
        return Boolean.FALSE;
    }

    public static String glVersion() {
        if (WINDOW != 0) {
            var maj = GLFW.glfwGetWindowAttrib(WINDOW, GLFW.GLFW_CONTEXT_VERSION_MAJOR);
            var min = GLFW.glfwGetWindowAttrib(WINDOW, GLFW.GLFW_CONTEXT_VERSION_MINOR);
            return maj+"."+min;
        } else {
            return "3.2";
        }
    }
}
