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

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.smelting.BasicSmeltingRecipe;
import net.minecraftforge.common.smelting.ISmeltingRecipe;
import net.minecraftforge.common.smelting.SmeltingHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;

import javax.annotation.Nonnull;

@Mod(modid = CustomSmeltingTest.MODID, version = "1.0", acceptableRemoteVersions = "*")
public class CustomSmeltingTest
{
    public static final String MODID = "furnace_customsmelt_test";

    private static final boolean ENABLED = false;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLED)
        {
            SmeltingHandler handler = SmeltingHandler.instance();
            handler.addSmeltingRecipe(new ItemStack(Blocks.COBBLESTONE), new ItemStack(Items.SKULL), 1); // ignored+warn(conflict)

            Ingredient cobbleNglass = Ingredient.fromStacks(new ItemStack(Blocks.COBBLESTONE), new ItemStack(Items.GLASS_BOTTLE));
            handler.addSmeltingRecipe(new BasicSmeltingRecipe(cobbleNglass, new ItemStack(Blocks.STONE), 1)); // merged

            NonNullList<ItemStack> IIron = OreDictionary.getOres("ingotIron", false);
            if (IIron.size() > 0)
                handler.addSmeltingRecipe(new BasicSmeltingRecipe(new OreIngredient("oreIron"), IIron.get(0), 1)); // merged

            handler.addSmeltingRecipe(new BasicSmeltingRecipe(new OreIngredient("oreBanana"), new ItemStack(Blocks.SAPLING), 1)); // ignored+warn(emtpy input)

            SmeltingHandler.instance().addSmeltingRecipe(new PotionSmeltingRecipe()); // added
        }
    }

    private static class PotionSmeltingRecipe implements ISmeltingRecipe
    {

        private static final ItemStack generic = new ItemStack(Items.LINGERING_POTION);
        private static final Ingredient input = Ingredient.fromItem(Items.POTIONITEM);

        @Nonnull
        @Override
        public Ingredient getInput()
        {
            return input;
        }

        @Nonnull
        @Override
        public ItemStack getSmeltingResult(ItemStack input)
        {
            if (input.isEmpty())
                return generic;
            // set the output item to be a lingering potion with the same effects as the input potion
            ItemStack out = new ItemStack(Items.LINGERING_POTION);
            PotionUtils.addPotionToItemStack(out, PotionUtils.getPotionFromItem(input));
            PotionUtils.appendEffects(out, PotionUtils.getEffectsFromStack(input));
            return out;
        }

        @Override
        public float getExperience(ItemStack input)
        {
            return input.getItem().getSmeltingExperience(input);
        }
    }
}
