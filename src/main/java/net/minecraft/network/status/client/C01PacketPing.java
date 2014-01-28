package net.minecraft.network.status.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.status.INetHandlerStatusServer;

public class C01PacketPing extends Packet
{
    private long field_149290_a;
    private static final String __OBFID = "CL_00001392";

    public C01PacketPing() {}

    @SideOnly(Side.CLIENT)
    public C01PacketPing(long p_i45276_1_)
    {
        this.field_149290_a = p_i45276_1_;
    }

    public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_149290_a = p_148837_1_.readLong();
    }

    public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeLong(this.field_149290_a);
    }

    public void func_148833_a(INetHandlerStatusServer p_149288_1_)
    {
        p_149288_1_.func_147311_a(this);
    }

    public boolean func_148836_a()
    {
        return true;
    }

    public long func_149289_c()
    {
        return this.field_149290_a;
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerStatusServer)p_148833_1_);
    }
}