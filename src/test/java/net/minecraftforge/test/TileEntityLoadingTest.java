package net.minecraftforge.test;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

@Mod(modid = TileEntityLoadingTest.MOD_ID, name = "TileEntity#onLoad() test mod", version = "1.0")
public class TileEntityLoadingTest
{
    static final String MOD_ID = "te_loading_test";
    static final boolean DEBUG = false;

    private static Logger logger;
    private static final Block testBlock = new TestBlock();

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        GameRegistry.register(testBlock);
        GameRegistry.register(new ItemBlock(testBlock).setRegistryName(testBlock.getRegistryName()));
        GameRegistry.registerTileEntity(TestTE.class, testBlock.getRegistryName().toString());
    }

    public static class TestBlock extends Block
    {
        static final String NAME = "test_block";

        TestBlock()
        {
            super(Material.ANVIL);
            setRegistryName(NAME);
            setUnlocalizedName(MOD_ID + "." + NAME);
            setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        }

        @Override
        public boolean hasTileEntity(IBlockState state)
        {
            return true;
        }

        @Nullable
        @Override
        public TileEntity createTileEntity(World world, IBlockState state)
        {
            return new TestTE();
        }
    }

    public static class TestTE extends TileEntity
    {
        @Override
        public void onLoad()
        {
            logger.info("World: " + world + ", Pos: " + pos + ", State: " + world.getBlockState(pos));
            if (DEBUG) logger.trace("Stack trace:", new Exception());
        }
    }
}
