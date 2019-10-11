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

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelFile.ExistingModelFile;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.crafting.ConditionalAdvancement;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import java.util.function.Consumer;

@Mod("data_gen_test")
@Mod.EventBusSubscriber(bus = Bus.MOD)
public class DataGeneratorTest
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();

        if (event.includeClient())
        {
            gen.addProvider(new ItemModels(gen));
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
        public ItemModels(DataGenerator generator)
        {
            super(generator, "forge", ITEM_FOLDER, ItemModelBuilder::new);
        }
        
        @Override
        protected void registerBuilders()
        {
            getBuilder("test_generated_model")
                    .parent(new UncheckedModelFile("item/generated"))
                    .texture("layer0", "block/stone");
            
            getBuilder("test_block_model")
                    .parent(new ExistingModelFile("block/block"))
                    .texture("all", "block/dirt")
                    .texture("top", "block/stone")
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
}
