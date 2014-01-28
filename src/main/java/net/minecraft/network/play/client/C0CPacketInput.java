package net.minecraft.network.play.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C0CPacketInput extends Packet
{
    private float field_149624_a;
    private float field_149622_b;
    private boolean field_149623_c;
    private boolean field_149621_d;
    private static final String __OBFID = "CL_00001367";

    public C0CPacketInput() {}

    @SideOnly(Side.CLIENT)
    public C0CPacketInput(float p_i45261_1_, float p_i45261_2_, boolean p_i45261_3_, boolean p_i45261_4_)
    {
        this.field_149624_a = p_i45261_1_;
        this.field_149622_b = p_i45261_2_;
        this.field_149623_c = p_i45261_3_;
        this.field_149621_d = p_i45261_4_;
    }

    public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_149624_a = p_148837_1_.readFloat();
        this.field_149622_b = p_148837_1_.readFloat();
        this.field_149623_c = p_148837_1_.readBoolean();
        this.field_149621_d = p_148837_1_.readBoolean();
    }

    public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeFloat(this.field_149624_a);
        p_148840_1_.writeFloat(this.field_149622_b);
        p_148840_1_.writeBoolean(this.field_149623_c);
        p_148840_1_.writeBoolean(this.field_149621_d);
    }

    public void func_148833_a(INetHandlerPlayServer p_149619_1_)
    {
        p_149619_1_.func_147358_a(this);
    }

    public float func_149620_c()
    {
        return this.field_149624_a;
    }

    public float func_149616_d()
    {
        return this.field_149622_b;
    }

    public boolean func_149618_e()
    {
        return this.field_149623_c;
    }

    public boolean func_149617_f()
    {
        return this.field_149621_d;
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayServer)p_148833_1_);
    }
}