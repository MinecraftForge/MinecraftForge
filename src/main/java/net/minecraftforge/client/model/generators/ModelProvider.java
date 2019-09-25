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
import java.util.function.Supplier;

public abstract class ModelProvider<T extends ModelBuilder<T>> implements IDataProvider {

    public static final String BLOCK_FOLDER = "block";
    public static final String ITEM_FOLDER = "item";

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    protected final DataGenerator generator;
    protected final String modid;
    protected final String folder;
    protected final Supplier<T> factory;
    protected final Map<ResourceLocation, GeneratedModelFile<T>> generatedModels = new HashMap<>();

    protected abstract void registerBuilders();

    public ModelProvider(DataGenerator generator, String modid, String folder, Supplier<T> factory) {
        this.generator = generator;
        this.modid = modid;
        this.folder = folder;
        this.factory = factory;
    }

    protected T getBuilder(String path) {
        return getModelFile(path).getBuilder();
    }

    protected GeneratedModelFile<T> getModelFile(String path) {
        ResourceLocation loc = new ResourceLocation(modid, path);
        return generatedModels.computeIfAbsent(loc, $ -> new GeneratedModelFile<>(loc, factory.get(), folder));
    }

    @Override
    public void act(DirectoryCache cache) throws IOException {
        generatedModels.clear();
        registerBuilders();
        for (GeneratedModelFile<T> e : generatedModels.values()) {
            e.generate(generator.getOutputFolder(), cache);
        }
    }
}
