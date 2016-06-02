package net.minecraftforge.debug;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.debug.DynBucketTest.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = "forgedebugrecttextures", name = "Rectangle Textures", version = "0.0.0")
public class RectangleTextureDebug
{
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        Block block = new BlockRectTexture();
        GameRegistry.register(block);
        GameRegistry.register(new ItemBlock(block).setRegistryName("rectTextureTest").setHasSubtypes(true));
    }

    public static enum EnumRectTex implements IStringSerializable
    {
        SQUARE_STATIC, SQUARE_ANIM, RECT_STATIC, RECT_STATIC_TOO_SHORT, RECT_STATIC_EXTRA_HEIGHT, RECT_STATIC_ODD, RECT_ANIM, RECT_ANIM_TOO_SHORT, RECT_ANIM_EXTRA_HEIGHT, RECT_ANIM_ODD, RECT_ANIM_TOO_MANY_FRAMES, TALL_STATIC, TALL_ANIM;

        @Override
        public String getName()
        {
            return this.name().toLowerCase();
        }

    }

    public static class BlockRectTexture extends Block
    {

        public static PropertyEnum<EnumRectTex> prop = PropertyEnum.create("type", EnumRectTex.class);

        public BlockRectTexture()
        {
            super(Material.ROCK);
            setUnlocalizedName("rectTextureTest");
            setRegistryName("rectTextureTest");
            setCreativeTab(CreativeTabs.SEARCH);
        }

        @Override
        protected BlockStateContainer createBlockState()
        {
            return new BlockStateContainer(this, new IProperty[] { prop });
        }

        @Override
        public int getMetaFromState(IBlockState state)
        {
            return state.getValue(prop).ordinal();
        }

        @Override
        public IBlockState getStateFromMeta(int meta)
        {
            return this.getDefaultState().withProperty(prop, EnumRectTex.values()[meta]);
        }

        @Override
        public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
        {
            for (EnumRectTex e : EnumRectTex.values())
            {
                list.add(new ItemStack(itemIn, 1, e.ordinal()));
            }
        }

        @Override
        public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem,
                EnumFacing side, float hitX, float hitY, float hitZ)
        {
            return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ);
        }
    }
}
