package net.minecraftforge.fml;

import net.minecraft.util.registry.Bootstrap;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;

public class DatagenModLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static GatherDataEvent.DataGeneratorConfig dataGeneratorConfig;
    private static ExistingFileHelper existingFileHelper;
    private static boolean runningDataGen;

    public static boolean isRunningDataGen() {
        return runningDataGen;
    }

    public static void begin(final Set<String> mods, final Path path, final java.util.Collection<Path> inputs, Collection<Path> existingPacks, final boolean serverGenerators, final boolean clientGenerators, final boolean devToolGenerators, final boolean reportsGenerator, final boolean structureValidator, final boolean flat) {
        if (mods.contains("minecraft") && mods.size() == 1) return;
        LOGGER.info("Initializing Data Gatherer for mods {}", mods);
        runningDataGen = true;
        Bootstrap.register();
        dataGeneratorConfig = new GatherDataEvent.DataGeneratorConfig(mods, path, inputs, serverGenerators, clientGenerators, devToolGenerators, reportsGenerator, structureValidator, flat);
        existingFileHelper = new ExistingFileHelper(existingPacks, structureValidator);
        ModLoader.get().gatherAndInitializeMods(ModWorkManager.syncExecutor(), ModWorkManager.parallelExecutor(), ()->{});
        ModLoader.get().runEventGenerator(mc->new GatherDataEvent(mc, dataGeneratorConfig.makeGenerator(p->dataGeneratorConfig.isFlat() ? p : p.resolve(mc.getModId()), dataGeneratorConfig.getMods().contains(mc.getModId())), dataGeneratorConfig, existingFileHelper));
        dataGeneratorConfig.runAll();
    }
}
