package net.minecraftforge.client.model.obj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

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
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.BufferUtils;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

public class OBJModel2 implements IModel {
    @Override
    public Collection<ResourceLocation> getDependencies()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<ResourceLocation> getTextures()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IFlexibleBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IModelState getDefaultState()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    public static class Parser
    {
        public MaterialLibrary materialLibrary = new MaterialLibrary();
        private IResourceManager manager;
        private InputStreamReader objStream;
        private BufferedReader objReader;
        
        public Parser(IResource from, IResourceManager manager) throws IOException
        {
            this.manager = manager;
            this.objStream = new InputStreamReader(from.getInputStream(), Charsets.UTF_8);
            this.objReader = new BufferedReader(objStream);
        }
        
        public OBJModel2 parse() throws IOException
        {
            String currentLine = "";
            String nextLine = "";
            String nextKey = "";
            String nextData = "";
            Material material;
            
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
                
                if (key.equalsIgnoreCase("mtllib")) materialLibrary.parseMaterials(manager, data);
                else if (key.equalsIgnoreCase("usemtl")) material = materialLibrary.materials.get(data);
                
            }
            return null;
        }
    }
    
    public static class MaterialLibrary
    {
        private Map<String, Material> materials = new HashMap<String, Material>();
        private Map<Face, Material> library = new HashMap<Face, Material>();
        private InputStreamReader mtlStream;
        private BufferedReader mtlReader;
        private List<String> materialCommands = new ArrayList<String>();
        
        public MaterialLibrary() {}
        
        public void parseMaterials(IResourceManager manager, String path) throws IOException
        {
            mtlStream = new InputStreamReader(manager.getResource(new ResourceLocation(path)).getInputStream(), Charsets.UTF_8);
            mtlReader = new BufferedReader(mtlStream);
            
            for (;;)
            {
                String currentLine = mtlReader.readLine();
                if (currentLine == null) break;
                if (currentLine.isEmpty() || currentLine.startsWith("#")) continue;
                materialCommands.add(currentLine);
            }
            
            List<String> materialCommandsSplit = new ArrayList<String>();
            
            for (String command : materialCommands)
            {
                String[] fields = command.split(" ", 2);
                String key = fields[0];
                String data = fields[1];
                
                if (key.equalsIgnoreCase("newmtl"))
                {
                    if (!materialCommandsSplit.isEmpty())
                    {
                        this.materials.put(materialCommandsSplit.get(0), Material.parseMaterial(materialCommandsSplit));
                        materialCommandsSplit = new ArrayList<String>();
                        materialCommandsSplit.add(command);
                    }
                    else materialCommandsSplit.add(command);
                }
                else materialCommandsSplit.add(command);
            }
            
            if (!materialCommandsSplit.isEmpty()) this.materials.put(materialCommandsSplit.get(0), Material.parseMaterial(materialCommandsSplit));
        }
    }
    
    public static class Material
    {
        private Vector4f color;
        private Texture texture;
        
        public Material(Vector4f color)
        {
            //TODO: figure out how color-only materials will work
            this(color, new Texture("minecraft:cobblestone"));
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
            
            return material;
        }
    }
    
    public static class Texture
    {
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
        private List<String> vertexCommands = new ArrayList<String>();
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
            this.norms = norms;
            this.texCoords = texCoords;
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
        
        public static Face parseFace(List<String> commands)
        {
            
            return null;
        }
        
        public static void parseFaces(IResourceManager manager, String path)
        {
            
        }
    }
    
    public static class Vertex
    {
        private Vector4f position;
        
        public Vertex(Vector4f position)
        {
            this.position = position;
        }
        
        public void setPosition(Vector4f position)
        {
            this.position = position;
        }
        
        public Vector4f getPosition()
        {
            return this.position;
        }
        
        public static void parseVertices(IResourceManager manager, String path)
        {
            
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
    
    
    
    private class OBJFlexibleModel implements IFlexibleBakedModel, ISmartBlockModel, ISmartItemModel, IPerspectiveAwareModel
    {
        private final OBJModel2 model;
        private final IModelState state;
        private final VertexFormat format;
        private final ByteBuffer buffer;
        private ImmutableList<BakedQuad> quads;
        private static final int BYTES_IN_INT = Integer.SIZE / Byte.SIZE;
        private static final int VERTICES_IN_QUAD = 4;
        private Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;
        
        public OBJFlexibleModel(OBJModel2 model, IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
        {
            this.model = model;
            this.state = state;
            this.format = format;
            this.bakedTextureGetter = bakedTextureGetter;
            buffer = BufferUtils.createByteBuffer(VERTICES_IN_QUAD * format.getNextOffset());
        }
        
        @Override
        public List<BakedQuad> getFaceQuads(EnumFacing side)
        {
            return Collections.emptyList();
        }
        
        @Override
        public List<BakedQuad> getGeneralQuads()
        {
//            ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
//            if (model.generalQuads != null) builder.addAll(model.generalQuads);
//            else builder.addAll(model.currentMesh.bakeMesh());
//            this.quads = builder.build();
//            return this.quads.asList();
            return null;
        }
        
        @Override
        public boolean isAmbientOcclusion()
        {
            return false;
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
            return bakedTextureGetter.apply(new ResourceLocation("forgedebugmodelloaderregistry", "texture.png"));
        }

        @Override
        public ItemCameraTransforms getItemCameraTransforms()
        {
            return ItemCameraTransforms.DEFAULT;
        }

        @Override
        public VertexFormat getFormat()
        {
            return Attributes.DEFAULT_BAKED_FORMAT;
        }

        @Override
        public Pair<IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType)
        {
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
            return this;
        }
    }
}
