package net.minecraftforge.client.model.obj;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.FMLLog;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

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
                OBJModel.Parser parser = new OBJModel.Parser(res, manager);
                OBJModel model = parser.parse();
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
    
    public static class OBJModelLocation extends ResourceLocation
    {
        public final String mesh;
        
        public OBJModelLocation(String domain, String path, String mesh)
        {
            super(domain, path);
            this.mesh = mesh;
        }
        
        public String getMesh()
        {
            return this.mesh;
        }
        
        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + ((mesh == null) ? 0 : mesh.hashCode());
            return result;
        }
        
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj) return true;
            if (!super.equals(obj)) return false;
            OBJModelLocation other = (OBJModelLocation) obj;
            if (mesh == null) if (other.mesh != null) return false;
            else if (!mesh.equals(other.mesh)) return false;
            return true;
        }
    }
}
