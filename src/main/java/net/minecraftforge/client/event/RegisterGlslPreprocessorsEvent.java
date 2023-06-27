/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.client.renderer.IForgeGlslPreprocessor;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This event is fired when a shader is about to be processed by the shader preprocessor.
 */
public class RegisterGlslPreprocessorsEvent extends Event implements IModBusEvent
{

    private final List<IForgeGlslPreprocessor> preprocessors = Lists.newArrayList();

    @Nullable
    private final ShaderInstance shaderInstance;

    @ApiStatus.Internal
    public RegisterGlslPreprocessorsEvent(@Nullable ShaderInstance shaderInstance)
    {
        this.shaderInstance = shaderInstance;
    }

    /**
     * The shader instance that is about to be processed.
     * Might be null if the shader is not yet known.
     *
     * @return The shader instance
     */
    @Nullable
    public ShaderInstance getShaderInstance()
    {
        return shaderInstance;
    }

    /**
     * The list of preprocessors that will be applied to the shader.
     * The default preprocessor is not included in this list.
     *
     * @return The list of preprocessors
     */
    public List<IForgeGlslPreprocessor> getPreprocessors()
    {
        return List.copyOf(preprocessors);
    }

    /**
     * Add a new preprocessor to the list of preprocessors.
     *
     * @param preprocessor The preprocessor to add
     */
    public void newProcessor(IForgeGlslPreprocessor preprocessor)
    {
        preprocessors.add(preprocessor);
    }

    /**
     * Add a new import handler to the list of preprocessors.
     *
     * @param importPrefix The import prefix to use, the default preprocessor uses moj_import. Pick something unique, this is not validated.
     * @param importHandler The import handler to add
     */
    public void newImportHandler(final String importPrefix, final IShaderImportHandler importHandler) {
        preprocessors.add(new GlslPreprocessor(importPrefix)
        {
            @org.jetbrains.annotations.Nullable
            @Override
            public String applyImport(boolean isRelative, @NotNull String toImport)
            {
                return importHandler.apply(isRelative, toImport);
            }
        });
    }

    /**
     * A handler for imports, used by {@link #newImportHandler(String, IShaderImportHandler)}.
     */
    @FunctionalInterface
    public interface IShaderImportHandler {

        /**
         * Invoked to handle an import.
         * Returns a string matching the import, or null if the import should be ignored.
         *
         * @param isRelative Whether the import is relative to the current shader
         * @param toImport The import to handle
         * @return The string to replace the import with, or null to ignore the import
         */
        @Nullable
        String apply(boolean isRelative, String toImport);
    }
}
