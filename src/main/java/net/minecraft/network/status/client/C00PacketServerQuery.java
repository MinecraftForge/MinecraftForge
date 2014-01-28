package net.minecraft.network.status.client;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.status.INetHandlerStatusServer;

public class C00PacketServerQuery extends Packet
{
    private static final String __OBFID = "CL_00001393";

    public void func_148837_a(PacketBuffer p_148837_1_) throws IOException {}

    public void func_148840_b(PacketBuffer p_148840_1_) throws IOException {}

    public void func_148833_a(INetHandlerStatusServer p_149287_1_)
    {
        p_149287_1_.func_147312_a(this);
    }

    public boolean func_148836_a()
    {
        return true;
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerStatusServer)p_148833_1_);
    }
}