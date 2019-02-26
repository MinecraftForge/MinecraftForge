/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.b3d.B3DLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.animation.ITimeValue;
import net.minecraftforge.common.model.animation.AnimationStateMachine;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;

/*
 * Central hub for custom model loaders.
 */
public class ModelLoaderRegistry
{
    private static final Logger LOGGER = LogManager.getLogger();
    
    private static final Set<ICustomModelLoader> loaders = Sets.newHashSet();
    private static final Map<ResourceLocation, IUnbakedModel> cache = Maps.newHashMap();
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
        ((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener(loader);
    }

    public static boolean loaded(ResourceLocation location)
    {
        return cache.containsKey(location);
    }

    /**
     * Primary method to get IModel instances.
     * ResourceLocation argument will be passed directly to the custom model loaders,
     * ModelResourceLocation argument will be loaded through the blockstate system.
     */
    public static IUnbakedModel getModel(Function<ResourceLocation, IUnbakedModel> fallback, ResourceLocation location) throws LoaderException
    {
        IUnbakedModel model;

        IUnbakedModel cached = cache.get(location);
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
            if (aliased != null) return getModel(fallback, aliased);

            ICustomModelLoader accepted = null;
            for(ICustomModelLoader loader : loaders)
            {
                try
                {
                    if(loader.accepts(location))
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

            if(accepted == null)
            {
                return null;
            }
            
            try
            {
                model = accepted.loadModel(fallback, location);
            }
            catch(Exception e)
            {
                throw new LoaderException(String.format("Exception loading model %s with loader %s, skipping", location, accepted), e);
            }
            if(model == null)
            {
                throw new LoaderException(String.format("Loader %s returned null while loading model %s", accepted, location));
            }
            textures.addAll(model.getTextures(fallback, new HashSet<>()));
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
        for (ResourceLocation dep : model.getOverrideLocations())
        {
            getModelOrMissing(fallback, dep);
        }
        return model;
    }

    /**
     * Use this if you don't care about the exception and want some model anyway.
     */
    public static IUnbakedModel getModelOrMissing(Function<ResourceLocation, IUnbakedModel> fallback, ResourceLocation location)
    {
        try
        {
            return getModel(fallback, location);
        }
        catch(Exception e)
        {
            return getMissingModel(fallback, location, e);
        }
    }

    /**
     * Use this if you want the model, but need to log the error.
     */
    public static IUnbakedModel getModelOrLogError(Function<ResourceLocation, IUnbakedModel> fallback, ResourceLocation location, String error)
    {
        try
        {
            return getModel(fallback, location);
        }
        catch(Exception e)
        {
            LOGGER.error(error, e);
            return getMissingModel(fallback, location, e);
        }
    }

    public static IUnbakedModel getMissingModel(Function<ResourceLocation, IUnbakedModel> getter)
    {
        return getter.apply(ModelBakery.MODEL_MISSING);
    }

    static IUnbakedModel getMissingModel(Function<ResourceLocation, IUnbakedModel> getter, ResourceLocation location, Throwable cause)
    {
        //IModel model =  new FancyMissingModel(ExceptionUtils.getStackTrace(cause).replaceAll("\\t", "    "));
        IUnbakedModel model = getMissingModel(getter);//new FancyMissingModel(getMissingModel(), location.toString());
        textures.addAll(model.getTextures(null, null));
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

    public static IAnimationStateMachine loadASM(ResourceLocation location, ImmutableMap<String, ITimeValue> customParameters, Function<ResourceLocation, IUnbakedModel> modelGetter)
    {
        return AnimationStateMachine.load(manager, location, customParameters, modelGetter);
    }
}
