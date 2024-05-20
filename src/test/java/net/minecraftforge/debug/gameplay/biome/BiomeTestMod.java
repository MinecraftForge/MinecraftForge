package net.minecraftforge.debug.gameplay.biome;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.test.BaseTestMod;

import java.util.List;
import java.util.function.Supplier;


@Mod(BiomeTestMod.MOD_ID)
public class BiomeTestMod extends BaseTestMod {
    public static final String MOD_ID = "biome_test_mod";


    public BiomeTestMod() {
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.register(modBus);
        ModItems.register(modBus);
    }

    public class ModPlacedFeatures {

        public static final ResourceKey<PlacedFeature> OVERWORLD_SILVER_ORE_PLACED_KEY = registerKey("overworld_silver_ore_placed");

        public static void bootstrap(BootstrapContext<PlacedFeature> context) {
            HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

            register(context, OVERWORLD_SILVER_ORE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.OVERWORLD_SILVER_ORE_KEY),
                    ModOrePlacement.commonOrePlacement(4, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(32))));
        }

        private static ResourceKey<PlacedFeature> registerKey(String name) {
            return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(BiomeTestMod.MOD_ID, name));
        }

        private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration, List<PlacementModifier> modifiers) {
            context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
        }
    }

    public static class ModOrePlacement {
        public static List<PlacementModifier> orePlacement(PlacementModifier m1, PlacementModifier m2) {
            return List.of(m1, InSquarePlacement.spread(), m2, BiomeFilter.biome());
        }

        public static List<PlacementModifier> commonOrePlacement(int count, PlacementModifier heightRange) {
            return orePlacement(CountPlacement.of(count), heightRange);
        }
    }

    public static class ModConfiguredFeatures {

        public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_SILVER_ORE_KEY = registerKey("overworld_silver_ore");

        public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
            RuleTest stoneReplaceables = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);

            List<OreConfiguration.TargetBlockState> overworldSilverOres = List.of(OreConfiguration.target(stoneReplaceables,
                    ModBlocks.SILVER_ORE.get().defaultBlockState()));

            register(context, OVERWORLD_SILVER_ORE_KEY, Feature.ORE, new OreConfiguration(overworldSilverOres, 9));

        }

        public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
            return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(BiomeTestMod.MOD_ID, name));
        }

        private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
            context.register(key, new ConfiguredFeature<>(feature, configuration));
        }
    }

    public static class ModBiomeModifiers {

        public static final ResourceKey<BiomeModifier> ADD_OVERWORLD_SILVER_ORE = registerKey("add_overworld_silver_ore");

        public static void bootstrap(BootstrapContext<BiomeModifier> context) {
            var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
            var biomes = context.lookup(Registries.BIOME);

            context.register(ADD_OVERWORLD_SILVER_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                    biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                    HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.OVERWORLD_SILVER_ORE_PLACED_KEY)),
                    GenerationStep.Decoration.UNDERGROUND_ORES));
        }

        private static ResourceKey<BiomeModifier> registerKey(String name) {
            return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(BiomeTestMod.MOD_ID, name));
        }
    }

    public static class ModBlocks {
        public static final DeferredRegister<Block> BLOCKS =
                DeferredRegister.create(ForgeRegistries.BLOCKS, BiomeTestMod.MOD_ID);
        public static final RegistryObject<Block> SILVER_ORE = registerBlock("silver_ore",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GOLD_ORE)));

        private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
            RegistryObject<T> toReturn = BLOCKS.register(name, block);
            registerBlockItem(name, toReturn);
            return toReturn;
        }

        private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
            return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        }

        public static void register(IEventBus eventBus) {
            BLOCKS.register(eventBus);
        }
    }

    public static class ModItems {
        public static final DeferredRegister<Item> ITEMS =
                DeferredRegister.create(ForgeRegistries.ITEMS, BiomeTestMod.MOD_ID);

        public static void register(IEventBus eventBus) {
            ITEMS.register(eventBus);
        }
    }
}
