package net.minecraftforge.debug;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;

@Mod(modid = "fluid_ingredient_test", name = "FluidIngredient Test", version = "0.0.0")
@Mod.EventBusSubscriber
public final class FluidIngredientTest
{

    static final boolean ENABLED = false;

    @GameRegistry.ObjectHolder("fluid_ingredient_test:dough")
    public static final Item DOUGH = null;

    @GameRegistry.ObjectHolder("fluid_ingredient_test:quarter_bucket")
    public static final Item QUARTER_BUCKET = null;

    @GameRegistry.ObjectHolder("fluid_ingredient_test:big_bucket")
    public static final Item BIG_BUCKET = null;

    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(
                new Item().setUnlocalizedName("test_dough").setCreativeTab(CreativeTabs.MISC).setRegistryName("fluid_ingredient_test:dough"),
                new TestWaterBucket(250, false).setCreativeTab(CreativeTabs.MISC).setUnlocalizedName("quarter_bucket").setRegistryName("fluid_ingredient_test:quarter_bucket"),
                new TestWaterBucket(2000, false).setCreativeTab(CreativeTabs.MISC).setUnlocalizedName("big_bucket").setRegistryName("fluid_ingredient_test:big_bucket")
        );
    }

    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event)
    {
        ModelLoader.setCustomModelResourceLocation(DOUGH, 0, new ModelResourceLocation("minecraft:snowball", "inventory"));
        ModelLoader.setCustomModelResourceLocation(QUARTER_BUCKET, 0, new ModelResourceLocation("minecraft:water_bucket", "inventory"));
        ModelLoader.setCustomModelResourceLocation(BIG_BUCKET, 0, new ModelResourceLocation("minecraft:water_bucket", "inventory"));
    }

    static class TestWaterBucket extends Item
    {
        final int capacity;
        private final boolean destroyContainer;

        public TestWaterBucket(int capacity, boolean destroy)
        {
            this.capacity = capacity;
            this.destroyContainer = destroy;
        }

        @Override
        public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list)
        {
            if (this.isInCreativeTab(tab))
            {
                ItemStack stack = new ItemStack(this);
                IFluidHandlerItem handler = FluidUtil.getFluidHandler(stack);
                handler.fill(new FluidStack(FluidRegistry.WATER, Integer.MAX_VALUE), true);
                list.add(stack);
            }
        }

        public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
        {
            return destroyContainer ?
                    new FluidHandlerItemStack.Consumable(stack, capacity) :
                    new FluidHandlerItemStack.SwapEmpty(stack, new ItemStack(Items.BUCKET), capacity);
        }
    }
}
