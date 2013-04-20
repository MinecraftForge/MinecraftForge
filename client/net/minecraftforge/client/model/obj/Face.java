package net.minecraftforge.client.model.obj;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.util.Vec3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Face
{
    public Vertex[] vertices;
    public Vertex[] vertexNormals;
    public Vertex faceNormal;
    public TextureCoordinate[] textureCoordinates;
    public TextureCoordinate[] scaledTextureCoordinates;

    public void addFaceForRender(Tessellator tessellator)
    {
        addFaceForRender(tessellator, 0.0005F);
    }

    public void addFaceForRender(Tessellator tessellator, float textureOffset)
    {
        if (faceNormal == null)
        {
            faceNormal = this.calculateFaceNormal();
        }

        tessellator.setNormal(faceNormal.x, faceNormal.y, faceNormal.z);

        float averageU = 0F;
        float averageV = 0F;

        if ((textureCoordinates != null) && (textureCoordinates.length > 0))
        {
            for (int i = 0; i < textureCoordinates.length; ++i)
            {
                averageU += textureCoordinates[i].u;
                averageV += textureCoordinates[i].v;
            }

            averageU = averageU / textureCoordinates.length;
            averageV = averageV / textureCoordinates.length;
        }

        float offsetU, offsetV;

        for (int i = 0; i < vertices.length; ++i)
        {

            if ((textureCoordinates != null) && (textureCoordinates.length > 0))
            {
                offsetU = textureOffset;
                offsetV = textureOffset;

                if (textureCoordinates[i].u > averageU)
                {
                    offsetU = -offsetU;
                }
                if (textureCoordinates[i].v > averageV)
                {
                    offsetV = -offsetV;
                }

                tessellator.addVertexWithUV(vertices[i].x, vertices[i].y, vertices[i].z, textureCoordinates[i].u + offsetU, textureCoordinates[i].v + offsetV);
            }
            else
            {
                tessellator.addVertex(vertices[i].x, vertices[i].y, vertices[i].z);
            }
        }
    }

    public void addFaceForRender(Tessellator tessellator, Icon icon)
    {
        if (faceNormal == null)
        {
            faceNormal = this.calculateFaceNormal();
        }

        tessellator.setNormal(faceNormal.x, faceNormal.y, faceNormal.z);
        
        //If there are no scaled coordinates for this face, but there are texture coordinates for the face compute and cache the coordinates.
        if(scaledTextureCoordinates == null && textureCoordinates != null)
        {
            scaledTextureCoordinates = new TextureCoordinate[textureCoordinates.length];
            
            for(int i = 0; i < textureCoordinates.length; ++i)
            {
                double u = textureCoordinates[i].u;
                double v = textureCoordinates[i].v;
                double interpolatedU = icon.getInterpolatedU((Math.floor(u) == u ? 0 : (u - (Math.ceil(u) - 1))) * 16);
                double interpolatedV = icon.getInterpolatedV((Math.floor(v) == v ? 0 : (v - (Math.ceil(v) - 1))) * 16);
                
                scaledTextureCoordinates[i] = new TextureCoordinate((float)interpolatedU, (float)interpolatedV);
            }
        }
        
        for (int i = 0; i < vertices.length; ++i)
        {
            if (scaledTextureCoordinates != null && scaledTextureCoordinates.length != 0)
            {
                tessellator.addVertexWithUV(vertices[i].x, vertices[i].y, vertices[i].z, scaledTextureCoordinates[i].u, scaledTextureCoordinates[i].v);
            }
            else
            {
                tessellator.addVertex(vertices[i].x, vertices[i].y, vertices[i].z);
            }
        }
    }

    public Vertex calculateFaceNormal()
    {
        Vec3 v1 = Vec3.createVectorHelper(vertices[1].x - vertices[0].x, vertices[1].y - vertices[0].y, vertices[1].z - vertices[0].z);
        Vec3 v2 = Vec3.createVectorHelper(vertices[2].x - vertices[0].x, vertices[2].y - vertices[0].y, vertices[2].z - vertices[0].z);
        Vec3 normalVector = null;

        normalVector = v1.crossProduct(v2).normalize();

        return new Vertex((float) normalVector.xCoord, (float) normalVector.yCoord, (float) normalVector.zCoord);
    }
}