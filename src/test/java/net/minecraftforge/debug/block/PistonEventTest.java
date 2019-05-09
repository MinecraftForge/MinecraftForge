package net.minecraftforge.debug.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.PistonEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * This test mod blocks pistons from moving cobblestone at all except indirectly
 * This test mod adds a block that moves upwards when pushed by a piston
 * This test mod informs the user what will happen the piston and affected blocks when changes are made
 */
@Mod.EventBusSubscriber(modid = PistonEventTest.MODID)
@Mod(modid = PistonEventTest.MODID)
public class PistonEventTest {

    public static final String MODID = "pistoneventtest";

    private static Block shiftOnMove = new BlockShiftOnMove();

    @SubscribeEvent
    public static void regItem(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new ItemBlock(shiftOnMove).setRegistryName(shiftOnMove.getRegistryName()));
    }

    @SubscribeEvent
    public static void regBlock(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().register(shiftOnMove);
    }

    @SubscribeEvent
    public static void onPistonExtend(PistonEvent.Extend event)
    {
        if (event.getWorld().isRemote)
        {
            BlockPistonStructureHelper pistonHelper = new BlockPistonStructureHelper(event.getWorld(), event.getPos(), event.getFacing(), true);
            EntityPlayer player = Minecraft.getMinecraft().player;
            if (pistonHelper.canMove()) 
            {
                player.sendMessage(new TextComponentString(String.format("Piston will extend moving %d blocks and destroy %d blocks", pistonHelper.getBlocksToMove().size(), pistonHelper.getBlocksToDestroy().size())));
            }
            else
            {
                player.sendMessage(new TextComponentString("Piston won't extend"));
            }
        }

        // Make the block move up and out of the way so long as it won't replace the piston
        World world = event.getWorld();
        BlockPos pushedBlockPos = event.getFaceOffsetPos();
        if (world.getBlockState(pushedBlockPos).getBlock() == shiftOnMove && event.getFacing() != EnumFacing.DOWN)
        {
            world.setBlockToAir(pushedBlockPos);
            world.setBlockState(pushedBlockPos.up(), shiftOnMove.getDefaultState());
        }
        
        // Block pushing cobblestone (directly, indirectly works)
        event.setCanceled(event.getWorld().getBlockState(event.getFaceOffsetPos()).getBlock() == Blocks.COBBLESTONE);
    }

    @SubscribeEvent
    public static void onPistonRetract(PistonEvent.Retract event)
    {
        boolean isSticky = event.getWorld().getBlockState(event.getPos()).getBlock() == Blocks.STICKY_PISTON;
        if (event.getWorld().isRemote)
        {
            EntityPlayer player = Minecraft.getMinecraft().player;
            if (isSticky)
            {
                BlockPos targetPos = event.getFaceOffsetPos().offset(event.getFacing());
                boolean canPush = BlockPistonBase.canPush(event.getWorld().getBlockState(targetPos), event.getWorld(), event.getFaceOffsetPos(), event.getFacing().getOpposite(), false, event.getFacing());
                boolean isAir = event.getWorld().isAirBlock(targetPos);
                player.sendMessage(new TextComponentString(String.format("Piston will retract moving %d blocks", !isAir && canPush ? 1 : 0)));
            }
            else
            {
                player.sendMessage(new TextComponentString("Piston will retract"));
            }
        }
        // Offset twice to see if retraction will pull cobblestone
        event.setCanceled(event.getWorld().getBlockState(event.getFaceOffsetPos().offset(event.getFacing())).getBlock() == Blocks.COBBLESTONE && isSticky);
    }

    public static class BlockShiftOnMove extends Block
    {
        public static String blockName = "shiftonmove";
        public static ResourceLocation blockId = new ResourceLocation(MODID, blockName);

        public BlockShiftOnMove()
        {
            super(Material.ROCK);
            setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
            setUnlocalizedName(MODID + "." + blockName);
            setRegistryName(blockId);
            setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        }

    }

}
