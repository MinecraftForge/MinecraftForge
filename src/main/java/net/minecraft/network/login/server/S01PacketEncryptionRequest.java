package net.minecraft.network.login.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import java.security.PublicKey;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.util.CryptManager;

public class S01PacketEncryptionRequest extends Packet
{
    private String field_149612_a;
    private PublicKey field_149610_b;
    private byte[] field_149611_c;
    private static final String __OBFID = "CL_00001376";

    public S01PacketEncryptionRequest() {}

    public S01PacketEncryptionRequest(String p_i45268_1_, PublicKey p_i45268_2_, byte[] p_i45268_3_)
    {
        this.field_149612_a = p_i45268_1_;
        this.field_149610_b = p_i45268_2_;
        this.field_149611_c = p_i45268_3_;
    }

    public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_149612_a = p_148837_1_.func_150789_c(20);
        this.field_149610_b = CryptManager.decodePublicKey(func_148834_a(p_148837_1_));
        this.field_149611_c = func_148834_a(p_148837_1_);
    }

    public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.func_150785_a(this.field_149612_a);
        func_148838_a(p_148840_1_, this.field_149610_b.getEncoded());
        func_148838_a(p_148840_1_, this.field_149611_c);
    }

    public void func_148833_a(INetHandlerLoginClient p_149606_1_)
    {
        p_149606_1_.func_147389_a(this);
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerLoginClient)p_148833_1_);
    }

    @SideOnly(Side.CLIENT)
    public String func_149609_c()
    {
        return this.field_149612_a;
    }

    @SideOnly(Side.CLIENT)
    public PublicKey func_149608_d()
    {
        return this.field_149610_b;
    }

    @SideOnly(Side.CLIENT)
    public byte[] func_149607_e()
    {
        return this.field_149611_c;
    }
}