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

@Mod(modid=FluidFogColorTest.MODID, name="Test Mod", version="1.0.0", acceptedMinecraftVersions="*") 
@EventBusSubscriber 
public class FluidFogColorTest{
	static final boolean ENABLED = false; // <-- enable mod
	static int color = 0xFFd742f4;        // <-- change value for testing

	static final String MODID = "fluidfogcolor";
	static final ResourceLocation RL = new ResourceLocation(MODID, "slime");
    static {if (ENABLED) {FluidRegistry.enableUniversalBucket();}}
	public static final MF SF = new MF("slime", new ResourceLocation(MODID,"slime_still"), new ResourceLocation(MODID, "slime_flow"));
	public static class MF extends Fluid{public MF(String name, ResourceLocation st, ResourceLocation flowing) {super(name, st, flowing);} @Override public int getColor() {return color;}}
    @ObjectHolder("slime") public static final BlockFluidBase SB = null;
    @EventHandler public void ev(FMLPreInitializationEvent ev) {if (ENABLED) {FluidRegistry.registerFluid(SF); FluidRegistry.addBucketForFluid(SF);}}
    @SubscribeEvent public static void evb(final RegistryEvent.Register<Block> ev) {if (ENABLED) ev.getRegistry().register((new BlockFluidClassic(SF, Material.WATER)).setRegistryName(RL).setUnlocalizedName(RL.toString()));}
    @SubscribeEvent public static void evib(final RegistryEvent.Register<Item> ev) {if (ENABLED) {ev.getRegistry().register((new ItemBlock(SB)).setRegistryName(RL));}} 
    @SubscribeEvent @SideOnly(Side.CLIENT) public static void evm(final ModelRegistryEvent ev) {if (ENABLED) {ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SB), 0, new ModelResourceLocation(RL, "inventory")); ModelLoader.setCustomModelResourceLocation((new ItemBlock(SB)), 0, new ModelResourceLocation(RL, "inventory"));}}
}

