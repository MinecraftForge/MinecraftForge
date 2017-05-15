package net.minecraftforge.gui;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.common.network.ForgeMessage;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GuiRegistry;
import net.minecraftforge.gui.capability.IGuiProvider;

public class OpenGuiMessage extends ForgeMessage
{
    private GuiRegistry.GuiProvider provider;
    private IGuiProvider instance;
    private Object[] extraData;
    private int identifier;
    private EntityPlayer player;

    public OpenGuiMessage(){ }

    public OpenGuiMessage(EntityPlayer player, IGuiProvider provider, Object... extraData)
    {
        this.identifier = player.openContainer.windowId;
        this.provider = (GuiRegistry.GuiProvider) GuiRegistry.getRegistry().getObject(provider.getGuiIdentifier());
        this.instance = provider;
        this.extraData = extraData;
        this.player = player;
    }

    @Override
    protected void fromBytes(ByteBuf buf)
    {
        identifier = buf.readInt();
        int guiID = buf.readInt();
        this.provider = (GuiRegistry.GuiProvider) GuiRegistry.getRegistry().getObjectById(guiID);
        this.instance = provider.getInstance().deserialize(buf);
    }

    @Override
    protected void toBytes(ByteBuf buf)
    {
        buf.writeInt(identifier);
        buf.writeInt(GuiRegistry.getRegistry().getId(provider));
        instance.serialize(buf, extraData);
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
            Object owner = msg.provider.getInstance().getOwner(player.worldObj, player);

            if(owner == null) return;
            Object gui = msg.provider.getInstance().clientElement(player.worldObj, player);
            FMLClientHandler.instance().showGuiScreen(gui);
            player.openContainer.windowId = msg.identifier;
        }
    }
}
