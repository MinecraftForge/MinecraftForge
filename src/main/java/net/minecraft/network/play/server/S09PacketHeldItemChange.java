package net.minecraft.network.play.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S09PacketHeldItemChange extends Packet
{
    private int field_149387_a;
    private static final String __OBFID = "CL_00001324";

    public S09PacketHeldItemChange() {}

    public S09PacketHeldItemChange(int p_i45215_1_)
    {
        this.field_149387_a = p_i45215_1_;
    }

    public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_149387_a = p_148837_1_.readByte();
    }

    public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeByte(this.field_149387_a);
    }

    public void func_148833_a(INetHandlerPlayClient p_149386_1_)
    {
        p_149386_1_.func_147257_a(this);
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayClient)p_148833_1_);
    }

    @SideOnly(Side.CLIENT)
    public int func_149385_c()
    {
        return this.field_149387_a;
    }
}