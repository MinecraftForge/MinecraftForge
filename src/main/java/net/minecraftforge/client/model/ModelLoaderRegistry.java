package net.minecraftforge.client.model;

import java.util.Deque;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader.VanillaLoader;
import net.minecraftforge.client.model.ModelLoader.VariantLoader;
import net.minecraftforge.client.model.b3d.B3DLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.FMLLog;

import org.apache.logging.log4j.Level;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

/*
 * Central hub for custom model loaders.
 */
public class ModelLoaderRegistry
{
    private static final Set<ICustomModelLoader> loaders = Sets.newHashSet();
    private static final Map<ResourceLocation, IModel> cache = Maps.newHashMap();
    private static final Deque<ResourceLocation> loadingModels = Queues.newArrayDeque();
    private static final Set<ResourceLocation> textures = Sets.newHashSet();

    // Forge built-in loaders
    static
    {
        registerLoader(B3DLoader.instance);
        registerLoader(OBJLoader.instance);
        registerLoader(ModelFluid.FluidLoader.instance);
        registerLoader(ItemLayerModel.Loader.instance);
        registerLoader(MultiLayerModel.Loader.instance);
        registerLoader(ModelDynBucket.LoaderDynBucket.instance);
    }

    /*
     * Makes system aware of your loader.
     */
    public static void registerLoader(ICustomModelLoader loader)
    {
        loaders.add(loader);
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(loader);
    }

    public static boolean loaded(ResourceLocation location)
    {
        return cache.containsKey(location);
    }


    public static ResourceLocation getActualLocation(ResourceLocation location)
    {
        if(location instanceof ModelResourceLocation) return location;
        if(location.getResourcePath().startsWith("builtin/")) return location;
        return new ResourceLocation(location.getResourceDomain(), "models/" + location.getResourcePath());
    }

    /**
     * Primary method to get IModel instances.
     * ResourceLocation argument will be passed directly to the custom model loaders,
     * ModelResourceLocation argument will be loaded through the blockstate system.
     */
    public static IModel getModel(ResourceLocation location) throws Exception
    {
        IModel model;
        if(cache.containsKey(location)) return cache.get(location);
        for(ResourceLocation loading : loadingModels)
        {
            if(location.getClass() == loading.getClass() && location.equals(loading))
            {
                throw new LoaderException("circular model dependencies, stack: [" + Joiner.on(", ").join(loadingModels) + "]");
            }
        }
        loadingModels.addLast(location);
        try
        {
            ResourceLocation actual = getActualLocation(location);
            ICustomModelLoader accepted = null;
            for(ICustomModelLoader loader : loaders)
            {
                try
                {
                    if(loader.accepts(actual))
                    {
                        if(accepted != null)
                        {
                            throw new LoaderException(String.format("2 loaders (%s and %s) want to load the same model %s", accepted, loader, location));
                        }
                        accepted = loader;
                    }
                }
                catch(Exception e)
                {
                    throw new LoaderException(String.format("Exception checking if model %s can be loaded with loader %s, skipping", location, loader), e);
                }
            }

            // no custom loaders found, try vanilla ones
            if(accepted == null)
            {
                if(VariantLoader.instance.accepts(actual))
                {
                     accepted = VariantLoader.instance;
                }
                else if(VanillaLoader.instance.accepts(actual))
                {
                    accepted = VanillaLoader.instance;
                }
            }

            if(accepted == null)
            {
                throw new LoaderException("no suitable loader found for the model " + location + ", skipping");
            }
            try
            {
                model = accepted.loadModel(actual);
            }
            catch(Exception e)
            {
                throw new LoaderException(String.format("Exception loading model %s with loader %s, skipping", location, accepted), e);
            }
            if(model == getMissingModel())
            {
                throw new LoaderException(String.format("Loader %s returned missing model while loading model %s", accepted, location));
            }
            if(model == null)
            {
                throw new LoaderException(String.format("Loader %s returned null while loading model %s", accepted, location));
            }
            textures.addAll(model.getTextures());
        }
        finally
        {
            ResourceLocation popLoc = loadingModels.removeLast();
            if(popLoc != location)
            {
                throw new IllegalStateException("Corrupted loading model stack: " + popLoc + " != " + location);
            }
        }
        cache.put(location, model);
        for (ResourceLocation dep : model.getDependencies())
        {
            getModelOrMissing(dep);
        }
        return model;
    }

    /**
     * Use this if you don't care about the exception and want some model anyway.
     */
    public static IModel getModelOrMissing(ResourceLocation location)
    {
        try
        {
            return getModel(location);
        }
        catch(Exception e)
        {
            return getMissingModel();
        }
    }

    /**
     * Use this if you want the model, but need to log the error.
     */
    public static IModel getModelOrLogError(ResourceLocation location, String error)
    {
        try
        {
            return getModel(location);
        }
        catch(Exception e)
        {
            FMLLog.getLogger().error(error, e);
            return getMissingModel();
        }
    }

    public static IModel getMissingModel()
    {
        return ModelLoader.VanillaLoader.instance.getLoader().getMissingModel();
    }

    public static void clearModelCache()
    {
        cache.clear();
        // putting the builtin models in
        cache.put(new ResourceLocation("minecraft:builtin/generated"), ItemLayerModel.instance);
        cache.put(new ResourceLocation("minecraft:block/builtin/generated"), ItemLayerModel.instance);
        cache.put(new ResourceLocation("minecraft:item/builtin/generated"), ItemLayerModel.instance);
    }

    static Iterable<ResourceLocation> getTextures()
    {
        return textures;
    }

    public static class LoaderException extends Exception
    {
        public LoaderException(String message)
        {
            super(message);
        }

        public LoaderException(String message, Throwable cause)
        {
            super(message, cause);
        }

        private static final long serialVersionUID = 1L;
    }
}
