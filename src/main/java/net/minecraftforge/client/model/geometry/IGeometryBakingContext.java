/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.geometry;

import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.NamedRenderTypeManager;
import net.minecraftforge.client.RenderTypeGroup;
import org.jetbrains.annotations.Nullable;

/**
 * The context in which a geometry is being baked, providing information such as lighting and
 * {@link ItemTransforms transforms}, and allowing the user to create {@link Material materials} and query
 * {@link RenderTypeGroup render types}.
 *
 * @see StandaloneGeometryBakingContext
 * @see BlockGeometryBakingContext
 */
public interface IGeometryBakingContext
{
    /**
     * Gets the name of the model being baked for logging and caching purposes.
     */
    String getModelName();

    /**
     * Checks if a material is present in the model.
     *
     * @param name The name of the material
     */
    boolean hasMaterial(String name);

    /**
     * Resolves the final texture name, taking into account texture aliases and replacements.
     *
     * @param name The name of the material
     * @return The material, or the missing texture if not found
     */
    Material getMaterial(String name);

    /**
     * Checks whether this model should render in 3D in a GUI.
     */
    boolean isGui3d();

    /**
     * Checks if block lighting should be used for this model.
     */
    boolean useBlockLight();

    /**
     * Checks if per-vertex ambient occlusion should be used for this model.
     */
    boolean useAmbientOcclusion();

    /**
     * Gets the transforms for display in item form.
     */
    ItemTransforms getTransforms();

    /**
     * Gets the root transformation to be applied to all variants of this model, regardless of item transforms.
     */
    Transformation getRootTransform();

    /**
     * Gets a hint of the render type this model should use. Custom loaders may ignore this.
     */
    @Nullable
    ResourceLocation getRenderTypeHint();

    /**
     * Queries the visibility of a component of this model.
     *
     * @param component The component for which to query visibility
     * @param fallback  The default visibility if an override isn't found
     * @return The visibility of the component
     */
    boolean isComponentVisible(String component, boolean fallback);

    /**
     * Gets a {@link RenderTypeGroup} with the given name, or the empty group if not found.
     */
    default RenderTypeGroup getRenderType(ResourceLocation name)
    {
        return NamedRenderTypeManager.get(name);
    }
}
