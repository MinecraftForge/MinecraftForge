package net.minecraftforge.debug.block;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Random;

@Mod(BubbleSourceTest.MODID)
public class BubbleSourceTest {
    static final String MODID = "bubble_source_test";
    static final String ELEVATOR_UP_BLOCK_NAME = "modded_soul_sand_test";
    static final String ELEVATOR_DOWN_BLOCK_NAME = "modded_magma_block_test";
    static final String TRAPDOOR_ELEVATOR_NAME = "trapdoor_and_bubble_elevator_test";

    private static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<ModdedSoulSandBlock> MODDED_SOUL_SAND = BLOCKS.register(ELEVATOR_UP_BLOCK_NAME, () ->
            new ModdedSoulSandBlock(Block.Properties.from(Blocks.SOUL_SAND), Direction.UP));
    public static final RegistryObject<ModdedMagmaBlock> MODDED_MAGMA_BLOCK = BLOCKS.register(ELEVATOR_DOWN_BLOCK_NAME, () ->
            new ModdedMagmaBlock(Block.Properties.from(Blocks.MAGMA_BLOCK), Direction.DOWN));
    public static final RegistryObject<TrapDoorAndBubbleColumnInOne> MODDED_TRAPDOOR = BLOCKS.register(TRAPDOOR_ELEVATOR_NAME, () ->
            new TrapDoorAndBubbleColumnInOne(Block.Properties.from(Blocks.OAK_TRAPDOOR)));

    static {
        ITEMS.register(ELEVATOR_UP_BLOCK_NAME, () -> new BlockItem(MODDED_SOUL_SAND.get(), new Item.Properties()));
        ITEMS.register(ELEVATOR_DOWN_BLOCK_NAME, () -> new BlockItem(MODDED_MAGMA_BLOCK.get(), new Item.Properties()));
        ITEMS.register(TRAPDOOR_ELEVATOR_NAME, () -> new BlockItem(MODDED_TRAPDOOR.get(), new Item.Properties()));
    }

    public BubbleSourceTest () {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
    }
}
// ===================================================================================================================
class ModdedSoulSandBlock extends SoulSandBlock {
    private final Direction pushDirection;

    public ModdedSoulSandBlock(Properties properties, Direction pushDirection) {
        super(properties);
        this.pushDirection = pushDirection;
    }

    @Override
    public boolean isValidBubbleElevatorRelay(BlockState state) {
        return true;
    }

    @Override
    public Direction getBubbleElevatorDirection(BlockState state) {
        return pushDirection;
    }
}
// ===================================================================================================================
class ModdedMagmaBlock extends MagmaBlock {
    private final Direction pushDirection;

    public ModdedMagmaBlock(Properties properties, Direction pushDirection) {
        super(properties);
        this.pushDirection = pushDirection;
    }

    @Override
    public boolean isValidBubbleElevatorRelay(BlockState state) {
        return true;
    }

    @Override
    public Direction getBubbleElevatorDirection(BlockState state) {
        return pushDirection;
    }
}
// ===================================================================================================================
class TrapDoorAndBubbleColumnInOne extends TrapDoorBlock {
    public static final BooleanProperty BUBBLE_ELEVATOR = BooleanProperty.create("bubble_elevator");
    public static final BooleanProperty DRAG = BlockStateProperties.DRAG;

    protected TrapDoorAndBubbleColumnInOne(Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(BUBBLE_ELEVATOR, Boolean.valueOf(false)).with(DRAG, Boolean.valueOf(true)).with(HORIZONTAL_FACING, Direction.NORTH).with(HALF, Half.BOTTOM).with(OPEN, Boolean.valueOf(false)).with(POWERED, Boolean.valueOf(false)).with(WATERLOGGED, Boolean.valueOf(false)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BUBBLE_ELEVATOR, DRAG, HORIZONTAL_FACING, HALF, OPEN, POWERED, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState blockstate = this.getDefaultState();
        IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
        Direction direction = context.getFace();
        if (!context.replacingClickedOnBlock() && direction.getAxis().isHorizontal()) {
            blockstate = blockstate.with(HORIZONTAL_FACING, direction).with(HALF, context.getHitVec().y - (double)context.getPos().getY() > 0.5D ? Half.TOP : Half.BOTTOM);
        } else {
            blockstate = blockstate.with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite()).with(HALF, direction == Direction.UP ? Half.BOTTOM : Half.TOP);
        }

        World world = context.getWorld();
        if (world.isBlockPowered(context.getPos())) {
            blockstate = blockstate.with(OPEN, Boolean.valueOf(true)).with(POWERED, Boolean.valueOf(true));
        }

        BlockState stateBelow = world.getBlockState(context.getPos().down());
        if (stateBelow.isValidBubbleElevatorRelay()) {
            blockstate = blockstate.with(BUBBLE_ELEVATOR, true).with(DRAG, stateBelow.getBubbleElevatorDirection() != Direction.UP);
        }

        return blockstate.with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
    }

