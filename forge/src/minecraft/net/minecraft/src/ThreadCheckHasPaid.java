package net.minecraft.src;

import java.net.HttpURLConnection;
import java.net.URL;
import net.minecraft.client.Minecraft;

public class ThreadCheckHasPaid extends Thread
{
    final Minecraft mc;

    public ThreadCheckHasPaid(Minecraft par1Minecraft)
    {
        this.mc = par1Minecraft;
    }

    public void run()
    {
        try
        {
            HttpURLConnection var1 = (HttpURLConnection)(new URL("https://login.minecraft.net/session?name=" + this.mc.session.username + "&session=" + this.mc.session.sessionId)).openConnection();
            var1.connect();

            if (var1.getResponseCode() == 400 && this == null)
            {
                Minecraft.hasPaidCheckTime = System.currentTimeMillis();
            }

            var1.disconnect();
        }
        catch (Exception var2)
        {
            var2.printStackTrace();
        }
    }
}
