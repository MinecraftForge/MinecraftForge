/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.pipeline;

import com.mojang.blaze3d.vertex.VertexConsumer;

/**
 * Wrapper for {@link VertexConsumer} which delegates all operations to its parent.
 * <p>
 * Useful for defining custom pipeline elements that only process certain data.
 *
 * @deprecated This class is scheduled for removal in 26.4 due to a lack of adoption by mods, in order to ease porting.
 *             Please reach out to the Forge team if you are relying on this and would like to see it remain in Forge.
 *             We may keep it or add it back in a future version if there is enough demand.
 */
@Deprecated(forRemoval = true, since = "26.4")
public class VertexConsumerWrapper implements VertexConsumer {
    protected final VertexConsumer parent;

    public VertexConsumerWrapper(VertexConsumer parent) {
        this.parent = parent;
    }

    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        parent.addVertex(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer setColor(int color) {
        parent.setColor(color);
        return this;
    }

    @Override
    public VertexConsumer setColor(int r, int g, int b, int a) {
        parent.setColor(r, g, b, a);
        return this;
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
        parent.setUv(u, v);
        return this;
    }

    @Override
    public VertexConsumer setUv1(int u, int v) {
        parent.setUv1(u, v);
        return this;
    }

    @Override
    public VertexConsumer setUv2(int u, int v) {
        parent.setUv2(u, v);
        return this;
    }

    @Override
    public VertexConsumer setUv3(float u, float v) {
        parent.setUv3(u, v);
        return this;
    }

    @Override
    public VertexConsumer setNormal(float x, float y, float z) {
        parent.setNormal(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer setLineWidth(float width) {
        parent.setLineWidth(width);
        return this;
    }
}
