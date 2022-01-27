/*
 * Minecraft Forge
 * Copyright (c) 2016-2022.
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

package net.minecraftforge.debug.world.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.stream.Stream;

/**
 * This class validates that {@link Ingredient#invalidate()} is called correctly.<br>
 * To verify, join a world, then leave it. Then join a world again in the same game session. If invalidation is not working,
 * the 2nd world join will trigger an exception.
 */
@Mod(IngredientInvalidationTest.MOD_ID)
public class IngredientInvalidationTest
{
    public static final String MOD_ID = "ingredient_invalidation";

    private static final boolean ENABLED = true;

    private static boolean invalidateExpected = false;
    private static boolean gotInvalidate = false;

    private static final Ingredient TEST_INGREDIENT = new Ingredient(Stream.of(new Ingredient.ItemValue(new ItemStack(Items.WHEAT))))
    {
        @Override
        protected void invalidate()
        {
            super.invalidate();
            gotInvalidate = true;
        }
    };

    public IngredientInvalidationTest()
    {
        if (!ENABLED)
            return;

        MinecraftForge.EVENT_BUS.addListener(IngredientInvalidationTest::worldLoad);
    }

    private static void worldLoad(WorldEvent.Load event)
    {
        if (event.getWorld() instanceof ServerLevel level && level.dimension().equals(Level.OVERWORLD))
        {
            TEST_INGREDIENT.getStackingIds(); // force invalidation if necessary
            if (!invalidateExpected)
            {
                invalidateExpected = true;
            }
            else if (!gotInvalidate)
            {
                throw new IllegalStateException("Ingredient.invalidate was not called.");
            }
            else
            {
                gotInvalidate = false;
            }
        }
    }

}