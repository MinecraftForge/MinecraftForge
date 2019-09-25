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

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;

public abstract class ModelFile {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    protected ResourceLocation location;

    protected ModelFile(ResourceLocation location) {
        this.location = location;
    }

    protected abstract boolean exists();

    public ResourceLocation getLocation() {
        Preconditions.checkState(exists(), "Model at " + location + " does not exist!");
        return location;
    }

    public ResourceLocation getUncheckedLocation() {
        return location;
    }

    public static class ExistingModelFile extends ModelFile {

        public ExistingModelFile(ResourceLocation location) {
            super(location);
        }

        @Override
        protected boolean exists() {
            //TODO proper check for non-generated model file
            return true;
        }
    }

    public static class GeneratedModelFile<T extends ModelBuilder<T>> extends ModelFile {
        private boolean generated = false;
        private final T generator;
        private final String folder;

        public GeneratedModelFile(ResourceLocation location, T generator, String folder) {
            super(location);
            this.generator = generator;
            this.folder = folder;
        }

        @Override
        protected boolean exists() {
            return generated;
        }

        public void generate(Path basePath, DirectoryCache cache) {
            Path target = getPath(basePath);
            try {
                IDataProvider.save(GSON, cache, generator.serialize(), target);
            } catch (IOException e) {
                LOGGER.error("Couldn't save model to {}", target, e);
            }
            generated = true;
        }

        protected Path getPath(Path basePath) {
            ResourceLocation loc = getUncheckedLocation();
            return basePath.resolve("assets/" + loc.getNamespace() + "/models/" + folder + "/" + loc.getPath() + ".json");
        }

        public T getBuilder() {
            return generator;
        }
    }
}
