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

package net.minecraftforge.debug.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.Tags.IOptionalNamedTag;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

/**
 * This test mod allows a custom scaffolding to move down
 * while sneaking through a method.
 */
@Mod(ScaffoldingTest.MODID)
public class ScaffoldingTest
{
    static final String MODID = "scaffolding_test";
    static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    static final IOptionalNamedTag<Block> SCAFFOLDING = BlockTags.createOptional(new ResourceLocation("forge", "scaffolding"));

    static final RegistryObject<Block> SCAFFOLDING_METHOD_TEST = BLOCKS.register("scaffolding_method_test", () -> new ScaffoldingMethodTestBlock(Properties.of(Material.DECORATION, MaterialColor.SAND).noCollission().sound(SoundType.SCAFFOLDING).dynamicShape()));

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
            this.getVariantBuilder(SCAFFOLDING_METHOD_TEST.get()).forAllStatesExcept((state) -> ConfiguredModel.builder().modelFile(state.getValue(ScaffoldingBlock.BOTTOM) ? new ModelFile.ExistingModelFile(new ResourceLocation("block/scaffolding_unstable"), this.models().existingFileHelper) : new ModelFile.ExistingModelFile(new ResourceLocation("block/scaffolding_stable"), this.models().existingFileHelper)).build(), ScaffoldingBlock.DISTANCE, ScaffoldingBlock.WATERLOGGED);
        }
    }

    static class ScaffoldingMethodTestBlock extends ScaffoldingBlock
    {

        public ScaffoldingMethodTestBlock(Properties properties)
        {
            super(properties);
        }

        @Override
        public boolean isScaffolding(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity)
        {
            return true;
        }
    }
}
