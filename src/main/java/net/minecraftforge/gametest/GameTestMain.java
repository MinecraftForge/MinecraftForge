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

package net.minecraftforge.gametest;

import com.mojang.serialization.Lifecycle;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.ValueConverter;
import net.minecraft.CrashReport;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.gametest.framework.GameTestBatch;
import net.minecraft.gametest.framework.GameTestRegistry;
import net.minecraft.gametest.framework.GameTestRunner;
import net.minecraft.gametest.framework.GameTestServer;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.resources.RegistryWriteOps;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.Eula;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerResources;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class GameTestMain {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) {
        System.setProperty("forge.enablegametest", "true");
        SharedConstants.tryDetectVersion();
        OptionParser optionParser = new OptionParser();
        OptionSpec<Void> helpOpt = optionParser.accepts("help").forHelp();
        OptionSpec<Void> safeModeOpt = optionParser.accepts("safeMode", "Loads level with vanilla datapack only");
        OptionSpec<String> savesDirOpt = optionParser.accepts("universe").withRequiredArg().defaultsTo(".");
        OptionSpec<String> saveNameOpt = optionParser.accepts("world").withRequiredArg();
        OptionSpec<BlockPos> spawnPosOpt = optionParser.accepts("spawnPos").withRequiredArg().withValuesConvertedBy(new BlockPosValueConverter()).defaultsTo(new BlockPos(0, 60, 0));
        OptionSpec<Void> acceptEulaOpt = optionParser.accepts("acceptEula");
        optionParser.accepts("allowUpdates").withRequiredArg().ofType(Boolean.class).defaultsTo(Boolean.TRUE); // Forge: allow mod updates to proceed
        optionParser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File(".")); //Forge: Consume this argument, we use it in the launcher, and the client side.

        try {
            OptionSet optionSet = optionParser.parse(args);
            if (optionSet.has(helpOpt)) {
                optionParser.printHelpOn(System.err);
                return;
            }
            Eula eula = new Eula(Paths.get("eula.txt"));

            if (!optionSet.has(acceptEulaOpt) && !eula.hasAgreedToEULA()) {
                LOGGER.info("You need to agree to the EULA in order to run the server. Go to eula.txt for more info.");
                return;
            }

            CrashReport.preload();
            Bootstrap.bootStrap();
            Bootstrap.validate();
            Util.startTimerHackThread();
            net.minecraftforge.fmllegacy.server.ServerModLoader.load(); // Load mods before we load almost anything else anymore. Single spot now.
            RegistryAccess.RegistryHolder registryHolder = RegistryAccess.builtin();
            DedicatedServerSettings serverSettings = new DedicatedServerSettings(Paths.get("server.properties"));
            serverSettings.forceSave();

            File savesDir = new File(optionSet.valueOf(savesDirOpt));
            String saveName = Optional.ofNullable(optionSet.valueOf(saveNameOpt)).orElse(serverSettings.getProperties().levelName);
            if (saveName == null || saveName.isEmpty() || new File(savesDir, saveName).getAbsolutePath().equals(new File(saveName).getAbsolutePath())) {
                LOGGER.error("Invalid world directory specified, must not be null, empty or the same directory as your universe! {}", saveName);
                return;
            }
            LevelStorageSource storageSource = LevelStorageSource.createDefault(savesDir.toPath());
            LevelStorageSource.LevelStorageAccess storageAccess = storageSource.createAccess(saveName);
            MinecraftServer.convertFromRegionFormatIfNeeded(storageAccess);
            LevelSummary levelSummary = storageAccess.getSummary();
            if (levelSummary != null && levelSummary.isIncompatibleWorldHeight()) {
                LOGGER.info("Loading of worlds with extended height is disabled.");
                return;
            }

            DataPackConfig storedDatapacks = storageAccess.getDataPacks();
            boolean flag = optionSet.has(safeModeOpt);
            if (flag) {
                LOGGER.warn("Safe mode active, only vanilla datapack will be loaded");
            }

            PackRepository packRepository = new PackRepository(PackType.SERVER_DATA, new ServerPacksSource(),
                    new FolderRepositorySource(storageAccess.getLevelPath(LevelResource.DATAPACK_DIR).toFile(), PackSource.WORLD));
            DataPackConfig datapacks = MinecraftServer.configurePackRepository(packRepository, storedDatapacks == null ? DataPackConfig.DEFAULT : storedDatapacks, flag);
            CompletableFuture<ServerResources> resourcesFuture = ServerResources.loadResources(packRepository.openAllSelected(), registryHolder, Commands.CommandSelection.DEDICATED,
                    serverSettings.getProperties().functionPermissionLevel, Util.backgroundExecutor(), Runnable::run);

            ServerResources resources;
            try {
                resources = resourcesFuture.get();
            } catch (Exception e) {
                LOGGER.warn("Failed to load datapacks, can't proceed with server load. You can either fix your datapacks or reset to vanilla with --safeMode", e);
                packRepository.close();
                return;
            }

            resources.updateGlobals();
            RegistryReadOps<Tag> registryOps = RegistryReadOps.createAndLoad(NbtOps.INSTANCE, resources.getResourceManager(), registryHolder);
            serverSettings.getProperties().getWorldGenSettings(registryHolder);
            WorldData worldData = storageAccess.getDataTag(registryOps, datapacks);
            if (worldData == null) {
                DedicatedServerProperties dedicatedserverproperties = serverSettings.getProperties();
                LevelSettings levelsettings = new LevelSettings(dedicatedserverproperties.levelName, dedicatedserverproperties.gamemode, dedicatedserverproperties.hardcore,
                        dedicatedserverproperties.difficulty, false, new GameRules(), datapacks);
                WorldGenSettings worldgensettings = dedicatedserverproperties.getWorldGenSettings(registryHolder);

                // Forge: Deserialize the DimensionGeneratorSettings to ensure modded dims are loaded on first server load (see SimpleRegistryCodec#decode). Vanilla behaviour only loads from the
                // server.properties and deserializes only after the 2nd server load.
                worldgensettings = WorldGenSettings.CODEC
                        .encodeStart(RegistryWriteOps.create(NbtOps.INSTANCE, registryHolder), worldgensettings)
                        .flatMap(nbt -> WorldGenSettings.CODEC.parse(registryOps, nbt))
                        .getOrThrow(false, errorMsg -> {});
                worldData = new PrimaryLevelData(levelsettings, worldgensettings, Lifecycle.stable());
            }

            storageAccess.saveDataTag(registryHolder, worldData);
            Collection<GameTestBatch> testBatches = GameTestRunner.groupTestsIntoBatches(GameTestRegistry.getAllTestFunctions());
            BlockPos spawnPos = optionSet.valueOf(spawnPosOpt);
            var server = MinecraftServer.spin(serverThread -> new GameTestServer(serverThread, storageAccess, packRepository, resources, testBatches, spawnPos, registryHolder));
            Thread thread = new Thread("Server Shutdown Thread") {
                @Override
                public void run() {
                    // We don't need to halt here as GameTestServer always calls System#exit on both crash and normal exit
                    LogManager.shutdown(); // we're manually managing the logging shutdown on the server. Make sure we do it here at the end.
                }
            };
            thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
            Runtime.getRuntime().addShutdownHook(thread);
        } catch (Exception e) {
            LOGGER.fatal("Failed to start the minecraft server", e);
        }
    }

    private static final class BlockPosValueConverter implements ValueConverter<BlockPos> {
        @Override
        public BlockPos convert(String value) {
            String[] split = value.split(",");
            return new BlockPos(Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
        }

        @Override
        public Class<BlockPos> valueType() {return BlockPos.class;}

        @Override
        public String valuePattern() {return null;}
    }
}
