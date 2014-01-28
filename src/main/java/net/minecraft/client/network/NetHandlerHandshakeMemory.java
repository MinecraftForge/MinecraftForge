package net.minecraft.client.network;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.INetHandlerHandshakeServer;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.NetHandlerLoginServer;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.Validate;

@SideOnly(Side.CLIENT)
public class NetHandlerHandshakeMemory implements INetHandlerHandshakeServer
{
    private final MinecraftServer field_147385_a;
    private final NetworkManager field_147384_b;
    private static final String __OBFID = "CL_00001445";

    public NetHandlerHandshakeMemory(MinecraftServer p_i45287_1_, NetworkManager p_i45287_2_)
    {
        this.field_147385_a = p_i45287_1_;
        this.field_147384_b = p_i45287_2_;
    }

    public void func_147383_a(C00Handshake p_147383_1_)
    {
        this.field_147384_b.func_150723_a(p_147383_1_.func_149594_c());
    }

    public void func_147231_a(IChatComponent p_147231_1_) {}

    public void func_147232_a(EnumConnectionState p_147232_1_, EnumConnectionState p_147232_2_)
    {
        Validate.validState(p_147232_2_ == EnumConnectionState.LOGIN || p_147232_2_ == EnumConnectionState.STATUS, "Unexpected protocol " + p_147232_2_, new Object[0]);

        switch (NetHandlerHandshakeMemory.SwitchEnumConnectionState.field_151263_a[p_147232_2_.ordinal()])
        {
            case 1:
                this.field_147384_b.func_150719_a(new NetHandlerLoginServer(this.field_147385_a, this.field_147384_b));
                break;
            case 2:
                throw new UnsupportedOperationException("NYI");
            default:
        }
    }

    public void func_147233_a() {}

    @SideOnly(Side.CLIENT)

    static final class SwitchEnumConnectionState
        {
            static final int[] field_151263_a = new int[EnumConnectionState.values().length];
            private static final String __OBFID = "CL_00001446";

            static
            {
                try
                {
                    field_151263_a[EnumConnectionState.LOGIN.ordinal()] = 1;
                }
                catch (NoSuchFieldError var2)
                {
                    ;
                }

                try
                {
                    field_151263_a[EnumConnectionState.STATUS.ordinal()] = 2;
                }
                catch (NoSuchFieldError var1)
                {
                    ;
                }
            }
        }
}