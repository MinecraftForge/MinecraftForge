package net.minecraftforge.client.model.obj;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.FMLLog;

import org.apache.logging.log4j.Level;

/*
 * Loader for OBJ models.
 * To enable your mod call instance.addDomain(modid).
 * If you need more control over accepted resources - extend the class, and register a new instance with ModelLoaderRegistry.
 */
public class OBJLoader implements ICustomModelLoader {
    public static final OBJLoader instance = new OBJLoader();
    private IResourceManager manager;
    private final Set<String> enabledDomains = new HashSet<String>();
    private final Map<ResourceLocation, OBJModel> cache = new HashMap<ResourceLocation, OBJModel>();

    public void addDomain(String domain)
    {
        enabledDomains.add(domain.toLowerCase());
        FMLLog.log(Level.INFO, "OBJLoader: Domain %s has been added.", domain.toLowerCase());
    }

    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        this.manager = resourceManager;
        cache.clear();
    }

    public boolean accepts(ResourceLocation modelLocation)
    {
        return enabledDomains.contains(modelLocation.getResourceDomain()) && modelLocation.getResourcePath().endsWith(".obj");
    }

    public IModel loadModel(ResourceLocation modelLocation) throws IOException
    {
        ResourceLocation file = new ResourceLocation(modelLocation.getResourceDomain(), modelLocation.getResourcePath());
        if (!cache.containsKey(file))
        {
            try
            {
                IResource resource = null;
                try
                {
                    resource = manager.getResource(file);
                }
                catch (FileNotFoundException e)
                {
                    if (modelLocation.getResourcePath().startsWith("models/block/"))
                        resource = manager.getResource(new ResourceLocation(file.getResourceDomain(), "models/item/" + file.getResourcePath().substring("models/block/".length())));
                    else if (modelLocation.getResourcePath().startsWith("models/item/"))
                        resource = manager.getResource(new ResourceLocation(file.getResourceDomain(), "models/block/" + file.getResourcePath().substring("models/item/".length())));
                    else throw e;
                }
                OBJModel.Parser parser = new OBJModel.Parser(resource, manager);
                OBJModel model = null;
                try
                {
                	model = parser.parse();
                }
                finally
                {
                	cache.put(modelLocation, model);
                }
            }
            catch (IOException e)
            {
//                FMLLog.log(Level.ERROR, e, "Exception loading model '%s' with OBJ loader, skipping", modelLocation);
                throw e;
            }
        }
        OBJModel model = cache.get(file);
        if (model == null) return ModelLoaderRegistry.getMissingModel();
        return model;
    }
}
