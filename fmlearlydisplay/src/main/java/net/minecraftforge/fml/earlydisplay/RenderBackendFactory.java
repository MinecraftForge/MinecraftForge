/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.earlydisplay;

/**
 * Selects the appropriate render backend based on the user's preference.
 * <p>
 * When Vulkan is implemented, the factory will try the preferred backend first
 * and fall back to the other one if creation fails.
 */
public final class RenderBackendFactory {
    private RenderBackendFactory() {}

    public static BaseRenderBackend create(String preferred) {
        if ("vulkan".equals(preferred)) {
            // Future: try VulkanRenderBackend first, fall back to OpenGLRenderBackend
            // var vulkan = new VulkanRenderBackend();
            // if (vulkan.isSupported()) return vulkan;
        }
        return new OpenGLRenderBackend();
    }
}
