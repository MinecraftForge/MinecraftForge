/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(RangedMobsUseModdedWeaponsTest.MOD_ID)
public class RangedMobsUseModdedWeaponsTest {

    //Testing if the new alternative for ProjectileHelper.getWeaponHoldingHand works for vanilla mobs
    // as well as replacing their usages of LivingEntity#isHolding(Item) with LivingEntity#isHolding(Predicate<Item)
    //Skeletons and Illusioners should be able to use the modded bow.
    //Piglins and Pillagers should be able to use the modded crossbow.

    public static final boolean ENABLE = true;

    public static final String MOD_ID = "ranged_mobs_use_modded_weapons_test";
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    private static final RegistryObject<Item> MODDED_BOW = ITEMS.register("modded_bow", () ->
            new BowItem(new Item.Properties().tab(ItemGroup.TAB_COMBAT).defaultDurability(384))
    );
    private static final RegistryObject<Item> MODDED_CROSSBOW = ITEMS.register("modded_crossbow", () ->
            new CrossbowItem(new Item.Properties().tab(ItemGroup.TAB_COMBAT).defaultDurability(326))
    );

    public RangedMobsUseModdedWeaponsTest()
    {
        if(ENABLE){
            IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
            ITEMS.register(modEventBus);
            modEventBus.addListener(this::onClientSetup);
        }
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        RangedWeaponModeLProperties.initBowModelProperties();
        RangedWeaponModeLProperties.initCrossbowModelProperties();
    }

    private static class RangedWeaponModeLProperties
    {
        static void initBowModelProperties()
        {
            ItemModelsProperties.register(MODDED_BOW.get(), new ResourceLocation("pull"), (itemStack, clientWorld, livingEntity) -> {
                if (livingEntity == null)
                {
                    return 0.0F;
                } else {
                    return livingEntity.getUseItem() != itemStack ? 0.0F : (float)(itemStack.getUseDuration() - livingEntity.getUseItemRemainingTicks()) / 20.0F;
                }
            });
            ItemModelsProperties.register(MODDED_BOW.get(), new ResourceLocation("pulling"), (itemStack, clientWorld, livingEntity) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack ? 1.0F : 0.0F);
        }

        static void initCrossbowModelProperties()
        {
            ItemModelsProperties.register(MODDED_CROSSBOW.get(), new ResourceLocation("pull"), (itemStack, clientWorld, livingEntity) -> {
                if (livingEntity == null)
                {
                    return 0.0F;
                } else {
                    return CrossbowItem.isCharged(itemStack) ? 0.0F : (float)(itemStack.getUseDuration() - livingEntity.getUseItemRemainingTicks()) / (float)CrossbowItem.getChargeDuration(itemStack);
                }
            });
            ItemModelsProperties.register(MODDED_CROSSBOW.get(), new ResourceLocation("pulling"), (itemStack, clientWorld, livingEntity) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack && !CrossbowItem.isCharged(itemStack) ? 1.0F : 0.0F);
            ItemModelsProperties.register(MODDED_CROSSBOW.get(), new ResourceLocation("charged"), (itemStack, clientWorld, livingEntity) -> livingEntity != null && CrossbowItem.isCharged(itemStack) ? 1.0F : 0.0F);
            ItemModelsProperties.register(MODDED_CROSSBOW.get(), new ResourceLocation("firework"), (itemStack, clientWorld, livingEntity) -> livingEntity != null && CrossbowItem.isCharged(itemStack) && CrossbowItem.containsChargedProjectile(itemStack, Items.FIREWORK_ROCKET) ? 1.0F : 0.0F);
        }
    }
}