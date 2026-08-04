/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/*


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
*/
