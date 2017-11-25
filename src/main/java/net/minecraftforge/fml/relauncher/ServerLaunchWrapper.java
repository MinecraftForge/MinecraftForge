/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

public class ServerLaunchWrapper {

    public static void main(String[] args)
    {
        new ServerLaunchWrapper().run(args);
    }

    private ServerLaunchWrapper()
    {

    }

    private void run(String[] args)
    {
        //Check java version, as we don't support java 9 yet
        //TODO remove this check in 1.13 when ModLauncher supports J9
        String javaVersion = System.getProperty("java.specification.version");
        if (javaVersion == null) //should never happen, but let's be safe
        {
            System.out.println("Could not determine java version, things may not work!");
        }
        else if (!javaVersion.startsWith("1.8") && !"true".equalsIgnoreCase(System.getProperty("fml.disableJavaVersionCheck")))
        {
            throw new RuntimeException(String.format("Your java version (%s) is not supported with this version of minecraft! Please use Java 8!", javaVersion));
        }

        Class<?> launchwrapper = null;
        try
        {
            launchwrapper = Class.forName("net.minecraft.launchwrapper.Launch", true, getClass().getClassLoader());
            Class.forName("org.objectweb.asm.Type",true, getClass().getClassLoader());
        }
        catch (Exception e)
        {
            System.err.println("We appear to be missing one or more essential library files.\n" +
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
            System.err.println("A problem occurred running the Server launcher.");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

}
