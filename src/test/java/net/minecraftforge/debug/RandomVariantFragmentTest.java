package net.minecraftforge.debug;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@Mod(modid = "random_variant_fragment_test", name = "Random Variant Fragment Test", version = "0.0.0.0")
@EventBusSubscriber
public class RandomVariantFragmentTest
{
    public static final PropertyBool PROP_A = PropertyBool.create("a");
    public static final PropertyBool PROP_B = PropertyBool.create("b");
    public static final Block RVFT_BLOCK = new Block(Material.ROCK)
    {
        {
            setDefaultState(getDefaultState().withProperty(PROP_A, false).withProperty(PROP_B, false));
            setUnlocalizedName("rvft");
            setRegistryName("rvft");
        }

        @Override
        public BlockStateContainer createBlockState()
        {
            return new BlockStateContainer(this, PROP_A, PROP_B);
        }

        @Override
        public int getMetaFromState(IBlockState state)
        {
            return (state.getValue(PROP_A) ? 0b0001 : 0) | (state.getValue(PROP_B) ? 0b0010 : 0);
        }

        @Override
        public IBlockState getStateFromMeta(int meta)
        {
            return getDefaultState().withProperty(PROP_A, (meta & 0b0001) != 0).withProperty(PROP_B, (meta & 0b0010) != 0);
        }

        @Override
        public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
        {
            return getStateFromMeta(((int)pos.toLong()));
        }

        @Override
        public BlockRenderLayer getBlockLayer()
        {
            return BlockRenderLayer.TRANSLUCENT;
        }
    };

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> e)
    {
        e.getRegistry().register(RVFT_BLOCK);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> e)
    {
        e.getRegistry().register(new ItemBlock(RVFT_BLOCK).setRegistryName(RVFT_BLOCK.getRegistryName()));
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent e)
    {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RVFT_BLOCK), 0, new ModelResourceLocation(RVFT_BLOCK.getRegistryName(), "inventory"));
    }
}
