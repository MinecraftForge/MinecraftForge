package net.minecraft.network.play.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C0EPacketClickWindow extends Packet
{
    private int field_149554_a;
    private int field_149552_b;
    private int field_149553_c;
    private short field_149550_d;
    private ItemStack field_149551_e;
    private int field_149549_f;
    private static final String __OBFID = "CL_00001353";

    public C0EPacketClickWindow() {}

    @SideOnly(Side.CLIENT)
    public C0EPacketClickWindow(int p_i45246_1_, int p_i45246_2_, int p_i45246_3_, int p_i45246_4_, ItemStack p_i45246_5_, short p_i45246_6_)
    {
        this.field_149554_a = p_i45246_1_;
        this.field_149552_b = p_i45246_2_;
        this.field_149553_c = p_i45246_3_;
        this.field_149551_e = p_i45246_5_ != null ? p_i45246_5_.copy() : null;
        this.field_149550_d = p_i45246_6_;
        this.field_149549_f = p_i45246_4_;
    }

    public void func_148833_a(INetHandlerPlayServer p_149545_1_)
    {
        p_149545_1_.func_147351_a(this);
    }

    public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_149554_a = p_148837_1_.readByte();
        this.field_149552_b = p_148837_1_.readShort();
        this.field_149553_c = p_148837_1_.readByte();
        this.field_149550_d = p_148837_1_.readShort();
        this.field_149549_f = p_148837_1_.readByte();
        this.field_149551_e = p_148837_1_.func_150791_c();
    }

    public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeByte(this.field_149554_a);
        p_148840_1_.writeShort(this.field_149552_b);
        p_148840_1_.writeByte(this.field_149553_c);
        p_148840_1_.writeShort(this.field_149550_d);
        p_148840_1_.writeByte(this.field_149549_f);
        p_148840_1_.func_150788_a(this.field_149551_e);
    }

    public String func_148835_b()
    {
        return this.field_149551_e != null ? String.format("id=%d, slot=%d, button=%d, type=%d, itemid=%d, itemcount=%d, itemaux=%d", new Object[] {Integer.valueOf(this.field_149554_a), Integer.valueOf(this.field_149552_b), Integer.valueOf(this.field_149553_c), Integer.valueOf(this.field_149549_f), Integer.valueOf(Item.func_150891_b(this.field_149551_e.getItem())), Integer.valueOf(this.field_149551_e.stackSize), Integer.valueOf(this.field_149551_e.getItemDamage())}): String.format("id=%d, slot=%d, button=%d, type=%d, itemid=-1", new Object[] {Integer.valueOf(this.field_149554_a), Integer.valueOf(this.field_149552_b), Integer.valueOf(this.field_149553_c), Integer.valueOf(this.field_149549_f)});
    }

    public int func_149548_c()
    {
        return this.field_149554_a;
    }

    public int func_149544_d()
    {
        return this.field_149552_b;
    }

    public int func_149543_e()
    {
        return this.field_149553_c;
    }

    public short func_149547_f()
    {
        return this.field_149550_d;
    }

    public ItemStack func_149546_g()
    {
        return this.field_149551_e;
    }

    public int func_149542_h()
    {
        return this.field_149549_f;
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayServer)p_148833_1_);
    }
}