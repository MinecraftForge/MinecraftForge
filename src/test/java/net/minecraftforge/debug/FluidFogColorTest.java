package net.minecraftforge.debug;

import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

@Mod( modid = FluidFogColorTest.MODID, 
      name = FluidFogColorTest.MODNAME, 
      version = FluidFogColorTest.MODVERSION,
      acceptedMinecraftVersions = "[1.12]")
@Mod.EventBusSubscriber
public class FluidFogColorTest
{
	// use this to enable / disable the test mod
	public static final boolean ENABLED = true;
	
    public static final String MODID = "fluidfogcolor";
    public static final String MODNAME = "Fluid Fog Color Test";
    public static final String MODVERSION = "1.0.0";
    public static final String MODDESCRIPTION = "Testing fluid related stuff";
    public static final String MODAUTHOR = "jabelar";
    public static final String MODCREDITS = "";
    public static final String MODURL = "www.jabelarminecraft.blogspot.com";
    public static final String MODLOGO = "";
     
    static 
    {
    	if (ENABLED)
    	{
    		FluidRegistry.enableUniversalBucket();
    	}
    }

    @Instance(MODID)
    public static FluidFogColorTest instance;
        
    @SidedProxy(clientSide="net.minecraftforge.debug.FluidFogColorTest$ClientProxy", serverSide="net.minecraftforge.debug.FluidFogColorTest$CommonProxy")
    public static CommonProxy proxy;
               
    @Mod.EventHandler
    public void fmlLifeCycleEvent(FMLPreInitializationEvent event) 
    {   	
    	if (!ENABLED) { return; }
        proxy.fmlLifeCycleEvent(event);
    }

	@Mod.EventHandler
    public void fmlLifeCycleEvent(FMLInitializationEvent event) 
    {
    	if (!ENABLED) { return; }
        proxy.fmlLifeCycleEvent(event);
    }

	@Mod.EventHandler
    public void fmlLifeCycle(FMLPostInitializationEvent event) 
	{
    	if (!ENABLED) { return; }
        proxy.fmlLifeCycleEvent(event);
    }
    
	public static class CommonProxy 
	{
	    public void fmlLifeCycleEvent(FMLPreInitializationEvent event)
	    {     	
            FluidRegistry.registerFluid(ModFluids.SLIME);
            FluidRegistry.addBucketForFluid(ModFluids.SLIME);
        }
	    
	    public void fmlLifeCycleEvent(FMLInitializationEvent event)
	    {

	    }

	    public void fmlLifeCycleEvent(FMLPostInitializationEvent event)
	    {
	        
	    }
	}
	
	public static class ClientProxy extends CommonProxy 
	{
	}
	
	public static class ModFluid extends Fluid
	{
        public ModFluid(String fluidName, ResourceLocation still, ResourceLocation flowing)
        {
            super(fluidName, still, flowing);
        }
	    
        @Override
        public int getColor()
        {
            return 0xFF00FF00;
        }
	}
	
	public static class ModFluids 
	{
		public static final Fluid SLIME = new ModFluid(
				"slime", 
				new ResourceLocation(FluidFogColorTest.MODID,"slime_still"), 
				new ResourceLocation(FluidFogColorTest.MODID, "slime_flow")
				)
				.setDensity(1100)
				.setGaseous(false)
				.setLuminosity(9)
				.setViscosity(25000)
				.setTemperature(300)
				.setEmptySound(SoundEvents.ITEM_BUCKET_EMPTY)
				.setFillSound(SoundEvents.ITEM_BUCKET_FILL)
				;
	} 
	
   public static class ModBlocks 
    {
        /*
         * fluid blocks
         * Make sure you set registry name here
         */
        public static final BlockFluidBase SLIME_BLOCK = (BlockFluidBase) Utilities.setBlockName(new BlockFluidClassic(ModFluids.SLIME, Material.WATER), "slime");
        
        public static final Set<Block> SET_BLOCKS = ImmutableSet.of(
                SLIME_BLOCK
                );
        public static final Set<ItemBlock> SET_ITEM_BLOCKS = ImmutableSet.of(
                new ItemBlock(SLIME_BLOCK)
                );

        /**
         * Initialize this mod's {@link Block}s with any post-registration data.
         */
        private static void initialize() 
        {
        }

        @Mod.EventBusSubscriber(modid = FluidFogColorTest.MODID)
        public static class RegistrationHandler 
        {

