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

package net.minecraftforge.fml.relauncher;

import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

public class ServerLaunchWrapper {

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        new ServerLaunchWrapper().run(args);
    }

    private ServerLaunchWrapper()
    {

    }

    private void run(String[] args)
    {
        if (System.getProperty("log4j.configurationFile") == null)
        {
            // Set this early so we don't need to reconfigure later
            System.setProperty("log4j.configurationFile", "log4j2_server.xml");
        }
        Class<?> launchwrapper = null;
        try
        {
            launchwrapper = Class.forName("net.minecraft.launchwrapper.Launch",true,getClass().getClassLoader());
            Class.forName("org.objectweb.asm.Type",true,getClass().getClassLoader());
        }
        catch (Exception e)
        {
            System.err.printf("We appear to be missing one or more essential library files.\n" +
            		"You will need to add them to your server before FML and Forge will run successfully.");
            e.printStackTrace(System.err);
            System.exit(1);
        }

        try
        {
            Method main = launchwrapper.getMethod("main", String[].class);
            String[] allArgs = new String[args.length + 2];
            allArgs[0] = "--tweakClass";
            allArgs[1] = "net.minecraftforge.fml.common.launcher.FMLServerTweaker";
            System.arraycopy(args, 0, allArgs, 2, args.length);
            main.invoke(null,(Object)allArgs);
        }
        catch (Exception e)
        {
            System.err.printf("A problem occurred running the Server launcher.");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

}
