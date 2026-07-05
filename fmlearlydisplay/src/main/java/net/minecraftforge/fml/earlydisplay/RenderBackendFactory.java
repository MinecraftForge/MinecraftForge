/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.earlydisplay;

import net.minecraftforge.fml.earlydisplay.vulkan.VulkanRenderBackend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFWVulkan.glfwVulkanSupported;

/**
 * Selects the appropriate render backend based on the user's preference.
 * <p>
 * When Vulkan is requested, the factory checks if Vulkan is available and
 * falls back to OpenGL if not.
 */
public final class RenderBackendFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger("EARLYDISPLAY");

    private RenderBackendFactory() {}

    public static BaseRenderBackend create(String preferred) {
        if ("vulkan".equals(preferred)) {
            try {
                glfwInit(); // safe to call before EarlyWindow.init() — GLFW is reference-counted
                if (glfwVulkanSupported()) {
                    LOGGER.info("Vulkan is supported, using Vulkan render backend");
                    return new VulkanRenderBackend();
                }
                LOGGER.warn("Vulkan was requested but is not supported, falling back to OpenGL");
            } catch (Throwable t) {
                LOGGER.warn("Vulkan backend initialization failed, falling back to OpenGL", t);
            }
        }
        return new OpenGLRenderBackend();
    }
}
