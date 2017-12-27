package net.minecraftforge.debug;

import net.minecraft.block.BlockStone;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeHills;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "fluidplaceblocktest", name = "FluidPlaceBlockTest", version = "0.0.0", acceptableRemoteVersions = "*")
public class FluidPlaceBlockTest
{
    private static final boolean ENABLED = false;

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
        if (!ENABLED) return;
        MinecraftForge.EVENT_BUS.register(FluidPlaceBlockTest.class);
    }

    @SubscribeEvent @SuppressWarnings("unused")
    public static void onFluidPlaceBlockEvent(BlockEvent.FluidPlaceBlockEvent event)
    {
        if (event.getState().getBlock() == Blocks.OBSIDIAN) event.setState(Blocks.GOLD_BLOCK.getDefaultState());
        if (event.getState().getBlock() == Blocks.COBBLESTONE) event.setState(Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE));
        if (event.getState() == Blocks.STONE.getDefaultState()){
            Biome biome = event.getWorld().getBiome(event.getPos());
            if (biome instanceof BiomeHills) event.setState(Blocks.EMERALD_BLOCK.getDefaultState());
            else event.setState(Blocks.DIAMOND_BLOCK.getDefaultState());
        }
        if (event.getState().getBlock() == Blocks.FIRE) event.setState(Blocks.GLASS.getDefaultState());
    }
}
