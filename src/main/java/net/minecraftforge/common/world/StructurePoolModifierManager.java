/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.common.world;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class StructurePoolModifierManager extends SimpleJsonResourceReloadListener
{

    public static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON_INSTANCE = new GsonBuilder().create();
    private static final String folder = "structure_pool_modifiers";
    private Map<ResourceLocation, IStructurePoolModifier> registeredModifiers = ImmutableMap.of();

    public StructurePoolModifierManager()
    {
        super(GSON_INSTANCE, folder);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resourceList, ResourceManager resourceManagerIn, ProfilerFiller p_10795_)
    {
        ImmutableMap.Builder<ResourceLocation, IStructurePoolModifier> builder = ImmutableMap.builder();

        ArrayList<ResourceLocation> finalLocations = new ArrayList<ResourceLocation>();
        ResourceLocation resourcelocation = new ResourceLocation("forge", "structure_pool_modifiers/structure_pool_modifiers.json");
        try
        {
            //read in all data files from forge:structure_pool_modifiers/structure_pool_modifiers in order to do layering
            for (Resource iresource : resourceManagerIn.getResources(resourcelocation))
            {
                try (InputStream inputstream = iresource.getInputStream();
                     Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
                )
                {
                    JsonObject jsonobject = GsonHelper.fromJson(GSON_INSTANCE, reader, JsonObject.class);
                    boolean replace = jsonobject.get("replace").getAsBoolean();
                    if (replace) finalLocations.clear();
                    JsonArray entryList = jsonobject.get("entries").getAsJsonArray();
                    for (JsonElement entry : entryList)
                    {
                        String loc = entry.getAsString();
                        ResourceLocation res = new ResourceLocation(loc);
                        if (finalLocations.contains(res)) finalLocations.remove(res);
                        finalLocations.add(res);
                    }
                } catch (RuntimeException | IOException ioexception)
                {
                    LOGGER.error("Couldn't read structure pool modifier list {} in data pack {}", resourcelocation, iresource.getSourceName(), ioexception);
                } finally
                {
                    IOUtils.closeQuietly((Closeable) iresource);
                }
            }
        } catch (IOException ioexception1)
        {
            LOGGER.error("Couldn't read structure pool modifier list from {}", resourcelocation, ioexception1);
        }
        //use layered config to fetch modifier data files (modifiers missing from config are disabled)
        finalLocations.forEach(location -> {
            try
            {
                IStructurePoolModifier modifier = deserializeModifier(location, resourceList.get(location));
                if (modifier != null)
                    builder.put(location, modifier);
            } catch (Exception exception)
            {
                LOGGER.error("Couldn't parse structure pool modifier {}", location, exception);
            }
        });
        this.registeredModifiers = builder.build();
    }

    private IStructurePoolModifier deserializeModifier(ResourceLocation location, JsonElement element)
    {
        if (!element.isJsonObject()) return null;
        JsonObject object = element.getAsJsonObject();
        ResourceLocation serializerLocation = new ResourceLocation(GsonHelper.getAsString(object, "type"));
        StructurePoolModifierSerializer<?> serialzier = ForgeRegistries.STRUCTURE_MODIFIER_SERIALIZERS.getValue(serializerLocation);
        if (serialzier == null)
        {
            throw new RuntimeException(String.format("Couldn't find type %s for structure pool modifier %s", serializerLocation, location));
        }
        return serialzier.read(location, object);
    }

    /**
     * @return an immutable collection of all registered structure pool modifiers
     */
    public Collection<IStructurePoolModifier> getAllStructurePoolModifiers()
    {
        return registeredModifiers.values();
    }

}
