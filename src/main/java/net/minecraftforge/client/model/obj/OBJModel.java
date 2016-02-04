package net.minecraftforge.client.model.obj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.*;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.common.FMLLog;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@SuppressWarnings("deprecation")
public class OBJModel implements IRetexturableModel<OBJModel>, IModelCustomData<OBJModel>
{
    //private Gson GSON = new GsonBuilder().create();
    private MaterialLibrary matLib;
    private final ResourceLocation modelLocation;
    private CustomData customData;

    public OBJModel(MaterialLibrary matLib, ResourceLocation modelLocation)
    {
        this(matLib, modelLocation, new CustomData());
    }

    public OBJModel(MaterialLibrary matLib, ResourceLocation modelLocation, CustomData customData)
    {
        this.matLib = matLib;
        this.modelLocation = modelLocation;
        this.customData = customData;
    }

    @Override
    public Collection<ResourceLocation> getDependencies()
    {
        return Collections.emptyList();
    }

    @Override
    public Collection<ResourceLocation> getTextures()
    {
        Iterator<Material> materialIterator = this.matLib.materials.values().iterator();
        List<ResourceLocation> textures = Lists.newArrayList();
        while (materialIterator.hasNext())
        {
            Material mat = materialIterator.next();
            ResourceLocation textureLoc = new ResourceLocation(mat.getTexture().getPath());
            if (!textures.contains(textureLoc) && !mat.isWhite())
                textures.add(textureLoc);
        }
        return textures;
    }

