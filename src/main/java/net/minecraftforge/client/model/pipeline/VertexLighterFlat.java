/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.world.IBlockDisplayReader;

public class VertexLighterFlat extends QuadGatheringTransformer
{
    protected static final VertexFormatElement NORMAL_4F = new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.NORMAL, 4);
    
    // TODO 1.16/1.17 possibly refactor out the need for the "unpacked" format entirely. It's creating more headaches than solutions.
    // This mess reverses the conversion to float bits done in LightUtil.unpack
    private static final int LIGHTMAP_PACKING_FACTOR = ((256 << (8 * (DefaultVertexFormats.TEX_2SB.getType().getSize() - 1))) - 1) >>> 1;
    // Max lightmap value, for rescaling
    private static final int LIGHTMAP_MAX = 0xF0;
    // Inlined factor for rescaling input lightmap values, "rounded" up to the next float value to avoid precision loss when result is truncated to int
    private static final float LIGHTMAP_RESCALE = Math.nextAfter((float) LIGHTMAP_PACKING_FACTOR / LIGHTMAP_MAX, LIGHTMAP_PACKING_FACTOR);

    protected final BlockInfo blockInfo;
    private int tint = -1;
    private boolean diffuse = true;

    protected int posIndex = -1;
    protected int normalIndex = -1;
    protected int colorIndex = -1;
    protected int lightmapIndex = -1;

    protected VertexFormat baseFormat;
    protected MatrixStack.Entry pose;
    
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
    
    public void setTransform(final MatrixStack.Entry pose)
    {
        this.pose = pose;
    }

    private void updateIndices()
    {
        for(int i = 0; i < getVertexFormat().getElements().size(); i++)
        {
            switch(getVertexFormat().getElements().get(i).getUsage())
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
                    if(getVertexFormat().getElements().get(i).getIndex() == 2)
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

    static VertexFormat withNormal(VertexFormat format)
    {
        //This is the case in 99.99%. Cache the value, so we don't have to redo it every time, and the speed up the equals check in LightUtil
        if (format == DefaultVertexFormats.BLOCK)
            return DefaultVertexFormats.BLOCK;
        return withNormalUncached(format);
    }

    private static VertexFormat withNormalUncached(VertexFormat format)
    {
        if (format == null || format.hasNormal())
            return format;
        List<VertexFormatElement> l = Lists.newArrayList(format.getElements());
        l.add(NORMAL_4F);
        return new VertexFormat(ImmutableList.copyOf(l));
    }

    @Override
    protected void processQuad()
    {
        float[][] position = quadData[posIndex];
        float[][] normal = null;
        float[][] lightmap = quadData[lightmapIndex];
        float[][] color = quadData[colorIndex];

        if (dataLength[normalIndex] >= 3
            && (quadData[normalIndex][0][0] != 0
            ||  quadData[normalIndex][0][1] != 0
            ||  quadData[normalIndex][0][2] != 0))
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
            v2.cross(v1);
            v2.normalize();
            for(int v = 0; v < 4; v++)
            {
                normal[v][0] = v2.getX();
                normal[v][1] = v2.getY();
                normal[v][2] = v2.getZ();
                normal[v][3] = 0;
            }
        }

        int multiplier = -1;
        if(tint != -1)
        {
            multiplier = blockInfo.getColorMultiplier(tint);
        }

        VertexFormat format = parent.getVertexFormat();
        int count = format.getElements().size();

        for(int v = 0; v < 4; v++)
        {
            float x = position[v][0] - .5f;
            float y = position[v][1] - .5f;
            float z = position[v][2] - .5f;

//            if(blockInfo.getState().getBlock().isFullCube(blockInfo.getState()))
            {
                x += normal[v][0] * .5f;
                y += normal[v][1] * .5f;
                z += normal[v][2] * .5f;
            }

            float blockLight = lightmap[v][0] * LIGHTMAP_RESCALE, skyLight = lightmap[v][1] * LIGHTMAP_RESCALE;
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

            // no need for remapping cause all we could've done is add 1 element to the end
            for(int e = 0; e < count; e++)
            {
                VertexFormatElement element = format.getElements().get(e);
                switch(element.getUsage())
                {
                    case POSITION:
                        final Vector4f pos = new Vector4f(
                                position[v][0], position[v][1], position[v][2], 1);
                        pos.transform(pose.getMatrix());

                        position[v][0] = pos.getX();
                        position[v][1] = pos.getY();
                        position[v][2] = pos.getZ();
                        parent.put(e, position[v]);
                        break;
                    case NORMAL:
                        final Vector3f norm = new Vector3f(normal[v]);
                        norm.transform(pose.getNormal());

                        normal[v][0] = norm.getX();
                        normal[v][1] = norm.getY();
                        normal[v][2] = norm.getZ();
                        parent.put(e, normal[v]);
                        break;
                    case COLOR:
                        parent.put(e, color[v]);
                        break;
                    case UV:
                        if(element.getIndex() == 2)
                        {
                            parent.put(e, lightmap[v]);
                            break;
                        }
                        // else fallthrough to default
                    default:
                        parent.put(e, quadData[e][v]);
                }
            }
        }
        tint = -1;
    }

    protected void updateLightmap(float[] normal, float[] lightmap, float x, float y, float z)
    {
        final float e1 = 1f - 1e-2f;
        final float e2 = 0.95f;

        boolean full = blockInfo.isFullCube();
        Direction side = null;

             if((full || y < -e1) && normal[1] < -e2) side = Direction.DOWN;
        else if((full || y >  e1) && normal[1] >  e2) side = Direction.UP;
        else if((full || z < -e1) && normal[2] < -e2) side = Direction.NORTH;
        else if((full || z >  e1) && normal[2] >  e2) side = Direction.SOUTH;
        else if((full || x < -e1) && normal[0] < -e2) side = Direction.WEST;
        else if((full || x >  e1) && normal[0] >  e2) side = Direction.EAST;

        int i = side == null ? 0 : side.ordinal() + 1;
        int brightness = blockInfo.getPackedLight()[i];

        lightmap[0] = LightTexture.getLightBlock(brightness) / (float) 0xF;
        lightmap[1] = LightTexture.getLightSky(brightness) / (float) 0xF;
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
    public void setQuadOrientation(Direction orientation) {}
    public void setQuadCulled() {}
    @Override
    public void setTexture(TextureAtlasSprite texture) {}
    @Override
    public void setApplyDiffuseLighting(boolean diffuse)
    {
        this.diffuse = diffuse;
    }

    public void setWorld(IBlockDisplayReader world)
    {
        blockInfo.setWorld(world);
    }

    public void setState(BlockState state)
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
        blockInfo.updateFlatLighting();
    }
}
