package net.minecraft.src;

import java.nio.FloatBuffer;
import org.lwjgl.opengl.GL11;

public class ClippingHelperImpl extends ClippingHelper
{
    private static ClippingHelperImpl instance = new ClippingHelperImpl();
    private FloatBuffer projectionMatrixBuffer = GLAllocation.createDirectFloatBuffer(16);
    private FloatBuffer modelviewMatrixBuffer = GLAllocation.createDirectFloatBuffer(16);
    private FloatBuffer field_1691_h = GLAllocation.createDirectFloatBuffer(16);

    /**
     * Initialises the ClippingHelper object then returns an instance of it.
     */
    public static ClippingHelper getInstance()
    {
        instance.init();
        return instance;
    }

    /**
     * Normalize the frustum.
     */
    private void normalize(float[][] par1ArrayOfFloat, int par2)
    {
        float var3 = MathHelper.sqrt_float(par1ArrayOfFloat[par2][0] * par1ArrayOfFloat[par2][0] + par1ArrayOfFloat[par2][1] * par1ArrayOfFloat[par2][1] + par1ArrayOfFloat[par2][2] * par1ArrayOfFloat[par2][2]);
        par1ArrayOfFloat[par2][0] /= var3;
        par1ArrayOfFloat[par2][1] /= var3;
        par1ArrayOfFloat[par2][2] /= var3;
        par1ArrayOfFloat[par2][3] /= var3;
    }

