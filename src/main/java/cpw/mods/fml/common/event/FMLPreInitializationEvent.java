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

package cpw.mods.fml.common.event;

import java.io.File;
import java.security.CodeSource;
import java.security.cert.Certificate;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.LoaderState.ModState;
import cpw.mods.fml.common.FMLModContainer;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.discovery.ASMDataTable;

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

    public File getSourceFile()
    {
        return sourceFile;
    }

    public ModMetadata getModMetadata()
    {
        return modMetadata;
    }

    public File getModConfigurationDirectory()
    {
        return configurationDir;
    }

    public File getSuggestedConfigurationFile()
    {
        return suggestedConfigFile;
    }

    public ASMDataTable getAsmData()
    {
        return asmData;
    }

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