package net.minecraftforge.debug.world;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.LazyValue;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.*;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeRegistry;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.IOException;

@Mod(ChunkGeneratorUpgradeWorldTest.MODID)
public class ChunkGeneratorUpgradeWorldTest
{
    public final static String MODID = "chunk_generator_upgrade_world_test";
    private static final boolean ENABLED = true;
    public static final Codec<ChunkGenerator> CODEC = Codec.unit(TestChunkGenerator::new);

    public ChunkGeneratorUpgradeWorldTest()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addGenericListener(Biome.class, this::registerChunkGenerator);
        bus.addListener(this::generateTestDim);
    }

    private void registerChunkGenerator(RegistryEvent.Register<Biome> event)
    {
        Registry.register(Registry.CHUNK_GENERATOR_CODEC, new ResourceLocation(MODID, "chunk_generator"), CODEC);
    }

    private void generateTestDim(GatherDataEvent event)
    {
        event.getGenerator().addProvider(new TestDimProvider(event.getGenerator()));
    }

    public static class TestChunkGenerator extends ChunkGenerator
    {
        public TestChunkGenerator()
        {
            super(new SingleBiomeProvider(BiomeRegistry.THE_VOID), new DimensionStructuresSettings(false));
        }

        @Override
        protected Codec<? extends ChunkGenerator> func_230347_a_()
        {
            return CODEC;
        }

        @Override
        public ChunkGenerator func_230349_a_(long p_230349_1_)
        {
            return this;
        }

        @Override
        public void generateSurface(WorldGenRegion p_225551_1_, IChunk p_225551_2_) {}

        @Override
        public void func_230352_b_(IWorld p_230352_1_, StructureManager p_230352_2_, IChunk p_230352_3_) {}

        @Override
        public int getHeight(int x, int z, Heightmap.Type heightmapType)
        {
            return 1;
        }

        @Override
        public IBlockReader func_230348_a_(int p_230348_1_, int p_230348_2_)
        {
            return new Blockreader(new BlockState[0]);
        }
    }

    public static class TestDimProvider implements IDataProvider
    {
        private final DataGenerator generator;

        public TestDimProvider(DataGenerator generator)
        {
            this.generator = generator;
        }

        @Override
        public void act(DirectoryCache cache) throws IOException
        {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            DynamicRegistries.Impl regs = DynamicRegistries.func_239770_b_();
            DynamicOps<JsonElement> ops = WorldGenSettingsExport.create(JsonOps.INSTANCE, regs);

            DimensionType overworld = regs.func_230520_a_().getOrDefault(DimensionType.OVERWORLD_ID);
            Dimension dim = new Dimension(() -> overworld, new TestChunkGenerator());

            JsonElement json = Dimension.CODEC.encodeStart(ops, dim).getOrThrow(false, s->{});
            IDataProvider.save(gson, cache, json, generator.getOutputFolder().resolve("data/" + MODID + "/dimension/test_dim.json"));
        }

        @Override
        public String getName()
        {
            return "Test Dimension Provider";
        }
    }
}
