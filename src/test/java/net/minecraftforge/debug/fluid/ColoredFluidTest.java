/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/*


package net.minecraftforge.debug.fluid;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.event.FMLPreInitializationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

//@Mod(modid = ColoredFluidTest.MODID, name = "Test Mod", version = "1.0.0", acceptedMinecraftVersions = "*", acceptableRemoteVersions = "*")
//@EventBusSubscriber
public class ColoredFluidTest
{
    static final boolean ENABLED = false;      // <-- enable mod
    static final int COLOR = 0xFFAFAF; // <-- change this to try other colors
    
    static final String MODID = "fluidadditionalfields";
    static final ResourceLocation RES_LOC = new ResourceLocation(MODID, "slime");
    static
    {
        if (ENABLED)
        {
            FluidRegistry.enableUniversalBucket();
        }
    }
    public static final Fluid SLIME = new Fluid("slime", new ResourceLocation(MODID, "slime_still"), new ResourceLocation(MODID, "slime_flow"), new ResourceLocation(MODID, "slime_overlay")).setColor(COLOR);
    @ObjectHolder("slime")
    public static final BlockFluidBase SLIME_BLOCK = null;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLED)
        {
            FluidRegistry.registerFluid(SLIME);
            FluidRegistry.addBucketForFluid(SLIME);
        }
    }

    @SubscribeEvent
    public static void eventBlockRegistry(final RegistryEvent.Register<Block> event)
    {
        if (ENABLED)
        {
            event.getRegistry().register((new BlockFluidClassic(SLIME, Material.WATER)).setRegistryName(RES_LOC).setUnlocalizedName(RES_LOC.toString()));
        }
    }
}
*/
