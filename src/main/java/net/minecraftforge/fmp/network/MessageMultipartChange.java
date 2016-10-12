package net.minecraftforge.fmp.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fmp.multipart.IMultipart;
import net.minecraftforge.fmp.multipart.IMultipartContainer;
import net.minecraftforge.fmp.multipart.MultipartHelper;
import net.minecraftforge.fmp.multipart.MultipartRegistry;

public class MessageMultipartChange implements IMessage, IMessageHandler<MessageMultipartChange, MessageMultipartChange>
{
    
    private Type type;
    private UUID partID;
    private ResourceLocation partType;
    private IMultipart part;
    private BlockPos pos;
    private byte[] data;

    private MessageMultipartChange(Type type, UUID partID, ResourceLocation partType, IMultipart part, BlockPos pos)
    {
        this.type = type;
        this.partID = partID;
        this.partType = partType;
        this.part = part;
        this.pos = pos;
    }

    public MessageMultipartChange()
    {
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(type.ordinal());
        buf.writeLong(partID.getMostSignificantBits());
        buf.writeLong(partID.getLeastSignificantBits());
        ByteBufUtils.writeUTF8String(buf, part.getType().toString());
        buf.writeInt(pos.getX()).writeInt(pos.getY()).writeInt(pos.getZ());

        if (type == Type.ADD || type == Type.UPDATE || type == Type.UPDATE_RERENDER)
        {
            ByteBuf dataBuf = Unpooled.buffer();
            part.writeUpdatePacket(new PacketBuffer(dataBuf));
            data = dataBuf.array();
            dataBuf.clear();
            buf.writeMedium(data.length);
            buf.writeBytes(data);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        type = Type.VALUES[buf.readInt()];

        long msb = buf.readLong();
        long lsb = buf.readLong();
        partID = new UUID(msb, lsb);

        partType = new ResourceLocation(ByteBufUtils.readUTF8String(buf));
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());

        if (type == Type.ADD || type == Type.UPDATE || type == Type.UPDATE_RERENDER)
        {
            data = new byte[buf.readUnsignedMedium()];
            buf.readBytes(data, 0, data.length);
        }
    }

    @Override
    public MessageMultipartChange onMessage(MessageMultipartChange message, MessageContext ctx)
    {
        if (ctx.side == Side.CLIENT)
        {
            schedulePacketHandling(message, ctx.getClientHandler());
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void schedulePacketHandling(final MessageMultipartChange message, INetHandler netHandler)
    {
        FMLCommonHandler.instance().getWorldThread(netHandler).addScheduledTask(new Runnable()
        {
            @Override
            public void run()
            {
                MessageMultipartChange.handlePacket(message);
            }
        });
    }

    @SideOnly(Side.CLIENT)
    private static void handlePacket(MessageMultipartChange message)
    {
        EntityPlayer player = FMLClientHandler.instance().getClientPlayerEntity();
        if (player == null || message.pos == null || message.type == null)
        {
            return;
        }
        World world = player.worldObj;
        if (world == null)
        {
            return;
        }

        if (message.type == Type.ADD)
        {
            message.part = MultipartRegistry.createPart(message.partType, new PacketBuffer(Unpooled.copiedBuffer(message.data)));
            MultipartHelper.addPart(world, message.pos, message.part, message.partID);

            if (message.part.getModelPath() != null)
            {
                world.markBlockRangeForRenderUpdate(message.pos, message.pos);
            }
            world.checkLight(message.pos);
        }
        else if (message.type == Type.REMOVE)
        {
            IMultipartContainer container = MultipartHelper.getPartContainer(world, message.pos);
            if (container != null)
            {
                message.part = container.getPartFromID(message.partID);
                if (message.part != null)
                {
                    container.removePart(message.part);
                    if (message.part.getModelPath() != null)
                    {
                        world.markBlockRangeForRenderUpdate(message.pos, message.pos);
                    }
                }
                world.checkLight(message.pos);
            }
        }
        else if (message.type == Type.UPDATE || message.type == Type.UPDATE_RERENDER)
        {
            IMultipartContainer container = MultipartHelper.getPartContainer(world, message.pos);
            if (container == null)
            {
                return;
            }
            message.part = container.getPartFromID(message.partID);

            if (message.part != null)
            {
                message.part.readUpdatePacket(new PacketBuffer(Unpooled.copiedBuffer(message.data)));

                if (message.type == Type.UPDATE_RERENDER)
                {
                    world.markBlockRangeForRenderUpdate(message.pos, message.pos);
                }
            }
        }
    }

    public void send(World world)
    {
        MultipartNetworkHandler.sendToAllWatching(this, world, pos);
    }

    public static MessageMultipartChange newPacket(World world, BlockPos pos, IMultipart part, Type type)
    {
        IMultipartContainer container = MultipartHelper.getPartContainer(world, pos);
        if (container == null)
        {
            throw new IllegalStateException("Attempted to " + type.name().toLowerCase() + " a multipart at an illegal position!");
        }
        return new MessageMultipartChange(type, container.getPartID(part), part.getType(), part, pos);
    }

    public static enum Type
    {
        ADD,
        REMOVE,
        UPDATE,
        UPDATE_RERENDER;

        public static final Type[] VALUES = values();
    }

}