            /**
             * Register this mod's {@link Block}s.
             *
             * @param event The event
             */
            @SubscribeEvent
            public static void onEvent(final RegistryEvent.Register<Block> event) 
            {
                if (!ENABLED) { return; }
                
                final IForgeRegistry<Block> registry = event.getRegistry();

                for (final Block block : SET_BLOCKS) {
                    registry.register(block);
                    // DEBUG
                    System.out.println("Registering block: "+block.getRegistryName());
                }
                
                initialize();
            }

            /**
             * Register this mod's {@link ItemBlock}s.
             *
             * @param event The event
             */
            @SubscribeEvent
            public static void registerItemBlocks(final RegistryEvent.Register<Item> event) 
            {
                if (!ENABLED) { return; }
                
                final IForgeRegistry<Item> registry = event.getRegistry();

                for (final ItemBlock item : SET_ITEM_BLOCKS) {
                    final Block block = item.getBlock();
                    final ResourceLocation registryName = Preconditions.checkNotNull(block.getRegistryName(), "Block %s has null registry name", block);
                    registry.register(item.setRegistryName(registryName));
                    // DEBUG
                    System.out.println("Registering Item Block for "+registryName);         }
            }       
            
            /**
             * On model event.
             *
             * @param event the event
             */
            @SubscribeEvent
            @SideOnly(Side.CLIENT)
            public static void onModelEvent(final ModelRegistryEvent event) 
            {
                if (!ENABLED) { return; }
                
                //DEBUG
                System.out.println("Registering block models");
                
                registerBlockModels();
                registerItemBlockModels();
            }
        }   
        
        /**
         * Register block models.
         */
        @SideOnly(Side.CLIENT)
        public static void registerBlockModels()
        {
            for (final Block block : SET_BLOCKS) {
                registerBlockModel(block);
                // DEBUG
                System.out.println("Registering block model for"
                        + ": "+block.getRegistryName());
            }        
        }
        
        /**
         * Register block model.
         *
         * @param parBlock the par block
         */
        @SideOnly(Side.CLIENT)
        public static void registerBlockModel(Block parBlock)
        {
            registerBlockModel(parBlock, 0);
        }
        
        /**
         * Register block model.
         *
         * @param parBlock the par block
         * @param parMetaData the par meta data
         */
        @SideOnly(Side.CLIENT)
        public static void registerBlockModel(Block parBlock, int parMetaData)
        {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(parBlock), parMetaData, new ModelResourceLocation(FluidFogColorTest.MODID + ":" + parBlock.getUnlocalizedName().substring(5), "inventory"));
        }

        
        /**
         * Register block models.
         */
        @SideOnly(Side.CLIENT)
        public static void registerItemBlockModels()
        {
            for (final ItemBlock block : SET_ITEM_BLOCKS) {
                registerItemBlockModel(block);
                // DEBUG
                System.out.println("Registering item block model for"
                        + ": "+block.getRegistryName());
            }        
        }
        
        /**
         * Register block model.
         *
         * @param parBlock the par block
         */
        @SideOnly(Side.CLIENT)
        public static void registerItemBlockModel(ItemBlock parBlock)
        {
            registerItemBlockModel(parBlock, 0);
        }
        
        /**
         * Register block model.
         *
         * @param parBlock the par block
         * @param parMetaData the par meta data
         */
        @SideOnly(Side.CLIENT)
        public static void registerItemBlockModel(ItemBlock parBlock, int parMetaData)
        {
            ModelLoader.setCustomModelResourceLocation(parBlock, parMetaData, new ModelResourceLocation(FluidFogColorTest.MODID + ":" + parBlock.getUnlocalizedName().substring(5), "inventory"));
        }
    }
   
   public static class Utilities 
   { 
       // Need to call this on item instance prior to registering the item
       /**
        * Sets the item name.
        *
        * @param parItem the par item
        * @param parItemName the par item name
        * @return the item
        */
       // chainable
       public static Item setItemName(Item parItem, String parItemName) {
           parItem.setRegistryName(parItemName);
           parItem.setUnlocalizedName(parItemName);
           return parItem;
          } 
       
       // Need to call this on block instance prior to registering the block
       /**
        * Sets the block name.
        *
        * @param parBlock the par block
        * @param parBlockName the par block name
        * @return the block
        */
       // chainable
       public static Block setBlockName(Block parBlock, String parBlockName) 
       {
           parBlock.setRegistryName(parBlockName);
           parBlock.setUnlocalizedName(parBlockName);
           return parBlock;
       }   
   }
}

