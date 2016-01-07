package net.minecraftforge.client.model;

import java.util.List;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.EnumFacing;

/*
 * Version of IBakedModel with less restriction on camera transformations and with explicit format of the baked array.
 */
@SuppressWarnings("deprecation")
public interface IFlexibleBakedModel extends IBakedModel
{
    /*
     * Specifies the format which BakedQuads' getVertexData will have.
     */
    VertexFormat getFormat();

    /*
     * Default implementation of IFlexibleBakedModel that should be useful in most cases
     */
    public static class Wrapper implements IFlexibleBakedModel
    {
        private final IBakedModel parent;
        VertexFormat format;

        public Wrapper(IBakedModel parent, VertexFormat format)
        {
            this.parent = parent;
            this.format = format;
        }

        public List<BakedQuad> getFaceQuads(EnumFacing side)
        {
            return parent.getFaceQuads(side);
        }

        public List<BakedQuad> getGeneralQuads()
        {
            return parent.getGeneralQuads();
        }

        public boolean isAmbientOcclusion()
        {
            return parent.isAmbientOcclusion();
        }

        public boolean isGui3d()
        {
            return parent.isGui3d();
        }

        public boolean isBuiltInRenderer()
        {
            return parent.isBuiltInRenderer();
        }

        public TextureAtlasSprite getParticleTexture()
        {
            return parent.getParticleTexture();
        }

        @Deprecated
        public ItemCameraTransforms getItemCameraTransforms()
        {
            return parent.getItemCameraTransforms();
        }

        public VertexFormat getFormat()
        {
            return new VertexFormat(format);
        }
    }
}
