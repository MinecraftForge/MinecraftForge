package net.minecraftforge.debug;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.model.ModelDynBucket;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.debug.ModelFluidDebug.TestFluid;
import net.minecraftforge.debug.ModelFluidDebug.TestGas;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid="DynBucketTest", version="0.1")
public class DynBucketTest {
    public static final Item dynBucket = new DynBucket();
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        GameRegistry.registerItem(dynBucket, "dynbucket");
        
        if(event.getSide().isClient()) {
            
            ModelLoader.setCustomMeshDefinition(dynBucket, new ItemMeshDefinition() {
                @Override
                public ModelResourceLocation getModelLocation(ItemStack stack) {
                  return ModelDynBucket.LOCATION;
                }
              });
            
            ModelBakery.addVariantName(dynBucket, "forge:dynbucket");
        }
        
        // register fluid containers
        int i = 0;
        registerFluidContainer(FluidRegistry.WATER, i++);
        registerFluidContainer(FluidRegistry.LAVA, i++);
        registerFluidContainer(FluidRegistry.getFluid(TestFluid.name), i++);
        registerFluidContainer(FluidRegistry.getFluid(TestGas.name), i++);
        
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    private void registerFluidContainer(Fluid fluid, int meta) {
        if(fluid == null)
            return;
        
        FluidStack fs = new FluidStack(fluid, FluidContainerRegistry.BUCKET_VOLUME);
        ItemStack stack = new ItemStack(dynBucket, 1, meta);
        
        FluidContainerRegistry.registerFluidContainer(fs, stack, new ItemStack(Items.bucket));
    }
    
    @SubscribeEvent
    public void onBucketFill(FillBucketEvent event) {
        IBlockState state = event.world.getBlockState(event.target.getBlockPos());
        if(state.getBlock() instanceof IFluidBlock) {
            Fluid fluid = ((IFluidBlock)state.getBlock()).getFluid();
            FluidStack fs = new FluidStack(fluid,  FluidContainerRegistry.BUCKET_VOLUME);
            
            ItemStack filled = FluidContainerRegistry.fillFluidContainer(fs, event.current);
            
            if(filled != null) {
                event.result = filled;
                event.setResult(Result.ALLOW);
            }
        }
    }
    
    public static class DynBucket extends Item {
        public DynBucket() {
            setUnlocalizedName("dynbucket");
            setMaxStackSize(1);
            setHasSubtypes(true);
            setCreativeTab(CreativeTabs.tabMisc);
        }
        
        @Override
        public void getSubItems(Item itemIn, CreativeTabs tab, List subItems)
        {
            for(int i = 0; i < 4; i++) {
                ItemStack bucket = new ItemStack(this, 1, i);
                if(FluidContainerRegistry.isFilledContainer(bucket))
                    subItems.add(bucket);
            }
        }
    }
}
