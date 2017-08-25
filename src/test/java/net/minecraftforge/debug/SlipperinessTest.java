package net.minecraftforge.debug;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "slipperiness_test", name = "Slipperiness Test", version = "0.0.0")
@EventBusSubscriber
public class SlipperinessTest
{
    public static final Block BB_BLOCK = new Block(Material.PACKED_ICE)
    {
        @Override
        public float getSlipperiness(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity)
        {
            return entity instanceof EntityBoat ? 2 : super.getSlipperiness(state, world, pos, entity);
        }
    }.setUnlocalizedName("boat_blaster").setRegistryName("boat_blaster");

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> e)
    {
        e.getRegistry().register(BB_BLOCK);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> e)
    {
        e.getRegistry().register(new ItemBlock(BB_BLOCK).setRegistryName(BB_BLOCK.getRegistryName()));
    }
}

