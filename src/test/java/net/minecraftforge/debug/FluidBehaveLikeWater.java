package net.minecraftforge.debug;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = FluidBehaveLikeWater.MODID, name = "Test Mod", version = "1.0.0", acceptedMinecraftVersions = "*")
@EventBusSubscriber
public class FluidBehaveLikeWater
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
    public static final ModFluid SLIME = new ModFluid("slime", new net.minecraft.util.ResourceLocation(MODID, "slime_still"),
            new ResourceLocation(MODID, "slime_flow"));

    public static class ModFluid extends Fluid
    {
        public ModFluid(String name, ResourceLocation st, ResourceLocation flowing)
        {
            super(name, st, flowing);
        }

        @Override
        public int getColor()
        {
            return 0xFFd742f4;
        }
    }

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

    @SubscribeEvent
    public static void eventItemRegistry(final RegistryEvent.Register<Item> event)
    {
        if (ENABLED)
        {
            event.getRegistry().register((new ItemBlock(SLIME_BLOCK)).setRegistryName(RES_LOC));
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void eventModelRegistry(final ModelRegistryEvent event)
    {
        if (ENABLED)
        {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SLIME_BLOCK), 0, new ModelResourceLocation(RES_LOC, "inventory"));
            ModelLoader.setCustomModelResourceLocation((new ItemBlock(SLIME_BLOCK)), 0, new ModelResourceLocation(RES_LOC, "inventory"));
        }
    }
}