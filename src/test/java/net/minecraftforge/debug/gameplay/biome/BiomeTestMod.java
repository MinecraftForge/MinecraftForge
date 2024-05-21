package net.minecraftforge.debug.gameplay.biome;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.test.BaseTestMod;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;


@Mod(BiomeTestMod.MOD_ID)
public class BiomeTestMod extends BaseTestMod {
    public static final String MOD_ID = "biome_test_mod";

    private static final ResourceKey<PlacedFeature> OVERWORLD_NETHERITE_BLOCK_ORE_PLACED_KEY = ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(BiomeTestMod.MOD_ID, "overworld_netherite_block_ore_placed"));
    private static final ResourceKey<BiomeModifier> ADD_OVERWORLD_NETHERITE_BLOCK_ORE = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(BiomeTestMod.MOD_ID, "add_overworld_netherite_block_ore"));
    private static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_NETHERITE_BLOCK_ORE = ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(BiomeTestMod.MOD_ID, "overworld_netherite_block_ore"));

    public BiomeTestMod() {
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(BiomeTestMod::onDataGen);
    }

    private static void onDataGen(GatherDataEvent event) {
        event.getGenerator().addProvider(event.includeServer(), new ModWorldGenProvider(event.getGenerator().getPackOutput(), event.getLookupProvider()));
    }

    private static class ModWorldGenProvider extends DatapackBuiltinEntriesProvider {
        public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
                .add(Registries.CONFIGURED_FEATURE, BiomeTestMod::bootstrapConfiguredFeature)
                .add(Registries.PLACED_FEATURE, BiomeTestMod::bootstrapPlacedFeature)
                .add(ForgeRegistries.Keys.BIOME_MODIFIERS, BiomeTestMod::bootstrapBiomeModifier);

        public ModWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries, BUILDER, Set.of(BiomeTestMod.MOD_ID));
        }
    }

    private static List<PlacementModifier> orePlacement(PlacementModifier m1, PlacementModifier m2) {
        return List.of(m1, InSquarePlacement.spread(), m2, BiomeFilter.biome());
    }

    private static List<PlacementModifier> commonOrePlacement(int count, PlacementModifier heightRange) {
        return orePlacement(CountPlacement.of(count), heightRange);
    }

    private static void bootstrapConfiguredFeature(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        RuleTest stoneReplaceables = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        List<OreConfiguration.TargetBlockState> overworldNetheriteBlockOre = List.of(OreConfiguration.target(stoneReplaceables, Blocks.NETHERITE_BLOCK.defaultBlockState()));
        context.register(OVERWORLD_NETHERITE_BLOCK_ORE, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(overworldNetheriteBlockOre, 23)));
    }

    private static void bootstrapBiomeModifier(BootstrapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        context.register(ADD_OVERWORLD_NETHERITE_BLOCK_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(OVERWORLD_NETHERITE_BLOCK_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES)
        );
    }

    private static void bootstrapPlacedFeature(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        context.register(OVERWORLD_NETHERITE_BLOCK_ORE_PLACED_KEY, new PlacedFeature(
                configuredFeatures.getOrThrow(OVERWORLD_NETHERITE_BLOCK_ORE),
                List.copyOf(commonOrePlacement(18, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(32))))
        ));
    }
}
