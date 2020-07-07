/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonBlockStructureHelper;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.event.world.PistonEvent;
import net.minecraftforge.event.world.PistonEvent.PistonMoveType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
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

    private static RegistryObject<Block> shiftOnMove = BLOCKS.register(blockName, () -> new Block(Block.Properties.create(Material.ROCK)));
    static {
        ITEMS.register(blockName, () -> new BlockItem(shiftOnMove.get(), new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)));
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
            World world = (World) event.getWorld();
            PistonBlockStructureHelper pistonHelper = event.getStructureHelper();
            PlayerEntity player = DistExecutor.safeCallWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().player);
            if (world.isRemote && player != null)
            {
                if (pistonHelper.canMove())
                {
                    player.sendMessage(new StringTextComponent(String.format("Piston will extend moving %d blocks and destroy %d blocks", pistonHelper.getBlocksToMove().size(), pistonHelper.getBlocksToDestroy().size())));
                }
                else
                {
                    player.sendMessage(new StringTextComponent("Piston won't extend"));
                }
            }

            if (pistonHelper.canMove())
            {
                List<BlockPos> posList = pistonHelper.getBlocksToMove();
                for (BlockPos newPos : posList)
                {
                    BlockState state = event.getWorld().getBlockState(newPos);
                    if (state.getBlock() == Blocks.BLACK_WOOL)
                    {
                        Block.spawnDrops(state, world, newPos);
                        world.setBlockState(newPos, Blocks.AIR.getDefaultState());
                    }
                }
            }

            // Make the block move up and out of the way so long as it won't replace the piston
            BlockPos pushedBlockPos = event.getFaceOffsetPos();
            if (world.getBlockState(pushedBlockPos).getBlock() == shiftOnMove.get() && event.getDirection() != Direction.DOWN)
            {
                world.setBlockState(pushedBlockPos, Blocks.AIR.getDefaultState());
                world.setBlockState(pushedBlockPos.up(), shiftOnMove.get().getDefaultState());
            }

            // Block pushing cobblestone (directly, indirectly works)
            event.setCanceled(event.getWorld().getBlockState(event.getFaceOffsetPos()).getBlock() == Blocks.COBBLESTONE);
        }
        else
        {
            boolean isSticky = event.getWorld().getBlockState(event.getPos()).getBlock() == Blocks.STICKY_PISTON;

            PlayerEntity player = DistExecutor.safeCallWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().player);
            if (event.getWorld().isRemote() && player != null)
            {
                if (isSticky)
                {
                    BlockPos targetPos = event.getFaceOffsetPos().offset(event.getDirection());
                    boolean canPush = PistonBlock.canPush(event.getWorld().getBlockState(targetPos), (World) event.getWorld(), event.getFaceOffsetPos(), event.getDirection().getOpposite(), false, event.getDirection());
                    boolean isAir = event.getWorld().isAirBlock(targetPos);
                    player.sendMessage(new StringTextComponent(String.format("Piston will retract moving %d blocks", !isAir && canPush ? 1 : 0)));
                }
                else
                {
                    player.sendMessage(new StringTextComponent("Piston will retract"));
                }
            }
            // Offset twice to see if retraction will pull cobblestone
            event.setCanceled(event.getWorld().getBlockState(event.getFaceOffsetPos().offset(event.getDirection())).getBlock() == Blocks.COBBLESTONE && isSticky);
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
