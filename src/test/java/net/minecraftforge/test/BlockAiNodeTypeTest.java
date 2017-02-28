package net.minecraftforge.test;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "ainodetypetest", name="AiNodeTypeTest", version = "1.0")
@Mod.EventBusSubscriber
public class BlockAiNodeTypeTest
{

    private static final Block TEST_BLOCK = new TestBlock();

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().register(TEST_BLOCK);
    }

    private static final class TestBlock extends Block {

        TestBlock() {
            super(Material.ROCK);
            setRegistryName("testblock");
        }

        @Nullable
        @Override
        public PathNodeType getAiPathNodeType(IBlockState state, IBlockAccess world, BlockPos pos)
        {
            return PathNodeType.DOOR_OPEN;
        }
    }

}
