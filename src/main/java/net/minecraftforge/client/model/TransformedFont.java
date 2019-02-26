/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import com.google.common.collect.ImmutableList;

import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.fonts.Font;
import net.minecraft.client.gui.fonts.TexturedGlyph;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;

public class TransformedFont extends Font {
    
    private class TransformedGlyph extends TexturedGlyph
    {
        TransformedGlyph(TexturedGlyph parent)
        {
            super(parent.getTextureLocation(), parent.u0, parent.u1, parent.v0, parent.v1, parent.field_211240_f, parent.field_211241_g, parent.field_211242_h, parent.field_211243_i);
        }
        
        @Override
        public void render(TextureManager textureManagerIn, boolean isItalic, float posX, float posY, BufferBuilder buffer, float red, float green, float blue, float alpha)
        {
            int i = 3;
            float f = posX + field_211240_f;
            float f1 = posX + field_211241_g;
            float f2 = field_211242_h - 3.0F;
            float f3 = field_211243_i - 3.0F;
            float f4 = posY + f2;
            float f5 = posY + f3;
            float f6 = isItalic ? 1.0F - 0.25F * f2 : 0.0F;
            float f7 = isItalic ? 1.0F - 0.25F * f3 : 0.0F;
            buffer.pos((double)(f + f6), (double)f4, 0.0D).tex((double)u0, (double)v0).color(red, green, blue, alpha).endVertex();
            buffer.pos((double)(f + f7), (double)f5, 0.0D).tex((double)u0, (double)v1).color(red, green, blue, alpha).endVertex();
            buffer.pos((double)(f1 + f7), (double)f5, 0.0D).tex((double)u1, (double)v1).color(red, green, blue, alpha).endVertex();
            buffer.pos((double)(f1 + f6), (double)f4, 0.0D).tex((double)u1, (double)v0).color(red, green, blue, alpha).endVertex();
            
            float sh = isItalic ? 1f : 0f;
            float w = (f1 - f) - 1.01f;
            float h = (f3 - f2) - 1.01f;

            UnpackedBakedQuad.Builder quadBuilder = new UnpackedBakedQuad.Builder(format);
            quadBuilder.setTexture(sprite);
            quadBuilder.setQuadOrientation(orientation);

            addVertex(quadBuilder, posX + sh,     posY,     u0, v0);
            addVertex(quadBuilder, posX - sh,     posY + h, u0, v1);
            addVertex(quadBuilder, posX + w + sh, posY + h, u1, v1);
            addVertex(quadBuilder, posX + w - sh, posY,     u1, v0);
            builder.add(quadBuilder.build());

            if(fillBlanks)
            {
                float cuv = 15f / 16f;

                quadBuilder = new UnpackedBakedQuad.Builder(format);
                quadBuilder.setTexture(sprite);
                quadBuilder.setQuadOrientation(orientation);

                addVertex(quadBuilder, posX + w + sh,              posY,     cuv, cuv);
                addVertex(quadBuilder, posX + w - sh,              posY + h, cuv, cuv);
                addVertex(quadBuilder, posX + (f1 - f) + sh, posY + h, cuv, cuv);
                addVertex(quadBuilder, posX + (f1 - f) - sh, posY,     cuv, cuv);
                builder.add(quadBuilder.build());

                quadBuilder = new UnpackedBakedQuad.Builder(format);
                quadBuilder.setTexture(sprite);
                quadBuilder.setQuadOrientation(orientation);

                addVertex(quadBuilder, posX + sh,            posY + h,         cuv, cuv);
                addVertex(quadBuilder, posX - sh,            posY + (f3 - f2), cuv, cuv);
                addVertex(quadBuilder, posX + (f1 - f) + sh, posY + (f3 - f2), cuv, cuv);
                addVertex(quadBuilder, posX + (f1 - f) - sh, posY + h,         cuv, cuv);
                builder.add(quadBuilder.build());
            }
        }
    }

    private float r, g, b, a;
    private final Matrix4f matrix;
    private ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
    private final VertexFormat format;
    private final Vector3f normal = new Vector3f(0, 0, 1);
    private final EnumFacing orientation;
    private boolean fillBlanks = false;

    private TextureAtlasSprite sprite;

    public TransformedFont(GameSettings settings, ResourceLocation font, TextureManager manager, boolean isUnicode, Matrix4f matrix, VertexFormat format)
    {
    	super(manager, null);
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
    }

    public void setFillBlanks(boolean fillBlanks)
    {
        this.fillBlanks = fillBlanks;
    }
    
    @Override
    public TexturedGlyph getGlyph(char character)
    {
        final TexturedGlyph glyph = super.getGlyph(character);
        return new TransformedGlyph(glyph);
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

    public ImmutableList<BakedQuad> build()
    {
        ImmutableList<BakedQuad> ret = builder.build();
        builder = ImmutableList.builder();
        return ret;
    }
}
