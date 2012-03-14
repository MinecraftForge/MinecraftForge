package net.minecraft.src;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

class PlayerUsageSnooperThread extends Thread
{
    final PlayerUsageSnooper field_48553_a;

    PlayerUsageSnooperThread(PlayerUsageSnooper par1PlayerUsageSnooper, String par2Str)
    {
        super(par2Str);
        this.field_48553_a = par1PlayerUsageSnooper;
    }

    public void run()
    {
        try
        {
            String var1 = "fName=" + URLEncoder.encode("???", "UTF-8") + "&lName=" + URLEncoder.encode("???", "UTF-8");
            URL var2 = new URL("http://snoop.minecraft.net/");
            HttpURLConnection var3 = (HttpURLConnection)var2.openConnection();
            var3.setRequestMethod("POST");
            var3.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            var3.setRequestProperty("Content-Length", "" + Integer.toString(var1.getBytes().length));
            var3.setRequestProperty("Content-Language", "en-US");
            var3.setUseCaches(false);
            var3.setDoInput(true);
            var3.setDoOutput(true);
            DataOutputStream var4 = new DataOutputStream(var3.getOutputStream());
            var4.writeBytes(var1);
            var4.flush();
            var4.close();
            InputStream var5 = var3.getInputStream();
            BufferedReader var6 = new BufferedReader(new InputStreamReader(var5));
            StringBuffer var8 = new StringBuffer();
            String var7;

            while ((var7 = var6.readLine()) != null)
            {
                var8.append(var7);
                var8.append('\r');
            }

            var6.close();
        }
        catch (Exception var9)
        {
            ;
        }
    }
}
