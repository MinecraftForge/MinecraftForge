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

package net.minecraftforge.debug.item.crafting;

import java.util.function.Consumer;

import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.BlacksmithingRecipeBuilder;
import net.minecraftforge.common.data.GrindingRecipeBuilder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(GrindingRecipeTest.MOD_ID)
public class GrindingRecipeTest
{
    public static final String MOD_ID = "grinding_recipe_test";
    
    public GrindingRecipeTest()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::gatherData);
    }
    
    
    private void gatherData(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        if (event.includeServer())
        {
            generator.addProvider(new RecipeProviderTest(generator));
        }
    }
    
    public class RecipeProviderTest extends RecipeProvider
    {
        
        public RecipeProviderTest(DataGenerator generator)
        {
            super(generator);
        }
        
        @Override
        protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer)
        {
            BlacksmithingRecipeBuilder.blacksmithing(Ingredient.of(new ItemStack(Items.IRON_INGOT)), Ingredient.of(new ItemStack(Items.FLINT)), new ItemStack(Items.FLINT_AND_STEEL), 0)
            .unlocks(MOD_ID, InventoryChangeTrigger.Instance.hasItems(Items.IRON_INGOT))
            .save(consumer, new ResourceLocation(MOD_ID, "flintandsteel"));
            
            GrindingRecipeBuilder.grinding(Ingredient.of(Items.LAPIS_LAZULI), Ingredient.of(Items.NETHERITE_INGOT), new ItemStack(Items.DIAMOND))
            .unlocks(MOD_ID, InventoryChangeTrigger.Instance.hasItems(Items.LAPIS_LAZULI))
            .save(consumer, new ResourceLocation(MOD_ID, "lapis2diamond"));
        }
    }
}
