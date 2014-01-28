package net.minecraft.network.login;

import net.minecraft.network.INetHandler;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;

public interface INetHandlerLoginServer extends INetHandler
{
    void func_147316_a(C00PacketLoginStart var1);

    void func_147315_a(C01PacketEncryptionResponse var1);
}