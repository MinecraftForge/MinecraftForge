package net.minecraft.client.model;

import net.minecraft.util.Vec3;

public class PositionTextureVertex
{
    public Vec3 vector3D;
    public float texturePositionX;
    public float texturePositionY;
    private static final String __OBFID = "CL_00000862";

    public PositionTextureVertex(float par1, float par2, float par3, float par4, float par5)
    {
        this(Vec3.createVectorHelper((double)par1, (double)par2, (double)par3), par4, par5);
    }

    public PositionTextureVertex setTexturePosition(float par1, float par2)
    {
        return new PositionTextureVertex(this, par1, par2);
    }

    public PositionTextureVertex(PositionTextureVertex par1PositionTextureVertex, float par2, float par3)
    {
        this.vector3D = par1PositionTextureVertex.vector3D;
        this.texturePositionX = par2;
        this.texturePositionY = par3;
    }

    public PositionTextureVertex(Vec3 par1Vec3, float par2, float par3)
    {
        this.vector3D = par1Vec3;
        this.texturePositionX = par2;
        this.texturePositionY = par3;
    }
}