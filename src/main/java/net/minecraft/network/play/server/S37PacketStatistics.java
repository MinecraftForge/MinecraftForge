package net.minecraft.network.play.server;

import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;

public class S37PacketStatistics extends Packet
{
    private Map field_148976_a;
    private static final String __OBFID = "CL_00001283";

    public S37PacketStatistics() {}

    public S37PacketStatistics(Map p_i45173_1_)
    {
        this.field_148976_a = p_i45173_1_;
    }

    public void func_148833_a(INetHandlerPlayClient p_148975_1_)
    {
        p_148975_1_.func_147293_a(this);
    }

    public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
    {
        int i = p_148837_1_.func_150792_a();
        this.field_148976_a = Maps.newHashMap();

        for (int j = 0; j < i; ++j)
        {
            StatBase statbase = StatList.func_151177_a(p_148837_1_.func_150789_c(32767));
            int k = p_148837_1_.func_150792_a();

            if (statbase != null)
            {
                this.field_148976_a.put(statbase, Integer.valueOf(k));
            }
        }
    }

    public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.func_150787_b(this.field_148976_a.size());
        Iterator iterator = this.field_148976_a.entrySet().iterator();

        while (iterator.hasNext())
        {
            Entry entry = (Entry)iterator.next();
            p_148840_1_.func_150785_a(((StatBase)entry.getKey()).statId);
            p_148840_1_.func_150787_b(((Integer)entry.getValue()).intValue());
        }
    }

    public String func_148835_b()
    {
        return String.format("count=%d", new Object[] {Integer.valueOf(this.field_148976_a.size())});
    }

    @SideOnly(Side.CLIENT)
    public Map func_148974_c()
    {
        return this.field_148976_a;
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayClient)p_148833_1_);
    }
}