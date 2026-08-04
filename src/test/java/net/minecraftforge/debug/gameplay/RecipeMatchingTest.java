/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/*


package net.minecraftforge.debug.gameplay;

import java.util.Random;

import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.FMLPreInitializationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;

//@Mod(modid = RecipeMatchingTest.MODID, name = "Recipe test mod", version = "1.0", acceptableRemoteVersions = "*")
public class RecipeMatchingTest
{
    public static final String MODID = "recipetest";
    private static final boolean ENABLED = true;
    public static CommonProxy proxy = null;


    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
        if (ENABLED)
            MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void registerRecipes(RegistryEvent.Register<IRecipe> event)
    {
        ResourceLocation location1 = new ResourceLocation(MODID, "dirt");
        ShapedOreRecipe recipe1 = new ShapedOreRecipe(location1, new ItemStack(Blocks.DIAMOND_BLOCK), "DDD", 'D', new ItemStack(Blocks.DIRT));
        recipe1.setRegistryName(location1);
        event.getRegistry().register(recipe1);

        if (FMLLaunchHandler.side() == Side.SERVER)
        {
            ResourceLocation location2 = new ResourceLocation(MODID, "stone");
            CraftingHelper.ShapedPrimer primer1 = CraftingHelper.parseShaped("SSS", 'S', new ItemStack(Blocks.IRON_BLOCK));
            ShapedRecipes recipe2 = new ShapedRecipes(location2.getResourcePath(), primer1.width, primer1.height, primer1.input, new ItemStack(Blocks.GOLD_BLOCK));
            recipe2.setRegistryName(location2);
            event.getRegistry().register(recipe2);
        }
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event)
    {
        proxy.registerItem(event);
    }

    public static abstract class CommonProxy
    {
        protected Item TOOL;
        public void registerItem(RegistryEvent.Register<Item> event)
        {
           TOOL = new Item()
           {
               Random RAND = new Random();
               @Override
               public ItemStack getContainerItem(ItemStack in)
               {
                    ItemStack ret = in.copy();
                    ret.attemptDamageItem(1, RAND, null);
                    return ret;
                }

               @Override
               public boolean hasContainerItem()
               {
                   return true;
               }
            }.setRegistryName(MODID, "tool").setMaxDamage(10).setCreativeTab(CreativeTabs.MISC).setUnlocalizedName("recipetest.tool").setMaxStackSize(1);
            event.getRegistry().register(TOOL);
        }
    }

    public static final class ServerProxy extends CommonProxy
    {
    }

    public static final class ClientProxy extends CommonProxy
    {
        @Override
        public void registerItem(RegistryEvent.Register<Item> event)
        {
            super.registerItem(event);
            ModelLoader.setCustomModelResourceLocation(TOOL, 0, new ModelResourceLocation("minecraft:stick#inventory"));
        }
    }
}
*/
