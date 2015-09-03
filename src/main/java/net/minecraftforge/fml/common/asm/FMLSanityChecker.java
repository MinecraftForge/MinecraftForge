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

package net.minecraftforge.fml.common.asm;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.security.cert.Certificate;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.logging.log4j.Level;

import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.common.CertificateHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraftforge.fml.common.patcher.ClassPatchManager;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.FMLRelaunchLog;
import net.minecraftforge.fml.relauncher.IFMLCallHook;
import net.minecraftforge.fml.relauncher.Side;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

public class FMLSanityChecker implements IFMLCallHook
{
    private static final String FMLFINGERPRINT =   "51:0A:FB:4C:AF:A4:A0:F2:F5:CF:C5:0E:B4:CC:3C:30:24:4A:E3:8E".toLowerCase().replace(":", "");
    private static final String FORGEFINGERPRINT = "E3:C3:D5:0C:7C:98:6D:F7:4C:64:5C:0A:C5:46:39:74:1C:90:A5:57".toLowerCase().replace(":", "");
    private static final String MCFINGERPRINT =    "CD:99:95:96:56:F7:53:DC:28:D8:63:B4:67:69:F7:F8:FB:AE:FC:FC".toLowerCase().replace(":", "");
    private LaunchClassLoader cl;
    private boolean liveEnv;
    public static File fmlLocation;

    @Override
    public Void call() throws Exception
    {
        CodeSource codeSource = getClass().getProtectionDomain().getCodeSource();
        boolean goodFML = false;
        boolean fmlIsJar = false;
        if (codeSource.getLocation().getProtocol().equals("jar"))
        {
            fmlIsJar = true;
            Certificate[] certificates = codeSource.getCertificates();
            if (certificates!=null)
            {

                for (Certificate cert : certificates)
                {
                    String fingerprint = CertificateHelper.getFingerprint(cert);
                    if (fingerprint.equals(FMLFINGERPRINT))
                    {
                        FMLRelaunchLog.info("Found valid fingerprint for FML. Certificate fingerprint %s", fingerprint);
                        goodFML = true;
                    }
                    else if (fingerprint.equals(FORGEFINGERPRINT))
                    {
                        FMLRelaunchLog.info("Found valid fingerprint for Minecraft Forge. Certificate fingerprint %s", fingerprint);
                        goodFML = true;
                    }
                    else
                    {
                        FMLRelaunchLog.severe("Found invalid fingerprint for FML: %s", fingerprint);
                    }
                }
            }
        }
        else
        {
            goodFML = true;
        }
        // Server is not signed, so assume it's good - a deobf env is dev time so it's good too
        boolean goodMC = FMLLaunchHandler.side() == Side.SERVER || !liveEnv;
        int certCount = 0;
        try
        {
            Class<?> cbr = Class.forName("net.minecraft.client.ClientBrandRetriever",false, cl);
            codeSource = cbr.getProtectionDomain().getCodeSource();
        }
        catch (Exception e)
        {
            // Probably a development environment, or the server (the server is not signed)
            goodMC = true;
        }
        JarFile mcJarFile = null;
        if (fmlIsJar && !goodMC && codeSource.getLocation().getProtocol().equals("jar"))
        {
            try
            {
                String mcPath = codeSource.getLocation().getPath().substring(5);
                mcPath = mcPath.substring(0, mcPath.lastIndexOf('!'));
                mcPath = URLDecoder.decode(mcPath, Charsets.UTF_8.name());
                mcJarFile = new JarFile(mcPath,true);
                mcJarFile.getManifest();
                JarEntry cbrEntry = mcJarFile.getJarEntry("net/minecraft/client/ClientBrandRetriever.class");
                ByteStreams.toByteArray(mcJarFile.getInputStream(cbrEntry));
                Certificate[] certificates = cbrEntry.getCertificates();
                certCount = certificates != null ? certificates.length : 0;
                if (certificates!=null)
                {

                    for (Certificate cert : certificates)
                    {
                        String fingerprint = CertificateHelper.getFingerprint(cert);
                        if (fingerprint.equals(MCFINGERPRINT))
                        {
                            FMLRelaunchLog.info("Found valid fingerprint for Minecraft. Certificate fingerprint %s", fingerprint);
                            goodMC = true;
                        }
                    }
                }
            }
            catch (Throwable e)
            {
                FMLRelaunchLog.log(Level.ERROR, e, "A critical error occurred trying to read the minecraft jar file");
            }
            finally
            {
                if (mcJarFile != null)
                {
                    try
                    {
                        mcJarFile.close();
                    }
                    catch (IOException ioe)
                    {
                        // Noise
                    }
                }
            }
        }
        else
        {
            goodMC = true;
        }
        if (!goodMC)
        {
            FMLRelaunchLog.severe("The minecraft jar %s appears to be corrupt! There has been CRITICAL TAMPERING WITH MINECRAFT, it is highly unlikely minecraft will work! STOP NOW, get a clean copy and try again!",codeSource.getLocation().getFile());
            if (!Boolean.parseBoolean(System.getProperty("fml.ignoreInvalidMinecraftCertificates","false")))
            {
                FMLRelaunchLog.severe("For your safety, FML will not launch minecraft. You will need to fetch a clean version of the minecraft jar file");
                FMLRelaunchLog.severe("Technical information: The class net.minecraft.client.ClientBrandRetriever should have been associated with the minecraft jar file, " +
                		"and should have returned us a valid, intact minecraft jar location. This did not work. Either you have modified the minecraft jar file (if so " +
                		"run the forge installer again), or you are using a base editing jar that is changing this class (and likely others too). If you REALLY " +
                		"want to run minecraft in this configuration, add the flag -Dfml.ignoreInvalidMinecraftCertificates=true to the 'JVM settings' in your launcher profile.");
                FMLCommonHandler.instance().exitJava(1, false);
            }
            else
            {
                FMLRelaunchLog.severe("FML has been ordered to ignore the invalid or missing minecraft certificate. This is very likely to cause a problem!");
                FMLRelaunchLog.severe("Technical information: ClientBrandRetriever was at %s, there were %d certificates for it", codeSource.getLocation(), certCount);
            }
        }
        if (!goodFML)
        {
            FMLRelaunchLog.severe("FML appears to be missing any signature data. This is not a good thing");
        }
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data)
    {
        liveEnv = (Boolean)data.get("runtimeDeobfuscationEnabled");
        cl = (LaunchClassLoader) data.get("classLoader");
        File mcDir = (File)data.get("mcLocation");
        fmlLocation = (File)data.get("coremodLocation");
        ClassPatchManager.INSTANCE.setup(FMLLaunchHandler.side());
        FMLDeobfuscatingRemapper.INSTANCE.setup(mcDir, cl, (String) data.get("deobfuscationFileName"));
    }

}
