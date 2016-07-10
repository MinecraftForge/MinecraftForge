package net.minecraftforge.gui;

import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.gui.capability.IGuiProvider;

public class ForgeGuiHandler
{
    public static void openGui(EntityPlayer player, World worldObj, IGuiProvider provider)
    {
        if (provider == null) return;

        Object owner = provider.getOwner(player, worldObj);
        if (worldObj.isRemote)
        {
            Object gui = provider.getClientGuiElement(player, worldObj, owner);
            FMLCommonHandler.instance().showGuiScreen(gui);
            return;
        }

        if (player instanceof FakePlayer || player instanceof EntityPlayerSP) return;
        EntityPlayerMP playerMP = (EntityPlayerMP) player;

        Container container = provider.getServerGuiElement(player, worldObj, owner);
        if (container == null) return;

        playerMP.getNextWindowId();
        playerMP.closeContainer();
        int windowId = playerMP.currentWindowId;

        OpenGuiMessage openGui = new OpenGuiMessage(provider);
        EmbeddedChannel embeddedChannel = FMLNetworkHandler.getServerChannel();
        embeddedChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
        embeddedChannel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(playerMP);
        embeddedChannel.writeOutbound(openGui);

        playerMP.openContainer = container;
        playerMP.openContainer.windowId = windowId;
        playerMP.openContainer.addListener(playerMP);

        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(player, player.openContainer));
    }
}
