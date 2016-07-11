package net.minecraftforge.gui;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.network.ForgeMessage;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class OpenGuiMessage extends ForgeMessage
{
    private EntityPlayer player;
    private int windowID;
    private ResourceLocation guiID;
    private GuiProvider provider;
    private Object[] extraData;

    public OpenGuiMessage(){ }

    public OpenGuiMessage(EntityPlayer player, GuiProvider provider, Object... extraData)
    {
        this.player = player;
        this.windowID = player.openContainer.windowId;
        this.guiID = provider.getRegistryName();
        this.provider = provider;
        this.extraData = extraData;
    }

    @Override
    protected void fromBytes(ByteBuf buf)
    {
        windowID = buf.readInt();
        guiID = new ResourceLocation(ByteBufUtils.readUTF8String(buf));
        this.provider = ForgeRegistries.GUI_PROVIDERS.getValue(guiID);

        EntityPlayer player = FMLClientHandler.instance().getClientPlayerEntity();
        provider.deserialize(buf, player.worldObj, player);
    }

    @Override
    protected void toBytes(ByteBuf buf)
    {
        buf.writeInt(windowID);
        ByteBufUtils.writeUTF8String(buf, guiID.toString());
        provider.serialize(buf, extraData);
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

            Object gui = msg.provider.clientElement(player.worldObj, player);
            FMLClientHandler.instance().showGuiScreen(gui);
            player.openContainer.windowId = msg.windowID;
        }
    }
}
