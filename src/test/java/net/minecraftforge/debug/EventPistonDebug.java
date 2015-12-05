package net.minecraftforge.debug;

import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPistonExtension.EnumPistonType;
import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChatComponentText;
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
		if (event.world.isRemote) {
			BlockPistonStructureHelper pistonHelper =
					new BlockPistonStructureHelper(event.world, event.pos, event.facing, true);
			for (EntityPlayer player : event.world.playerEntities) {
				if (pistonHelper.canMove())
					player.addChatMessage(new ChatComponentText(String.format("Piston will extend moving %d blocks and destroy %d blocks",
							pistonHelper.getBlocksToMove().size(), pistonHelper.getBlocksToDestroy().size())));
				else
					player.addChatMessage(new ChatComponentText("Piston won't extend"));
			}
		}
		event.setCanceled(event.world.getBlockState(event.pos.offset(event.facing)).getBlock() == Blocks.cobblestone);
	}

	@SubscribeEvent
	public void onPistonRetract(PistonEvent.PistonRetractEvent event) {
		if (!event.world.isRemote) {
			for (EntityPlayer player : event.world.playerEntities) {
				boolean isSticky = event.world.getBlockState(event.pos).getValue(BlockPistonExtension.TYPE) == EnumPistonType.STICKY;
				if (isSticky) {
					BlockPistonStructureHelper pistonHelper =
							new BlockPistonStructureHelper(event.world, event.pos, event.facing, false);
					if (pistonHelper.canMove())
						player.addChatMessage(new ChatComponentText(String.format("Piston will retract moving %d blocks and destroy %d blocks",
								pistonHelper.getBlocksToMove().size(), pistonHelper.getBlocksToDestroy().size())));
					else
						player.addChatMessage(new ChatComponentText("Piston won't retract"));
				} else {
					player.addChatMessage(new ChatComponentText("Piston will retract"));
				}
			}
		}
		event.setCanceled(event.world.getBlockState(event.pos.offset(event.facing, 2)).getBlock() == Blocks.cobblestone);
	}
}
