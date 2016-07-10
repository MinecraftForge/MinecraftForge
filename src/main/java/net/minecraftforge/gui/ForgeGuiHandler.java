package net.minecraftforge.gui;

import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.network.ForgeNetworkHandler;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.gui.capability.IGuiProvider;

import javax.annotation.Nullable;
import java.util.HashMap;

public class ForgeGuiHandler
{
    public static void openGui(EntityPlayer player, World worldObj, IGuiProvider provider, @Nullable EnumHand hand)
    {
        if (provider == null) return;

        Object owner = provider.getOwner(player, worldObj, hand);
        if (worldObj.isRemote)
        {
            Object gui = provider.getClientGuiElement(player, worldObj, owner);
            FMLCommonHandler.instance().showGuiScreen(gui);
            return;
        }

        if (player instanceof EntityPlayerMP && !(player instanceof FakePlayer))
        {
            EntityPlayerMP playerMP = (EntityPlayerMP) player;

            Container container = provider.getServerGuiElement(player, worldObj, owner);
            if (container != null)
            {
                playerMP.getNextWindowId();
                playerMP.closeContainer();
                int windowId = playerMP.currentWindowId;

                OpenGuiMessage openGui = new OpenGuiMessage(player, provider, hand);
                EmbeddedChannel embeddedChannel = ForgeNetworkHandler.getChannel(Side.SERVER);
                embeddedChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
                embeddedChannel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(playerMP);
                embeddedChannel.writeOutbound(openGui);

                playerMP.openContainer = container;
                playerMP.openContainer.windowId = windowId;
                playerMP.openContainer.addListener(playerMP);

                net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(player, player.openContainer));
            }
        }
    }
}
