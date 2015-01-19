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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * Loader for OBJ models.
 * To enable your mod call instance.addDomain(modid).
 * If you need more control over accepted resources - extend the class, and register a new instance with ModelLoaderRegistry.
 */
public class OBJLoader implements ICustomModelLoader {
    public static final Logger logger = LogManager.getLogger(OBJLoader.class);
    public static final OBJLoader instance = new OBJLoader();
    
    private IResourceManager manager;
    
    private final Set<String> enabledDomains = new HashSet<String>();
//    private final Map<ResourceLocation, OBJModel2> cache = new HashMap<ResourceLocation, OBJModel2>();
    private final Map<ResourceLocation, OBJModel> cache = new HashMap<ResourceLocation, OBJModel>();
    
    public OBJLoader()
    {
        logger.info("OBJLoader: constructed");
        ModelLoaderRegistry.registerLoader(this);
    }
    
    public void addDomain(String domain)
    {
        logger.printf(Level.INFO, "OBJLoader: adding domain: %s", domain);
        enabledDomains.add(domain.toLowerCase());
    }
    
    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        this.manager = resourceManager;
//        OBJModel2.setManager(manager);
        cache.clear();
    }

    public boolean accepts(ResourceLocation modelLocation)
    {
        return enabledDomains.contains(modelLocation.getResourceDomain()) && modelLocation.getResourcePath().endsWith(".obj");
    }
    
    @SuppressWarnings("unchecked")
    public IModel loadModel(ResourceLocation modelLocation)
    {
        logger.printf(Level.INFO, "OBJLoader: loading model at location: %s, %s", modelLocation.getResourceDomain(), modelLocation.getResourcePath());
        if (!cache.containsKey(modelLocation))
        {
            try
            {
                IResource res;
                try
                {
                    logger.printf(Level.INFO, "OBJLoader: trying to get resource for location: %s, %s", modelLocation.getResourceDomain(), modelLocation.getResourcePath());
                    res = manager.getResource(modelLocation);
                    logger.printf(Level.INFO, "OBJLoader: got resource %s, %s from manager", res.getResourceLocation().getResourceDomain(), res.getResourceLocation().getResourcePath());
                }
                catch (FileNotFoundException e)
                {
                    if (modelLocation.getResourcePath().startsWith("models/block/"))
                    {
                        res = manager.getResource(new ResourceLocation(modelLocation.getResourceDomain(), "models/item/" + modelLocation.getResourcePath().substring("models/block/".length())));
                        logger.printf(Level.INFO, "OBJLoader: got resource %s, %s from models/item/", res.getResourceLocation().getResourceDomain(), res.getResourceLocation().getResourcePath());
                    }
                    else if (modelLocation.getResourcePath().startsWith("models/item/"))
                    {
                        res = manager.getResource(new ResourceLocation(modelLocation.getResourceDomain(), "models/block/" + modelLocation.getResourcePath().substring("models/item/".length())));
                        logger.printf(Level.INFO, "OBJLoader: got resource %s, %s from models/block/", res.getResourceLocation().getResourceDomain(), res.getResourceLocation().getResourcePath());
                    }
                    else
                    {
                        throw e;
                    }
                }
//                OBJModel2 model = loadFromResource(res);
                OBJModel model = loadFromResource(res);
                cache.put(modelLocation, model);
            }
            catch (IOException e)
            {
                FMLLog.log(Level.ERROR, e, "Exception loading model %s with OBJ loader, skipping", modelLocation);
                cache.put(modelLocation, null);
            }
        }
        
        OBJModel model = cache.get(modelLocation);
        if (model == null) return ModelLoaderRegistry.getMissingModel();
        return model;
    }
    
    public OBJModel loadFromResource(IResource res) throws IOException
    {
        logger.printf(Level.INFO, "OBJLoader: loading model from resource: %s, %s", res.getResourceLocation().getResourceDomain(), res.getResourceLocation().getResourcePath());
//        OBJModel2 model = new OBJModel2();
//        model.parseAndLoadModel(res);
        OBJModel.Parser parser = new OBJModel.Parser(res);
        return parser.parse();
//        return model;
    }
}
