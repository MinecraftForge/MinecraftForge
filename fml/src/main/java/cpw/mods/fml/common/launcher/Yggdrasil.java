package cpw.mods.fml.common.launcher;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.*;
import com.mojang.authlib.yggdrasil.*;

/**
 * Basic implementation of Mojang's 'Yggdrasil' login system, purely intended as a dev time bare bones login.
 * Login errors are not handled.
 */
public class Yggdrasil
{
    public static void login(Map<String, String> args)
    {
        if (!args.containsKey("--username") || !args.containsKey("--password")) return;
        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) new YggdrasilAuthenticationService(Proxy.NO_PROXY, "1").createUserAuthentication(Agent.MINECRAFT);
        auth.setUsername(args.get("--username"));
        auth.setPassword(args.remove("--password"));

        try
        {
            auth.logIn();
        }
        catch (AuthenticationException e)
        {
            LogManager.getLogger("FMLTWEAK").error("-- Login failed!  " + e.getMessage());
            Throwables.propagate(e);
            return; // dont set other variables
        }

        args.put("--username",       auth.getSelectedProfile().getName());
        args.put("--uuid",           auth.getSelectedProfile().getId().toString().replace("-", ""));
        args.put("--accessToken",    auth.getAuthenticatedToken());
        args.put("--userProperties", auth.getUserProperties().toString());
    }
}
