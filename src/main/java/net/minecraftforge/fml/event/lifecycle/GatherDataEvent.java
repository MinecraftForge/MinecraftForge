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

package net.minecraftforge.fml.event.lifecycle;

import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.fml.ModContainer;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class GatherDataEvent extends ModLifecycleEvent
{
    private final DataGenerator dataGenerator;
    private final DataGeneratorConfig config;
    private final ExistingFileHelper existingFileHelper;
    public GatherDataEvent(final ModContainer modContainer, final DataGenerator dataGenerator, final DataGeneratorConfig dataGeneratorConfig, ExistingFileHelper existingFileHelper)
    {
        super(modContainer);
        this.dataGenerator = dataGenerator;
        this.config = dataGeneratorConfig;
        this.existingFileHelper = existingFileHelper;
    }

    public DataGenerator getGenerator() { return this.dataGenerator; }
    public ExistingFileHelper getExistingFileHelper() { return existingFileHelper; }
    public boolean includeServer() { return this.config.server; }
    public boolean includeClient() { return this.config.client; }
    public boolean includeDev() { return this.config.dev; }
    public boolean includeReports() { return this.config.reports; }
    public boolean validate() { return this.config.validate; }

    public static class DataGeneratorConfig {
        private final Set<String> mods;
        private final Path path;
        private final Collection<Path> inputs;
        private final boolean server;
        private final boolean client;
        private final boolean dev;
        private final boolean reports;
        private final boolean validate;
        private List<DataGenerator> generators = new ArrayList<>();

        public DataGeneratorConfig(final Set<String> mods, final Path path, final Collection<Path> inputs, final boolean server, final boolean client, final boolean dev, final boolean reports, final boolean validate) {
            this.mods = mods;
            this.path = path;
            this.inputs = inputs;
            this.server = server;
            this.client = client;
            this.dev = dev;
            this.reports = reports;
            this.validate = validate;
        }

        public Set<String> getMods() {
            return mods;
        }

        public DataGenerator makeGenerator(final Function<Path,Path> pathEnhancer, final boolean shouldExecute) {
            final DataGenerator generator = new DataGenerator(pathEnhancer.apply(path), inputs);
            if (shouldExecute) generators.add(generator);
            return generator;
        }

        public void runAll() {
            generators.forEach(LamdbaExceptionUtils.rethrowConsumer(DataGenerator::run));
        }
    }
}
