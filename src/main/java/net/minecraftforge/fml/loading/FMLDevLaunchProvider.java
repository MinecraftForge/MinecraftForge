/*
 * Minecraft Forge
 * Copyright (c) 2018.
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
import net.minecraft.client.main.Main;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

import static net.minecraftforge.fml.Logging.CORE;
import static net.minecraftforge.fml.Logging.fmlLog;

public class FMLDevLaunchProvider extends FMLCommonLaunchHandler implements ILaunchHandlerService
{
    @Override
    public String name()
    {
        return "devfml";
    }

    @Override
    public Path[] identifyTransformationTargets()
    {
        try
        {
            return new Path[] {
                    Paths.get(getClass().getProtectionDomain().getCodeSource().getLocation().toURI())
            };
        }
        catch (URISyntaxException e)
        {
            throw new RuntimeException("I can't find myself!");
        }
    }

    @Override
    public Callable<Void> launchService(String[] arguments, ClassLoader launchClassLoader)
    {
        return () -> {
            Main.main(arguments);
            return null;
        };
    }

    @Override
    public void setup(IEnvironment environment)
    {
        fmlLog.debug(CORE, "No jar creation necessary. Launch is dev environment");
    }
}
