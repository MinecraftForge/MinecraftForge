package net.minecraftforge.client.model.b3d;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

import scala.actors.threadpool.Arrays;

public class B3DModel {
    private static final Logger logger = LogManager.getLogger(B3DModel.class);
    private final List<Texture> textures;
    private final List<Brush> brushes;
    private final INode node;

    public B3DModel(List<Texture> textures, List<Brush> brushes, INode node)
    {
        this.textures = textures;
        this.brushes = brushes;
        this.node = node;
    }

    public static class Parser
    {
        private static final int version = 0001;
        private final ByteBuffer buf;

        private byte[] tag = new byte[4];
        private int length;
        public Parser(InputStream in) throws IOException
        {
            if(in instanceof FileInputStream)
            {
                // fast shorthand for normal files
                FileChannel channel = ((FileInputStream)in).getChannel();
                buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).order(ByteOrder.LITTLE_ENDIAN);
            }
            else
            {
                // slower default for others
                IOUtils.readFully(in, tag);
                byte[] tmp = new byte[4];
                IOUtils.readFully(in, tmp);
                int l = ByteBuffer.wrap(tmp).order(ByteOrder.LITTLE_ENDIAN).getInt();
                if(l < 0 || l + 8 < 0) throw new IOException("File is too large");
                buf = ByteBuffer.allocate(l + 8).order(ByteOrder.LITTLE_ENDIAN);
                buf.clear();
                buf.put(tag);
                buf.put(tmp);
                buf.put(IOUtils.toByteArray(in, l));
                buf.flip();
            }
        }

        private B3DModel res;

        public B3DModel parse() throws IOException
        {
            if(res != null) return res;
            readHeader();
            res = bb3d();
            return res;
        }

        private final List<Texture> textures = new ArrayList<Texture>();

        private Texture getTexture(int texture)
        {
            if(texture > textures.size())
            {
                logger.error(String.format("texture %s is out of range", texture));
                return null;
            }
            else if(texture == -1) return null;
            return textures.get(texture);
        }

        private final List<Brush> brushes = new ArrayList<Brush>();

        private Brush getBrush(int brush)
        {
            if(brush > brushes.size())
            {
                logger.error(String.format("brush %s is out of range", brush));
                return null;
            }
            else if(brush == -1) return null;
            return brushes.get(brush);
        }

        private final List<Vertex> vertices = new ArrayList<Vertex>();

        private Vertex getVertex(int vertex)
        {
            if(vertex > vertices.size())
            {
                logger.error(String.format("vertex %s is out of range", vertex));
                return null;
            }
            return vertices.get(vertex);
        }

        private void readHeader() throws IOException
        {
            buf.get(tag);
            length = buf.getInt();
        }

        private boolean isChunk(String tag) throws IOException
        {
            return Arrays.equals(this.tag, tag.getBytes("US-ASCII"));
        }

        private void chunk(String tag) throws IOException
        {
            if(!isChunk(tag)) throw new IOException("Expected chunk " + tag + ", got " + new String(this.tag, "US-ASCII"));
            pushLimit();
        }

        private String readString() throws IOException
        {
            int start = buf.position();
            while(buf.get() != 0);
            int end = buf.position();
            byte[] tmp = new byte[end - start - 1];
            buf.position(start);
            buf.get(tmp);
            buf.get();
            String ret =  new String(tmp, "UTF8");
            return ret;
        }

        private Deque<Integer> limitStack = new ArrayDeque<Integer>();

        private void pushLimit()
        {
            limitStack.push(buf.limit());
            buf.limit(buf.position() + length);
        }

        private void popLimit()
        {
            buf.limit(limitStack.pop());
        }

        private B3DModel bb3d() throws IOException
        {
            chunk("BB3D");
            int version = buf.getInt();
            if(version / 100 > this.version / 100)
                throw new IOException("Unsupported major model version: " + ((float)version / 100));
            if(version % 100 > this.version % 100)
                logger.warn(String.format("Minor version differnce in model: ", ((float)version / 100)));
            readHeader();
            List<Texture> textures = texs();
            readHeader();
            List<Brush> brushes = brus();
            readHeader();
            INode node = node();
            popLimit();
            return new B3DModel(textures, brushes, node);
        }

