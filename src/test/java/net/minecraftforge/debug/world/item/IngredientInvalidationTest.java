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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.stream.Stream;

@Mod(IngredientInvalidationTest.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = IngredientInvalidationTest.MOD_ID)
public class IngredientInvalidationTest {
    public static final String MOD_ID = "ingredient_invalidation";

    private static final boolean ENABLED = true;

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final RegistryObject<Item> TEST_ITEM = ITEMS.register("test_item", () -> new Item(new Item.Properties()));

    private static boolean invalidateExpected = false;
    private static boolean gotInvalidate = false;

    private static final Ingredient TEST_INGREDIENT = new Ingredient(Stream.of(new Ingredient.ItemValue(new ItemStack(Items.WHEAT)))) {
        @Override
        protected void invalidate() {
            super.invalidate();
            gotInvalidate = true;
        }
    };

    public IngredientInvalidationTest() {
        if (!ENABLED)
            return;

        MinecraftForge.EVENT_BUS.register(ForgeEventListener.class);
    }

    private static class ForgeEventListener {

        @SubscribeEvent
        public static void worldLoad(WorldEvent.Load event) {
            if (event.getWorld() instanceof ServerLevel level && level.dimension().equals(Level.OVERWORLD)) {
                TEST_INGREDIENT.getStackingIds(); // force invalidation if necessary
                if (!invalidateExpected) {
                    invalidateExpected = true;
                } else if (!gotInvalidate) {
                    throw new IllegalStateException("Ingredient.invalidate was not called.");
                } else {
                    gotInvalidate = false;
                }
            }
        }
    }
}