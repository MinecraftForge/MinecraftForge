package net.minecraftforge.client.model.b3d;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import com.google.common.base.Function;
import com.google.common.io.LittleEndianDataInputStream;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelTransformation;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.FMLLog;

/*
 * Loader for Blitz3D models.
 * To enable for your mod call instance.addDomain(modid).
 * If you need more control over accepted resources - extend the class, and register a new instance with ModelLoaderRegistry.
 */
public class B3DLoader implements ICustomModelLoader {
    private static final B3DLoader instance = new B3DLoader();

    private IResourceManager manager;

    private final Set<String> enabledDomains = new HashSet<String>();

    public void addDomain(String domain)
    {
        enabledDomains.add(domain);
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
            B3DModel.Parser parser = new B3DModel.Parser(manager.getResource(modelLocation).getInputStream());
            B3DModel model = parser.parse();
            return new Wrapper(model);
        }
        catch(IOException e)
        {
            FMLLog.log(Level.ERROR, e, "Exception loading model %s with B3D loader, skipping", modelLocation);
        }
        return ModelLoaderRegistry.getMissingModel();
    }

    private static class Wrapper implements IModel
    {
        private final B3DModel model;

        public Wrapper(B3DModel model)
        {
            this.model = model;
        }

        public Collection<ResourceLocation> getDependencies()
        {
            // no dependencies for in-file models
            return Collections.emptyList();
        }

        public Collection<ResourceLocation> getTextures()
        {
            // TODO Auto-generated method stub
            return null;
        }

        public IFlexibleBakedModel bake(IModelTransformation transformation, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
        {
            // TODO Auto-generated method stub
            return null;
        }

        public IModelTransformation getDefaultTransformation()
        {
            // TODO Auto-generated method stub
            return null;
        }
    }
}
