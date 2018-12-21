package net.minecraftforge.client.extensions;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;

public interface IForgeBakedModel
{
    default IBakedModel getBakedModel()
    {
        return (IBakedModel) this;
    }

    default boolean isAmbientOcclusion(IBlockState state) { return getBakedModel().isAmbientOcclusion(); }
    
    /*
     * Returns the pair of the model for the given perspective, and the matrix that
     * should be applied to the GL state before rendering it (matrix may be null).
     */
    default org.apache.commons.lang3.tuple.Pair<? extends IBakedModel, javax.vecmath.Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType)
    {
        return net.minecraftforge.client.ForgeHooksClient.handlePerspective(getBakedModel(), cameraTransformType);
    }
}
