package net.minecraftforge.debug;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = ForgeBlockStatesLoaderDebug.MODID)
public class ForgeBlockStatesLoaderDebug {
    public static final String MODID = "ForgeBlockStatesLoader";
    public static final String ASSETS = "forgeblockstatesloader:";
    
    public static final BlockHolder blockHolder = new BlockHolder();

    public static final Block blockCustom = new CustomMappedBlock();
    public static final String nameCustomWall = "custom_wall";
    public static final BlockWall blockCustomWall = new BlockWall(Blocks.cobblestone);
    public static final ItemMultiTexture itemCustomWall = new ItemMultiTexture(blockCustomWall, blockCustomWall, new Function<ItemStack, String>()
    {
        @Override
        public String apply(ItemStack stack)
        {
            return BlockWall.EnumType.byMetadata(stack.getMetadata()).getUnlocalizedName();
        }
    });

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {   
    	blockCustom.setUnlocalizedName(MODID + ".customBlock");
    	GameRegistry.registerBlock(blockCustom, "customBlock");
    	
        blockCustomWall.setUnlocalizedName(MODID + ".customWall");
        GameRegistry.registerBlock(blockCustomWall, null, nameCustomWall);
        GameRegistry.registerItem(itemCustomWall, nameCustomWall);
        GameData.getBlockItemMap().put(blockCustomWall, itemCustomWall);
        
        blockHolder.setUnlocalizedName(MODID + ".block_holder");
        blockHolder.setCreativeTab(CreativeTabs.tabBlock);
        GameRegistry.registerBlock(blockHolder, "block_holder");

        if (event.getSide() == Side.CLIENT)
            preInitClient(event);
    }
    
    @SideOnly(Side.CLIENT)
    public void preInitClient(FMLPreInitializationEvent event)
    {
    	ModelLoader.setCustomStateMapper(blockCustom, new StateMap.Builder().setProperty(CustomMappedBlock.VARIANT).build());
    	
        ModelLoader.setCustomStateMapper(blockCustomWall, new IStateMapper()
        {
            StateMap stateMap = new StateMap.Builder().setProperty(BlockWall.VARIANT).setBuilderSuffix("_wall").build();
            @Override
            public Map putStateModelLocations(Block block)
            {
                Map<IBlockState, ModelResourceLocation> map = (Map<IBlockState, ModelResourceLocation>) stateMap.putStateModelLocations(block);
                Map<IBlockState, ModelResourceLocation> newMap = Maps.newHashMap();
                
                for (Entry<IBlockState, ModelResourceLocation> e : map.entrySet())
                {
                    ModelResourceLocation loc = e.getValue();
                    newMap.put(e.getKey(), new ModelResourceLocation(ASSETS + loc.getResourcePath(), loc.getVariant()));
                }
                
                return newMap;
            }
        });
        Item customWallItem = Item.getItemFromBlock(blockCustomWall);
        ModelLoader.setCustomModelResourceLocation(customWallItem, 0, new ModelResourceLocation(ASSETS + "cobblestone_wall", "inventory"));
        ModelLoader.setCustomModelResourceLocation(customWallItem, 1, new ModelResourceLocation(ASSETS + "mossy_cobblestone_wall", "inventory"));
        ModelBakery.addVariantName(customWallItem, ASSETS + "cobblestone_wall", ASSETS + "mossy_cobblestone_wall");
    }

    // this block is never actually used, it's only needed for the error message on load to see the variant it maps to
    public static class CustomMappedBlock extends Block {
    	public static final PropertyEnum VARIANT = PropertyEnum.create("type", CustomVariant.class);

		protected CustomMappedBlock() {
			super(Material.rock);
			
			this.setUnlocalizedName(MODID + ".customMappedBlock");
		}

		@Override
		protected BlockState createBlockState() {
			return new BlockState(this,  VARIANT);
		}

		@Override
		public int getMetaFromState(IBlockState state)
		{
		    return ((CustomVariant)state.getValue(VARIANT)).ordinal();
		}

		@Override
		public IBlockState getStateFromMeta(int meta)
		{
		    if(meta > CustomVariant.values().length || meta < 0)
		        meta = 0;
		    
		    return this.getDefaultState().withProperty(VARIANT, CustomVariant.values()[meta]);
		}

		public static enum CustomVariant implements IStringSerializable {
			TypeA,
			TypeB;
			
			public String getName() { return this.toString(); };
		}
    }

    public static class BlockHolder extends Block {
        public static final PropertyEnum FACING = PropertyEnum.create("facing", EnumFacing.class, EnumFacing.HORIZONTALS);
        public static final PropertyEnum BLOCK_TYPE = PropertyEnum.create("block_type", BlockType.class);

        public BlockHolder() {
            super(Material.iron);
        }

        @Override
        protected BlockState createBlockState()
        {
            return new BlockState(this, FACING, BLOCK_TYPE);
        }

        @Override
        public int getMetaFromState(IBlockState state)
        {
            EnumFacing face = (EnumFacing) state.getValue(FACING);
            BlockType type = (BlockType) state.getValue(BLOCK_TYPE);
            return face.ordinal() - 2 + type.ordinal() * 4;
        }

        @Override
        public IBlockState getStateFromMeta(int meta)
        {
            if(meta > 15 || meta < 0)
                meta = 0;

            EnumFacing face = EnumFacing.getHorizontal(meta % 4);
            BlockType type = BlockType.values()[meta / 4];

            return this.getDefaultState().withProperty(FACING, face).withProperty(BLOCK_TYPE, type);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public EnumWorldBlockLayer getBlockLayer()
        {
            return EnumWorldBlockLayer.CUTOUT;
        }

        @Override
        public boolean isOpaqueCube()
        {
            return false;
        }

        @Override
        public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
        {
            if (worldIn.isRemote) {return false;}

            if (playerIn.isSneaking())
            {
                worldIn.setBlockState(pos, state.cycleProperty(FACING));
                return true;
            }

            worldIn.setBlockState(pos, state.cycleProperty(BLOCK_TYPE));

            return true;
        }

        public static enum BlockType implements IStringSerializable {
            NONE,
            IRON,
            GOLD,
            DIAMOND;

            @Override
            public String getName() { return this.toString(); }
        }
    }
}
