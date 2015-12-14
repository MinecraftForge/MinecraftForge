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
import net.minecraft.util.ResourceLocation;
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
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid="DynBucketTest", version="0.1", dependencies = "after:" + ModelFluidDebug.MODID)
public class DynBucketTest {
    public static final Item dynBucket = new DynBucket();
    public static final Item dynBottle = new DynBottle();

    @SidedProxy(clientSide = "net.minecraftforge.debug.DynBucketTest$ClientProxy", serverSide = "net.minecraftforge.debug.DynBucketTest$CommonProxy")
    public static CommonProxy proxy;

    public static class CommonProxy {
        void setupModels() {}
    }

    public static class ClientProxy extends CommonProxy {

        @Override
        void setupModels() {
            ModelLoader.setCustomMeshDefinition(dynBucket, new ItemMeshDefinition() {
                @Override
                public ModelResourceLocation getModelLocation(ItemStack stack) {
                    return ModelDynBucket.LOCATION;
                }
            });

            ModelBakery.addVariantName(dynBucket, "forge:dynbucket");

            ModelLoader.setCustomMeshDefinition(dynBottle, new ItemMeshDefinition() {
                @Override
                public ModelResourceLocation getModelLocation(ItemStack stack) {
                    return new ModelResourceLocation(new ResourceLocation("forge", "dynbottle"), "inventory");
                }
            });

            ModelBakery.addVariantName(dynBottle, "forge:dynbottle");
        }
    }


    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        GameRegistry.registerItem(dynBucket, "dynbucket");
        GameRegistry.registerItem(dynBottle, "dynbottle");

        // register fluid containers
        int i = 0;
        //registerFluidContainer(FluidRegistry.WATER, i++);
        //registerFluidContainer(FluidRegistry.LAVA, i++);
        registerFluidContainer(FluidRegistry.getFluid(TestFluid.name), i++);
        registerFluidContainer(FluidRegistry.getFluid(TestGas.name), i++);

        i = 0;
        registerFluidContainer2(FluidRegistry.WATER, i++);
        registerFluidContainer2(FluidRegistry.LAVA, i++);
        registerFluidContainer2(FluidRegistry.getFluid(TestFluid.name), i++);
        registerFluidContainer2(FluidRegistry.getFluid(TestGas.name), i++);

        // Set TestFluidBlocks blockstate to use milk instead of testfluid for the texture to be loaded
        FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluid("milk"), new ItemStack(Items.milk_bucket), FluidContainerRegistry.EMPTY_BUCKET);



        proxy.setupModels();
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    private void registerFluidContainer(Fluid fluid, int meta) {
        if(fluid == null)
            return;
        
        FluidStack fs = new FluidStack(fluid, FluidContainerRegistry.BUCKET_VOLUME);
        ItemStack stack = new ItemStack(dynBucket, 1, meta);
        
        FluidContainerRegistry.registerFluidContainer(fs, stack, new ItemStack(Items.bucket));
    }

    private void registerFluidContainer2(Fluid fluid, int meta) {
        if(fluid == null)
            return;

        FluidStack fs = new FluidStack(fluid, 250);
        ItemStack stack = new ItemStack(dynBottle, 1, meta);

        FluidContainerRegistry.registerFluidContainer(fs, stack, new ItemStack(Items.glass_bottle));
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

    public static class DynBottle extends Item {
        public DynBottle() {
            setUnlocalizedName("dynbottle");
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