        private List<Texture> texs() throws IOException
        {
            chunk("TEXS");
            List<Texture> ret = new ArrayList<Texture>();
            while(buf.hasRemaining())
            {
                logger.info("TEST");
                String path = readString();
                logger.info("path: '" + path + "'");
                int flags = buf.getInt();
                int blend = buf.getInt();
                Vector2f pos = new Vector2f(buf.getFloat(), buf.getFloat());
                Vector2f scale = new Vector2f(buf.getFloat(), buf.getFloat());
                float rot = buf.getFloat();
                ret.add(new Texture(path, flags, blend, pos, scale, rot));
                logger.info("TEX: '" + path + "' " + flags + " " + blend + " " + pos + " " + scale + " " + rot);
            }
            popLimit();
            this.textures.addAll(ret);
            return ret;
        }

        private List<Brush> brus() throws IOException
        {
            chunk("BRUS");
            List<Brush> ret = new ArrayList<Brush>();
            int n_texs = buf.getInt();
            while(buf.hasRemaining())
            {
                String name = readString();
                Vector4f color = new Vector4f(buf.getFloat(), buf.getFloat(), buf.getFloat(), buf.getFloat());
                float shininess = buf.getFloat();
                int blend = buf.getInt();
                int fx = buf.getInt();
                List<Texture> textures = new ArrayList<Texture>();
                for(int i = 0; i < n_texs; i++) textures.add(getTexture(buf.getInt()));
                ret.add(new Brush(name, color, shininess, blend, fx, textures));
            }
            popLimit();
            this.brushes.addAll(ret);
            return ret;
        }

        private List<Vertex> vrts() throws IOException
        {
            chunk("VRTS");
            List<Vertex> ret = new ArrayList<Vertex>();
            int flags = buf.getInt();
            int tex_coord_sets = buf.getInt();
            int tex_coord_set_size = buf.getInt();
            while(buf.hasRemaining())
            {
                Vector3f v = new Vector3f(buf.getFloat(), buf.getFloat(), buf.getFloat()), n = null;
                Vector4f color = null;
                if((flags & 1) != 0)
                {
                    n = new Vector3f(buf.getFloat(), buf.getFloat(), buf.getFloat());
                }
                if((flags & 2) != 0)
                {
                    color = new Vector4f(buf.getFloat(), buf.getFloat(), buf.getFloat(), buf.getFloat());
                }
                Vector4f[] tex_coords = new Vector4f[4];
                for(int i = 0; i < tex_coord_sets; i++)
                {
                    switch(tex_coord_set_size)
                    {
                    case 1:
                        tex_coords[i] = new Vector4f(buf.getFloat(), 0, 0, 1);
                        break;
                    case 2:
                        tex_coords[i] = new Vector4f(buf.getFloat(), buf.getFloat(), 0, 1);
                        break;
                    case 3:
                        tex_coords[i] = new Vector4f(buf.getFloat(), buf.getFloat(), buf.getFloat(), 1);
                        break;
                    case 4:
                        tex_coords[i] = new Vector4f(buf.getFloat(), buf.getFloat(), buf.getFloat(), buf.getFloat());
                        break;
                    default:
                        logger.error(String.format("Unsupported number of texture coords: ", tex_coord_set_size));
                        tex_coords[i] = new Vector4f(0, 0, 0, 1);
                    }
                }
                ret.add(new Vertex(v, n, color, tex_coords));
            }
            popLimit();
            this.vertices.clear();
            this.vertices.addAll(ret);
            return ret;
        }

        private List<Face> tris() throws IOException
        {
            chunk("TRIS");
            List<Face> ret = new ArrayList<Face>();
            int brush_id = buf.getInt();
            while(buf.hasRemaining())
            {
                ret.add(new Face(getVertex(buf.getInt()), getVertex(buf.getInt()), getVertex(buf.getInt()), getBrush(brush_id)));
            }
            popLimit();
            return ret;
        }

