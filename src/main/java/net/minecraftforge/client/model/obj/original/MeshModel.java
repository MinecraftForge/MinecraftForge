package net.minecraftforge.client.model.obj.original;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelManager;

public class MeshModel {

    public List<Vector3f> positions;
    public List<Vector3f> normals;
    public List<Vector2f> texCoords;

    public List<MeshPart> parts;

    public MeshModel() {
        parts = new ArrayList<MeshPart>();
    }

    public void addPosition(float x, float y, float z) {
        if (positions == null)
            positions = new ArrayList<Vector3f>();
        positions.add(new Vector3f(x, y, z));
    }

    public void addNormal(float x, float y, float z) {
        if (normals == null)
            normals = new ArrayList<Vector3f>();
        normals.add(new Vector3f(x, y, z));
    }

    public void addTexCoords(float x, float y) {
        if (texCoords == null)
            texCoords = new ArrayList<Vector2f>();
        texCoords.add(new Vector2f(x, y));
    }

    public void addPart(MeshPart part) {
        parts.add(part);
    }

    private int getColorValue(Vector3f color) {
        int r = (int) color.x;
        int g = (int) color.y;
        int b = (int) color.z;
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    public List<BakedQuad> bakeModel(ModelManager manager) {
        List<BakedQuad> bakeList = new ArrayList<BakedQuad>();

        for (int j = 0; j < parts.size(); j++) {
            MeshPart part = parts.get(j);

            TextureAtlasSprite sprite = null;
            int color = (int) 0xFFFFFFFF;

            if (part.material != null) {
                if (part.material.DiffuseTextureMap != null) {
                    sprite = manager.getTextureMap().getAtlasSprite(part.material.DiffuseTextureMap);
                } else if (part.material.AmbientTextureMap != null) {
                    sprite = manager.getTextureMap().getAtlasSprite(part.material.AmbientTextureMap);
                }
                
                if (part.material.DiffuseColor != null) {
                    color = getColorValue(part.material.DiffuseColor);
                }
            }

            for (int i = 0; i < part.indices.size(); i += 4) {
                BakedQuad quad = bakeQuad(part, i, sprite, color);
                bakeList.add(quad);
            }
        }
        return bakeList;
    }

    private BakedQuad bakeQuad(MeshPart part, int startIndex, TextureAtlasSprite sprite, int color) {
        int[] faceData = new int[28];
        
        for (int i = 0; i < 4; i++) {
            Vector3f position = new Vector3f(0, 0, 0);
            Vector2f texCoord = new Vector2f(0, 0);
            int p = 0;
            int[] indices = part.indices.get(startIndex + i);

            if (positions != null)
                position = positions.get(indices[p++]);

            if (normals != null)
                p++; // normals not used by minecraft

            if (texCoords != null)
                texCoord = texCoords.get(indices[p++]);

            storeVertexData(faceData, i, position, texCoord, sprite, color);
        }
        
        return new BakedQuad(faceData, -1, FaceBakery.getFacingFromVertexData(faceData));
    }

    private static void storeVertexData(int[] faceData, int storeIndex, Vector3f position, Vector2f faceUV, TextureAtlasSprite sprite, int shadeColor) {
        int l = storeIndex * 7;
        
        faceData[l + 0] = Float.floatToRawIntBits(position.x);
        faceData[l + 1] = Float.floatToRawIntBits(position.y);
        faceData[l + 2] = Float.floatToRawIntBits(position.z);
        faceData[l + 3] = shadeColor;
        
        if (sprite != null) {
            faceData[l + 4] = Float.floatToRawIntBits(sprite.getInterpolatedU(faceUV.x * 16));
            faceData[l + 5] = Float.floatToRawIntBits(sprite.getInterpolatedV(faceUV.y * 16));
        } else {
            faceData[l + 4] = Float.floatToRawIntBits(faceUV.x);
            faceData[l + 5] = Float.floatToRawIntBits(faceUV.y);
        }
        
        faceData[l + 6] = 0;
    }
}
