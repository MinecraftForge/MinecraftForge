package net.minecraft.network.play.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C00PacketKeepAlive extends Packet
{
    private int field_149461_a;
    private static final String __OBFID = "CL_00001359";

    public C00PacketKeepAlive() {}

    @SideOnly(Side.CLIENT)
    public C00PacketKeepAlive(int p_i45252_1_)
    {
        this.field_149461_a = p_i45252_1_;
    }

    public void func_148833_a(INetHandlerPlayServer p_149459_1_)
    {
        p_149459_1_.func_147353_a(this);
    }

    public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_149461_a = p_148837_1_.readInt();
    }

    public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeInt(this.field_149461_a);
    }

    public boolean func_148836_a()
    {
        return true;
    }

    public int func_149460_c()
    {
        return this.field_149461_a;
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayServer)p_148833_1_);
    }
}