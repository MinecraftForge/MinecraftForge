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

package net.minecraftforge.debug.world;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DimensionLightMapModificationEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(DimensionLightMapTest.MODID)
public class DimensionLightMapTest
{

    public static final String MODID = "dim_lightmap_test";

    public static ResourceLocation DIMENSION_LOC = new ResourceLocation(MODID,"custom_dimension_1");
    public static RegistryKey<World> TEST_WORLD;
    private static final boolean ENABLED = true;

    public DimensionLightMapTest()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.addListener(this::onDimensionLightMapModification);
        }
    }

    public void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> {
            TEST_WORLD = RegistryKey.create(Registry.DIMENSION_REGISTRY, DIMENSION_LOC);
        });
    }

    public void onDimensionLightMapModification(DimensionLightMapModificationEvent event)
    {
        if (event.getWorld().dimension() == TEST_WORLD)
        {
            //Make the lighting colors flash red. Works best in enclosed areas
            float red = 1F;
            float green = event.getLightMapColors().y() * 1 - ((float)(Math.sin(event.getWorld().getGameTime() * 0.1)) * 0.5F);
            float blue = event.getLightMapColors().z() * 1 - ((float)(Math.sin(event.getWorld().getGameTime() * 0.1)) * 0.5F);
            event.getLightMapColors().set(red, green, blue);
        }
    }

}
