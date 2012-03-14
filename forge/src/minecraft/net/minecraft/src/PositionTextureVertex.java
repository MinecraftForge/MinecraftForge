package net.minecraft.src;

public class PositionTextureVertex
{
    public Vec3D vector3D;
    public float texturePositionX;
    public float texturePositionY;

    public PositionTextureVertex(float par1, float par2, float par3, float par4, float par5)
    {
        this(Vec3D.createVectorHelper((double)par1, (double)par2, (double)par3), par4, par5);
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

    public PositionTextureVertex(Vec3D par1Vec3D, float par2, float par3)
    {
        this.vector3D = par1Vec3D;
        this.texturePositionX = par2;
        this.texturePositionY = par3;
    }
}
