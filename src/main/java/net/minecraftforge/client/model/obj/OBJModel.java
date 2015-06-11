package net.minecraftforge.client.model.obj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
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
import net.minecraftforge.client.model.IColoredBakedQuad.ColoredBakedQuad;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelPart;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.BufferUtils;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class OBJModel implements IModel {
    static final Set<String> unknownObjectCommands = new HashSet<String>();
    private MaterialLibrary matLib;
    private IModelState state;
    
    public OBJModel(MaterialLibrary matLib) {
        this.matLib = matLib;
    }
    
    private void setModelState(IModelState state) {
        this.state = state;
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
        List<ResourceLocation> textures = new ArrayList<ResourceLocation>();
        while (materialIterator.hasNext()) {
            Material mat = materialIterator.next();
            ResourceLocation textureLoc = new ResourceLocation(mat.getTexture().getPath());
            if (!textures.contains(textureLoc)) textures.add(textureLoc);
        }
        return textures;
    }

    @Override
    public IFlexibleBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        ImmutableMap.Builder<String, TextureAtlasSprite> builder = ImmutableMap.builder();
        Iterator<Material> materialIterator = this.matLib.library.values().iterator();
        while (materialIterator.hasNext()) {
            Material mat = materialIterator.next();
            builder.put(mat.texture.path, bakedTextureGetter.apply(new ResourceLocation(mat.getTexture().getPath())));
            builder.put("missingno", bakedTextureGetter.apply(new ResourceLocation("missingno")));
        }
        
        return new OBJBakedModel(this, state, format, bakedTextureGetter);
    }

    public OBJState getDefaultState()
    {
        return new OBJState(null, ItemCameraTransforms.DEFAULT);
    }
    
    public MaterialLibrary getMatLib() {
        return this.matLib;
    }
    
    public static class Parser
    {
        public MaterialLibrary materialLibrary = new MaterialLibrary();
        private IResourceManager manager;
        private InputStreamReader objStream;
        private BufferedReader objReader;
        private ResourceLocation objFrom;
        
        private List<String> elementList = new ArrayList<String>();
        private List<Vertex> vertices = new ArrayList<Vertex>();
        private List<Normal> normals = new ArrayList<Normal>();
        private List<TextureCoordinate> texCoords = new ArrayList<TextureCoordinate>();
        
        public Parser(IResource from, IResourceManager manager) throws IOException
        {
            this.manager = manager;
            this.objFrom = from.getResourceLocation();
            this.objStream = new InputStreamReader(from.getInputStream(), Charsets.UTF_8);
            this.objReader = new BufferedReader(objStream);
        }
        
        public List<String> getElements() {
            return this.elementList;
        }
        
        public OBJModel parse() throws IOException
        {
            String currentLine = "";
            String nextLine = "";
            String nextKey = "";
            String nextData = "";
            Material material = new Material();
            
            for (;;)
            {
                currentLine = nextLine;
                nextLine = objReader.readLine();
                
                if (currentLine == null) break;
                if (currentLine.isEmpty() || currentLine.startsWith("#")) continue;
                if (nextLine != null && !nextLine.isEmpty() && !nextLine.startsWith("#"))
                {
                    String[] nextFields = nextLine.split(" ", 2);
                    nextKey = nextFields[0];
                    nextData = nextFields[1];
                }
                                
                String[] fields = currentLine.split(" ", 2);
                String key = fields[0];
                String data = fields[1];
                
                if (key.equalsIgnoreCase("mtllib")) materialLibrary.parseMaterials(manager, data, objFrom);
                else if (key.equalsIgnoreCase("usemtl")) material = materialLibrary.materials.get(data);
                else if (key.equalsIgnoreCase("v")) {
                    String[] splitData = data.split(" ");
                    float[] floatSplitData = new float[splitData.length];
                    for (int i = 0; i < splitData.length; i++) floatSplitData[i] = Float.parseFloat(splitData[i]);
                    Vector4f pos = new Vector4f(floatSplitData[0], floatSplitData[1], floatSplitData[2], floatSplitData.length == 4 ? floatSplitData[3] : 1);
                    Vertex vertex = new Vertex(pos, new Vector4f(0.5f, 0.5f, 0.5f, 1.0f));
                    this.vertices.add(vertex);
                }
                else if (key.equalsIgnoreCase("vn")) {
                    String[] splitData = data.split(" ");
                    float[] floatSplitData = new float[splitData.length];
                    for (int i = 0; i < splitData.length; i++) floatSplitData[i] = Float.parseFloat(splitData[i]);
                    Normal normal = new Normal(new Vector3f(floatSplitData[0], floatSplitData[1], floatSplitData[2]));
                    this.normals.add(normal);
                }
                else if (key.equalsIgnoreCase("vt")) {
                    String[] splitData = data.split(" ");
                    float[] floatSplitData = new float[splitData.length];
                    for (int i = 0; i < splitData.length; i++) floatSplitData[i] = Float.parseFloat(splitData[i]);
                    TextureCoordinate texCoord = new TextureCoordinate(new Vector3f(floatSplitData[0], floatSplitData[1], floatSplitData.length == 3 ? floatSplitData[2] : 1));
                    this.texCoords.add(texCoord);
                }
                else if (key.equalsIgnoreCase("f")) {
                    String[] splitSpace = data.split(" ");
                    String[][] splitSlash = new String[splitSpace.length][];
                    
                    int[] verts = new int[splitSpace.length];
                    int[] texCoords = new int[splitSpace.length];
                    int[] norms = new int[splitSpace.length];
                    
                    List<Vertex> v = new ArrayList<Vertex>(verts.length);
                    List<TextureCoordinate> t = new ArrayList<TextureCoordinate>(texCoords.length);
                    List<Normal> n = new ArrayList<Normal>(norms.length);
                    
                    for (int i = 0; i < splitSpace.length; i++) {
                        if (splitSpace[i].contains("//")) {
                            splitSlash[i] = splitSpace[i].split("//");
                            
                            verts[i] = Integer.parseInt(splitSlash[i][0]);
                            verts[i] = verts[i] < 0 ? verts[i] = this.vertices.size() - 1 : verts[i] - 1;
                            norms[i] = Integer.parseInt(splitSlash[i][1]);
                            norms[i] = norms[i] < 0 ? norms[i] = this.normals.size() - 1 : norms[i] - 1;
                            
                            v.add(this.vertices.get(verts[i]));
                            n.add(this.normals.get(norms[i]));
                        } else if (splitSpace[i].contains("/")) {
                            splitSlash[i] = splitSpace[i].split("/");
                            
                            verts[i] = Integer.parseInt(splitSlash[i][0]);
                            verts[i] = verts[i] < 0 ? this.vertices.size() - 1 : verts[i] - 1;
                            texCoords[i] = Integer.parseInt(splitSlash[i][1]);
                            texCoords[i] = texCoords[i] < 0 ? this.texCoords.size() - 1 : texCoords[i] - 1;
                            if (splitSlash[i].length > 2) {
                                norms[i] = Integer.parseInt(splitSlash[i][2]);
                                norms[i] = norms[i] < 0 ? this.normals.size() - 1 : norms[i] - 1;
                            }
                            
                            v.add(this.vertices.get(verts[i]));
                            t.add(this.texCoords.get(texCoords[i]));
                            if (splitSlash[i].length > 2) n.add(this.normals.get(norms[i]));
                        } else {
                            splitSlash[i] = splitSpace[i].split("");
                            
                            verts[i] = Integer.parseInt(splitSlash[i][0]);
                            verts[i] = verts[i] < 0 ? this.vertices.size() - 1 : verts[i] - 1;
                            
                            v.add(this.vertices.get(verts[i]));
                        }
                    }
                    
                    Vertex[] va = new Vertex[v.size()];
                    v.toArray(va);
                    TextureCoordinate[] ta = new TextureCoordinate[t.size()];
                    t.toArray(ta);
                    Normal[] na = new Normal[n.size()];
                    n.toArray(na);
                    Face face = new Face(va, ta, na);
                    this.materialLibrary.library.put(face, material);
                    if (elementList.isEmpty()) {
                        this.materialLibrary.elements.put("default", face);
                    } else {
                        for (String s : elementList) {
                            this.materialLibrary.elements.put(s, face);
                        }
                    }
                } else if (key.equalsIgnoreCase("g") || key.equalsIgnoreCase("o")) {
                    elementList.clear();
                    if (key.equalsIgnoreCase("g")) {
                        String[] splitSpace = data.split(" ");
                        for (String s : splitSpace) elementList.add(s);
                    } else {
                        elementList.add(data);
                    }
                }
            }
            return new OBJModel(this.materialLibrary);
        }
    }
    
    public static class MaterialLibrary
    {
        private Map<String, Material> materials = new HashMap<String, Material>();
        private Map<Face, Material> library = new HashMap<Face, Material>();
        private Map<String, Face> elements = new HashMap<String, Face>();
        private InputStreamReader mtlStream;
        private BufferedReader mtlReader;
        private List<String> materialCommands = new ArrayList<String>();
        
        public MaterialLibrary() {}
        
        public void parseMaterials(IResourceManager manager, String path, ResourceLocation from) throws IOException
        {
            String domain = from.getResourceDomain();
            mtlStream = new InputStreamReader(manager.getResource(new ResourceLocation(domain, path)).getInputStream(), Charsets.UTF_8);
            mtlReader = new BufferedReader(mtlStream);
            
            for (;;)
            {
                String currentLine = mtlReader.readLine();
                if (currentLine == null) break;
                if (currentLine.isEmpty() || currentLine.startsWith("#")) continue;
                materialCommands.add(currentLine);
            }
                        
            for (String command : materialCommands) {
                String[] fields = command.split(" ", 2);
                String key = fields[0];
                String data = fields[1];
                
                if (key.equalsIgnoreCase("newmtl")) {
                    this.materials.put(data, Material.parseMaterial(materialCommands));
                }
            }
        }
        
        public Map<String, Face> getElements() {
            return this.elements;
        }
    }
    
    public static class Material
    {
        private Vector4f color;
        private Texture texture = Texture.White;
        
        public Material() 
        {
            this(Texture.White);
        }
        
        public Material(Vector4f color)
        {
            this(color, Texture.White);
        }
        
        public Material(Texture texture)
        {
            this(new Vector4f(1, 1, 1, 1), texture);
        }
        
        public Material(Vector4f color, Texture texture)
        {
            this.color = color;
            this.texture = texture;
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
        
        public static Material parseMaterial(List<String> commands)
        {
            Material material = new Material(null, null);
            ListIterator<String> commandIterator = commands.listIterator();
            
            while (commandIterator.hasNext())
            {
                String currentLine = commandIterator.next();
                String[] fields = currentLine.split(" ", 2);
                String key = fields[0];
                String data = fields[1];
                
                if (key.equalsIgnoreCase("newmtl")) continue;
                else if (key.equalsIgnoreCase("Ka") || key.equalsIgnoreCase("Kd") || key.equalsIgnoreCase("Ks"))
                {
                    String[] rgbStrings = data.split(" ", 3);
                    Vector4f color = new Vector4f(Float.parseFloat(rgbStrings[0]), Float.parseFloat(rgbStrings[1]), Float.parseFloat(rgbStrings[2]), 1.0f);
                    material.setColor(color);
                }
                else if (key.equalsIgnoreCase("illum")) ;   //TODO: implement illumination models?
                else if (key.equalsIgnoreCase("map_Ka") || key.equalsIgnoreCase("map_Kd") || key.equalsIgnoreCase("map_Ks"))
                {
                    if (data.contains(" "))
                    {
                        String[] mapStrings = data.split(" ");
                        String texturePath = mapStrings[mapStrings.length - 1];
                        Texture texture = new Texture(texturePath);
                        material.setTexture(texture);
                    }
                    else
                    {
                        Texture texture = new Texture(data);
                        material.setTexture(texture);
                    }
                }
                else if (key.equalsIgnoreCase("d") || key.equalsIgnoreCase("Tr")) ;     //TODO: transparency?
                else if (key.equalsIgnoreCase("map_d")) ;   //TODO: alpha map?
            }
            if (material.getTexture() == null) material.setTexture(Texture.White);
            return material;
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
        
        public Face(Vertex[] verts)
        {
            this(verts, null, null);
        }
        
        public Face(Vertex[] verts, Normal[] norms)
        {
            this(verts, null, norms);
        }
        
        public Face(Vertex[] verts, TextureCoordinate[] texCoords)
        {
            this(verts, texCoords, null);
        }
        
        public Face(Vertex[] verts, TextureCoordinate[] texCoords, Normal[] norms)
        {
            this.verts = verts;
            this.verts = this.verts.length > 0 ? this.verts : null;
            this.norms = norms;
            this.norms = this.norms.length > 0 ? this.norms : null;
            this.texCoords = texCoords;
            this.texCoords = this.texCoords.length > 0 ? this.texCoords : null;
        }
        
        public void setVertices(Vertex[] verts)
        {
            this.verts = verts;
        }
        
        public Vertex[] getVertices()
        {
            return this.verts;
        }
        
        public void setNormals(Normal[] norms)
        {
            this.norms = norms;
        }
        
        public Normal[] getNormals()
        {
            return this.norms;
        }
        
        public void setTextureCoordinates(TextureCoordinate[] texCoords)
        {
            this.texCoords = texCoords;
        }
        
        public TextureCoordinate[] getTextureCoordinates()
        {
            return this.texCoords;
        }
        
        public Normal getNormal() {
            if (norms == null) { //use vertices to calculate normal
                Vector3f v1 = new Vector3f(this.verts[0].getPosition().x, this.verts[0].getPosition().y, this.verts[0].getPosition().z);
                Vector3f v2 = new Vector3f(this.verts[1].getPosition().x, this.verts[1].getPosition().y, this.verts[1].getPosition().z);
                Vector3f v3 = new Vector3f(this.verts[2].getPosition().x, this.verts[2].getPosition().y, this.verts[2].getPosition().z);
                Vector3f v4 = this.verts.length > 3 ? new Vector3f(this.verts[3].getPosition().x, this.verts[3].getPosition().y, this.verts[3].getPosition().z) : null;
                
                if (v4 == null) {
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
                } else {
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
            } else { //use normals to calculate normal
                Vector3f n1 = this.norms[0].getNormal();
                Vector3f n2 = this.norms[1].getNormal();
                Vector3f n3 = this.norms[2].getNormal();
                Vector3f n4 = this.norms.length > 3 ? this.norms[3].getNormal() : null;
                
                if (n4 == null) {
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
                } else {
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
        private Vector4f color;
        
        public Vertex(Vector4f position, Vector4f color)
        {
            this.position = position;
            this.color = color;
        }
        
        public void setPos(Vector4f position)
        {
            this.position = position;
        }
        
        public Vector4f getPosition()
        {
            return this.position;
        }
        
        public void setColor(Vector4f color)
        {
            this.color = color;
        }
        
        public Vector4f getColor()
        {
            return this.color;
        }
        
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append(String.format("v:%n"));
            builder.append(String.format("    position: %s %s %s%n", position.x, position.y, position.z));
            builder.append(String.format("    color: %s %s %s %s%n", color.x, color.y, color.z, color.w));
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
    
    public class OBJState implements IModelState {
        private List<String> visibleElements = new ArrayList<String>();
        private ItemCameraTransforms transforms = ItemCameraTransforms.DEFAULT;
        
        public OBJState(List<String> visibleElements, ItemCameraTransforms transforms) {
            this.visibleElements = visibleElements;
            this.transforms = transforms;
        }
        
        public void setVisibleElements(List<String> visibleElements) {
            this.visibleElements = visibleElements;
        }
        
        public List<String> getVisibleElements() {
            return this.visibleElements;
        }
        
        public void setTransforms(ItemCameraTransforms transforms) {
            this.transforms = transforms;
        }
        
        public ItemCameraTransforms getTransforms() {
            return this.transforms;
        }

        @Override
        public TRSRTransformation apply(IModelPart part)
        {
            TRSRTransformation ret = TRSRTransformation.identity();
            if (part instanceof Element) {
                Element element = (Element) part;
                ret = ret.compose(new TRSRTransformation(element.getPos(), element.getRot(), element.getScale(), null));
                Matrix4f matrix = ret.getMatrix();
                matrix.invert();
                ret = ret.compose(new TRSRTransformation(matrix));
            }
            return ret;
        }
    }
    
    public static enum OBJModelProperty implements IUnlistedProperty<OBJState> {
        instance;
        public String getName() {
            return "OBJModel";
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
    
    public class Element implements IModelPart {
        private final String name;
        private final Vector3f pos;
        private final Vector3f scale;
        private final Quat4f rot;
        private final ImmutableList<Face> faces;
        
        public Element(String name, Vector3f pos, Vector3f scale, Quat4f rot, List<Face> faces) {
            this.name = name;
            this.pos = pos;
            this.scale = scale;
            this.rot = rot;
            this.faces = ImmutableList.copyOf(faces);
        }
        
        public ImmutableList<Face> bake() {
            ImmutableList.Builder<Face> builder = ImmutableList.builder();
            for (Face f : faces) {
                builder.add(f);
            }
            return builder.build();
        }
        
        public String getName() {
            return this.name;
        }
        
        public Vector3f getPos() {
            return this.pos;
        }
        
        public Vector3f getScale() {
            return this.scale;
        }
        
        public Quat4f getRot() {
            return this.rot;
        }
        
        public List<Face> getFaces() {
            return this.faces;
        }
    }
    
    private class OBJBakedModel implements IFlexibleBakedModel, ISmartBlockModel, ISmartItemModel, IPerspectiveAwareModel
    {
        private final OBJModel model;
        private final IModelState state;
        private final VertexFormat format;
        private final ByteBuffer buffer;
        private ImmutableList<BakedQuad> quads;
        private static final int BYTES_IN_INT = Integer.SIZE / Byte.SIZE;
        private static final int VERTICES_IN_QUAD = 4;
        private Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;
        private ImmutableMap<String, TextureAtlasSprite> textures;
        
        public OBJBakedModel(OBJModel model, IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
        {
            this.model = model;
            this.state = state;
            this.format = format;
            this.bakedTextureGetter = bakedTextureGetter;
            buffer = BufferUtils.createByteBuffer(VERTICES_IN_QUAD * format.getNextOffset());
            model.setModelState(state);
        }
        
        @Override
        public List<BakedQuad> getFaceQuads(EnumFacing side)
        {
            return Collections.emptyList();
        }
        
        @Override
        public List<BakedQuad> getGeneralQuads()
        {
            if (quads == null) {
                ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
                ImmutableMap.Builder<String, TextureAtlasSprite> texBuilder = ImmutableMap.builder();
                List<Face> faces = new ArrayList<Face>();
                List<ResourceLocation> texturesAdded = new ArrayList<ResourceLocation>();
                Iterator<Map.Entry<Face, Material>> faceIterator = model.matLib.library.entrySet().iterator();
                while (faceIterator.hasNext()) {
                    Map.Entry<Face, Material> entry = faceIterator.next();
                    faces.add(entry.getKey());
                    if (!texturesAdded.contains(entry.getValue().getTexture().getTextureLocation())) texturesAdded.add(entry.getValue().getTexture().getTextureLocation());
//                    texBuilder.put(entry.getValue().texture.path, bakedTextureGetter.apply(new ResourceLocation(entry.getValue().texture.path)));
                }
                for (ResourceLocation r : texturesAdded) texBuilder.put(r.getResourceDomain() + ":" + r.getResourcePath(), bakedTextureGetter.apply(r));
                this.textures = texBuilder.build();
                for (Face f : faces) {
                    buffer.clear();
                    List<Texture> textures = new ArrayList<Texture>();
                    textures.add(model.matLib.library.get(f).texture);
                    TextureAtlasSprite sprite;
                    if (textures.isEmpty()) sprite = this.textures.get("missingno");
                    else if (textures.get(0) == OBJModel.Texture.White) sprite = ModelLoader.White.instance;
                    else sprite = this.textures.get(textures.get(0).getPath());
                    putVertexData(f.verts[0], sprite, f.norms != null ? f.norms[0] : f.getNormal());
                    putVertexData(f.verts[1], sprite, f.norms != null ? f.norms[1] : f.getNormal());
                    putVertexData(f.verts[2], sprite, f.norms != null ? f.norms[2] : f.getNormal());
                    putVertexData(f.verts[3], sprite, f.norms != null ? f.norms[3] : f.getNormal());
                    buffer.flip();
                    int[] data = new int[VERTICES_IN_QUAD * format.getNextOffset() / BYTES_IN_INT];
                    buffer.asIntBuffer().get(data);
                    builder.add(new ColoredBakedQuad(data, -1, EnumFacing.getFacingFromVector(f.getNormal().normal.x, f.getNormal().normal.y, f.getNormal().normal.z)));
                }
                quads = builder.build();
            }
            return quads;
        }
        
        private void put(VertexFormatElement e, Float... fs) {
            Attributes.put(buffer, e, true, 0f, fs);
        }
        
        @SuppressWarnings("unchecked")
        private final void putVertexData(Vertex v, TextureAtlasSprite sprite, Normal n) {
            int oldPos = buffer.position();
            Number[] ns = new Number[16];
            for (int i = 0; i < ns.length; i++) ns[i] = 0f;
            for (VertexFormatElement e : (List<VertexFormatElement>) format.getElements()) {
                switch (e.getUsage()) {
                case POSITION:
                    put(e, v.position.x, v.position.y, v.position.z, 1f);
                    break;
                case COLOR:
                    if (v.color != null) put(e, v.color.x, v.color.y, v.color.z, v.color.w); 
                    else put(e, 1f, 1f, 1f, 1f);
                    break;
                case NORMAL:
                    put(e, n.normal.x, n.normal.y, n.normal.z, 1f);
                    break;
                case GENERIC:
                    put(e, 0f, 0f, 0f, 0f);
                    break;
                default:
                    break;
                }
            }
            buffer.position(oldPos + format.getNextOffset());
        }
        
        @Override
        public boolean isAmbientOcclusion()
        {
            return true;
        }

        @Override
        public boolean isGui3d()
        {
            return true;
        }

        @Override
        public boolean isBuiltInRenderer()
        {
            return false;
        }

        @Override
        public TextureAtlasSprite getTexture()
        {
            return this.textures.get(0);
//            return bakedTextureGetter.apply(new ResourceLocation("forgedebugmodelloaderregistry", "texture.png"));
        }

        @Override
        public ItemCameraTransforms getItemCameraTransforms()
        {
            if (this.state instanceof IExtendedBlockState) {
                IExtendedBlockState exState = (IExtendedBlockState) this.state;
                if (exState.getUnlistedNames().contains(OBJModelProperty.instance)) {
                    OBJState s = exState.getValue(OBJModelProperty.instance);
                    return s.getTransforms();
                }
            }
            return ItemCameraTransforms.DEFAULT;
        }

        @Override
        public VertexFormat getFormat()
        {
            return format;
        }

        @Override
        public Pair<IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType)
        {
            //FIXME figure out where to get matrices for the different camera transform types  
            if (this.state instanceof IExtendedBlockState) {
                IExtendedBlockState exState = (IExtendedBlockState) this.state;
                if (exState.getUnlistedNames().contains(OBJModelProperty.instance)) {
                    OBJState s = (OBJState) exState.getValue(OBJModelProperty.instance);
                    Matrix4f matrix = new Matrix4f();
                    switch (cameraTransformType) {
                    case THIRD_PERSON:
                        TRSRTransformation thirdPerson = new TRSRTransformation();
                        break;
                    case FIRST_PERSON:
                        break;
                    case HEAD:
                        break;
                    case GUI:
                        break;
                    case NONE:
                    default:
                        matrix.setIdentity();
                        return Pair.of((IBakedModel) this, matrix);
                    }
                }
            }
            Matrix4f matrix = new Matrix4f();
            matrix.setIdentity();
            return Pair.of((IBakedModel) this, matrix);
        }

        @Override
        public IBakedModel handleItemState(ItemStack stack)
        {
            return this;
        }

        @Override
        public IBakedModel handleBlockState(IBlockState state)
        {
            if (state instanceof IExtendedBlockState) {
                IExtendedBlockState exState = (IExtendedBlockState) state;
                if (exState.getUnlistedNames().contains(OBJModelProperty.instance)) {
                    OBJState s = (OBJState) exState.getValue(OBJModelProperty.instance);
                    if (s != null) {
                        return getModel(s.getVisibleElements(), s.getTransforms());
                    }
                }
            }
            return this;
        }
        
        public OBJBakedModel getModel(List<String> visibleElements, ItemCameraTransforms transforms) {
            return new OBJBakedModel(model, new OBJState(visibleElements, transforms), format, bakedTextureGetter);
        }
    }
}
