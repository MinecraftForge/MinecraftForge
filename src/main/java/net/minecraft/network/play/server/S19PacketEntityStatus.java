package net.minecraft.network.play.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

public class S19PacketEntityStatus extends Packet
{
    private int field_149164_a;
    private byte field_149163_b;
    private static final String __OBFID = "CL_00001299";

    public S19PacketEntityStatus() {}

    public S19PacketEntityStatus(Entity p_i45192_1_, byte p_i45192_2_)
    {
        this.field_149164_a = p_i45192_1_.func_145782_y();
        this.field_149163_b = p_i45192_2_;
    }

    public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_149164_a = p_148837_1_.readInt();
        this.field_149163_b = p_148837_1_.readByte();
    }

    public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeInt(this.field_149164_a);
        p_148840_1_.writeByte(this.field_149163_b);
    }

    public void func_148833_a(INetHandlerPlayClient p_149162_1_)
    {
        p_149162_1_.func_147236_a(this);
    }

    @SideOnly(Side.CLIENT)
    public Entity func_149161_a(World p_149161_1_)
    {
        return p_149161_1_.getEntityByID(this.field_149164_a);
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayClient)p_148833_1_);
    }

    @SideOnly(Side.CLIENT)
    public byte func_149160_c()
    {
        return this.field_149163_b;
    }
}