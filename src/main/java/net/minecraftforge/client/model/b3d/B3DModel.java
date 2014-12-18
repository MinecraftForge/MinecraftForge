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
import java.util.List;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class B3DModel {

    private final List<Texture> textures;
    private final List<Brush> brushes;
    private final Node node;

    public B3DModel(List<Texture> textures, List<Brush> brushes, Node node)
    {
        this.textures = textures;
        this.brushes = brushes;
        this.node = node;
    }

    public static class Parser
    {
        private static final int version = 0001;
        private static final Logger logger = LogManager.getLogger(Parser.class);
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
            return this.tag.equals(tag.getBytes("US-ASCII"));
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
            byte[] tmp = new byte[end - start];
            buf.get(tmp);
            return new String(tmp, "UTF8");
        }

        private Deque<Integer> limitStack = new ArrayDeque<Integer>();

        private void pushLimit()
        {
            limitStack.push(buf.position() + length);
        }

        private void popLimit()
        {
            buf.position(limitStack.pop());
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
            Node node = node();
            popLimit();
            return new B3DModel(textures, brushes, node);
        }

        private List<Texture> texs() throws IOException
        {
            chunk("TEXS");
            List<Texture> ret = new ArrayList<Texture>();
            while(buf.hasRemaining())
            {
                String path = readString();
                int flags = buf.getInt();
                int blend = buf.getInt();
                Vector2f pos = new Vector2f(buf.getFloat(), buf.getFloat());
                Vector2f scale = new Vector2f(buf.getFloat(), buf.getFloat());
                float rot = buf.getFloat();
                ret.add(new Texture(path, flags, blend, pos, scale, rot));
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

        private Pair<Brush, List<Face>> tris() throws IOException
        {
            chunk("TRIS");
            List<Face> ret = new ArrayList<Face>();
            int brush_id = buf.getInt();
            while(buf.hasRemaining())
            {
                ret.add(new Face(getVertex(buf.getInt()), getVertex(buf.getInt()), getVertex(buf.getInt())));
            }
            popLimit();
            return Pair.of(getBrush(brush_id), ret);
        }

        private Mesh mesh() throws IOException
        {
            chunk("MESH");
            int brush_id = buf.getInt();
            readHeader();
            vrts();
            List<Pair<Brush, List<Face>>> ret = new ArrayList<Pair<Brush, List<Face>>>();
            do
            {
                readHeader();
                ret.add(tris());
            }
            while(buf.hasRemaining());
            popLimit();
            return new Mesh(getBrush(brush_id), ret);
        }

        private Bone bone() throws IOException
        {
            chunk("BONE");
            List<Pair<Vertex, Float>> ret = new ArrayList<Pair<Vertex, Float>>();
            while(buf.hasRemaining())
            {
                ret.add(Pair.of(getVertex(buf.getInt()), buf.getFloat()));
            }
            popLimit();
            return new Bone(ret);
        }

        private List<Key> keys() throws IOException
        {
            chunk("KEYS");
            List<Key> ret = new ArrayList<Key>();
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
                ret.add(new Key(pos, scale, rot));
            }
            popLimit();
            return ret;
        }

        private Animation anim() throws IOException
        {
            chunk("ANIM");
            int flags = buf.getInt();
            int frames = buf.getInt();
            float fps = buf.getFloat();
            popLimit();
            return new Animation(flags, frames, fps);
        }

        private Node node() throws IOException
        {
            chunk("NODE");
            List<Key> keys = new ArrayList<Key>();
            List<Node> nodes = new ArrayList<Node>();
            Animation animation = null;
            Object kind = null;
            String name = readString();
            Vector3f pos = new Vector3f(buf.getFloat(), buf.getFloat(), buf.getFloat());
            Vector3f scale = new Vector3f(buf.getFloat(), buf.getFloat(), buf.getFloat());
            Vector4f rot = new Vector4f(buf.getFloat(), buf.getFloat(), buf.getFloat(), buf.getFloat());
            readHeader();
            if(isChunk("MESH")) kind = mesh();
            else if(isChunk("BONE")) kind = bone();
            else skip();
            String last = "KEYS";
            while(buf.hasRemaining())
            {
                readHeader();
                if(last.equals("KEYS"))
                {
                    if(isChunk(last)) keys.addAll(keys());
                    else
                    {
                        last = "NODE";
                        if(!isChunk(last)) skip();
                        else nodes.add(node());
                    }
                }
                else if(last.equals("NODE"))
                {
                    if(isChunk(last)) nodes.add(node());
                    else
                    {
                        last = "ANIM";
                        if(!isChunk(last)) skip();
                        else animation = anim();
                    }
                }
                else skip();
            }
            popLimit();
            return new Node(name, pos, scale, rot, kind, keys, nodes, animation);
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

    public Node getNode()
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
    }

    public static class Face
    {

        public Face(Vertex vertex, Vertex vertex2, Vertex vertex3)
        {
            // TODO Auto-generated constructor stub
        }
    }

    public static class Mesh
    {

        public Mesh(Brush brush, List<Pair<Brush, List<Face>>> ret)
        {
            // TODO Auto-generated constructor stub
        }
    }

    public static class Bone
    {

        public Bone(List<Pair<Vertex, Float>> ret)
        {
            // TODO Auto-generated constructor stub
        }
    }

    public static class Key
    {

        public Key(Vector3f pos, Vector3f scale, Vector4f rot)
        {
            // TODO Auto-generated constructor stub
        }
    }

    public static class Animation
    {

        public Animation(int flags, int frames, float fps)
        {
            // TODO Auto-generated constructor stub
        }
    }

    public static class Node
    {

        public Node(String name, Vector3f pos, Vector3f scale, Vector4f rot, Object kind, List<Key> keys, List<Node> nodes, Animation animation)
        {
            // TODO Auto-generated constructor stub
        }
    }
}
