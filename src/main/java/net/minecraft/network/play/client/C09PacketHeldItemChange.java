package net.minecraft.network.play.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C09PacketHeldItemChange extends Packet
{
    private int field_149615_a;
    private static final String __OBFID = "CL_00001368";

    public C09PacketHeldItemChange() {}

    @SideOnly(Side.CLIENT)
    public C09PacketHeldItemChange(int p_i45262_1_)
    {
        this.field_149615_a = p_i45262_1_;
    }

    public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_149615_a = p_148837_1_.readShort();
    }

    public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeShort(this.field_149615_a);
    }

    public void func_148833_a(INetHandlerPlayServer p_149613_1_)
    {
        p_149613_1_.func_147355_a(this);
    }

    public int func_149614_c()
    {
        return this.field_149615_a;
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayServer)p_148833_1_);
    }
}