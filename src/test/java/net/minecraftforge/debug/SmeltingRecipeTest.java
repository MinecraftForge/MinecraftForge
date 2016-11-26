/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.smelting.SmeltingRecipeRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;

@Mod(modid = "forgesmeltingtest")
public class SmeltingRecipeTest
{

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        // test general mechanic
        SmeltingRecipeRegistry.addSimpleRecipe(new ItemStack(Blocks.DIRT), new ItemStack(Items.DIAMOND), 2, 50);

        // test metadata inputs
        SmeltingRecipeRegistry.addSimpleRecipe(new ItemStack(Blocks.DIRT, 1, 1), new ItemStack(Items.DIAMOND_AXE));

        // test wildcard experience
        SmeltingRecipeRegistry.addSimpleRecipe(new ItemStack(Blocks.DIAMOND_BLOCK), new ItemStack(Blocks.DIRT, 1, 1));
        SmeltingRecipeRegistry.setExperience(new ItemStack(Blocks.DIRT, 1, OreDictionary.WILDCARD_VALUE), 4);
    }

}
