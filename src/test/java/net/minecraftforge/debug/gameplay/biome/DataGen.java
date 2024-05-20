package net.minecraftforge.debug.gameplay.biome;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = BiomeTestMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGen {
    @SubscribeEvent
    public static void gatherdata(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), ModLootTableProvider.create(packOutput, lookupProvider));

        //generator.addProvider(event.includeClient(), new ModBlockStateProvider(packOutput, existingFileHelper));

        generator.addProvider(event.includeServer(), new ModWorldGenProvider(packOutput, lookupProvider));

    }

    public static class ModBlockStateProvider extends BlockStateProvider {
        public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
            super(output, BiomeTestMod.MOD_ID, exFileHelper);
        }

        @Override
        protected void registerStatesAndModels() {
            blockWithItem(BiomeTestMod.ModBlocks.SILVER_ORE);
        }

        private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
            simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
        }
    }

    public static class ModLootTableProvider {
        public static LootTableProvider create(PackOutput output, CompletableFuture<HolderLookup.Provider> pRegistries) {
            return new LootTableProvider(output, Set.of(), List.of(
                    new LootTableProvider.SubProviderEntry(ModBlockLootTables::new, LootContextParamSets.BLOCK)), pRegistries);
        }
    }

    public static class ModWorldGenProvider extends DatapackBuiltinEntriesProvider {
        public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
                .add(Registries.CONFIGURED_FEATURE, BiomeTestMod.ModConfiguredFeatures::bootstrap)
                .add(Registries.PLACED_FEATURE, BiomeTestMod.ModPlacedFeatures::bootstrap)
                .add(ForgeRegistries.Keys.BIOME_MODIFIERS, BiomeTestMod.ModBiomeModifiers::bootstrap);

        public ModWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries, BUILDER, Set.of(BiomeTestMod.MOD_ID));
        }
    }

    public static class ModBlockLootTables extends BlockLootSubProvider {
        public ModBlockLootTables() {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags());
        }

        @Override
        protected void generate() {
            this.dropSelf(BiomeTestMod.ModBlocks.SILVER_ORE.get());
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return BiomeTestMod.ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
        }
    }
}
