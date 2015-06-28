package net.minecraftforge.client.model;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;

/**
 * Doesn't touch the buffer in any way - you'll probably want to at least call buffer.flip after filling it with data.
 */
public class BakedModelBuilder extends IVertexConsumer.ByteBufferImpl
{
    private final ByteBuffer buf;
    private final IQuadInfo info;
    private final TextureAtlasSprite particle;
    private boolean isAmbientOcclusion = false;
    private boolean isGui3d = false;
    private VertexFormat format;

    public BakedModelBuilder(int quads, IQuadInfo info, TextureAtlasSprite particle)
    {
        this(createBuffer(quads), info, particle);
    }

    public BakedModelBuilder(ByteBuffer buf, IQuadInfo info, TextureAtlasSprite particle)
    {
        super(buf);
        this.buf = buf;
        this.info = info;
        this.particle = particle;
    }

    private static ByteBuffer createBuffer(int quads)
    {
        return ByteBuffer.allocate(quads);
    }

    @Override
    public void setVertexFormat(VertexFormat format)
    {
        this.format = new VertexFormat(format);
        super.setVertexFormat(format);
    }

    public BakedModelBuilder setFormat(VertexFormat format)
    {
        this.setVertexFormat(format);
        return this;
    }

    public BakedModelBuilder setAO(boolean isAmbientOcclusion)
    {
        this.isAmbientOcclusion = isAmbientOcclusion;
        return this;
    }

    public BakedModelBuilder set3d(boolean isGui3d)
    {
        this.isGui3d = isGui3d;
        return this;
    }

    public IFlexibleBakedModel build()
    {
        return new BakedModel(isAmbientOcclusion, isGui3d, particle, format, buf, info);
    }

    private static class BakedModel implements IFastBakedModel
    {
        private final boolean isAmbientOcclusion;
        private final boolean isGui3d;
        private final TextureAtlasSprite particle;
        private final VertexFormat format;
        private final ByteBuffer buf;
        private final IQuadInfo info;

        private ImmutableList<BakedQuad> quadList = null;

        public BakedModel(boolean isAmbientOcclusion, boolean isGui3d, TextureAtlasSprite particle, VertexFormat format, ByteBuffer buf, IQuadInfo info)
        {
            this.isAmbientOcclusion = isAmbientOcclusion;
            this.isGui3d = isGui3d;
            this.particle = particle;
            this.format = format;
            this.buf = buf;
            this.info = info;
        }

        public boolean isAmbientOcclusion()
        {
            return isAmbientOcclusion;
        }

        public boolean isGui3d()
        {
            return isGui3d;
        }

        public boolean isBuiltInRenderer()
        {
            return false;
        }

        public TextureAtlasSprite getTexture()
        {
            return particle;
        }

        public ItemCameraTransforms getItemCameraTransforms()
        {
            return ItemCameraTransforms.DEFAULT;
        }

        public List<BakedQuad> getFaceQuads(EnumFacing side)
        {
            return ImmutableList.of();
        }

        // Culling data is lost right now.
        public List<BakedQuad> getGeneralQuads()
        {
            if(quadList == null)
            {
                ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
                IntBuffer iBuf = buf.asIntBuffer();
                for(int i = 0; i < iBuf.limit(); i += format.getNextOffset() / 4)
                {
                    int[] data = new int[format.getNextOffset() / 4];
                    iBuf.get(data, i, data.length);
                    if(isColored(i))
                    {
                        builder.add(new IColoredBakedQuad.ColoredBakedQuad(data, getTintIndex(i), getOrientation(i)));
                    }
                    else
                    {
                        builder.add(new BakedQuad(data, getTintIndex(i), getOrientation(i)));
                    }
                }
                quadList = builder.build();
            }
            return quadList;
        }

        public VertexFormat getFormat()
        {
            return format;
        }

        public ByteBuffer getDataBuffer()
        {
            return buf;
        }

        public int getTintIndex(int quad)
        {
            return info.getTintIndex(quad);
        }

        public EnumFacing getOrientation(int quad)
        {
            return info.getOrientation(quad);
        }

        public boolean isColored(int quad)
        {
            return info.isColored(quad);
        }

        public boolean isCulled(int quad)
        {
            return info.isCulled(quad);
        }
    }
}
