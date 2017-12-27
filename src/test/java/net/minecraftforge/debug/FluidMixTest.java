package net.minecraftforge.debug;

import net.minecraft.block.BlockStone;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeHills;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "fluidmixtest", name = "FluidMixTest", version = "0.0.0")
@Mod.EventBusSubscriber @SuppressWarnings("unused")
public class FluidMixTest
{
    @SubscribeEvent @SuppressWarnings("unused")
    public static void onFluidMixEvent(BlockEvent.FluidMixEvent event){
        if (event.getState().getBlock() == Blocks.OBSIDIAN) event.setBlock(Blocks.GOLD_BLOCK.getDefaultState());
        if (event.getState().getBlock() == Blocks.COBBLESTONE) event.setBlock(Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE));
        if (event.getState() == Blocks.STONE.getDefaultState()){
            Biome biome = event.getWorld().getBiome(event.getPos());
            if (biome instanceof BiomeHills) event.setBlock(Blocks.EMERALD_BLOCK.getDefaultState());
            else event.setBlock(Blocks.DIAMOND_BLOCK.getDefaultState());
        }
    }
}
