/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.util;

import com.mojang.blaze3d.shaders.Program;
import net.minecraftforge.client.renderer.IForgeGlslPreprocessor;

import java.util.Arrays;
import java.util.List;

public final class GlslPreprocessorUtils
{

    private GlslPreprocessorUtils()
    {
        throw new IllegalStateException("Can not instantiate an instance of: GlslPreprocessorUtils. This is a utility class");
    }

    public static List<String> toShaderProgram(String input, Program.Type type, IForgeGlslPreprocessor... preprocessors) {

        List<String> current = Arrays.asList(input.split("\n"));

        for (IForgeGlslPreprocessor preprocessor : preprocessors)
        {
            current = preprocessor.process(String.join("\n", current), type);
        }

        return current.stream().map(s -> s + "\n").toList();

    }
}
