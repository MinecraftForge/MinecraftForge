package net.minecraftforge.debug;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPistonExtension.EnumPistonType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.PistonEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = EventPistonDebug.MODID)
public class EventPistonDebug
{
    public static final String MODID = "EventPistonDebug";
    
    private static Block shiftOnMove = new BlockShiftOnMove();
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        GameRegistry.register(shiftOnMove);
        GameRegistry.register(new ItemBlock(shiftOnMove).setRegistryName(shiftOnMove.getRegistryName()));
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPistonExtend(PistonEvent.PistonExtendEvent event)
    {
        if (event.getWorld().isRemote)
        {
            BlockPistonStructureHelper pistonHelper =
                    new BlockPistonStructureHelper(event.getWorld(), event.getPos(), event.getFacing(), true);
            for (EntityPlayer player : event.getWorld().playerEntities)
            {
                if (pistonHelper.canMove())
                    player.addChatMessage(new TextComponentString(String.format("Piston will extend moving %d blocks and destroy %d blocks",
                            pistonHelper.getBlocksToMove().size(), pistonHelper.getBlocksToDestroy().size())));
                else
                    player.addChatMessage(new TextComponentString("Piston won't extend"));
            }
        } else {
            World world = event.getWorld();
            BlockPos pushedBlockPos = event.getPos().offset(event.getFacing());
            if (world.getBlockState(pushedBlockPos).getBlock().equals(shiftOnMove) && event.getFacing() != EnumFacing.DOWN)
            {
                world.setBlockToAir(pushedBlockPos);
                world.setBlockState(pushedBlockPos.up(), shiftOnMove.getDefaultState());
            }
        }
        event.setCanceled(event.getWorld().getBlockState(event.getPos().offset(event.getFacing())).getBlock() == Blocks.COBBLESTONE);
    }

    @SubscribeEvent
    public void onPistonRetract(PistonEvent.PistonRetractEvent event)
    {
        if (!event.getWorld().isRemote)
        {
            for (EntityPlayer player : event.getWorld().playerEntities)
            {
                boolean isSticky = event.getWorld().getBlockState(event.getPos()).getValue(BlockPistonExtension.TYPE) == EnumPistonType.STICKY;
                if (isSticky)
                {
                    BlockPistonStructureHelper pistonHelper =
                            new BlockPistonStructureHelper(event.getWorld(), event.getPos(), event.getFacing(), false);
                    if (pistonHelper.canMove())
                        player.addChatMessage(new TextComponentString(String.format("Piston will retract moving %d blocks and destroy %d blocks",
                                pistonHelper.getBlocksToMove().size(), pistonHelper.getBlocksToDestroy().size())));
                    else
                        player.addChatMessage(new TextComponentString("Piston won't retract"));
                } else {
                    player.addChatMessage(new TextComponentString("Piston will retract"));
                }
            }
        }
        event.setCanceled(event.getWorld().getBlockState(event.getPos().offset(event.getFacing(), 2)).getBlock() == Blocks.COBBLESTONE);
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