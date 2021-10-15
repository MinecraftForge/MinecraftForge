/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.debug.fluid;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.DispenseFluidContainer;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.Validate;

import java.util.Optional;
import java.util.stream.Stream;

@Mod(CoreFluidTest.MODID)
public class CoreFluidTest
{
    public static final boolean ENABLE = true;
    public static final String MODID = "core_fluid_test";

    public static final ResourceLocation FLUID_STILL = new ResourceLocation("minecraft:block/brown_mushroom_block");
    public static final ResourceLocation FLUID_FLOWING = new ResourceLocation("minecraft:block/mushroom_stem");
    public static final ResourceLocation FLUID_OVERLAY = new ResourceLocation("minecraft:block/obsidian");

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, MODID);

    // Override Fluid
    public static final RegistryObject<FlowingFluid> TEST_OVERRIDE_FLUID = FLUIDS.register("test_override_fluid", () ->
            new TestOverrideFluid.Source(makeProperties())
    );

    public static final RegistryObject<FlowingFluid> TEST_OVERRIDE_FLUID_FLOWING = FLUIDS.register("test_override_fluid_flowing", () ->
            new TestOverrideFluid.Flowing(makeProperties())
    );

    public static final RegistryObject<LiquidBlock> TEST_OVERRIDE_FLUID_BLOCK = BLOCKS.register("test_override_fluid_block", () ->
            new LiquidBlock(TEST_OVERRIDE_FLUID, BlockBehaviour.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops())
    );

    public static final RegistryObject<Item> TEST_OVERRIDE_FLUID_BUCKET = ITEMS.register("test_override_fluid_bucket", () ->
            new BucketItem(TEST_OVERRIDE_FLUID, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(CreativeModeTab.TAB_MISC))
    );

    private static ForgeFlowingFluid.Properties makeProperties()
    {
        return new ForgeFlowingFluid.Properties(TEST_OVERRIDE_FLUID, TEST_OVERRIDE_FLUID_FLOWING,
                FluidAttributes.builder(FLUID_STILL, FLUID_FLOWING)
                        .overlay(FLUID_OVERLAY)
                        .color(0xAF1080FF)
        ).bucket(TEST_OVERRIDE_FLUID_BUCKET).block(TEST_OVERRIDE_FLUID_BLOCK);
    }

    // Attribute Fluid
    public static final RegistryObject<FlowingFluid> TEST_ATTRIBUTE_FLUID = FLUIDS.register("test_attribute_fluid", () ->
            new ForgeFlowingFluid.Source(makePropertiesForAttributeExample())
    );

    public static final RegistryObject<FlowingFluid> TEST_ATTRIBUTE_FLUID_FLOWING = FLUIDS.register("test_attribute_fluid_flowing", () ->
            new ForgeFlowingFluid.Flowing(makePropertiesForAttributeExample())
    );

    public static final RegistryObject<LiquidBlock> TEST_ATTRIBUTE_FLUID_BLOCK = BLOCKS.register("test_attribute_fluid_block", () ->
            new LiquidBlock(TEST_ATTRIBUTE_FLUID, BlockBehaviour.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops())
    );

    public static final RegistryObject<Item> TEST_ATTRIBUTE_FLUID_BUCKET = ITEMS.register("test_attribute_fluid_bucket", () ->
            new BucketItem(TEST_ATTRIBUTE_FLUID, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(CreativeModeTab.TAB_MISC))
    );

    private static ForgeFlowingFluid.Properties makePropertiesForAttributeExample()
    {
        return new ForgeFlowingFluid.Properties(TEST_ATTRIBUTE_FLUID, TEST_ATTRIBUTE_FLUID_FLOWING,
                FluidAttributes.builder(FLUID_STILL, FLUID_FLOWING)
                        .viewOverlay(FLUID_OVERLAY)
                        .overlay(FLUID_OVERLAY)
                        .color(0xAF1080FF)
                        .motionScale((state, entity) -> entity instanceof Player ? 0.014D : -0.014D) // Will push away the entity if it's a player, but will pull it towards the source block if it's not.
                        .fallDistanceModifier((state, entity) -> entity instanceof Player ? 0.0D : 2.0D) // Will double the falling distance if an entity falls through it if it's not a player, otherwise it resets the value.
                        .canSwim(state -> true) // Allows the player swimming animation and speed boost while inside this fluid
                        .canDrown((state, entity) -> entity instanceof Player) // Only players can drown in this fluid
                        .canExtinguish((state, entity) -> entity instanceof Player) // Only players can be extinguished if on fire in this fluid
                        .canHydrate((fluidState, blockState) -> true) // Can hydrate ConcretePowder -> Concrete, Farmland, and Coral.
                        .canBoat((state, boat) -> true) // You can boat in this fluid
        ).bucket(TEST_ATTRIBUTE_FLUID_BUCKET).block(TEST_ATTRIBUTE_FLUID_BLOCK)
                .canMultiply(((fluidState, reader, blockPos) -> true)); // This fluid can multiply and create new source blocks
    }

    // Fluid-Loggable Block
    public static final RegistryObject<Block> FLUIDLOGGABLE_BLOCK = BLOCKS.register("fluidloggable_block", () ->
            new FluidloggableBlock(BlockBehaviour.Properties.of(Material.WOOD).noCollission().strength(100.0F).noDrops())
    );

    public static final RegistryObject<Item> FLUIDLOGGABLE_BLOCK_ITEM = ITEMS.register("fluidloggable_block", () ->
            new BlockItem(FLUIDLOGGABLE_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_MISC))
    );

    public CoreFluidTest()
    {
        if (ENABLE)
        {
            IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
            BLOCKS.register(modEventBus);
            ITEMS.register(modEventBus);
            FLUIDS.register(modEventBus);
            modEventBus.addListener(this::loadComplete);
            modEventBus.addListener(this::clientSetup);
        }
    }

    private void clientSetup(FMLClientSetupEvent event)
    {
        Stream.of(TEST_OVERRIDE_FLUID, TEST_OVERRIDE_FLUID_FLOWING, TEST_ATTRIBUTE_FLUID, TEST_ATTRIBUTE_FLUID_FLOWING).forEach(f ->
                ItemBlockRenderTypes.setRenderLayer(f.get(), RenderType.translucent()));
    }

    public void loadComplete(FMLLoadCompleteEvent event)
    {
        BlockState waterState = Fluids.WATER.defaultFluidState().createLegacyBlock();
        BlockState lavaState = Fluids.LAVA.defaultFluidState().createLegacyBlock();
        BlockState overrideState = TEST_OVERRIDE_FLUID.get().defaultFluidState().createLegacyBlock();
        BlockState attributeState = TEST_ATTRIBUTE_FLUID.get().defaultFluidState().createLegacyBlock();

        Validate.isTrue(waterState.getBlock() == Blocks.WATER);
        Validate.isTrue(lavaState.getBlock() == Blocks.LAVA);
        Validate.isTrue(overrideState.getBlock() == TEST_OVERRIDE_FLUID_BLOCK.get());
        Validate.isTrue(attributeState.getBlock() == TEST_ATTRIBUTE_FLUID_BLOCK.get());

        ItemStack waterBucket = Fluids.WATER.getAttributes().getBucket(new FluidStack(Fluids.WATER, 1));
        ItemStack lavaBucket = Fluids.LAVA.getAttributes().getBucket(new FluidStack(Fluids.LAVA, 1));
        ItemStack overrideBucket = TEST_OVERRIDE_FLUID.get().getAttributes().getBucket(new FluidStack(TEST_OVERRIDE_FLUID.get(), 1));
        ItemStack attributeBucket = TEST_ATTRIBUTE_FLUID.get().getAttributes().getBucket(new FluidStack(TEST_ATTRIBUTE_FLUID.get(), 1));

        Validate.isTrue(waterBucket.getItem() == Fluids.WATER.getBucket());
        Validate.isTrue(lavaBucket.getItem() == Fluids.LAVA.getBucket());
        Validate.isTrue(overrideBucket.getItem() == TEST_OVERRIDE_FLUID.get().getBucket());
        Validate.isTrue(attributeBucket.getItem() == TEST_ATTRIBUTE_FLUID.get().getBucket());

        event.enqueueWork(() -> {
            DispenserBlock.registerBehavior(TEST_OVERRIDE_FLUID_BUCKET.get(), DispenseFluidContainer.getInstance());
            DispenserBlock.registerBehavior(TEST_ATTRIBUTE_FLUID_BUCKET.get(), DispenseFluidContainer.getInstance());
        });
    }

    // Test Override Fluid Class
    public static class TestOverrideFluid
    {
        public static class Source extends ForgeFlowingFluid.Source
        {
            public Source(Properties properties)
            {
                super(properties);
            }

            @Override
            public boolean canSwim(FluidState state)
            {
                return true;
            }

            @Override
            public boolean canDrown(FluidState state, LivingEntity entity)
            {
                return entity instanceof Player;
            }

            @Override
            public boolean canExtinguish(FluidState state, Entity entity)
            {
                return entity instanceof Player;
            }

            @Override
            public boolean canHydrate(FluidState fluidState, BlockState blockState)
            {
                return blockState.getBlock() instanceof ConcretePowderBlock || blockState.getBlock() instanceof FarmBlock;
            }

            @Override
            public boolean canBoat(FluidState state, Boat boat)
            {
                return false;
            }

            @Override
            public void sink(FluidState state, LivingEntity entity)
            {
                entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, (double)-0.1F * entity.getAttribute(ForgeMod.SWIM_SPEED.get()).getValue(), 0.0D));
            }

            // If the custom fluid is trying to place a flowing block, and it touches a Lava source block.
            // Then it will generate a copy of the BlockState below said position.
            @Override
            public boolean handleFluidInteraction(FluidState state, Level level, BlockPos pos)
            {
                for (Direction direction : Direction.values())
                {
                    if (direction != Direction.DOWN)
                    {
                        if (level.getFluidState(pos.relative(direction)).getType() == Fluids.LAVA)
                        {
                            level.setBlock(pos, level.getBlockState(pos.below()), Constants.BlockFlags.DEFAULT);
                            return false;
                        }
                    }
                }
                return true;
            }
        }

        public static class Flowing extends ForgeFlowingFluid.Flowing
        {
            public Flowing(Properties properties)
            {
                super(properties);
            }

            @Override
            public boolean canSwim(FluidState state)
            {
                return true;
            }

            @Override
            public boolean canDrown(FluidState state, LivingEntity entity)
            {
                return entity instanceof Player;
            }

            @Override
            public boolean canExtinguish(FluidState state, Entity entity)
            {
                return entity instanceof Player;
            }

            @Override
            public boolean canHydrate(FluidState fluidState, BlockState blockState)
            {
                return blockState.getBlock() instanceof ConcretePowderBlock || blockState.getBlock() instanceof FarmBlock;
            }

            @Override
            public boolean canBoat(FluidState state, Boat boat)
            {
                return false;
            }

            @Override
            public void sink(FluidState state, LivingEntity entity)
            {
                entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, (double)-0.1F * entity.getAttribute(ForgeMod.SWIM_SPEED.get()).getValue(), 0.0D));
            }

            // If the custom fluid is trying to place a flowing block, and it touches a Lava source block.
            // Then it will generate a copy of the BlockState below said position.
            @Override
            public boolean handleFluidInteraction(FluidState state, Level level, BlockPos pos)
            {
                for (Direction direction : Direction.values())
                {
                    if (direction != Direction.DOWN)
                    {
                        if (level.getFluidState(pos.relative(direction)).getType() == Fluids.LAVA)
                        {
                            level.setBlock(pos, level.getBlockState(pos.below()), Constants.BlockFlags.DEFAULT);
                            return false;
                        }
                    }
                }
                return true;
            }
        }
    }

    private static class FluidloggableBlock extends Block implements BucketPickup, LiquidBlockContainer
    {
        public static final BooleanProperty FLUIDLOGGED = BooleanProperty.create("fluidlogged");

        public FluidloggableBlock(Properties properties)
        {
            super(properties);
            registerDefaultState(getStateDefinition().any().setValue(FLUIDLOGGED, false));
        }

        @Override
        public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
        {
            return Shapes.empty();
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
        {
            builder.add(FLUIDLOGGED);
        }

        @Override
        public boolean canPlaceLiquid(BlockGetter world, BlockPos pos, BlockState state, Fluid fluid)
        {
            return !state.getValue(FLUIDLOGGED) && !fluid.defaultFluidState().isEmpty() && fluid == TEST_ATTRIBUTE_FLUID.get();
        }

        @Override
        public boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState blockState, FluidState fluidState) {
            if (canPlaceLiquid(world, pos, blockState, fluidState.getType()))
            {
                if (!world.isClientSide())
                {
                    world.setBlock(pos, blockState.setValue(FLUIDLOGGED, true), 3);
                    world.getLiquidTicks().scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(world));
                }
                return true;
            }
            return false;
        }

        @Override
        public ItemStack pickupBlock(LevelAccessor world, BlockPos pos, BlockState blockState)
        {
            if (blockState.getValue(FLUIDLOGGED))
            {
                world.setBlock(pos, blockState.setValue(FLUIDLOGGED, false), 3);
                return new ItemStack(blockState.getFluidState().getType().getBucket());
            }
            return ItemStack.EMPTY;
        }

        @Override
        public Optional<SoundEvent> getPickupSound()
        {
            return Optional.of(SoundEvents.BUCKET_FILL);
        }

        @Override
        public FluidState getFluidState(BlockState state)
        {
            return state.getValue(FLUIDLOGGED) ? TEST_ATTRIBUTE_FLUID.get().defaultFluidState() : Fluids.EMPTY.defaultFluidState();
        }
    }

}
