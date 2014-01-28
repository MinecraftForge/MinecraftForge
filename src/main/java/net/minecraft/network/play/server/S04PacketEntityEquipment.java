package net.minecraft.network.play.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S04PacketEntityEquipment extends Packet
{
    private int field_149394_a;
    private int field_149392_b;
    private ItemStack field_149393_c;
    private static final String __OBFID = "CL_00001330";

    public S04PacketEntityEquipment() {}

    public S04PacketEntityEquipment(int p_i45221_1_, int p_i45221_2_, ItemStack p_i45221_3_)
    {
        this.field_149394_a = p_i45221_1_;
        this.field_149392_b = p_i45221_2_;
        this.field_149393_c = p_i45221_3_ == null ? null : p_i45221_3_.copy();
    }

    public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_149394_a = p_148837_1_.readInt();
        this.field_149392_b = p_148837_1_.readShort();
        this.field_149393_c = p_148837_1_.func_150791_c();
    }

    public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeInt(this.field_149394_a);
        p_148840_1_.writeShort(this.field_149392_b);
        p_148840_1_.func_150788_a(this.field_149393_c);
    }

    public void func_148833_a(INetHandlerPlayClient p_149391_1_)
    {
        p_149391_1_.func_147242_a(this);
    }

    @SideOnly(Side.CLIENT)
    public ItemStack func_149390_c()
    {
        return this.field_149393_c;
    }

    public String func_148835_b()
    {
        return String.format("entity=%d, slot=%d, item=%s", new Object[] {Integer.valueOf(this.field_149394_a), Integer.valueOf(this.field_149392_b), this.field_149393_c});
    }

    @SideOnly(Side.CLIENT)
    public int func_149389_d()
    {
        return this.field_149394_a;
    }

    @SideOnly(Side.CLIENT)
    public int func_149388_e()
    {
        return this.field_149392_b;
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayClient)p_148833_1_);
    }
}