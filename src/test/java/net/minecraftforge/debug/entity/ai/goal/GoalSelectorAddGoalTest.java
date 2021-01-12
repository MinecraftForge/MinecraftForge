package net.minecraftforge.debug.entity.ai.goal;

import com.mojang.brigadier.Command;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Hand;
import net.minecraft.util.concurrent.TickDelayedTask;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.GameType;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import static net.minecraft.util.Util.DUMMY_UUID;

/**
 * Use /goal_selector_add_goal_test to run the test
 */
@Mod(GoalSelectorAddGoalTest.MOD_ID)
public class GoalSelectorAddGoalTest
{
    /**
     * iff true: Run on world load automatically
     */
    public static final boolean AUTO = false;
    public static final String MOD_ID = "goal_selector_add_goal_test";
    public static final int TEST_X = 10000;
    public static final int TEST_Y = 200;
    public static final int TEST_Z = 10000;
    public static final BlockPos TEST_POS = new BlockPos(TEST_X, TEST_Y, TEST_Z);
    public static final int ROOM_RANGE = 8;

    public GoalSelectorAddGoalTest()
    {
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
        if (AUTO)
        {
            MinecraftForge.EVENT_BUS.addListener(this::runTestOnWorldLoad);
        }
    }

    public void registerCommands(RegisterCommandsEvent event)
    {
        event.getDispatcher().register(Commands.literal(MOD_ID).executes(ctx -> {
            new Test(ctx.getSource().asPlayer());
            return Command.SINGLE_SUCCESS;
        }).then(Commands.argument("dim", DimensionArgument.getDimension()).executes(ctx -> {
            Entity entity = ctx.getSource().getEntity();
            if (entity instanceof ServerPlayerEntity)
            {
                new Test(DimensionArgument.getDimensionArgument(ctx, "dim"), (ServerPlayerEntity) entity);
            }
            else
            {
                new Test(DimensionArgument.getDimensionArgument(ctx, "dim"));
            }
            return Command.SINGLE_SUCCESS;
        })));
    }

    public void runTestOnWorldLoad(WorldEvent.Load event)
    {
        IWorld world = event.getWorld();
        if (world instanceof ServerWorld)
        {
            new Test((ServerWorld) world);
        }
    }

    public static class Test
    {
        private final ServerWorld world;
        private final ServerPlayerEntity player;
        private final Vector3d prevPosition;
        private final float prevRotationYaw;
        private final float prevRotationPitch;
        private final GameType prevGameType;
        private final Set<MobEntity> createdEntities = Collections.newSetFromMap(new IdentityHashMap<>());

        public Test(ServerWorld world)
        {
            this(world, null);
        }

        public Test(ServerPlayerEntity player)
        {
            this(player.getServerWorld(), player);
        }

        public Test(ServerWorld world, @Nullable ServerPlayerEntity player)
        {
            this.world = world;
            this.player = player;
            this.prevPosition = player == null ? Vector3d.ZERO : player.getPositionVec();
            this.prevRotationYaw = player == null ? 0 : player.rotationYaw;
            this.prevRotationPitch = player == null ? 0 : player.rotationPitch;
            this.prevGameType = player == null ? GameType.SURVIVAL : player.interactionManager.getGameType();
            setup();
        }

        @SubscribeEvent
        public void onLivingDeath(LivingDeathEvent event)
        {
            LivingEntity entity = event.getEntityLiving();
            if (entity instanceof MobEntity && createdEntities.contains(entity))
            {
                checkAndCleanup();
            }
        }

        @SubscribeEvent
        public void onLivingDrops(LivingDropsEvent event)
        {
            LivingEntity entity = event.getEntityLiving();
            if (entity instanceof MobEntity && createdEntities.contains(entity))
            {
                event.getDrops().clear();
            }
        }

        /***
         * This event happens during goal ticking
         * Updating the held item can modify the goals for certain entities (e.g. skeletons)
         * So we need to delay the disarming
         * Thanks to @malte0811
         * @see AbstractSkeletonEntity#setCombatTask()
         * @see MinecraftServer#enqueue(Runnable)
         */
        @SubscribeEvent
        public void onLivingAttackEvent(LivingAttackEvent event)
        {
            Entity entity = event.getSource().getTrueSource();
            if (entity instanceof MobEntity && createdEntities.contains(entity))
            {
                MobEntity mob = (MobEntity) entity;
                World world = mob.getEntityWorld();
                if (world instanceof ServerWorld)
                {
                    // Disarm mob with a 1 in 5 chance
                    if (world.rand.nextInt(5) == 0)
                    {
                        MinecraftServer server = ((ServerWorld) world).getServer();
                        // Delay till the next tick to avoid CME during goal ticking
                        // Must use enqueue because execute will just run right away if it's on the server thread
                        server.enqueue(new TickDelayedTask(server.getTickCounter(), () -> {
                            // Check that the mob didn't die in the meantime
                            if (mob.isAlive())
                            {
                                mob.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
                            }
                        }));
                    }
                }
            }
        }

