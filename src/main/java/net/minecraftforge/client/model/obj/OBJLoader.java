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

package net.minecraftforge.client.model.obj;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import joptsimple.internal.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class OBJLoader implements IModelLoader<OBJModel>
{
    public static OBJLoader INSTANCE = new OBJLoader();

    private final Map<ModelSettings, OBJModel> modelCache = Maps.newHashMap();
    private final Map<ResourceLocation, MaterialLibrary> materialCache = Maps.newHashMap();

    private IResourceManager manager = Minecraft.getInstance().getResourceManager();

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        modelCache.clear();
        materialCache.clear();
        manager = resourceManager;
    }

    @Override
    public OBJModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents)
    {
        if (!modelContents.has("model"))
            throw new RuntimeException("OBJ Loader requires a 'model' key that points to a valid .OBJ model.");

        String modelLocation = modelContents.get("model").getAsString();

        boolean detectCullableFaces = JSONUtils.getBoolean(modelContents, "detectCullableFaces", true);
        boolean diffuseLighting = JSONUtils.getBoolean(modelContents, "diffuseLighting", false);
        boolean flipV = JSONUtils.getBoolean(modelContents, "flip-v", false);
        boolean ambientToFullbright = JSONUtils.getBoolean(modelContents, "ambientToFullbright", true);
        @Nullable
        String materialLibraryOverrideLocation = modelContents.has("materialLibraryOverride") ? JSONUtils.getString(modelContents, "materialLibraryOverride") : null;

        return loadModel(new ModelSettings(new ResourceLocation(modelLocation), detectCullableFaces, diffuseLighting, flipV, ambientToFullbright, materialLibraryOverrideLocation));
    }

    public OBJModel loadModel(ModelSettings settings)
    {
        return modelCache.computeIfAbsent(settings, (data) -> {
            IResource resource;
            try
            {
                resource = manager.getResource(settings.modelLocation);
            }
            catch (IOException e)
            {
                throw new RuntimeException("Could not find OBJ model", e);
            }

            try(LineReader rdr = new LineReader(resource))
            {
                return new OBJModel(rdr, settings);
            }
            catch (Exception e)
            {
                throw new RuntimeException("Could not read OBJ model", e);
            }
        });
    }

    public MaterialLibrary loadMaterialLibrary(ResourceLocation materialLocation)
    {
        return materialCache.computeIfAbsent(materialLocation, (location) -> {
            IResource resource;
            try
            {
                resource = manager.getResource(location);
            }
            catch (IOException e)
            {
                throw new RuntimeException("Could not find OBJ material library", e);
            }

            try(LineReader rdr = new LineReader(resource))
            {
                return new MaterialLibrary(rdr);
            }
            catch (Exception e)
            {
                throw new RuntimeException("Could not read OBJ material library", e);
            }
        });
    }

    public static class LineReader implements AutoCloseable
    {
        InputStreamReader lineStream;
        BufferedReader lineReader;

        public LineReader(IResource resource)
        {
            this.lineStream = new InputStreamReader(resource.getInputStream(), Charsets.UTF_8);
            this.lineReader = new BufferedReader(lineStream);
        }

        @Nullable
        public String[] readAndSplitLine(boolean ignoreEmptyLines) throws IOException
        {
            //noinspection LoopConditionNotUpdatedInsideLoop
            do
            {
                String currentLine = lineReader.readLine();
                if (currentLine == null)
                    return null;

                List<String> lineParts = new ArrayList<>();

                if (currentLine.startsWith("#"))
                    currentLine = "";

                if (currentLine.length() > 0)
                {

                    boolean hasContinuation;
                    do
                    {
                        hasContinuation = currentLine.endsWith("\\");
                        String tmp = hasContinuation ? currentLine.substring(0, currentLine.length() - 1) : currentLine;

                        Arrays.stream(tmp.split("[\t ]+")).filter(s -> !Strings.isNullOrEmpty(s)).forEach(lineParts::add);

                        if (hasContinuation)
                        {
                            currentLine = lineReader.readLine();
                            if (currentLine == null)
                                break;

                            if (currentLine.length() == 0 || currentLine.startsWith("#"))
                                break;
                        }
                    } while (hasContinuation);
                }

                if (lineParts.size() > 0)
                    return lineParts.toArray(new String[0]);
            }
            while (ignoreEmptyLines);

            return new String[0];
        }

        @Override
        public void close() throws Exception
        {
            lineReader.close();
            lineStream.close();
        }
    }

    public static class ModelSettings
    {
        @Nonnull
        public final ResourceLocation modelLocation;
        public final boolean detectCullableFaces;
        public final boolean diffuseLighting;
        public final boolean flipV;
        public final boolean ambientToFullbright;
        @Nullable
        public final String materialLibraryOverrideLocation;

        public ModelSettings(@Nonnull ResourceLocation modelLocation, boolean detectCullableFaces, boolean diffuseLighting, boolean flipV, boolean ambientToFullbright,
                             @Nullable String materialLibraryOverrideLocation)
        {
            this.modelLocation = modelLocation;
            this.detectCullableFaces = detectCullableFaces;
            this.diffuseLighting = diffuseLighting;
            this.flipV = flipV;
            this.ambientToFullbright = ambientToFullbright;
            this.materialLibraryOverrideLocation = materialLibraryOverrideLocation;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ModelSettings that = (ModelSettings) o;
            return equals(that);
        }

        public boolean equals(@Nonnull ModelSettings that)
        {
            return detectCullableFaces == that.detectCullableFaces &&
                    diffuseLighting == that.diffuseLighting &&
                    flipV == that.flipV &&
                    ambientToFullbright == that.ambientToFullbright &&
                    modelLocation.equals(that.modelLocation) &&
                    Objects.equals(materialLibraryOverrideLocation, that.materialLibraryOverrideLocation);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(modelLocation, detectCullableFaces, diffuseLighting, flipV, ambientToFullbright, materialLibraryOverrideLocation);
        }
    }
}
