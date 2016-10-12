package net.minecraftforge.debug;

import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

@Mod(modid = ForgeBlockStatesLoaderDebug.MODID)
public class ForgeBlockStatesLoaderDebug {
    public static final String MODID = "ForgeBlockStatesLoader";
    public static final String ASSETS = "forgeblockstatesloader:";

    public static final Block blockCustom = new CustomMappedBlock();
    public static final String nameCustomWall = "custom_wall";
    public static final BlockWall blockCustomWall = new BlockWall(Blocks.cobblestone);
    public static final ItemMultiTexture itemCustomWall = (ItemMultiTexture)new ItemMultiTexture(blockCustomWall, blockCustomWall, new Function<ItemStack, String>()
    {
        @Override
        public String apply(ItemStack stack)
        {
            return BlockWall.EnumType.byMetadata(stack.getMetadata()).getUnlocalizedName();
        }
    }).setRegistryName(nameCustomWall);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        blockCustom.setUnlocalizedName(MODID + ".customBlock").setRegistryName("customBlock");
        GameRegistry.registerBlock(blockCustom);

        blockCustomWall.setUnlocalizedName(MODID + ".customWall").setRegistryName(nameCustomWall);
        GameRegistry.registerBlock(blockCustomWall, (Class<? extends ItemBlock>)null);
        GameRegistry.registerItem(itemCustomWall);
        GameData.getBlockItemMap().put(blockCustomWall, itemCustomWall);

        if (event.getSide() == Side.CLIENT)
            preInitClient(event);
    }

    @SideOnly(Side.CLIENT)
    public void preInitClient(FMLPreInitializationEvent event)
    {
        ModelLoader.setCustomStateMapper(blockCustom, new StateMap.Builder().withName(CustomMappedBlock.VARIANT).build());

        ModelLoader.setCustomStateMapper(blockCustomWall, new IStateMapper()
        {
            StateMap stateMap = new StateMap.Builder().withName(BlockWall.VARIANT).withSuffix("_wall").build();
            @Override
            public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block block)
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
    }

    // this block is never actually used, it's only needed for the error message on load to see the variant it maps to
    public static class CustomMappedBlock extends Block
    {
        public static final PropertyEnum<CustomVariant> VARIANT = PropertyEnum.create("type", CustomVariant.class);

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
}