    private void init()
    {
        this.projectionMatrixBuffer.clear();
        this.modelviewMatrixBuffer.clear();
        this.field_1691_h.clear();
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, this.projectionMatrixBuffer);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, this.modelviewMatrixBuffer);
        this.projectionMatrixBuffer.flip().limit(16);
        this.projectionMatrixBuffer.get(this.projectionMatrix);
        this.modelviewMatrixBuffer.flip().limit(16);
        this.modelviewMatrixBuffer.get(this.modelviewMatrix);
        this.clippingMatrix[0] = this.modelviewMatrix[0] * this.projectionMatrix[0] + this.modelviewMatrix[1] * this.projectionMatrix[4] + this.modelviewMatrix[2] * this.projectionMatrix[8] + this.modelviewMatrix[3] * this.projectionMatrix[12];
        this.clippingMatrix[1] = this.modelviewMatrix[0] * this.projectionMatrix[1] + this.modelviewMatrix[1] * this.projectionMatrix[5] + this.modelviewMatrix[2] * this.projectionMatrix[9] + this.modelviewMatrix[3] * this.projectionMatrix[13];
        this.clippingMatrix[2] = this.modelviewMatrix[0] * this.projectionMatrix[2] + this.modelviewMatrix[1] * this.projectionMatrix[6] + this.modelviewMatrix[2] * this.projectionMatrix[10] + this.modelviewMatrix[3] * this.projectionMatrix[14];
        this.clippingMatrix[3] = this.modelviewMatrix[0] * this.projectionMatrix[3] + this.modelviewMatrix[1] * this.projectionMatrix[7] + this.modelviewMatrix[2] * this.projectionMatrix[11] + this.modelviewMatrix[3] * this.projectionMatrix[15];
        this.clippingMatrix[4] = this.modelviewMatrix[4] * this.projectionMatrix[0] + this.modelviewMatrix[5] * this.projectionMatrix[4] + this.modelviewMatrix[6] * this.projectionMatrix[8] + this.modelviewMatrix[7] * this.projectionMatrix[12];
        this.clippingMatrix[5] = this.modelviewMatrix[4] * this.projectionMatrix[1] + this.modelviewMatrix[5] * this.projectionMatrix[5] + this.modelviewMatrix[6] * this.projectionMatrix[9] + this.modelviewMatrix[7] * this.projectionMatrix[13];
        this.clippingMatrix[6] = this.modelviewMatrix[4] * this.projectionMatrix[2] + this.modelviewMatrix[5] * this.projectionMatrix[6] + this.modelviewMatrix[6] * this.projectionMatrix[10] + this.modelviewMatrix[7] * this.projectionMatrix[14];
        this.clippingMatrix[7] = this.modelviewMatrix[4] * this.projectionMatrix[3] + this.modelviewMatrix[5] * this.projectionMatrix[7] + this.modelviewMatrix[6] * this.projectionMatrix[11] + this.modelviewMatrix[7] * this.projectionMatrix[15];
        this.clippingMatrix[8] = this.modelviewMatrix[8] * this.projectionMatrix[0] + this.modelviewMatrix[9] * this.projectionMatrix[4] + this.modelviewMatrix[10] * this.projectionMatrix[8] + this.modelviewMatrix[11] * this.projectionMatrix[12];
        this.clippingMatrix[9] = this.modelviewMatrix[8] * this.projectionMatrix[1] + this.modelviewMatrix[9] * this.projectionMatrix[5] + this.modelviewMatrix[10] * this.projectionMatrix[9] + this.modelviewMatrix[11] * this.projectionMatrix[13];
        this.clippingMatrix[10] = this.modelviewMatrix[8] * this.projectionMatrix[2] + this.modelviewMatrix[9] * this.projectionMatrix[6] + this.modelviewMatrix[10] * this.projectionMatrix[10] + this.modelviewMatrix[11] * this.projectionMatrix[14];
        this.clippingMatrix[11] = this.modelviewMatrix[8] * this.projectionMatrix[3] + this.modelviewMatrix[9] * this.projectionMatrix[7] + this.modelviewMatrix[10] * this.projectionMatrix[11] + this.modelviewMatrix[11] * this.projectionMatrix[15];
        this.clippingMatrix[12] = this.modelviewMatrix[12] * this.projectionMatrix[0] + this.modelviewMatrix[13] * this.projectionMatrix[4] + this.modelviewMatrix[14] * this.projectionMatrix[8] + this.modelviewMatrix[15] * this.projectionMatrix[12];
        this.clippingMatrix[13] = this.modelviewMatrix[12] * this.projectionMatrix[1] + this.modelviewMatrix[13] * this.projectionMatrix[5] + this.modelviewMatrix[14] * this.projectionMatrix[9] + this.modelviewMatrix[15] * this.projectionMatrix[13];
        this.clippingMatrix[14] = this.modelviewMatrix[12] * this.projectionMatrix[2] + this.modelviewMatrix[13] * this.projectionMatrix[6] + this.modelviewMatrix[14] * this.projectionMatrix[10] + this.modelviewMatrix[15] * this.projectionMatrix[14];
        this.clippingMatrix[15] = this.modelviewMatrix[12] * this.projectionMatrix[3] + this.modelviewMatrix[13] * this.projectionMatrix[7] + this.modelviewMatrix[14] * this.projectionMatrix[11] + this.modelviewMatrix[15] * this.projectionMatrix[15];
        this.frustum[0][0] = this.clippingMatrix[3] - this.clippingMatrix[0];
        this.frustum[0][1] = this.clippingMatrix[7] - this.clippingMatrix[4];
        this.frustum[0][2] = this.clippingMatrix[11] - this.clippingMatrix[8];
        this.frustum[0][3] = this.clippingMatrix[15] - this.clippingMatrix[12];
        this.normalize(this.frustum, 0);
        this.frustum[1][0] = this.clippingMatrix[3] + this.clippingMatrix[0];
        this.frustum[1][1] = this.clippingMatrix[7] + this.clippingMatrix[4];
        this.frustum[1][2] = this.clippingMatrix[11] + this.clippingMatrix[8];
        this.frustum[1][3] = this.clippingMatrix[15] + this.clippingMatrix[12];
        this.normalize(this.frustum, 1);
        this.frustum[2][0] = this.clippingMatrix[3] + this.clippingMatrix[1];
        this.frustum[2][1] = this.clippingMatrix[7] + this.clippingMatrix[5];
        this.frustum[2][2] = this.clippingMatrix[11] + this.clippingMatrix[9];
        this.frustum[2][3] = this.clippingMatrix[15] + this.clippingMatrix[13];
        this.normalize(this.frustum, 2);
        this.frustum[3][0] = this.clippingMatrix[3] - this.clippingMatrix[1];
        this.frustum[3][1] = this.clippingMatrix[7] - this.clippingMatrix[5];
        this.frustum[3][2] = this.clippingMatrix[11] - this.clippingMatrix[9];
        this.frustum[3][3] = this.clippingMatrix[15] - this.clippingMatrix[13];
        this.normalize(this.frustum, 3);
        this.frustum[4][0] = this.clippingMatrix[3] - this.clippingMatrix[2];
        this.frustum[4][1] = this.clippingMatrix[7] - this.clippingMatrix[6];
        this.frustum[4][2] = this.clippingMatrix[11] - this.clippingMatrix[10];
        this.frustum[4][3] = this.clippingMatrix[15] - this.clippingMatrix[14];
        this.normalize(this.frustum, 4);
        this.frustum[5][0] = this.clippingMatrix[3] + this.clippingMatrix[2];
        this.frustum[5][1] = this.clippingMatrix[7] + this.clippingMatrix[6];
        this.frustum[5][2] = this.clippingMatrix[11] + this.clippingMatrix[10];
        this.frustum[5][3] = this.clippingMatrix[15] + this.clippingMatrix[14];
        this.normalize(this.frustum, 5);
    }
}
