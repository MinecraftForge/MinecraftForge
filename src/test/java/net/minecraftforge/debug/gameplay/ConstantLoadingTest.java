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

package net.minecraftforge.debug.gameplay;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.Ingredient.IItemList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;

//@Mod(ConstantLoadingTest.MODID)
//@Mod.EventBusSubscriber
public class ConstantLoadingTest
{
    public static final String MODID = "constantloadingtest";
    private static final boolean ENABLED = true;

    @SubscribeEvent
    public void init(FMLServerStartedEvent event) throws IOException
    {
        if (!ENABLED)
        {
            return;
        }
        
        Map<ResourceLocation, IItemList> constants = CraftingHelper.loadConstants(event.getServer().getResourceManager(), new ResourceLocation(MODID, "test/_constants.json"));
        Ingredient flint = Ingredient.fromItemListStream(Stream.of(constants.get(new ResourceLocation("FLINT"))));
        if (flint == null)
        {
            throw new IllegalStateException("Constant ingredient not loaded properly");
        }
        if (!flint.test(new ItemStack(Items.FLINT)))
        {
            throw new IllegalStateException("Constant ingredient failed to match test input");
        }
    }
}
