package net.minecraftforge.client.model.obj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
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

import org.apache.commons.lang3.builder.StandardToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;
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
import com.google.common.collect.Sets;

/*
 * Main class for OBJ loader.
 * Responsible for parsing .obj and .mtl files, storing parsed data, converting it,
 * and baking it into an IBakedModel for rendering.
 * Some data manipulation (ie. changing group visibilities/material colors) can be
 * achieved via OBJState and manipulation of the IBakedModel from within a block/tile entity class.
 * (See ModelLoaderRegistryDebug.java for examples)
 */
@SuppressWarnings("deprecation")
public class OBJModel implements IRetexturableModel<OBJModel>, IModelCustomData<OBJModel>, IModelSimpleProperties<OBJModel>
{
	public static final StandardToStringStyle STYLE = new StandardToStringStyle();
    private MaterialLibrary matLib;
    private OBJCustomData customData = null;
    private final ResourceLocation modelLocation;
    private final boolean gui3d;
    private final boolean smooth;

    static
    {
    	STYLE.setUseShortClassName(true);
    	STYLE.setUseIdentityHashCode(false);
    	STYLE.setContentStart(String.format(":[{%n    "));
    	STYLE.setContentEnd(String.format("%n}]%n"));
    	STYLE.setFieldNameValueSeparator(": ");
    	STYLE.setFieldSeparator(String.format(",%n    "));
    	STYLE.setNullText("---");
    }
    
    public OBJModel(MaterialLibrary matLib, ResourceLocation modelLocation, float[][] parsedUVBounds)
    {
        this(matLib, modelLocation, new OBJCustomData(parsedUVBounds), true, true);
    }
    
    public OBJModel(MaterialLibrary matLib, ResourceLocation modelLocation, float[][] parsedUVBounds, boolean gui3d, boolean smooth)
    {
    	this(matLib, modelLocation, new OBJCustomData(parsedUVBounds), gui3d, smooth);
    }

    public OBJModel(MaterialLibrary matLib, ResourceLocation modelLocation, OBJCustomData customData)
    {
        this(matLib, modelLocation, customData, true, true);
    }
    
