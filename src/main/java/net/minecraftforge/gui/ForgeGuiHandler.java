package net.minecraftforge.gui;

import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.network.ForgeNetworkHandler;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.relauncher.Side;

public class ForgeGuiHandler
{
    public static void openGui(EntityPlayer player, World worldObj, GuiProvider provider, Object... extras)
    {
        if (provider == null) return;

        if (worldObj.isRemote)
        {
            Object gui = provider.clientElement(worldObj, player);
            FMLCommonHandler.instance().showGuiScreen(gui);
            return;
        }

        if (player instanceof EntityPlayerMP && !(player instanceof FakePlayer))
            openGuiRemote((EntityPlayerMP) player, worldObj, provider, extras);
    }

    private static void openGuiRemote(EntityPlayerMP playerMP, World world, GuiProvider provider, Object... extras)
    {
        Container container = provider.serverElement(world, playerMP);
        if (container != null)
        {
            playerMP.getNextWindowId();
            playerMP.closeContainer();
            int windowId = playerMP.currentWindowId;

            OpenGuiMessage openGui = new OpenGuiMessage(playerMP, provider, extras);
            EmbeddedChannel embeddedChannel = ForgeNetworkHandler.getChannel(Side.SERVER);
            embeddedChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
            embeddedChannel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(playerMP);
            embeddedChannel.writeOutbound(openGui);

            playerMP.openContainer = container;
            playerMP.openContainer.windowId = windowId;
            playerMP.openContainer.addListener(playerMP);

            MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(playerMP, playerMP.openContainer));
        }
    }
}
