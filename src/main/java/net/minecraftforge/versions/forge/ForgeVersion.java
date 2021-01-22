/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.versions.forge;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.loading.JarVersionLookupHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

import static net.minecraftforge.fml.Logging.CORE;

public class ForgeVersion
{
    private static final Logger LOGGER = LogManager.getLogger();
    // This is Forge's Mod Id, used for the ForgeMod and resource locations
    public static final String MOD_ID = "forge";

    private static final String forgeVersion;
    private static final String forgeSpec;
    private static final String forgeGroup;

    static {
        LOGGER.debug(CORE, "Forge Version package {} from {}", ForgeVersion.class.getPackage(), ForgeVersion.class.getClassLoader());
        String vers = JarVersionLookupHandler.getImplementationVersion(ForgeVersion.class).orElse(System.getenv("FORGE_VERSION"));
        if (vers == null) throw new RuntimeException("Missing forge version, cannot continue");
        String spec = JarVersionLookupHandler.getSpecificationVersion(ForgeVersion.class).orElse(System.getenv("FORGE_SPEC"));
        if (spec == null) throw new RuntimeException("Missing forge spec, cannot continue");
        String group = JarVersionLookupHandler.getImplementationTitle(ForgeVersion.class).orElse(System.getenv("FORGE_GROUP"));
        if (group == null) {
            group = "net.minecraftforge"; // If all else fails, Our normal group
        }
        forgeVersion = vers;
        forgeSpec = spec;
        forgeGroup = group;
        LOGGER.debug(CORE, "Found Forge version {}", forgeVersion);
        LOGGER.debug(CORE, "Found Forge spec {}", forgeSpec);
        LOGGER.debug(CORE, "Found Forge group {}", forgeGroup);
    }

    public static String getVersion()
    {
        return forgeVersion;
    }

    public static VersionChecker.Status getStatus()
    {
        return VersionChecker.getResult(ModList.get().getModFileById(MOD_ID).getMods().get(0)).status;
    }

    @Nullable
    public static String getTarget()
    {
        VersionChecker.CheckResult res = VersionChecker.getResult(ModList.get().getModFileById(MOD_ID).getMods().get(0));
        return res.target == null ? "" : res.target.toString();
    }

    public static String getSpec() {
        return forgeSpec;
    }

    public static String getGroup() {
        return forgeGroup;
    }
}

