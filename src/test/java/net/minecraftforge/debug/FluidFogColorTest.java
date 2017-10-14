package net.minecraftforge.debug;

import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@net.minecraftforge.fml.common.Mod(modid=FluidFogColorTest.MODID, name="Test Mod", version="1.0.0", acceptedMinecraftVersions="*") 
@net.minecraftforge.fml.common.Mod.EventBusSubscriber 
public class FluidFogColorTest{
	static final boolean ENABLED = true; // enable mod
	static int color = 0xFFd742f4; // change value for testing

	static final String MODID = "fluidfogcolor";
	static final net.minecraft.util.ResourceLocation RL = new net.minecraft.util.ResourceLocation(MODID, "slime");
    static {if (ENABLED) {net.minecraftforge.fluids.FluidRegistry.enableUniversalBucket();}}
	public static final MF SF = new MF("slime", new net.minecraft.util.ResourceLocation(MODID,"slime_still"), new net.minecraft.util.ResourceLocation(MODID, "slime_flow"));
	public static class MF extends net.minecraftforge.fluids.Fluid{public MF(String name, net.minecraft.util.ResourceLocation st, net.minecraft.util.ResourceLocation flowing) {super(name, st, flowing);} @Override public int getColor() {return color;}}
    @ObjectHolder("slime") public static final net.minecraftforge.fluids.BlockFluidBase SB = null;
    @net.minecraftforge.fml.common.Mod.EventHandler public void ev(net.minecraftforge.fml.common.event.FMLPreInitializationEvent ev) {if (ENABLED) {net.minecraftforge.fluids.FluidRegistry.registerFluid(SF); net.minecraftforge.fluids.FluidRegistry.addBucketForFluid(SF);}}
    @net.minecraftforge.fml.common.eventhandler.SubscribeEvent public static void evb(final net.minecraftforge.event.RegistryEvent.Register<net.minecraft.block.Block> ev) {if (ENABLED) ev.getRegistry().register((new net.minecraftforge.fluids.BlockFluidClassic(SF, net.minecraft.block.material.Material.WATER)).setRegistryName(RL).setUnlocalizedName(RL.toString()));}
    @net.minecraftforge.fml.common.eventhandler.SubscribeEvent public static void evib(final net.minecraftforge.event.RegistryEvent.Register<net.minecraft.item.Item> ev) {if (ENABLED) {ev.getRegistry().register((new net.minecraft.item.ItemBlock(SB)).setRegistryName(RL));}} 
    @net.minecraftforge.fml.common.eventhandler.SubscribeEvent @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT) public static void evm(final net.minecraftforge.client.event.ModelRegistryEvent ev) {if (ENABLED) {net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(net.minecraft.item.Item.getItemFromBlock(SB), 0, new net.minecraft.client.renderer.block.model.ModelResourceLocation(RL, "inventory")); net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation((new net.minecraft.item.ItemBlock(SB)), 0, new net.minecraft.client.renderer.block.model.ModelResourceLocation(RL, "inventory"));}}
}

