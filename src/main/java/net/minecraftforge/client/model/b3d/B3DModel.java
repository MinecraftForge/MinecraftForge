/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.b3d;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraftforge.versions.forge.ForgeVersion;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;
import com.google.common.base.Joiner;
import java.util.Optional;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;

import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.world.phys.Vec2;

@Deprecated(forRemoval = true, since = "1.18")
public class B3DModel
{
    static final Logger logger = LogManager.getLogger(ForgeVersion.MOD_ID + ".B3DModel");
    private static final boolean printLoadedModels = "true".equals(System.getProperty("b3dloader.printLoadedModels"));
    private final List<Texture> textures;
    private final List<Brush> brushes;
    private final Node<?> root;
    private final ImmutableMap<String, Node<Mesh>> meshes;

    public B3DModel(List<Texture> textures, List<Brush> brushes, Node<?> root, ImmutableMap<String, Node<Mesh>> meshes)
    {
        this.textures = textures;
        this.brushes = brushes;
        this.root = root;
        this.meshes = meshes;
    }

    public static class Parser
    {
        private static final int version = 1;
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
                ((Buffer)buf).clear();
                buf.put(tag);
                buf.put(tmp);
                buf.put(IOUtils.toByteArray(in, l));
                ((Buffer)buf).flip();
            }
        }

        private String dump = "";
        private void dump(String str)
        {
            if(printLoadedModels)
            {
                dump += str + "\n";
            }
        }

        private B3DModel res;

        public B3DModel parse() throws IOException
        {
            if(res != null) return res;
            dump = "\n";
            readHeader();
            res = bb3d();
            if(printLoadedModels)
            {
                logger.info(dump);
            }
            return res;
        }

        private final List<Texture> textures = new ArrayList<>();

        private Texture getTexture(int texture)
        {
            if(texture > textures.size())
            {
                logger.error("texture {} is out of range", texture);
                return null;
            }
            else if(texture == -1) return Texture.White;
            return textures.get(texture);
        }

        private final List<Brush> brushes = new ArrayList<>();

        private @Nullable Brush getBrush(int brush) throws IOException
        {
            if(brush > brushes.size())
            {
                throw new IOException(String.format(Locale.ENGLISH, "brush %s is out of range", brush));
            }
            else if(brush == -1) return null;
            return brushes.get(brush);
        }

        private final List<Vertex> vertices = new ArrayList<>();

        private Vertex getVertex(int vertex) throws IOException
        {
            if(vertex > vertices.size())
            {
                throw new IOException(String.format(Locale.ENGLISH, "vertex %s is out of range", vertex));
            }
            return vertices.get(vertex);
        }

        private final ImmutableMap.Builder<String, Node<Mesh>> meshes = ImmutableMap.builder();

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
            ((Buffer)buf).position(start);
            buf.get(tmp);
            buf.get();
            return new String(tmp, "UTF8");
        }

        private Deque<Integer> limitStack = new ArrayDeque<>();

        private void pushLimit()
        {
            limitStack.push(buf.limit());
            ((Buffer)buf).limit(buf.position() + length);
        }

        private void popLimit()
        {
            ((Buffer)buf).limit(limitStack.pop());
        }

        private B3DModel bb3d() throws IOException
        {
            chunk("BB3D");
            int version = buf.getInt();
            if(version / 100 > Parser.version / 100)
                throw new IOException("Unsupported major model version: " + ((float)version / 100));
            if(version % 100 > Parser.version % 100)
                logger.warn(String.format(Locale.ENGLISH, "Minor version difference in model: %s", ((float)version / 100)));
            List<Texture> textures = Collections.emptyList();
            List<Brush> brushes = Collections.emptyList();
            Node<?> root = null;
            dump("BB3D(version = " + version + ") {");
            while(buf.hasRemaining())
            {
                readHeader();
                if     (isChunk("TEXS")) textures = texs();
                else if(isChunk("BRUS")) brushes = brus();
                else if(isChunk("NODE")) root = node();
                else skip();
            }
            dump("}");
            popLimit();
            if (root == null) {
                throw new IOException("not found the root node in the model");
            }
            return new B3DModel(textures, brushes, root, meshes.build());
        }

        private List<Texture> texs() throws IOException
        {
            chunk("TEXS");
            List<Texture> ret = new ArrayList<>();
            while(buf.hasRemaining())
            {
                String path = readString();
                int flags = buf.getInt();
                int blend = buf.getInt();
                Vec2 pos = new Vec2(buf.getFloat(), buf.getFloat());
                Vec2 scale = new Vec2(buf.getFloat(), buf.getFloat());
                float rot = buf.getFloat();
                ret.add(new Texture(path, flags, blend, pos, scale, rot));
            }
            dump("TEXS([" + Joiner.on(", ").join(ret) + "])");
            popLimit();
            this.textures.addAll(ret);
            return ret;
        }

        private List<Brush> brus() throws IOException
        {
            chunk("BRUS");
            List<Brush> ret = new ArrayList<>();
            int n_texs = buf.getInt();
            while(buf.hasRemaining())
            {
                String name = readString();
                Vector4f color = new Vector4f(buf.getFloat(), buf.getFloat(), buf.getFloat(), buf.getFloat());
                float shininess = buf.getFloat();
                int blend = buf.getInt();
                int fx = buf.getInt();
                List<Texture> textures = new ArrayList<>();
                for(int i = 0; i < n_texs; i++) textures.add(getTexture(buf.getInt()));
                ret.add(new Brush(name, color, shininess, blend, fx, textures));
            }
            dump("BRUS([" + Joiner.on(", ").join(ret) + "])");
            popLimit();
            this.brushes.addAll(ret);
            return ret;
        }

        private List<Vertex> vrts() throws IOException
        {
            chunk("VRTS");
            List<Vertex> ret = new ArrayList<>();
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
                Vector4f[] tex_coords = new Vector4f[tex_coord_sets];
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
                        logger.error(String.format(Locale.ENGLISH, "Unsupported number of texture coords: %s", tex_coord_set_size));
                        tex_coords[i] = new Vector4f(0, 0, 0, 1);
                    }
                }
                ret.add(new Vertex(v, n, color, tex_coords));
            }
            dump("VRTS([" + Joiner.on(", ").join(ret) + "])");
            popLimit();
            this.vertices.clear();
            this.vertices.addAll(ret);
            return ret;
        }

        private List<Face> tris() throws IOException
        {
            chunk("TRIS");
            List<Face> ret = new ArrayList<>();
            int brush_id = buf.getInt();
            while(buf.hasRemaining())
            {
                ret.add(new Face(getVertex(buf.getInt()), getVertex(buf.getInt()), getVertex(buf.getInt()), getBrush(brush_id)));
            }
            dump("TRIS([" + Joiner.on(", ").join(ret) + "])");
            popLimit();
            return ret;
        }

        private Pair<Brush, List<Face>> mesh() throws IOException
        {
            chunk("MESH");
            int brush_id = buf.getInt();
            readHeader();
            dump("MESH(brush = " + brush_id + ") {");
            vrts();
            List<Face> ret = new ArrayList<>();
            while(buf.hasRemaining())
            {
                readHeader();
                ret.addAll(tris());
            }
            dump("}");
            popLimit();
            return Pair.of(getBrush(brush_id), ret);
        }

        private List<Pair<Vertex, Float>> bone() throws IOException
        {
            chunk("BONE");
            List<Pair<Vertex, Float>> ret = new ArrayList<>();
            while(buf.hasRemaining())
            {
                ret.add(Pair.of(getVertex(buf.getInt()), buf.getFloat()));
            }
            dump("BONE(...)");
            popLimit();
            return ret;
        }

        private final Deque<Table<Integer, Optional<Node<?>>, Key>> animations = new ArrayDeque<>();

        private Map<Integer, Key> keys() throws IOException
        {
            chunk("KEYS");
            Map<Integer, Key> ret = new HashMap<>();
            int flags = buf.getInt();
            Vector3f pos = null, scale = null;
            Quaternion rot = null;
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
                    rot = readQuat();
                }
                Key key = new Key(pos, scale, rot);
                Key oldKey = animations.peek().get(frame, null);
                if(oldKey != null)
                {
                    if(pos != null)
                    {
                        if(oldKey.getPos() != null) logger.error("Duplicate keys: {} and {} (ignored)", oldKey, key);
                        else key = new Key(oldKey.getPos(), key.getScale(), key.getRot());
                    }
                    if(scale != null)
                    {
                        if(oldKey.getScale() != null) logger.error("Duplicate keys: {} and {} (ignored)", oldKey, key);
                        else key = new Key(key.getPos(), oldKey.getScale(), key.getRot());
                    }
                    if(rot != null)
                    {
                        if(oldKey.getRot() != null) logger.error("Duplicate keys: {} and {} (ignored)", oldKey, key);
                        else key = new Key(key.getPos(), key.getScale(), oldKey.getRot());
                    }
                }
                animations.peek().put(frame, Optional.empty(), key);
                ret.put(frame, key);
            }
            dump("KEYS([(" + Joiner.on("), (").withKeyValueSeparator(" -> ").join(ret) + ")])");
            popLimit();
            return ret;
        }

        private Triple<Integer, Integer, Float> anim() throws IOException
        {
            chunk("ANIM");
            int flags = buf.getInt();
            int frames = buf.getInt();
            float fps = buf.getFloat();
            dump("ANIM(" + flags + ", " + frames + ", " + fps + ")");
            popLimit();
            return Triple.of(flags, frames, fps);
        }

        private Node<?> node() throws IOException
        {
            chunk("NODE");
            animations.push(HashBasedTable.create());
            Triple<Integer, Integer, Float> animData = null;
            Pair<Brush, List<Face>> mesh = null;
            List<Pair<Vertex, Float>> bone = null;
            Map<Integer, Key> keys = new HashMap<>();
            List<Node<?>> nodes = new ArrayList<>();
            String name = readString();
            Vector3f pos = new Vector3f(buf.getFloat(), buf.getFloat(), buf.getFloat());
            Vector3f scale = new Vector3f(buf.getFloat(), buf.getFloat(), buf.getFloat());
            Quaternion rot = readQuat();
            dump("NODE(" + name + ", " + pos + ", " + scale + ", " + rot + ") {");
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
            dump("}");
            popLimit();
            Table<Integer, Optional<Node<?>>, Key> keyData = animations.pop();
            Node<?> node;
            if(mesh != null)
            {
                Node<Mesh> mNode = Node.create(name, pos, scale, rot, nodes, new Mesh(mesh));
                meshes.put(name, mNode);
                node = mNode;
            }
            else if(bone != null) node = Node.create(name, pos, scale, rot, nodes, new Bone(bone));
            else node = Node.create(name, pos, scale, rot, nodes, new Pivot());
            if(animData == null)
            {
                for(Table.Cell<Integer, Optional<Node<?>>, Key> key : keyData.cellSet())
                {
                    animations.peek().put(key.getRowKey(), Optional.of(key.getColumnKey().orElse(node)), key.getValue());
                }
            }
            else
            {
                node.setAnimation(animData, keyData);
            }
            return node;
        }

        private Quaternion readQuat()
        {
            float w = buf.getFloat();
            float x = buf.getFloat();
            float y = buf.getFloat();
            float z = buf.getFloat();
            return new Quaternion(x, y, z, w);
        }

        private void skip()
        {
            ((Buffer)buf).position(buf.position() + length);
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

    public Node<?> getRoot()
    {
        return root;
    }

    public ImmutableMap<String, Node<Mesh>> getMeshes()
    {
        return meshes;
    }

    public static class Texture
    {
        public static final Texture White = new Texture("builtin/white", 0, 0, new Vec2(0, 0), new Vec2(1, 1), 0);
        private final String path;
        private final int flags;
        private final int blend;
        private final Vec2 pos;
        private final Vec2 scale;
        private final float rot;

        public Texture(String path, int flags, int blend, Vec2 pos, Vec2 scale, float rot)
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

        public Vec2 getPos()
        {
            return pos;
        }

        public Vec2 getScale()
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
            return String.format(Locale.ENGLISH, "Texture [path=%s, flags=%s, blend=%s, pos=%s, scale=%s, rot=%s]", path, flags, blend, pos, scale, rot);
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
            return String.format(Locale.ENGLISH, "Brush [name=%s, color=%s, shininess=%s, blend=%s, fx=%s, textures=%s]", name, color, shininess, blend, fx, textures);
        }
    }

    public static class Vertex
    {
        private final Vector3f pos;
        @Nullable
        private final Vector3f normal;
        @Nullable
        private final Vector4f color;
        private final Vector4f[] texCoords;
        public Vertex(Vector3f pos, @Nullable Vector3f normal, @Nullable Vector4f color, Vector4f[] texCoords)
        {
            this.pos = pos;
            this.normal = normal;
            this.color = color;
            this.texCoords = texCoords;
        }

        public Vertex bake(Mesh mesh, Function<Node<?>, Matrix4f> animator)
        {
            // geometry
            Float totalWeight = 0f;
            Matrix4f t = new Matrix4f();
            if(mesh.getWeightMap().get(this).isEmpty())
            {
                t.setIdentity();
            }
            else
            {
                for(Pair<Float, Node<Bone>> bone : mesh.getWeightMap().get(this))
                {
                    totalWeight += bone.getLeft();
                    Matrix4f bm = animator.apply(bone.getRight());
                    bm.multiply(bone.getLeft());
                    t.add(bm);
                }
                if(Math.abs(totalWeight) > 1e-4) t.multiply(1f / totalWeight);
                else t.setIdentity();
            }

            Transformation trsr = new Transformation(t);

            // pos
            Vector4f pos = new Vector4f(this.pos);
            pos.setW(1);
            trsr.transformPosition(pos);
            pos.normalize();
            Vector3f rPos = new Vector3f(pos.x(), pos.y(), pos.z());

            // normal
            Vector3f rNormal = null;

            if(this.normal != null)
            {
                rNormal = this.normal.copy();
                trsr.transformNormal(rNormal);
            }

            // texCoords TODO
            return new Vertex(rPos, rNormal, color, texCoords);
        }

        public Vector3f getPos()
        {
            return pos;
        }

        @Nullable
        public Vector3f getNormal()
        {
            return normal;
        }

        @Nullable
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
            return String.format(Locale.ENGLISH, "Vertex [pos=%s, normal=%s, color=%s, texCoords=%s]", pos, normal, color, java.util.Arrays.toString(texCoords));
        }
    }

    public static class Face
    {
        private final Vertex v1, v2, v3;
        @Nullable
        private final Brush brush;
        private final Vector3f normal;

        public Face(Vertex v1, Vertex v2, Vertex v3, @Nullable Brush brush)
        {
            this(v1, v2, v3, brush, getNormal(v1, v2, v3));
        }

        public Face(Vertex v1, Vertex v2, Vertex v3, @Nullable Brush brush, Vector3f normal)
        {
            this.v1 = v1;
            this.v2 = v2;
            this.v3 = v3;
            this.brush = brush;
            this.normal = normal;
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

        @Nullable
        public Brush getBrush()
        {
            return brush;
        }

        @Override
        public String toString()
        {
            return String.format(Locale.ENGLISH, "Face [v1=%s, v2=%s, v3=%s]", v1, v2, v3);
        }

        public Vector3f getNormal()
        {
            return normal;
        }

        public static Vector3f getNormal(Vertex v1, Vertex v2, Vertex v3)
        {
            Vector3f a = v2.getPos().copy();
            a.sub(v1.getPos());
            Vector3f b = v3.getPos().copy();
            b.sub(v1.getPos());
            Vector3f c = a.copy();
            c.cross(b);
            c.normalize();
            return c;
        }
    }

    public static class Key
    {
        @Nullable
        private final Vector3f pos;
        @Nullable
        private final Vector3f scale;
        @Nullable
        private final Quaternion rot;

        public Key(@Nullable Vector3f pos, @Nullable Vector3f scale, @Nullable Quaternion rot)
        {
            this.pos = pos;
            this.scale = scale;
            this.rot = rot;
        }

        @Nullable
        public Vector3f getPos()
        {
            return pos;
        }

        @Nullable
        public Vector3f getScale()
        {
            return scale;
        }

        @Nullable
        public Quaternion getRot()
        {
            return rot;
        }

        @Override
        public String toString()
        {
            return String.format(Locale.ENGLISH, "Key [pos=%s, scale=%s, rot=%s]", pos, scale, rot);
        }
    }

    public static class Animation
    {
        private final int flags;
        private final int frames;
        private final float fps;
        private final ImmutableTable<Integer, Node<?>, Key> keys;

        public Animation(int flags, int frames, float fps, ImmutableTable<Integer, Node<?>, Key> keys)
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

        public ImmutableTable<Integer, Node<?>, Key> getKeys()
        {
            return keys;
        }

        @Override
        public String toString()
        {
            return String.format(Locale.ENGLISH, "Animation [flags=%s, frames=%s, fps=%s, keys=...]", flags, frames, fps);
        }
    }

    public static interface IKind<K extends IKind<K>>
    {
        void setParent(Node<K> parent);
        Node<K> getParent();
    }

    public static class Node<K extends IKind<K>>
    {
        private final String name;
        private final Vector3f pos;
        private final Vector3f scale;
        private final Quaternion rot;
        private final ImmutableMap<String, Node<?>> nodes;
        @Nullable
        private Animation animation;
        private final K kind;
        @Nullable
        private Node<? extends IKind<?>> parent;

        public static <K extends IKind<K>> Node<K> create(String name, Vector3f pos, Vector3f scale, Quaternion rot, List<Node<?>> nodes, K kind)
        {
            return new Node<>(name, pos, scale, rot, nodes, kind);
        }

        public Node(String name, Vector3f pos, Vector3f scale, Quaternion rot, List<Node<?>> nodes, K kind)
        {
            this.name = name;
            this.pos = pos;
            this.scale = scale;
            this.rot = rot;
            this.nodes = buildNodeMap(nodes);
            this.kind = kind;
            kind.setParent(this);
            for(Node<?> child : this.nodes.values())
            {
                child.setParent(this);
            }
        }

        public void setAnimation(Animation animation)
        {
            this.animation = animation;
            Deque<Node<?>> q = new ArrayDeque<>(nodes.values());

            while(!q.isEmpty())
            {
                Node<?> node = q.pop();
                if(node.getAnimation() != null) continue;
                node.setAnimation(animation);
                q.addAll(node.getNodes().values());
            }
        }

        public void setAnimation(Triple<Integer, Integer, Float> animData, Table<Integer, Optional<Node<?>>, Key> keyData)
        {
            ImmutableTable.Builder<Integer, Node<?>, Key> builder = ImmutableTable.builder();
            for(Table.Cell<Integer, Optional<Node<?>>, Key> key : keyData.cellSet())
            {
                builder.put(key.getRowKey(), key.getColumnKey().orElse(this), key.getValue());
            }
            setAnimation(new Animation(animData.getLeft(), animData.getMiddle(), animData.getRight(), builder.build()));
        }

        private ImmutableMap<String, Node<?>> buildNodeMap(List<Node<?>> nodes)
        {
            ImmutableMap.Builder<String, Node<?>> builder = ImmutableMap.builder();
            for(Node<?> node : nodes)
            {
                builder.put(node.getName(), node);
            }
            return builder.build();
        }

        public String getName()
        {
            return name;
        }

        public K getKind()
        {
            return kind;
        }

        public Vector3f getPos()
        {
            return pos;
        }

        public Vector3f getScale()
        {
            return scale;
        }

        public Quaternion getRot()
        {
            return rot;
        }

        public ImmutableMap<String, Node<?>> getNodes()
        {
            return nodes;
        }

        @Nullable
        public Animation getAnimation()
        {
            return animation;
        }

        @Nullable
        public Node<? extends IKind<?>> getParent()
        {
            return parent;
        }

        public void setParent(Node<? extends IKind<?>> parent)
        {
            this.parent = parent;
        }

        @Override
        public String toString()
        {
            return String.format(Locale.ENGLISH, "Node [name=%s, kind=%s, pos=%s, scale=%s, rot=%s, keys=..., nodes=..., animation=%s]", name, kind, pos, scale, rot, animation);
        }
    }

    public static class Pivot implements IKind<Pivot>
    {
        private Node<Pivot> parent;

        @Override
        public void setParent(Node<Pivot> parent)
        {
            this.parent = parent;
        }

        @Override
        public Node<Pivot> getParent()
        {
            return parent;
        }
    }

    public static class Mesh implements IKind<Mesh>
    {
        private Node<Mesh> parent;
        private final Brush brush;
        private final ImmutableList<Face> faces;
        //private final ImmutableList<Bone> bones;

        private Set<Node<Bone>> bones = new HashSet<>();

        private ImmutableMultimap<Vertex, Pair<Float, Node<Bone>>> weightMap = ImmutableMultimap.of();

        public Mesh(Pair<Brush, List<Face>> data)
        {
            this.brush = data.getLeft();
            this.faces = ImmutableList.copyOf(data.getRight());
        }

        public ImmutableMultimap<Vertex, Pair<Float, Node<Bone>>> getWeightMap()
        {
            return weightMap;
        }

        public ImmutableList<Face> bake(Function<Node<?>, Matrix4f> animator)
        {
            ImmutableList.Builder<Face> builder = ImmutableList.builder();
            for(Face f : getFaces())
            {
                Vertex v1 = f.getV1().bake(this, animator);
                Vertex v2 = f.getV2().bake(this, animator);
                Vertex v3 = f.getV3().bake(this, animator);
                builder.add(new Face(v1, v2, v3, f.getBrush()));
            }
            return builder.build();
        }

        public Brush getBrush()
        {
            return brush;
        }

        public ImmutableList<Face> getFaces()
        {
            return faces;
        }

        public ImmutableSet<Node<Bone>> getBones()
        {
            return ImmutableSet.copyOf(bones);
        }

        @Override
        public String toString()
        {
            return String.format(Locale.ENGLISH, "Mesh [pivot=%s, brush=%s, data=...]", super.toString(), brush);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void setParent(Node<Mesh> parent)
        {
            this.parent = parent;
            Deque<Node<?>> queue = new ArrayDeque<>(parent.getNodes().values());
            while(!queue.isEmpty())
            {
                Node<?> node = queue.pop();
                if(node.getKind() instanceof Bone)
                {
                    bones.add((Node<Bone>)node);
                    queue.addAll(node.getNodes().values());
                }
            }
            ImmutableMultimap.Builder<Vertex, Pair<Float, Node<Bone>>> builder = ImmutableMultimap.builder();
            for(Node<Bone> bone : getBones())
            {
                for(Pair<Vertex, Float> b : bone.getKind().getData())
                {
                    builder.put(b.getLeft(), Pair.of(b.getRight(), bone));
                }
            }
            weightMap = builder.build();
        }

        @Override
        public Node<Mesh> getParent()
        {
            return parent;
        }
    }

    public static class Bone implements IKind<Bone>
    {
        private Node<Bone> parent;
        private final List<Pair<Vertex, Float>> data;

        public Bone(List<Pair<Vertex, Float>> data)
        {
            this.data = data;
        }

        public List<Pair<Vertex, Float>> getData()
        {
            return data;
        }

        /*@Override
        public String toString()
        {
            return String.format("Bone [data=%s]", data);
        }*/

        @Override
        public void setParent(Node<Bone> parent)
        {
            this.parent = parent;
        }

        @Override
        public Node<Bone> getParent()
        {
            return parent;
        }
    }
}
