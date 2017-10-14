package net.minecraftforge.debug;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
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

@Mod(modid = FluidAdditionalFieldsTest.MODID, name = "Test Mod", version = "1.0.0", acceptedMinecraftVersions = "*")
@EventBusSubscriber
public class FluidAdditionalFieldsTest
{
    static final boolean ENABLED = false; // <-- enable mod
    static final int COLOR = 0xFFd742f4; // <-- change value for testing

    static final String MODID = "fluidadditionalfields";
    static final ResourceLocation RES_LOC = new ResourceLocation(MODID, "slime");
    static
    {
        if (ENABLED)
        {
            FluidRegistry.enableUniversalBucket();
        }
    }
    public static final Fluid SLIME = new Fluid("slime", new ResourceLocation(MODID, "slime_still"), new ResourceLocation(MODID, "slime_flow")).setColor(COLOR);
    @ObjectHolder("slime")
    public static final BlockFluidBase SLIME_BLOCK = null;

    @EventHandler
    public void ev(FMLPreInitializationEvent event)
    {
        if (ENABLED)
        {
            FluidRegistry.registerFluid(SLIME);
            FluidRegistry.addBucketForFluid(SLIME);
        }
    }

    @SubscribeEvent
    public static void evb(final RegistryEvent.Register<Block> event)
    {
        if (ENABLED)
            event.getRegistry().register((new BlockFluidClassic(SLIME, Material.WATER)).setRegistryName(RES_LOC).setUnlocalizedName(RES_LOC.toString()));
    }

    @SubscribeEvent
    public static void evib(final RegistryEvent.Register<Item> event)
    {
        if (ENABLED)
        {
            event.getRegistry().register((new ItemBlock(SLIME_BLOCK)).setRegistryName(RES_LOC));
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void evm(final ModelRegistryEvent event)
    {
        if (ENABLED)
        {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SLIME_BLOCK), 0, new ModelResourceLocation(RES_LOC, "inventory"));
            ModelLoader.setCustomModelResourceLocation((new ItemBlock(SLIME_BLOCK)), 0, new ModelResourceLocation(RES_LOC, "inventory"));
        }
    }
}
