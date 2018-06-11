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

import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = FurnaceFuelBurnTimeEventTest.MOD_ID, name = "Test for FurnaceFuelBurnTimeEvent", version = "1.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class FurnaceFuelBurnTimeEventTest
{
    public static final String MOD_ID = "furnacefuelburntimeeventtest";
    private static final ResourceLocation unburnableWoodShovelName = new ResourceLocation(MOD_ID, "unburnable_wood_shovel");
    private static final ResourceLocation flammableGoldShovelName = new ResourceLocation(MOD_ID, "flammable_gold_shovel");
    @GameRegistry.ObjectHolder("unburnable_wood_shovel")
    public static final Item UNBURNABLE_WOOD_SHOVEL = null;
    @GameRegistry.ObjectHolder("flammable_gold_shovel")
    public static final Item FLAMMABLE_GOLD_SHOVEL = null;

    @SubscribeEvent
    public static void onFurnaceFuelBurnTimeEvent(FurnaceFuelBurnTimeEvent event)
    {
        ItemStack itemStack = event.getItemStack();
        Item item = itemStack.getItem();
        if (item == Items.SLIME_BALL)
        {
            event.setBurnTime(100); // make slime balls burn as fuel in the furnace
        }
        else if (item == Items.WOODEN_AXE)
        {
            event.setBurnTime(0); // do not allow the wooden axe to be used as fuel
        }
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new UnburnableWoodShovel().setRegistryName(unburnableWoodShovelName));
        event.getRegistry().register(new FlammableGoldShovel().setRegistryName(flammableGoldShovelName));
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        {
            final ModelResourceLocation location = new ModelResourceLocation(unburnableWoodShovelName, "inventory");
            ModelBakery.registerItemVariants(UNBURNABLE_WOOD_SHOVEL, location);
            ModelLoader.setCustomMeshDefinition(UNBURNABLE_WOOD_SHOVEL, is -> location);
        }
        {
            final ModelResourceLocation location = new ModelResourceLocation(flammableGoldShovelName, "inventory");
            ModelBakery.registerItemVariants(FLAMMABLE_GOLD_SHOVEL, location);
            ModelLoader.setCustomMeshDefinition(FLAMMABLE_GOLD_SHOVEL, is -> location);
        }
    }

    public static class UnburnableWoodShovel extends ItemSpade
    {
        public UnburnableWoodShovel()
        {
            super(ToolMaterial.WOOD);
            setCreativeTab(CreativeTabs.TOOLS);
            setMaxStackSize(1);
        }

        @Override
        public int getItemBurnTime(ItemStack itemStack)
        {
            return 0;
        }

        @Override
        public String getItemStackDisplayName(ItemStack stack)
        {
            return "Unburnable Wooden Shovel";
        }
    }

    public static class FlammableGoldShovel extends ItemSpade
    {
        public FlammableGoldShovel()
        {
            super(ToolMaterial.GOLD);
            setCreativeTab(CreativeTabs.TOOLS);
            setMaxStackSize(1);
        }

        @Override
        public int getItemBurnTime(ItemStack itemStack)
        {
            return 1000;
        }

        @Override
        public String getItemStackDisplayName(ItemStack stack)
        {
            return "Flammable Golden Shovel";
        }
    }
}
