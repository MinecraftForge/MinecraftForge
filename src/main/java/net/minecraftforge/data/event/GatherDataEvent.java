/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.data.event;

import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraft.DetectedVersion;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.event.IModBusEvent;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GatherDataEvent extends Event implements IModBusEvent
{
    private final DataGenerator dataGenerator;
    private final DataGeneratorConfig config;
    private final ExistingFileHelper existingFileHelper;
    private final ModContainer modContainer;

    public GatherDataEvent(final ModContainer mc, final DataGenerator dataGenerator, final DataGeneratorConfig dataGeneratorConfig, ExistingFileHelper existingFileHelper)
    {
        this.modContainer = mc;
        this.dataGenerator = dataGenerator;
        this.config = dataGeneratorConfig;
        this.existingFileHelper = existingFileHelper;
    }

    public ModContainer getModContainer() {
        return this.modContainer;
    }

    public Collection<Path> getInputs() { return this.config.getInputs(); }
    public DataGenerator getGenerator() { return this.dataGenerator; }
    public ExistingFileHelper getExistingFileHelper() { return existingFileHelper; }
    public CompletableFuture<HolderLookup.Provider> getLookupProvider() { return this.config.lookupProvider; }
    public boolean includeServer() { return this.config.server; }
    public boolean includeClient() { return this.config.client; }
    public boolean includeDev() { return this.config.dev; }
    public boolean includeReports() { return this.config.reports; }
    public boolean validate() { return this.config.validate; }

    public static class DataGeneratorConfig {
        private final Set<String> mods;
        private final Path path;
        private final Collection<Path> inputs;
        private final CompletableFuture<HolderLookup.Provider> lookupProvider;
        private final boolean server;
        private final boolean client;
        private final boolean dev;
        private final boolean reports;
        private final boolean validate;
        private final boolean flat;
        private final List<DataGenerator> generators = new ArrayList<>();

        public DataGeneratorConfig(final Set<String> mods, final Path path, final Collection<Path> inputs, final CompletableFuture<HolderLookup.Provider> lookupProvider,
                final boolean server, final boolean client, final boolean dev, final boolean reports, final boolean validate, final boolean flat) {
            this.mods = mods;
            this.path = path;
            this.inputs = inputs;
            this.lookupProvider = lookupProvider;
            this.server = server;
            this.client = client;
            this.dev = dev;
            this.reports = reports;
            this.validate = validate;
            this.flat = flat;

        }

        public Collection<Path> getInputs() {
            return this.inputs;
        }

        public Set<String> getMods() {
            return mods;
        }

        public boolean isFlat() {
            return flat || getMods().size() == 1;
        }

        public DataGenerator makeGenerator(final Function<Path,Path> pathEnhancer, final boolean shouldExecute) {
            final DataGenerator generator = new DataGenerator(pathEnhancer.apply(path), DetectedVersion.tryDetectVersion(), shouldExecute);
            if (shouldExecute)
                generators.add(generator);
            return generator;
        }

        public void runAll() {
            Map<Path, List<DataGenerator>> paths = generators.stream().collect(Collectors.groupingBy(gen -> gen.getPackOutput().getOutputFolder(), LinkedHashMap::new, Collectors.toList()));

            paths.values().forEach(LamdbaExceptionUtils.rethrowConsumer(lst -> {
                DataGenerator parent = lst.get(0);
                for (int x = 1; x < lst.size(); x++)
                    lst.get(x).getProvidersView().forEach((name, provider) -> parent.addProvider(true, provider));
                parent.run();
            }));
        }
    }
}
