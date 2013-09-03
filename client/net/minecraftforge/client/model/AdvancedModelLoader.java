package net.minecraftforge.client.model;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import net.minecraftforge.client.model.obj.ObjModelLoader;
import net.minecraftforge.client.model.techne.TechneModelLoader;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Common interface for advanced model loading from files, based on file suffix
 * Model support can be queried through the {@link #getSupportedSuffixes()} method.
 * Instances can be created by calling {@link #loadModel(String)} with a class-loadable-path
 *
 * @author cpw
 *
 */
@SideOnly(Side.CLIENT)
public class AdvancedModelLoader {
    private static Map<String, IModelCustomLoader> instances = Maps.newHashMap();

    /**
     * Register a new model handler
     * @param modelHandler The model handler to register
     */
    public static void registerModelHandler(IModelCustomLoader modelHandler)
    {
        for (String suffix : modelHandler.getSuffixes())
        {
            instances.put(suffix, modelHandler);
        }
    }

    /**
     * Load the model from the supplied classpath resolvable resource name
     * @param resourceName The resource name
     * @return A model
     * @throws IllegalArgumentException if the resource name cannot be understood
     * @throws ModelFormatException if the underlying model handler cannot parse the model format
     */
    public static IModelCustom loadModel(String resourceName) throws IllegalArgumentException, ModelFormatException
    {
        int i = resourceName.lastIndexOf('.');
        if (i == -1)
        {
            FMLLog.severe("The resource name %s is not valid", resourceName);
            throw new IllegalArgumentException("The resource name is not valid");
        }
        String suffix = resourceName.substring(i+1);
        IModelCustomLoader loader = instances.get(suffix);
        if (loader == null)
        {
            FMLLog.severe("The resource name %s is not supported", resourceName);
            throw new IllegalArgumentException("The resource name is not supported");
        }

        URL resource = AdvancedModelLoader.class.getResource(resourceName);
        if (resource == null)
        {
            FMLLog.severe("The resource name %s could not be found", resourceName);
            throw new IllegalArgumentException("The resource name could not be found");
        }
        return loader.loadInstance(resourceName, resource);
    }

    public static Collection<String> getSupportedSuffixes()
    {
        return instances.keySet();
    }


    static
    {
        registerModelHandler(new ObjModelLoader());
        registerModelHandler(new TechneModelLoader());
    }
}
