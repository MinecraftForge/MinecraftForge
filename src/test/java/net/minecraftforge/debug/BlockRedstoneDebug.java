package net.minecraftforge.debug;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = BlockRedstoneDebug.MODID)
public class BlockRedstoneDebug
{
    public static final String MODID = "ForgeDebugBlockRedstone";

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        GameRegistry.registerBlock(TestBlock.instance, ItemTestBlock.class, TestBlock.name);
    }

    public static enum TestBlockType implements IStringSerializable {

        NORMAL,
        POWERED;

        @Override
        public String getName()
        {
            return toString();
        }
    }

    public static class TestBlock extends Block
    {
        public static final String name = "custom_redstone_block";
        public static PropertyEnum<TestBlockType> property = PropertyEnum.create("type", TestBlockType.class);
        public static TestBlock instance = new TestBlock();

        public TestBlock()
        {
            super(Material.iron);
            setDefaultState(getDefaultState().withProperty(property, TestBlockType.NORMAL));
            setCreativeTab(CreativeTabs.tabBlock);
        }

        @Override
        public String getLocalizedName()
        {
            return name;
        }

        @Override
        protected BlockState createBlockState()
        {
            return new BlockState(this, property);
        }

        @Override
        public IBlockState getStateFromMeta(int meta)
        {
            return getDefaultState().withProperty(property, TestBlockType.values()[meta & 1]);
        }

        @Override
        public int getMetaFromState(IBlockState state)
        {
            return state.getValue(property).ordinal() & 1;
        }

        @Override
        public boolean canProvidePower(IBlockAccess world, BlockPos pos) {
            return world.getBlockState(pos).getValue(property) == TestBlockType.POWERED;
        }

        @Override
        public int isProvidingWeakPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side)
        {
            return state.getValue(property) == TestBlockType.NORMAL ? 0 : 15;
        }

        @Override
        public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list)
        {
            list.add(new ItemStack(item, 1, 0));
            list.add(new ItemStack(item, 1, 1));
        }
    }

    public static class ItemTestBlock extends ItemBlock {

        public ItemTestBlock(Block block)
        {
            super(block);
            setHasSubtypes(true);
            setMaxDamage(0);
        }

        @Override
        public int getMetadata(int meta)
        {
            return meta;
        }
    }
}
