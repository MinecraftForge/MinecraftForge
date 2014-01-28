package net.minecraft.server.network;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import io.netty.util.concurrent.GenericFutureListener;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.crypto.SecretKey;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.INetHandlerLoginServer;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.CryptManager;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerLoginServer implements INetHandlerLoginServer
{
    private static final AtomicInteger field_147331_b = new AtomicInteger(0);
    private static final Logger field_147332_c = LogManager.getLogger();
    private static final Random field_147329_d = new Random();
    private final byte[] field_147330_e = new byte[4];
    private final MinecraftServer field_147327_f;
    public final NetworkManager field_147333_a;
    private NetHandlerLoginServer.LoginState field_147328_g;
    private int field_147336_h;
    private GameProfile field_147337_i;
    private String field_147334_j;
    private SecretKey field_147335_k;
    private static final String __OBFID = "CL_00001458";

    public NetHandlerLoginServer(MinecraftServer p_i45298_1_, NetworkManager p_i45298_2_)
    {
        this.field_147328_g = NetHandlerLoginServer.LoginState.HELLO;
        this.field_147334_j = "";
        this.field_147327_f = p_i45298_1_;
        this.field_147333_a = p_i45298_2_;
        field_147329_d.nextBytes(this.field_147330_e);
    }

    public void func_147233_a()
    {
        if (this.field_147328_g == NetHandlerLoginServer.LoginState.READY_TO_ACCEPT)
        {
            this.func_147326_c();
        }

        if (this.field_147336_h++ == FMLNetworkHandler.LOGIN_TIMEOUT)
        {
            this.func_147322_a("Took too long to log in");
        }
    }

    public void func_147322_a(String p_147322_1_)
    {
        try
        {
            field_147332_c.info("Disconnecting " + this.func_147317_d() + ": " + p_147322_1_);
            ChatComponentText chatcomponenttext = new ChatComponentText(p_147322_1_);
            this.field_147333_a.func_150725_a(new S00PacketDisconnect(chatcomponenttext), new GenericFutureListener[0]);
            this.field_147333_a.func_150718_a(chatcomponenttext);
        }
        catch (Exception exception)
        {
            field_147332_c.error("Error whilst disconnecting player", exception);
        }
    }

    public void func_147326_c()
    {
        if (!this.field_147337_i.isComplete())
        {
            UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + this.field_147337_i.getName()).getBytes(Charsets.UTF_8));
            this.field_147337_i = new GameProfile(uuid.toString().replaceAll("-", ""), this.field_147337_i.getName());
        }

        String s = this.field_147327_f.getConfigurationManager().func_148542_a(this.field_147333_a.getSocketAddress(), this.field_147337_i);

        if (s != null)
        {
            this.func_147322_a(s);
        }
        else
        {
            this.field_147328_g = NetHandlerLoginServer.LoginState.ACCEPTED;
            this.field_147333_a.func_150725_a(new S02PacketLoginSuccess(this.field_147337_i), new GenericFutureListener[0]);
            FMLNetworkHandler.fmlServerHandshake(this.field_147327_f.getConfigurationManager(), this.field_147333_a, this.field_147327_f.getConfigurationManager().func_148545_a(this.field_147337_i));
        }
    }

    public void func_147231_a(IChatComponent p_147231_1_)
    {
        field_147332_c.info(this.func_147317_d() + " lost connection: " + p_147231_1_.func_150260_c());
    }

    public String func_147317_d()
    {
        return this.field_147337_i != null ? this.field_147337_i.toString() + " (" + this.field_147333_a.getSocketAddress().toString() + ")" : String.valueOf(this.field_147333_a.getSocketAddress());
    }

    public void func_147232_a(EnumConnectionState p_147232_1_, EnumConnectionState p_147232_2_)
    {
        Validate.validState(this.field_147328_g == NetHandlerLoginServer.LoginState.ACCEPTED || this.field_147328_g == NetHandlerLoginServer.LoginState.HELLO, "Unexpected change in protocol", new Object[0]);
        Validate.validState(p_147232_2_ == EnumConnectionState.PLAY || p_147232_2_ == EnumConnectionState.LOGIN, "Unexpected protocol " + p_147232_2_, new Object[0]);
    }

    public void func_147316_a(C00PacketLoginStart p_147316_1_)
    {
        Validate.validState(this.field_147328_g == NetHandlerLoginServer.LoginState.HELLO, "Unexpected hello packet", new Object[0]);
        this.field_147337_i = p_147316_1_.func_149304_c();

        if (this.field_147327_f.isServerInOnlineMode() && !this.field_147333_a.func_150731_c())
        {
            this.field_147328_g = NetHandlerLoginServer.LoginState.KEY;
            this.field_147333_a.func_150725_a(new S01PacketEncryptionRequest(this.field_147334_j, this.field_147327_f.getKeyPair().getPublic(), this.field_147330_e), new GenericFutureListener[0]);
        }
        else
        {
            this.field_147328_g = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
        }
    }

    public void func_147315_a(C01PacketEncryptionResponse p_147315_1_)
    {
        Validate.validState(this.field_147328_g == NetHandlerLoginServer.LoginState.KEY, "Unexpected key packet", new Object[0]);
        PrivateKey privatekey = this.field_147327_f.getKeyPair().getPrivate();

        if (!Arrays.equals(this.field_147330_e, p_147315_1_.func_149299_b(privatekey)))
        {
            throw new IllegalStateException("Invalid nonce!");
        }
        else
        {
            this.field_147335_k = p_147315_1_.func_149300_a(privatekey);
            this.field_147328_g = NetHandlerLoginServer.LoginState.AUTHENTICATING;
            this.field_147333_a.func_150727_a(this.field_147335_k);
            (new Thread("User Authenticator #" + field_147331_b.incrementAndGet())
            {
                private static final String __OBFID = "CL_00001459";
                public void run()
                {
                    try
                    {
                        String s = (new BigInteger(CryptManager.getServerIdHash(NetHandlerLoginServer.this.field_147334_j, NetHandlerLoginServer.this.field_147327_f.getKeyPair().getPublic(), NetHandlerLoginServer.this.field_147335_k))).toString(16);
                        NetHandlerLoginServer.this.field_147337_i = NetHandlerLoginServer.this.field_147327_f.func_147130_as().hasJoinedServer(new GameProfile((String)null, NetHandlerLoginServer.this.field_147337_i.getName()), s);

                        if (NetHandlerLoginServer.this.field_147337_i != null)
                        {
                            NetHandlerLoginServer.field_147332_c.info("UUID of player " + NetHandlerLoginServer.this.field_147337_i.getName() + " is " + NetHandlerLoginServer.this.field_147337_i.getId());
                            NetHandlerLoginServer.this.field_147328_g = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
                        }
                        else
                        {
                            NetHandlerLoginServer.this.func_147322_a("Failed to verify username!");
                            NetHandlerLoginServer.field_147332_c.error("Username \'" + NetHandlerLoginServer.this.field_147337_i.getName() + "\' tried to join with an invalid session");
                        }
                    }
                    catch (AuthenticationUnavailableException authenticationunavailableexception)
                    {
                        NetHandlerLoginServer.this.func_147322_a("Authentication servers are down. Please try again later, sorry!");
                        NetHandlerLoginServer.field_147332_c.error("Couldn\'t verify username because servers are unavailable");
                    }
                }
            }).start();
        }
    }

    static enum LoginState
    {
        HELLO,
        KEY,
        AUTHENTICATING,
        READY_TO_ACCEPT,
        ACCEPTED;

        private static final String __OBFID = "CL_00001463";
    }
}