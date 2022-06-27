/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.renderable;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A renderable object composed of a hierarchy of parts, each made up of a number of meshes.
 * <p>
 * Each mesh renders a set of quads using a different texture.
 *
 * @see Builder
 */
public class CompositeRenderable implements IRenderable<CompositeRenderable.Transforms>
{
    private final List<Component> components = new ArrayList<>();

    private CompositeRenderable()
    {
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, ITextureRenderTypeLookup textureRenderTypeLookup, int lightmap, int overlay, float partialTick, Transforms context)
    {
        for (var component : components)
            component.render(poseStack, bufferSource, textureRenderTypeLookup, lightmap, overlay, context);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    private static class Component
    {
        private final String name;
        private final List<Component> children = new ArrayList<>();
        private final List<Mesh> meshes = new ArrayList<>();

        public Component(String name)
        {
            this.name = name;
        }

        public void render(PoseStack poseStack, MultiBufferSource bufferSource, ITextureRenderTypeLookup textureRenderTypeLookup, int lightmap, int overlay, Transforms context)
        {
            Matrix4f matrix = context.getTransform(name);
            if (matrix != null)
            {
                poseStack.pushPose();
                poseStack.mulPoseMatrix(matrix);
            }

            for (var part : children)
                part.render(poseStack, bufferSource, textureRenderTypeLookup, lightmap, overlay, context);

            for (var mesh : meshes)
                mesh.render(poseStack, bufferSource, textureRenderTypeLookup, lightmap, overlay);

            if (matrix != null)
                poseStack.popPose();
        }
    }

    private static class Mesh
    {
        private final ResourceLocation texture;
        private final List<BakedQuad> quads = new ArrayList<>();

        public Mesh(ResourceLocation texture)
        {
            this.texture = texture;
        }

        public void render(PoseStack poseStack, MultiBufferSource bufferSource, ITextureRenderTypeLookup textureRenderTypeLookup, int lightmap, int overlay)
        {
            var consumer = bufferSource.getBuffer(textureRenderTypeLookup.get(texture));
            for (var quad : quads)
            {
                consumer.putBulkData(poseStack.last(), quad, 1, 1, 1, 1, lightmap, overlay, true);
            }
        }
    }

    public static class Builder
    {
        private final CompositeRenderable renderable = new CompositeRenderable();

        private Builder()
        {
        }

        public PartBuilder<Builder> child(String name)
        {
            var child = new Component(name);
            renderable.components.add(child);
            return new PartBuilder<>(this, child);
        }

        public CompositeRenderable get()
        {
            return renderable;
        }
    }

    public static class PartBuilder<T>
    {
        private final T parent;
        private final Component component;

        private PartBuilder(T parent, Component component)
        {
            this.parent = parent;
            this.component = component;
        }

        public PartBuilder<PartBuilder<T>> child(String name)
        {
            var child = new Component(component.name + "/" + name);
            this.component.children.add(child);
            return new PartBuilder<>(this, child);
        }

        public PartBuilder<T> addMesh(ResourceLocation texture, List<BakedQuad> quads)
        {
            var mesh = new Mesh(texture);
            mesh.quads.addAll(quads);
            component.meshes.add(mesh);
            return this;
        }

        public T end()
        {
            return parent;
        }
    }

    /**
     * A context value that provides {@link Matrix4f} transforms for certain parts of the model.
     */
    public static class Transforms
    {
        /**
         * A default instance that has no transforms specified.
         */
        public static final Transforms EMPTY = new Transforms(ImmutableMap.of());

        /**
         * Builds a MultipartTransforms object with the given mapping.
         */
        public static Transforms of(ImmutableMap<String, Matrix4f> parts)
        {
            return new Transforms(parts);
        }

        private final ImmutableMap<String, Matrix4f> parts;

        private Transforms(ImmutableMap<String, Matrix4f> parts)
        {
            this.parts = parts;
        }

        @Nullable
        public Matrix4f getTransform(String part)
        {
            return parts.get(part);
        }
    }
}
