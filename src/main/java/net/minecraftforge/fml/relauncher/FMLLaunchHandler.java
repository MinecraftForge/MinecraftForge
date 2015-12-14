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

import org.apache.logging.log4j.Level;

import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.common.launcher.FMLTweaker;

import com.google.common.base.Throwables;

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
        this.classLoader.addClassLoaderExclusion("org.apache.");
        this.classLoader.addClassLoaderExclusion("com.google.common.");
        this.classLoader.addClassLoaderExclusion("org.objectweb.asm.");
        this.classLoader.addClassLoaderExclusion("LZMA.");
    }

    private void setupClient()
    {
        FMLRelaunchLog.side = Side.CLIENT;
        side = Side.CLIENT;
        setupHome();
    }

    private void setupServer()
    {
        FMLRelaunchLog.side = Side.SERVER;
        side = Side.SERVER;
        setupHome();

    }

    private void setupHome()
    {
        FMLInjectionData.build(minecraftHome, classLoader);
        FMLRelaunchLog.minecraftHome = minecraftHome;
        FMLRelaunchLog.info("Forge Mod Loader version %s.%s.%s.%s for Minecraft %s loading", FMLInjectionData.major, FMLInjectionData.minor,
                FMLInjectionData.rev, FMLInjectionData.build, FMLInjectionData.mccversion, FMLInjectionData.mcpversion);
        FMLRelaunchLog.info("Java is %s, version %s, running on %s:%s:%s, installed at %s", System.getProperty("java.vm.name"), System.getProperty("java.version"), System.getProperty("os.name"), System.getProperty("os.arch"), System.getProperty("os.version"), System.getProperty("java.home"));
        FMLRelaunchLog.fine("Java classpath at launch is %s", System.getProperty("java.class.path"));
        FMLRelaunchLog.fine("Java library path at launch is %s", System.getProperty("java.library.path"));

        try
        {
            CoreModManager.handleLaunch(minecraftHome, classLoader, tweaker);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            FMLRelaunchLog.log(Level.ERROR, t, "An error occurred trying to configure the minecraft home at %s for Forge Mod Loader", minecraftHome.getAbsolutePath());
            throw Throwables.propagate(t);
        }
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
}
