package net.minecraftforge.client.model;

import javax.vecmath.Matrix4f;

import com.google.common.base.Function;

/*
 * generic model-specific transformation - for example, animation states can be represented by it
 */
public interface IModelState extends Function<IModelPart, TRSRTransformation> {
    /*
     * returns the transformation (in the local coordinates) that needs to be applied to the specific part of the model
     */
    @Override
    TRSRTransformation apply(IModelPart part);
}
