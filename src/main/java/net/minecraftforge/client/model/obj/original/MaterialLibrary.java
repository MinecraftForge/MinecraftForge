package net.minecraftforge.client.model.obj.original;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import javax.vecmath.Vector3f;

import com.google.common.base.Charsets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

public class MaterialLibrary extends Dictionary<String, Material> {
    static final Set<String> unknownCommands = new HashSet<String>();

    private final Dictionary<String, Material> materialLibrary = new Hashtable<String, Material>();

    private Material currentMaterial;

    public MaterialLibrary() {}

    @Override
    public int size() {
        return materialLibrary.size();
    }

    @Override
    public boolean isEmpty() {
        return materialLibrary.isEmpty();
    }

    @Override
    public Enumeration<String> keys() {
        return materialLibrary.keys();
    }

    @Override
    public Enumeration<Material> elements() {
        return materialLibrary.elements();
    }

    @Override
    public Material get(Object key) {
        return materialLibrary.get(key);
    }

    @Override
    public Material put(String key, Material value) {
        return materialLibrary.put(key, value);
    }

    @Override
    public Material remove(Object key) {
        return materialLibrary.remove(key);
    }

    public void loadFromStream(ResourceLocation loc) throws IOException {
        IResource res = Minecraft.getMinecraft().getResourceManager().getResource(loc);
        InputStreamReader lineStream = new InputStreamReader(res.getInputStream(), Charsets.UTF_8);
        BufferedReader lineReader = new BufferedReader(lineStream);

        for (; ; ) {
            String currentLine = lineReader.readLine();
            
            if (currentLine == null) {
                break;
            }

            if (currentLine.length() == 0 || currentLine.startsWith("#")) {
                continue;
            }

            String[] fields = currentLine.split(" ", 2);
            String keyword = fields[0];
            String data = fields[1];

            if (keyword.equalsIgnoreCase("newmtl")) {
                pushMaterial(data);
            } else if (keyword.equalsIgnoreCase("Ka")) {
                currentMaterial.AmbientColor = parseVector3f(data);
            } else if (keyword.equalsIgnoreCase("Kd")) {
                currentMaterial.DiffuseColor = parseVector3f(data);
            } else if (keyword.equalsIgnoreCase("Ks")) {
                currentMaterial.SpecularColor = parseVector3f(data);
            } else if (keyword.equalsIgnoreCase("Ns")) {
                currentMaterial.SpecularCoefficient = parseInt(data);
            } else if (keyword.equalsIgnoreCase("Tr")) {
                currentMaterial.Transparency = parseFloat(data);
            } else if (keyword.equalsIgnoreCase("illum")) {
                currentMaterial.IlluminationModel = parseInt(data);
            } else if (keyword.equalsIgnoreCase("map_Ka")) {
                currentMaterial.AmbientTextureMap = data;
                ResourceLocation texture = new ResourceLocation(data);
                
                if(!texture.getResourcePath().contains("item")) {
//                    ElementsOfPower.modelRegistrationHelper.registerItemSprite(texture);
                } else {
//                    ElementsOfPower.modelRegistrationHelper.registerBlockSprite(texture);
                }
            } else if (keyword.equalsIgnoreCase("map_Kd")) {
                currentMaterial.DiffuseTextureMap = data;
                ResourceLocation texture = new ResourceLocation(data);
                
                if(!texture.getResourcePath().contains("item")) {
//                    ElementsOfPower.modelRegistrationHelper.registerItemSprite(texture);
                } else {
//                    ElementsOfPower.modelRegistrationHelper.registerBlockSprite(texture);
                }
            } else if (keyword.equalsIgnoreCase("map_Ks")) {
                currentMaterial.SpecularTextureMap = data;
            } else if (keyword.equalsIgnoreCase("map_Ns")) {
                currentMaterial.SpecularHighlightTextureMap = data;
            } else if (keyword.equalsIgnoreCase("map_d")) {
                currentMaterial.AlphaTextureMap = data;
            } else if (keyword.equalsIgnoreCase("map_bump")) {
                currentMaterial.BumpMap = data;
            } else if (keyword.equalsIgnoreCase("bump")) {
                currentMaterial.BumpMap = data;
            } else if (keyword.equalsIgnoreCase("disp")) {
                currentMaterial.DisplacementMap = data;
            } else if (keyword.equalsIgnoreCase("decal")) {
                currentMaterial.StencilDecalMap = data;
            } else {
                if(!unknownCommands.contains(keyword)) {
                    System.out.println("Unrecognized command: " + currentLine);
                    unknownCommands.add(keyword);
                }
                
                continue;
            }
        }
    }

    private float parseFloat(String data) {
        return Float.parseFloat(data);
    }

    private int parseInt(String data) {
        return Integer.parseInt(data);
    }

    private Vector3f parseVector3f(String data) {
        String[] parts = data.split(" ");
        return new Vector3f(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2]));
    }

    private void pushMaterial(String materialName) {
        currentMaterial = new Material(materialName);
        materialLibrary.put(currentMaterial.Name, currentMaterial);
    }
}
