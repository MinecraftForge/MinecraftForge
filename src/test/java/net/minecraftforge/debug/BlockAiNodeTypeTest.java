package net.minecraftforge.debug;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.item.Item;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;
import java.util.Collections;

@Mod(modid = BlockAiNodeTypeTest.MOD_ID, name = "AiNodeTypeTest", version = "1.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class BlockAiNodeTypeTest
{
    static final String MOD_ID = "ai_node_type_test";
    static final boolean ENABLED = false;

    private static final Block TEST_BLOCK = new TestBlock();
    private static final Block TEST_THREAT_BLOCK = new TestThreatBlock();
    private static PathNodeType scaryType;

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Block> event)
    {
        if (ENABLED)
        {
            event.getRegistry().register(TEST_BLOCK);
            event.getRegistry().register(TEST_THREAT_BLOCK);
            scaryType = EnumHelper.addPathNodeType("SCARY_TYPE", -1f);
        }
    }

    @Mod.EventBusSubscriber(value = Side.CLIENT, modid = MOD_ID)
    public static class ClientEventHandler
    {
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            if (ENABLED)
            {
                ModelLoader.setCustomStateMapper(TEST_BLOCK, block -> Collections.emptyMap());
                ModelBakery.registerItemVariants(Item.getItemFromBlock(TEST_BLOCK));
                ModelLoader.setCustomStateMapper(TEST_THREAT_BLOCK, block -> Collections.emptyMap());
                ModelBakery.registerItemVariants(Item.getItemFromBlock(TEST_THREAT_BLOCK));
            }
        }
    }

    private static final class TestBlock extends Block
    {
        TestBlock()
        {
            super(Material.ROCK);
            setRegistryName("test_block");
        }

        @Override
        public PathNodeType getAiPathNodeType(IBlockState state, IBlockAccess world, BlockPos pos)
        {
            return PathNodeType.DOOR_OPEN;
        }
    }

    private static final class TestThreatBlock extends Block
    {
        TestThreatBlock()
        {
            super(Material.WOOD);
            setRegistryName("test_threat_block");
        }

        @Nullable
        @Override
        public PathNodeType getAiPathNodeType(IBlockState state, IBlockAccess world, BlockPos pos)
        {
            return scaryType;
        }

        @Override
        public PathNodeType getAiEnvironmentalPathNodeType(IBlockState state, IBlockAccess world, BlockPos pos, PathNodeType original)
        {
            return scaryType;
        }
    }
}
