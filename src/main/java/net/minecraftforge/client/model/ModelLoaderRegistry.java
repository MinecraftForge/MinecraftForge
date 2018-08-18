/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.client.model;

import java.util.Deque;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader.VanillaLoader;
import net.minecraftforge.client.model.ModelLoader.VariantLoader;
import net.minecraftforge.client.model.b3d.B3DLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.animation.ITimeValue;
import net.minecraftforge.common.model.animation.AnimationStateMachine;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;
import net.minecraftforge.fml.common.FMLLog;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
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
    private static final Map<ResourceLocation, ResourceLocation> aliases = Maps.newHashMap();

    private static IResourceManager manager;

    // Forge built-in loaders
    static
    {
        registerLoader(B3DLoader.INSTANCE);
        registerLoader(OBJLoader.INSTANCE);
        registerLoader(ModelFluid.FluidLoader.INSTANCE);
        registerLoader(ItemLayerModel.Loader.INSTANCE);
        registerLoader(MultiLayerModel.Loader.INSTANCE);
        registerLoader(ModelDynBucket.LoaderDynBucket.INSTANCE);
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

        IModel cached = cache.get(location);
        if (cached != null) return cached;

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
            ResourceLocation aliased = aliases.get(location);
            if (aliased != null) return getModel(aliased);

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
                if(VariantLoader.INSTANCE.accepts(actual))
                {
                     accepted = VariantLoader.INSTANCE;
                }
                else if(VanillaLoader.INSTANCE.accepts(actual))
                {
                    accepted = VanillaLoader.INSTANCE;
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
            return getMissingModel(location, e);
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
            FMLLog.log.error(error, e);
            return getMissingModel(location, e);
        }
    }

    public static IModel getMissingModel()
    {
        final ModelLoader loader = VanillaLoader.INSTANCE.getLoader();
        if(loader == null)
        {
            throw new IllegalStateException("Using ModelLoaderRegistry too early.");
        }
        return loader.getMissingModel();
    }

    static IModel getMissingModel(ResourceLocation location, Throwable cause)
    {
        //IModel model =  new FancyMissingModel(ExceptionUtils.getStackTrace(cause).replaceAll("\\t", "    "));
        IModel model = new FancyMissingModel(getMissingModel(), location.toString());
        textures.addAll(model.getTextures());
        return model;
    }

    static void addAlias(ResourceLocation from, ResourceLocation to)
    {
        aliases.put(from, to);
    }

    public static void clearModelCache(IResourceManager manager)
    {
        ModelLoaderRegistry.manager = manager;
        aliases.clear();
        textures.clear();
        cache.clear();
        // putting the builtin models in
        cache.put(new ResourceLocation("minecraft:builtin/generated"), ItemLayerModel.INSTANCE);
        cache.put(new ResourceLocation("minecraft:block/builtin/generated"), ItemLayerModel.INSTANCE);
        cache.put(new ResourceLocation("minecraft:item/builtin/generated"), ItemLayerModel.INSTANCE);
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

    public static IAnimationStateMachine loadASM(ResourceLocation location, ImmutableMap<String, ITimeValue> customParameters)
    {
        return AnimationStateMachine.load(manager, location, customParameters);
    }
}
