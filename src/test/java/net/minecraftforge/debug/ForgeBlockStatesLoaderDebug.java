package net.minecraftforge.debug;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockWall;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
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
        blockCustomWall.setUnlocalizedName(MODID + ".customWall");
        GameRegistry.registerBlock(blockCustomWall, null, nameCustomWall);
        GameRegistry.registerItem(itemCustomWall, nameCustomWall);
        GameData.getBlockItemMap().put(blockCustomWall, itemCustomWall);
        
        if (event.getSide() == Side.CLIENT)
            preInitClient(event);
    }
    
    @SideOnly(Side.CLIENT)
    public void preInitClient(FMLPreInitializationEvent event)
    {
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
}
