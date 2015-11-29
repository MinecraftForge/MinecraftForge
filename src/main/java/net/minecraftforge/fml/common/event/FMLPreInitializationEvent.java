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

package net.minecraftforge.fml.common.event;

import java.io.File;
import java.security.CodeSource;
import java.security.cert.Certificate;
import java.util.Properties;

import net.minecraftforge.fml.common.FMLModContainer;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.LoaderState.ModState;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Called before {@link FMLInitializationEvent} during mod startup.
 *
 * This is the first of three commonly called events during mod initialization.
 *
 * Recommended activities:
 * Setup your logging {@link #getModLog()}
 * Load any configuration data you might have {@link #getSuggestedConfigurationFile()}
 * Search for a version.properties file and load it {@link #getVersionProperties()}
 * Configure your {@link ModMetadata} programmatically {@link #getModMetadata()}
 * Register your blocks and items with the {@link net.minecraftforge.fml.common.registry.GameRegistry}
 * Discover parts of your mod by using annotation search {@link #getAsmData()}
 *
 * @see net.minecraftforge.fml.common.Mod.EventHandler for how to subscribe to this event
 * @author cpw
 */
public class FMLPreInitializationEvent extends FMLStateEvent
{
    private ModMetadata modMetadata;
    private File sourceFile;
    private File configurationDir;
    private File suggestedConfigFile;
    private ASMDataTable asmData;
    private ModContainer modContainer;

    public FMLPreInitializationEvent(Object... data)
    {
        super(data);
        this.asmData = (ASMDataTable)data[0];
        this.configurationDir = (File)data[1];
    }

    @Override
    public ModState getModState()
    {
        return ModState.PREINITIALIZED;
    }

    @Override
    public void applyModContainer(ModContainer activeContainer)
    {
        this.modContainer = activeContainer;
        this.modMetadata = activeContainer.getMetadata();
        this.sourceFile = activeContainer.getSource();
        this.suggestedConfigFile = new File(configurationDir, activeContainer.getModId()+".cfg");
    }

    /**
     * Get the {@link File} the mod was loaded from
     * @return The file the mod was loaded from
     */
    public File getSourceFile()
    {
        return sourceFile;
    }

    /**
     * Get the {@link ModMetadata} for this mod
     * @return the mod metadata for the mod
     */
    public ModMetadata getModMetadata()
    {
        return modMetadata;
    }

    /**
     * Get the main configuration directory for this minecraft instance
     * @return the main configuration directory
     */
    public File getModConfigurationDirectory()
    {
        return configurationDir;
    }

    /**
     * Get a suggested configuration file for this mod. It will be of the form &lt;modid&gt;.cfg
     * @return A suggested configuration file name for this mod
     */
    public File getSuggestedConfigurationFile()
    {
        return suggestedConfigFile;
    }

    /**
     * Get the {@link ASMDataTable} for this instance of Minecraft. This is a special structure containing
     * parsing information from FML. It can be searched for annotations parsed out by FML.
     * @return
     */
    public ASMDataTable getAsmData()
    {
        return asmData;
    }

    /**
     * Get a version.properties file as a {@link Properties} object from the mod file.
     * This can be used to load build-type information
     * such as a unique version number from a properties file shipped as part of the distributable.
     * @return A properties object if one exists, else null
     */
    public Properties getVersionProperties()
    {
        if (this.modContainer instanceof FMLModContainer)
        {
            return ((FMLModContainer)this.modContainer).searchForVersionProperties();
        }

        return null;
    }

    /**
     * Get a logger instance configured to write to the FML Log as a parent, identified by modid. Handy for mod logging!
     * Configurations can be applied through the <code>config/logging.properties</code> file, specifying logging levels
     * for your ModID. Use this!
     *
     * @return A logger
     */
    public Logger getModLog()
    {
        Logger log = LogManager.getLogger(modContainer.getModId());
        return log;
    }


    /**
     * Retrieve the FML signing certificates, if any. Validate these against the
     * published FML certificates in your mod, if you wish.
     *
     * Deprecated because mods should <b>NOT</b> trust this code. Rather
     * they should copy this, or something like this, into their own mods.
     *
     * @return Certificates used to sign FML and Forge
     */
    @Deprecated
    public Certificate[] getFMLSigningCertificates()
    {
        CodeSource codeSource = getClass().getClassLoader().getParent().getClass().getProtectionDomain().getCodeSource();
        Certificate[] certs = codeSource.getCertificates();
        if (certs == null)
        {
            return new Certificate[0];
        }
        else
        {
            return certs;
        }
    }
}
