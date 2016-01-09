package net.minecraftforge.client.model;

import java.util.List;

import javax.vecmath.Matrix4f;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

/*
 * Model that changes based on the rendering perspective
 * (first-person, GUI, e.t.c - see TransformType)
 */
public interface IPerspectiveAwareModel extends IFlexibleBakedModel
{
    /*
     * Returns the pair of the model for the given perspective, and the matrix
     * that should be applied to the GL state before rendering it (matrix may be null).
     */
    Pair<? extends IFlexibleBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType);

    public static class MapWrapper implements IPerspectiveAwareModel
    {
        private final IFlexibleBakedModel parent;
        private final ImmutableMap<TransformType, TRSRTransformation> transforms;

        public MapWrapper(IFlexibleBakedModel parent, ImmutableMap<TransformType, TRSRTransformation> transforms)
        {
            this.parent = parent;
            this.transforms = transforms;
        }

        public MapWrapper(IFlexibleBakedModel parent, IModelState state)
        {
            this(parent, getTransforms(state));
        }

        public static ImmutableMap<TransformType, TRSRTransformation> getTransforms(IModelState state)
        {
            ImmutableMap.Builder<TransformType, TRSRTransformation> builder = ImmutableMap.builder();
            for(TransformType type : TransformType.values())
            {
                Optional<TRSRTransformation> tr = state.apply(Optional.of(type));
                if(tr.isPresent())
                {
                    builder.put(type, tr.get());
                }
            }
            return builder.build();
        }

        public static ImmutableMap<TransformType, TRSRTransformation> getTransforms(ItemCameraTransforms transforms)
        {
            ImmutableMap.Builder<TransformType, TRSRTransformation> builder = ImmutableMap.builder();
            for(TransformType type : TransformType.values())
            {
                builder.put(type, new TRSRTransformation(transforms.getTransform(type)));
            }
            return builder.build();
        }

        public static Pair<? extends IFlexibleBakedModel, Matrix4f> handlePerspective(IFlexibleBakedModel model, ImmutableMap<TransformType, TRSRTransformation> transforms, TransformType cameraTransformType)
        {
            TRSRTransformation tr = transforms.get(cameraTransformType);
            Matrix4f mat = null;
            if(tr != null && !tr.equals(TRSRTransformation.identity())) mat = TRSRTransformation.blockCornerToCenter(tr).getMatrix();
            return Pair.of(model, mat);
        }

        public static Pair<? extends IFlexibleBakedModel, Matrix4f> handlePerspective(IFlexibleBakedModel model, IModelState state, TransformType cameraTransformType)
        {
            TRSRTransformation tr = state.apply(Optional.of(cameraTransformType)).or(TRSRTransformation.identity());
            if(tr != TRSRTransformation.identity())
            {
                return Pair.of(model, TRSRTransformation.blockCornerToCenter(tr).getMatrix());
            }
            return Pair.of(model, null);
        }

        public boolean isAmbientOcclusion() { return parent.isAmbientOcclusion(); }
        public boolean isGui3d() { return parent.isGui3d(); }
        public boolean isBuiltInRenderer() { return parent.isBuiltInRenderer(); }
        public TextureAtlasSprite getParticleTexture() { return parent.getParticleTexture(); }
        public ItemCameraTransforms getItemCameraTransforms() { return parent.getItemCameraTransforms(); }
        public List<BakedQuad> getFaceQuads(EnumFacing side) { return parent.getFaceQuads(side); }
        public List<BakedQuad> getGeneralQuads() { return parent.getGeneralQuads(); }
        public VertexFormat getFormat() { return parent.getFormat(); }

        @Override
        public Pair<? extends IFlexibleBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType)
        {
            return handlePerspective(this, transforms, cameraTransformType);
        }
    }
}
