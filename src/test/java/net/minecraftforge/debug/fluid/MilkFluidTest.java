package net.minecraftforge.debug.fluid;

import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod(MilkFluidTest.MODID)
public class MilkFluidTest
{
    public static final String MODID = "milk_fluid_test";
    // if true, enables the fluid wrapper added by this test mod
    private static final boolean ENABLE_FLUID_WRAPPER = false;

    public static final ResourceLocation FLUID_TEXTURE = new ResourceLocation("minecraft:block/white_concrete");
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, MODID);

    private static ForgeFlowingFluid.Properties makeProperties()
    {
        return new ForgeFlowingFluid.Properties(milk, milk_flowing, FluidAttributes.builder(FLUID_TEXTURE, FLUID_TEXTURE))
            .bucket(Items.MILK_BUCKET.delegate).block(milk_block);
    }

    public static RegistryObject<FlowingFluid> milk = FLUIDS.register("milk", () ->
        new ForgeFlowingFluid.Source(makeProperties())
    );
    public static RegistryObject<FlowingFluid> milk_flowing = FLUIDS.register("flowing_milk", () ->
        new ForgeFlowingFluid.Flowing(makeProperties())
    );

    public static RegistryObject<FlowingFluidBlock> milk_block = BLOCKS.register("milk_block", () ->
        new FlowingFluidBlock(milk, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops())
    );

    public MilkFluidTest()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modEventBus);
        FLUIDS.register(modEventBus);
        MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, MilkFluidTest::initCapabilities);
        if (ENABLE_FLUID_WRAPPER) {
            MinecraftForge.EVENT_BUS.addListener(MilkFluidTest::useMilk);
        }
    }

    public static void initCapabilities(AttachCapabilitiesEvent<ItemStack> event)
    {
        ItemStack stack = event.getObject();
        if (stack.getItem() == Items.MILK_BUCKET)
        {
            event.addCapability(new ResourceLocation(MODID, "milk"), new FluidMilkBucketWrapper(stack));
        }
    }

    public static void useMilk(PlayerInteractEvent event)
    {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() == Items.MILK_BUCKET)
        {
            FluidUtil.getFluidContained(stack).ifPresent((fluid) -> event.getPlayer().sendStatusMessage(new StringTextComponent("Contains ").func_240702_b_(fluid.getFluid().getRegistryName().toString()), true));
        }
    }

    public static class FluidMilkBucketWrapper implements IFluidHandlerItem, ICapabilityProvider
    {
        private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);

        @Nonnull
        private ItemStack container;
        public FluidMilkBucketWrapper(@Nonnull ItemStack container)
        {
            this.container = container;
        }

        @Nonnull
        @Override
        public ItemStack getContainer()
        {
            return container;
        }

        @Override
        public int getTanks()
        {
            return 1;
        }

        @Nonnull
        private FluidStack getFluid()
        {
            return new FluidStack(milk.get(), FluidAttributes.BUCKET_VOLUME);
        }

        @Nonnull
        @Override
        public FluidStack getFluidInTank(int tank)
        {
            return getFluid();
        }

        @Override
        public int getTankCapacity(int tank)
        {
            return FluidAttributes.BUCKET_VOLUME;
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack)
        {
            return true;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action)
        {
            return 0;
        }

        @Nonnull
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action)
        {
            if (resource.getFluid() != milk.get())
            {
                return FluidStack.EMPTY;
            }
            return drain(resource.getAmount(), action);
        }

        @Nonnull
        @Override
        public FluidStack drain(int maxDrain, FluidAction action)
        {
            if (maxDrain < FluidAttributes.BUCKET_VOLUME)
            {
                return FluidStack.EMPTY;
            }
            if (action.execute())
            {
                container = new ItemStack(Items.BUCKET);
            }
            return getFluid();
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side)
        {
            return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(capability, holder);
        }
    }
}
