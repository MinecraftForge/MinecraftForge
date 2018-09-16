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

package net.minecraftforge.debug.item;

import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Usage (use survival so you can eat food):
 * 1. Drink curable_potion from Brewing creative tab
 * 2. Relog to test that changes to curative items persist, then eat the "medicine" item to cure the effect
 * 3. Drink incurable_potion from Brewing creative tab
 * 4. Relog to test that changes to curative items persist, then try drinking milk and eating medicine: they should have no effect
 */
@Mod(modid = PotionCurabilityTest.MODID, name = "Potion Curative Item Debug", version = "1.0", acceptableRemoteVersions = "*")
public class PotionCurabilityTest
{
    public static final boolean ENABLED = false;
    public static final String MODID = "potion_curative_item_debug";

    @ObjectHolder("medicine")
    public static final Item MEDICINE = null;
    private static Potion INCURABLE_POTION;

    @Mod.EventBusSubscriber(modid = MODID)
    public static class Registration
    {
        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            if (!ENABLED) return;
            event.getRegistry().register(new Medicine().setRegistryName(MODID, "medicine"));
        }

        @net.minecraftforge.eventbus.api.SubscribeEvent
        public static void registerPotions(RegistryEvent.Register<Potion> event)
        {
            if (!ENABLED) return;
            INCURABLE_POTION = new IncurablePotion().setRegistryName(MODID, "incurable_potion");
            event.getRegistry().register(INCURABLE_POTION);
        }

        @SubscribeEvent
        public static void registerPotionTypes(RegistryEvent.Register<PotionType> event)
        {
            if (!ENABLED) return;
            // Register PotionType that can be cured with medicine
            PotionEffect curable = new PotionEffect(INCURABLE_POTION, 1200);
            curable.setCurativeItems(Collections.singletonList(new ItemStack(MEDICINE)));
            event.getRegistry().register(new PotionType(curable).setRegistryName(MODID, "curable_potion_type"));

            // Register PotionType that can't be cured
            event.getRegistry().register(new PotionType(new PotionEffect(INCURABLE_POTION, 1200)).setRegistryName(MODID, "incurable_potion_type"));
        }
    }

    @Mod.EventBusSubscriber(value = Side.CLIENT, modid = MODID)
    public static class ClientEventHandler
    {
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            if (!ENABLED) return;
            ModelBakery.registerItemVariants(MEDICINE);
        }
    }

    private static class IncurablePotion extends Potion
    {
        protected IncurablePotion()
        {
            super(false, 0x94A061);
            setPotionName("incurable_potion");
            setIconIndex(6, 0);
        }

        @Override
        public List<ItemStack> getCurativeItems()
        {
            // By default, no PotionEffect of this Potion can be cured by anything
            return new ArrayList<ItemStack>();
        }
    }

    private static class Medicine extends ItemFood
    {
        public Medicine()
        {
            super(2, 1, false);
            setUnlocalizedName("medicine");
            setAlwaysEdible();
        }

        @Override
        protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player)
        {
            if (!worldIn.isRemote)
            {
                player.curePotionEffects(stack);
            }
        }
    }
}
