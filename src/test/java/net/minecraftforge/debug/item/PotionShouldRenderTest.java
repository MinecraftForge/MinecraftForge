/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/*


package net.minecraftforge.debug.item;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

//@Mod(modid = PotionShouldRenderTest.modID, name = "No Potion Effect Render Test", version = "0.0.0", acceptableRemoteVersions = "*")
public class PotionShouldRenderTest
{
    public static final String modID = "nopotioneffect";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        TestPotion INSTANCE = (TestPotion)new TestPotion(new ResourceLocation(modID, "test_potion"), false, 0xff00ff).setRegistryName(new ResourceLocation(modID, "test_potion"));
        ForgeRegistries.POTIONS.register(INSTANCE);
    }

    public static class TestPotion extends Potion
    {

        public TestPotion(ResourceLocation location, boolean badEffect, int potionColor)
        {
            super(badEffect, potionColor);
        }

        @Override
        public boolean shouldRender(PotionEffect effect)
        {
            return false;
        }
    }
}
*/
