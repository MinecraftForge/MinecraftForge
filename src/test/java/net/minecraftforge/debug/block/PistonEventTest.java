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

package net.minecraftforge.debug.block;

import java.util.List;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.event.world.PistonEvent;
import net.minecraftforge.event.world.PistonEvent.PistonMoveType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

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
    public static final String MODID = "piston_event_test";
    public static String blockName = "shiftonmove";
    private static DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static DeferredRegister<Item>  ITEMS  = DeferredRegister.create(ForgeRegistries.ITEMS,  MODID);

    private static RegistryObject<Block> shiftOnMove = BLOCKS.register(blockName, () -> new Block(Block.Properties.of(Material.STONE)));
    static {
        ITEMS.register(blockName, () -> new BlockItem(shiftOnMove.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));
    }

    public PistonEventTest()
    {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        modBus.addListener(this::gatherData);
    }

    @SubscribeEvent
    public static void pistonPre(PistonEvent.Pre event)
    {
        if (event.getPistonMoveType() == PistonMoveType.EXTEND)
        {
            Level world = (Level) event.getWorld();
            PistonStructureResolver pistonHelper = event.getStructureHelper();
            Player player = DistExecutor.safeCallWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().player);
            if (world.isClientSide && player != null)
            {
                if (pistonHelper.resolve())
                {
                    player.sendMessage(new TextComponent(String.format("Piston will extend moving %d blocks and destroy %d blocks", pistonHelper.getToPush().size(), pistonHelper.getToDestroy().size())), player.getUUID());
                }
                else
                {
                    player.sendMessage(new TextComponent("Piston won't extend"), player.getUUID());
                }
            }

            if (pistonHelper.resolve())
            {
                List<BlockPos> posList = pistonHelper.getToPush();
                for (BlockPos newPos : posList)
                {
                    BlockState state = event.getWorld().getBlockState(newPos);
                    if (state.getBlock() == Blocks.BLACK_WOOL)
                    {
                        Block.dropResources(state, world, newPos);
                        world.setBlockAndUpdate(newPos, Blocks.AIR.defaultBlockState());
                    }
                }
            }

            // Make the block move up and out of the way so long as it won't replace the piston
            BlockPos pushedBlockPos = event.getFaceOffsetPos();
            if (world.getBlockState(pushedBlockPos).getBlock() == shiftOnMove.get() && event.getDirection() != Direction.DOWN)
            {
                world.setBlockAndUpdate(pushedBlockPos, Blocks.AIR.defaultBlockState());
                world.setBlockAndUpdate(pushedBlockPos.above(), shiftOnMove.get().defaultBlockState());
            }

            // Block pushing cobblestone (directly, indirectly works)
            event.setCanceled(event.getWorld().getBlockState(event.getFaceOffsetPos()).getBlock() == Blocks.COBBLESTONE);
        }
        else
        {
            boolean isSticky = event.getWorld().getBlockState(event.getPos()).getBlock() == Blocks.STICKY_PISTON;

            Player player = DistExecutor.safeCallWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().player);
            if (event.getWorld().isClientSide() && player != null)
            {
                if (isSticky)
                {
                    BlockPos targetPos = event.getFaceOffsetPos().relative(event.getDirection());
                    boolean canPush = PistonBaseBlock.isPushable(event.getWorld().getBlockState(targetPos), (Level) event.getWorld(), event.getFaceOffsetPos(), event.getDirection().getOpposite(), false, event.getDirection());
                    boolean isAir = event.getWorld().isEmptyBlock(targetPos);
                    player.sendMessage(new TextComponent(String.format("Piston will retract moving %d blocks", !isAir && canPush ? 1 : 0)), player.getUUID());
                }
                else
                {
                    player.sendMessage(new TextComponent("Piston will retract"), player.getUUID());
                }
            }
            // Offset twice to see if retraction will pull cobblestone
            event.setCanceled(event.getWorld().getBlockState(event.getFaceOffsetPos().relative(event.getDirection())).getBlock() == Blocks.COBBLESTONE && isSticky);
        }
    }

    public void gatherData(GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();

        if (event.includeClient())
        {
            gen.addProvider(new BlockStates(gen, event.getExistingFileHelper()));
        }
    }

    private class BlockStates extends BlockStateProvider
    {
        public BlockStates(DataGenerator gen, ExistingFileHelper exFileHelper)
        {
            super(gen, MODID, exFileHelper);
        }

        @Override
        protected void registerStatesAndModels()
        {
            ModelFile model = models().cubeAll(shiftOnMove.get().getRegistryName().getPath(), mcLoc("block/furnace_top"));
            simpleBlock(shiftOnMove.get(), model);
            simpleBlockItem(shiftOnMove.get(), model);
        }
    }

}
