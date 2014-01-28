package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.mco.McoServer;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
public class GuiScreenRealmsPinger
{
    private static final Logger field_148510_a = LogManager.getLogger();
    private final List field_148509_b = Collections.synchronizedList(new ArrayList());
    private static final String __OBFID = "CL_00000807";

                public void func_147233_a() {}
    public void func_148506_a(final McoServer p_148506_1_) throws UnknownHostException
    {
        if (p_148506_1_.field_148807_g != null)
        {
            ServerAddress serveraddress = ServerAddress.func_78860_a(p_148506_1_.field_148807_g);
            final NetworkManager networkmanager = NetworkManager.func_150726_a(InetAddress.getByName(serveraddress.getIP()), serveraddress.getPort());
            this.field_148509_b.add(networkmanager);
            networkmanager.func_150719_a(new INetHandlerStatusClient()
            {
                private boolean field_147399_d = false;
                private static final String __OBFID = "CL_00000808";
                public void func_147397_a(S00PacketServerInfo p_147397_1_)
                {
                    ServerStatusResponse serverstatusresponse = p_147397_1_.func_149294_c();

                    if (serverstatusresponse.func_151318_b() != null)
                    {
                        p_148506_1_.field_148813_n = EnumChatFormatting.GRAY + "" + serverstatusresponse.func_151318_b().func_151333_b();
                    }

                    networkmanager.func_150725_a(new C01PacketPing(Minecraft.getSystemTime()), new GenericFutureListener[0]);
                    this.field_147399_d = true;
                }
                public void func_147398_a(S01PacketPong p_147398_1_)
                {
                    networkmanager.func_150718_a(new ChatComponentText("Finished"));
                }
                public void func_147231_a(IChatComponent p_147231_1_)
                {
                    if (!this.field_147399_d)
                    {
                        GuiScreenRealmsPinger.field_148510_a.error("Can\'t ping " + p_148506_1_.field_148807_g + ": " + p_147231_1_.func_150260_c());
                    }
                }
                public void func_147232_a(EnumConnectionState p_147232_1_, EnumConnectionState p_147232_2_)
                {
                    if (p_147232_2_ != EnumConnectionState.STATUS)
                    {
                        throw new UnsupportedOperationException("Unexpected change in protocol to " + p_147232_2_);
                    }
                }
                public void func_147233_a() {}
            });

            try
            {
                networkmanager.func_150725_a(new C00Handshake(4, serveraddress.getIP(), serveraddress.getPort(), EnumConnectionState.STATUS), new GenericFutureListener[0]);
                networkmanager.func_150725_a(new C00PacketServerQuery(), new GenericFutureListener[0]);
            }
            catch (Throwable throwable)
            {
                field_148510_a.error(throwable);
            }
        }
    }

    public void func_148507_b()
    {
        List list = this.field_148509_b;

        synchronized (this.field_148509_b)
        {
            Iterator iterator = this.field_148509_b.iterator();

            while (iterator.hasNext())
            {
                NetworkManager networkmanager = (NetworkManager)iterator.next();

                if (networkmanager.func_150724_d())
                {
                    iterator.remove();
                    networkmanager.func_150718_a(new ChatComponentText("Cancelled"));
                }
            }
        }
    }
}