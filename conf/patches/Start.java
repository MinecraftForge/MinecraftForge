import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import net.minecraft.client.Minecraft;

public class Start
{
    public static void main(String[] args)
    {
        try
        {
            Field f = Minecraft.class.getDeclaredField("field_71463_am");
            Field.setAccessible(new Field[] { f }, true);
            f.set(null, new File("."));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }
        if (args.length != 2)
        {
            Minecraft.main(args);
        }
        else
        {
            try {
                String parameters = "http://login.minecraft.net/?user=" + URLEncoder.encode(args[0], "UTF-8") + 
                        "&password=" + URLEncoder.encode(args[1], "UTF-8") + 
                        "&version=" + 13;
                String result = openUrl(parameters);
                
                if (result == null) 
                {
                  System.out.println("Can't connect to minecraft.net");
                  return;
                }
                
                if (!result.contains(":")) 
                {
                  System.out.println("Login Failed: " + result);
                  return;
                }
                //latestVersion, downloadTicket, userName, sessionId    
                String[] values = result.split(":");    
                Minecraft.main(new String[]{values[2].trim(), values[3].trim()});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private static String openUrl(String addr)
    {
        try {
            URL url = new URL(addr);
            java.io.InputStream is;
            is = url.openConnection().getInputStream();
    
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(is));
            String buf = "";
            String line = null;
            while((line = reader.readLine() ) != null)
            {
               buf += "\n" + line;
            }
            reader.close();
            return buf;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}