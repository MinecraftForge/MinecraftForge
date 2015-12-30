package net.minecraftforge.client.model;

import java.util.List;

import javax.vecmath.Matrix4f;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.EnumFacing;

import org.apache.commons.lang3.tuple.Pair;

public interface ISmartBlockModel extends IBakedModel
{
    IBakedModel handleBlockState(IBlockState state);

    public static abstract class Wrapper implements ISmartBlockModel, IFlexibleBakedModel
    {
        protected final IFlexibleBakedModel parent;

        public Wrapper(IFlexibleBakedModel parent)
        {
            this.parent = parent;
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

        public ItemCameraTransforms getItemCameraTransforms()
        {
            return parent.getItemCameraTransforms();
        }

        public VertexFormat getFormat()
        {
            return parent.getFormat();
        }
    }

    public static abstract class PerspectiveWrapper extends Wrapper implements IPerspectiveAwareModel
    {
        protected final IPerspectiveAwareModel parent;

        public PerspectiveWrapper(IPerspectiveAwareModel parent)
        {
            super(parent);
            this.parent = parent;
        }

        public Pair<? extends IFlexibleBakedModel, Matrix4f> handlePerspective(TransformType type)
        {
            Pair<? extends IFlexibleBakedModel, Matrix4f> pair = parent.handlePerspective(type);
            return Pair.of(new ISmartBlockModel.Wrapper(pair.getLeft())
            {
                public IBakedModel handleBlockState(IBlockState state)
                {
                    return PerspectiveWrapper.this.handleBlockState(state);
                }
            }, pair.getRight());
        }
    }
}
