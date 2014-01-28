package net.minecraft.network.play.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.scoreboard.ScoreObjective;

public class S3BPacketScoreboardObjective extends Packet
{
    private String field_149343_a;
    private String field_149341_b;
    private int field_149342_c;
    private static final String __OBFID = "CL_00001333";

    public S3BPacketScoreboardObjective() {}

    public S3BPacketScoreboardObjective(ScoreObjective p_i45224_1_, int p_i45224_2_)
    {
        this.field_149343_a = p_i45224_1_.getName();
        this.field_149341_b = p_i45224_1_.getDisplayName();
        this.field_149342_c = p_i45224_2_;
    }

    public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_149343_a = p_148837_1_.func_150789_c(16);
        this.field_149341_b = p_148837_1_.func_150789_c(32);
        this.field_149342_c = p_148837_1_.readByte();
    }

    public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.func_150785_a(this.field_149343_a);
        p_148840_1_.func_150785_a(this.field_149341_b);
        p_148840_1_.writeByte(this.field_149342_c);
    }

    public void func_148833_a(INetHandlerPlayClient p_149340_1_)
    {
        p_149340_1_.func_147291_a(this);
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayClient)p_148833_1_);
    }

    @SideOnly(Side.CLIENT)
    public String func_149339_c()
    {
        return this.field_149343_a;
    }

    @SideOnly(Side.CLIENT)
    public String func_149337_d()
    {
        return this.field_149341_b;
    }

    @SideOnly(Side.CLIENT)
    public int func_149338_e()
    {
        return this.field_149342_c;
    }
}