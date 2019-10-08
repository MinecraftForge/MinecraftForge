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

package net.minecraftforge.client.model.generators;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelFile.GeneratedModelFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class ModelProvider<T extends ModelBuilder<T>> implements IDataProvider {

    public static final String BLOCK_FOLDER = "block";
    public static final String ITEM_FOLDER = "item";

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    protected final DataGenerator generator;
    protected final String modid;
    protected final String folder;
    protected final Function<ResourceLocation, T> factory;
    protected final Map<ResourceLocation, T> generatedModels = new HashMap<>();
    protected final ExistingFileHelper existingFileHelper;

    protected DirectoryCache cache;

    protected abstract void registerBuilders();

    public ModelProvider(DataGenerator generator, String modid, String folder, Function<ResourceLocation, T> factory, ExistingFileHelper existingFileHelper) {
        this.generator = generator;
        this.modid = modid;
        this.folder = folder;
        this.factory = factory;
        this.existingFileHelper = existingFileHelper;
    }

    public ModelProvider(DataGenerator generator, String modid, String folder, BiFunction<ResourceLocation, ExistingFileHelper, T> builderFromModId, ExistingFileHelper existingFileHelper) {
        this(generator, modid, folder, loc->builderFromModId.apply(loc, existingFileHelper), existingFileHelper);
    }

    protected T getBuilder(String path) {
        ResourceLocation loc = new ResourceLocation(modid, path);
        ResourceLocation outputLoc = new ResourceLocation(modid, folder+"/"+path);
        return generatedModels.computeIfAbsent(loc, $->factory.apply(outputLoc));
    }

    protected GeneratedModelFile<T> getModelFile(String path) {
        ResourceLocation loc = new ResourceLocation(modid, path);
        T builder = generatedModels.get(loc);
        Preconditions.checkNotNull(builder);
        GeneratedModelFile<T> ret = builder.getOutputFile();
        Preconditions.checkNotNull(ret);
        return ret;
    }

    protected ModelFile.ExistingModelFile getExistingFile(String path) {
        return new ModelFile.ExistingModelFile(path, existingFileHelper);
    }

    @Override
    public void act(DirectoryCache cache) throws IOException {
        this.cache = cache;
        generatedModels.clear();
        registerBuilders();
        this.cache = null;
    }
}
