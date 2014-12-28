package net.minecraftforge.client.model;

import javax.vecmath.Matrix4f;

/*
 * transformation that can be represented by 4x4 matrix, and can therefore be applied to the current GL state
 */
public interface ISimpleModelTransformation extends IModelTransformation {
    /*
     * return the Matrix4f representing this transformation
     */
    public Matrix4f getMatrix();

    /*
     * apply this transformation to the current GL matrix
     */
    public void multiplyCurrentGlMatrix();
}
