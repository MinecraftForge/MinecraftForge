package net.minecraftforge.debug;

@net.minecraftforge.fml.common.Mod(modid=FluidBehaveLikeWater.MODID, name="Test Mod", version="1.0.0", acceptedMinecraftVersions="*") 
@net.minecraftforge.fml.common.Mod.EventBusSubscriber 
public class FluidBehaveLikeWater{
    static final boolean ENABLED = true; // <-- enable mod

    static final String MODID = "fluidbehaveslikewater";
    static final net.minecraft.util.ResourceLocation RL = new net.minecraft.util.ResourceLocation(MODID, "slime");
    static {if (ENABLED) {net.minecraftforge.fluids.FluidRegistry.enableUniversalBucket();}}
    public static final MF SF = new MF("slime", new net.minecraft.util.ResourceLocation(MODID,"slime_still"), new net.minecraft.util.ResourceLocation(MODID, "slime_flow"));
    public static class MF extends net.minecraftforge.fluids.Fluid{public MF(String name, net.minecraft.util.ResourceLocation st, net.minecraft.util.ResourceLocation flowing) {super(name, st, flowing);} @Override public int getColor() {return 0xFFd742f4;}}
    @net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder("slime") public static final net.minecraftforge.fluids.BlockFluidBase SB = null;
    @net.minecraftforge.fml.common.Mod.EventHandler public void ev(net.minecraftforge.fml.common.event.FMLPreInitializationEvent ev) {if (ENABLED) {net.minecraftforge.fluids.FluidRegistry.registerFluid(SF); net.minecraftforge.fluids.FluidRegistry.addBucketForFluid(SF);}}
    @net.minecraftforge.fml.common.eventhandler.SubscribeEvent public static void evb(final net.minecraftforge.event.RegistryEvent.Register<net.minecraft.block.Block> ev) {if (ENABLED) ev.getRegistry().register((new net.minecraftforge.fluids.BlockFluidClassic(SF, new net.minecraft.block.material.MaterialLiquid(net.minecraft.block.material.MapColor.GREEN))).setRegistryName(RL).setUnlocalizedName(RL.toString()));}
    @net.minecraftforge.fml.common.eventhandler.SubscribeEvent public static void evib(final net.minecraftforge.event.RegistryEvent.Register<net.minecraft.item.Item> ev) {if (ENABLED) {ev.getRegistry().register((new net.minecraft.item.ItemBlock(SB)).setRegistryName(RL));}} 
    @net.minecraftforge.fml.common.eventhandler.SubscribeEvent @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT) public static void evm(final net.minecraftforge.client.event.ModelRegistryEvent ev) {if (ENABLED) {net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(net.minecraft.item.Item.getItemFromBlock(SB), 0, new net.minecraft.client.renderer.block.model.ModelResourceLocation(RL, "inventory")); net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation((new net.minecraft.item.ItemBlock(SB)), 0, new net.minecraft.client.renderer.block.model.ModelResourceLocation(RL, "inventory"));}}
}