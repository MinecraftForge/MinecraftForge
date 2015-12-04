package net.minecraftforge.client.model;

import com.google.common.base.Optional;

/*
 * Represents the dynamic information associated with the model.
 * Common use case is (possibly interpolated) animation frame.
 */
public interface IModelState
{
    /*
     * Returns the transformation that needs to be applied to the specific part of the model.
     * Coordinate system is determined by the part type.
     * if no part is provided, global model transformation is returned.
     */
    Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part);
}