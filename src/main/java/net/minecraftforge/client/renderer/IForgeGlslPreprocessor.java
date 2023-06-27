/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.renderer;

import com.mojang.blaze3d.shaders.Program;

import java.util.List;

@FunctionalInterface
public interface IForgeGlslPreprocessor
{
    /**
     * Implements a default shader preprocessor for Forge compatible shaders.
     *
     * @param shaderSource The shader source to process
     * @param type
     * @return The lines of the processed shader
     */
    List<String> process(String shaderSource, Program.Type type);
}
