package net.minecraft.src;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PlayerUsageSnooper
{
    public static final PlayerUsageSnooper field_48478_a = new PlayerUsageSnooper();
    private long field_48476_b = -1L;
    private Map field_48477_c = new HashMap();
    private Map field_48475_d = new HashMap();

    public void func_48472_a()
    {
        long var1 = System.currentTimeMillis();

        if (this.field_48476_b == -1L)
        {
            this.field_48476_b = System.currentTimeMillis();
        }
        else
        {
            long var3 = var1 - this.field_48476_b;

            if (var3 >= -600000L && var3 <= 1200000L)
            {
                if (var3 > 600000L)
                {
                    this.func_48473_b();
                    this.field_48476_b += 600000L;
                }
            }
            else
            {
                System.out.println("Skipping snoop interval");
                this.field_48476_b = var1;
            }
        }
    }

    public void func_48474_a(String par1Str, String par2Str)
    {
        this.field_48475_d.put(par1Str, par2Str);
    }

    private void func_48473_b()
    {
        String var1 = "";
        Iterator var2 = this.field_48475_d.keySet().iterator();
        String var3;
        String var4;

        while (var2.hasNext())
        {
            var3 = (String)var2.next();
            var4 = (String)this.field_48475_d.get(var3);

            if (var1.length() > 0)
            {
                var1 = var1 + "&";
            }

            try
            {
                var1 = var1 + var3 + "=" + URLEncoder.encode(var4, "UTF-8");
            }
            catch (Exception var7)
            {
                ;
            }
        }

        var2 = this.field_48477_c.keySet().iterator();

        while (var2.hasNext())
        {
            var3 = (String)var2.next();
            var4 = ((Integer)this.field_48477_c.get(var3)).toString();

            if (var1.length() > 0)
            {
                var1 = var1 + "&";
            }

            try
            {
                var1 = var1 + var3 + "=" + URLEncoder.encode(var4, "UTF-8");
            }
            catch (Exception var6)
            {
                ;
            }
        }

        this.field_48477_c.clear();
        PlayerUsageSnooperThread var8 = new PlayerUsageSnooperThread(this, "reporter");
        var8.setDaemon(true);
        var8.start();
    }
}