        private Pair<Brush, List<Face>> mesh() throws IOException
        {
            chunk("MESH");
            int brush_id = buf.getInt();
            readHeader();
            vrts();
            List<Face> ret = new ArrayList<Face>();
            do
            {
                readHeader();
                ret.addAll(tris());
            }
            while(buf.hasRemaining());
            popLimit();
            return Pair.of(getBrush(brush_id), ret);
        }

        private List<Pair<Vertex, Float>> bone() throws IOException
        {
            chunk("BONE");
            List<Pair<Vertex, Float>> ret = new ArrayList<Pair<Vertex, Float>>();
            while(buf.hasRemaining())
            {
                ret.add(Pair.of(getVertex(buf.getInt()), buf.getFloat()));
            }
            popLimit();
            return ret;
        }

        private final Deque<Table<Integer, Optional<INode>, Key>> animations = new ArrayDeque<Table<Integer, Optional<INode>, Key>>();

        private Map<Integer, Key> keys() throws IOException
        {
            chunk("KEYS");
            Map<Integer, Key> ret = new HashMap<Integer, Key>();
            int flags = buf.getInt();
            Vector3f pos = null, scale = null;
            Vector4f rot = null;
            while(buf.hasRemaining())
            {
                int frame = buf.getInt();
                if((flags & 1) != 0)
                {
                    pos = new Vector3f(buf.getFloat(), buf.getFloat(), buf.getFloat());
                }
                if((flags & 2) != 0)
                {
                    scale = new Vector3f(buf.getFloat(), buf.getFloat(), buf.getFloat());
                }
                if((flags & 4) != 0)
                {
                    rot = new Vector4f(buf.getFloat(), buf.getFloat(), buf.getFloat(), buf.getFloat());
                }
                Key key = new Key(pos, scale, rot);
                //logger.info("Key: " + frame + " " + key);
                Key oldKey = animations.peek().get(frame, null);
                if(oldKey != null)
                {
                    if(pos != null)
                    {
                        if(oldKey.getPos() != null) logger.error("Duplicate keys: %s and %s (ignored)", oldKey, key);
                        else key = new Key(oldKey.getPos(), key.getScale(), key.getRot());
                    }
                    if(scale != null)
                    {
                        if(oldKey.getScale() != null) logger.error("Duplicate keys: %s and %s (ignored)", oldKey, key);
                        else key = new Key(key.getPos(), oldKey.getScale(), key.getRot());
                    }
                    if(rot != null)
                    {
                        if(oldKey.getRot() != null) logger.error("Duplicate keys: %s and %s (ignored)", oldKey, key);
                        else key = new Key(key.getPos(), key.getScale(), oldKey.getRot());
                    }
                }
                animations.peek().put(frame, Optional.<INode>absent(), key);
                ret.put(frame, key);
            }
            popLimit();
            return ret;
        }

        private Triple<Integer, Integer, Float> anim() throws IOException
        {
            chunk("ANIM");
            int flags = buf.getInt();
            int frames = buf.getInt();
            float fps = buf.getFloat();
            popLimit();
            return Triple.of(flags, frames, fps);
        }

        private INode node() throws IOException
        {
            chunk("NODE");
            animations.push(HashBasedTable.<Integer, Optional<INode>, Key>create());
            Triple<Integer, Integer, Float> animData = null;
            Animation animation = null;
            Pair<Brush, List<Face>> mesh = null;
            List<Pair<Vertex, Float>> bone = null;
            Map<Integer, Key> keys = new HashMap<Integer, Key>();
            List<INode> nodes = new ArrayList<INode>();
            String name = readString();
            Vector3f pos = new Vector3f(buf.getFloat(), buf.getFloat(), buf.getFloat());
            Vector3f scale = new Vector3f(buf.getFloat(), buf.getFloat(), buf.getFloat());
            Vector4f rot = new Vector4f(buf.getFloat(), buf.getFloat(), buf.getFloat(), buf.getFloat());
            while(buf.hasRemaining())
            {
                readHeader();
                if     (isChunk("MESH")) mesh = mesh();
                else if(isChunk("BONE")) bone = bone();
                else if(isChunk("KEYS")) keys.putAll(keys());
                else if(isChunk("NODE")) nodes.add(node());
                else if(isChunk("ANIM")) animData = anim();
                else skip();
            }
            popLimit();
            Table<Integer, Optional<INode>, Key> keyData = animations.pop();
            INode node;
            if(animData == null)
            {
                node = new Pivot(name, pos, scale, rot, keys, nodes, null);
                for(Table.Cell<Integer, Optional<INode>, Key> key : keyData.cellSet())
                {
                    //logger.info("KEY1: " + key + " " + node);
                    animations.peek().put(key.getRowKey(), key.getColumnKey().or(Optional.of(node)), key.getValue());
                }
            }
            else
            {
                node = new Pivot(name, pos, scale, rot, keys, nodes, animData, keyData);
            }
            if(mesh != null) node = new Mesh(node, mesh);
            else if(bone != null) node = new Bone(node, bone);
            for(INode child : node.getNodes().values())
            {
                child.setParent(node);
            }
            return node;
        }

