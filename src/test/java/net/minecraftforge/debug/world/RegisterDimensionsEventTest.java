package net.minecraftforge.debug.world;

import java.util.OptionalLong;

import com.mojang.serialization.Lifecycle;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.biome.FuzzyOffsetConstantColumnBiomeZoomer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraftforge.event.world.RegisterDimensionsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(RegisterDimensionsEventTest.MODID)
@Mod.EventBusSubscriber
public class RegisterDimensionsEventTest
{
    protected static final String MODID = "register_dimensions_event_test";

    private static final ResourceKey<LevelStem> TEST_LEVEL_STEM_KEY = ResourceKey.create(Registry.LEVEL_STEM_REGISTRY, new ResourceLocation(MODID, "test_dim"));

    @SubscribeEvent
    protected static void onRegisterDimensions(final RegisterDimensionsEvent event)
    {
        NoiseGeneratorSettings settings = NoiseGeneratorSettings.netherLikePreset(new StructureSettings(false), Blocks.DEEPSLATE.defaultBlockState(), Blocks.WATER.defaultBlockState());
        DimensionType dimensionType = DimensionType.create(OptionalLong.empty(), true, false, false, true, 1.0D, false, false, true, false, true, 0, 128, 128, FuzzyOffsetConstantColumnBiomeZoomer.INSTANCE, BlockTags.INFINIBURN_OVERWORLD.getName(), DimensionType.OVERWORLD_EFFECTS, 0.03F);

        event.getLevelStemRegistry().register(TEST_LEVEL_STEM_KEY, new LevelStem(() -> dimensionType, new NoiseBasedChunkGenerator(new FixedBiomeSource(event.getRegistryHolder().registryOrThrow(Registry.BIOME_REGISTRY).getOrThrow(Biomes.LUSH_CAVES)), event.getOverworldSeed(), () -> settings)), Lifecycle.stable());
    }
}
