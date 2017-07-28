package net.minecraftforge.debug;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

@Mod(modid = TileEntityLoadingTest.MODID, name = "TileEntity#onLoad() test mod", version = "1.0", acceptableRemoteVersions = "*")
public class TileEntityLoadingTest
{
    public static final boolean ENABLED = false;
    static final String MODID = "te_loading_test";
    static final boolean DEBUG = false;

    private static Logger logger;
    @ObjectHolder(TestBlock.NAME)
    private static final Block TEST_BLOCK = null;

    @Mod.EventBusSubscriber(modid = MODID)
    public static class Registration
    {
        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event)
        {
            if (!ENABLED) return;
            event.getRegistry().register(new TestBlock());
            GameRegistry.registerTileEntity(TestTE.class, (new ResourceLocation(MODID, TestBlock.NAME)).toString());
        }
        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            if (!ENABLED) return;
            event.getRegistry().register(new ItemBlock(TEST_BLOCK).setRegistryName(TEST_BLOCK.getRegistryName()));
        }
    }

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLED)
            logger = event.getModLog();
    }

    public static class TestBlock extends Block
    {
        static final String NAME = "test_block";

        TestBlock()
        {
            super(Material.ANVIL);
            setRegistryName(NAME);
            setUnlocalizedName(MODID + "." + NAME);
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
            logger.info("World: {}, Pos: {}, State: {}", world, pos, world.getBlockState(pos));
            if (DEBUG)
            {
                logger.trace("Stack trace:", new Exception());
            }
        }
    }
}
