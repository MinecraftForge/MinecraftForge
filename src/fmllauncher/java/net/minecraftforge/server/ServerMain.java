/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.google.common.collect.ObjectArrays;
import cpw.mods.modlauncher.Launcher;

public class ServerMain {
    public static void main(String[] args) throws IOException {
        final Properties props = new Properties();
        try(InputStream stream = ServerMain.class.getClassLoader().getResourceAsStream("server_launcher.properties")) {
            if (stream == null) {
                System.out.println("ERROR: could not load server_launcher.properties, invalid launcher jar, you must specify all arguments");
                return;
            }
            props.load(stream);
        }

        boolean exit = false;
        int lib_count = Integer.parseInt(props.getProperty("library.count"));
        for (int x = 0; x < lib_count; x++) {
            String lib = props.getProperty(String.format("library.%03d", x));
            if (lib == null) {
                System.out.println("Invalid server launcher properties, missing library: " + x);
                exit = true;
            } else if (!new File(lib).exists()) {
                System.out.println("Missing library: " + lib);
                exit = true;
            }
        }

        if (exit)
            return;

        String defaults = props.getProperty("args");
        if (defaults == null) {
            System.out.println("Null default argumetns found, you must specify all arguments.");
            return;
        }

        System.out.println("Appending default arguments: " + defaults);
        final String[] argArray = ObjectArrays.concat(defaults.split(" "), args, String.class);
        Launcher.main(argArray);
    }
}
