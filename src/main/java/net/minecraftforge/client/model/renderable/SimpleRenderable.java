/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.renderable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Implements a simple renderable consisting of a hierarchy of parts, where each part can contain a number of meshes.
 * Each mesh pairs a texture, with a set of quads.
 */
public class SimpleRenderable implements IRenderable<MultipartTransforms>
{
    private final List<Part> parts = new ArrayList<>();

    private SimpleRenderable()
    {
    }

    public static Builder builder()
    {
        return new Builder();
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, Function<ResourceLocation, RenderType> renderTypeFunction, int lightmapCoord, int overlayCoord, float partialTicks, MultipartTransforms renderValues)
    {
        for(var part : parts)
        {
            part.render(poseStack, bufferSource, renderTypeFunction, lightmapCoord, overlayCoord, renderValues);
        }
    }

    private static class Part
    {
        private final String name;
        private final List<Part> parts = new ArrayList<>();
        private final List<Mesh> meshes = new ArrayList<>();

        public Part(String name)
        {
            this.name = name;
        }

        public void render(PoseStack poseStack, MultiBufferSource bufferSource, Function<ResourceLocation, RenderType> renderTypeFunction, int lightmapCoord, int overlayCoord, MultipartTransforms renderValues)
        {
            Matrix4f matrix = renderValues.getPartValues(name);
            if (matrix != null)
            {
                poseStack.pushPose();
                poseStack.mulPoseMatrix(matrix);
            }

            for(var part : parts)
            {
                part.render(poseStack, bufferSource, renderTypeFunction, lightmapCoord, overlayCoord, renderValues);
            }

            for(var mesh : meshes)
            {
                mesh.render(poseStack, bufferSource, renderTypeFunction, lightmapCoord, overlayCoord, renderValues);
            }

            if (matrix != null)
            {
                poseStack.popPose();
            }
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

        public void render(PoseStack poseStack, MultiBufferSource bufferSource, Function<ResourceLocation, RenderType> renderTypeFunction, int lightmapCoord, int overlayCoord, MultipartTransforms renderValues)
        {
            var consumer = bufferSource.getBuffer(renderTypeFunction.apply(texture));
            for(var quad : quads)
            {
                consumer.putBulkData(poseStack.last(), quad, 1, 1, 1, 1, lightmapCoord, overlayCoord, true);
            }
        }
    }

    public static class Builder
    {
        private final SimpleRenderable renderable = new SimpleRenderable();

        private Builder()
        {
        }

        public PartBuilder<Builder> child(String name)
        {
            var child = new Part(name);
            renderable.parts.add(child);
            return new PartBuilder<>(this, child);
        }

        public SimpleRenderable get()
        {
            return renderable;
        }
    }

    public static class PartBuilder<T>
    {
        private final T parent;
        private final Part part;

        private PartBuilder(T parent, Part part)
        {
            this.parent = parent;
            this.part = part;
        }

        public PartBuilder<PartBuilder<T>> child(String name)
        {
            var child = new Part(part.name + "/" + name);
            this.part.parts.add(child);
            return new PartBuilder<>(this, child);
        }

        public PartBuilder<T> addMesh(ResourceLocation texture, List<BakedQuad> quads)
        {
            var mesh = new Mesh(texture);
            mesh.quads.addAll(quads);
            part.meshes.add(mesh);
            return this;
        }

        public T end()
        {
            return parent;
        }
    }
}
