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

package net.minecraftforge.client.model.obj;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * Loader for OBJ models.
 * To enable your mod call instance.addDomain(modid).
 * If you need more control over accepted resources - extend the class, and register a new instance with ModelLoaderRegistry.
 */
public enum OBJLoader implements ICustomModelLoader {
    INSTANCE;

    private static final Logger LOGGER = LogManager.getLogger();
    private IResourceManager manager;
    private final Set<String> enabledDomains = new HashSet<>();
    private final Map<ResourceLocation, OBJModel> cache = new HashMap<>();
    private final Map<ResourceLocation, Exception> errors = new HashMap<>();

    public void addDomain(String domain)
    {
        enabledDomains.add(domain.toLowerCase());
        LOGGER.info("OBJLoader: Domain {} has been added.", domain.toLowerCase());
    }

    @Override
    public void func_195410_a(IResourceManager resourceManager)
    {
        this.manager = resourceManager;
        cache.clear();
        errors.clear();
    }

    @Override
    public boolean accepts(ResourceLocation modelLocation)
    {
        return enabledDomains.contains(modelLocation.getNamespace()) && modelLocation.getPath().endsWith(".obj");
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws Exception
    {
        ResourceLocation file = new ResourceLocation(modelLocation.getNamespace(), modelLocation.getPath());
        if (!cache.containsKey(file))
        {
            IResource resource = null;
            try
            {
                try
                {
                    resource = manager.getResource(file);
                }
                catch (FileNotFoundException e)
                {
                    if (modelLocation.getPath().startsWith("models/block/"))
                        resource = manager.getResource(new ResourceLocation(file.getNamespace(), "models/item/" + file.getPath().substring("models/block/".length())));
                    else if (modelLocation.getPath().startsWith("models/item/"))
                        resource = manager.getResource(new ResourceLocation(file.getNamespace(), "models/block/" + file.getPath().substring("models/item/".length())));
                    else throw e;
                }
                OBJModel.Parser parser = new OBJModel.Parser(resource, manager);
                OBJModel model = null;
                try
                {
                    model = parser.parse();
                }
                catch (Exception e)
                {
                    errors.put(modelLocation, e);
                }
                finally
                {
                    cache.put(modelLocation, model);
                }
            }
            finally
            {
                IOUtils.closeQuietly(resource);
            }
        }
        OBJModel model = cache.get(file);
        if (model == null) throw new ModelLoaderRegistry.LoaderException("Error loading model previously: " + file, errors.get(modelLocation));
        return model;
    }
}
