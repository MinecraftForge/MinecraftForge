/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.model.pipeline;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.BakedQuad.MaterialInfo;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraftsingularity.client.textures.UnitTextureAtlasSprite;

import java.util.function.Consumer;

import org.joml.Vector3f;

/**
 * Vertex consumer that outputs {@linkplain BakedQuad baked quads}.
 * <p>
 * This consumer accepts data in {@link com.mojang.blaze3d.vertex.DefaultVertexFormat#BLOCK} and is not picky about
 * ordering or missing elements, but will not automatically populate missing data (color will be black, for example).
 */
public class BakedQuadBuilder implements VertexConsumer {
    private static final int MAX_VERTICES = 4;
    private final Consumer<BakedQuad> quadConsumer;

    int vertexIndex = -1;
    private Vector3f[] positions;
    private long[] uvs;

    private int tintIndex;
    private Direction direction = Direction.DOWN;
    private TextureAtlasSprite sprite = UnitTextureAtlasSprite.INSTANCE;
    private boolean shade;
    private int lightEmission;

    public BakedQuadBuilder(Consumer<BakedQuad> quadConsumer) {
        this.quadConsumer = quadConsumer;
        setup();
    }

    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        if (vertexIndex++ == MAX_VERTICES)
            build();

        var pos = this.positions[vertexIndex];
        pos.x = x;
        pos.y = y;
        pos.z = z;
        return this;
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
        this.uvs[vertexIndex] = UVPair.pack(u, v);
        return this;
    }

    /** Will not actually do anything, vanilla implementations ignore if passed invalid data so we do as well */
    @Override
    public VertexConsumer setNormal(float x, float y, float z) {
        //throw new IllegalStateException("Normal is not supported in BakedQuads anymore, vanilla never used this");
        return this;
    }

    /** Will not actually do anything, vanilla implementations ignore if passed invalid data so we do as well */
    @Override
    public VertexConsumer setColor(int r, int g, int b, int a) {
        //throw new IllegalStateException("Per-vertex color is not supported in BakedQuads anymore, vanilla removed the feature");
        return this;
    }

    /** Will not actually do anything, vanilla implementations ignore if passed invalid data so we do as well */
    @Override
    public VertexConsumer setColor(int packed) {
        //throw new IllegalStateException("Per-vertex color is not supported in BakedQuads anymore, vanilla removed the feature");
        return this;
    }

    /** Will not actually do anything, vanilla implementations ignore if passed invalid data so we do as well */
    @Override
    public VertexConsumer setLineWidth(float packed) {
        //throw new IllegalStateException("Normal is not supported in BakedQuads anymore, vanilla never used this");
        return this;
    }

    /** Will not actually do anything, vanilla implementations ignore if passed invalid data so we do as well */
    @Override
    public VertexConsumer setUv1(int u, int v) {
        //throw new IllegalStateException("UV1 is not supported in BakedQuads anymore, vanilla never used this");
        return this;
    }

    /** Will not actually do anything, vanilla implementations ignore if passed invalid data so we do as well */
    @Override
    public VertexConsumer setUv2(int u, int v) {
        //throw new IllegalStateException("UV2 is not supported in BakedQuads anymore, you can't bake lighting per vertex anymore");
        return this;
    }

    private void setup() {
        this.positions = new Vector3f[]{ new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f() };
        this.uvs = new long[4];
        this.vertexIndex = 0;
    }

    public BakedQuad build() {
        BakedQuad quad = new BakedQuad(
            this.positions[0], this.positions[1], this.positions[2], this.positions[3],
            this.uvs[0],       this.uvs[1],       this.uvs[2],       this.uvs[3],
            direction,
            MaterialInfo.of(
                new Material.Baked(sprite, false),
                sprite.transparency(),
                tintIndex, shade, lightEmission
            )
        );
        setup();
        // We have a full quad, pass it to the consumer and reset
        quadConsumer.accept(quad);
        return quad;
    }

    public void setTintIndex(int tintIndex) {
        this.tintIndex = tintIndex;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setSprite(TextureAtlasSprite sprite) {
        this.sprite = sprite;
    }

    public void setShade(boolean shade) {
        this.shade = shade;
    }

    public void setLightEmission(int value) {
        this.lightEmission = value;
    }

    public static class Single extends BakedQuadBuilder {
        private final BakedQuad[] output;

        public Single() {
            this(new BakedQuad[1]);
        }

        private Single(BakedQuad[] output) {
            super(q -> output[0] = q);
            this.output = output;
        }

        public BakedQuad getQuad() {
            var quad = Preconditions.checkNotNull(output[0], "No quad has been emitted. Vertices in buffer: " + vertexIndex);
            output[0] = null;
            return quad;
        }
    }
}
