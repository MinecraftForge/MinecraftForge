/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package net.minecraftforge.fml.relauncher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.common.ForgeVersion;

public class FMLInjectionData
{
    static File minecraftHome;
    static String major;
    static String minor;
    static String rev;
    static String build;
    static String mccversion;
    static String mcpversion;

    public static List<String> containers = new ArrayList<String>();

    static void build(File mcHome, LaunchClassLoader classLoader)
    {
        minecraftHome = mcHome;
        major = String.valueOf(ForgeVersion.majorVersion);
        minor = String.valueOf(ForgeVersion.minorVersion);
        rev = String.valueOf(ForgeVersion.revisionVersion);
        build = String.valueOf(ForgeVersion.buildVersion);
        mccversion = ForgeVersion.mcVersion;
        mcpversion = ForgeVersion.mcpVersion;
    }

    static String debfuscationDataName()
    {
        return "/deobfuscation_data-"+mccversion+".lzma";
    }
    public static Object[] data()
    {
        return new Object[] { major, minor, rev, build, mccversion, mcpversion, minecraftHome, containers };
    }
}
