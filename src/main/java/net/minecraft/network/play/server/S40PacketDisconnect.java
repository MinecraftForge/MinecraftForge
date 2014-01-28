package net.minecraft.network.play.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.IChatComponent;

public class S40PacketDisconnect extends Packet
{
    private IChatComponent field_149167_a;
    private static final String __OBFID = "CL_00001298";

    public S40PacketDisconnect() {}

    public S40PacketDisconnect(IChatComponent p_i45191_1_)
    {
        this.field_149167_a = p_i45191_1_;
    }

    public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_149167_a = IChatComponent.Serializer.func_150699_a(p_148837_1_.func_150789_c(32767));
    }

    public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.func_150785_a(IChatComponent.Serializer.func_150696_a(this.field_149167_a));
    }

    public void func_148833_a(INetHandlerPlayClient p_149166_1_)
    {
        p_149166_1_.func_147253_a(this);
    }

    public boolean func_148836_a()
    {
        return true;
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayClient)p_148833_1_);
    }

    @SideOnly(Side.CLIENT)
    public IChatComponent func_149165_c()
    {
        return this.field_149167_a;
    }
}