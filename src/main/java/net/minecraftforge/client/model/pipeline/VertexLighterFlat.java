package net.minecraftforge.client.model.pipeline;

import javax.vecmath.Vector3f;

import com.google.common.base.Objects;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public class VertexLighterFlat extends QuadGatheringTransformer
{
    protected final BlockInfo blockInfo = new BlockInfo();
    private int tint = -1;

    protected int posIndex = -1;
    protected int normalIndex = -1;
    protected int colorIndex = -1;
    protected int lightmapIndex = -1;

    @Override
    public void setParent(IVertexConsumer parent)
    {
        super.setParent(parent);
        if(Objects.equal(getVertexFormat(), parent.getVertexFormat())) return;
        setVertexFormat(getVertexFormat(parent));
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

    private static VertexFormat getVertexFormat(IVertexConsumer parent)
    {
        VertexFormat format = parent.getVertexFormat();
        if(format.hasNormal()) return format;
        format = new VertexFormat(format);
        format.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.NORMAL, 4));
        return format;
    }

    @Override
    protected void processQuad()
    {
        float[][] position = quadData[posIndex];
        float[][] normal = null;
        float[][] lightmap = quadData[lightmapIndex];
        float[][] color = quadData[colorIndex];

        if(normalIndex != -1 && (
            quadData[normalIndex][0][0] != -1 ||
            quadData[normalIndex][0][1] != -1 ||
            quadData[normalIndex][0][2] != -1))
        {
            normal = quadData[normalIndex];
        }
        else
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
                        // position adding moved to WorldRendererConsumer due to x and z not fitting completely into a float
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
        float e1 = 1 - 1e-2f;
        float e2 = 0.95f;
        BlockPos pos = blockInfo.getBlockPos();

        boolean full = blockInfo.getBlock().isFullCube();

        if((full || y < -e1) && normal[1] < -e2) pos = pos.down();
        if((full || y >  e1) && normal[1] >  e2) pos = pos.up();
        if((full || z < -e1) && normal[2] < -e2) pos = pos.north();
        if((full || z >  e1) && normal[2] >  e2) pos = pos.south();
        if((full || x < -e1) && normal[0] < -e2) pos = pos.west();
        if((full || x >  e1) && normal[0] >  e2) pos = pos.east();

        int brightness = blockInfo.getBlock().getMixedBrightnessForBlock(blockInfo.getWorld(), pos);

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

    public void setQuadTint(int tint)
    {
        this.tint = tint;
    }
    public void setQuadOrientation(EnumFacing orientation) {}
    public void setQuadCulled() {}
    public void setQuadColored() {}

    public void setWorld(IBlockAccess world)
    {
        blockInfo.setWorld(world);
    }

    public void setBlock(Block block)
    {
        blockInfo.setBlock(block);
    }

    public void setBlockPos(BlockPos blockPos)
    {
        blockInfo.setBlockPos(blockPos);
    }

    public void updateBlockInfo()
    {
        blockInfo.updateShift(true);
    }
}
