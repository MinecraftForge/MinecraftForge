package net.minecraft.network.play.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C10PacketCreativeInventoryAction extends Packet
{
    private int field_149629_a;
    private ItemStack field_149628_b;
    private static final String __OBFID = "CL_00001369";

    public C10PacketCreativeInventoryAction() {}

    @SideOnly(Side.CLIENT)
    public C10PacketCreativeInventoryAction(int p_i45263_1_, ItemStack p_i45263_2_)
    {
        this.field_149629_a = p_i45263_1_;
        this.field_149628_b = p_i45263_2_ != null ? p_i45263_2_.copy() : null;
    }

    public void func_148833_a(INetHandlerPlayServer p_149626_1_)
    {
        p_149626_1_.func_147344_a(this);
    }

    public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_149629_a = p_148837_1_.readShort();
        this.field_149628_b = p_148837_1_.func_150791_c();
    }

    public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeShort(this.field_149629_a);
        p_148840_1_.func_150788_a(this.field_149628_b);
    }

    public int func_149627_c()
    {
        return this.field_149629_a;
    }

    public ItemStack func_149625_d()
    {
        return this.field_149628_b;
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayServer)p_148833_1_);
    }
}