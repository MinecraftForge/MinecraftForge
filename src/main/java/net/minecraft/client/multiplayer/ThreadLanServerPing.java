package net.minecraft.client.multiplayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
public class ThreadLanServerPing extends Thread
{
    private static final AtomicInteger field_148658_a = new AtomicInteger(0);
    private static final Logger field_148657_b = LogManager.getLogger();
    private final String motd;
    // JAVADOC FIELD $$ field_77529_c
    private final DatagramSocket socket;
    private boolean isStopping = true;
    private final String address;
    private static final String __OBFID = "CL_00001137";

    public ThreadLanServerPing(String par1Str, String par2Str) throws IOException
    {
        super("LanServerPinger #" + field_148658_a.incrementAndGet());
        this.motd = par1Str;
        this.address = par2Str;
        this.setDaemon(true);
        this.socket = new DatagramSocket();
    }

    public void run()
    {
        String s = getPingResponse(this.motd, this.address);
        byte[] abyte = s.getBytes();

        while (!this.isInterrupted() && this.isStopping)
        {
            try
            {
                InetAddress inetaddress = InetAddress.getByName("224.0.2.60");
                DatagramPacket datagrampacket = new DatagramPacket(abyte, abyte.length, inetaddress, 4445);
                this.socket.send(datagrampacket);
            }
            catch (IOException ioexception)
            {
                field_148657_b.warn("LanServerPinger: " + ioexception.getMessage());
                break;
            }

            try
            {
                sleep(1500L);
            }
            catch (InterruptedException interruptedexception)
            {
                ;
            }
        }
    }

    public void interrupt()
    {
        super.interrupt();
        this.isStopping = false;
    }

    public static String getPingResponse(String par0Str, String par1Str)
    {
        return "[MOTD]" + par0Str + "[/MOTD][AD]" + par1Str + "[/AD]";
    }

    public static String getMotdFromPingResponse(String par0Str)
    {
        int i = par0Str.indexOf("[MOTD]");

        if (i < 0)
        {
            return "missing no";
        }
        else
        {
            int j = par0Str.indexOf("[/MOTD]", i + "[MOTD]".length());
            return j < i ? "missing no" : par0Str.substring(i + "[MOTD]".length(), j);
        }
    }

    public static String getAdFromPingResponse(String par0Str)
    {
        int i = par0Str.indexOf("[/MOTD]");

        if (i < 0)
        {
            return null;
        }
        else
        {
            int j = par0Str.indexOf("[/MOTD]", i + "[/MOTD]".length());

            if (j >= 0)
            {
                return null;
            }
            else
            {
                int k = par0Str.indexOf("[AD]", i + "[/MOTD]".length());

                if (k < 0)
                {
                    return null;
                }
                else
                {
                    int l = par0Str.indexOf("[/AD]", k + "[AD]".length());
                    return l < k ? null : par0Str.substring(k + "[AD]".length(), l);
                }
            }
        }
    }
}