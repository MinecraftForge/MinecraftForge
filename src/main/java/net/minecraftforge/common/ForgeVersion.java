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

package net.minecraftforge.common;

import net.minecraftforge.fml.VersionChecker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class ForgeVersion
{
    // This is Forge's Mod Id, used for the ForgeMod and resource locations
    public static final String MOD_ID = "forge";
    //This number is incremented every time we remove deprecated code/major API changes, never reset
    public static final int majorVersion    = 14;
    //This number is incremented every minecraft release, never reset
    public static final int minorVersion    = 23;
    //This number is incremented every time a interface changes or new major feature is added, and reset every Minecraft version
    public static final int revisionVersion = 2;
    //This number is incremented every time Jenkins builds Forge, and never reset. Should always be 0 in the repo code.
    public static final int buildVersion    = 0;
    // This is the minecraft version we're building for - used in various places in Forge/FML code
    public static final String mcVersion = "1.13";
    // This is the MCP data version we're using
    public static final String mcpVersion = "9.42";

    private static final Logger log = LogManager.getLogger();

    public static int getMajorVersion()
    {
        return majorVersion;
    }

    public static int getMinorVersion()
    {
        return minorVersion;
    }

    public static int getRevisionVersion()
    {
        return revisionVersion;
    }

    public static int getBuildVersion()
    {
        return buildVersion;
    }

    public static String getVersion()
    {
        return String.format("%d.%d.%d.%d", majorVersion, minorVersion, revisionVersion, buildVersion);
    }

    public static VersionChecker.Status getStatus()
    {
        return VersionChecker.Status.PENDING;
    }

    @Nullable
    public static String getTarget()
    {
        return "";
    }
}

