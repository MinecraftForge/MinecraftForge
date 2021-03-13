package net.minecraftforge.debug.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(ConcretePowderConversionTest.MOD_ID)
@Mod.EventBusSubscriber
public class ConcretePowderConversionTest
{
    public static final String MOD_ID = "concrete_powder_conversion_test";

    @Mod.EventBusSubscriber(bus= Mod.EventBusSubscriber.Bus.MOD, modid = MOD_ID)
    private static class RegistryHandler
    {
        @SubscribeEvent
        public static void registerBlocks(final RegistryEvent.Register<Block> event)
        {
            event.getRegistry().register(new TestBlock());
        }
    }

    private static class TestBlock extends Block
    {
        public TestBlock()
        {
            super(Properties.from(Blocks.DIRT));
            this.setRegistryName(MOD_ID, "test_block");
        }

        @Override
        public boolean causesConcretePowderToSolidify(BlockState state, Direction side, IBlockReader world, BlockPos pos)
        {
            return true;
        }
    }
}