    // ==================== here begins bubble column stuff

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        if (state.get(BUBBLE_ELEVATOR)) {
            BlockState blockstate = worldIn.getBlockState(pos.up());
            if (blockstate.isAir()) {
                entityIn.onEnterBubbleColumnWithAirAbove(state.get(DRAG));
                if (!worldIn.isRemote) {
                    ServerWorld serverworld = (ServerWorld)worldIn;

                    for(int i = 0; i < 2; ++i) {
                        serverworld.spawnParticle(ParticleTypes.SPLASH, (double)((float)pos.getX() + worldIn.rand.nextFloat()), (double)(pos.getY() + 1), (double)((float)pos.getZ() + worldIn.rand.nextFloat()), 1, 0.0D, 0.0D, 0.0D, 1.0D);
                        serverworld.spawnParticle(ParticleTypes.BUBBLE, (double)((float)pos.getX() + worldIn.rand.nextFloat()), (double)(pos.getY() + 1), (double)((float)pos.getZ() + worldIn.rand.nextFloat()), 1, 0.0D, 0.01D, 0.0D, 0.2D);
                    }
                }
            } else {
                entityIn.onEnterBubbleColumn(state.get(DRAG));
            }
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (state.get(BUBBLE_ELEVATOR)) {
            BlockState stateBelow = worldIn.getBlockState(pos.down());
            worldIn.getBlockState(pos.up()).convertIntoBubbleElevator(worldIn, pos.up(), null, stateBelow.getBubbleElevatorDirection());
        }
    }

    @Override
    public void tick(BlockState state, World worldIn, BlockPos pos, Random random) {
        if (state.get(BUBBLE_ELEVATOR)) {
            if (worldIn.getBlockState(pos.down()).isValidBubbleElevatorRelay()) {
                worldIn.getBlockState(pos.up()).convertIntoBubbleElevator(worldIn, pos.up(), null, state.getBubbleElevatorDirection());
            } else {
                worldIn.setBlockState(pos, state.with(BUBBLE_ELEVATOR, false));
            }
        }
    }

    @Override
    public int tickRate(IWorldReader worldIn) {
        return 5;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (stateIn.get(BUBBLE_ELEVATOR)) {
            double d0 = (double)pos.getX();
            double d1 = (double)pos.getY();
            double d2 = (double)pos.getZ();
            if (stateIn.get(DRAG)) {
                worldIn.addOptionalParticle(ParticleTypes.CURRENT_DOWN, d0 + 0.5D, d1 + 0.8D, d2, 0.0D, 0.0D, 0.0D);
                if (rand.nextInt(200) == 0) {
                    worldIn.playSound(d0, d1, d2, SoundEvents.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundCategory.BLOCKS, 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false);
                }
            } else {
                worldIn.addOptionalParticle(ParticleTypes.BUBBLE_COLUMN_UP, d0 + 0.5D, d1, d2 + 0.5D, 0.0D, 0.04D, 0.0D);
                worldIn.addOptionalParticle(ParticleTypes.BUBBLE_COLUMN_UP, d0 + (double)rand.nextFloat(), d1 + (double)rand.nextFloat(), d2 + (double)rand.nextFloat(), 0.0D, 0.04D, 0.0D);
                if (rand.nextInt(200) == 0) {
                    worldIn.playSound(d0, d1, d2, SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundCategory.BLOCKS, 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false);
                }
            }
        }
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if(stateIn.get(WATERLOGGED)) {
            if (facing == Direction.DOWN) {
                if (facingState.isValidBubbleElevatorRelay()) {
                    stateIn = stateIn.with(BUBBLE_ELEVATOR, true).with(DRAG, facingState.getBubbleElevatorDirection() != Direction.UP);
                    worldIn.setBlockState(currentPos, stateIn, 2);
                } else {
                    stateIn = stateIn.with(BUBBLE_ELEVATOR, false);
                    worldIn.setBlockState(currentPos, stateIn, 2);
                }
            } else if (facing == Direction.UP && facingState.getFluidState().getLevel() >= 8 && facingState.getFluidState().isSource() ) {
                worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, this.tickRate(worldIn));
            }
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }
        return stateIn;
    }

    // ================== Here begins Forge Interface stuff

    @Override
    public boolean isValidBubbleElevatorRelay(BlockState state) {
        return state.get(BUBBLE_ELEVATOR);
    }

    @Override
    public Direction getBubbleElevatorDirection(BlockState state) {
        return state.get(DRAG) ? Direction.DOWN : Direction.UP;
    }

    @Override
    public void convertIntoBubbleElevator(BlockState state, IWorld world, BlockPos pos, @Nullable Block vanillaBubbleColumnReplacement, Direction elevatorDirection) {
        if(state.get(WATERLOGGED)) {
            boolean drag = elevatorDirection != Direction.UP;
            world.setBlockState(pos, state.with(BUBBLE_ELEVATOR, true).with(DRAG, drag), 2);
        }
    }
}