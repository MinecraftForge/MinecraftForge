package net.minecraft.network.play.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import org.apache.commons.lang3.ArrayUtils;

public class S3APacketTabComplete extends Packet
{
    private String[] field_149632_a;
    private static final String __OBFID = "CL_00001288";

    public S3APacketTabComplete() {}

    public S3APacketTabComplete(String[] p_i45178_1_)
    {
        this.field_149632_a = p_i45178_1_;
    }

    public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_149632_a = new String[p_148837_1_.func_150792_a()];

        for (int i = 0; i < this.field_149632_a.length; ++i)
        {
            this.field_149632_a[i] = p_148837_1_.func_150789_c(32767);
        }
    }

    public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.func_150787_b(this.field_149632_a.length);
        String[] astring = this.field_149632_a;
        int i = astring.length;

        for (int j = 0; j < i; ++j)
        {
            String s = astring[j];
            p_148840_1_.func_150785_a(s);
        }
    }

    public void func_148833_a(INetHandlerPlayClient p_149631_1_)
    {
        p_149631_1_.func_147274_a(this);
    }

    @SideOnly(Side.CLIENT)
    public String[] func_149630_c()
    {
        return this.field_149632_a;
    }

    public String func_148835_b()
    {
        return String.format("candidates=\'%s\'", new Object[] {ArrayUtils.toString(this.field_149632_a)});
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayClient)p_148833_1_);
    }
}