        private void checkAndCleanup()
        {
            boolean hasWitherSkeletons = createdEntities.stream()
                    .filter(LivingEntity::isAlive)
                    .anyMatch(e -> e instanceof WitherSkeletonEntity);
            boolean hasPiglins = createdEntities.stream()
                    .filter(LivingEntity::isAlive)
                    .anyMatch(e -> e instanceof PiglinEntity);
            if (!hasWitherSkeletons || !hasPiglins)
            {
                cleanup();
            }
        }

        private void setup()
        {
            // Chunkload area
            load3x3(new ChunkPos(TEST_POS), true);

            // Create a platform
            fillRoom(TEST_POS.down(), ROOM_RANGE, Blocks.GLASS.getDefaultState());

            Supplier<EntityType<? extends MobEntity>> typeSupplier = new Supplier<EntityType<? extends MobEntity>>()
            {
                boolean witherSkeleton = true;

                @Override
                public EntityType<? extends MobEntity> get()
                {
                    try
                    {
                        return witherSkeleton ? EntityType.WITHER_SKELETON : EntityType.PIGLIN;
                    }
                    finally
                    {
                        witherSkeleton = !witherSkeleton;
                    }
                }
            };
            // Create wither skeletons or piglins and add DummyGoal to their goalSelector
            getAllInSquareRange(TEST_POS, ROOM_RANGE - 1)
                    .forEach(pos -> {
                        MobEntity entity = spawnEntity(typeSupplier.get(), pos);
                        entity.goalSelector.addGoal(0, new DummyGoal());
                    });

            // Teleport player to test location in spectator
            if (player != null)
            {
                player.teleport(world, TEST_X, TEST_Y, TEST_Z, prevRotationYaw, prevRotationPitch);
                player.setGameType(GameType.SPECTATOR);
            }

            MinecraftForge.EVENT_BUS.register(this);
        }

        private void cleanup()
        {
            MinecraftForge.EVENT_BUS.unregister(this);

            // Teleport player back
            if (player != null)
            {
                player.sendMessage(new StringTextComponent("Success!"), DUMMY_UUID);
                player.setGameType(this.prevGameType);
                player.teleport(world, this.prevPosition.x, this.prevPosition.y, this.prevPosition.z, this.prevRotationYaw, this.prevRotationPitch);
            }

            // Remove created entities
            createdEntities.stream()
                    .filter(LivingEntity::isAlive)
                    .forEach(Entity::remove);

            // Remove platform
            fillRoom(TEST_POS.down(), ROOM_RANGE, Blocks.AIR.getDefaultState());

            // Release chunks
            load3x3(new ChunkPos(TEST_POS), false);
        }

        private void load3x3(ChunkPos pos, boolean add)
        {
            for (int ox = -1; ox <= 1; ox++)
            {
                for (int oz = -1; oz <= 1; oz++)
                {
                    world.forceChunk(pos.x + ox, pos.z + oz, add);
                }
            }
        }

        private Iterable<BlockPos> getAllInSquareRange(BlockPos origin, int range)
        {
            return BlockPos.getAllInBoxMutable(origin.add(-range, 0, -range), origin.add(range, 0, range));
        }

        private void fillRoom(BlockPos origin, int range, BlockState state)
        {
            // Floor and Roof
            getAllInSquareRange(origin, range)
                    .forEach((pos -> world.setBlockState(pos, state)));
            getAllInSquareRange(origin.add(0, 5, 0), range)
                    .forEach((pos -> world.setBlockState(pos, state)));
            // Walls
            BlockPos.getAllInBoxMutable(origin.add(-range, 1, -range), origin.add(range, 4, -range))
                    .forEach((pos -> world.setBlockState(pos, state)));
            BlockPos.getAllInBoxMutable(origin.add(range, 1, -range), origin.add(range, 4, range))
                    .forEach((pos -> world.setBlockState(pos, state)));
            BlockPos.getAllInBoxMutable(origin.add(range, 1, range), origin.add(-range, 4, range))
                    .forEach((pos -> world.setBlockState(pos, state)));
            BlockPos.getAllInBoxMutable(origin.add(-range, 1, range), origin.add(-range, 4, -range))
                    .forEach((pos -> world.setBlockState(pos, state)));
        }

        private <T extends MobEntity> T spawnEntity(EntityType<T> type, BlockPos pos)
        {
            T entity = type.create(world);
            Objects.requireNonNull(entity);
            entity.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            entity.onInitialSpawn(world.getWorld(), world.getDifficultyForLocation(entity.getPosition()), SpawnReason.COMMAND, null, null);
            entity.setCanPickUpLoot(false);
            entity.setCustomName(new StringTextComponent("TESTING"));
            world.addEntity(entity);
            createdEntities.add(entity);
            return entity;
        }
    }

    public static class DummyGoal extends Goal
    {
        public DummyGoal()
        {
            this.setMutexFlags(EnumSet.allOf(Goal.Flag.class));
        }

        @Override
        public boolean shouldExecute()
        {
            return false;
        }
    }
}
