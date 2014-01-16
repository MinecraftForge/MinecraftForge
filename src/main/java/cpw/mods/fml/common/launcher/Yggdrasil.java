package cpw.mods.fml.common.launcher;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Basic implementation of Mojang's 'Yggdrasil' login system, purely intended as a dev time bare bones login.
 * Login errors are not handled.
 */
@SuppressWarnings("unused")
public class Yggdrasil {
    private static class Request {
        Agent agent = new Agent();
        String username;
        String password;
        String clientToken = null;
        boolean requestUser = true;

        public Request(String username, String password){
          this.username = username;
          this.password = password;
        }

        private static class Agent {
            String name = "Minecraft";
            int version = 1;
        }
    }

    private static class Response {
        String error;
        String errorMessage;
        String cause;
        String accessToken;
        String clientToken;
        Profile selectedProfile;
        Profile[] availableProfiles;
        User user;

        private static class Profile {
            String id;
            String name;
            boolean legacy;
        }

        private static class User {
            String id;
            List<Property> properties;

            private static class Property {
                String name;
                String value;
            }
        }
    }

    private static void close(Closeable c)
    {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e){}
        }
    }

    public static void login(Map<String, String> args)
    {
        if (!args.containsKey("--username") || !args.containsKey("--password")) return;

        String username = args.get("--username");
        String password = args.remove("--password");

        Gson GSON = new GsonBuilder().setPrettyPrinting().create();
        String request = GSON.toJson(new Request(username, password));

        OutputStream out = null;
        InputStream in = null;

        try
        {
            HttpURLConnection con = (HttpURLConnection)(new URL("https://authserver.mojang.com/authenticate")).openConnection();
            con.setConnectTimeout(15000);
            con.setReadTimeout(15000);
            con.setUseCaches(false);

            byte[] data = request.getBytes(Charsets.UTF_8);

            con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            con.setRequestProperty("Content-Length", Integer.toString(data.length));
            con.setDoOutput(true);

            out = con.getOutputStream();
            out.write(data);

            in = con.getInputStream();
            Response result = GSON.fromJson(IOUtils.toString(in, Charsets.UTF_8), Response.class);

            if (result.selectedProfile != null)
            {
                args.put("--username", result.selectedProfile.name);
                args.put("--uuid",     result.selectedProfile.id);
                args.put("--accessToken", result.accessToken);
            }
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            close(out);
            close(in);
        }
    }
}
