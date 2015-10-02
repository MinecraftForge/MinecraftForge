package net.minecraftforge.fml.debug;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(modid = "guihandlertest")
public class GuiHandlerDebug {

	private static Logger LOGGER = LogManager.getLogger();
	private static final boolean debug = true;

	@EventHandler
	public void onInit(FMLInitializationEvent event) 
	{
		if(debug)
		{
			FMLCommonHandler.instance().bus().register(this);
			NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
		}
	}

	@SubscribeEvent
	public void onPickup(PlayerEvent.ItemPickupEvent event)
	{
		event.player.openGui(this, 15, event.player.getEntityWorld(), event.player.getPosition());
	}

	public static class GuiContainer extends Container {

		@Override
		public boolean canInteractWith(EntityPlayer playerIn) {
			return true;
		}
	}

	public static class GuiHandler implements IGuiHandler {

		@Override
		public Object getServerGuiElement(int ID, EntityPlayer player, World world, BlockPos pos) {
			LOGGER.info("Received Server Gui Message: \n - ID: " + ID + "\n - player: " + player + "\n - world:" + world + "\n - pos: " + pos + "\n");
			return new GuiContainer();
		}

		@Override
		public Object getClientGuiElement(int ID, EntityPlayer player, World world, BlockPos pos) {
			LOGGER.info("Received Client Gui Message: \n - ID: " + ID + "\n - player: " + player + "\n - world:" + world + "\n - pos: " + pos + "\n");
			return null;
		}
		
	}
}
