package net.minecraftforge.debug.world;

import java.util.Objects;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraft.world.server.ServerWorld;
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
        final CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal(MODID)
            .requires(DynamicDimensionManagerTest::isCommandSourceServerAdmin)
            .then(Commands.literal("add")
                .then(Commands.argument(NEW_DIMENSION_NAME, ResourceLocationArgument.id())
                    .suggests((context,builder) -> ISuggestionProvider.suggest(new String[]{"<new_dimension_name>"}, builder))
                    .executes(DynamicDimensionManagerTest::addNewDimension)))
            .then(Commands.literal("remove")
                .then(Commands.argument(DIMENSION, DimensionArgument.dimension())
                    .executes(DynamicDimensionManagerTest::removeDimension)))
            );
    }
    
    private static boolean isCommandSourceServerAdmin(final CommandSource source)
    {
        return source.hasPermission(4);
    }
    
    private static int addNewDimension(final CommandContext<CommandSource> context)
    {
        final CommandSource source = context.getSource();
        final ResourceLocation rawID = ResourceLocationArgument.getId(context, NEW_DIMENSION_NAME);
        // convert minecraft namespace to modid
        final ResourceLocation worldID = rawID.getNamespace() == "minecraft" ? new ResourceLocation(MODID, rawID.getPath()) : rawID;
        final RegistryKey<World> worldKey = RegistryKey.create(Registry.DIMENSION_REGISTRY, worldID);
        final MinecraftServer server = source.getServer();
        final ServerWorld existingWorld = server.getLevel(worldKey);
        if (existingWorld != null)
        {
            source.sendFailure(new StringTextComponent(String.format("World with id %s already exists", worldID)));
            return 0;
        }
        DynamicDimensionManager.getOrCreateWorld(server, worldKey, DynamicDimensionManagerTest::makeDimension);
        source.sendSuccess(new StringTextComponent(String.format("Created world with id %s", worldID)), true);
        return 1;
    }
    
    private static int removeDimension(final CommandContext<CommandSource> context) throws CommandSyntaxException
    {
        final CommandSource source = context.getSource();
        final MinecraftServer server = source.getServer();
        final ServerWorld worldToRemove = DimensionArgument.getDimension(context, DIMENSION);
        final RegistryKey<World> key = worldToRemove.dimension();
        source.sendSuccess(new StringTextComponent(String.format("Unregistering dimension %s", key.location())), true);
        DynamicDimensionManager.markDimensionForUnregistration(server, key);
        return 1;
    }
    
    private static Dimension makeDimension(final MinecraftServer server, final RegistryKey<Dimension> key)
    {
        final long seed = Objects.hash(server.getLevel(World.OVERWORLD).getSeed(), key.location());
        final DynamicRegistries registries = server.registryAccess();
        final DimensionType overworldDimensionType = registries.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY).get(DimensionType.OVERWORLD_LOCATION);
        final BiomeProvider biomeProvider = new OverworldBiomeProvider(seed, false, false, registries.registryOrThrow(Registry.BIOME_REGISTRY));
        final DimensionSettings noiseSettings = registries.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY).get(DimensionSettings.OVERWORLD);
        final ChunkGenerator chunkGenerator = new NoiseChunkGenerator(biomeProvider, seed, () -> noiseSettings);
        return new Dimension(() -> overworldDimensionType, chunkGenerator);
    }
}
