/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.debug.fluid;

import java.awt.Color;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidColor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = ColoredFluidTest.MODID, name = "Colored Fluid Test Mod", version = "1.0.0", acceptedMinecraftVersions = "*", acceptableRemoteVersions = "*")
@EventBusSubscriber
public class ColoredFluidTest
{
    static final boolean ENABLED = false;      // <-- enable mod
    static final Color COLOR = Color.PINK; // <-- change this to try other colors
    
    static final String MODID = "fluidadditionalfields";
    static final ResourceLocation RES_LOC = new ResourceLocation(MODID, "slime");
    static
    {
        if (ENABLED)
        {
            FluidRegistry.enableUniversalBucket();
        }
    }

    public static final Fluid SLIME = new ColoredFluid("slime", new ResourceLocation(MODID, "slime_still"), new ResourceLocation(MODID, "slime_flow"));

    @ObjectHolder("slime")
    public static final BlockFluidBase SLIME_BLOCK = null;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLED)
        {
            FluidRegistry.registerFluid(SLIME);
            FluidRegistry.addBucketForFluid(SLIME);
            FluidRegistry.registerFluidColorHandler(new IFluidColor.Default(), SLIME);
        }
    }

    @SubscribeEvent
    public static void registerBlocks(final RegistryEvent.Register<Block> event)
    {
        if (ENABLED)
        {
            event.getRegistry().register(new BlockFluidClassic(SLIME, Material.WATER)
                    .setRegistryName(RES_LOC)
                    .setUnlocalizedName(MODID + ".slime")
            );
        }
    }

    @EventBusSubscriber(modid = MODID, value = Side.CLIENT)
    public static class ClientEventHandler
    {
        @SubscribeEvent
        public static void registerBlockColors(ColorHandlerEvent.Block event)
        {
            event.getBlockColors().registerBlockColorHandler((state, worldIn, pos, tintIndex) -> COLOR.getRGB(), SLIME_BLOCK);
        }
    }

    static class ColoredFluid extends Fluid
    {
        ColoredFluid(String fluidName, ResourceLocation still, ResourceLocation flowing)
        {
            super(fluidName, still, flowing);
        }

        @Override
        public int getColor()
        {
            return -1;
        }

        @Override
        public int getColor(FluidStack stack)
        {
            return COLOR.getRGB();
        }

        @Override
        public int getColor(World world, BlockPos pos)
        {
            return COLOR.getRGB();
        }
    }
}
