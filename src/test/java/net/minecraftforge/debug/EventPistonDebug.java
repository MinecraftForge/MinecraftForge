package net.minecraftforge.debug;

import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPistonExtension.EnumPistonType;
import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.PistonEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = EventPistonDebug.MODID)
public class EventPistonDebug {
    public static final String MODID = "EventPistonDebug";

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPistonExtend(PistonEvent.PistonExtendEvent event) {
        if (event.getWorld().isRemote) {
            BlockPistonStructureHelper pistonHelper =
                    new BlockPistonStructureHelper(event.getWorld(), event.getPos(), event.getFacing(), true);
            for (EntityPlayer player : event.getWorld().playerEntities) {
                if (pistonHelper.canMove())
                    player.addChatMessage(new TextComponentString(String.format("Piston will extend moving %d blocks and destroy %d blocks",
                            pistonHelper.getBlocksToMove().size(), pistonHelper.getBlocksToDestroy().size())));
                else
                    player.addChatMessage(new TextComponentString("Piston won't extend"));
            }
        }
        event.setCanceled(event.getWorld().getBlockState(event.getPos().offset(event.getFacing())).getBlock() == Blocks.COBBLESTONE);
    }

    @SubscribeEvent
    public void onPistonRetract(PistonEvent.PistonRetractEvent event) {
        if (!event.getWorld().isRemote) {
            for (EntityPlayer player : event.getWorld().playerEntities) {
                boolean isSticky = event.getWorld().getBlockState(event.getPos()).getValue(BlockPistonExtension.TYPE) == EnumPistonType.STICKY;
                if (isSticky) {
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
}