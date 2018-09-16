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

package net.minecraftforge.debug.client;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;

@Mod(modid = SearchableCreativeTabTest.MODID, name = "Debug Search Tab", version = "1.0", acceptableRemoteVersions = "*")
public class SearchableCreativeTabTest
{
    public static final String MODID = "debugsearchtab";
    static final boolean ENABLED = false;

    public static final CreativeTabs SEARCH_TAB = !ENABLED ? null : new CreativeTabs(1, "searchtab")
    {
        @OnlyIn(Dist.CLIENT)
        public ItemStack getTabIconItem()
        {
            return new ItemStack(Items.TOTEM_OF_UNDYING);
        }
        
        @Override
        public boolean hasSearchBar()
        {
        	return true;
        }
        
        @Override
        @OnlyIn(Dist.CLIENT)
        public void displayAllRelevantItems(NonNullList<ItemStack> items)
        {
        	super.displayAllRelevantItems(items);
        	items.add(new ItemStack(Blocks.BARRIER));
        	items.add(new ItemStack(Blocks.COMMAND_BLOCK));
        	items.add(new ItemStack(Blocks.CHAIN_COMMAND_BLOCK));
        	items.add(new ItemStack(Blocks.REPEATING_COMMAND_BLOCK));
        }
    };
}
