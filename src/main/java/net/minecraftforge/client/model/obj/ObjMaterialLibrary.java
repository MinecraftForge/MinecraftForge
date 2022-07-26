/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.obj;

import com.google.common.collect.Maps;
import com.mojang.math.Vector4f;
import joptsimple.internal.Strings;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * An OBJ material library (MTL), composed of named {@link Material materials}.
 */
public class ObjMaterialLibrary
{
    public static final ObjMaterialLibrary EMPTY = new ObjMaterialLibrary();
    final Map<String, Material> materials = Maps.newHashMap();

    private ObjMaterialLibrary()
    {
    }

    public ObjMaterialLibrary(ObjTokenizer reader) throws IOException
    {
        Material currentMaterial = null;

        String[] line;
        while ((line = reader.readAndSplitLine(true)) != null)
        {
            switch (line[0])
            {
                case "newmtl":
                {
                    String name = Strings.join(Arrays.copyOfRange(line, 1, line.length), " ");
                    currentMaterial = new Material(name);
                    materials.put(name, currentMaterial);
                    break;
                }

                case "Ka":
                    currentMaterial.ambientColor = ObjModel.parseVector4(line);
                    break;

                case "map_Ka":
                    // Ignores all options params
                    currentMaterial.ambientColorMap = line[line.length - 1];
                    break;

                case "Kd":
                    currentMaterial.diffuseColor = ObjModel.parseVector4(line);
                    break;

                case "forge_TintIndex":
                    currentMaterial.diffuseTintIndex = Integer.parseInt(line[1]);
                    break;

                case "map_Kd":
                    // Ignores all options params
                    currentMaterial.diffuseColorMap = line[line.length - 1];
                    break;

                case "Ks":
                    currentMaterial.specularColor = ObjModel.parseVector4(line);
                    break;

                case "Ns":
                    currentMaterial.specularHighlight = Float.parseFloat(line[1]);
                    break;

                case "map_Ks":
                    // Ignores all options params
                    currentMaterial.specularColorMap = line[line.length - 1];
                    break;

                case "d":
                    // Ignores all options params
                    currentMaterial.dissolve = Float.parseFloat(line[1]);
                    break;
                case "Tr":
                    // Ignores all options params
                    currentMaterial.transparency = Float.parseFloat(line[1]);
                    break;
            }
        }
    }

    public Material getMaterial(String mat)
    {
        if (!materials.containsKey(mat))
            throw new NoSuchElementException("The material was not found in the library: " + mat);
        return materials.get(mat);
    }

    public static class Material
    {
        public final String name;
        public Vector4f ambientColor = new Vector4f(0, 0, 0, 1);
        public String ambientColorMap;
        public Vector4f diffuseColor = new Vector4f(1, 1, 1, 1);
        public String diffuseColorMap;
        public Vector4f specularColor = new Vector4f(0, 0, 0, 1);
        public float specularHighlight = 0;
        public String specularColorMap;

        public float dissolve = 1.0f;
        public float transparency = 0.0f;

        // non-standard
        public int diffuseTintIndex = 0;

        public Material(String name)
        {
            this.name = name;
        }
    }
}
