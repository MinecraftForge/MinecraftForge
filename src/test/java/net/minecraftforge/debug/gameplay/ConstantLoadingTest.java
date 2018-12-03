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

import java.io.IOException;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = ConstantLoadingTest.MODID, name = "ConstantLoadingTestMod", version = "1.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class ConstantLoadingTest
{
    public static final String MODID = "constantloadingtest";
    private static final boolean ENABLED = true;

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        if (!ENABLED)
        {
            return;
        }
        JsonContext ctx = null;
        try
        {
            ctx = CraftingHelper.loadContext(new ResourceLocation(MODID, "test/_constants.json"));
        }
        catch (IOException e) 
        {
            throw new RuntimeException("Exception loading test constants file", e);
        }

        Ingredient flint = ctx.getConstant("FLINT");
        if (flint == null)
        {
            throw new IllegalStateException("Constant ingredient not loaded properly");
        }
        if (!flint.apply(new ItemStack(Items.FLINT)))
        {
            throw new IllegalStateException("Constant ingredient failed to match test input");
        }
    }
}
