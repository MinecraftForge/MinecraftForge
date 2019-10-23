/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.debug;

import static net.minecraftforge.debug.DataGeneratorTest.MODID;

import java.util.function.Consumer;

import com.google.common.collect.ObjectArrays;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.LogBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.crafting.ConditionalAdvancement;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod(MODID)
@Mod.EventBusSubscriber(bus = Bus.MOD)
public class DataGeneratorTest
{
    static final String MODID = "data_gen_test";

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();

        if (event.includeClient())
        {
            gen.addProvider(new ItemModels(gen, event.getExistingFileHelper()));
            gen.addProvider(new BlockStates(gen, event.getExistingFileHelper()));
        }
        if (event.includeServer())
        {
            gen.addProvider(new Recipes(gen));
        }
    }

    public static class Recipes extends RecipeProvider implements IConditionBuilder
    {
        public Recipes(DataGenerator gen)
        {
            super(gen);
        }

        protected void registerRecipes(Consumer<IFinishedRecipe> consumer)
        {
            ResourceLocation ID = new ResourceLocation("data_gen_test", "conditional");

            ConditionalRecipe.builder()
            .addCondition(
                and(
                    not(modLoaded("minecraft")),
                    itemExists("minecraft", "dirt"),
                    FALSE()
                )
            )
            .addRecipe(
                ShapedRecipeBuilder.shapedRecipe(Blocks.DIAMOND_BLOCK, 64)
                .patternLine("XXX")
                .patternLine("XXX")
                .patternLine("XXX")
                .key('X', Blocks.DIRT)
                .setGroup("")
                .addCriterion("has_dirt", hasItem(Blocks.DIRT)) //Doesn't actually print... TODO: nested/conditional advancements?
                ::build
            )
            .setAdvancement(ID,
                ConditionalAdvancement.builder()
                .addCondition(
                    and(
                        not(modLoaded("minecraft")),
                        itemExists("minecraft", "dirt"),
                        FALSE()
                    )
                )
                .addAdvancement(
                    Advancement.Builder.builder()
                    .withParentId(new ResourceLocation("minecraft", "root"))
                    .withDisplay(Blocks.DIAMOND_BLOCK,
                        new StringTextComponent("Dirt2Diamonds"),
                        new StringTextComponent("The BEST crafting recipe in the game!"),
                        null, FrameType.TASK, false, false, false
                    )
                    .withRewards(AdvancementRewards.Builder.recipe(ID))
                    .withCriterion("has_dirt", hasItem(Blocks.DIRT))
                )
            )
            .build(consumer, ID);
        }
    }

    public static class ItemModels extends ModelProvider<ItemModelBuilder>
    {
        public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper)
        {
            super(generator, MODID, ITEM_FOLDER, ItemModelBuilder::new, existingFileHelper);
        }

        @Override
        protected void registerModels()
        {
            getBuilder("test_generated_model")
                    .parent(new UncheckedModelFile("item/generated"))
                    .texture("layer0", mcLoc("block/stone"));

            getBuilder("test_block_model")
                    .parent(getExistingFile(mcLoc("block/block")))
                    .texture("all", mcLoc("block/dirt"))
                    .texture("top", mcLoc("block/stone"))
                    .element()
                        .cube("#all")
                        .face(Direction.UP)
                            .texture("#top")
                            .tintindex(0)
                            .end()
                        .end();
        }

        @Override
        public String getName()
        {
            return "Forge Test Item Models";
        }
    }

   public static class BlockStates extends BlockStateProvider
   {

       public BlockStates(DataGenerator gen, ExistingFileHelper exFileHelper)
       {
           super(gen, MODID, exFileHelper);
       }

       @Override
       protected void registerStatesAndModels()
       {
           // Unnecessarily complicated example to showcase how manual building works
           ModelFile birchFenceGate = fenceGate("birch_fence_gate", mcLoc("block/birch_planks"));
           ModelFile birchFenceGateOpen = fenceGateOpen("birch_fence_gate_open", mcLoc("block/birch_planks"));
           ModelFile birchFenceGateWall = fenceGateWall("birch_fence_gate_wall", mcLoc("block/birch_planks"));
           ModelFile birchFenceGateWallOpen = fenceGateWallOpen("birch_fence_gate_wall_open", mcLoc("block/birch_planks"));
           ModelFile invisbleModel = new UncheckedModelFile(new ResourceLocation("builtin/generated"));
           VariantBlockStateBuilder builder = getVariantBuilder(Blocks.BIRCH_FENCE_GATE);
           for (Direction dir : FenceGateBlock.HORIZONTAL_FACING.getAllowedValues()) {
               int angle = (int) dir.getHorizontalAngle();
               builder
                       .partialState()
                            .with(FenceGateBlock.HORIZONTAL_FACING, dir)
                            .with(FenceGateBlock.IN_WALL, false)
                            .with(FenceGateBlock.OPEN, false)
                            .modelForState()
                                .modelFile(invisbleModel)
                            .nextModel()
                                .modelFile(birchFenceGate)
                                .rotationY(angle)
                                .uvLock(true)
                                .weight(100)
                            .addModel()
                       .partialState()
                            .with(FenceGateBlock.HORIZONTAL_FACING, dir)
                            .with(FenceGateBlock.IN_WALL, false)
                            .with(FenceGateBlock.OPEN, true)
                            .modelForState()
                                .modelFile(birchFenceGateOpen)
                                .rotationY(angle)
                                .uvLock(true)
                            .addModel()
                       .partialState()
                            .with(FenceGateBlock.HORIZONTAL_FACING, dir)
                            .with(FenceGateBlock.IN_WALL, true)
                            .with(FenceGateBlock.OPEN, false)
                            .modelForState()
                                .modelFile(birchFenceGateWall)
                                .rotationY(angle)
                                .uvLock(true)
                            .addModel()
                       .partialState()
                            .with(FenceGateBlock.HORIZONTAL_FACING, dir)
                            .with(FenceGateBlock.IN_WALL, true)
                            .with(FenceGateBlock.OPEN, true)
                            .modelForState()
                                .modelFile(birchFenceGateWallOpen)
                                .rotationY(angle)
                                .uvLock(true)
                            .addModel();
           }

           // Realistic examples using helpers
           simpleBlock(Blocks.STONE, model -> ObjectArrays.concat(
                   ConfiguredModel.allYRotations(model, 0, false),
                   ConfiguredModel.allYRotations(model, 180, false),
                   ConfiguredModel.class));

           ModelFile furnace = orientable("furnace", mcLoc("block/furnace_side"), mcLoc("block/furnace_front"), mcLoc("block/furnace_top"));
           ModelFile furnaceLit = orientable("furnace_on", mcLoc("block/furnace_side"), mcLoc("block/furnace_front_on"), mcLoc("block/furnace_top"));

           getVariantBuilder(Blocks.FURNACE)
               .forAllStates(state -> ConfiguredModel.builder()
                       .modelFile(state.get(FurnaceBlock.LIT) ? furnaceLit : furnace)
                       .rotationY((int) state.get(FurnaceBlock.FACING).getOpposite().getHorizontalAngle())
                       .build()
               );

           ModelFile barrel = cubeBottomTop("barrel", mcLoc("block/barrel_side"), mcLoc("block/barrel_bottom"), mcLoc("block/barrel_top"));
           ModelFile barrelOpen = cubeBottomTop("barrel_open", mcLoc("block/barrel_side"), mcLoc("block/barrel_bottom"), mcLoc("block/barrel_top_open"));
           directionalBlock(Blocks.BARREL, state -> state.get(BarrelBlock.PROPERTY_OPEN) ? barrelOpen : barrel); // Testing custom state interpreter

           logBlock((LogBlock) Blocks.ACACIA_LOG);

           stairsBlock((StairsBlock) Blocks.ACACIA_STAIRS, "acacia", mcLoc("block/acacia_planks"));
           slabBlock((SlabBlock) Blocks.ACACIA_SLAB, Blocks.ACACIA_PLANKS.getRegistryName(), mcLoc("block/acacia_planks"));

           fenceBlock((FenceBlock) Blocks.ACACIA_FENCE, "acacia", mcLoc("block/acacia_planks"));
           fenceGateBlock((FenceGateBlock) Blocks.ACACIA_FENCE_GATE, "acacia", mcLoc("block/acacia_planks"));

           wallBlock((WallBlock) Blocks.COBBLESTONE_WALL, "cobblestone", mcLoc("block/cobblestone"));

           paneBlock((PaneBlock) Blocks.GLASS_PANE, "glass", mcLoc("block/glass"), mcLoc("block/glass_pane_top"));

           doorBlock((DoorBlock) Blocks.ACACIA_DOOR, "acacia", mcLoc("block/acacia_door_bottom"), mcLoc("block/acacia_door_top"));
           trapdoorBlock((TrapDoorBlock) Blocks.ACACIA_TRAPDOOR, "acacia", mcLoc("block/acacia_trapdoor"), true);
           trapdoorBlock((TrapDoorBlock) Blocks.OAK_TRAPDOOR, "oak", mcLoc("block/oak_trapdoor"), false); // Test a non-orientable trapdoor

           simpleBlock(Blocks.TORCH, torch("torch", mcLoc("block/torch")));
           horizontalBlock(Blocks.WALL_TORCH, torchWall("wall_torch", mcLoc("block/torch")), 90);
       }

       @Override
       public String getName()
       {
           return "Forge Test Blockstates";
       }
    }
}
