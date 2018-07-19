package net.minecraftforge.debug.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid="test_full_ladder")
public class FullBlockLadderTest
{

    @EventHandler
    public static void onPreInit(FMLPreInitializationEvent e)
    {
        MinecraftForge.EVENT_BUS.register(BlockRegistry.class);
    }
     
    public static class BlockRegistry
    {
        @SubscribeEvent public static void onBlockRegistry(RegistryEvent.Register<Block> e) { e.getRegistry().register(new FullLadder()); }
    }
    
    public static class FullLadder extends Block
    {
        
        public FullLadder()
        {
            super(Material.ROCK);
            this.setRegistryName("test_full_ladder:the_ladder");
        }

        @Override
        public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity, EnumFacing side)
        {
            return true;
        }
        
    }
    
}