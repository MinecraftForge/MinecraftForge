package net.minecraftforge.gui;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.common.network.ForgeMessage;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.FMLNetworkException;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.gui.capability.IGuiProvider;

public class OpenGuiMessage extends ForgeMessage
{
    private IGuiProvider provider;
    private EnumHand hand;
    private int identifier;
    private Class<? extends IGuiProvider> providerClass;

    public OpenGuiMessage(){ }

    public OpenGuiMessage(EntityPlayer player, IGuiProvider provider, EnumHand hand)
    {
        this.identifier = player.openContainer.windowId;
        this.provider = provider;
        this.providerClass = provider.getClass();
        this.hand = hand;
    }

    @Override
    protected void fromBytes(ByteBuf buf)
    {
        hand = buf.readBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
        identifier = buf.readInt();
        String className = ByteBufUtils.readUTF8String(buf);
        try
        {
            Class classAttempt = Class.forName(className);
            if(classAttempt != null)
            {
                provider = (IGuiProvider) classAttempt.newInstance();
                if(provider != null) provider.deserialize(buf);
            }
        }
        catch (ClassNotFoundException e)
        {
            FMLLog.severe("%s", e);
            return;
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
            throw new FMLNetworkException("Error instantiating gui handler class: " + className);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(hand == EnumHand.MAIN_HAND);
        buf.writeInt(identifier);
        ByteBufUtils.writeUTF8String(buf, provider.getClass().getName());
        provider.serialize(buf);
    }

    public static class Handler extends SimpleChannelInboundHandler<OpenGuiMessage>    {

        @SuppressWarnings("unused")
        public Handler() {}

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, final OpenGuiMessage msg) throws Exception
        {
            IThreadListener thread = FMLCommonHandler.instance().getWorldThread(ctx.channel().attr(NetworkRegistry.NET_HANDLER).get());
            if (thread.isCallingFromMinecraftThread())
            {
                process(msg);
            }
            else
            {
                thread.addScheduledTask(new Runnable()
                {
                    public void run()
                    {
                        Handler.this.process(msg);
                    }
                });
            }
        }

        private void process(OpenGuiMessage msg)
        {
            EntityPlayer player = FMLClientHandler.instance().getClientPlayerEntity();
            Object owner = msg.provider.getOwner(player, player.worldObj, msg.hand);

            if(owner == null) return;
            Object gui = msg.provider.getClientGuiElement(player, player.worldObj, owner);
            FMLClientHandler.instance().showGuiScreen(gui);
            player.openContainer.windowId = msg.identifier;
        }
    }
}
