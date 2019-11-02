package net.minecraftforge.debug.item;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.*;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nullable;

@Mod(EmptyModdedFluidBucketIntoBlockTest.MOD_ID)
public class EmptyModdedFluidBucketIntoBlockTest {
    static final String MOD_ID = "empty_modded_fluid_bucket_into_block_test";
    static final String FLUID_NAME = "test_magenta_water";
    static final String CORAL_NAME = "test_tube_coral";
    static final ResourceLocation waterStillTexture = new ResourceLocation("minecraft", "block/water_still");
    static final ResourceLocation waterFlowingTexture = new ResourceLocation("minecraft", "block/water_flow");

    private static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, MOD_ID);
    private static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS = new DeferredRegister<>(ForgeRegistries.FLUIDS, MOD_ID);

    @ObjectHolder("empty_modded_fluid_bucket_into_block_test:"+FLUID_NAME)
    public static final FlowingFluid MAGENTA_WATER = null;
    public static RegistryObject<FlowingFluid> MAGENTA_WATER_REG = FLUIDS.register(FLUID_NAME, () ->
            new ForgeFlowingFluid.Source(EmptyModdedFluidBucketIntoBlockTest.MAGENTA_WATER_PROPERTIES));

    public static RegistryObject<FlowingFluid> MAGENTA_WATER_FLOWING = FLUIDS.register(FLUID_NAME+"_flowing", () ->
            new ForgeFlowingFluid.Flowing(EmptyModdedFluidBucketIntoBlockTest.MAGENTA_WATER_PROPERTIES));

    public static RegistryObject<FlowingFluidBlock> MAGENTA_WATER_BLOCK = BLOCKS.register(FLUID_NAME+"_block", () ->
            new FlowingFluidBlock(MAGENTA_WATER_REG, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()));

    public static RegistryObject<Item> MAGENTA_WATER_BUCKET = ITEMS.register(FLUID_NAME+"_bucket", () ->
            new BucketItem(MAGENTA_WATER_REG, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(ItemGroup.MISC)));

    public static final RegistryObject<Block> TUBE_CORAL = BLOCKS.register(CORAL_NAME, () ->
            new ModdedCoralPlantBlock(Block.Properties.from(Blocks.TUBE_CORAL)));

    public static final ForgeFlowingFluid.Properties MAGENTA_WATER_PROPERTIES =
            new ForgeFlowingFluid.Properties(MAGENTA_WATER_REG, MAGENTA_WATER_FLOWING, FluidAttributes.builder(waterStillTexture, waterFlowingTexture).color(0xFFC354CD))
                    .bucket(MAGENTA_WATER_BUCKET).block(MAGENTA_WATER_BLOCK).canMultiply();

    static {
        ITEMS.register(CORAL_NAME, () -> new BlockItem(TUBE_CORAL.get(), new Item.Properties()));
    }

    public EmptyModdedFluidBucketIntoBlockTest() {
         IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::loadComplete);

        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        FLUIDS.register(modBus);
    }

    public void loadComplete(FMLLoadCompleteEvent event)
    {
        // some sanity checks
        BlockState state = Fluids.WATER.getDefaultState().getBlockState();
        BlockState state2 = Fluids.WATER.getAttributes().getBlock(null,null,Fluids.WATER.getDefaultState());
        Validate.isTrue(state.getBlock() == Blocks.WATER && state2 == state);
        ItemStack stack = Fluids.WATER.getAttributes().getBucket(new FluidStack(Fluids.WATER, 1));
        Validate.isTrue(stack.getItem() == Fluids.WATER.getFilledBucket());
    }
}

//==================== TEST BLOCK CLASS (CORAL) WITH CUSTOM FLUID FOR WATERLOGGED

class ModdedCoralPlantBlock extends Block implements IBucketPickupHandler, ILiquidContainer {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 15.0D, 14.0D);

    public ModdedCoralPlantBlock(Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(WATERLOGGED, true));
    }

    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
        return this.getDefaultState().with(WATERLOGGED, (ifluidstate.getFluid() == EmptyModdedFluidBucketIntoBlockTest.MAGENTA_WATER && ifluidstate.getLevel() == 8));
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.get(WATERLOGGED)) {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, EmptyModdedFluidBucketIntoBlockTest.MAGENTA_WATER, EmptyModdedFluidBucketIntoBlockTest.MAGENTA_WATER.getTickRate(worldIn));
        }
        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    public IFluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? EmptyModdedFluidBucketIntoBlockTest.MAGENTA_WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    //============= CoralPlantBlock =================



    //============= IBucketPickupHandler, ILiquidContainer ================

    @Override
    public Fluid pickupFluid(IWorld worldIn, BlockPos pos, BlockState state) {
        if(state.get(WATERLOGGED)) {
            worldIn.setBlockState(pos, state.with(WATERLOGGED, false), 3);
            return EmptyModdedFluidBucketIntoBlockTest.MAGENTA_WATER;
        } else {
            return Fluids.EMPTY;
        }
    }

    @Override
    public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
        return !state.get(WATERLOGGED) && fluidIn == EmptyModdedFluidBucketIntoBlockTest.MAGENTA_WATER;
    }

    @Override
    public boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, IFluidState fluidStateIn) {
        if(!state.get(WATERLOGGED)) {
            Fluid fluid = fluidStateIn.getFluid();
            if (fluid == EmptyModdedFluidBucketIntoBlockTest.MAGENTA_WATER) {
                if (!worldIn.isRemote()) {
                    worldIn.setBlockState(pos, state.with(WATERLOGGED, true), 3);
                    worldIn.getPendingFluidTicks().scheduleTick(pos, fluid, fluid.getTickRate(worldIn));
                }
                return true;
            }
        }
        return false;
    }
}