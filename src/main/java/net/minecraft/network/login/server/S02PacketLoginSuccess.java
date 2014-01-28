package net.minecraft.network.login.server;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;

public class S02PacketLoginSuccess extends Packet
{
    private GameProfile field_149602_a;
    private static final String __OBFID = "CL_00001375";

    public S02PacketLoginSuccess() {}

    public S02PacketLoginSuccess(GameProfile p_i45267_1_)
    {
        this.field_149602_a = p_i45267_1_;
    }

    public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
    {
        String s = p_148837_1_.func_150789_c(36);
        String s1 = p_148837_1_.func_150789_c(16);
        this.field_149602_a = new GameProfile(s, s1);
    }

    public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.func_150785_a(this.field_149602_a.getId());
        p_148840_1_.func_150785_a(this.field_149602_a.getName());
    }

    public void func_148833_a(INetHandlerLoginClient p_149601_1_)
    {
        p_149601_1_.func_147390_a(this);
    }

    public boolean func_148836_a()
    {
        return true;
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerLoginClient)p_148833_1_);
    }
}