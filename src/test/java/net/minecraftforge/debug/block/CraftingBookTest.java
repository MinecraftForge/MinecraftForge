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

package net.minecraftforge.debug.block;

import net.minecraft.item.*;
import net.minecraft.item.crafting.*;
import net.minecraftforge.common.ForgeRecipeBookCategory;
import net.minecraftforge.common.extensions.IForgeRecipeBookCategory;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ObjectHolder;

@Mod("base_crafting_book_test")
@Mod.EventBusSubscriber(bus = Bus.MOD)
public class CraftingBookTest {
    @ObjectHolder("damageable")
    public static final ForgeRecipeBookCategory<?> damageable = null;
    @ObjectHolder("edible")
    public static final ForgeRecipeBookCategory<?> edible = null;
    @ObjectHolder("enchantable")
    public static final ForgeRecipeBookCategory<?> enchantable = null;
    @ObjectHolder("stackable")
    public static final ForgeRecipeBookCategory<?> stackable = null;

    @SubscribeEvent
    public static void registerForgeRecipeBookCategories(RegistryEvent.Register<IForgeRecipeBookCategory<?>> event) {
        // category for damageable items
        event.getRegistry().register(new ForgeRecipeBookCategory<IRecipeType<?>>(false, IRecipeType.CRAFTING,
                recipe -> recipe.getResultItem().isDamageableItem(), new ItemStack(Items.ZOMBIE_HEAD))
                .setRegistryName("base_crafting_book_test", "damageable"));
        // category for edible items
        event.getRegistry().register(new ForgeRecipeBookCategory<IRecipeType<?>>(false, IRecipeType.CRAFTING,
                recipe -> recipe.getResultItem().isEdible(), new ItemStack(Items.ROTTEN_FLESH))
                .setRegistryName("base_crafting_book_test", "edible"));
        // category for enchantable items
        event.getRegistry().register(new ForgeRecipeBookCategory<IRecipeType<?>>(false, IRecipeType.CRAFTING,
                recipe -> recipe.getResultItem().isEnchantable(), new ItemStack(Items.GOLDEN_AXE), new ItemStack(Items.GOLDEN_PICKAXE))
                .setRegistryName("base_crafting_book_test", "enchantable"));
        // category for stackable items
        event.getRegistry().register(new ForgeRecipeBookCategory<IRecipeType<?>>(false, IRecipeType.CRAFTING,
                recipe -> recipe.getResultItem().isStackable(), new ItemStack(Items.BEACON))
                .setRegistryName("base_crafting_book_test", "stackable"));
    }
}
