package net.minecraft.src;

public class TexturedQuad
{
    public PositionTextureVertex[] vertexPositions;
    public int nVertices;
    private boolean invertNormal;

    public TexturedQuad(PositionTextureVertex[] par1ArrayOfPositionTextureVertex)
    {
        this.nVertices = 0;
        this.invertNormal = false;
        this.vertexPositions = par1ArrayOfPositionTextureVertex;
        this.nVertices = par1ArrayOfPositionTextureVertex.length;
    }

    public TexturedQuad(PositionTextureVertex[] par1ArrayOfPositionTextureVertex, int par2, int par3, int par4, int par5, float par6, float par7)
    {
        this(par1ArrayOfPositionTextureVertex);
        float var8 = 0.0F / par6;
        float var9 = 0.0F / par7;
        par1ArrayOfPositionTextureVertex[0] = par1ArrayOfPositionTextureVertex[0].setTexturePosition((float)par4 / par6 - var8, (float)par3 / par7 + var9);
        par1ArrayOfPositionTextureVertex[1] = par1ArrayOfPositionTextureVertex[1].setTexturePosition((float)par2 / par6 + var8, (float)par3 / par7 + var9);
        par1ArrayOfPositionTextureVertex[2] = par1ArrayOfPositionTextureVertex[2].setTexturePosition((float)par2 / par6 + var8, (float)par5 / par7 - var9);
        par1ArrayOfPositionTextureVertex[3] = par1ArrayOfPositionTextureVertex[3].setTexturePosition((float)par4 / par6 - var8, (float)par5 / par7 - var9);
    }

    public void flipFace()
    {
        PositionTextureVertex[] var1 = new PositionTextureVertex[this.vertexPositions.length];

        for (int var2 = 0; var2 < this.vertexPositions.length; ++var2)
        {
            var1[var2] = this.vertexPositions[this.vertexPositions.length - var2 - 1];
        }

        this.vertexPositions = var1;
    }

    public void draw(Tessellator par1Tessellator, float par2)
    {
        Vec3D var3 = this.vertexPositions[1].vector3D.subtract(this.vertexPositions[0].vector3D);
        Vec3D var4 = this.vertexPositions[1].vector3D.subtract(this.vertexPositions[2].vector3D);
        Vec3D var5 = var4.crossProduct(var3).normalize();
        par1Tessellator.startDrawingQuads();

        if (this.invertNormal)
        {
            par1Tessellator.setNormal(-((float)var5.xCoord), -((float)var5.yCoord), -((float)var5.zCoord));
        }
        else
        {
            par1Tessellator.setNormal((float)var5.xCoord, (float)var5.yCoord, (float)var5.zCoord);
        }

        for (int var6 = 0; var6 < 4; ++var6)
        {
            PositionTextureVertex var7 = this.vertexPositions[var6];
            par1Tessellator.addVertexWithUV((double)((float)var7.vector3D.xCoord * par2), (double)((float)var7.vector3D.yCoord * par2), (double)((float)var7.vector3D.zCoord * par2), (double)var7.texturePositionX, (double)var7.texturePositionY);
        }

        par1Tessellator.draw();
    }
}
