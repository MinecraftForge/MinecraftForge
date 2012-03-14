package net.minecraft.src;

import java.util.HashMap;
import java.util.Map;

public class PacketCount
{
    /** If false, countPacket does nothing */
    public static boolean allowCounting = true;

    /** A count of the total number of each packet sent grouped by IDs. */
    private static final Map packetCountForID = new HashMap();

    /** A count of the total size of each packet sent grouped by IDs. */
    private static final Map sizeCountForID = new HashMap();

    /** Used to make threads queue to add packets */
    private static final Object lock = new Object();

    public static void countPacket(int par0, long par1)
    {
        if (allowCounting)
        {
            Object var3 = lock;

            synchronized (lock)
            {
                if (packetCountForID.containsKey(Integer.valueOf(par0)))
                {
                    packetCountForID.put(Integer.valueOf(par0), Long.valueOf(((Long)packetCountForID.get(Integer.valueOf(par0))).longValue() + 1L));
                    sizeCountForID.put(Integer.valueOf(par0), Long.valueOf(((Long)sizeCountForID.get(Integer.valueOf(par0))).longValue() + par1));
                }
                else
                {
                    packetCountForID.put(Integer.valueOf(par0), Long.valueOf(1L));
                    sizeCountForID.put(Integer.valueOf(par0), Long.valueOf(par1));
                }
            }
        }
    }
}
