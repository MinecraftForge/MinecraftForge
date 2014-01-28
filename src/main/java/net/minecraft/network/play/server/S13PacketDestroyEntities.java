package net.minecraft.network.play.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S13PacketDestroyEntities extends Packet
{
    private int[] field_149100_a;
    private static final String __OBFID = "CL_00001320";

    public S13PacketDestroyEntities() {}

    public S13PacketDestroyEntities(int ... p_i45211_1_)
    {
        this.field_149100_a = p_i45211_1_;
    }

    public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_149100_a = new int[p_148837_1_.readByte()];

        for (int i = 0; i < this.field_149100_a.length; ++i)
        {
            this.field_149100_a[i] = p_148837_1_.readInt();
        }
    }

    public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeByte(this.field_149100_a.length);

        for (int i = 0; i < this.field_149100_a.length; ++i)
        {
            p_148840_1_.writeInt(this.field_149100_a[i]);
        }
    }

    public void func_148833_a(INetHandlerPlayClient p_149099_1_)
    {
        p_149099_1_.func_147238_a(this);
    }

    public String func_148835_b()
    {
        StringBuilder stringbuilder = new StringBuilder();

        for (int i = 0; i < this.field_149100_a.length; ++i)
        {
            if (i > 0)
            {
                stringbuilder.append(", ");
            }

            stringbuilder.append(this.field_149100_a[i]);
        }

        return String.format("entities=%d[%s]", new Object[] {Integer.valueOf(this.field_149100_a.length), stringbuilder});
    }

    @SideOnly(Side.CLIENT)
    public int[] func_149098_c()
    {
        return this.field_149100_a;
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayClient)p_148833_1_);
    }
}