package net.minecraftforge.client.model.obj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelState;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import scala.actors.threadpool.Arrays;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class OBJModel implements IModel {
    public static final boolean shouldLog = true;
    private static final Logger logger = LogManager.getLogger(OBJModel.class);
    static final Set<String> unknownObjectCommands = new HashSet<String>();
    static IResourceManager manager;
    boolean loadingBlockModel = false;
    ImmutableMap<String, ResourceLocation> textures;
    
    public OBJModel() {}
    
    public static void setManager(IResourceManager mgr)
    {
        if (shouldLog) logger.info("OBJ: manager set");
        manager = mgr;
    }
    
    @Override
    public Collection<ResourceLocation> getDependencies()
    {
        return Collections.emptyList();
    }

    @Override
    public Collection<ResourceLocation> getTextures()
    {
        if (shouldLog) logger.printf(Level.INFO, "OBJ: getTextures: are textures null: %b", this.textures == null);
        return this.textures.values();
    }

    @Override
    public IFlexibleBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        if (shouldLog) logger.info("OBJ: baking IFlexibleBakedModel");
        ImmutableMap.Builder<String, TextureAtlasSprite> builder = ImmutableMap.builder();
        for (String path : textures.keySet())
        {
            if (shouldLog) logger.printf(Level.INFO, "OBJ: put texture at path %s in builder", path);
            builder.put(path, bakedTextureGetter.apply(textures.get(path)));
        }
        if (shouldLog) logger.info("OBJ: put missingno in builder");
        builder.put("missingno", bakedTextureGetter.apply(new ResourceLocation("missingno")));
        if (shouldLog) logger.info("OBJ: returning flexible model");
//        return new OBJFlexibleModel(this, state, format, bakedTextureGetter);
        return null;
    }

    @Override
    public IModelState getDefaultState()
    {
        if (shouldLog) logger.info("OBJ: getDefaultState");
//        return new OBJModelState();
        return null;
    }
    
//    public OBJMaterialLibrary getOBJMaterialLibrary()
//    {
//        if (shouldLogDebugData) logger.info("OBJ: getOBJMaterialLibrary");
//        return this.matLib;
//    }
    
