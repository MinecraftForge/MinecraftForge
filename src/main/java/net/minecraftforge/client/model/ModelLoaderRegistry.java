package net.minecraftforge.client.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader.VanillaLoader;
import net.minecraftforge.client.model.b3d.B3DLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.FMLLog;

import org.apache.logging.log4j.Level;

/*
 * Central hub for custom model loaders.
 */
public class ModelLoaderRegistry
{
    private static final Set<ICustomModelLoader> loaders = new HashSet<ICustomModelLoader>();
    private static final Map<ResourceLocation, IModel> cache = new HashMap<ResourceLocation, IModel>();

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
        if(location.getResourcePath().startsWith("builtin/")) return location;
        return new ResourceLocation(location.getResourceDomain(), "models/" + location.getResourcePath());
    }

    /**
     * Primary method to get IModel instances.
     * ResourceLocation argument will be passed directly to the custom model loaders,
     * ModelResourceLocation argument will be loaded through the blockstate system.
     */
    public static IModel getModel(ResourceLocation location) throws IOException
    {
        IModel model;
        if(location instanceof ModelResourceLocation)
        {
            ModelLoader loader = ModelLoader.VanillaLoader.instance.getLoader();
            if(loader != null)
            {
                model = loader.getVariantModel((ModelResourceLocation)location);
            }
            else
            {
                FMLLog.log(Level.ERROR, "Loading model too early, skipping: %s", location);
                model = getMissingModel();
            }
        }
        else
        {
            if(cache.containsKey(location)) return cache.get(location);
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
                            FMLLog.severe("2 loaders (%s and %s) want to load the same model %s", accepted, loader, location);
                            throw new IllegalStateException("2 loaders want to load the same model");
                        }
                        accepted = loader;
                    }
                }
                catch(Exception e)
                {
                    FMLLog.log(Level.ERROR, e, "Exception checking if model %s can be loaded with loader %s, skipping", location, loader);
                }
            }

            // no custom loaders found, try vanilla one
            if(accepted == null)
            {
                if(VanillaLoader.instance.accepts(actual)) accepted = VanillaLoader.instance;
            }

            if(accepted == null)
            {
                FMLLog.severe("no suitable loader found for the model %s, skipping", location);
                model = getMissingModel();
            }
            else
            {
                try
                {
                    model = accepted.loadModel(actual);
                }
                catch (IOException e)
                {
                    throw e;
                }
                catch(Exception e)
                {
                    FMLLog.log(Level.ERROR, e, "Exception loading model %s with loader %s, skipping", location, accepted);
                    model = getMissingModel();
                }
            }
        }
        cache.put(location, model);
        return model;
    }

    public static IModel getMissingModel()
    {
        return ModelLoader.VanillaLoader.instance.getLoader().getMissingModel();
    }

    public static void clearModelCache()
    {
        cache.clear();
        // putting the builtin models in
        cache.put(new ResourceLocation("minecraft:builtin/generated"), ModelLoader.VanillaLoader.instance.getLoader().getItemModel());
        cache.put(new ResourceLocation("minecraft:block/builtin/generated"), ModelLoader.VanillaLoader.instance.getLoader().getItemModel());
        cache.put(new ResourceLocation("minecraft:item/builtin/generated"), ModelLoader.VanillaLoader.instance.getLoader().getItemModel());
    }
}
