package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import org.apache.commons.lang3.StringUtils;

public class C14PacketTabComplete extends Packet
{
    private String field_149420_a;
    private static final String __OBFID = "CL_00001346";

    public C14PacketTabComplete() {}

    public C14PacketTabComplete(String p_i45239_1_)
    {
        this.field_149420_a = p_i45239_1_;
    }

    public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_149420_a = p_148837_1_.func_150789_c(32767);
    }

    public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.func_150785_a(StringUtils.substring(this.field_149420_a, 0, 32767));
    }

    public void func_148833_a(INetHandlerPlayServer p_149418_1_)
    {
        p_149418_1_.func_147341_a(this);
    }

    public String func_149419_c()
    {
        return this.field_149420_a;
    }

    public String func_148835_b()
    {
        return String.format("message=\'%s\'", new Object[] {this.field_149420_a});
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayServer)p_148833_1_);
    }
}