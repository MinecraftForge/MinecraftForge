/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.level.PistonEvent;
import net.minecraftforge.event.level.PistonEvent.PistonMoveType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * This test mod blocks pistons from moving cobblestone at all except indirectly
 * This test mod adds a block that moves upwards when pushed by a piston
 * This test mod informs the user what will happen the piston and affected blocks when changes are made
 * This test mod makes black wool pushed by a piston drop after being pushed.
 */
@Mod.EventBusSubscriber(modid = PistonEventTest.MODID)
@Mod(value = PistonEventTest.MODID)
public class PistonEventTest
{
    static final boolean ENABLE_PISTON_EVENT_LOGGING = false;

    public static final String MODID = "piston_event_test";
    public static String blockName = "shiftonmove";
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    private static final RegistryObject<Block> SHIFT_ON_MOVE = BLOCKS.register(blockName, () -> new Block(Block.Properties.of().mapColor(MapColor.STONE)));
    private static final RegistryObject<Item> SHIFT_ON_MOVE_ITEM = ITEMS.register(blockName, () -> new BlockItem(SHIFT_ON_MOVE.get(), new Item.Properties()));

    public PistonEventTest()
    {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        modBus.addListener(this::gatherData);
        modBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
            event.accept(SHIFT_ON_MOVE_ITEM);
    }

    @SubscribeEvent
    public static void pistonPre(PistonEvent.Pre event)
    {
        if (event.getPistonMoveType() == PistonMoveType.EXTEND)
        {
            Level world = (Level) event.getLevel();
            PistonStructureResolver pistonHelper = Objects.requireNonNull(event.getStructureHelper());

            if (pistonHelper.resolve())
            {
                sendMessage(world, String.format(Locale.ENGLISH, "Piston will extend moving %d blocks and destroy %d blocks", pistonHelper.getToPush().size(), pistonHelper.getToDestroy().size()));
                List<BlockPos> posList = pistonHelper.getToPush();
                for (BlockPos newPos : posList)
                {
                    BlockState state = event.getLevel().getBlockState(newPos);
                    if (state.getBlock() == Blocks.BLACK_WOOL)
                    {
                        Block.dropResources(state, world, newPos);
                        world.setBlockAndUpdate(newPos, Blocks.AIR.defaultBlockState());
                    }
                }
            }
            else
            {
                sendMessage(world, "Piston won't extend");
            }

            // Make the block move up and out of the way so long as it won't replace the piston
            BlockPos pushedBlockPos = event.getFaceOffsetPos();
            if (world.getBlockState(pushedBlockPos).getBlock() == SHIFT_ON_MOVE.get() && event.getDirection() != Direction.DOWN)
            {
                world.setBlockAndUpdate(pushedBlockPos, Blocks.AIR.defaultBlockState());
                world.setBlockAndUpdate(pushedBlockPos.above(), SHIFT_ON_MOVE.get().defaultBlockState());
            }

            // Block pushing cobblestone (directly, indirectly works)
            event.setCanceled(event.getLevel().getBlockState(event.getFaceOffsetPos()).getBlock() == Blocks.COBBLESTONE);
        }
        else
        {
            boolean isSticky = event.getLevel().getBlockState(event.getPos()).getBlock() == Blocks.STICKY_PISTON;

            if (isSticky)
            {
                BlockPos targetPos = event.getFaceOffsetPos().relative(event.getDirection());
                boolean canPush = PistonBaseBlock.isPushable(event.getLevel().getBlockState(targetPos), (Level) event.getLevel(), event.getFaceOffsetPos(), event.getDirection().getOpposite(), false, event.getDirection());
                boolean isAir = event.getLevel().isEmptyBlock(targetPos);
                sendMessage(event.getLevel(), String.format(Locale.ENGLISH, "Piston will retract moving %d blocks", !isAir && canPush ? 1 : 0));
            }
            else
            {
                sendMessage(event.getLevel(), "Piston will retract");
            }

            // Offset twice to see if retraction will pull cobblestone
            event.setCanceled(event.getLevel().getBlockState(event.getFaceOffsetPos().relative(event.getDirection())).getBlock() == Blocks.COBBLESTONE && isSticky);
        }
    }

    private static void sendMessage(LevelAccessor levelAccessor, String message)
    {
        if (!ENABLE_PISTON_EVENT_LOGGING) return;

        if (!levelAccessor.isClientSide() && levelAccessor instanceof ServerLevel level)
        {
            level.getServer().sendSystemMessage(Component.literal(message));
        }
    }

    public void gatherData(GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();

        gen.addProvider(event.includeClient(), new BlockStates(gen.getPackOutput(), event.getExistingFileHelper()));
    }

    private static class BlockStates extends BlockStateProvider
    {
        public BlockStates(PackOutput output, ExistingFileHelper exFileHelper)
        {
            super(output, MODID, exFileHelper);
        }

        @Override
        protected void registerStatesAndModels()
        {
            simpleBlockWithItem(SHIFT_ON_MOVE.get(), models().cubeAll(Objects.requireNonNull(SHIFT_ON_MOVE.getId()).getPath(), mcLoc("block/furnace_top")));
        }
    }

}
