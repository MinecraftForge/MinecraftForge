package net.minecraftforge.gui;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.gui.capability.IGuiProvider;

public class OpenGuiMessage implements IMessage
{
    private IGuiProvider provider;
    public OpenGuiMessage(IGuiProvider provider)
    {
        this.provider = provider;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        provider.deserialize(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
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
            WorldClient world = FMLClientHandler.instance().getWorldClient();
            Object owner = msg.provider.getOwner(player, world);

            if(owner == null) return;
            Object gui = msg.provider.getClientGuiElement(player, world, owner);
            FMLClientHandler.instance().showGuiScreen(gui);
        }
    }
}
