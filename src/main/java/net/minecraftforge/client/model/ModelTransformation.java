package net.minecraftforge.client.model;

import java.nio.FloatBuffer;
import java.util.Collection;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.block.model.ItemTransformVec3f;

public class ModelTransformation implements ISimpleModelTransformation
{
    private static final FloatBuffer matrixBuf = BufferUtils.createFloatBuffer(16);

    private final Matrix4f matrix = new Matrix4f();

    public ModelTransformation(Matrix4f matrix)
    {
        this.matrix.set(matrix);
    }

    public ModelTransformation(ItemTransformVec3f transform)
    {
        this(transform.getMatrix());
    }

    public Matrix4f getMatrix()
    {
        return (Matrix4f)matrix.clone();
    }

    public void multiplyCurrentGlMatrix()
    {
        multiplyCurrentGlMatrix(matrix);
    }

    public static void multiplyCurrentGlMatrix(Matrix4f matrix)
    {
        matrixBuf.clear();
        float[] t = new float[4];
        for(int i = 0; i < 4; i++)
        {
            matrix.getColumn(i, t);
            matrixBuf.put(t);
        }
        matrixBuf.flip();
        GL11.glMultMatrix(matrixBuf);
    }
}
