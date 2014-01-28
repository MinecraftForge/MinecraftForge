import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.main.Main;

public class Start
{
    public static void main(String[] args) throws Exception
    {
        int userIndex = -1;
        int passIndex = -1;
        int sessIndex = -1;
        int versIndex = -1;

        for( int x = 0; x < args.length; x++)
        {
            if (args[x].equals("--username"))      userIndex = x + 1;
            else if (args[x].equals("--password")) passIndex = x + 1;
            else if (args[x].equals("--session"))  sessIndex = x + 1;
            else if (args[x].equals("--version"))  versIndex = x + 1;
        }

        if (userIndex != 0-1 && passIndex != -1 && sessIndex == -1)
        {
            String[] session = getSession(args[userIndex], args[passIndex]);
            if (session != null)
            {
                args[userIndex] = session[0];
                args = concat(args, new String[]{"--session", session[1]});
            }
        }
        
        //Kill the password if its there so it isn't printed to the console.
        if (passIndex != -1)
        {
            args[passIndex-1] = "no_password_for_joo";
            args[passIndex] = "no_password_for_joo";
        }

        if (versIndex == -1)
        {
            args = concat(args, new String[]{ "--version", "fml_mcp" });
        }

        Main.main(args);
    }
    
    private static String[] getSession(String username, String password) throws UnsupportedEncodingException
    {
        String parameters = "http://login.minecraft.net/?user=" + URLEncoder.encode(username, "UTF-8") +
                "&password=" + URLEncoder.encode(password, "UTF-8") +
                "&version=" + 13;
        String result = openUrl(parameters);
        
        if (result == null)
        {
            System.out.println("Can't connect to minecraft.net");
            return null;
        }
        
        if (!result.contains(":"))
        {
            System.out.println("Login Failed: " + result);
            return null;
        }
        String[] values = result.split(":");
        return new String[]{ values[2].trim(), values[3].trim() };
    }

    private static String openUrl(String addr)
    {
        try
        {
            URL url = new URL(addr);
            java.io.InputStream is;
            is = url.openConnection().getInputStream();
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(is));
            String buf = "";
            String line = null;

            while ((line = reader.readLine()) != null)
            {
                buf += "\n" + line;
            }

            reader.close();
            return buf;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }
    
    private static <T> T[] concat(T[] first, T[] second)
    {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}