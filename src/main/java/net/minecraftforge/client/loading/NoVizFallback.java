/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.loading;

import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.GpuBackend;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.server.packs.resources.ReloadInstance;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import org.lwjgl.glfw.GLFW;

public final class NoVizFallback {
    private static long WINDOW;
    public static LongSupplier windowHandoff(int width, int height, String title, long monitor, Supplier<Object> backend) {
        return () -> {
            try {
                return WINDOW = Window.createGlfwWindow(width, height, title, monitor, (GpuBackend)backend.get());
            } catch (Throwable e) {
                return sneak(e);
            }
        };
    }


    @SuppressWarnings("unchecked")
    private static <R, E extends Throwable> R sneak(Throwable t) throws E {
        throw (E)t;
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
