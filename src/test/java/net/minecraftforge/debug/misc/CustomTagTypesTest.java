/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.debug.misc;

import net.minecraft.tags.TagRegistry;
import net.minecraft.tags.TagRegistryManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.Tags.IOptionalNamedTag;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(CustomTagTypesTest.MODID)
public class CustomTagTypesTest
{
    public static final String MODID = "custom_tag_types_test";

    public CustomTagTypesTest()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event)
    {
        BiomeTags.init();
    }

    private static class BiomeTags
    {
        //This needs to be initialised after registry events.
        private static final TagRegistry<Biome> biomeTagRegistry = TagRegistryManager.getOrCreateCustomTagType(ForgeRegistries.BIOMES);
        private static final IOptionalNamedTag<Biome> OCEANS = tag("oceans");

        private static IOptionalNamedTag<Biome> tag(String name)
        {
            return biomeTagRegistry.createOptional(new ResourceLocation(MODID, name), () -> null);
        }

        private static void init() {}
    }
}
