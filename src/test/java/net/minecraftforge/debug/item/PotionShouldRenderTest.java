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

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@Mod(modid = PotionShouldRenderTest.modID, name = "No Potion Effect Render Test", version = "0.0.0", acceptableRemoteVersions = "*")
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
