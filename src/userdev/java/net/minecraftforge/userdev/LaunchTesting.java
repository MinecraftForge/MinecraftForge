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

package net.minecraftforge.userdev;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.Agent;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import cpw.mods.modlauncher.Launcher;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Proxy;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;

public class LaunchTesting
{
    public static void main(String... args) throws InterruptedException
    {
        final String markerselection = System.getProperty("forge.logging.markers", "");
        Arrays.stream(markerselection.split(",")).forEach(marker-> System.setProperty("forge.logging.marker."+ marker.toLowerCase(Locale.ROOT), "ACCEPT"));

        ArgumentList lst = ArgumentList.from(args);

        String target = lst.getOrDefault("launchTarget", System.getenv().get("target"));

        if (target == null) {
            throw new IllegalArgumentException("Environment variable target must be set.");
        }

        lst.putLazy("gameDir", ".");
        lst.putLazy("launchTarget", target);
        lst.putLazy("fml.mcpVersion", System.getenv("MCP_VERSION"));
        lst.putLazy("fml.mcVersion", System.getenv("MC_VERSION"));
        lst.putLazy("fml.forgeGroup", System.getenv("FORGE_GROUP"));
        lst.putLazy("fml.forgeVersion", System.getenv("FORGE_VERSION"));

        if (target.contains("client")) {
            hackNatives();
            lst.putLazy("version", "MOD_DEV");
            lst.putLazy("assetIndex", System.getenv("assetIndex"));
            lst.putLazy("assetsDir", System.getenv().getOrDefault("assetDirectory", "assets"));

            String assets = lst.get("assetsDir");
            if (assets == null || !new File(assets).exists()) {
                throw new IllegalArgumentException("Environment variable 'assetDirectory' must be set to a valid path.");
            }

            if (!lst.hasValue("accessToken")) {
                if (!login(lst)) {
                    String username = lst.get("username");
                    if (username != null) { // Replace '#' placeholders with random numbers
                        Matcher m = Pattern.compile("#+").matcher(username);
                        StringBuffer replaced = new StringBuffer();
                        while (m.find()) {
                            m.appendReplacement(replaced, getRandomNumbers(m.group().length()));
                        }
                        m.appendTail(replaced);
                        lst.put("username", replaced.toString());
                    } else {
                        lst.putLazy("username", "Dev");
                    }
                    lst.put("accessToken", "DONT_CRASH");
                    lst.put("userProperties", "{}");
                }
            }
        }

        if (Arrays.asList(
                "fmldevclient", "fmldevserver", "fmldevdata",
                "fmluserdevclient", "fmluserdevserver", "fmluserdevdata"
            ).contains(target)) {
            //nop
        } else {
            throw new IllegalArgumentException("Unknown value for 'target' property: " + target);
        }

        Launcher.main(lst.getArguments());
        Thread.sleep(10000);// Why do we have this? -Lex 03/06/19 // because there's daemon threads that should cleanly exit -cpw 04/10/20
    }

    private static String getRandomNumbers(int length)
    {   // Generate a time-based random number, to mimic how n.m.client.Main works
        return Long.toString(System.nanoTime() % (int) Math.pow(10, length));
    }

    private static void hackNatives()
    {
        String paths = System.getProperty("java.library.path");
        String nativesDir = System.getenv().get("nativesDirectory");

        if (Strings.isNullOrEmpty(paths))
            paths = nativesDir;
        else
            paths += File.pathSeparator + nativesDir;

        System.setProperty("java.library.path", paths);

        // hack the classloader now.
        try
        {
            final Method initializePathMethod = ClassLoader.class.getDeclaredMethod("initializePath", String.class);
            initializePathMethod.setAccessible(true);
            final Object usrPathsValue = initializePathMethod.invoke(null, "java.library.path");
            final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
            usrPathsField.setAccessible(true);
            usrPathsField.set(null, usrPathsValue);
        }
        catch(Throwable t) {}
    }

    /**
     * Basic implementation of Mojang's 'Yggdrasil' login system, purely intended as a dev time bare bones login.
     * Login errors are not handled.
     * Do not use this unless you know what you are doing and must use it to debug things REQUIRING authentication.
     * Forge is not responsible for any auth information passed in, saved to logs, run configs, etc...
     * BE CAREFUL WITH YOUR LOGIN INFO
     */
    private static boolean login(ArgumentList args) {
        if (!args.hasValue("username") || !args.hasValue("password")) {
            args.remove("password"); //Just in case, so it shouldn't show up anywhere.
            return false;
        }

        UserAuthentication auth = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "1").createUserAuthentication(Agent.MINECRAFT);
        auth.setUsername(args.get("username"));
        auth.setPassword(args.remove("password"));

        try {
            auth.logIn();
        } catch (AuthenticationException e) {
            LogManager.getLogger().error("Login failed!", e);
            throw new RuntimeException(e); // don't set other variables
        }

        Gson gson = (new GsonBuilder()).registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer()).create();
        args.put("username",       auth.getSelectedProfile().getName());
        args.put("uuid",           auth.getSelectedProfile().getId().toString().replace("-", ""));
        args.put("accessToken",    auth.getAuthenticatedToken());
        args.put("userProperties", gson.toJson(auth.getUserProperties()));
        return true;
    }
}
