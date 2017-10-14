package net.minecraftforge.debug;

@net.minecraftforge.fml.common.Mod(modid=FluidAdditionalFieldsTest.MODID, name="Test Mod", version="1.0.0", acceptedMinecraftVersions="*") 
@net.minecraftforge.fml.common.Mod.EventBusSubscriber 
public class FluidAdditionalFieldsTest{
	static final boolean ENABLED = false; // <-- enable mod
	static final int COLOR = 0xFFd742f4;  // <-- change value for testing

	static final String MODID = "fluidadditionalfields";
	static final net.minecraft.util.ResourceLocation RL = new net.minecraft.util.ResourceLocation(MODID, "slime");
    static {if (ENABLED) {net.minecraftforge.fluids.FluidRegistry.enableUniversalBucket();}}
	public static final net.minecraftforge.fluids.Fluid SF = new net.minecraftforge.fluids.Fluid("slime", new net.minecraft.util.ResourceLocation(MODID,"slime_still"), new net.minecraft.util.ResourceLocation(MODID, "slime_flow")).setColor(COLOR);
    @net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder("slime") public static final net.minecraftforge.fluids.BlockFluidBase SB = null;
    @net.minecraftforge.fml.common.Mod.EventHandler public void ev(net.minecraftforge.fml.common.event.FMLPreInitializationEvent ev) {if (ENABLED) {net.minecraftforge.fluids.FluidRegistry.registerFluid(SF); net.minecraftforge.fluids.FluidRegistry.addBucketForFluid(SF);}}
    @net.minecraftforge.fml.common.eventhandler.SubscribeEvent public static void evb(final net.minecraftforge.event.RegistryEvent.Register<net.minecraft.block.Block> ev) {if (ENABLED) ev.getRegistry().register((new net.minecraftforge.fluids.BlockFluidClassic(SF, net.minecraft.block.material.Material.WATER)).setRegistryName(RL).setUnlocalizedName(RL.toString()));}
    @net.minecraftforge.fml.common.eventhandler.SubscribeEvent public static void evib(final net.minecraftforge.event.RegistryEvent.Register<net.minecraft.item.Item> ev) {if (ENABLED) {ev.getRegistry().register((new net.minecraft.item.ItemBlock(SB)).setRegistryName(RL));}} 
    @net.minecraftforge.fml.common.eventhandler.SubscribeEvent @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT) public static void evm(final net.minecraftforge.client.event.ModelRegistryEvent ev) {if (ENABLED) {net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(net.minecraft.item.Item.getItemFromBlock(SB), 0, new net.minecraft.client.renderer.block.model.ModelResourceLocation(RL, "inventory")); net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation((new net.minecraft.item.ItemBlock(SB)), 0, new net.minecraft.client.renderer.block.model.ModelResourceLocation(RL, "inventory"));}}
}
