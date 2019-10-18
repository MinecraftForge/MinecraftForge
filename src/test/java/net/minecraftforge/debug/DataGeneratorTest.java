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

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.model.generators.BlockstateProvider;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.client.model.generators.VariantBlockstate;
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
                    .texture("layer0", new ResourceLocation("block/stone"));

            getBuilder("test_block_model")
                    .parent(getExistingFile("block/block"))
                    .texture("all", new ResourceLocation("block/dirt"))
                    .texture("top", new ResourceLocation("block/stone"))
                    .element()
                        .cube("#all")
                        .face(Direction.UP)
                            .texture("#top")
                            .end()
                        .end();
        }

        @Override
        public String getName()
        {
            return "Forge Test Item Models";
        }
    }

   public static class BlockStates extends BlockstateProvider
   {

       public BlockStates(DataGenerator gen, ExistingFileHelper exFileHelper)
       {
           super(gen, MODID, exFileHelper);
       }

       @Override
       protected void registerStatesAndModels()
       {
           ModelFile acaciaFenceGate = getBuilder("acacia_fence_gate")
                   .parent(getExistingFile("block/template_fence_gate"))
                   .texture("texture", new ResourceLocation("block/acacia_planks"));
           ModelFile acaciaFenceGateOpen = getBuilder("acacia_fence_gate_open")
                   .parent(getExistingFile("block/template_fence_gate_open"))
                   .texture("texture", new ResourceLocation("block/acacia_planks"));
           ModelFile acaciaFenceGateWall = getBuilder("acacia_fence_gate_wall")
                   .parent(getExistingFile("block/template_fence_gate_wall"))
                   .texture("texture", new ResourceLocation("block/acacia_planks"));
           ModelFile acaciaFenceGateWallOpen = getBuilder("acacia_fence_gate_wall_open")
                   .parent(getExistingFile("block/template_fence_gate_wall_open"))
                   .texture("texture", new ResourceLocation("block/acacia_planks"));
           ModelFile invisbleModel = new UncheckedModelFile(new ResourceLocation("builtin/generated"));
           VariantBlockstate builder = getVariantBuilder(Blocks.ACACIA_FENCE_GATE);
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
                                .modelFile(acaciaFenceGate)
                                .rotationY(angle)
                                .uvLock(true)
                                .weight(100)
                            .addModel()
                       .partialState()
                            .with(FenceGateBlock.HORIZONTAL_FACING, dir)
                            .with(FenceGateBlock.IN_WALL, false)
                            .with(FenceGateBlock.OPEN, true)
                            .modelForState()
                                .modelFile(acaciaFenceGateOpen)
                                .rotationY(angle)
                                .uvLock(true)
                            .addModel()
                       .partialState()
                            .with(FenceGateBlock.HORIZONTAL_FACING, dir)
                            .with(FenceGateBlock.IN_WALL, true)
                            .with(FenceGateBlock.OPEN, false)
                            .modelForState()
                                .modelFile(acaciaFenceGateWall)
                                .rotationY(angle)
                                .uvLock(true)
                            .addModel()
                       .partialState()
                            .with(FenceGateBlock.HORIZONTAL_FACING, dir)
                            .with(FenceGateBlock.IN_WALL, true)
                            .with(FenceGateBlock.OPEN, true)
                            .modelForState()
                                .modelFile(acaciaFenceGateWallOpen)
                                .rotationY(angle)
                                .uvLock(true)
                            .addModel();
           }

           ModelFile acaciaFencePost = getBuilder("acacia_fence_post")
                   .parent(getExistingFile("block/fence_post"))
                   .texture("texture", new ResourceLocation("block/acacia_planks"));
           
           ModelFile acaciaFenceSide = getBuilder("acacia_fence_side")
                   .parent(getExistingFile("block/fence_side"))
                   .texture("texture", new ResourceLocation("block/acacia_planks"));
           
           getMultipartBuilder(Blocks.ACACIA_FENCE)
                   .part().modelFile(acaciaFencePost).addModel().build()
                   .part().modelFile(acaciaFenceSide).uvLock(true).addModel()
                           .condition(FenceBlock.NORTH, true).build()
                   .part().modelFile(acaciaFenceSide).rotationY(90).uvLock(true).addModel()
                           .condition(FenceBlock.EAST, true).build()
                   .part().modelFile(acaciaFenceSide).rotationY(180).uvLock(true).addModel()
                           .condition(FenceBlock.SOUTH, true).build()
                   .part().modelFile(acaciaFenceSide).rotationY(270).uvLock(true).addModel()
                           .condition(FenceBlock.WEST, true).build();
       }

       @Override
       public String getName()
       {
           return "Forge Test Blockstates";
       }
    }
}
