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

import java.io.File;

import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.TracingPrintStream;
import net.minecraftforge.fml.common.launcher.FMLTweaker;
import net.minecraftforge.fml.relauncher.libraries.LibraryManager;

import org.apache.logging.log4j.LogManager;

public class FMLLaunchHandler
{
    private static FMLLaunchHandler INSTANCE;
    static Side side;
    private LaunchClassLoader classLoader;
    private FMLTweaker tweaker;
    private File minecraftHome;

    public static void configureForClientLaunch(LaunchClassLoader loader, FMLTweaker tweaker)
    {
        instance(loader, tweaker).setupClient();
    }

    public static void configureForServerLaunch(LaunchClassLoader loader, FMLTweaker tweaker)
    {
        instance(loader, tweaker).setupServer();
    }

    private static FMLLaunchHandler instance(LaunchClassLoader launchLoader, FMLTweaker tweaker)
    {
        if (INSTANCE == null)
        {
            INSTANCE = new FMLLaunchHandler(launchLoader, tweaker);
        }
        return INSTANCE;

    }

    private FMLLaunchHandler(LaunchClassLoader launchLoader, FMLTweaker tweaker)
    {
        this.classLoader = launchLoader;
        this.tweaker = tweaker;
        this.minecraftHome = tweaker.getGameDir();
        this.classLoader.addClassLoaderExclusion("net.minecraftforge.fml.relauncher.");
        this.classLoader.addClassLoaderExclusion("net.minecraftforge.classloading.");
        this.classLoader.addTransformerExclusion("net.minecraftforge.fml.common.asm.transformers.");
        this.classLoader.addTransformerExclusion("net.minecraftforge.fml.common.patcher.");
        this.classLoader.addTransformerExclusion("net.minecraftforge.fml.repackage.");
        this.classLoader.addClassLoaderExclusion("org.apache.commons.");
        this.classLoader.addClassLoaderExclusion("org.apache.http.");
        this.classLoader.addClassLoaderExclusion("org.apache.maven.");
        this.classLoader.addClassLoaderExclusion("com.google.common.");
        this.classLoader.addClassLoaderExclusion("org.objectweb.asm.");
        this.classLoader.addClassLoaderExclusion("LZMA.");
    }

    private void setupClient()
    {
        side = Side.CLIENT;
        setupHome();
    }

    private void setupServer()
    {
        side = Side.SERVER;
        setupHome();
    }

    private void setupHome()
    {
        FMLInjectionData.build(minecraftHome, classLoader);
        redirectStdOutputToLog();
        FMLLog.log.info("Forge Mod Loader version {}.{}.{}.{} for Minecraft {} loading", FMLInjectionData.major, FMLInjectionData.minor,
                FMLInjectionData.rev, FMLInjectionData.build, FMLInjectionData.mccversion);
        FMLLog.log.info("Java is {}, version {}, running on {}:{}:{}, installed at {}", System.getProperty("java.vm.name"), System.getProperty("java.version"), System.getProperty("os.name"), System.getProperty("os.arch"), System.getProperty("os.version"), System.getProperty("java.home"));
        FMLLog.log.debug("Java classpath at launch is:");
        for (String path : System.getProperty("java.class.path").split(File.pathSeparator))
            FMLLog.log.debug("    {}", path);
        FMLLog.log.debug("Java library path at launch is:");
        for (String path : System.getProperty("java.library.path").split(File.pathSeparator))
            FMLLog.log.debug("    {}", path);

        try
        {
            LibraryManager.setup(minecraftHome);
            CoreModManager.handleLaunch(minecraftHome, classLoader, tweaker);
        }
        catch (Throwable t)
        {
            throw new RuntimeException("An error occurred trying to configure the Minecraft home at " + minecraftHome.getAbsolutePath() + " for Forge Mod Loader", t);
        }
    }

    private void redirectStdOutputToLog()
    {
        FMLLog.log.debug("Injecting tracing printstreams for STDOUT/STDERR.");
        System.setOut(new TracingPrintStream(LogManager.getLogger("STDOUT"), System.out));
        System.setErr(new TracingPrintStream(LogManager.getLogger("STDERR"), System.err));
    }

    public static Side side()
    {
        return side;
    }


    private void injectPostfixTransformers()
    {
        CoreModManager.injectTransformers(classLoader);
    }

    public static void appendCoreMods()
    {
        INSTANCE.injectPostfixTransformers();
    }

    public static boolean isDeobfuscatedEnvironment()
    {
        return CoreModManager.deobfuscatedEnvironment;
    }
}
