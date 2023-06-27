/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.renderer.transparency;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.renderer.IForgeGlslPreprocessor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OITGlslPreprocessor implements IForgeGlslPreprocessor
{
    @Nullable
    private final ShaderInstance instance;

    public OITGlslPreprocessor(@Nullable ShaderInstance shaderInstance)
    {
        instance = shaderInstance;
    }

    @Override
    public List<String> process(String shaderSource, Program.Type type)
    {
        if (instance == null || type != Program.Type.FRAGMENT)
        {
            return List.of(shaderSource.split("\n"));
        }

        final List<String> lines = Lists.newArrayList(shaderSource.split("\n"));

        if (!isAdaptable(lines))
        {
            return lines;
        }

        updateVersion(lines);
        injectTransparentRenderUniform(lines, instance);
        renameMainMethod(lines);
        injectShaderPrefix(lines);
        injectShaderSuffix(lines);

        return lines;
    }

    private static boolean isAdaptable(final List<String> lines) {
        return lines.stream().anyMatch(s -> s.equals("in float vertexDistance;")) &&
                lines.stream().filter(s -> s.startsWith("out vec4")).count() == 1;
    }

    private static void updateVersion(List<String> lines)
    {
        //We inject the extension so we can handle this for now like this.
        final int versionLine = findLastLineIndexStartingWith(lines, "#version");
        if (versionLine == -1) {
            throw new IllegalStateException("Failed to find #version line in shader");
        }

        final String[] versionParts = OITShaders.FRAGMENT_SHADER_VERSION_HEADER.split("\n");

        lines.remove(versionLine);
        for (int i = 0; i < versionParts.length; i++)
        {
            String versionPart = versionParts[i];
            lines.add(versionLine + i, versionPart);
        }
    }

    private static void injectTransparentRenderUniform(List<String> lines, ShaderInstance instance)
    {
        injectUniform(lines, ForgeHooksClient.UNIFORM_OIT_TYPE, ForgeHooksClient.UNIFORM_OIT_NAME);

        final Uniform uniform = new Uniform(ForgeHooksClient.UNIFORM_OIT_NAME, 0, 1, instance); //BOOL -> ONCE
        instance.registerCustomUniform(uniform);
    }

    private static void injectUniform(final List<String> lines, final String type, final String name) {
        int injectionIndex = findLastLineIndexStartingWith(lines, "uniform");
        if (injectionIndex == -1) {
            injectionIndex = findLastLineIndexStartingWith(lines, "#extension");
            if (injectionIndex == -1) {
                injectionIndex = findLastLineIndexStartingWith(lines, "#version");
                if (injectionIndex == -1) {
                    throw new IllegalStateException("Failed to find #version, or #extension or existing uniform lines in shader");
                }
            }
        }
        lines.add(injectionIndex + 1, "uniform " + type + " " + name + ";");
    }

    private static int findLastLineIndexStartingWith(final List<String> lines, final String prefix) {
        for (int i = lines.size() - 1; i >= 0; --i) {
            if (lines.get(i).trim().startsWith(prefix)) {
                return i;
            }
        }
        return -1;
    }

    private static void renameMainMethod(final List<String> lines) {
        final int mainMethodLine = findLastLineIndexStartingWith(lines, "void main()");

        final String mainMethod = lines.get(mainMethodLine);
        lines.set(mainMethodLine, mainMethod.replace("main", "mainShader"));
    }

    private static void injectShaderPrefix(final List<String> lines) {
        int versionLine = findLastLineIndexStartingWith(lines, "#extension");
        if (versionLine == -1) {
            versionLine = findLastLineIndexStartingWith(lines, "#version");
            if (versionLine == -1) {
                throw new IllegalStateException("Failed to find #version or #extension line in shader");
            }
        }

        final List<String> toInject = List.of(OITShaders.FRAGMENT_SHADER_PREFIX.split("\n"));
        for (int i = 0; i < toInject.size(); i++)
        {
            String line = toInject.get(i);
            lines.add(versionLine + 1 + i, line);
        }
    }

    private static void injectShaderSuffix(final List<String> lines) {
        final int outLine = findLastLineIndexStartingWith(lines, "out vec4");
        final String outVariable = lines.get(outLine).split(" ")[2].replace(";", "");

        lines.remove(outLine);
        lines.add(outLine, "vec4 " + outVariable + ";");

        //TODO: Dynamically determine the depth variable name
        final String depthVariable = "vertexDistance";

        final String code = OITShaders.FRAGMENT_SHADER_SUFFIX
                .replace("%OUT_COLOR%", outVariable)
                .replace("%Z_VALUE%", depthVariable);

        final List<String> toInject = List.of(code.split("\n"));
        lines.add("\n");
        lines.addAll(toInject);
    }
}
