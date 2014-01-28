package net.minecraft.network.play.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C11PacketEnchantItem extends Packet
{
    private int field_149541_a;
    private int field_149540_b;
    private static final String __OBFID = "CL_00001352";

    public C11PacketEnchantItem() {}

    @SideOnly(Side.CLIENT)
    public C11PacketEnchantItem(int p_i45245_1_, int p_i45245_2_)
    {
        this.field_149541_a = p_i45245_1_;
        this.field_149540_b = p_i45245_2_;
    }

    public void func_148833_a(INetHandlerPlayServer p_149538_1_)
    {
        p_149538_1_.func_147338_a(this);
    }

    public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_149541_a = p_148837_1_.readByte();
        this.field_149540_b = p_148837_1_.readByte();
    }

    public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeByte(this.field_149541_a);
        p_148840_1_.writeByte(this.field_149540_b);
    }

    public String func_148835_b()
    {
        return String.format("id=%d, button=%d", new Object[] {Integer.valueOf(this.field_149541_a), Integer.valueOf(this.field_149540_b)});
    }

    public int func_149539_c()
    {
        return this.field_149541_a;
    }

    public int func_149537_d()
    {
        return this.field_149540_b;
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayServer)p_148833_1_);
    }
}