package net.minecraftforge.fml.common.launcher;

import java.net.Proxy;
import java.util.Map;

import org.apache.logging.log4j.LogManager;

import com.google.common.base.Throwables;
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

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
