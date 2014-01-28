package net.minecraft.network.status;

import net.minecraft.network.INetHandler;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;

public interface INetHandlerStatusServer extends INetHandler
{
    void func_147311_a(C01PacketPing var1);

    void func_147312_a(C00PacketServerQuery var1);
}