    @Override
    public IFlexibleBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        ImmutableMap.Builder<String, TextureAtlasSprite> builder = ImmutableMap.builder();
        builder.put(ModelLoader.White.loc.toString(), ModelLoader.White.instance);
        TextureAtlasSprite missing = bakedTextureGetter.apply(new ResourceLocation("missingno"));
        for (Map.Entry<String, Material> e : matLib.materials.entrySet())
        {
            if (e.getValue().getTexture().getTextureLocation().getResourcePath().startsWith("#"))
            {
                FMLLog.severe("OBJLoader: Unresolved texture '%s' for obj model '%s'", e.getValue().getTexture().getTextureLocation().getResourcePath(), modelLocation);
                builder.put(e.getKey(), missing);
            }
            else
            {
                builder.put(e.getKey(), bakedTextureGetter.apply(e.getValue().getTexture().getTextureLocation()));
            }
        }
        builder.put("missingno", missing);
        return new OBJBakedModel(this, state, format, builder.build());
    }

    public MaterialLibrary getMatLib()
    {
        return this.matLib;
    }

    @Override
    public IModel process(ImmutableMap<String, String> customData)
    {
        OBJModel ret = new OBJModel(this.matLib, this.modelLocation, new CustomData(this.customData, customData));
        return ret;
    }

    @Override
    public IModel retexture(ImmutableMap<String, String> textures)
    {
        OBJModel ret = new OBJModel(this.matLib.makeLibWithReplacements(textures), this.modelLocation, this.customData);
        return ret;
    }

    static class CustomData
    {
        public boolean ambientOcclusion = true;
        public boolean gui3d = true;
        // should be an enum, TODO
        //public boolean modifyUVs = false;
        public boolean flipV = false;

        public CustomData(CustomData parent, ImmutableMap<String, String> customData)
        {
            this.ambientOcclusion = parent.ambientOcclusion;
            this.gui3d = parent.gui3d;
            this.flipV = parent.flipV;
            this.process(customData);
        }

        public CustomData() {}

        public void process(ImmutableMap<String, String> customData)
        {
            for (Map.Entry<String, String> e : customData.entrySet())
            {
                if (e.getKey().equals("ambient"))
                    this.ambientOcclusion = Boolean.valueOf(e.getValue());
                else if (e.getKey().equals("gui3d"))
                    this.gui3d = Boolean.valueOf(e.getValue());
                /*else if (e.getKey().equals("modifyUVs"))
                    this.modifyUVs = Boolean.valueOf(e.getValue());*/
                else if (e.getKey().equals("flip-v"))
                    this.flipV = Boolean.valueOf(e.getValue());
            }
        }
    }

    public static class Parser
    {
        private static final Pattern WHITE_SPACE = Pattern.compile("\\s+");
        private static Set<String> unknownObjectCommands = new HashSet<String>();
        public MaterialLibrary materialLibrary = new MaterialLibrary();
        private IResourceManager manager;
        private InputStreamReader objStream;
        private BufferedReader objReader;
        private ResourceLocation objFrom;

        private List<String> groupList = Lists.newArrayList();
        private List<Vertex> vertices = Lists.newArrayList();
        private List<Normal> normals = Lists.newArrayList();
        private List<TextureCoordinate> texCoords = Lists.newArrayList();

        public Parser(IResource from, IResourceManager manager) throws IOException
        {
            this.manager = manager;
            this.objFrom = from.getResourceLocation();
            this.objStream = new InputStreamReader(from.getInputStream(), Charsets.UTF_8);
            this.objReader = new BufferedReader(objStream);
        }

        public List<String> getElements()
        {
            return this.groupList;
        }

        public OBJModel parse() throws IOException
        {
            String currentLine = "";
            Material material = new Material();
            material.setName(Material.DEFAULT_NAME);
            int usemtlCounter = 0;

//            float[] minUVBounds = new float[] {0.0f, 0.0f};
//            float[] maxUVBounds = new float[] {1.0f, 1.0f};

            for (;;)
            {
                currentLine = objReader.readLine();
                if (currentLine == null) break;
                currentLine.trim();
                if (currentLine.isEmpty() || currentLine.startsWith("#")) continue;

                String[] fields = WHITE_SPACE.split(currentLine, 2);
                String key = fields[0];
                String data = fields[1];
                String[] splitData = WHITE_SPACE.split(data);

                if (key.equalsIgnoreCase("mtllib"))
                    this.materialLibrary.parseMaterials(manager, data, objFrom);
                else if (key.equalsIgnoreCase("usemtl"))
                {
                    material = this.materialLibrary.materials.get(data);
                    usemtlCounter++;
                }
                else if (key.equalsIgnoreCase("v"))
                {
                    float[] floatSplitData = new float[splitData.length];
                    for (int i = 0; i < splitData.length; i++)
                        floatSplitData[i] = Float.parseFloat(splitData[i]);
                    Vector4f pos = new Vector4f(floatSplitData[0], floatSplitData[1], floatSplitData[2], floatSplitData.length == 4 ? floatSplitData[3] : 1);
                    Vertex vertex = new Vertex(pos, material);
                    this.vertices.add(vertex);
                }
                else if (key.equalsIgnoreCase("vn"))
                {
                    float[] floatSplitData = new float[splitData.length];
                    for (int i = 0; i < splitData.length; i++)
                        floatSplitData[i] = Float.parseFloat(splitData[i]);
                    Normal normal = new Normal(floatSplitData);
                    this.normals.add(normal);
                }
                else if (key.equalsIgnoreCase("vt"))
                {
                    float[] floatSplitData = new float[splitData.length];
                    for (int i = 0; i < splitData.length; i++)
                        floatSplitData[i] = Float.parseFloat(splitData[i]);
                    TextureCoordinate texCoord = new TextureCoordinate(new Vector3f(floatSplitData[0], floatSplitData[1], floatSplitData.length == 3 ? floatSplitData[2] : 1));
                    if (texCoord.u < 0.0f || texCoord.u > 1.0f || texCoord.v < 0.0f || texCoord.v > 1.0f)
                        throw new UVsOutOfBoundsException(this.objFrom);
//                    this.UVsOutOfBounds = (texCoord.u < 0.0f || texCoord.u > 1.0f || texCoord.v < 0.0f || texCoord.v > 1.0f);

//                    if (texCoord.u < 0.0f || texCoord.u > 1.0f || texCoord.v < 0.0f || texCoord.v > 1.0f)
//                    {
//                        this.UVsOutOfBounds = true;
//                        texCoord.u -= Math.floor(texCoord.u);
//                        texCoord.v -= Math.floor(texCoord.v);
//                    }

//                    minUVBounds[0] = floatSplitData[0] < minUVBounds[0] ? floatSplitData[0] : minUVBounds[0];
//                    minUVBounds[1] = floatSplitData[1] < minUVBounds[1] ? floatSplitData[1] : minUVBounds[1];
//                    maxUVBounds[0] = floatSplitData[0] > maxUVBounds[0] ? floatSplitData[0] : maxUVBounds[0];
//                    maxUVBounds[1] = floatSplitData[1] > maxUVBounds[1] ? floatSplitData[1] : maxUVBounds[1];
//                    FMLLog.info("u: [%f, %f] v: [%f, %f]", minUVBounds[]);
                    this.texCoords.add(texCoord);
                }
                else if (key.equalsIgnoreCase("f"))
                {
                    String[][] splitSlash = new String[splitData.length][];
                    if (splitData.length > 4) FMLLog.warning("OBJModel.Parser: found a face ('f') with more than 4 vertices, only the first 4 of these vertices will be rendered!");

                    int vert = 0;
                    int texCoord = 0;
                    int norm = 0;

                    List<Vertex> v = Lists.newArrayListWithCapacity(splitData.length);
//                    List<TextureCoordinate> t = Lists.newArrayListWithCapacity(splitData.length);
//                    List<Normal> n = Lists.newArrayListWithCapacity(splitData.length);

                    for (int i = 0; i < splitData.length; i++)
                    {
                        if (splitData[i].contains("//"))
                        {
                            splitSlash[i] = splitData[i].split("//");

                            vert = Integer.parseInt(splitSlash[i][0]);
                            vert = vert < 0 ? this.vertices.size() - 1 : vert - 1;
                            norm = Integer.parseInt(splitSlash[i][1]);
                            norm = norm < 0 ? this.normals.size() - 1 : norm - 1;

                            Vertex newV = new Vertex(new Vector4f(this.vertices.get(vert).getPos()), this.vertices.get(vert).getMaterial());
                            newV.setNormal(this.normals.get(norm));

                            v.add(newV);
//                            n.add(this.normals.get(norm));
                        }
                        else if (splitData[i].contains("/"))
                        {
                            splitSlash[i] = splitData[i].split("/");

                            vert = Integer.parseInt(splitSlash[i][0]);
                            vert = vert < 0 ? this.vertices.size() - 1 : vert - 1;
                            texCoord = Integer.parseInt(splitSlash[i][1]);
                            texCoord = texCoord < 0 ? this.texCoords.size() - 1 : texCoord - 1;
                            if (splitSlash[i].length > 2)
                            {
                                norm = Integer.parseInt(splitSlash[i][2]);
                                norm = norm < 0 ? this.normals.size() - 1 : norm - 1;
                            }

                            Vertex newV = new Vertex(new Vector4f(this.vertices.get(vert).getPos()), this.vertices.get(vert).getMaterial());
                            newV.setTextureCoordinate(this.texCoords.get(texCoord));
                            newV.setNormal(splitSlash[i].length > 2 ? this.normals.get(norm) : null);

                            v.add(newV);
//                            t.add(this.texCoords.get(texCoord));
//                            if (splitSlash[i].length > 2) n.add(this.normals.get(norm));
                        }
                        else
                        {
                            splitSlash[i] = splitData[i].split("");

                            vert = Integer.parseInt(splitSlash[i][0]);
                            vert = vert < 0 ? this.vertices.size() - 1 : vert - 1;

                            Vertex newV = new Vertex(new Vector4f(this.vertices.get(vert).getPos()), this.vertices.get(vert).getMaterial());
                            v.add(newV);
                        }
                    }

                    Vertex[] va = new Vertex[v.size()];
                    v.toArray(va);
//                    TextureCoordinate[] ta = new TextureCoordinate[t.size()];
//                    t.toArray(ta);
//                    Normal[] na = new Normal[n.size()];
//                    n.toArray(na);
                    Face face = new Face(va, material.name);
                    if (usemtlCounter < this.vertices.size())
                    {
                        for (Vertex ver : face.getVertices())
                        {
                            ver.setMaterial(material);
                        }
                    }

                    if (groupList.isEmpty())
                    {
                        if (this.materialLibrary.getGroups().containsKey(Group.DEFAULT_NAME))
                        {
                            this.materialLibrary.getGroups().get(Group.DEFAULT_NAME).addFace(face);
                        }
                        else
                        {
                            Group def = new Group(Group.DEFAULT_NAME, null);
                            def.addFace(face);
                            this.materialLibrary.getGroups().put(Group.DEFAULT_NAME, def);
                        }
                    }
                    else
                    {
                        for (String s : groupList)
                        {
                            if (this.materialLibrary.getGroups().containsKey(s))
                            {
                                this.materialLibrary.getGroups().get(s).addFace(face);
                            }
                            else
                            {
                                Group e = new Group(s, null);
                                e.addFace(face);
                                this.materialLibrary.getGroups().put(s, e);
                            }
                        }
                    }
                }
                else if (key.equalsIgnoreCase("g") || key.equalsIgnoreCase("o"))
                {
                    groupList.clear();
                    if (key.equalsIgnoreCase("g"))
                    {
                        String[] splitSpace = data.split(" ");
                        for (String s : splitSpace)
                            groupList.add(s);
                    }
                    else
                    {
                        groupList.add(data);
                    }
                }
                else
                {
                    if (!unknownObjectCommands.contains(key))
                    {
                        unknownObjectCommands.add(key);
                        FMLLog.info("OBJLoader.Parser: command '%s' (model: '%s') is not currently supported, skipping", key, objFrom);
                    }
                }
            }

            OBJModel model = new OBJModel(this.materialLibrary, this.objFrom);
//            model.getMatLib().setUVBounds(minUVBounds[0], maxUVBounds[0], minUVBounds[1], maxUVBounds[1]);
            return model;
        }
    }

    public static class MaterialLibrary
    {
        private static final Pattern WHITE_SPACE = Pattern.compile("\\s+");
        private Set<String> unknownMaterialCommands = new HashSet<String>();
        private Map<String, Material> materials = new HashMap<String, Material>();
        private Map<String, Group> groups = new HashMap<String, Group>();
        private InputStreamReader mtlStream;
        private BufferedReader mtlReader;

//        private float[] minUVBounds = new float[] {0.0f, 0.0f};
//        private float[] maxUVBounds = new float[] {1.0f, 1.0f};

        public MaterialLibrary()
        {
            this.groups.put(Group.DEFAULT_NAME, new Group(Group.DEFAULT_NAME, null));
            Material def = new Material();
            def.setName(Material.DEFAULT_NAME);
            this.materials.put(Material.DEFAULT_NAME, def);
        }

        public MaterialLibrary makeLibWithReplacements(ImmutableMap<String, String> replacements)
        {
            Map<String, Material> mats = new HashMap<String, Material>();
            for (Map.Entry<String, Material> e : this.materials.entrySet())
            {
                // key for the material name, with # added if missing
                String keyMat = e.getKey();
                if(!keyMat.startsWith("#")) keyMat = "#" + keyMat;
                // key for the texture name, with ".png" stripped and # added if missing
                String keyTex = e.getValue().getTexture().getPath();
                if(keyTex.endsWith(".png")) keyTex = keyTex.substring(0, keyTex.length() - ".png".length());
                if(!keyTex.startsWith("#")) keyTex = "#" + keyTex;
                if (replacements.containsKey(keyMat))
                {
                    Texture currentTexture = e.getValue().texture;
                    Texture replacementTexture = new Texture(replacements.get(keyMat), currentTexture.position, currentTexture.scale, currentTexture.rotation);
                    Material replacementMaterial = new Material(e.getValue().color, replacementTexture, e.getValue().name);
                    mats.put(e.getKey(), replacementMaterial);
                }
                else if (replacements.containsKey(keyTex))
                {
                    Texture currentTexture = e.getValue().texture;
                    Texture replacementTexture = new Texture(replacements.get(keyTex), currentTexture.position, currentTexture.scale, currentTexture.rotation);
                    Material replacementMaterial = new Material(e.getValue().color, replacementTexture, e.getValue().name);
                    mats.put(e.getKey(), replacementMaterial);
                }
                else
                {
                    mats.put(e.getKey(), e.getValue());
                }
            }
            MaterialLibrary ret = new MaterialLibrary();
            ret.unknownMaterialCommands = this.unknownMaterialCommands;
            ret.materials = mats;
            ret.groups = this.groups;
            ret.mtlStream = this.mtlStream;
            ret.mtlReader = this.mtlReader;
//            ret.minUVBounds = this.minUVBounds;
//            ret.maxUVBounds = this.maxUVBounds;
            return ret;
        }

//        public float[] getMinUVBounds()
//        {
//            return this.minUVBounds;
//        }

//        public float[] getMaxUVBounds()
//        {
//            return this.maxUVBounds;
//        }

//        public void setUVBounds(float minU, float maxU, float minV, float maxV)
//        {
//            this.minUVBounds[0] = minU;
//            this.maxUVBounds[0] = maxU;
//            this.minUVBounds[1] = minV;
//            this.maxUVBounds[1] = maxV;
//        }

        public Map<String, Group> getGroups()
        {
            return this.groups;
        }

        public List<Group> getGroupsContainingFace(Face f)
        {
            List<Group> groupList = Lists.newArrayList();
            for (Group g : this.groups.values())
            {
                if (g.faces.contains(f)) groupList.add(g);
            }
            return groupList;
        }

        public void changeMaterialColor(String name, int color)
        {
            Vector4f colorVec = new Vector4f();
            colorVec.w = (color >> 24 & 255) / 255;
            colorVec.x = (color >> 16 & 255) / 255;
            colorVec.y = (color >> 8 & 255) / 255;
            colorVec.z = (color & 255) / 255;
            this.materials.get(name).setColor(colorVec);
        }

        public Material getMaterial(String name)
        {
            return this.materials.get(name);
        }

        public ImmutableList<String> getMaterialNames()
        {
            return ImmutableList.copyOf(this.materials.keySet());
        }

        public void parseMaterials(IResourceManager manager, String path, ResourceLocation from) throws IOException
        {
            this.materials.clear();
            boolean hasSetTexture = false;
            boolean hasSetColor = false;
            String domain = from.getResourceDomain();
            if (!path.contains("/"))
                path = from.getResourcePath().substring(0, from.getResourcePath().lastIndexOf("/") + 1) + path;
            mtlStream = new InputStreamReader(manager.getResource(new ResourceLocation(domain, path)).getInputStream(), Charsets.UTF_8);
            mtlReader = new BufferedReader(mtlStream);

            String currentLine = "";
            Material material = new Material();
            material.setName(Material.WHITE_NAME);
            material.setTexture(Texture.WHITE);
            this.materials.put(Material.WHITE_NAME, material);
            this.materials.put(Material.DEFAULT_NAME, new Material(Texture.WHITE));

            for (;;)
            {
                currentLine = mtlReader.readLine();
                if (currentLine == null) break;
                currentLine.trim();
                if (currentLine.isEmpty() || currentLine.startsWith("#")) continue;

                String[] fields = WHITE_SPACE.split(currentLine, 2);
                String key = fields[0];
                String data = fields[1];

                if (key.equalsIgnoreCase("newmtl"))
                {
                    hasSetColor = false;
                    hasSetTexture = false;
                    material = new Material();
                    material.setName(data);
                    this.materials.put(data, material);
                }
                else if (key.equalsIgnoreCase("Ka") || key.equalsIgnoreCase("Kd") || key.equalsIgnoreCase("Ks"))
                {
                    if (key.equalsIgnoreCase("Kd") || !hasSetColor)
                    {
                        String[] rgbStrings = WHITE_SPACE.split(data, 3);
                        Vector4f color = new Vector4f(Float.parseFloat(rgbStrings[0]), Float.parseFloat(rgbStrings[1]), Float.parseFloat(rgbStrings[2]), 1.0f);
                        hasSetColor = true;
                        material.setColor(color);
                    }
                    else
                    {
                        FMLLog.info("OBJModel: A color has already been defined for material '%s' in '%s'. The color defined by key '%s' will not be applied!", material.getName(), new ResourceLocation(domain, path).toString(), key);
                    }
                }
                else if (key.equalsIgnoreCase("map_Ka") || key.equalsIgnoreCase("map_Kd") || key.equalsIgnoreCase("map_Ks"))
                {
                    if (key.equalsIgnoreCase("map_Kd") || !hasSetTexture)
                    {
                        if (data.contains(" "))
                        {
                            String[] mapStrings = WHITE_SPACE.split(data);
                            String texturePath = mapStrings[mapStrings.length - 1];
                            Texture texture = new Texture(texturePath);
                            hasSetTexture = true;
                            material.setTexture(texture);
                        }
                        else
                        {
                            Texture texture = new Texture(data);
                            hasSetTexture = true;
                            material.setTexture(texture);
                        }
                    }
                    else
                    {
                        FMLLog.info("OBJModel: A texture has already been defined for material '%s' in '%s'. The texture defined by key '%s' will not be applied!", material.getName(), new ResourceLocation(domain, path).toString(), key);
                    }
                }
                else if (key.equalsIgnoreCase("d") || key.equalsIgnoreCase("Tr"))
                {
                    //d <-optional key here> float[0.0:1.0, 1.0]
                    //Tr r g b OR Tr spectral map file OR Tr xyz r g b (CIEXYZ colorspace)
                    String[] splitData = WHITE_SPACE.split(data);
                    float alpha = Float.parseFloat(splitData[splitData.length - 1]);
                    material.getColor().setW(alpha);
                }
                else
                {
                    if (!unknownMaterialCommands.contains(key))
                    {
                        unknownMaterialCommands.add(key);
                        FMLLog.info("OBJLoader.MaterialLibrary: key '%s' (model: '%s') is not currently supported, skipping", key, new ResourceLocation(domain, path));
                    }
                }
            }
        }
    }

    public static class Material
    {
        public static final String WHITE_NAME = "OBJModel.White.Texture.Name";
        public static final String DEFAULT_NAME = "OBJModel.Default.Texture.Name";
        private Vector4f color;
        private Texture texture = Texture.WHITE;
        private String name = DEFAULT_NAME;

        public Material()
        {
            this(new Vector4f(1f, 1f, 1f, 1f));
        }

        public Material(Vector4f color)
        {
            this(color, Texture.WHITE, WHITE_NAME);
        }

        public Material(Texture texture)
        {
            this(new Vector4f(1f, 1f, 1f, 1f), texture, DEFAULT_NAME);
        }

        public Material(Vector4f color, Texture texture, String name)
        {
            this.color = color;
            this.texture = texture;
            this.name = name != null ? name : DEFAULT_NAME;
        }

        public void setName(String name)
        {
            this.name = name != null ? name : DEFAULT_NAME;
        }

        public String getName()
        {
            return this.name;
        }

        public void setColor(Vector4f color)
        {
            this.color = color;
        }

        public Vector4f getColor()
        {
            return this.color;
        }

        public void setTexture(Texture texture)
        {
            this.texture = texture;
        }

        public Texture getTexture()
        {
            return this.texture;
        }

        public boolean isWhite()
        {
            return this.texture.equals(Texture.WHITE);
        }

        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder(String.format("%nMaterial:%n"));
            builder.append(String.format("    Name: %s%n", this.name));
            builder.append(String.format("    Color: %s%n", this.color.toString()));
            builder.append(String.format("    Is White: %b%n", this.isWhite()));
            return builder.toString();
        }
    }

    public static class Texture
    {
        public static Texture WHITE = new Texture("builtin/white", new Vector2f(0, 0), new Vector2f(1, 1), 0);
        private String path;
        private Vector2f position;
        private Vector2f scale;
        private float rotation;

        public Texture(String path)
        {
            this(path, new Vector2f(0, 0), new Vector2f(1, 1), 0);
        }

        public Texture(String path, Vector2f position, Vector2f scale, float rotation)
        {
            this.path = path;
            this.position = position;
            this.scale = scale;
            this.rotation = rotation;
        }

        public ResourceLocation getTextureLocation()
        {
            ResourceLocation loc = new ResourceLocation(this.path);
            return loc;
        }

        public void setPath(String path)
        {
            this.path = path;
        }

        public String getPath()
        {
            return this.path;
        }

        public void setPosition(Vector2f position)
        {
            this.position = position;
        }

        public Vector2f getPosition()
        {
            return this.position;
        }

        public void setScale(Vector2f scale)
        {
            this.scale = scale;
        }

        public Vector2f getScale()
        {
            return this.scale;
        }

        public void setRotation(float rotation)
        {
            this.rotation = rotation;
        }

        public float getRotation()
        {
            return this.rotation;
        }
    }

    public static class Face
    {
        private Vertex[] verts = new Vertex[4];
//        private Normal[] norms = new Normal[4];
//        private TextureCoordinate[] texCoords = new TextureCoordinate[4];
        private String materialName = Material.DEFAULT_NAME;
        private boolean isTri = false;

        public Face(Vertex[] verts)
        {
            this(verts, Material.DEFAULT_NAME);
        }

        public Face(Vertex[] verts, String materialName) {
            this.verts = verts != null && verts.length > 2 ? verts : null;
            setMaterialName(materialName);
            checkData();
        }

//        public Face(Vertex[] verts, Normal[] norms)
//        {
//            this(verts, norms, null);
//        }

//        public Face(Vertex[] verts, TextureCoordinate[] texCoords)
//        {
//            this(verts, null, texCoords);
//        }

//        public Face(Vertex[] verts, Normal[] norms, TextureCoordinate[] texCoords)
//        {
//            this(verts, norms, texCoords, Material.DEFAULT_NAME);
//        }

//        public Face(Vertex[] verts, Normal[] norms, TextureCoordinate[] texCoords, String materialName)
//        {
//            this.verts = verts != null && verts.length > 2 ? verts : null;
//            this.norms = norms != null && norms.length > 2 ? norms : null;
//            this.texCoords = texCoords != null && texCoords.length > 2 ? texCoords : null;
//            setMaterialName(materialName);
//            checkData();
//        }

        private void checkData()
        {
            if (this.verts != null && this.verts.length == 3)
            {
                this.isTri = true;
                this.verts = new Vertex[]{this.verts[0], this.verts[1], this.verts[2], this.verts[2]};
            }
        }

        public void setMaterialName(String materialName)
        {
            this.materialName = materialName != null && !materialName.isEmpty() ? materialName : this.materialName;
        }

        public String getMaterialName()
        {
            return this.materialName;
        }
        
        public boolean isTriangles() 
        {
            return isTri;
        }

        public boolean setVertices(Vertex[] verts)
        {
            if (verts == null) return false;
            else this.verts = verts;
            checkData();
            return true;
        }

        public Vertex[] getVertices()
        {
            return this.verts;
        }

//        public boolean areUVsNormalized()
//        {
//            for (Vertex v : this.verts)
//                if (!v.hasNormalizedUVs())
//                    return false;
//            return true;
//        }

//        public void normalizeUVs(float[] min, float[] max)
//        {
//            if (!this.areUVsNormalized())
//            {
//                for (int i = 0; i < this.verts.length; i++) {
//                    TextureCoordinate texCoord = this.verts[i].getTextureCoordinate();
//                    min[0] = texCoord.u < min[0] ? texCoord.u : min[0];
//                    max[0] = texCoord.u > max[0] ? texCoord.u : max[0];
//                    min[1] = texCoord.v < min[1] ? texCoord.v : min[1];
//                    max[1] = texCoord.v > max[1] ? texCoord.v : max[1];
//                }
//
//                for (Vertex v : this.verts) {
//                    v.texCoord.u = (v.texCoord.u - min[0]) / (max[0] - min[0]);
//                    v.texCoord.v = (v.texCoord.v - min[1]) / (max[1] - max[1]);
//                }
//            }
//        }

        public Face bake(TRSRTransformation transform)
        {
            Matrix4f m = transform.getMatrix();
            Matrix3f mn = null;
            Vertex[] vertices = new Vertex[verts.length];
//            Normal[] normals = norms != null ? new Normal[norms.length] : null;
//            TextureCoordinate[] textureCoords = texCoords != null ? new TextureCoordinate[texCoords.length] : null;

            for (int i = 0; i < verts.length; i++)
            {
                Vertex v = verts[i];
//                Normal n = norms != null ? norms[i] : null;
//                TextureCoordinate t = texCoords != null ? texCoords[i] : null;

                Vector4f pos = new Vector4f(v.getPos()), newPos = new Vector4f();
                pos.w = 1;
                m.transform(pos, newPos);
                vertices[i] = new Vertex(newPos, v.getMaterial());

                if (v.hasNormal())
                {
                    if(mn == null)
                    {
                        mn = new Matrix3f();
                        m.getRotationScale(mn);
                        mn.invert();
                        mn.transpose();
                    }
                    Vector3f normal = new Vector3f(v.getNormal().getData()), newNormal = new Vector3f();
                    mn.transform(normal, newNormal);
                    newNormal.normalize();
                    vertices[i].setNormal(new Normal(newNormal));
                }

                if (v.hasTextureCoordinate()) vertices[i].setTextureCoordinate(v.getTextureCoordinate());
                else v.setTextureCoordinate(TextureCoordinate.getDefaultUVs()[i]);

                //texCoords TODO
//                if (t != null) textureCoords[i] = t;
            }
            return new Face(vertices, this.materialName);
        }

        public Normal getNormal()
        {
            Vector3f a = this.verts[2].getPos3();
            a.sub(this.verts[0].getPos3());
            Vector3f b = this.verts[3].getPos3();
            b.sub(this.verts[1].getPos3());
            a.cross(a, b);
            a.normalize();
            return new Normal(a);
        }
    }

    public static class Vertex
    {
        private Vector4f position;
        private Normal normal;
        private TextureCoordinate texCoord;
        private Material material = new Material();

        public Vertex(Vector4f position, Material material)
        {
            this.position = position;
            this.material = material;
        }

        public void setPos(Vector4f position)
        {
            this.position = position;
        }

        public Vector4f getPos()
        {
            return this.position;
        }

        public Vector3f getPos3()
        {
            return new Vector3f(this.position.x, this.position.y, this.position.z);
        }

        public boolean hasNormal()
        {
            return this.normal != null;
        }

        public void setNormal(Normal normal)
        {
            this.normal = normal;
        }

        public Normal getNormal()
        {
            return this.normal;
        }

        public boolean hasTextureCoordinate()
        {
            return this.texCoord != null;
        }

        public void setTextureCoordinate(TextureCoordinate texCoord)
        {
            this.texCoord = texCoord;
        }

        public TextureCoordinate getTextureCoordinate()
        {
            return this.texCoord;
        }

//        public boolean hasNormalizedUVs()
//        {
//            return this.texCoord.u >= 0.0f && this.texCoord.u <= 1.0f && this.texCoord.v >= 0.0f && this.texCoord.v <= 1.0f;
//        }

        public void setMaterial(Material material)
        {
            this.material = material;
        }

        public Material getMaterial()
        {
            return this.material;
        }

        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append(String.format("v:%n"));
            builder.append(String.format("    position: %s %s %s%n", position.x, position.y, position.z));
            builder.append(String.format("    material: %s %s %s %s %s%n", material.getName(), material.getColor().x, material.getColor().y, material.getColor().z, material.getColor().w));
            return builder.toString();
        }
    }

    public static class Normal
    {
        public float x, y, z;

        public Normal()
        {
            this(0.0f, 0.0f, 0.0f);
        }

        public Normal(float[] data)
        {
            this(data[0], data[1], data[2]);
        }

        public Normal(Vector3f vector3f)
        {
            this(vector3f.x, vector3f.y, vector3f.z);
        }

        public Normal(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Vector3f getData()
        {
            return new Vector3f(this.x, this.y, this.z);
        }
    }

    public static class TextureCoordinate
    {
        public float u, v, w;

        public TextureCoordinate()
        {
            this(0.0f, 0.0f, 1.0f);
        }

        public TextureCoordinate(float[] data)
        {
            this(data[0], data[1], data[2]);
        }

        public TextureCoordinate(Vector3f data)
        {
            this(data.x, data.y, data.z);
        }

        public TextureCoordinate(float u, float v, float w)
        {
            this.u = u;
            this.v = v;
            this.w = w;
        }

        public Vector3f getData()
        {
            return new Vector3f(this.u, this.v, this.w);
        }

        public static TextureCoordinate[] getDefaultUVs()
        {
            TextureCoordinate[] texCoords = new TextureCoordinate[4];
            texCoords[0] = new TextureCoordinate(0.0f, 0.0f, 1.0f);
            texCoords[1] = new TextureCoordinate(1.0f, 0.0f, 1.0f);
            texCoords[2] = new TextureCoordinate(1.0f, 1.0f, 1.0f);
            texCoords[3] = new TextureCoordinate(0.0f, 1.0f, 1.0f);
            return texCoords;
        }
    }

    public static class Group implements IModelPart
    {
        public static final String DEFAULT_NAME = "OBJModel.Default.Element.Name";
        public static final String ALL = "OBJModel.Group.All.Key";
        public static final String ALL_EXCEPT = "OBJModel.Group.All.Except.Key";
        private String name = DEFAULT_NAME;
        private LinkedHashSet<Face> faces = new LinkedHashSet<Face>();
        public float[] minUVBounds = new float[] {0.0f, 0.0f};
        public float[] maxUVBounds = new float[] {1.0f, 1.0f};

//        public float[] minUVBounds = new float[] {0.0f, 0.0f};
//        public float[] maxUVBounds = new float[] {1.0f, 1.0f};

        public Group(String name, LinkedHashSet<Face> faces)
        {
            this.name = name != null ? name : DEFAULT_NAME;
            this.faces = faces == null ? new LinkedHashSet<Face>() : faces;
        }

        public LinkedHashSet<Face> applyTransform(Optional<TRSRTransformation> transform)
        {
            LinkedHashSet<Face> faceSet = new LinkedHashSet<Face>();
            for (Face f : this.faces)
            {
//                if (minUVBounds != null && maxUVBounds != null) f.normalizeUVs(minUVBounds, maxUVBounds);
                faceSet.add(f.bake(transform.or(TRSRTransformation.identity())));
            }
            return faceSet;
        }

        public String getName()
        {
            return this.name;
        }

        public LinkedHashSet<Face> getFaces()
        {
            return this.faces;
        }

        public void setFaces(LinkedHashSet<Face> faces)
        {
            this.faces = faces;
        }

        public void addFace(Face face)
        {
            this.faces.add(face);
        }

        public void addFaces(List<Face> faces)
        {
            this.faces.addAll(faces);
        }
    }

    public static class OBJState implements IModelState
    {
        protected Map<String, Boolean> visibilityMap = Maps.newHashMap();
        public IModelState parent;
        protected Operation operation = Operation.SET_TRUE;

        public OBJState(List<String> visibleGroups, boolean visibility)
        {
            this(visibleGroups, visibility, TRSRTransformation.identity());
        }

        public OBJState(List<String> visibleGroups, boolean visibility, IModelState parent)
        {
            this.parent = parent;
            for (String s : visibleGroups) this.visibilityMap.put(s, visibility);
        }

        public IModelState getParent(IModelState parent)
        {
            if (parent == null) return null;
            else if (parent instanceof OBJState) return ((OBJState) parent).parent;
            return parent;
        }

        public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part)
        {
            if (parent != null) return parent.apply(part);
            return Optional.absent();
        }

        public Map<String, Boolean> getVisibilityMap()
        {
            return this.visibilityMap;
        }

        public List<String> getGroupsWithVisibility(boolean visibility)
        {
            List<String> ret = Lists.newArrayList();
            for (Map.Entry<String, Boolean> e : this.visibilityMap.entrySet())
            {
                if (e.getValue() == visibility)
                {
                    ret.add(e.getKey());
                }
            }
            return ret;
        }

        public List<String> getGroupNamesFromMap()
        {
            return Lists.newArrayList(this.visibilityMap.keySet());
        }

        public void changeGroupVisibilities(List<String> names, Operation operation)
        {
            if (names == null || names.isEmpty()) return;
            this.operation = operation;
            if (names.get(0).equals(Group.ALL))
            {
                for (String s : this.visibilityMap.keySet())
                {
                    this.visibilityMap.put(s, this.operation.performOperation(this.visibilityMap.get(s)));
                }
            }
            else if (names.get(0).equals(Group.ALL_EXCEPT))
            {
                for (String s : this.visibilityMap.keySet())
                {
                    if (!names.subList(1,  names.size()).contains(s))
                    {
                        this.visibilityMap.put(s,  this.operation.performOperation(this.visibilityMap.get(s)));
                    }
                }
            }
            else
            {
                for (String s : names)
                {
                    this.visibilityMap.put(s, this.operation.performOperation(this.visibilityMap.get(s)));
                }
            }
        }

        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder("OBJState: ");
            builder.append(String.format("%n    parent: %s%n", this.parent.toString()));
            builder.append(String.format("    visibility map: %n"));
            for (Map.Entry<String, Boolean> e : this.visibilityMap.entrySet())
            {
                builder.append(String.format("        name: %s visible: %b%n", e.getKey(), e.getValue()));
            }
            return builder.toString();
        }

        @Override
        public int hashCode()
        {
            return Objects.hashCode(visibilityMap, parent, operation);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            OBJState other = (OBJState) obj;
            return Objects.equal(visibilityMap, other.visibilityMap) &&
                Objects.equal(parent, other.parent) &&
                operation == other.operation;
        }

        public enum Operation
        {
            SET_TRUE,
            SET_FALSE,
            TOGGLE;

            Operation(){}

            public boolean performOperation(boolean valueToToggle)
            {
                switch(this)
                {
                default:
                case SET_TRUE: return true;
                case SET_FALSE: return false;
                case TOGGLE: return !valueToToggle;
                }
            }
        }
    }

    public enum OBJProperty implements IUnlistedProperty<OBJState>
    {
        instance;
        public String getName()
        {
            return "OBJPropery";
        }

        @Override
        public boolean isValid(OBJState value)
        {
            return value instanceof OBJState;
        }

        @Override
        public Class<OBJState> getType()
        {
            return OBJState.class;
        }

        @Override
        public String valueToString(OBJState value)
        {
            return value.toString();
        }
    }

    public class OBJBakedModel implements IFlexibleBakedModel, ISmartBlockModel, ISmartItemModel, IPerspectiveAwareModel
    {
        private final OBJModel model;
        private IModelState state;
        private final VertexFormat format;
        private Set<BakedQuad> quads;
        private ImmutableMap<String, TextureAtlasSprite> textures;
        private TextureAtlasSprite sprite = ModelLoader.White.instance;

        public OBJBakedModel(OBJModel model, IModelState state, VertexFormat format, ImmutableMap<String, TextureAtlasSprite> textures)
        {
            this.model = model;
            this.state = state;
            if (this.state instanceof OBJState) this.updateStateVisibilityMap((OBJState) this.state);
            this.format = format;
            this.textures = textures;
        }

        public void scheduleRebake()
        {
            this.quads = null;
        }

        @Override
        public List<BakedQuad> getFaceQuads(EnumFacing side)
        {
            return Collections.emptyList();
        }

        @Override
        public List<BakedQuad> getGeneralQuads()
        {
            if (quads == null)
            {
                quads = Collections.synchronizedSet(new LinkedHashSet<BakedQuad>());
                Set<Face> faces = Collections.synchronizedSet(new LinkedHashSet<Face>());
                Optional<TRSRTransformation> transform = Optional.absent();
                for (Group g : this.model.getMatLib().getGroups().values())
                {
//                    g.minUVBounds = this.model.getMatLib().minUVBounds;
//                    g.maxUVBounds = this.model.getMatLib().maxUVBounds;
//                    FMLLog.info("Group: %s u: [%f, %f] v: [%f, %f]", g.name, g.minUVBounds[0], g.maxUVBounds[0], g.minUVBounds[1], g.maxUVBounds[1]);

                    if (this.state instanceof OBJState)
                    {
                        OBJState state = (OBJState) this.state;
                        if (state.parent != null)
                        {
                            transform = state.parent.apply(Optional.<IModelPart>absent());
                        }
                        //TODO: can this be replaced by updateStateVisibilityMap(OBJState)?
                        if (state.getGroupNamesFromMap().contains(Group.ALL))
                        {
                            state.visibilityMap.clear();
                            for (String s : this.model.getMatLib().getGroups().keySet())
                            {
                                state.visibilityMap.put(s, state.operation.performOperation(true));
                            }
                        }
                        else if (state.getGroupNamesFromMap().contains(Group.ALL_EXCEPT))
                        {
                            List<String> exceptList = state.getGroupNamesFromMap().subList(1, state.getGroupNamesFromMap().size());
                            state.visibilityMap.clear();
                            for (String s : this.model.getMatLib().getGroups().keySet())
                            {
                                if (!exceptList.contains(s))
                                {
                                    state.visibilityMap.put(s, state.operation.performOperation(true));
                                }
                            }
                        }
                        else
                        {
                            for (String s : state.visibilityMap.keySet())
                            {
                                state.visibilityMap.put(s, state.operation.performOperation(state.visibilityMap.get(s)));
                            }
                        }
                        if (state.getGroupsWithVisibility(true).contains(g.getName()))
                        {
                            faces.addAll(g.applyTransform(transform));
                        }
                    }
                    else
                    {
                        transform = state.apply(Optional.<IModelPart>absent());
                        faces.addAll(g.applyTransform(transform));
                    }
                }
                for (Face f : faces)
                {
                    if (this.model.getMatLib().materials.get(f.getMaterialName()).isWhite())
                    {
                        for (Vertex v : f.getVertices())
                        {//update material in each vertex
                            if (!v.getMaterial().equals(this.model.getMatLib().getMaterial(v.getMaterial().getName())))
                            {
                                v.setMaterial(this.model.getMatLib().getMaterial(v.getMaterial().getName()));
                            }
                        }
                        sprite = ModelLoader.White.instance;
                    } else sprite = this.textures.get(f.getMaterialName());
                    UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
                    builder.setQuadOrientation(EnumFacing.getFacingFromVector(f.getNormal().x, f.getNormal().y, f.getNormal().z));
                    builder.setQuadColored();
                    Normal faceNormal = f.getNormal();
                    putVertexData(builder, f.verts[0], faceNormal, TextureCoordinate.getDefaultUVs()[0], sprite);
                    putVertexData(builder, f.verts[1], faceNormal, TextureCoordinate.getDefaultUVs()[1], sprite);
                    putVertexData(builder, f.verts[2], faceNormal, TextureCoordinate.getDefaultUVs()[2], sprite);
                    putVertexData(builder, f.verts[3], faceNormal, TextureCoordinate.getDefaultUVs()[3], sprite);
                    quads.add(builder.build());
                }
            }
            List<BakedQuad> quadList = Collections.synchronizedList(Lists.newArrayList(quads));
            return quadList;
        }

        private final void putVertexData(UnpackedBakedQuad.Builder builder, Vertex v, Normal faceNormal, TextureCoordinate defUV, TextureAtlasSprite sprite)
        {
            for (int e = 0; e < format.getElementCount(); e++)
            {
                switch (format.getElement(e).getUsage())
                {
                    case POSITION:
                        builder.put(e, v.getPos().x, v.getPos().y, v.getPos().z, v.getPos().w);
                        break;
                    case COLOR:
                        float d;
                        if (v.hasNormal())
                            d = LightUtil.diffuseLight(v.getNormal().x, v.getNormal().y, v.getNormal().z);
                        else
                            d = LightUtil.diffuseLight(faceNormal.x, faceNormal.y, faceNormal.z);

                        if (v.getMaterial() != null)
                            builder.put(e,
                                    d * v.getMaterial().getColor().x,
                                    d * v.getMaterial().getColor().y,
                                    d * v.getMaterial().getColor().z,
                                    v.getMaterial().getColor().w);
                        else
                            builder.put(e, d, d, d, 1);
                        break;
                    case UV:
                        if (!v.hasTextureCoordinate())
                            builder.put(e,
                                    sprite.getInterpolatedU(defUV.u * 16),
                                    sprite.getInterpolatedV((model.customData.flipV ? 1 - defUV.v: defUV.v) * 16),
                                    0, 1);
                        else
                            builder.put(e,
                                    sprite.getInterpolatedU(v.getTextureCoordinate().u * 16),
                                    sprite.getInterpolatedV((model.customData.flipV ? 1 - v.getTextureCoordinate().v : v.getTextureCoordinate().v) * 16),
                                    0, 1);
                        break;
                    case NORMAL:
                        if (!v.hasNormal())
                            builder.put(e, faceNormal.x, faceNormal.y, faceNormal.z, 0);
                        else
                            builder.put(e, v.getNormal().x, v.getNormal().y, v.getNormal().z, 0);
                        break;
                    default:
                        builder.put(e);
                }
            }
        }

        @Override
        public boolean isAmbientOcclusion()
        {
            return model != null ? model.customData.ambientOcclusion : true;
        }

        @Override
        public boolean isGui3d()
        {
            return model != null ? model.customData.gui3d : true;
        }

        @Override
        public boolean isBuiltInRenderer()
        {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleTexture()
        {
            return this.sprite;
        }

        @Override
        public ItemCameraTransforms getItemCameraTransforms()
        {
            return ItemCameraTransforms.DEFAULT;
        }

        @Override
        public VertexFormat getFormat()
        {
            return format;
        }

        @Override
        public IBakedModel handleItemState(ItemStack stack)
        {
            return this;
        }

        @Override
        public OBJBakedModel handleBlockState(IBlockState state)
        {
            if (state instanceof IExtendedBlockState)
            {
                IExtendedBlockState exState = (IExtendedBlockState) state;
                if (exState.getUnlistedNames().contains(OBJProperty.instance))
                {
                    OBJState s = exState.getValue(OBJProperty.instance);
                    if (s != null)
                    {
                        if (s.visibilityMap.containsKey(Group.ALL) || s.visibilityMap.containsKey(Group.ALL_EXCEPT))
                        {
                            this.updateStateVisibilityMap(s);
                        }
                        return getCachedModel(s);
                    }
                }
            }
            return this;
        }

        private void updateStateVisibilityMap(OBJState state)
        {
            if (state.visibilityMap.containsKey(Group.ALL))
            {
                boolean operation = state.visibilityMap.get(Group.ALL);
                state.visibilityMap.clear();
                for (String s : this.model.getMatLib().getGroups().keySet())
                {
                    state.visibilityMap.put(s,  state.operation.performOperation(operation));
                }
            }
            else if (state.visibilityMap.containsKey(Group.ALL_EXCEPT))
            {
                List<String> exceptList = state.getGroupNamesFromMap().subList(1, state.getGroupNamesFromMap().size());
                state.visibilityMap.remove(Group.ALL_EXCEPT);
                for (String s : this.model.getMatLib().getGroups().keySet())
                {
                    if (!exceptList.contains(s))
                    {
                        state.visibilityMap.put(s, state.operation.performOperation(state.visibilityMap.get(s)));
                    }
                }
            }
            else
            {
                for (String s : state.visibilityMap.keySet())
                {
                    state.visibilityMap.put(s, state.operation.performOperation(state.visibilityMap.get(s)));
                }
            }
        }

        private final LoadingCache<IModelState, OBJBakedModel> cache = CacheBuilder.newBuilder().maximumSize(20).build(new CacheLoader<IModelState, OBJBakedModel>()
        {
            public OBJBakedModel load(IModelState state) throws Exception
            {
                return new OBJBakedModel(model, state, format, textures);
            }
        });

        public OBJBakedModel getCachedModel(IModelState state)
        {
            return cache.getUnchecked(state);
        }

        public OBJModel getModel()
        {
            return this.model;
        }

        public IModelState getState()
        {
            return this.state;
        }

        public OBJBakedModel getBakedModel()
        {
            return new OBJBakedModel(this.model, this.state, this.format, this.textures);
        }

        @Override
        public Pair<? extends IFlexibleBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType)
        {
            return IPerspectiveAwareModel.MapWrapper.handlePerspective(this, state, cameraTransformType);
        }

        @Override
        public String toString()
        {
            return this.model.modelLocation.toString();
        }
    }

    @SuppressWarnings("serial")
    public static class UVsOutOfBoundsException extends RuntimeException
    {
        public ResourceLocation modelLocation;

        public UVsOutOfBoundsException(ResourceLocation modelLocation)
        {
            super(String.format("Model '%s' has UVs ('vt') out of bounds 0-1! The missing model will be used instead. Support for UV processing will be added to the OBJ loader in the future.", modelLocation));
            this.modelLocation = modelLocation;
        }
    }

    @Override
    public IModelState getDefaultState()
    {
        return TRSRTransformation.identity();
    }
}
