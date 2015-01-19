package net.minecraftforge.debug;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.client.event.BlockModelRegisterEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/*
 * Do note that the functionality of this event is shown by the lack of
 * errors on startup, despite leaving the two states which the test property
 * can have undefined.
 */
@Mod(modid = BlockModelRegisterEventDebug.MODID, version = BlockModelRegisterEventDebug.VERSION)
public class BlockModelRegisterEventDebug 
{
    public static final String MODID = "ForgeDebugBlockModelRegisterEvent";
    public static final String VERSION = "1.0";
    
    public static final PropertyBlock block = new PropertyBlock();
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) 
    {
        GameRegistry.registerBlock(block, PropertyBlock.name);
        MinecraftForge.EVENT_BUS.register(new BlockModelRegisterEventHandler());
    }
    
    public static class BlockModelRegisterEventHandler
    {
    	@SubscribeEvent
    	public void onBlockModelRegister(BlockModelRegisterEvent event)
    	{
    		BlockModelShapes modelShapes = event.modelShapes;
    		
    		modelShapes.registerBlockWithStateMapper(block, (new StateMap.Builder()).addPropertiesToIgnore(new IProperty[] {PropertyBlock.TEST_PROP}).build());
    	}
    }
    
    public static class PropertyBlock extends Block
    {
        public static final String name = "property_block";
		public static final PropertyBool TEST_PROP = PropertyBool.create("test");
    	
		public PropertyBlock() 
		{
			super(Material.iron);
			
            this.setUnlocalizedName(MODID + ":" + name);
            this.setCreativeTab(CreativeTabs.tabBlock);
		}

		@Override
		public IBlockState getStateFromMeta(int meta)
		{
			return this.getDefaultState().withProperty(TEST_PROP, meta == 1);
		}

		@Override
		public int getMetaFromState(IBlockState state)
		{
			return (Boolean)state.getValue(TEST_PROP) ? 1 : 0;
		}

		@Override
		protected BlockState createBlockState()
		{
			return new BlockState(this, TEST_PROP);
		}
    }
}
