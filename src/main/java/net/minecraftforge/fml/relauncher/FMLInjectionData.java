/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

    public static final List<String> containers = new ArrayList<String>();

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
