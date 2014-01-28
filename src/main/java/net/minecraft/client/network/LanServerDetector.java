package net.minecraft.client.network;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ThreadLanServerPing;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
public class LanServerDetector
{
    private static final AtomicInteger field_148551_a = new AtomicInteger(0);
    private static final Logger field_148550_b = LogManager.getLogger();
    private static final String __OBFID = "CL_00001133";

    @SideOnly(Side.CLIENT)
    public static class ThreadLanServerFind extends Thread
        {
            // JAVADOC FIELD $$ field_77500_a
            private final LanServerDetector.LanServerList localServerList;
            // JAVADOC FIELD $$ field_77498_b
            private final InetAddress broadcastAddress;
            // JAVADOC FIELD $$ field_77499_c
            private final MulticastSocket socket;
            private static final String __OBFID = "CL_00001135";

            public ThreadLanServerFind(LanServerDetector.LanServerList par1LanServerList) throws IOException
            {
                super("LanServerDetector #" + LanServerDetector.field_148551_a.incrementAndGet());
                this.localServerList = par1LanServerList;
                this.setDaemon(true);
                this.socket = new MulticastSocket(4445);
                this.broadcastAddress = InetAddress.getByName("224.0.2.60");
                this.socket.setSoTimeout(5000);
                this.socket.joinGroup(this.broadcastAddress);
            }

            public void run()
            {
                byte[] abyte = new byte[1024];

                while (!this.isInterrupted())
                {
                    DatagramPacket datagrampacket = new DatagramPacket(abyte, abyte.length);

                    try
                    {
                        this.socket.receive(datagrampacket);
                    }
                    catch (SocketTimeoutException sockettimeoutexception)
                    {
                        continue;
                    }
                    catch (IOException ioexception1)
                    {
                        LanServerDetector.field_148550_b.error("Couldn\'t ping server", ioexception1);
                        break;
                    }

                    String s = new String(datagrampacket.getData(), datagrampacket.getOffset(), datagrampacket.getLength());
                    LanServerDetector.field_148550_b.debug(datagrampacket.getAddress() + ": " + s);
                    this.localServerList.func_77551_a(s, datagrampacket.getAddress());
                }

                try
                {
                    this.socket.leaveGroup(this.broadcastAddress);
                }
                catch (IOException ioexception)
                {
                    ;
                }

                this.socket.close();
            }
        }

    @SideOnly(Side.CLIENT)
    public static class LanServer
        {
            private String lanServerMotd;
            private String lanServerIpPort;
            // JAVADOC FIELD $$ field_77491_c
            private long timeLastSeen;
            private static final String __OBFID = "CL_00001134";

            public LanServer(String par1Str, String par2Str)
            {
                this.lanServerMotd = par1Str;
                this.lanServerIpPort = par2Str;
                this.timeLastSeen = Minecraft.getSystemTime();
            }

            public String getServerMotd()
            {
                return this.lanServerMotd;
            }

            public String getServerIpPort()
            {
                return this.lanServerIpPort;
            }

            // JAVADOC METHOD $$ func_77489_c
            public void updateLastSeen()
            {
                this.timeLastSeen = Minecraft.getSystemTime();
            }
        }

    @SideOnly(Side.CLIENT)
    public static class LanServerList
        {
            private ArrayList listOfLanServers = new ArrayList();
            boolean wasUpdated;
            private static final String __OBFID = "CL_00001136";

            public synchronized boolean getWasUpdated()
            {
                return this.wasUpdated;
            }

            public synchronized void setWasNotUpdated()
            {
                this.wasUpdated = false;
            }

            public synchronized List getLanServers()
            {
                return Collections.unmodifiableList(this.listOfLanServers);
            }

            public synchronized void func_77551_a(String par1Str, InetAddress par2InetAddress)
            {
                String s1 = ThreadLanServerPing.getMotdFromPingResponse(par1Str);
                String s2 = ThreadLanServerPing.getAdFromPingResponse(par1Str);

                if (s2 != null)
                {
                    s2 = par2InetAddress.getHostAddress() + ":" + s2;
                    boolean flag = false;
                    Iterator iterator = this.listOfLanServers.iterator();

                    while (iterator.hasNext())
                    {
                        LanServerDetector.LanServer lanserver = (LanServerDetector.LanServer)iterator.next();

                        if (lanserver.getServerIpPort().equals(s2))
                        {
                            lanserver.updateLastSeen();
                            flag = true;
                            break;
                        }
                    }

                    if (!flag)
                    {
                        this.listOfLanServers.add(new LanServerDetector.LanServer(s1, s2));
                        this.wasUpdated = true;
                    }
                }
            }
        }
}