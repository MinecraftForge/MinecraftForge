package net.minecraft.network.login.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.util.IChatComponent;

public class S00PacketDisconnect extends Packet
{
    private IChatComponent field_149605_a;
    private static final String __OBFID = "CL_00001377";

    public S00PacketDisconnect() {}

    public S00PacketDisconnect(IChatComponent p_i45269_1_)
    {
        this.field_149605_a = p_i45269_1_;
    }

    public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_149605_a = IChatComponent.Serializer.func_150699_a(p_148837_1_.func_150789_c(32767));
    }

    public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.func_150785_a(IChatComponent.Serializer.func_150696_a(this.field_149605_a));
    }

    public void func_148833_a(INetHandlerLoginClient p_149604_1_)
    {
        p_149604_1_.func_147388_a(this);
    }

    public boolean func_148836_a()
    {
        return true;
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerLoginClient)p_148833_1_);
    }

    @SideOnly(Side.CLIENT)
    public IChatComponent func_149603_c()
    {
        return this.field_149605_a;
    }
}