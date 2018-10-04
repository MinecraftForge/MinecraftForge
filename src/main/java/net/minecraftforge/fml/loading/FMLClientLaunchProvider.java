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

package net.minecraftforge.fml.loading;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ILaunchHandlerService;
import cpw.mods.modlauncher.api.ITransformingClassLoader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.versions.forge.ForgeVersion;
import net.minecraftforge.versions.mcp.MCPVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

public class FMLClientLaunchProvider extends FMLCommonLaunchHandler implements ILaunchHandlerService
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Path forgePath;
    private static final Path patchedBinariesPath;
    private static final Path srgMcPath;

    static {
        Path forgePath1 = null;
        Path patchedBinariesPath1 = null;
        Path srgMcPath1 = null;
        try {
            forgePath1 = Paths.get(FMLClientLaunchProvider.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            patchedBinariesPath1 = forgePath1.resolveSibling("forge-"+MCPVersion.getMCVersion()+"-"+ForgeVersion.getVersion()+"-client.jar");
            Path libs = forgePath1.getParent().getParent().getParent().getParent().getParent();
            srgMcPath1 = libs.resolve(Paths.get("net","minecraft", "client", MCPVersion.getMCPandMCVersion(), "client-"+MCPVersion.getMCPandMCVersion()+"-srg.jar")).toAbsolutePath();
        } catch (URISyntaxException e) {

        }
        forgePath = forgePath1;
        patchedBinariesPath = patchedBinariesPath1;
        srgMcPath = srgMcPath1;
    }
    @Override
    public String name()
    {
        return "fmlclient";
    }

    @Override
    public Path[] identifyTransformationTargets()
    {
        LOGGER.info("SRG MC at {} is {}", srgMcPath.toString(), Files.exists(srgMcPath) ? "present" : "missing");
        LOGGER.info("Forge patches at {} is {}", patchedBinariesPath.toString(), Files.exists(patchedBinariesPath) ? "present" : "missing");
        LOGGER.info("Forge at {} is {}", forgePath.toString(), Files.exists(forgePath) ? "present" : "missing");
        if (!(Files.exists(srgMcPath) && Files.exists(patchedBinariesPath) && Files.exists(forgePath))) {
            throw new RuntimeException("Failed to find patched jars");
        }
        return new Path[] {forgePath, patchedBinariesPath, srgMcPath};
    }

    @Override
    public Callable<Void> launchService(String[] arguments, ITransformingClassLoader launchClassLoader)
    {
        return () -> {
            super.beforeStart(launchClassLoader, forgePath);
            launchClassLoader.addTargetPackageFilter(getPackagePredicate());
            Class.forName("net.minecraft.client.main.Main", true, launchClassLoader.getInstance()).getMethod("main", String[].class).invoke(null, (Object)arguments);
            return null;
        };
    }

    @Override
    public void setup(final IEnvironment environment) {
    }

    @Override
    public Dist getDist()
    {
        return Dist.CLIENT;
    }
}
