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

package net.minecraftforge.debug.world;

import java.util.Objects;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.OverworldBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraftforge.common.DynamicDimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(DynamicDimensionManagerTest.MODID)
public class DynamicDimensionManagerTest
{
    public static final String MODID = "dynamic_dimension_manager_test";
    public static final String NEW_DIMENSION_NAME = "new_dimension_name";
    public static final String DIMENSION = "dimension";
    
    public DynamicDimensionManagerTest()
    {
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        final IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        
        forgeBus.addListener(this::onRegisterCommands);
    }
    
    private void onRegisterCommands(final RegisterCommandsEvent event)
    {
        final CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal(MODID)
            .requires(DynamicDimensionManagerTest::isCommandSourceServerAdmin)
            .then(Commands.literal("add")
                .then(Commands.argument(NEW_DIMENSION_NAME, ResourceLocationArgument.id())
                    .suggests((context,builder) -> SharedSuggestionProvider.suggest(new String[]{"<new_dimension_name>"}, builder))
                    .executes(DynamicDimensionManagerTest::addNewDimension)))
            .then(Commands.literal("remove")
                .then(Commands.argument(DIMENSION, DimensionArgument.dimension())
                    .executes(DynamicDimensionManagerTest::removeDimension)))
            );
    }
    
    private static boolean isCommandSourceServerAdmin(final CommandSourceStack source)
    {
        return source.hasPermission(4);
    }
    
    private static int addNewDimension(final CommandContext<CommandSourceStack> context)
    {
        final CommandSourceStack source = context.getSource();
        final ResourceLocation rawID = ResourceLocationArgument.getId(context, NEW_DIMENSION_NAME);
        // convert minecraft namespace to modid
        final ResourceLocation worldID = rawID.getNamespace() == "minecraft" ? new ResourceLocation(MODID, rawID.getPath()) : rawID;
        final ResourceKey<Level> worldKey = ResourceKey.create(Registry.DIMENSION_REGISTRY, worldID);
        final MinecraftServer server = source.getServer();
        final ServerLevel existingWorld = server.getLevel(worldKey);
        if (existingWorld != null)
        {
            source.sendFailure(new TextComponent(String.format("World with id %s already exists", worldID)));
            return 0;
        }
        DynamicDimensionManager.getOrCreateLevel(server, worldKey, DynamicDimensionManagerTest::makeDimension);
        source.sendSuccess(new TextComponent(String.format("Created world with id %s", worldID)), true);
        return 1;
    }
    
    private static int removeDimension(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException
    {
        final CommandSourceStack source = context.getSource();
        final MinecraftServer server = source.getServer();
        final ServerLevel worldToRemove = DimensionArgument.getDimension(context, DIMENSION);
        final ResourceKey<Level> key = worldToRemove.dimension();
        source.sendSuccess(new TextComponent(String.format("Unregistering dimension %s", key.location())), true);
        DynamicDimensionManager.markDimensionForUnregistration(server, key);
        return 1;
    }
    
    private static LevelStem makeDimension(final MinecraftServer server, final ResourceKey<LevelStem> key)
    {
        final long seed = Objects.hash(server.getLevel(Level.OVERWORLD).getSeed(), key.location());
        final RegistryAccess registries = server.registryAccess();
        final DimensionType overworldDimensionType = registries.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY).get(DimensionType.OVERWORLD_LOCATION);
        final BiomeSource biomeProvider = new OverworldBiomeSource(seed, false, false, registries.registryOrThrow(Registry.BIOME_REGISTRY));
        final NoiseGeneratorSettings noiseSettings = registries.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY).get(NoiseGeneratorSettings.OVERWORLD);
        final ChunkGenerator chunkGenerator = new NoiseBasedChunkGenerator(biomeProvider, seed, () -> noiseSettings);
        return new LevelStem(() -> overworldDimensionType, chunkGenerator);
    }
}
