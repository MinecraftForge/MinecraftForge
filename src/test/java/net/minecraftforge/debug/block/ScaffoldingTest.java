package net.minecraftforge.debug.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.Tags.IOptionalNamedTag;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * This test mod allows a custom scaffolding to move down
 * while sneaking through a tag and method.
 */
@Mod(ScaffoldingTest.MODID)
public class ScaffoldingTest
{
    static final String MODID = "scaffolding_test";
    static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    static final IOptionalNamedTag<Block> SCAFFOLDING = BlockTags.createOptional(new ResourceLocation("forge", "scaffolding"));

    static final RegistryObject<Block> SCAFFOLDING_TAG_TEST = BLOCKS.register("scaffolding_tag_test", () -> new ScaffoldingTagTestBlock(AbstractBlock.Properties.create(Material.MISCELLANEOUS, MaterialColor.SAND).doesNotBlockMovement().sound(SoundType.SCAFFOLDING).variableOpacity()));
    static final RegistryObject<Block> SCAFFOLDING_METHOD_TEST = BLOCKS.register("scaffolding_method_test", () -> new ScaffoldingMethodTestBlock(AbstractBlock.Properties.create(Material.MISCELLANEOUS, MaterialColor.SAND).doesNotBlockMovement().sound(SoundType.SCAFFOLDING).variableOpacity()));

    public ScaffoldingTest()
    {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modBus);
        modBus.addListener(this::gatherData);
    }

    private void gatherData(final GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();
        if(event.includeClient()) gen.addProvider(new ScaffoldingBlockState(gen, MODID, event.getExistingFileHelper()));
        if(event.includeServer()) gen.addProvider(new ScaffoldingTagsProvider(gen));
    }

    static class ScaffoldingTagsProvider extends BlockTagsProvider
    {
        public ScaffoldingTagsProvider(DataGenerator generatorIn)
        {
            super(generatorIn);
        }

        @Override
        protected void registerTags()
        {
            this.func_240522_a_(SCAFFOLDING).func_240532_a_(SCAFFOLDING_TAG_TEST.get());
        }
    }

    static class ScaffoldingBlockState extends BlockStateProvider
    {
        public ScaffoldingBlockState(DataGenerator gen, String modid, ExistingFileHelper exFileHelper)
        {
            super(gen, modid, exFileHelper);
        }

        @Override
        protected void registerStatesAndModels()
        {
            this.getVariantBuilder(SCAFFOLDING_TAG_TEST.get()).forAllStatesExcept((state) -> ConfiguredModel.builder().modelFile(state.get(ScaffoldingBlock.field_220120_c) ? new ModelFile.ExistingModelFile(new ResourceLocation("block/scaffolding_unstable"), this.models().existingFileHelper) : new ModelFile.ExistingModelFile(new ResourceLocation("block/scaffolding_stable"), this.models().existingFileHelper)).build(), ScaffoldingBlock.field_220118_a, ScaffoldingBlock.WATERLOGGED);
            this.getVariantBuilder(SCAFFOLDING_METHOD_TEST.get()).forAllStatesExcept((state) -> ConfiguredModel.builder().modelFile(state.get(ScaffoldingBlock.field_220120_c) ? new ModelFile.ExistingModelFile(new ResourceLocation("block/scaffolding_unstable"), this.models().existingFileHelper) : new ModelFile.ExistingModelFile(new ResourceLocation("block/scaffolding_stable"), this.models().existingFileHelper)).build(), ScaffoldingBlock.field_220118_a, ScaffoldingBlock.WATERLOGGED);
        }
    }

    static class ScaffoldingTagTestBlock extends ScaffoldingBlock
    {

        public ScaffoldingTagTestBlock(Properties properties)
        {
            super(properties);
        }

        @Override
        public boolean isScaffolding(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity)
        {
            return state.getBlock().isIn(SCAFFOLDING);
        }
    }

    static class ScaffoldingMethodTestBlock extends ScaffoldingBlock
    {

        public ScaffoldingMethodTestBlock(Properties properties)
        {
            super(properties);
        }

        @Override
        public boolean isScaffolding(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity)
        {
            return true;
        }
    }
}
