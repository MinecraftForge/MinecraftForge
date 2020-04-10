/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.common.launcher;

import java.net.Proxy;
import java.util.Map;

import org.apache.logging.log4j.LogManager;

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
            LogManager.getLogger("FML.TWEAK").error("-- Login failed!", e);
            throw new RuntimeException(e); // don't set other variables
        }

        args.put("--username",       auth.getSelectedProfile().getName());
        args.put("--uuid",           auth.getSelectedProfile().getId().toString().replace("-", ""));
        args.put("--accessToken",    auth.getAuthenticatedToken());
        args.put("--userProperties", auth.getUserProperties().toString());
    }
}
