package net.minecraftforge.client.model.b3d;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;
import org.lwjgl.BufferUtils;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.io.LittleEndianDataInputStream;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICameraTransformations;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelTransformation;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.b3d.B3DModel.*;
import net.minecraftforge.fml.common.FMLLog;

/*
 * Loader for Blitz3D models.
 * To enable for your mod call instance.addDomain(modid).
 * If you need more control over accepted resources - extend the class, and register a new instance with ModelLoaderRegistry.
 */
public class B3DLoader implements ICustomModelLoader {
    public static final B3DLoader instance = new B3DLoader();

    private IResourceManager manager;

    private final Set<String> enabledDomains = new HashSet<String>();

    public B3DLoader()
    {
        ModelLoaderRegistry.registerLoader(this);
    }

    public void addDomain(String domain)
    {
        enabledDomains.add(domain.toLowerCase());
    }

    public void onResourceManagerReload(IResourceManager manager)
    {
        this.manager = manager;
    }

    public boolean accepts(ResourceLocation modelLocation)
    {
        return enabledDomains.contains(modelLocation.getResourceDomain()) && modelLocation.getResourcePath().endsWith(".b3d");
    }

    public IModel loadModel(ResourceLocation modelLocation)
    {
        try
        {
            IResource resource = null;
            try
            {
                resource = manager.getResource(modelLocation);
            }
            catch(FileNotFoundException e)
            {
                if(modelLocation.getResourcePath().startsWith("models/block/"))
                    resource = manager.getResource(new ResourceLocation(modelLocation.getResourceDomain(), "models/item/" + modelLocation.getResourcePath().substring("models/block/".length())));
                else if(modelLocation.getResourcePath().startsWith("models/item/"))
                    resource = manager.getResource(new ResourceLocation(modelLocation.getResourceDomain(), "models/block/" + modelLocation.getResourcePath().substring("models/item/".length())));
                else throw e;
            }
            B3DModel.Parser parser = new B3DModel.Parser(resource.getInputStream());
            B3DModel model = parser.parse();
            return new Wrapper(model.getNode());
        }
        catch(IOException e)
        {
            FMLLog.log(Level.ERROR, e, "Exception loading model %s with B3D loader, skipping", modelLocation);
        }
        return ModelLoaderRegistry.getMissingModel();
    }

    public static class B3DFrame implements IModelTransformation
    {
        private final int frame;
        public B3DFrame(int frame)
        {
            this.frame = frame;
        }

        public int getFrame()
        {
            return frame;
        }
    }

    private static class Wrapper implements IModel
    {
        private final B3DModel.INode node;

        public Wrapper(B3DModel.INode node)
        {
            this.node = node;
        }

        public Collection<ResourceLocation> getDependencies()
        {
            // no dependencies for in-file models
            return Collections.emptyList();
        }

        public Collection<ResourceLocation> getTextures()
        {
            // TODO Auto-generated method stub
            return Collections.emptyList();
        }

        public IFlexibleBakedModel bake(IModelTransformation transformation, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
        {
            // TODO handle vanilla transformations
            if(transformation instanceof ModelRotation) return new BakedWrapper(getDefaultTransformation(), node, bakedTextureGetter);
            if(!(transformation instanceof B3DFrame))
                throw new UnsupportedOperationException("can only bake b3d models with b3d or vanilla transformations, got: " + transformation);
            // TODO Auto-generated method stub
            //return ModelLoaderRegistry.getMissingModel().bake(transformation, bakedTextureGetter);
            return new BakedWrapper(transformation, node, bakedTextureGetter);
        }

        public IModelTransformation getDefaultTransformation()
        {
            // TODO Auto-generated method stub
            return new B3DFrame(0);
        }
    }
    private static class BakedWrapper implements IFlexibleBakedModel
    {
        private final IModelTransformation transformation;
        private final B3DModel.INode node;
        private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;

        public BakedWrapper(IModelTransformation transformation, INode node, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
        {
            this.transformation = transformation;
            this.node = node;
            this.bakedTextureGetter = bakedTextureGetter;
        }

        public List<BakedQuad> getFaceQuads(EnumFacing side)
        {
            return Collections.emptyList();
        }

        private static final int BYTES_IN_INT = Integer.SIZE / Byte.SIZE;
        private static final int VERTICES_IN_QUAD = 4;
        private static final int BLOCK_FORMAT_INT_SIZE = DefaultVertexFormats.BLOCK.getNextOffset() / BYTES_IN_INT;
        private static final ByteBuffer buf = BufferUtils.createByteBuffer(VERTICES_IN_QUAD * DefaultVertexFormats.BLOCK.getNextOffset());

        public List<BakedQuad> getGeneralQuads()
        {
            ImmutableList.Builder<BakedQuad> ret = ImmutableList.builder();
            for(INode child : node.getNodes().values())
            {
                ret.addAll(new BakedWrapper(transformation, child, bakedTextureGetter).getGeneralQuads());
            }
            if(node instanceof Mesh)
            {
                Mesh mesh = (Mesh) node;
                Multimap<Vertex, Pair<Float, Bone>> weightMap = mesh.getWeightMap();
                for(Face f : mesh.getFaces())
                {
                    buf.clear();
                    putVertexData(f.getV1());
                    putVertexData(f.getV2());
                    putVertexData(f.getV3());
                    putVertexData(f.getV3());
                    buf.flip();
                    int[] data = new int[VERTICES_IN_QUAD * BLOCK_FORMAT_INT_SIZE];
                    buf.asIntBuffer().get(data);
                    ret.add(new BakedQuad(data , -1, EnumFacing.UP));
                }
            }
            return ret.build();
        }

        private final void putVertexData(Vertex v)
        {
            // see DefaultVertexFormats.BLOCK
            // TODO handle everything not handled (texture transformations, bones, transformations, e.t.c)
            buf.putFloat(v.getPos().x);
            buf.putFloat(v.getPos().y);
            buf.putFloat(v.getPos().z);
            if(v.getColor() != null)
            {
                buf.put((byte)(v.getColor().x / (Byte.MAX_VALUE - 1)));
                buf.put((byte)(v.getColor().y / (Byte.MAX_VALUE - 1)));
                buf.put((byte)(v.getColor().z / (Byte.MAX_VALUE - 1)));
                buf.put((byte)(v.getColor().w / (Byte.MAX_VALUE - 1)));
            }
            else
            {
                buf.putInt(0);
            }
            if(v.getTexCoords().length > 0)
            {
                buf.putFloat(v.getTexCoords()[0].x);
                buf.putFloat(v.getTexCoords()[0].y);
            }
            else
            {
                buf.putFloat(0).putFloat(0);
            }
            if(v.getTexCoords().length > 1)
            {
                buf.putShort((short)(v.getTexCoords()[1].x / (Short.MAX_VALUE - 1)));
                buf.putShort((short)(v.getTexCoords()[1].y / (Short.MAX_VALUE - 1)));
            }
            else
            {
                buf.putInt(0);
            }
        }

        public boolean isAmbientOcclusion()
        {
            return true;
        }

        public boolean isGui3d()
        {
            return true;
        }

        public boolean isBuiltInRenderer()
        {
            return false;
        }

        public TextureAtlasSprite getTexture()
        {
            // TODO Auto-generated method stub
            return null;
        }

        public ItemCameraTransforms getItemCameraTransforms()
        {
            return ItemCameraTransforms.DEFAULT;
        }

        public ICameraTransformations getCameraTransforms()
        {
            return ItemCameraTransforms.DEFAULT;
        }
    }
}