        private void skip()
        {
            buf.position(buf.position() + length);
        }
    }

    // boilerplate below

    public List<Texture> getTextures()
    {
        return textures;
    }

    public List<Brush> getBrushes()
    {
        return brushes;
    }

    public INode getNode()
    {
        return node;
    }

    public static class Texture
    {
        private final String path;
        private final int flags;
        private final int blend;
        private final Vector2f pos;
        private final Vector2f scale;
        private final float rot;

        public Texture(String path, int flags, int blend, Vector2f pos, Vector2f scale, float rot)
        {
            this.path = path;
            this.flags = flags;
            this.blend = blend;
            this.pos = pos;
            this.scale = scale;
            this.rot = rot;
        }

        public String getPath()
        {
            return path;
        }

        public int getFlags()
        {
            return flags;
        }

        public int getBlend()
        {
            return blend;
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
            return String.format("Texture [path=%s, flags=%s, blend=%s, pos=%s, scale=%s, rot=%s]", path, flags, blend, pos, scale, rot);
        }
    }

    public static class Brush
    {
        private final String name;
        private final Vector4f color;
        private final float shininess;
        private final int blend;
        private final int fx;
        private final List<Texture> textures;

        public Brush(String name, Vector4f color, float shininess, int blend, int fx, List<Texture> textures)
        {
            this.name = name;
            this.color = color;
            this.shininess = shininess;
            this.blend = blend;
            this.fx = fx;
            this.textures = textures;
        }

        public String getName()
        {
            return name;
        }

        public Vector4f getColor()
        {
            return color;
        }

        public float getShininess()
        {
            return shininess;
        }

        public int getBlend()
        {
            return blend;
        }

        public int getFx()
        {
            return fx;
        }

        public List<Texture> getTextures()
        {
            return textures;
        }

        @Override
        public String toString()
        {
            return String.format("Brush [name=%s, color=%s, shininess=%s, blend=%s, fx=%s, textures=%s]", name, color, shininess, blend, fx, textures);
        }
    }

    public static class Vertex
    {
        private final Vector3f pos;
        private final Vector3f normal;
        private final Vector4f color;
        private final Vector4f[] texCoords;
        public Vertex(Vector3f pos, Vector3f normal, Vector4f color, Vector4f[] texCoords)
        {
            this.pos = pos;
            this.normal = normal;
            this.color = color;
            this.texCoords = texCoords;
        }

        public Vector3f getPos()
        {
            return pos;
        }

        public Vector3f getNormal()
        {
            return normal;
        }

        public Vector4f getColor()
        {
            return color;
        }

        public Vector4f[] getTexCoords()
        {
            return texCoords;
        }

        @Override
        public String toString()
        {
            return String.format("Vertex [pos=%s, normal=%s, color=%s, texCoords=%s]", pos, normal, color, java.util.Arrays.toString(texCoords));
        }
    }

    public static class Face
    {
        private final Vertex v1, v2, v3;
        private final Brush brush;

        public Face(Vertex v1, Vertex v2, Vertex v3, Brush brush)
        {
            this.v1 = v1;
            this.v2 = v2;
            this.v3 = v3;
            this.brush = brush;
        }

        public Vertex getV1()
        {
            return v1;
        }

        public Vertex getV2()
        {
            return v2;
        }

        public Vertex getV3()
        {
            return v3;
        }

        @Override
        public String toString()
        {
            return String.format("Face [v1=%s, v2=%s, v3=%s]", v1, v2, v3);
        }
    }

    public static class Key
    {
        private final Vector3f pos;
        private final Vector3f scale;
        private final Vector4f rot;

        public Key(Vector3f pos, Vector3f scale, Vector4f rot)
        {
            this.pos = pos;
            this.scale = scale;
            this.rot = rot;
        }

        public Vector3f getPos()
        {
            return pos;
        }

        public Vector3f getScale()
        {
            return scale;
        }

        public Vector4f getRot()
        {
            return rot;
        }

        @Override
        public String toString()
        {
            return String.format("Key [pos=%s, scale=%s, rot=%s]", pos, scale, rot);
        }
    }

    public static class Animation
    {
        private final int flags;
        private final int frames;
        private final float fps;
        private final ImmutableTable<Integer, INode, Key> keys;

        public Animation(int flags, int frames, float fps, ImmutableTable<Integer, INode, Key> keys)
        {
            this.flags = flags;
            this.frames = frames;
            this.fps = fps;
            this.keys = keys;
        }

        public int getFlags()
        {
            return flags;
        }

        public int getFrames()
        {
            return frames;
        }

        public float getFps()
        {
            return fps;
        }

        public ImmutableTable<Integer, INode, Key> getKeys()
        {
            return keys;
        }

        @Override
        public String toString()
        {
            return String.format("Animation [flags=%s, frames=%s, fps=%s, keys=...]", flags, frames, fps);
        }
    }

    public static interface INode {
        String getName();
        Vector3f getPos();
        Vector3f getScale();
        Vector4f getRot();
        ImmutableMap<Integer, Key> getKeys();
        ImmutableMap<String, INode> getNodes();
        INode getParent();
        void setParent(INode parent);
    }

    public static class Pivot implements INode
    {
        private final String name;
        private final Vector3f pos;
        private final Vector3f scale;
        private final Vector4f rot;
        private final ImmutableMap<Integer, Key> keys;
        private final ImmutableMap<String, INode> nodes;
        private final Animation animation;
        private INode parent;

        public Pivot(String name, Vector3f pos, Vector3f scale, Vector4f rot, Map<Integer, Key> keys, List<INode> nodes, Animation animation)
        {
            this.name = name;
            this.pos = pos;
            this.scale = scale;
            this.rot = rot;
            this.keys = ImmutableMap.copyOf(keys);
            this.nodes = buildNodeMap(nodes);
            this.animation = animation;
        }

        public Pivot(String name, Vector3f pos, Vector3f scale, Vector4f rot, Map<Integer, Key> keys, List<INode> nodes, Triple<Integer, Integer, Float> animData, Table<Integer, Optional<INode>, Key> keyData)
        {
            this.name = name;
            this.pos = pos;
            this.scale = scale;
            this.rot = rot;
            this.keys = ImmutableMap.copyOf(keys);
            this.nodes = buildNodeMap(nodes);

            ImmutableTable.Builder<Integer, INode, Key> builder = ImmutableTable.builder();
            for(Table.Cell<Integer, Optional<INode>, Key> key : keyData.cellSet())
            {
                //System.out.println("KEY2: " + key + " " + this);
                builder.put(key.getRowKey(), key.getColumnKey().or(this), key.getValue());
            }
            animation = new Animation(animData.getLeft(), animData.getMiddle(), animData.getRight(), builder.build());
        }

        private ImmutableMap<String, INode> buildNodeMap(List<INode> nodes)
        {
            ImmutableMap.Builder<String, INode> builder = ImmutableMap.builder();
            for(INode node : nodes)
            {
                builder.put(node.getName(), node);
            }
            return builder.build();
        }

        public String getName()
        {
            return name;
        }

        public Vector3f getPos()
        {
            return pos;
        }

        public Vector3f getScale()
        {
            return scale;
        }

        public Vector4f getRot()
        {
            return rot;
        }

        public ImmutableMap<Integer, Key> getKeys()
        {
            return keys;
        }

        public ImmutableMap<String, INode> getNodes()
        {
            return nodes;
        }

        public Animation getAnimation()
        {
            return animation;
        }

        public INode getParent()
        {
            return parent;
        }

        public void setParent(INode parent)
        {
            this.parent = parent;
        }

        @Override
        public String toString()
        {
            return String.format("Node [name=%s, pos=%s, scale=%s, rot=%s, keys=..., nodes=..., animation=%s]", name, pos, scale, rot, animation);
        }
    }

    public static class Mesh implements INode
    {
        private final INode parent;
        private final Brush brush;
        private final ImmutableList<Face> faces;

        private Set<Bone> bones = new HashSet<Bone>();

        private boolean newBones = false;
        private final Multimap<Vertex, Pair<Float, Bone>> weightMap = HashMultimap.create();

        public Mesh(INode parent, Pair<Brush, List<Face>> data)
        {
            this.parent = parent;
            this.brush = data.getLeft();
            this.faces = ImmutableList.copyOf(data.getRight());
        }

        public ImmutableMultimap<Vertex, Pair<Float, Bone>> getWeightMap()
        {
            if(newBones)
            {
                weightMap.clear();
                for(Bone bone : getBones())
                {
                    for(Pair<Vertex, Float> data : bone.getData())
                    {
                        weightMap.put(data.getLeft(), Pair.of(data.getRight(), bone));
                    }
                }
            }
            return ImmutableMultimap.copyOf(weightMap);
        }

        public Brush getBrush()
        {
            return brush;
        }

        public ImmutableList<Face> getFaces()
        {
            return faces;
        }

        public String getName()
        {
            return parent.getName();
        }

        public Vector3f getPos()
        {
            return parent.getPos();
        }

        public Vector3f getScale()
        {
            return parent.getScale();
        }

        public Vector4f getRot()
        {
            return parent.getRot();
        }

        public ImmutableMap<Integer, Key> getKeys()
        {
            return parent.getKeys();
        }

        public ImmutableMap<String, INode> getNodes()
        {
            return parent.getNodes();
        }

        public INode getParent()
        {
            return parent.getParent();
        }

        public void setParent(INode parent)
        {
            this.parent.setParent(parent);
        }

        public ImmutableSet<Bone> getBones()
        {
            return ImmutableSet.copyOf(bones);
        }

        void addBone(Bone bone)
        {
            bones.add(bone);
        }

        @Override
        public String toString()
        {
            return String.format("Mesh [brush=%s, data=...]", brush);
        }
    }

    public static class Bone implements INode
    {
        private final INode parent;
        private final List<Pair<Vertex, Float>> data;

        public Bone(INode parent, List<Pair<Vertex, Float>> data)
        {
            this.parent = parent;
            this.data = data;
        }

        public List<Pair<Vertex, Float>> getData()
        {
            return data;
        }

        public String getName()
        {
            return parent.getName();
        }

        public Vector3f getPos()
        {
            return parent.getPos();
        }

        public Vector3f getScale()
        {
            return parent.getScale();
        }

        public Vector4f getRot()
        {
            return parent.getRot();
        }

        public ImmutableMap<Integer, Key> getKeys()
        {
            return parent.getKeys();
        }

        public ImmutableMap<String, INode> getNodes()
        {
            return parent.getNodes();
        }

        public INode getParent()
        {
            return parent.getParent();
        }

        public void setParent(INode parent)
        {
            this.parent.setParent(parent);
            Mesh mesh = getParentMesh();
            if(mesh == null)
            {
                logger.error(String.format("no parent mesh for bone %s while setting parent to %s", this, parent));
            }
            else
            {
                mesh.addBone(this);
            }
        }

        /*@Override
        public String toString()
        {
            return String.format("Bone [data=%s]", data);
        }*/

        public Mesh getParentMesh()
        {
            INode res = parent;
            while(res != null && !(res instanceof Mesh)) res = res.getParent();
            return (Mesh)res;
        }
    }
}
