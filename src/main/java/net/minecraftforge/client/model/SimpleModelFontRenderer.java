/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.client.model;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public abstract class SimpleModelFontRenderer extends FontRenderer {

    private float r, g, b, a;
    private final Matrix4f matrix;
    private ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
    private final VertexFormat format;
    private final Vector3f normal = new Vector3f(0, 0, 1);
    private final EnumFacing orientation;
    private boolean fillBlanks = false;

    private TextureAtlasSprite sprite;

    public SimpleModelFontRenderer(GameSettings settings, ResourceLocation font, TextureManager manager, boolean isUnicode, Matrix4f matrix, VertexFormat format)
    {
        super(settings, font, manager, isUnicode);
        this.matrix = new Matrix4f(matrix);
        Matrix3f nm = new Matrix3f();
        this.matrix.getRotationScale(nm);
        nm.invert();
        nm.transpose();
        this.format = format;
        nm.transform(normal);
        normal.normalize();
        orientation = EnumFacing.getFacingFromVector(normal.x, normal.y, normal.z);
    }

    public void setSprite(TextureAtlasSprite sprite)
    {
        this.sprite = sprite;
        super.onResourceManagerReload(null);
    }

    public void setFillBlanks(boolean fillBlanks)
    {
        this.fillBlanks = fillBlanks;
    }

    @Override
    protected float renderDefaultChar(int pos, boolean italic)
    {
        float x = (pos % 16) / 16f;
        float y = (pos / 16) / 16f;
        float sh = italic ? 1f : 0f;
        float w = charWidth[pos] - 1.01f;
        float h = FONT_HEIGHT - 1.01f;
        float wt = w  / 128f;
        float ht = h  / 128f;

        UnpackedBakedQuad.Builder quadBuilder = new UnpackedBakedQuad.Builder(format);
        quadBuilder.setTexture(sprite);
        quadBuilder.setQuadOrientation(orientation);

        addVertex(quadBuilder, posX + sh,     posY,     x,      y);
        addVertex(quadBuilder, posX - sh,     posY + h, x,      y + ht);
        addVertex(quadBuilder, posX + w + sh, posY + h, x + wt, y + ht);
        addVertex(quadBuilder, posX + w - sh, posY,     x + wt, y);
        builder.add(quadBuilder.build());

        if(fillBlanks)
        {
            float cuv = 15f / 16f;

            quadBuilder = new UnpackedBakedQuad.Builder(format);
            quadBuilder.setTexture(sprite);
            quadBuilder.setQuadOrientation(orientation);

            addVertex(quadBuilder, posX + w + sh,              posY,     cuv, cuv);
            addVertex(quadBuilder, posX + w - sh,              posY + h, cuv, cuv);
            addVertex(quadBuilder, posX + charWidth[pos] + sh, posY + h, cuv, cuv);
            addVertex(quadBuilder, posX + charWidth[pos] - sh, posY,     cuv, cuv);
            builder.add(quadBuilder.build());

            quadBuilder = new UnpackedBakedQuad.Builder(format);
            quadBuilder.setTexture(sprite);
            quadBuilder.setQuadOrientation(orientation);

            addVertex(quadBuilder, posX + sh,                  posY + h,           cuv, cuv);
            addVertex(quadBuilder, posX - sh,                  posY + FONT_HEIGHT, cuv, cuv);
            addVertex(quadBuilder, posX + charWidth[pos] + sh, posY + FONT_HEIGHT, cuv, cuv);
            addVertex(quadBuilder, posX + charWidth[pos] - sh, posY + h,           cuv, cuv);
            builder.add(quadBuilder.build());
        }
        return charWidth[pos];
    }

    private final Vector4f vec = new Vector4f();

    private void addVertex(UnpackedBakedQuad.Builder quadBuilder, float x, float y, float u, float v)
    {
        vec.x = x;
        vec.y = y;
        vec.z = 0;
        vec.w = 1;
        matrix.transform(vec);
        for(int e = 0; e < format.getElementCount(); e++)
        {
            switch(format.getElement(e).getUsage())
            {
                case POSITION:
                    quadBuilder.put(e, vec.x, vec.y, vec.z, vec.w);
                    break;
                case UV:
                    quadBuilder.put(e, sprite.getInterpolatedU(u * 16), sprite.getInterpolatedV(v * 16), 0, 1);
                    break;
                case COLOR:
                    quadBuilder.put(e, r, g, b, a);
                    break;
                case NORMAL:
                    //quadBuilder.put(e, normal.x, normal.y, normal.z, 1);
                    quadBuilder.put(e, 0, 0, 1, 1);
                    break;
                default:
                    quadBuilder.put(e);
                    break;
            }
        }
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        super.onResourceManagerReload(resourceManager);
        String p = locationFontTexture.getResourcePath();
        if(p.startsWith("textures/")) p = p.substring("textures/".length(), p.length());
        if(p.endsWith(".png")) p = p.substring(0, p.length() - ".png".length());
        String f = locationFontTexture.getResourceDomain() + ":" + p;
        sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(f);
    }

    @Override
    protected abstract float renderUnicodeChar(char c, boolean italic);

    @Override
    protected void doDraw(float shift)
    {
        posX += (int)shift;
    }

    @Override
    protected void setColor(float r, float g, float b, float a)
    {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    @Override public void enableAlpha()
    {
    }

    @Override
    protected void bindTexture(ResourceLocation location)
    {
    }

    public ImmutableList<BakedQuad> build()
    {
        ImmutableList<BakedQuad> ret = builder.build();
        builder = ImmutableList.builder();
        return ret;
    }
}
