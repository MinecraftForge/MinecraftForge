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

import com.google.common.collect.ImmutableList;

import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;

public abstract class SimpleModelFontRenderer extends FontRenderer {

    private float r, g, b, a;
    private final TransformationMatrix transform;
    private ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
    private final Vector3f normal = new Vector3f(0, 0, 1);
    private final Direction orientation;
    private boolean fillBlanks = false;

    private TextureAtlasSprite sprite;

    public SimpleModelFontRenderer(GameSettings settings, ResourceLocation font, TextureManager manager, boolean isUnicode, Matrix4f matrix)
    {
        super(manager, null);
        this.transform = new TransformationMatrix(matrix);
        transform.transformNormal(normal);
        orientation = Direction.getFacingFromVector(normal.getX(), normal.getY(), normal.getZ());
    }

    public void setSprite(TextureAtlasSprite sprite)
    {
        this.sprite = sprite;
    }

    public void setFillBlanks(boolean fillBlanks)
    {
        this.fillBlanks = fillBlanks;
    }

    private final Vector4f vec = new Vector4f();

    private void addVertex(BakedQuadBuilder quadBuilder, float x, float y, float u, float v)
    {
        ImmutableList<VertexFormatElement> elements = quadBuilder.getVertexFormat().getElements();
        for(int e = 0; e < elements.size(); e++)
        {
            VertexFormatElement element = elements.get(e);
            switch(element.getUsage())
            {
                case POSITION:
                    vec.set(x, y, 0f, 1f);
                    transform.transformPosition(vec);
                    quadBuilder.put(e, vec.getX(), vec.getY(), vec.getZ(), vec.getW());
                    break;
                case COLOR:
                    quadBuilder.put(e, r, g, b, a);
                    break;
                case NORMAL:
                    //quadBuilder.put(e, normal.x, normal.y, normal.z, 1);
                    quadBuilder.put(e, 0, 0, 1, 1);
                    break;
                case UV:
                    if(element.getIndex() == 0)
                    {
                        quadBuilder.put(e, sprite.getInterpolatedU(u * 16), sprite.getInterpolatedV(v * 16), 0, 1);
                        break;
                    }
                    // else fallthrough to default
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
