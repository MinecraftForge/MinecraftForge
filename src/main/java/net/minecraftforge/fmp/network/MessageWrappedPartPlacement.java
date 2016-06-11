package net.minecraftforge.fmp.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.RayTraceUtils;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fmp.item.PartPlacementWrapper;

public class MessageWrappedPartPlacement implements IMessage, IMessageHandler<MessageWrappedPartPlacement, MessageWrappedPartPlacement>
{
    
    private String wrapper;
    private EnumHand hand;

    public MessageWrappedPartPlacement(String handler, EnumHand hand)
    {
        this.wrapper = handler;
        this.hand = hand;
    }

    public MessageWrappedPartPlacement()
    {
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, wrapper);
        buf.writeInt(hand.ordinal());
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        wrapper = ByteBufUtils.readUTF8String(buf);
        hand = EnumHand.values()[buf.readInt()];
    }

    @Override
    public MessageWrappedPartPlacement onMessage(final MessageWrappedPartPlacement message, final MessageContext ctx)
    {
        if (ctx.side == Side.SERVER)
        {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    MessageWrappedPartPlacement.handlePacket(message, ctx.getServerHandler().playerEntity);
                }
            });
        }
        return null;
    }

    private static void handlePacket(MessageWrappedPartPlacement message, EntityPlayer player)
    {
        World world = player.worldObj;
        RayTraceResult mop = world.rayTraceBlocks(RayTraceUtils.getStart(player), RayTraceUtils.getEnd(player));
        if (mop == null || mop.typeOfHit != Type.BLOCK)
        {
            return;
        }
        BlockPos pos = mop.getBlockPos();
        EnumFacing side = mop.sideHit;
        Vec3d hit = mop.hitVec.subtract(new Vec3d(mop.getBlockPos()));
        ItemStack stack = player.getHeldItem(message.hand);

        if (PartPlacementWrapper.getWrapper(message.wrapper).doPlace(world, pos, side, hit, stack, player))
        {
            player.swingArm(message.hand);
        }
    }

    public void send()
    {
        MultipartNetworkHandler.sendToServer(this);
    }

}
