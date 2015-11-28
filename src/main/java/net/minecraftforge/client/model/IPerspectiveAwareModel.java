package net.minecraftforge.client.model;

import java.util.List;

import javax.vecmath.Matrix4f;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.EnumFacing;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/*
 * Model that changes based on the rendering perspective
 * (first-person, GUI, e.t.c - see TransformType)
 */
public interface IPerspectiveAwareModel extends IBakedModel
{
    /*
     * Returns the pair of the model for the given perspective, and the matrix
     * that should be applied to the GL state before rendering it (matrix may be null).
     */
    Pair<IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType);

    public static class MapWrapper implements IFlexibleBakedModel, IPerspectiveAwareModel
    {
        private final IFlexibleBakedModel parent;
        private final ImmutableMap<TransformType, TRSRTransformation> transforms;

        public MapWrapper(IFlexibleBakedModel parent, ImmutableMap<TransformType, TRSRTransformation> transforms)
        {
            this.parent = parent;
            this.transforms = transforms;
        }

        public MapWrapper(IFlexibleBakedModel parent, IPerspectiveState state, IModelPart part)
        {
            this(parent, getTransforms(state, part));
        }

        public static ImmutableMap<TransformType, TRSRTransformation> getTransforms(IPerspectiveState state, IModelPart part)
        {
            ImmutableMap.Builder<TransformType, TRSRTransformation> builder = ImmutableMap.builder();
            for(TransformType type : TransformType.values())
            {
                builder.put(type, state.forPerspective(type).apply(part));
            }
            return builder.build();
        }

        public boolean isAmbientOcclusion() { return parent.isAmbientOcclusion(); }
        public boolean isGui3d() { return parent.isGui3d(); }
        public boolean isBuiltInRenderer() { return parent.isBuiltInRenderer(); }
        public TextureAtlasSprite getTexture() { return parent.getTexture(); }
        public ItemCameraTransforms getItemCameraTransforms() { return parent.getItemCameraTransforms(); }
        public List<BakedQuad> getFaceQuads(EnumFacing side) { return parent.getFaceQuads(side); }
        public List<BakedQuad> getGeneralQuads() { return parent.getGeneralQuads(); }
        public VertexFormat getFormat() { return parent.getFormat(); }

        @Override
        public Pair<IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType)
        {
            TRSRTransformation tr = transforms.get(cameraTransformType);
            Matrix4f mat = null;
            if(tr != null && tr != TRSRTransformation.identity()) mat = TRSRTransformation.blockCornerToCenter(tr).getMatrix();
            return Pair.of((IBakedModel)this, mat);
        }
    }
}
