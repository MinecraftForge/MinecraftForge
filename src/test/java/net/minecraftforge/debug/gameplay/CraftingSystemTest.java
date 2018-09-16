/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.debug.gameplay;

import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.BooleanSupplier;

@Mod(modid = CraftingSystemTest.MOD_ID, name = "CraftingTestMod", version = "1.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class CraftingSystemTest
{
    static final String MOD_ID = "crafting_system_test";
    static final boolean ENABLED = false;

    static final Logger logger = LogManager.getLogger(MOD_ID);

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event)
    {
        logger.info("Registering Test Recipes:");
    }

    public static class IngredientFactory implements IIngredientFactory
    {
        @Override
        public Ingredient parse(JsonContext context, JsonObject json)
        {
            return null;
        }
    }

    public static class RecipeFactory implements IRecipeFactory
    {
        @Override
        public IRecipe parse(JsonContext context, JsonObject json)
        {
            return null;
        }
    }

    public static class ConditionFactory implements IConditionFactory
    {
        @Override
        public BooleanSupplier parse(JsonContext context, JsonObject json)
        {
            return () -> true;
        }
    }
}
