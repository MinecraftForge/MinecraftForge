/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.pipeline;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.texture.OverlayTexture;

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Vertex pipeline element that remaps incoming data to another format.
 */
public class RemappingVertexPipeline implements VertexConsumer
{
    private static final Set<VertexFormatElement> KNOWN_ELEMENTS = Set.of(DefaultVertexFormat.ELEMENT_POSITION,
            DefaultVertexFormat.ELEMENT_COLOR, DefaultVertexFormat.ELEMENT_UV, DefaultVertexFormat.ELEMENT_UV1,
            DefaultVertexFormat.ELEMENT_UV2, DefaultVertexFormat.ELEMENT_NORMAL, DefaultVertexFormat.ELEMENT_PADDING);
    private static final int[] EMPTY_INT_ARRAY = new int[0];

    private final VertexConsumer parent;
    private final VertexFormat targetFormat;

    private final Vector3d position = new Vector3d(0, 0, 0);
    private final Vector3f normal = new Vector3f(0, 0, 0);
    private final int[] color = new int[] { 255, 255, 255, 255 };
    private final float[] uv0 = new float[] { 0, 0 };
    private final int[] uv1 = new int[] { OverlayTexture.NO_WHITE_U, OverlayTexture.WHITE_OVERLAY_V };
    private final int[] uv2 = new int[] { 0, 0 };

    private final Map<VertexFormatElement, Integer> miscElementIds;
    private final int[][] misc;

    public RemappingVertexPipeline(VertexConsumer parent, VertexFormat targetFormat)
    {
        this.parent = parent;
        this.targetFormat = targetFormat;

        this.miscElementIds = new IdentityHashMap<>();
        int i = 0;
        for (var element : targetFormat.getElements())
            if (element.getUsage() != VertexFormatElement.Usage.PADDING && !KNOWN_ELEMENTS.contains(element))
                this.miscElementIds.put(element, i++);
        this.misc = new int[i][];
        Arrays.fill(this.misc, EMPTY_INT_ARRAY);
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z)
    {
        position.set(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer normal(float x, float y, float z)
    {
        normal.set(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer color(int r, int g, int b, int a)
    {
        color[0] = r;
        color[1] = g;
        color[2] = b;
        color[3] = a;
        return this;
    }

    @Override
    public VertexConsumer uv(float u, float v)
    {
        uv0[0] = u;
        uv0[1] = v;
        return this;
    }

    @Override
    public VertexConsumer overlayCoords(int u, int v)
    {
        uv1[0] = u;
        uv1[1] = v;
        return this;
    }

    @Override
    public VertexConsumer uv2(int u, int v)
    {
        uv2[0] = u;
        uv2[1] = v;
        return this;
    }

    @Override
    public VertexConsumer misc(VertexFormatElement element, int... values)
    {
        Integer id = miscElementIds.get(element);
        if (id != null)
            misc[id] = Arrays.copyOf(values, values.length);
        return this;
    }

    @Override
    public void endVertex()
    {
        for (var element : targetFormat.getElements())
        {
            // Ignore padding
            if (element.getUsage() == VertexFormatElement.Usage.PADDING)
                continue;

            // Try to match and output any of the supported elements, and if that fails, treat as misc
            if (element.equals(DefaultVertexFormat.ELEMENT_POSITION))
                parent.vertex(position.x, position.y, position.z);
            else if (element.equals(DefaultVertexFormat.ELEMENT_NORMAL))
                parent.normal(normal.x(), normal.y(), normal.z());
            else if (element.equals(DefaultVertexFormat.ELEMENT_COLOR))
                parent.color(color[0], color[1], color[2], color[3]);
            else if (element.equals(DefaultVertexFormat.ELEMENT_UV0))
                parent.uv(uv0[0], uv0[1]);
            else if (element.equals(DefaultVertexFormat.ELEMENT_UV1))
                parent.overlayCoords(uv1[0], uv1[1]);
            else if (element.equals(DefaultVertexFormat.ELEMENT_UV2))
                parent.uv2(uv2[0], uv2[1]);
            else
                parent.misc(element, misc[miscElementIds.get(element)]);
        }
    }

    @Override
    public void defaultColor(int r, int g, int b, int a)
    {
        parent.defaultColor(r, g, b, a);
    }

    @Override
    public void unsetDefaultColor()
    {
        parent.unsetDefaultColor();
    }
}