//    public void setOBJMaterialLibrary(OBJMaterialLibrary matLib)
//    {
//        if (shouldLog) logger.printf(Level.INFO, "OBJ: material library was null: %b", this.matLib == null);
//        this.matLib = matLib != null ? matLib : this.matLib;
//        if (shouldLog) logger.info("OBJ: material library set");
//    }
    
    public static class Parser
    {
        private InputStreamReader lineStream;
        private BufferedReader lineReader;
        private OBJModel model;
        private List<Texture> textures = new ArrayList<Texture>();
        private List<Material> materials = new ArrayList<Material>();
        private List<Vertex> vertices = new ArrayList<Vertex>();
        private List<Vector3f> texCoords = new ArrayList<Vector3f>();
        private List<Vector3f> normals = new ArrayList<Vector3f>();
        private List<ModelElement> elements = new ArrayList<ModelElement>();
        private ImmutableMap.Builder<String, Mesh> meshes = ImmutableMap.builder();
        
        List<String> vertexData = new ArrayList<String>();
        List<String> texCoordData = new ArrayList<String>();
        List<String> normalData = new ArrayList<String>();
        List<String> faceData = new ArrayList<String>();
        List<String> mtllibData = new ArrayList<String>();
        Map<String, Integer> usemtlData = new HashMap<String, Integer>();
        
        public Parser(IResource from) throws IOException
        {
            this.lineStream = new InputStreamReader(from.getInputStream(), Charsets.UTF_8);
            this.lineReader = new BufferedReader(lineStream);
        }
        
        public OBJModel parse() throws IOException
        {
            Mesh mesh = new Mesh();
            ModelElement element = new ModelElement();
            
            String currentLine = "";
            String nextLine = "";
            String nextKey = "";
            String nextData = "";
            
            for (;;)
            {
                currentLine = nextLine;
                nextLine = lineReader.readLine();
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
                
                if (key.equalsIgnoreCase("v")) vertexData.add(data);
                else if (key.equalsIgnoreCase("vt")) texCoordData.add(data);
                else if (key.equalsIgnoreCase("vn")) normalData.add(data);
                else if (key.equalsIgnoreCase("f")) faceData.add(data);
                else if (key.equalsIgnoreCase("mtllib")) mtllibData.add(data);
                else if (key.equalsIgnoreCase("usemtl")) usemtlData.put(data, faceData.size());
            }
            
            Set<String> usemtlKeys = usemtlData.keySet();
            Collection<Integer> usemtlElements = usemtlData.values();
            List<String> usemtlKeyList = Arrays.asList(usemtlKeys.toArray());
            List<Integer> usemtlElementList = Arrays.asList(usemtlElements.toArray());
            
            ListIterator<String> vertexIterator = vertexData.listIterator();
            ListIterator<String> texCoordIterator = texCoordData.listIterator();
            ListIterator<String> normalIterator = normalData.listIterator();
            ListIterator<String> faceIterator = faceData.listIterator();
            ListIterator<String> mtllibIterator = mtllibData.listIterator();
            ListIterator<String> usemtlKeyIterator = usemtlKeyList.listIterator();
            ListIterator<Integer> usemtlElementIterator = usemtlElementList.listIterator();
            
            while (texCoordIterator.hasNext())
            {
                String data = texCoordIterator.next();
                String[] splitData = data.split(" ");
                float[] floatSplitData = new float[splitData.length];
                for (int i = 0; i < splitData.length; i++) floatSplitData[i] = Float.parseFloat(splitData[i]);
                Vector3f texCoord = new Vector3f(floatSplitData[0], floatSplitData[1], floatSplitData.length == 3 ? floatSplitData[2] : 1);
                this.texCoords.add(texCoord);
            }
            
            while (normalIterator.hasNext())
            {
                String data = normalIterator.next();
                String[] splitData = data.split(" ", 3);
                float[] floatSplitData = new float[splitData.length];
                for (int i = 0; i < splitData.length; i++) floatSplitData[i] = Float.parseFloat(splitData[i]);
                Vector3f normal = new Vector3f(floatSplitData[0], floatSplitData[1], floatSplitData[2]);
                this.normals.add(normal);
            }
            
            while (vertexIterator.hasNext())
            {
                String data = vertexIterator.next();
                String[] splitData = data.split(" ");
                float[] floatSplitData = new float[splitData.length];
                for (int i = 0; i < splitData.length; i++) floatSplitData[i] = Float.parseFloat(splitData[i]);
                Vector4f pos = new Vector4f(floatSplitData[0], floatSplitData[1], floatSplitData[2], floatSplitData.length == 4 ? floatSplitData[3] : 1);
                Vertex vertex = new Vertex(pos, new Vector4f(0.5f, 0.5f, 0.5f, 1.0f));
                vertices.add(vertex);
            }
            
            //TODO: add support for negative index references!
            while (faceIterator.hasNext())
            {
                String data = faceIterator.next();  //(#/#/# #/#/# #/#/# #/#/#) or (#/# #/# #/# #/#) or (# # # #) or (#//# #//# #//# #//#)
                String[] splitSpace = data.split(" ");  //([#/#/#], [#/#/#], [#/#/#], [#/#/#]) ...
                String[][] splitSlash = new String[splitSpace.length][];
                for (int i = 0; i < splitSpace.length; i++)
                {
                    if (splitSpace[i].contains("////"))     //([#//#], [#//#], [#//#], [#//#])
                    {
                        splitSlash[i] = splitSpace[i].split("////");    //([(#), (#)], [(#), (#)], [(#), (#)], [(#), (#)])
                        int[] verts = new int[splitSlash[i].length];
                        int[] norms = new int[splitSlash[i].length];
                        for (int v = 0; v < splitSlash[i].length; v++)
                        {
                            verts[v] = Integer.parseInt(splitSlash[i][v]);
                            verts[v] = verts[v] < 0 ? Math.abs(verts[v]) : verts[v];
                            norms[v] = Integer.parseInt(splitSlash[i][v]);
                            norms[v] = norms[v] < 0 ? Math.abs(norms[v]) : norms[v];
                        }
                        
                        Face face = new Face(this.vertices.get(verts[0]), this.vertices.get(verts[1]), this.vertices.get(verts[2]), this.vertices.get(verts[3]), null);
                    }
                }
            }
            
            System.out.println("vertices: " + vertexData.size());
            System.out.println("texCoords: " + texCoordData.size());
            System.out.println("normals: " + normalData.size());
            System.out.println("faces: " + faceData.size());
            System.out.println("mtllibs: " + mtllibData.size());
            System.out.println("usemtls: " + usemtlData.size());
            
            return model;
        }
        
        private Texture getTexture(int texture)
        {
            if (texture > textures.size())
            {
                if (shouldLog) logger.error(String.format("texture %s is out of range", texture));
                return null;
            }
            else if (texture == -1) return null;
            return textures.get(texture);
        }
        
        private Material getMaterial(int material)
        {
            if (material > materials.size())
            {
                if (shouldLog) logger.error(String.format("material %s is out of range", material));
                return null;
            }
            else if (material == -1) return null;
            return materials.get(material);
        }
        
        private Vertex getVertex(int vertex)
        {
            if (vertex > vertices.size())
            {
                if (shouldLog) logger.error(String.format("vertex %s is out of range", vertex));
                return null;
            }
            else if (vertex == -1) return null;
            return vertices.get(vertex);
        }
        
        private OBJModel bakeOBJ() throws IOException
        {
            List<Texture> textures = null;
            List<Material> materials = null;
            
            for (;;)
            {
                String currentLine = lineReader.readLine();
                if (currentLine == null) break;
                if (currentLine.length() == 0 || currentLine.startsWith("#")) continue;
                String[] fields = currentLine.split(" ", 2);
                String key = fields[0];
                String data = fields[1];
                
                if (key.equalsIgnoreCase("mtllib")) ;
                else if (key.equalsIgnoreCase("usemtl")) ;
                else if (key.equalsIgnoreCase("o") || key.equalsIgnoreCase("g")) ;
                else if (key.equalsIgnoreCase("v")) ;
                else if (key.equalsIgnoreCase("vn")) ;
                else if (key.equalsIgnoreCase("vt")) ;
                else if (key.equalsIgnoreCase("f")) ;
                else
                {
                    if (!unknownObjectCommands.contains(key))
                    {
                        logger.printf(Level.WARN, "OBJ: Unrecognized command: %s, skipping", currentLine);
                        unknownObjectCommands.add(key);
                    }
                    continue;
                }
            }
            return null;
        }
    }
    
    public static class Mesh
    {
        private final List<ModelElement> elements = new ArrayList<ModelElement>();
        
        public Mesh() {}
        
        public void addElement(ModelElement element)
        {
            if (element != null) elements.add(element);
        }
        
        public void addElements(List<ModelElement> elements)
        {
            if (elements != null && !elements.isEmpty()) this.elements.addAll(elements);
        }
        
        public ModelElement getElement(int index)
        {
            if (index >= 0 && index < this.elements.size()) return this.elements.get(index);
            else return null;
        }
        public List<ModelElement> getElements()
        {
            return elements;
        }
        
        public List<ModelElement> bakeMesh()
        {
            ImmutableList.Builder<ModelElement> builder = ImmutableList.builder();
            for (ModelElement e : getElements()) builder.add(e);
            return builder.build();
        }
        
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append(String.format("mesh:%n"));
            builder.append(String.format("    elements:%n"));
            for (ModelElement e : getElements()) builder.append(String.format("        %s", e.toString()));
            return builder.toString();
        }
    }
    
    public static class ModelElement
    {
        List<Face> faces = new ArrayList<Face>();
        
        public ModelElement() {}
        
        public void addFace(Face face)
        {
            if (face != null && !faces.contains(face)) faces.add(face);
        }
        
        public void addFaces(List<Face> faces)
        {
            if (!this.faces.isEmpty())
            {
                if (faces != null)
                {
                    for (Face f : faces)
                    {
                        if (!this.faces.contains(f)) this.faces.add(f);
                    }
                }
            }
            else this.faces = faces;
        }
        
        public Face getFace(int index)
        {
            if (index >= 0 && index < this.faces.size()) return this.faces.get(index);
            else return null;
        }
        
        public List<Face> getFaces()
        {
            return faces;
        }
        
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            for (Face f : faces) builder.append(f.toString());
            return builder.toString();
        }
    }
    
    public static class Face
    {
        private final Vertex[] vertices = new Vertex[4];
        private final Material material;
        private final Normal normal;
        private final TexCoord[] texCoords = new TexCoord[4];
        
        public Face(Vertex v1, Vertex v2, Vertex v3, Vertex v4, Material material)
        {
            this(v1, v2, v3, v4, material, getFaceNormal(v1, v2, v3, v4));
        }
        
        public Face(Vertex v1, Vertex v2, Vertex v3, Vertex v4, Material material, Normal normal)
        {
            this(v1, v2, v3, v4, material, normal, new TexCoord(v1.pos), new TexCoord(v2.pos), new TexCoord(v3.pos), new TexCoord(v4.pos));
        }
        
        public Face(Vertex v1, Vertex v2, Vertex v3, Vertex v4, Material material, Normal normal, TexCoord t1, TexCoord t2, TexCoord t3, TexCoord t4)
        {
            this.vertices[0] = v1;
            this.vertices[1] = v2;
            this.vertices[2] = v3;
            this.vertices[3] = v4;
            this.material = material;
            this.normal = normal;
            this.texCoords[0] = t1;
            this.texCoords[1] = t2;
            this.texCoords[2] = t3;
            this.texCoords[3] = t4;
        }
        
        public Vertex[] getVertices()
        {
            return this.vertices;
        }
        
        public Material getMaterial()
        {
            return material;
        }
        
        public Normal getNormal(int index)
        {
            return normal;
        }
        
        public TexCoord[] getTexCoords()
        {
            return this.texCoords;
        }
        
//        @Override
//        public String toString()
//        {
//            StringBuilder builder = new StringBuilder();
//            builder.append(String.format("f:%n"));
//            for (int i = 0; i < verts.length; i++) builder.append(String.format("    %d: %s", i + 1, verts[i].toString()));
//            builder.append(String.format("    material: %s", material.toString()));
//            builder.append(String.format("    normal: %s %s %s%n", normal.x, normal.y, normal.z));
//            return builder.toString();
//        }
        
        public static Normal getFaceNormal(Vertex v1, Vertex v2, Vertex v3, Vertex v4)
        {
            Vector4f v1Pos = v1.getPos();
            Vector4f v2Pos = v2.getPos();
            Vector4f v3Pos = v3.getPos();
            Vector4f v4Pos = v4 != null ? v4.getPos() : null;
            
            if (v4Pos == null)
            {
                Vector3f s1 = new Vector3f(v1Pos.x, v1Pos.y, v1Pos.z);
                Vector3f a = new Vector3f(v2Pos.x, v2Pos.y, v2Pos.z);
                a.sub(s1);
                Vector3f b = new Vector3f(v3Pos.x, v3Pos.y, v3Pos.z);
                b.sub(s1);
                Vector3f c = new Vector3f();
                c.cross(a, b);
                c.normalize();
                Normal normal = new Normal(c);
                return normal;
            }
            else
            {
                Vector3f s1 = new Vector3f(v1Pos.x, v1Pos.y, v1Pos.z);
                Vector3f a = new Vector3f(v2Pos.x, v2Pos.y, v2Pos.z);
                a.sub(s1);
                Vector3f b = new Vector3f(v3Pos.x, v3Pos.y, v3Pos.z);
                b.sub(s1);
                Vector3f c = new Vector3f();
                c.cross(a, b);
                c.normalize();
                
                Vector3f d = new Vector3f(v4Pos.x, v4Pos.y, v4Pos.z);
                d.sub(s1);
                Vector3f e = new Vector3f();
                e.cross(d, b);
                e.normalize();
                
                Vector3f avg = new Vector3f();
                avg.x = (c.x + e.x) / 2.0f;
                avg.y = (c.y + e.y) / 2.0f;
                avg.z = (c.z + e.z) / 2.0f;
                avg.normalize();
                Normal normal = new Normal(avg);
                return normal;
            }
        }
    }
    
    public static class Vertex
    {
        private Vector4f pos;
        private Vector4f color;
        
        public Vertex(Vector4f pos, Vector4f color)
        {
            this.pos = pos;
            this.color = color;
        }
        
        public void setPos(Vector4f pos)
        {
            this.pos = pos;
        }
        
        public Vector4f getPos()
        {
            return this.pos;
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
            builder.append(String.format("    position: %s %s %s%n", pos.x, pos.y, pos.z));
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
    
    public static class TexCoord
    {
        private Vector4f texCoord;
        
        public TexCoord(Vector4f texCoord)
        {
            this.texCoord = texCoord;
        }
        
        public void setTexCoord(Vector4f texCoord)
        {
            this.texCoord = texCoord;
        }
        
        public Vector4f getTexCoord()
        {
            return this.texCoord;
        }
    }
    
    public static class MaterialLibrary
    {
        static final Set<String> unknownMaterialCommands = new HashSet<String>();
        private final Map<String, Material> materialLibrary = new HashMap<String, Material>();
        
        public MaterialLibrary() {}
        
        public void loadFromStream(ResourceLocation loc) throws IOException
        {
            IResource res = Minecraft.getMinecraft().getResourceManager().getResource(loc);
            InputStreamReader lineStream = new InputStreamReader(res.getInputStream(), Charsets.UTF_8);
            BufferedReader lineReader = new BufferedReader(lineStream);
            
            for (;;)
            {
                String currentLine = lineReader.readLine();
                if (currentLine == null) break;
                if (currentLine.length() == 0 || currentLine.startsWith("#")) continue;
                String[] fields = currentLine.split(" ", 2);
                String key = fields[0];
                String data = fields[1];
                
                if (key.equalsIgnoreCase("newmtl")) ;
                else if (key.equalsIgnoreCase("Ka")) ;
                else if (key.equalsIgnoreCase("Kd")) ;
                else if (key.equalsIgnoreCase("Ks")) ;
                else if (key.equalsIgnoreCase("Ns")) ;
                else if (key.equalsIgnoreCase("Tr")) ;
                else if (key.equalsIgnoreCase("Tf")) ;
                else if (key.equalsIgnoreCase("illum")) ;
                else if (key.equalsIgnoreCase("map_Ka")) ;
                else if (key.equalsIgnoreCase("map_Kd")) ;
                else if (key.equalsIgnoreCase("map_Ks")) ;
                
            }
        }
    }
    
    public static class Material
    {
        private final String name;
        private final Vector4f color;
//        private final float shininess;
        private final List<Texture> textures;
        
        public Material(String name, Vector4f color, List<Texture> textures)
        {
            this.name = name;
            this.color = color;
            this.textures = textures;
        }
        
//        public Material(String name, Vector4f color, float shininess, List<Texture> textures)
//        {
//            this.name = name;
//            this.color = color;
//            this.shininess = shininess;
//            this.textures = textures;
//        }
        
        public String getName()
        {
            return name;
        }
        
        public Vector4f getColor()
        {
            return color;
        }
        
        public List<Texture> getTextures()
        {
            return textures;
        }
        
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append(String.format("material:%n"));
            builder.append(String.format("    name: %s%n", name));
            builder.append(String.format("    color: %s %s %s %s%n", color.x, color.y, color.z, color.w));
            builder.append(String.format("    textures:%n"));
            for (Texture t : textures) builder.append(String.format("        %s%n", t.toString()));
            return String.format("OBJMaterial [name=%s, color=%s, textures=%s]", name, color, textures);
        }
    }
    
    public static class Texture
    {
        private final String path;
        private final Vector2f pos;
        private final Vector2f scale;
        private final float rot;
        
        public Texture(String path, Vector2f pos, Vector2f scale, float rot)
        {
            this.path = path;
            this.pos = pos;
            this.scale = scale;
            this.rot = rot;
        }
        
        public String getPath()
        {
            return path;
        }
        
        public Vector2f getPos()
        {
            return pos;
        }
        
        public Vector2f getScale()
        {
            return scale;
        }
        
        public float getRot()
        {
            return rot;
        }
        
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append(String.format("texture:%n"));
            builder.append(String.format("    path: %s%n", path));
            builder.append(String.format("    position: %s %s%n", pos.x, pos.y));
            builder.append(String.format("    scale: %s %s%n", scale.x, scale.y));
            builder.append(String.format("    rotation: %s%n", rot));
            return builder.toString();
        }
    }
}
