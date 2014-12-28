package net.minecraftforge.client.model;

import javax.vecmath.Matrix4f;

import com.google.common.base.Function;

/*
 * Represents the dynamic information associated with the model.
 * Common use case is (possibly interpolated) animation frame.
 */
<<<<<<< HEAD
public interface IModelState extends Function<IModelPart, TRSRTransformation> {
=======
public interface IModelState extends Function<IModelPart, TRSRTransformation>
{
>>>>>>> Added model loader registry
    /*
     * returns the transformation (in the local coordinates) that needs to be applied to the specific part of the model.
     */
    @Override
    TRSRTransformation apply(IModelPart part);
}
