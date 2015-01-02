package net.minecraftforge.client.model;

import javax.vecmath.Matrix4f;

/*
 * generic model-specific transformation - for example, animation states can be represented by it
 */
public interface IModelState {
    /*
     * Matrix representing this transformation
     */
    Matrix4f getMatrix();
    /*
     * Returns the child's state relative to the parent
     */
    IModelState compose(IModelState childLocalState);
}
