package net.minecraftforge.debug;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
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

@Mod(modid = FluidBehavesLikeWater.MODID, name = "Test Mod", version = "1.0.0", acceptedMinecraftVersions = "*")
@EventBusSubscriber
public class FluidBehavesLikeWater
{
    static final boolean ENABLED = true; // <-- enable mod
    public static final Material SLIME_MATERIAL = new MaterialLiquid(MapColor.GREEN)
            .setCanDrownEntity()
            .setCanFloatBoat()   // <-- change these chained methods to change material behavior
            .setCanPushEntity()
            .setIsSwimmable()
            .setCanBeAbsorbed()
            .setCanWaterPlants()
            .setCanSpawnWaterCreatures();

    static final String MODID = "fluidbehaveslikewater";
    static final net.minecraft.util.ResourceLocation RES_LOC = new ResourceLocation(MODID, "slime");
    static
    {
        if (ENABLED)
        {
            FluidRegistry.enableUniversalBucket();
        }
    }
    public static final Fluid SLIME = new Fluid("slime", new net.minecraft.util.ResourceLocation(MODID, "slime_still"),
            new ResourceLocation(MODID, "slime_flow")) 
            {        
                @Override
                public int getColor()
                {
                    return 0xFFd742f4; // pink slime
                }
            };

    @ObjectHolder("slime")
    public static final BlockFluidBase SLIME_BLOCK = null;

    @EventHandler
    public void preInit(FMLPreInitializationEvent evetn)
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
            event.getRegistry().register((new BlockFluidClassic(SLIME, SLIME_MATERIAL)).setRegistryName(RES_LOC).setUnlocalizedName(RES_LOC.toString()));
        }
    }
}