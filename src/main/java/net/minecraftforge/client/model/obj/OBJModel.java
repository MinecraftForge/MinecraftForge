package net.minecraftforge.client.model.obj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelCustomData;
import net.minecraftforge.client.model.IModelPart;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.IPerspectiveState;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.common.FMLLog;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class OBJModel implements IRetexturableModel, IModelCustomData
{
    private Gson GSON = new GsonBuilder().create();
    private MaterialLibrary matLib;
    private final ResourceLocation modelLocation;
    private boolean ambientOcclusion = true;
    private boolean gui3d = true;

    public OBJModel(MaterialLibrary matLib, ResourceLocation modelLocation)
    {
        this.matLib = matLib;
        this.modelLocation = modelLocation;
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
            if (!textures.contains(textureLoc))
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
                FMLLog.severe("unresolved texture '%s' for obj model '%s'", e.getValue().getTexture().getTextureLocation().getResourcePath(), modelLocation);
                builder.put(e.getKey(), missing);
            }
            else
            {
                builder.put(e.getKey(), bakedTextureGetter.apply(e.getValue().getTexture().getTextureLocation()));
            }
        }
        builder.put("missingno", missing);
        return new OBJBakedModel(this, state, format, builder.build(), ambientOcclusion, gui3d);
    }

    public TRSRTransformation getDefaultState()
    {
        return TRSRTransformation.identity();
    }

    public MaterialLibrary getMatLib()
    {
        return this.matLib;
    }

    @Override
    public IModel process(ImmutableMap<String, String> customData)
    {
        for (Map.Entry<String, String> e : customData.entrySet())
        {
            if (e.getKey().equals("ambient"))
                this.ambientOcclusion = GSON.fromJson(e.getValue(), Boolean.TYPE);
            else if (e.getKey().equals("gui3d"))
                this.gui3d = GSON.fromJson(e.getValue(), Boolean.TYPE);
        }
        return this;
    }

    @Override
    public IModel retexture(ImmutableMap<String, String> textures)
    {
        OBJModel ret = new OBJModel(this.matLib.makeLibWithReplacements(textures), this.modelLocation);
        ret.ambientOcclusion = this.ambientOcclusion;
        ret.gui3d = this.gui3d;
        return ret;
    }

    public static class Parser
    {
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

        private float minU = 0f;
        private float maxU = 1f;
        private float minV = 0f;
        private float maxV = 1f;

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
            int usemtlCounter = 0;

            for (;;)
            {
                currentLine = objReader.readLine();
                if (currentLine == null) break;
                if (currentLine.isEmpty() || currentLine.startsWith("#")) continue;

                String[] fields = currentLine.split(" ", 2);
                String key = fields[0];
                String data = fields[1];

                if (key.equalsIgnoreCase("mtllib"))
                    this.materialLibrary.parseMaterials(manager, data, objFrom);
                else if (key.equalsIgnoreCase("usemtl"))
                {
                    material = this.materialLibrary.materials.get(data);
                    usemtlCounter++;
                }
                else if (key.equalsIgnoreCase("v"))
                {
                    String[] splitData = data.split(" ");
                    float[] floatSplitData = new float[splitData.length];
                    for (int i = 0; i < splitData.length; i++)
                        floatSplitData[i] = Float.parseFloat(splitData[i]);
                    Vector4f pos = new Vector4f(floatSplitData[0], floatSplitData[1], floatSplitData[2], floatSplitData.length == 4 ? floatSplitData[3] : 1);
                    Vertex vertex = new Vertex(pos, material);
                    this.vertices.add(vertex);
                }
                else if (key.equalsIgnoreCase("vn"))
                {
                    String[] splitData = data.split(" ");
                    float[] floatSplitData = new float[splitData.length];
                    for (int i = 0; i < splitData.length; i++)
                        floatSplitData[i] = Float.parseFloat(splitData[i]);
                    Normal normal = new Normal(new Vector3f(floatSplitData[0], floatSplitData[1], floatSplitData[2]));
                    this.normals.add(normal);
                }
                else if (key.equalsIgnoreCase("vt"))
                {
                    String[] splitData = data.split(" ");
                    float[] floatSplitData = new float[splitData.length];
                    for (int i = 0; i < splitData.length; i++)
                        floatSplitData[i] = Float.parseFloat(splitData[i]);
                    TextureCoordinate texCoord = new TextureCoordinate(new Vector3f(floatSplitData[0], floatSplitData[1], floatSplitData.length == 3 ? floatSplitData[2] : 1));
                    if (floatSplitData[0] < this.minU)
                        this.minU = floatSplitData[0];
                    else if (floatSplitData[0] > this.maxU)
                        this.maxU = floatSplitData[0];

                    if (floatSplitData[1] < this.minV)
                        this.minV = floatSplitData[1];
                    else if (floatSplitData[1] > this.maxV)
                        this.maxV = floatSplitData[1];
                    
                    this.texCoords.add(texCoord);
                }
                else if (key.equalsIgnoreCase("f"))
                {
                    String[] splitSpace = data.split(" ");
                    String[][] splitSlash = new String[splitSpace.length][];
                    if (splitSpace.length > 4) FMLLog.warning("OBJModel.Parser: found a face ('f') with more than 4 vertices, only the first 4 of these vertices will be rendered!");

                    int vert = 0;
                    int texCoord = 0;
                    int norm = 0;

                    List<Vertex> v = Lists.newArrayListWithCapacity(splitSpace.length);
                    List<TextureCoordinate> t = Lists.newArrayListWithCapacity(splitSpace.length);
                    List<Normal> n = Lists.newArrayListWithCapacity(splitSpace.length);

                    for (int i = 0; i < splitSpace.length; i++)
                    {
                        if (splitSpace[i].contains("//"))
                        {
                            splitSlash[i] = splitSpace[i].split("//");

                            vert = Integer.parseInt(splitSlash[i][0]);
                            vert = vert < 0 ? this.vertices.size() - 1 : vert - 1;
                            norm = Integer.parseInt(splitSlash[i][1]);
                            norm = norm < 0 ? this.normals.size() - 1 : norm - 1;

                            v.add(this.vertices.get(vert));
                            n.add(this.normals.get(norm));
                        }
                        else if (splitSpace[i].contains("/"))
                        {
                            splitSlash[i] = splitSpace[i].split("/");

                            vert = Integer.parseInt(splitSlash[i][0]);
                            vert = vert < 0 ? this.vertices.size() - 1 : vert - 1;
                            texCoord = Integer.parseInt(splitSlash[i][1]);
                            texCoord = texCoord < 0 ? this.texCoords.size() - 1 : texCoord - 1;
                            if (splitSlash[i].length > 2)
                            {
                                norm = Integer.parseInt(splitSlash[i][2]);
                                norm = norm < 0 ? this.normals.size() - 1 : norm - 1;
                            }

                            v.add(this.vertices.get(vert));
                            t.add(this.texCoords.get(texCoord));
                            if (splitSlash[i].length > 2) n.add(this.normals.get(norm));
                        }
                        else
                        {
                            splitSlash[i] = splitSpace[i].split("");

                            vert = Integer.parseInt(splitSlash[i][0]);
                            vert = vert < 0 ? this.vertices.size() - 1 : vert - 1;

                            v.add(this.vertices.get(vert));
                        }
                    }

                    Vertex[] va = new Vertex[v.size()];
                    v.toArray(va);
                    TextureCoordinate[] ta = new TextureCoordinate[t.size()];
                    t.toArray(ta);
                    Normal[] na = new Normal[n.size()];
                    n.toArray(na);
                    Face face = new Face(va, na, ta, material.name);
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
            model.getMatLib().setUVBounds(minU, maxU, minV, maxV);
            return model;
        }
    }

    public static class MaterialLibrary
    {
        private Set<String> unknownMaterialCommands = new HashSet<String>();
        private Map<String, Material> materials = new HashMap<String, Material>();
        private Map<String, Group> groups = new HashMap<String, Group>();
        private InputStreamReader mtlStream;
        private BufferedReader mtlReader;
        private float minU = 0f;
        private float maxU = 1f;
        private float minV = 0f;
        private float maxV = 1f;

        public MaterialLibrary()
        {
            this.groups.put(Group.DEFAULT_NAME, new Group(Group.DEFAULT_NAME, null));
        }
        
        public MaterialLibrary makeLibWithReplacements(ImmutableMap<String, String> replacements)
        {   
            Map<String, Material> mats = new HashMap<String, Material>();
            for (Map.Entry<String, Material> e : this.materials.entrySet())
            {
                if (replacements.containsKey(e.getKey()) || replacements.containsKey("all"))
                {
                    Texture currentTexture = e.getValue().texture;
                    Texture replacementTexture = new Texture(replacements.get(e.getKey()), currentTexture.position, currentTexture.scale, currentTexture.rotation);
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
            ret.minU = this.minU;
            ret.maxU = this.maxU;
            ret.minV = this.minV;
            ret.maxV = this.maxV;
            return ret;
        }

        public float getMinU()
        {
            return this.minU;
        }

        public float getMaxU()
        {
            return this.maxU;
        }

        public float getMinV()
        {
            return this.minV;
        }

        public float getMaxV()
        {
            return this.maxV;
        }

        public void setUVBounds(float minU, float maxU, float minV, float maxV)
        {
            this.minU = minU;
            this.maxU = maxU;
            this.minV = minV;
            this.maxV = maxV;
        }

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
            if (!path.contains("/")) {
                path = from.getResourcePath().substring(0, from.getResourcePath().lastIndexOf("/") + 1) + path;
            }
            mtlStream = new InputStreamReader(manager.getResource(new ResourceLocation(domain, path)).getInputStream(), Charsets.UTF_8);
            mtlReader = new BufferedReader(mtlStream);

            String currentLine = "";
            Material material = new Material();
            material.setName(Material.WHITE_NAME);
            material.setTexture(Texture.White);
            this.materials.put(Material.WHITE_NAME, material);
            this.materials.put(Material.DEFAULT_NAME, new Material(Texture.White));

            for (;;)
            {
                currentLine = mtlReader.readLine();
                if (currentLine == null) break;
                if (currentLine.isEmpty() || currentLine.startsWith("#")) continue;

                String[] fields = currentLine.split(" ", 2);
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
                        String[] rgbStrings = data.split(" ", 3);
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
                            String[] mapStrings = data.split(" ");
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
                else
                {
                    if (!unknownMaterialCommands.contains(key))
                    {
                        unknownMaterialCommands.add(key);
                        FMLLog.info("OBJLoader.MaterialLibrary: command '%s' (model: '%s') is not currently supported, skipping", key, new ResourceLocation(domain, path));
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
        private Texture texture = Texture.White;
        private String name = DEFAULT_NAME;

        public Material()
        {
            this(new Vector4f(1f, 1f, 1f, 1f));
        }

        public Material(Vector4f color)
        {
            this(color, Texture.White, WHITE_NAME);
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
            return this.texture.equals(Texture.White);
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
        public static Texture White = new Texture("builtin/white", new Vector2f(0, 0), new Vector2f(1, 1), 0);
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
        private Normal[] norms = new Normal[4];
        private TextureCoordinate[] texCoords = new TextureCoordinate[4];
        private String materialName = Material.DEFAULT_NAME;

        public Face(Vertex[] verts)
        {
            this(verts, new Normal[0], new TextureCoordinate[0]);
        }

        public Face(Vertex[] verts, Normal[] norms)
        {
            this(verts, norms, null);
        }

        public Face(Vertex[] verts, TextureCoordinate[] texCoords)
        {
            this(verts, null, texCoords);
        }

        public Face(Vertex[] verts, Normal[] norms, TextureCoordinate[] texCoords)
        {
            this(verts, norms, texCoords, Material.DEFAULT_NAME);
        }

        public Face(Vertex[] verts, Normal[] norms, TextureCoordinate[] texCoords, String materialName)
        {
            this.verts = verts;
            this.verts = this.verts != null && this.verts.length > 0 ? this.verts : null;
            this.norms = norms;
            this.norms = this.norms != null && this.norms.length > 0 ? this.norms : null;
            this.texCoords = texCoords;
            this.texCoords = this.texCoords != null && this.texCoords.length > 0 ? this.texCoords : null;
            setMaterialName(materialName);
            ensureQuads();
        }

        private void ensureQuads()
        {
            if (this.verts != null && this.verts.length == 3)
            {
                this.verts = new Vertex[]{this.verts[0], this.verts[1], this.verts[2], this.verts[2]};
            }

            if (this.norms != null && this.norms.length == 3)
            {
                this.norms = new Normal[]{this.norms[0], this.norms[1], this.norms[2], this.norms[2]};
            }

            if (this.texCoords != null && this.texCoords.length == 3)
            {
                this.texCoords = new TextureCoordinate[]{this.texCoords[0], this.texCoords[1], this.texCoords[2], this.texCoords[2]};
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

        public boolean setVertices(Vertex[] verts)
        {
            if (verts == null) return false;
            else this.verts = verts;
            return true;
        }

        public Vertex[] getVertices()
        {
            return this.verts;
        }

        public boolean setNormals(Normal[] norms)
        {
            if (norms == null) return false;
            else this.norms = norms;
            return true;
        }

        public Normal[] getNormals()
        {
            return this.norms;
        }

        public boolean setTextureCoordinates(TextureCoordinate[] texCoords)
        {
            if (texCoords == null) return false;
            else this.texCoords = texCoords;
            return true;
        }

        public TextureCoordinate[] getTextureCoordinates()
        {
            return this.texCoords;
        }

        public boolean areUVsNormalized()
        {
            for (TextureCoordinate t : this.texCoords)
            {
                if (!(t.getPosition().x > 0.0f && t.getPosition().x < 1.0f && t.getPosition().y > 0.0f && t.getPosition().y < 1.0f))
                {
                    return false;
                }
            }
            return true;
        }

        public Face bake(TRSRTransformation transform)
        {
            Matrix4f m = transform.getMatrix();
            Vertex[] vertices = new Vertex[verts.length];
            Normal[] normals = norms != null ? new Normal[norms.length] : null;
            TextureCoordinate[] textureCoords = texCoords != null ? new TextureCoordinate[texCoords.length] : null;

            for (int i = 0; i < verts.length; i++)
            {
                m = transform.getMatrix();
                Vertex v = verts[i];
                Normal n = norms != null ? norms[i] : null;
                TextureCoordinate t = texCoords != null ? texCoords[i] : null;

                Vector4f pos = new Vector4f(v.getPosition()), newPos = new Vector4f();
                pos.w = 1;
                m.transform(pos, newPos);
                Vector4f rPos = new Vector4f(newPos.x, newPos.y, newPos.z, newPos.w);
                vertices[i] = new Vertex(rPos, v.getMaterial());

                if (n != null)
                {
                    m.invert();
                    m.transpose();
                    Vector4f normal = new Vector4f(n.getNormal()), newNormal = new Vector4f();
                    normal.w = 1;
                    m.transform(normal, newNormal);
                    Vector3f rNormal = new Vector3f(newNormal.x / newNormal.w, newNormal.y / newNormal.w, newNormal.z / newNormal.w);
                    rNormal.normalize();
                    normals[i] = new Normal(rNormal);
                }

                //texCoords TODO
                if (t != null) textureCoords[i] = t;
            }
            return new Face(vertices, normals, textureCoords, this.materialName);
        }

        public Normal getNormal()
        {
            if (norms == null)
            { // use vertices to calculate normal
                Vector3f v1 = new Vector3f(this.verts[0].getPosition().x, this.verts[0].getPosition().y, this.verts[0].getPosition().z);
                Vector3f v2 = new Vector3f(this.verts[1].getPosition().x, this.verts[1].getPosition().y, this.verts[1].getPosition().z);
                Vector3f v3 = new Vector3f(this.verts[2].getPosition().x, this.verts[2].getPosition().y, this.verts[2].getPosition().z);
                Vector3f v4 = this.verts.length > 3 ? new Vector3f(this.verts[3].getPosition().x, this.verts[3].getPosition().y, this.verts[3].getPosition().z) : null;

                if (v4 == null)
                {
                    Vector3f v2c = new Vector3f(v2.x, v2.y, v2.z);
                    Vector3f v1c = new Vector3f(v1.x, v1.y, v1.z);
                    v1c.sub(v2c);
                    Vector3f v3c = new Vector3f(v3.x, v3.y, v3.z);
                    v3c.sub(v2c);
                    Vector3f c = new Vector3f();
                    c.cross(v1c, v3c);
                    c.normalize();
                    Normal normal = new Normal(c);
                    return normal;
                }
                else
                {
                    Vector3f v2c = new Vector3f(v2.x, v2.y, v2.z);
                    Vector3f v1c = new Vector3f(v1.x, v1.y, v1.z);
                    v1c.sub(v2c);
                    Vector3f v3c = new Vector3f(v3.x, v3.y, v3.z);
                    v3c.sub(v2c);
                    Vector3f c = new Vector3f();
                    c.cross(v1c, v3c);
                    c.normalize();

                    v1c = new Vector3f(v1.x, v1.y, v1.z);
                    v3c = new Vector3f(v3.x, v3.y, v3.z);

                    Vector3f v4c = new Vector3f(v4.x, v4.y, v4.z);
                    v1c.sub(v4c);
                    v3c.sub(v4c);
                    Vector3f d = new Vector3f();
                    d.cross(v1c, v3c);
                    d.normalize();

                    Vector3f avg = new Vector3f();
                    avg.x = (c.x + d.x) * 0.5f;
                    avg.y = (c.y + d.y) * 0.5f;
                    avg.z = (c.z + d.z) * 0.5f;
                    avg.normalize();
                    Normal normal = new Normal(avg);
                    return normal;
                }
            }
            else
            { // use normals to calculate normal
                Vector3f n1 = this.norms[0].getNormal();
                Vector3f n2 = this.norms[1].getNormal();
                Vector3f n3 = this.norms[2].getNormal();
                Vector3f n4 = this.norms.length > 3 ? this.norms[3].getNormal() : null;

                if (n4 == null)
                {
                    Vector3f n2c = new Vector3f(n2.x, n2.y, n2.z);
                    Vector3f n1c = new Vector3f(n1.x, n1.y, n1.z);
                    n1c.sub(n2c);
                    Vector3f n3c = new Vector3f(n3.x, n3.y, n3.z);
                    n3c.sub(n2c);
                    Vector3f c = new Vector3f();
                    c.cross(n1c, n3c);
                    c.normalize();
                    Normal normal = new Normal(c);
                    return normal;
                }
                else
                {
                    Vector3f n2c = new Vector3f(n2.x, n2.y, n2.z);
                    Vector3f n1c = new Vector3f(n1.x, n1.y, n1.z);
                    n1c.sub(n2c);
                    Vector3f n3c = new Vector3f(n3.x, n3.y, n3.z);
                    n3c.sub(n2c);
                    Vector3f c = new Vector3f();
                    c.cross(n1c, n3c);
                    c.normalize();

                    n1c = new Vector3f(n1.x, n1.y, n1.z);
                    n3c = new Vector3f(n3.x, n3.y, n3.z);

                    Vector3f n4c = new Vector3f(n4.x, n4.y, n4.z);
                    n1c.sub(n4c);
                    n3c.sub(n4c);
                    Vector3f d = new Vector3f();
                    d.cross(n1c, n3c);
                    d.normalize();

                    Vector3f avg = new Vector3f();
                    avg.x = (c.x + d.x) * 0.5f;
                    avg.y = (c.y + d.y) * 0.5f;
                    avg.z = (c.z + d.z) * 0.5f;
                    avg.normalize();
                    Normal normal = new Normal(avg);
                    return normal;
                }
            }
        }
    }

    public static class Vertex
    {
        private Vector4f position;
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

        public Vector4f getPosition()
        {
            return this.position;
        }

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
        private Vector3f normal;

        public Normal(Vector3f normal)
        {
            this.normal = normal;
        }

        public void setNormal(Vector3f normal)
        {
            this.normal = normal;
        }

        public Vector3f getNormal()
        {
            return this.normal;
        }
    }

    public static class TextureCoordinate
    {
        private Vector3f position;

        public TextureCoordinate(Vector3f position)
        {
            this.position = position;
        }

        public void setPosition(Vector3f position)
        {
            this.position = position;
        }

        public Vector3f getPosition()
        {
            return this.position;
        }
    }

    public static class Group implements IModelPart
    {
        public static final String DEFAULT_NAME = "OBJModel.Default.Element.Name";
        public static final String ALL = "OBJModel.Group.All.Key";
        public static final String ALL_EXCEPT = "OBJModel.Group.All.Except.Key";
        private String name = DEFAULT_NAME;
        private LinkedHashSet<Face> faces = new LinkedHashSet<Face>();

        public Group(String name, LinkedHashSet<Face> faces)
        {
            this.name = name != null ? name : DEFAULT_NAME;
            this.faces = faces == null ? new LinkedHashSet<Face>() : faces;
        }

        public LinkedHashSet<Face> applyTransform(TRSRTransformation transform)
        {
            LinkedHashSet<Face> faceSet = new LinkedHashSet<Face>();
            for (Face f : this.faces)
            {
                faceSet.add(f.bake(transform));
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
        protected Map<String, Boolean> visibilityMap = new HashMap<String, Boolean>();
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

        private IModelState getParent(IModelState parent)
        {
            if (parent == null) return null;
            else if (parent instanceof OBJState) return ((OBJState) parent).parent;
            return parent;
        }

        public TRSRTransformation apply(IModelPart part)
        {
            if (parent != null) return parent.apply(part);
            return new TRSRTransformation(EnumFacing.NORTH);
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
        private static final int BYTES_IN_INT = Integer.SIZE / Byte.SIZE;
        private static final int VERTICES_IN_QUAD = 4;
        private ImmutableMap<String, TextureAtlasSprite> textures;
        private TextureAtlasSprite sprite = ModelLoader.White.instance;
        private boolean ambientOcclusion = true;
        private boolean gui3d = true;

        public OBJBakedModel(OBJModel model, IModelState state, VertexFormat format, ImmutableMap<String, TextureAtlasSprite> textures)
        {
            this(model, state, format, textures, true, true);
        }

        public OBJBakedModel(OBJModel model, IModelState state, VertexFormat format, ImmutableMap<String, TextureAtlasSprite> textures, boolean ambientOcclusion, boolean gui3d)
        {
            this.model = model;
            this.state = state;
            if (this.state instanceof OBJState) this.updateStateVisibilityMap((OBJState) this.state);
            this.format = format;
            this.textures = textures;
            this.ambientOcclusion = ambientOcclusion;
            this.gui3d = gui3d;
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
                TRSRTransformation transform = TRSRTransformation.identity();
                for (Group g : this.model.getMatLib().getGroups().values())
                {
                    if (this.state instanceof OBJState)
                    {
                        OBJState state = (OBJState) this.state;
                        if (state.parent != null && state.parent instanceof TRSRTransformation)
                        {
                            transform = (TRSRTransformation) state.parent;
                        }
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
                    else if (this.state instanceof TRSRTransformation)
                    {
                        transform = (TRSRTransformation) this.state;
                        faces.addAll(g.applyTransform(transform));
                    }
                    else
                    {
                        transform = TRSRTransformation.identity();
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

                    float minU = 0.0f;
                    float maxU = 1.0f;
                    float minV = 0.0f;
                    float maxV = 1.0f;

                    if (f.texCoords != null && !f.areUVsNormalized())
                    {
                        for (TextureCoordinate t : f.texCoords)
                        {
                            minU = t.getPosition().x < minU ? t.getPosition().x : minU;
                            maxU = t.getPosition().x > maxU ? t.getPosition().x : maxU;
                            minV = t.getPosition().y < minV ? t.getPosition().y : minV;
                            maxV = t.getPosition().y > maxV ? t.getPosition().y : maxV;
                        }

                        for (int i = 0; i < f.texCoords.length; i++)
                        {
                            TextureCoordinate t = f.texCoords[i];
                            float U = (t.getPosition().x - minU) / (maxU - minU);
                            float V = (t.getPosition().y - minV) / (maxV - minV);
                            Vector3f normPos = new Vector3f(U, V, t.getPosition().z);
                            f.texCoords[i] = new TextureCoordinate(normPos);
                        }
                    }

                    TextureCoordinate def1 = new TextureCoordinate(new Vector3f(minU, minV, 1));
                    TextureCoordinate def2 = new TextureCoordinate(new Vector3f(maxU, minV, 1));
                    TextureCoordinate def3 = new TextureCoordinate(new Vector3f(maxU, maxV, 1));
                    TextureCoordinate def4 = new TextureCoordinate(new Vector3f(minU, maxV, 1));
                    UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
                    builder.setQuadColored();
                    builder.setQuadOrientation(EnumFacing.getFacingFromVector(f.getNormal().normal.x, f.getNormal().normal.y, f.getNormal().normal.z));
                    putVertexData(builder, f.verts[0], f.texCoords != null ? f.texCoords[0] : def1, f.norms != null ? f.norms[0] : f.getNormal(), sprite);
                    putVertexData(builder, f.verts[1], f.texCoords != null ? f.texCoords[1] : def2, f.norms != null ? f.norms[1] : f.getNormal(), sprite);
                    putVertexData(builder, f.verts[2], f.texCoords != null ? f.texCoords[2] : def3, f.norms != null ? f.norms[2] : f.getNormal(), sprite);
                    putVertexData(builder, f.verts[3], f.texCoords != null ? f.texCoords[3] : def4, f.norms != null ? f.norms[3] : f.getNormal(), sprite);
                    quads.add(builder.build());
                }
            }
            List<BakedQuad> quadList = Collections.synchronizedList(Lists.newArrayList(quads));
            return quadList;
        }

        private void put(ByteBuffer buffer, VertexFormatElement e, Float... fs)
        {
            Attributes.put(buffer, e, true, 0f, fs);
        }

        @SuppressWarnings("unchecked")
        private final void putVertexData(UnpackedBakedQuad.Builder builder, Vertex v, TextureCoordinate t, Normal n, TextureAtlasSprite sprite)
        {
            for (int e = 0; e < format.getElementCount(); e++)
            {
                switch (format.getElement(e).getUsage())
                {
                    case POSITION:
                        builder.put(e, v.getPosition().x, v.getPosition().y, v.getPosition().z, v.getPosition().w);
                        break;
                    case COLOR:
                        float d = LightUtil.diffuseLight(n.normal.x, n.normal.y, n.normal.z);
                        if (v.getMaterial() != null) builder.put(e, d * v.getMaterial().getColor().x, d * v.getMaterial().getColor().y, d * v.getMaterial().getColor().z, d * v.getMaterial().getColor().w);
                        else builder.put(e, d, d, d, 1);
                        break;
                    case UV:
                        if (t != null) builder.put(e, sprite.getInterpolatedU(t.getPosition().x * 16), sprite.getInterpolatedV(t.getPosition().y * 16));
                        else builder.put(e);
                        break;
                    case NORMAL:
                        builder.put(e, n.normal.x, n.normal.y, n.normal.z, 0);
                        break;
                    default:
                        builder.put(e);
                        break;
                }
            }
        }

        @Override
        public boolean isAmbientOcclusion()
        {
            return ambientOcclusion;
        }

        @Override
        public boolean isGui3d()
        {
            return gui3d;
        }

        @Override
        public boolean isBuiltInRenderer()
        {
            return false;
        }

        @Override
        public TextureAtlasSprite getTexture()
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

        private final Map<IModelState, OBJBakedModel> cache = new HashMap<IModelState, OBJBakedModel>();

        public OBJBakedModel getCachedModel(IModelState state)
        {
            if (!cache.containsKey(state))
            {
                cache.put(state, new OBJBakedModel(this.model, state, this.format, this.textures));
            }
            return cache.get(state);
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
        public Pair<IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType)
        {
            if (state instanceof IPerspectiveState)
            {
                return Pair.of((IBakedModel) this, TRSRTransformation.blockCornerToCenter(((IPerspectiveState) state).forPerspective(cameraTransformType).apply(model)).getMatrix());
            }
            return Pair.of((IBakedModel) this, null);
        }

        @Override
        public String toString()
        {
            return this.model.modelLocation.toString();
        }
    }
}