    public OBJModel(MaterialLibrary matLib, ResourceLocation modelLocation, OBJCustomData customData, boolean gui3d, boolean smooth)
    {
    	this.matLib = matLib;
    	this.modelLocation = modelLocation;
    	this.customData = customData;
    	this.customData.setGroupNameList(this.matLib.getGroupNames());
    	this.gui3d = gui3d;
    	this.smooth = smooth;
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
            if (!textures.contains(textureLoc) && !mat.isWhite()) textures.add(textureLoc);
        }
        return textures;
    }

    @Override
    public IFlexibleBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        ImmutableMap.Builder<String, TextureAtlasSprite> builder = ImmutableMap.builder();
        builder.put(ModelLoader.White.loc.toString(), ModelLoader.White.instance);
        TextureAtlasSprite missing = bakedTextureGetter.apply(new ResourceLocation("missingno"));
        for (Map.Entry<String, Material> e : this.matLib.materials.entrySet())
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
        
        boolean allFalse = this.customData.allProcessUVValuesFalse();
        boolean outOfBounds = this.customData.hasUVsOutOfBounds();
        if (allFalse && outOfBounds)
        {	
        	FMLLog.severe("OBJLoader: Model '%s' has UVs ('vt') out of bounds 0..1! It may not render as expected!", this.modelLocation);
        }
        
        return new OBJBakedModel(this, state, format, builder.build());
    }

    public MaterialLibrary getMatLib()
    {
        return this.matLib;
    }
    
    public OBJCustomData getCustomData()
    {
    	return this.customData;
    }
    
    public ResourceLocation getModelLocation()
    {
    	return this.modelLocation;
    }

    @Override
    public IModel process(ImmutableMap<String, String> customData)
    {
    	OBJCustomData data;
    	if (this.customData != null)
    	{
    		data = this.customData.duplicate();
    		data.process(customData);
    	}
    	else
    	{
    		data = new OBJCustomData(customData);
    	}
        OBJModel ret = new OBJModel(this.matLib, this.modelLocation, data);
        return ret;
    }

    @Override
    public IModel retexture(ImmutableMap<String, String> textures)
    {
        OBJModel ret = new OBJModel(this.matLib.makeLibWithReplacements(textures), this.modelLocation, this.customData, this.gui3d, this.smooth);
        return ret;
    }
    
    @Override
    public IModelState getDefaultState()
    {
    	return new OBJState(null, TRSRTransformation.identity());
    }

	@Override
	public OBJModel smoothLighting(boolean value)
	{
		if (value == this.smooth)
		{
			return this;
		}
		return new OBJModel(this.matLib, this.modelLocation, this.customData, this.gui3d, value);
	}

	@Override
	public OBJModel gui3d(boolean value)
	{
		if (value == this.gui3d)
		{
			return this;
		}
		return new OBJModel(this.matLib, this.modelLocation, this.customData, value, this.smooth);
	}
    
    @Override
    public String toString()
    {
    	return this.modelLocation.toString();
    }

    public static class Parser
    {
        private static final Pattern WHITE_SPACE = Pattern.compile("\\s+");
        private static Set<String> unknownObjectCommands = Sets.newHashSet();
        private MaterialLibrary materialLibrary = new MaterialLibrary();
        private IResourceManager manager;
        private InputStreamReader objStream;
        private BufferedReader objReader;
        private ResourceLocation objFrom;

        private List<String> groupList = Lists.newArrayList();
        private List<Vertex> vertices = Lists.newArrayList();
        private List<Vector3f> normals = Lists.newArrayList();
        private List<Vector3f> texCoords = Lists.newArrayList();

        public Parser(IResource from, IResourceManager manager) throws IOException
        {
            this.manager = manager;
            this.objFrom = from.getResourceLocation();
            this.objStream = new InputStreamReader(from.getInputStream(), Charsets.UTF_8);
            this.objReader = new BufferedReader(objStream);
        }

        public OBJModel parse() throws IOException
        {
            String currentLine = "";
            Material material = this.materialLibrary.getMaterial(Material.DEFAULT_NAME);
            int usemtlCounter = 0;
            float[][] parsedUVBounds = new float[][] {{0.0f, 0.0f}, {1.0f, 1.0f}};

            for (;;)
            {
                currentLine = objReader.readLine();
                if (currentLine == null) break;
                currentLine = currentLine.trim();
                if (currentLine.isEmpty() || currentLine.startsWith("#")) continue;

                String[] fields = WHITE_SPACE.split(currentLine, 2);
                String key = fields[0];
                String data = fields[1];
                String[] splitData = WHITE_SPACE.split(data);

                if (key.equalsIgnoreCase("mtllib"))
                {
                    this.materialLibrary.parseMaterials(manager, data, objFrom);
                }
                else if (key.equalsIgnoreCase("usemtl"))
                {
                	if (data.equalsIgnoreCase("obj:white")) material = this.materialLibrary.getMaterial(Material.WHITE_NAME);
                	else material = this.materialLibrary.getMaterial(data);
                	if (material == null)
                	{
                		FMLLog.severe("OBJLoader: Model '%s' tried to use a material that wasn't defined in its .mtl file, a plain white texture will be used instead!", objFrom);
                		material = this.materialLibrary.getMaterial(Material.WHITE_NAME);
                	}
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
                    Vector3f normal = new Vector3f(floatSplitData);
                    this.normals.add(normal);
                }
                else if (key.equalsIgnoreCase("vt"))
                {
                    float[] floatSplitData = new float[splitData.length];
                    for (int i = 0; i < splitData.length; i++)
                        floatSplitData[i] = Float.parseFloat(splitData[i]);
                    Vector3f texCoord = new Vector3f(floatSplitData[0], floatSplitData[1], floatSplitData.length == 3 ? floatSplitData[2] : 1);
                    parsedUVBounds[0][0] = Math.min(parsedUVBounds[0][0], texCoord.x);
                    parsedUVBounds[1][0] = Math.max(parsedUVBounds[1][0], texCoord.x);
                    parsedUVBounds[0][1] = Math.min(parsedUVBounds[0][1], texCoord.y);
                    parsedUVBounds[1][1] = Math.max(parsedUVBounds[1][1], texCoord.y);
                    this.texCoords.add(texCoord);
                }
                else if (key.equalsIgnoreCase("f"))
                {
                    String[][] splitSlash = new String[splitData.length][];
                    if (splitData.length > 4) FMLLog.warning("OBJModel.Parser: found a face 'f' with more than 4 vertices, only the first 4 of these vertices will be rendered!");

                    int vert = 0;
                    int texCoord = 0;
                    int norm = 0;

                    List<Vertex> v = Lists.newArrayListWithCapacity(splitData.length);

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
                        }
                        else
                        {
                            vert = Integer.parseInt(splitData[i]);
                            vert = vert < 0 ? this.vertices.size() - 1 : vert - 1;

                            Vertex newV = new Vertex(new Vector4f(this.vertices.get(vert).getPos()), this.vertices.get(vert).getMaterial());
                            v.add(newV);
                        }
                    }

                    Vertex[] va = new Vertex[v.size()];
                    v.toArray(va);
                    Face face = new Face(va, material.name);
                    
                    if (usemtlCounter < this.vertices.size())
                    {
                        for (Vertex ver : face.getVertices())
                        {
                            ver.setMaterial(material);
                        }
                    }

                    if (this.groupList.isEmpty())
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
                    	String[] splitSpace = WHITE_SPACE.split(data);
                        for (String s : splitSpace)
                        {
                            groupList.add(s);
                        }
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
            
            if (!this.materialLibrary.getGroups().get(Group.DEFAULT_NAME).hasFaces())
            {
            	this.materialLibrary.getGroups().remove(Group.DEFAULT_NAME);
            }

            OBJModel model = new OBJModel(this.materialLibrary, this.objFrom, parsedUVBounds);
            return model;
        }
    }

    public static class MaterialLibrary
    {
        private static final Pattern WHITE_SPACE = Pattern.compile("\\s+");
        private Set<String> unknownMaterialCommands = Sets.newHashSet();
        private Map<String, Material> materials = Maps.newHashMap();
        private Map<String, Group> groups = Maps.newHashMap();
        private InputStreamReader mtlStream;
        private BufferedReader mtlReader;

        public MaterialLibrary()
        {
            this.groups.put(Group.DEFAULT_NAME, new Group(Group.DEFAULT_NAME, null));
            this.initMaterialMap();
        }
        
        protected static MaterialLibrary copyFrom(MaterialLibrary matLib)
        {
        	MaterialLibrary ret = new MaterialLibrary();
        	ret.groups = matLib.groups;
        	ret.materials = matLib.materials;
        	ret.mtlReader = matLib.mtlReader;
        	ret.mtlStream = matLib.mtlStream;
        	ret.unknownMaterialCommands = matLib.unknownMaterialCommands;
        	return ret;
        }
        
        private void initMaterialMap()
        {
        	this.materials.put(Material.DEFAULT_NAME, new Material(Texture.WHITE));
            this.materials.put(Material.WHITE_NAME, new Material());
        }

        public MaterialLibrary makeLibWithReplacements(ImmutableMap<String, String> replacements)
        {
        	//TODO: double check that this works properly
        	Map<String, Material> mats = Maps.newHashMap();
        	for (Map.Entry<String, Material> e : this.materials.entrySet())
        	{
        		if (replacements.containsKey(e.getKey()))
        		{
        			Texture current = e.getValue().getTexture();
        			Texture replacement = Texture.copyFrom(current);
        			String repPath = replacements.get(e.getKey());
        			if (repPath.startsWith("#"))
        			{
        				repPath = repPath.substring(1);
        				if (this.materials.containsKey(repPath))
        				{
        					replacement.setPath(this.materials.get(repPath).getTexture().getPath());
        				}
        				else
        				{
        					FMLLog.warning("OBJModel.MaterialLibrary: Tried to set the texture path for material '%s' to the texture path of a non-existant material '%s', the original texture will be used.", e.getKey(), repPath);
        				}
        			}
        			else
        			{
        				replacement.setPath(repPath);
        			}
        			Material repMat = Material.copyFrom(e.getValue());
        			repMat.setTexture(replacement);
        			mats.put(e.getKey(), repMat);
        		}
        		else
        		{
        			mats.put(e.getKey(), Material.copyFrom(e.getValue()));
        		}
        	}
        	MaterialLibrary ret = MaterialLibrary.copyFrom(this);
        	ret.materials = mats;
        	return ret;
        }

        public Map<String, Group> getGroups()
        {
            return this.groups;
        }
        
        public ImmutableMap<String, Group> getGroupsWithFaces()
        {
        	ImmutableMap.Builder<String, Group> builder = ImmutableMap.builder();
        	for (Map.Entry<String, Group> e : this.groups.entrySet())
        	{
        		if (!e.getValue().faces.isEmpty())
        		{
        			builder.put(e);
        		}
        	}
        	return builder.build();
        }
        
        public ImmutableList<String> getGroupNames()
        {
        	return ImmutableList.copyOf(this.groups.keySet());
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

        public Material getMaterial(String name)
        {
            return this.materials.get(name);
        }

        public ImmutableList<String> getMaterialNames()
        {
            return ImmutableList.copyOf(this.materials.keySet());
        }
        
        public static Vector3f[] getDefaultUVs(Pair<Boolean, Boolean> flip)
        {
        	boolean u = flip != null ? flip.getLeft() : false;
        	boolean v = flip != null ? flip.getRight() : false;
        	Vector3f[] uvs = new Vector3f[4];
        	uvs[0] = new Vector3f(u ? 1.0f : 0.0f, v ? 0.0f : 1.0f, 1.0f);
        	uvs[1] = new Vector3f(u ? 0.0f : 1.0f, v ? 0.0f : 1.0f, 1.0f);
        	uvs[2] = new Vector3f(u ? 0.0f : 1.0f, v ? 1.0f : 0.0f, 1.0f);
        	uvs[3] = new Vector3f(u ? 1.0f : 0.0f, v ? 1.0f : 0.0f, 1.0f);
        	return uvs;
        }

        public void parseMaterials(IResourceManager manager, String libs, ResourceLocation from) throws IOException
        {
        	this.materials.clear();
        	this.initMaterialMap();
        	String[] splitPath = WHITE_SPACE.split(libs);
        	for (String path : splitPath)
        	{
        		boolean hasSetTexture = false;
            	boolean hasSetColor = false;
            	String domain = from.getResourceDomain();
            	if (!path.contains("/"))
                	path = from.getResourcePath().substring(0, from.getResourcePath().lastIndexOf("/") + 1) + path;
            	mtlStream = new InputStreamReader(manager.getResource(new ResourceLocation(domain, path)).getInputStream(), Charsets.UTF_8);
            	mtlReader = new BufferedReader(mtlStream);

            	String currentLine = "";
            	Material material = new Material();

            	for (;;)
            	{
                	currentLine = mtlReader.readLine();
                	if (currentLine == null) break;
                	currentLine = currentLine.trim();
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
                            float alpha = material.getColor() != null ? material.getColor().getW() : 1.0f;
                        	Vector4f color = new Vector4f(Float.parseFloat(rgbStrings[0]), Float.parseFloat(rgbStrings[1]), Float.parseFloat(rgbStrings[2]), alpha);
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
                    	String[] splitData = WHITE_SPACE.split(data);
                    	float alpha = Float.parseFloat(splitData[splitData.length - 1]);
                    	if (key.equalsIgnoreCase("Tr")) alpha = 1.0f - alpha;
                        material.getColor().setW(alpha);
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
        
        protected static Material copyFrom(Material material)
        {
        	return new Material(material.color, material.texture, material.name);
        }

        protected void setName(String name)
        {
            this.name = name != null ? name : DEFAULT_NAME;
        }

        public String getName()
        {
            return this.name;
        }

        protected void setColor(Vector4f color)
        {
            this.color = color;
        }

        public Vector4f getColor()
        {
            return this.color;
        }

        //TODO: can't really be changed after OBJModel.bake() is called...
        protected void setTexture(Texture texture)
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
        	ToStringBuilder build = new ToStringBuilder(this, STYLE);
        	build.append("Name", this.name);
        	build.appendToString(this.texture.toString());
        	build.append("Color", this.color);
        	build.append("Is White", this.isWhite());
        	return build.toString();
        }
    }

    //TODO: evaluate the usefulness of having an entire class for this... currently no way to change position/scale/rotation
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
        
        protected static Texture copyFrom(Texture texture)
        {
        	return new Texture(texture.path, texture.position, texture.scale, texture.rotation);
        }

        public ResourceLocation getTextureLocation()
        {
            return new ResourceLocation(this.path);
        }

        protected void setPath(String path)
        {
            this.path = path;
        }

        public String getPath()
        {
            return this.path;
        }

        protected void setPosition(Vector2f position)
        {
            this.position = position;
        }

        public Vector2f getPosition()
        {
            return this.position;
        }

        protected void setScale(Vector2f scale)
        {
            this.scale = scale;
        }

        public Vector2f getScale()
        {
            return this.scale;
        }

        protected void setRotation(float rotation)
        {
            this.rotation = rotation;
        }

        public float getRotation()
        {
            return this.rotation;
        }
        
        @Override
        public String toString()
        {
        	ToStringBuilder build = new ToStringBuilder(this, STYLE);
        	build.append("Location", this.path);
        	build.append("Position", this.position);
        	build.append("Scale", this.scale);
        	build.append("Rotation", this.rotation);
        	return build.toString();
        }
    }

    public static class Face
    {
        private Vertex[] verts = new Vertex[4];
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
        
        public Face(Vertex[] verts, String materialName, boolean isTri)
        {
        	this.verts = verts != null && verts.length > 2 ? verts : null;
        	setMaterialName(materialName);
        	this.isTri = isTri;
        }

        private void checkData()
        {
            if (this.verts != null && this.verts.length == 3)
            {
                this.isTri = true;
                this.verts = new Vertex[]{this.verts[0], this.verts[1], this.verts[2], this.verts[2]};
            }
        }

        protected void setMaterialName(String materialName)
        {
            this.materialName = materialName != null && !materialName.isEmpty() ? materialName : this.materialName;
        }

        public String getMaterialName()
        {
            return this.materialName;
        }
        
        public boolean isTriangle() 
        {
            return this.isTri;
        }

        protected boolean setVertices(Vertex[] verts)
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
        
        private static Vertex[] ensureTextureCoordinates(Vertex[] verts, boolean isTri)
        {
        	Vector3f[] defUVs = MaterialLibrary.getDefaultUVs(Pair.of(false, false));
        	Vertex[] retVerts = new Vertex[verts.length];
        	for (int i = 0; i < verts.length; i++)
        	{
        		retVerts[i] = Vertex.copyFrom(verts[i]);
        		if (!retVerts[i].hasTextureCoordinate())
        		{
        			retVerts[i].setTextureCoordinate(defUVs[(isTri && i == 3) ? 2 : i]);
        		}
        	}
        	return retVerts;
        }
        
        public static Vertex[] normalizeUVs(Vertex[] verts, Pair<Boolean, Boolean> flags, float[][] bounds)
        {
        	Vertex[] retVerts = new Vertex[verts.length];
        	for (int i = 0; i < verts.length; i++)
        	{
        		retVerts[i] = Vertex.copyFrom(verts[i]);
        		if (flags.getLeft()) retVerts[i].texCoord.x = (retVerts[i].texCoord.x - bounds[0][0]) / (bounds[1][0] - bounds[0][0]);
        		if (flags.getRight()) retVerts[i].texCoord.y = (retVerts[i].texCoord.y - bounds[0][1]) / (bounds[1][1] - bounds[0][1]);
        	}
        	return retVerts;
        }
        
        public static Vertex[] unitizeUVs(Vertex[] verts, Pair<Boolean, Boolean> flags, boolean isTri)
        {
        	Vector3f[] defUVs = MaterialLibrary.getDefaultUVs(Pair.of(false, false));
        	Vertex[] retVerts = new Vertex[verts.length];
        	for (int i = 0; i < verts.length; i++)
        	{
        		retVerts[i] = Vertex.copyFrom(verts[i]);
        		if (flags.getLeft()) retVerts[i].texCoord.x = defUVs[(isTri && i == 3) ? 2 : i].x;
        		if (flags.getRight()) retVerts[i].texCoord.y = defUVs[(isTri && i == 3) ? 2 : i].y;
        	}
        	return retVerts;
        }
        
        public static Vertex[] flipUVs(Vertex[] verts, Pair<Boolean, Boolean> flags)
        {
        	Vertex[] retVerts = new Vertex[verts.length];
        	for (int i = 0; i < verts.length; i++)
        	{
        		retVerts[i] = Vertex.copyFrom(verts[i]);
        		if (flags.getLeft()) retVerts[i].texCoord.x = 1 - retVerts[i].texCoord.x;
        		if (flags.getRight()) retVerts[i].texCoord.y = 1 - retVerts[i].texCoord.y;
        	}
        	return retVerts;
        }

        public Face bake(TRSRTransformation transform, OBJCustomData customData, Map<String, Material> materials)
        {
        	//Calculate and set vertex uvs
        	Vertex[] vertices = new Vertex[this.verts.length];
        	for (int i = 0; i < this.verts.length; i++)
        	{
        		vertices[i] = Vertex.copyFrom(this.verts[i]);
        		//update vertex material in case it has changed
        		if (materials != null && !vertices[i].getMaterial().equals(materials.get(vertices[i].getMaterial().getName())))
        		{
        			vertices[i].setMaterial(materials.get(vertices[i].getMaterial().getName()));
        		}
        	}
        	vertices = ensureTextureCoordinates(vertices, this.isTri);
        	
        	if (customData != null && customData.hasProcessed)
        	{
        		for (Map.Entry<OBJCustomData.Keys, Pair<Boolean, Boolean>> e : customData.processUVData.entrySet())
        		{
        			switch (e.getKey())
        			{
        			case NORMALIZE_UVS: vertices = normalizeUVs(vertices, e.getValue(), customData.parsedUVBounds); break;
        			case UNITIZE_UVS: vertices = unitizeUVs(vertices, e.getValue(), this.isTri); break;
        			case FLIP_UVS: vertices = flipUVs(vertices, e.getValue()); break;
        			default: break;
        			}
        		}
        	}
        	
        	// Calculate and set vertex normals
            Matrix4f m = transform.getMatrix();
            Matrix3f mn = null;
            boolean useFaceNormal = false;

            for (int i = 0; i < this.verts.length; i++)
            {
            	Vector4f pos = vertices[i].getPos(), newPos = new Vector4f();
            	m.transform(pos, newPos);
            	vertices[i].setPos(newPos);
            	if (!vertices[i].hasNormal()) useFaceNormal = true;
            }
            
            if (useFaceNormal)
            {
            	this.useFaceNormal(vertices);
            }
            else
            {
            	for (int i = 0; i < this.verts.length; i++)
            	{
            		if (mn == null)
            		{
            			mn = new Matrix3f();
            			m.getRotationScale(mn);
            			mn.invert();
            			mn.transpose();
            		}
            		Vector3f normal = vertices[i].getNormal(), newNormal = new Vector3f();
            		mn.transform(normal, newNormal);
            		newNormal.normalize();
            		vertices[i].setNormal(newNormal);
            	}
            }
            
            return new Face(vertices, this.materialName, this.isTri);
        }
        
        private void useFaceNormal(Vertex[] verts)
        {
        	Vector3f a = verts[2].getPos3();
        	a.sub(verts[0].getPos3());
        	Vector3f b = verts[3].getPos3();
        	b.sub(verts[1].getPos3());
        	a.cross(a, b);
        	a.normalize();
        	for (int i = 0; i < verts.length; i++)
        	{
        		verts[i].setNormal(a);
        	}
        }

        public Vector3f getNormal()
        {
            Vector3f a = this.verts[2].getPos3();
            a.sub(this.verts[0].getPos3());
            Vector3f b = this.verts[3].getPos3();
            b.sub(this.verts[1].getPos3());
            a.cross(a, b);
            a.normalize();
            return new Vector3f(a);
        }
        
        @Override
        public String toString()
        {
        	ToStringBuilder build = new ToStringBuilder(this, STYLE);
        	build.append("Is Triangle", this.isTri);
        	build.append("Vertices", this.verts, false);
        	build.append("Material Name", this.materialName);
        	return build.toString();
        }
    }

    public static class Vertex
    {
        protected Vector4f position;
        protected Vector3f normal;
        protected Vector3f texCoord;
        protected Material material = new Material();

        public Vertex(Vector4f position, Material material)
        {
            this.position = position;
            this.material = material;
        }
        
        public static Vertex copyFrom(Vertex vert)
        {
        	Vertex retVert = new Vertex(vert.getPos(), vert.getMaterial());
        	retVert.setTextureCoordinate(vert.getTextureCoordinate());
        	retVert.setNormal(vert.getNormal());
        	return retVert;
        }

        protected void setPos(Vector4f position)
        {
            this.position = position;
        }

        public Vector4f getPos()
        {
            return new Vector4f(this.position);
        }

        public Vector3f getPos3()
        {
            return new Vector3f(this.position.x, this.position.y, this.position.z);
        }

        public boolean hasNormal()
        {
            return this.normal != null;
        }

        protected void setNormal(Vector3f normal)
        {
            this.normal = normal;
        }

        public Vector3f getNormal()
        {
            return this.normal == null ? null : new Vector3f(this.normal);
        }

        public boolean hasTextureCoordinate()
        {
            return this.texCoord != null;
        }

        protected void setTextureCoordinate(Vector3f texCoord)
        {
            this.texCoord = texCoord;
        }

        public Vector3f getTextureCoordinate()
        {
            return this.texCoord == null ? null : new Vector3f(this.texCoord);
        }

        protected void setMaterial(Material material)
        {
            this.material = material;
        }

        public Material getMaterial()
        {
            return Material.copyFrom(this.material);
        }

        @Override
        public String toString()
        {
        	ToStringBuilder build = new ToStringBuilder(this, STYLE);
        	build.append("Position", this.position);
        	build.append("Normal", this.normal);
        	build.append("Texture Coordinate", this.texCoord);
        	build.append("Material", this.material);
        	return build.toString();
        }
    }

    public static class Group implements IModelPart
    {
        public static final String DEFAULT_NAME = "OBJModel.Default.Group.Name";
        public static final String ALL = "OBJModel.Group.All.Key";
        public static final String ALL_EXCEPT = "OBJModel.Group.All.Except.Key";
        private String name = DEFAULT_NAME;
        private LinkedHashSet<Face> faces = Sets.newLinkedHashSet();

        public Group(String name, LinkedHashSet<Face> faces)
        {
            this.name = name != null ? name : DEFAULT_NAME;
            this.faces = faces == null ? Sets.<Face>newLinkedHashSet() : faces;
        }

        public LinkedHashSet<Face> applyTransform(Optional<TRSRTransformation> transform, OBJModel model, Map<String, Material> materials)
        {
            LinkedHashSet<Face> faceSet = Sets.newLinkedHashSet();
            for (Face f : this.faces)
            {
                faceSet.add(f.bake(transform.or(TRSRTransformation.identity()), model.customData, materials));
            }
            return faceSet;
        }

        public String getName()
        {
            return this.name;
        }

        public boolean hasFaces()
        {
        	return this.faces != null && !this.faces.isEmpty();
        }
        
        public LinkedHashSet<Face> getFaces()
        {
            return Sets.newLinkedHashSet(this.faces);
        }

        protected void setFaces(LinkedHashSet<Face> faces)
        {
            this.faces = faces;
        }

        protected void addFace(Face face)
        {
            this.faces.add(face);
        }

        protected void addFaces(List<Face> faces)
        {
            this.faces.addAll(faces);
        }
        
        @Override
        public String toString()
        {
        	ToStringBuilder build = new ToStringBuilder(this, STYLE);
        	build.append("Name", this.name);
        	build.append("Faces", this.faces, false);
        	return build.toString();
        }
    }

    public static class OBJState implements IModelState
    {
    	/**
    	 * If shownConfigs == null, all configurations will be shown.
    	 * If shownConfigs.isEmpty(), all configurations will be hidden.
    	 * Otherwise, only listed configurations will be shown, the rest hidden.
    	 */
    	private List<String> shownConfigs = null;
    	private Map<String, Vector4f> materialColorMap = Maps.newHashMap();
    	private boolean ignoreHidden = false;
    	private IModelState parent;
    	
    	public OBJState() {}
        
        public OBJState(IModelState parent)
        {
        	this.parent = parent;
        }
    	
    	public OBJState(List<String> activeConfigs)
    	{
    		this.shownConfigs = activeConfigs;
    	}
    	
    	public OBJState(List<String> activeConfigs, IModelState parent)
    	{
    		this.shownConfigs = activeConfigs;
    		this.parent = parent;
    	}
    	
    	public static OBJState copyFrom(OBJState state)
    	{
    		OBJState retState = new OBJState();
    		retState.shownConfigs = state.shownConfigs != null ? Lists.newArrayList(state.shownConfigs) : null;
    		retState.materialColorMap = state.materialColorMap != null ? Maps.newHashMap(state.materialColorMap) : Maps.<String, Vector4f>newHashMap();
    		retState.ignoreHidden = state.ignoreHidden;
    		retState.parent = state.parent;
    		return retState;
    	}

    	public OBJState setIgnoreHidden(boolean ignoreHidden)
    	{
    		this.ignoreHidden = ignoreHidden;
    		return this;
    	}
    	
    	public boolean getIgnoreHidden()
    	{
    		return this.ignoreHidden;
    	}
        
    	/**
    	 * Show configuration named with configName.
    	 * @param configName, name of the configuration to show
    	 * @return this
    	 */
        public OBJState showConfig(String configName)
        {
        	if (configName != null)
        	{
        		if (this.shownConfigs == null) this.shownConfigs = Lists.newArrayList();
        		this.shownConfigs.add(configName);
        	}
        	return this;
        }
        
        /**
         * Show configurations named in configNames.
         * If ignoreHidden == true, only the lists of shown Groups in each configuration will be combined,
         * the hidden lists will be ignored. The default is false.
         * This can be useful if the configurations being combined have the same Groups in different states
         * (ie. one configurations has a Group listed as shown, and another configurations has that same Group listed as hidden).
         * When combining configurations, the lists of shown Groups get combined first, then the lists of hidden Groups.
         * 
         * @param configNames, list of configurations to combine when baking the model.
         * @param ignoreHidden, true if hidden Groups should be ignored when combining
         * @return this
         */
        public OBJState showConfigs(List<String> configNames, boolean ignoreHidden)
        {
        	if (this.shownConfigs == null) this.shownConfigs = Lists.newArrayList();
        	this.shownConfigs.addAll(configNames);
        	this.ignoreHidden = ignoreHidden;
        	return this;
        }
        
        public ImmutableList<String> getShownConfigs()
        {
        	return this.shownConfigs == null ? null : ImmutableList.copyOf(this.shownConfigs);
        }
        
        public OBJState setShowAllConfigs()
        {
        	this.shownConfigs = null;
        	return this;
        }
        
        public OBJState setHideAllConfigs()
        {
        	this.shownConfigs = Lists.newArrayList();
        	return this;
        }
        
        public boolean shouldShowAllConfigs()
        {
        	return this.shownConfigs == null;
        }
        
        public boolean shouldHideAllConfigs()
        {
        	return this.shownConfigs != null && this.shownConfigs.isEmpty();
        }
        
        public OBJState setParent(IModelState parent)
        {
        	this.parent = parent;
        	return this;
        }
        
        public IModelState getParent()
        {
        	return this.parent;
        }

        public IModelState getParent(IModelState parent)
        {
            if (parent == null) return null;
            else if (parent instanceof OBJState) return ((OBJState) parent).parent;
            return parent;
        }

        public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part)
        {
            if (this.parent != null) return parent.apply(part);
            return Optional.absent();
        }
        
        public OBJState setMaterialColor(String materialName, int color)
        {
        	float a = (color >> 24 & 255) / 255;
        	float r = (color >> 16 & 255) / 255;
        	float g = (color >> 8 & 255) / 255;
        	float b = (color & 255) / 255;
        	return this.setMaterialColor(materialName, new Vector4f(r, g, b, a));
        }
        
        public OBJState setMaterialColor(String materialName, Vector4f color)
        {
        	this.materialColorMap.put(materialName, color);
        	return this;
        }
        
        public Map<String, Vector4f> getMaterialColorMap()
        {
        	if (this.materialColorMap == null) this.materialColorMap = Maps.newHashMap();
        	return this.materialColorMap;
        }

        @Override
        public String toString()
        {
        	ToStringBuilder build = new ToStringBuilder(this, STYLE);
        	build.append("Parent", this.parent);
        	build.append("Material Color Map", this.materialColorMap, false);
        	build.append("Shown Configurations", this.shownConfigs, false);
        	build.append("Ignore Hidden", this.ignoreHidden);
        	return build.toString();
        }

        @Override
        public int hashCode()
        {
            return Objects.hashCode(this.shownConfigs, this.parent, this.materialColorMap, this.ignoreHidden);
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
            return Objects.equal(this.shownConfigs, other.shownConfigs) &&
                Objects.equal(this.parent, other.parent) &&
                Objects.equal(this.materialColorMap, other.materialColorMap) &&
                Objects.equal(this.ignoreHidden, other.ignoreHidden);
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
        private final VertexFormat format;
        private final boolean gui3d;
        private final boolean smooth;
        private IModelState state;
        private Set<BakedQuad> quads;
        private ImmutableMap<String, TextureAtlasSprite> textures;
        private TextureAtlasSprite sprite = ModelLoader.White.instance;
        private Map<Group, Boolean> visibilityMap = Maps.newHashMap();
        private Map<String, Vector4f> colorMap = Maps.newHashMap();
        private Map<String, Material> materials;
        

        public OBJBakedModel(OBJModel model, IModelState state, VertexFormat format, ImmutableMap<String, TextureAtlasSprite> textures)
        {
            this(model, state, format, textures, null);
        }
        
        public OBJBakedModel(OBJModel model, IModelState state, VertexFormat format, ImmutableMap<String, TextureAtlasSprite> textures, Map<Group, Boolean> visibilityMap)
        {
        	this.model = model;
        	this.gui3d = this.model.gui3d;
        	this.smooth = this.model.smooth;
        	this.materials = Maps.newHashMap(this.model.getMatLib().materials);
        	this.state = state;
        	this.format = format;
        	this.textures = textures;
        	if (visibilityMap == null) this.fillMap();
        	else this.visibilityMap = visibilityMap;
        }
        
        private void fillMap()
        {
        	for (Group g : this.model.getMatLib().getGroups().values())
        	{
        		this.visibilityMap.put(g, true);
        	}
        }

        @Override
        public List<BakedQuad> getFaceQuads(EnumFacing side)
        {
            return Collections.emptyList();
        }
        
        private void bakeFaces(Set<Face> faces, Optional<TRSRTransformation> transform)
        {
        	for (Group g : this.model.getMatLib().getGroups().values())
        	{
        		faces.addAll(g.applyTransform(transform, this.model, this.materials));
        	}
        }

        @Override
        public List<BakedQuad> getGeneralQuads()
        {
        	if (this.quads == null)
        	{
        		boolean applyToAll = true;
        		this.quads = Collections.synchronizedSet(Sets.<BakedQuad>newLinkedHashSet());
        		Set<Face> faces = Collections.synchronizedSet(Sets.<Face>newLinkedHashSet());
        		//TODO: allow Groups to have their own TRSRTransformation(?)
        		Optional<TRSRTransformation> transform = this.state.apply(Optional.<IModelPart>absent());
        		if (this.state instanceof OBJState)
        		{
        			OBJState s = ((OBJState) this.state);
        			this.colorMap = s.getMaterialColorMap();
        			if (this.materials != null)
        			{
        				for (Map.Entry<String, Vector4f> e : this.colorMap.entrySet())
        				{
        					if (this.materials.containsKey(e.getKey()))
        					{
        						this.materials.get(e.getKey()).setColor(e.getValue());
        					}
        				}
        			}
        			
        			OBJCustomData.GroupConfig activeConfig = this.model.getCustomData().getConfigHandler().getCombinedConfig(s.getShownConfigs(), s.getIgnoreHidden());
        			if (!s.shouldHideAllConfigs() && activeConfig != null)
        			{
        				applyToAll = false;
        				for (Map.Entry<String, Boolean> e : activeConfig.getVisMap().entrySet())
        				{
        					if (e.getValue())
        					{
        						faces.addAll(this.model.getMatLib().getGroups().get(e.getKey()).applyTransform(transform, this.model, this.materials));
        					}
        				}
        			}
        			else
        			{
        				applyToAll = !s.shouldHideAllConfigs() || s.shouldShowAllConfigs();
        			}
//        			else if (s.shouldShowAllConfigs())
//        			{
//        				for (Group g : this.model.getMatLib().getGroups().values())
//        				{
//        					faces.addAll(g.applyTransform(transform, this.model, this.materials));
//        				}
//        			}
        		}
//        		else
//        		{
//        			//TODO: allow for a "default configuration" to use here?
//        			for (Group g : this.model.getMatLib().getGroups().values())
//        			{
//        				faces.addAll(g.applyTransform(transform, this.model, this.materials));
//        			}
//        		}
        		if (applyToAll)
        		{
        			this.bakeFaces(faces, transform);
        		}
        		
        		for (Face f : faces)
        		{
        			if (this.materials.get(f.getMaterialName()).isWhite())
        			{
        				this.sprite = ModelLoader.White.instance;
        			}
        			else
        			{
        				this.sprite = this.textures.get(f.getMaterialName());
        			}
        			UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(this.format);
        			Vector3f[] defUVs = MaterialLibrary.getDefaultUVs(Pair.of(false, false));
        			Vector3f faceNormal = f.getNormal();
        			builder.setQuadOrientation(EnumFacing.getFacingFromVector(faceNormal.x, faceNormal.y, faceNormal.z));
        			builder.setQuadColored();
        			putVertexData(builder, f.verts[0], defUVs[0], this.sprite);
        			putVertexData(builder, f.verts[1], defUVs[1], this.sprite);
        			putVertexData(builder, f.verts[2], defUVs[2], this.sprite);
        			putVertexData(builder, f.verts[3], defUVs[3], this.sprite);
        			this.quads.add(builder.build());
        		}
        	}
        	List<BakedQuad> quadList = Collections.synchronizedList(Lists.newArrayList(this.quads));
        	return quadList;
        }

        private final void putVertexData(UnpackedBakedQuad.Builder builder, Vertex v, Vector3f defUV, TextureAtlasSprite sprite)
        {
            for (int e = 0; e < format.getElementCount(); e++)
            {
                switch (format.getElement(e).getUsage())
                {
                    case POSITION:
                        builder.put(e, v.getPos().x, v.getPos().y, v.getPos().z, v.getPos().w);
                        break;
                    case COLOR:
                        float d = LightUtil.diffuseLight(v.getNormal().x, v.getNormal().y, v.getNormal().z);
                        builder.put(e,
                        		d * v.getMaterial().getColor().x,
                        		d * v.getMaterial().getColor().y,
                        		d * v.getMaterial().getColor().z,
                        		v.getMaterial().getColor().w);
                        break;
                    case UV:
                    	if (this.model.customData.useFullAtlas)
                    		builder.put(e, defUV.x, defUV.y, 0, 1);
                    	else if (sprite.equals(ModelLoader.White.instance))
                    		builder.put(e,
                    				sprite.getInterpolatedU(defUV.x * 16),
                    				sprite.getInterpolatedV(defUV.y * 16),
                    				0, 1);
                    	else
                    		builder.put(e,
                    				sprite.getInterpolatedU(v.getTextureCoordinate().x * 16),
                    				sprite.getInterpolatedV(v.getTextureCoordinate().y * 16),
                    				0, 1);
                        break;
                    case NORMAL:
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
            return this.smooth;
        }

        @Override
        public boolean isGui3d()
        {
            return this.gui3d;
        }

        @Override
        public boolean isBuiltInRenderer()
        {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleTexture()
        {
        	//TODO: any way to color this for vertex-colored blocks?
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
            return this.format;
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
            			IModelState parent = this.state;
            			if (parent instanceof OBJState)
            			{
            				parent = ((OBJState) parent).getParent();
            			}
            			OBJState newState = OBJState.copyFrom(s);
            			newState.setParent(parent);
            			return getCachedModel(newState);
            		}
            	}
            }
            return this;
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
            return IPerspectiveAwareModel.MapWrapper.handlePerspective(this, this.state, cameraTransformType);
        }

        @Override
        public String toString()
        {
        	//TODO
            return this.model.modelLocation.toString();
        }
    }
}
