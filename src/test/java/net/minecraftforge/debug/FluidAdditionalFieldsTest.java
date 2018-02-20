package net.minecraftforge.debug;

import java.awt.Color;

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
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@Mod(modid = FluidAdditionalFieldsTest.MODID, name = "Test Mod", version = "1.0.0", acceptedMinecraftVersions = "*", acceptableRemoteVersions = "*")
@EventBusSubscriber
public class FluidAdditionalFieldsTest
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
