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

package net.minecraftforge.client.model.pipeline;

import javax.vecmath.Vector3f;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.Objects;

public class VertexLighterFlat extends QuadGatheringTransformer
{
    protected static final VertexFormatElement NORMAL_4F = new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.NORMAL, 4);

    protected final BlockInfo blockInfo;
    private int tint = -1;
    private boolean diffuse = true;

    protected int posIndex = -1;
    protected int normalIndex = -1;
    protected int colorIndex = -1;
    protected int lightmapIndex = -1;

    protected VertexFormat baseFormat;

    public VertexLighterFlat(BlockColors colors)
    {
        this.blockInfo = new BlockInfo(colors);
    }

    @Override
    public void setParent(IVertexConsumer parent)
    {
        super.setParent(parent);
        setVertexFormat(parent.getVertexFormat());
    }

    private void updateIndices()
    {
        for(int i = 0; i < getVertexFormat().getElementCount(); i++)
        {
            switch(getVertexFormat().getElement(i).getUsage())
            {
                case POSITION:
                    posIndex = i;
                    break;
                case NORMAL:
                    normalIndex = i;
                    break;
                case COLOR:
                    colorIndex = i;
                    break;
                case UV:
                    if(getVertexFormat().getElement(i).getIndex() == 1)
                    {
                        lightmapIndex = i;
                    }
                    break;
                default:
            }
        }
        if(posIndex == -1)
        {
            throw new IllegalArgumentException("vertex lighter needs format with position");
        }
        if(lightmapIndex == -1)
        {
            throw new IllegalArgumentException("vertex lighter needs format with lightmap");
        }
        if(colorIndex == -1)
        {
            throw new IllegalArgumentException("vertex lighter needs format with color");
        }
    }

    @Override
    public void setVertexFormat(VertexFormat format)
    {
        if (Objects.equals(format, baseFormat)) return;
        baseFormat = format;
        super.setVertexFormat(withNormal(format));
        updateIndices();
    }

    private static VertexFormat withNormal(VertexFormat format)
    {
        if (format == null || format.hasNormal()) return format;
        return new VertexFormat(format).addElement(NORMAL_4F);
    }

    @Override
    protected void processQuad()
    {
        float[][] position = quadData[posIndex];
        float[][] normal = null;
        float[][] lightmap = quadData[lightmapIndex];
        float[][] color = quadData[colorIndex];

        if (dataLength[normalIndex] >= 3
            && (quadData[normalIndex][0][0] != -1
            ||  quadData[normalIndex][0][1] != -1
            ||  quadData[normalIndex][0][2] != -1))
        {
            normal = quadData[normalIndex];
        }
        else // normals must be generated
        {
            normal = new float[4][4];
            Vector3f v1 = new Vector3f(position[3]);
            Vector3f t = new Vector3f(position[1]);
            Vector3f v2 = new Vector3f(position[2]);
            v1.sub(t);
            t.set(position[0]);
            v2.sub(t);
            v1.cross(v2, v1);
            v1.normalize();
            for(int v = 0; v < 4; v++)
            {
                normal[v][0] = v1.x;
                normal[v][1] = v1.y;
                normal[v][2] = v1.z;
                normal[v][3] = 0;
            }
        }

        int multiplier = -1;
        if(tint != -1)
        {
            multiplier = blockInfo.getColorMultiplier(tint);
        }

        VertexFormat format = parent.getVertexFormat();
        int count = format.getElementCount();

        for(int v = 0; v < 4; v++)
        {
            position[v][0] += blockInfo.getShx();
            position[v][1] += blockInfo.getShy();
            position[v][2] += blockInfo.getShz();

            float x = position[v][0] - .5f;
            float y = position[v][1] - .5f;
            float z = position[v][2] - .5f;

            //if(blockInfo.getBlock().isFullCube())
            {
                x += normal[v][0] * .5f;
                y += normal[v][1] * .5f;
                z += normal[v][2] * .5f;
            }

            float blockLight = lightmap[v][0], skyLight = lightmap[v][1];
            updateLightmap(normal[v], lightmap[v], x, y, z);
            if(dataLength[lightmapIndex] > 1)
            {
                if(blockLight > lightmap[v][0]) lightmap[v][0] = blockLight;
                if(skyLight > lightmap[v][1]) lightmap[v][1] = skyLight;
            }
            updateColor(normal[v], color[v], x, y, z, tint, multiplier);
            if(diffuse)
            {
                float d = LightUtil.diffuseLight(normal[v][0], normal[v][1], normal[v][2]);
                for(int i = 0; i < 3; i++)
                {
                    color[v][i] *= d;
                }
            }
            if(EntityRenderer.anaglyphEnable)
            {
                applyAnaglyph(color[v]);
            }

            // no need for remapping cause all we could've done is add 1 element to the end
            for(int e = 0; e < count; e++)
            {
                VertexFormatElement element = format.getElement(e);
                switch(element.getUsage())
                {
                    case POSITION:
                        // position adding moved to VertexBufferConsumer due to x and z not fitting completely into a float
                        /*float[] pos = new float[4];
                        System.arraycopy(position[v], 0, pos, 0, position[v].length);
                        pos[0] += blockInfo.getBlockPos().getX();
                        pos[1] += blockInfo.getBlockPos().getY();
                        pos[2] += blockInfo.getBlockPos().getZ();*/
                        parent.put(e, position[v]);
                        break;
                    case NORMAL: if(normalIndex != -1)
                    {
                        parent.put(e, normal[v]);
                        break;
                    }
                    case COLOR:
                        parent.put(e, color[v]);
                        break;
                    case UV: if(element.getIndex() == 1)
                    {
                        parent.put(e, lightmap[v]);
                        break;
                    }
                    default:
                        parent.put(e, quadData[e][v]);
                }
            }
        }
        tint = -1;
    }

    protected void applyAnaglyph(float[] color)
    {
        float r = color[0];
        color[0] = (r * 30 + color[1] * 59 + color[2] * 11) / 100;
        color[1] = (r * 3 + color[1] * 7) / 10;
        color[2] = (r * 3 + color[2] * 7) / 10;
    }

    protected void updateLightmap(float[] normal, float[] lightmap, float x, float y, float z)
    {
        final float e1 = 1f - 1e-2f;
        final float e2 = 0.95f;

        boolean full = blockInfo.isFullCube();
        EnumFacing side = null;

             if((full || y < -e1) && normal[1] < -e2) side = EnumFacing.DOWN;
        else if((full || y >  e1) && normal[1] >  e2) side = EnumFacing.UP;
        else if((full || z < -e1) && normal[2] < -e2) side = EnumFacing.NORTH;
        else if((full || z >  e1) && normal[2] >  e2) side = EnumFacing.SOUTH;
        else if((full || x < -e1) && normal[0] < -e2) side = EnumFacing.WEST;
        else if((full || x >  e1) && normal[0] >  e2) side = EnumFacing.EAST;

        int i = side == null ? 0 : side.ordinal() + 1;
        int brightness = blockInfo.getPackedLight()[i];

        lightmap[0] = ((float)((brightness >> 0x04) & 0xF) * 0x20) / 0xFFFF;
        lightmap[1] = ((float)((brightness >> 0x14) & 0xF) * 0x20) / 0xFFFF;
    }

    protected void updateColor(float[] normal, float[] color, float x, float y, float z, float tint, int multiplier)
    {
        if(tint != -1)
        {
            color[0] *= (float)(multiplier >> 0x10 & 0xFF) / 0xFF;
            color[1] *= (float)(multiplier >> 0x8 & 0xFF) / 0xFF;
            color[2] *= (float)(multiplier & 0xFF) / 0xFF;
        }
    }

    @Override
    public void setQuadTint(int tint)
    {
        this.tint = tint;
    }
    @Override
    public void setQuadOrientation(EnumFacing orientation) {}
    public void setQuadCulled() {}
    @Override
    public void setTexture(TextureAtlasSprite texture) {}
    @Override
    public void setApplyDiffuseLighting(boolean diffuse)
    {
        this.diffuse = diffuse;
    }

    public void setWorld(IBlockAccess world)
    {
        blockInfo.setWorld(world);
    }

    public void setState(IBlockState state)
    {
        blockInfo.setState(state);
    }

    public void setBlockPos(BlockPos blockPos)
    {
        blockInfo.setBlockPos(blockPos);
    }

    public void resetBlockInfo()
    {
        blockInfo.reset();
    }

    public void updateBlockInfo()
    {
        blockInfo.updateShift();
        blockInfo.updateFlatLighting();
    }
}
