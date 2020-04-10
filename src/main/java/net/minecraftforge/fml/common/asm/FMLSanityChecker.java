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

package net.minecraftforge.fml.common.asm;

import java.io.File;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.CodeSource;
import java.security.cert.Certificate;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.minecraftforge.fml.common.FMLLog;
import org.apache.commons.io.IOUtils;

import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.common.CertificateHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraftforge.fml.common.patcher.ClassPatchManager;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.IFMLCallHook;
import net.minecraftforge.fml.relauncher.Side;

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
                        FMLLog.log.info("Found valid fingerprint for FML. Certificate fingerprint {}", fingerprint);
                        goodFML = true;
                    }
                    else if (fingerprint.equals(FORGEFINGERPRINT))
                    {
                        FMLLog.log.info("Found valid fingerprint for Minecraft Forge. Certificate fingerprint {}", fingerprint);
                        goodFML = true;
                    }
                    else
                    {
                        FMLLog.log.error("Found invalid fingerprint for FML: {}", fingerprint);
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
                mcPath = URLDecoder.decode(mcPath, StandardCharsets.UTF_8.name());
                mcJarFile = new JarFile(mcPath,true);
                mcJarFile.getManifest();
                JarEntry cbrEntry = mcJarFile.getJarEntry("net/minecraft/client/ClientBrandRetriever.class");
                InputStream mcJarFileInputStream = mcJarFile.getInputStream(cbrEntry);
                try
                {
                    ByteStreams.toByteArray(mcJarFileInputStream);
                }
                finally
                {
                    IOUtils.closeQuietly(mcJarFileInputStream);
                }
                Certificate[] certificates = cbrEntry.getCertificates();
                certCount = certificates != null ? certificates.length : 0;
                if (certificates!=null)
                {

                    for (Certificate cert : certificates)
                    {
                        String fingerprint = CertificateHelper.getFingerprint(cert);
                        if (fingerprint.equals(MCFINGERPRINT))
                        {
                            FMLLog.log.info("Found valid fingerprint for Minecraft. Certificate fingerprint {}", fingerprint);
                            goodMC = true;
                        }
                    }
                }
            }
            catch (Throwable e)
            {
                FMLLog.log.error("A critical error occurred trying to read the minecraft jar file", e);
            }
            finally
            {
                IOUtils.closeQuietly(mcJarFile);
            }
        }
        else
        {
            goodMC = true;
        }
        if (!goodMC)
        {
            FMLLog.log.error("The minecraft jar {} appears to be corrupt! There has been CRITICAL TAMPERING WITH MINECRAFT, it is highly unlikely minecraft will work! STOP NOW, get a clean copy and try again!", codeSource.getLocation().getFile());
            if (!Boolean.parseBoolean(System.getProperty("fml.ignoreInvalidMinecraftCertificates","false")))
            {
                FMLLog.log.error("For your safety, FML will not launch minecraft. You will need to fetch a clean version of the minecraft jar file");
                FMLLog.log.error("Technical information: The class net.minecraft.client.ClientBrandRetriever should have been associated with the minecraft jar file, " +
                		"and should have returned us a valid, intact minecraft jar location. This did not work. Either you have modified the minecraft jar file (if so " +
                		"run the forge installer again), or you are using a base editing jar that is changing this class (and likely others too). If you REALLY " +
                		"want to run minecraft in this configuration, add the flag -Dfml.ignoreInvalidMinecraftCertificates=true to the 'JVM settings' in your launcher profile.");
                FMLCommonHandler.instance().exitJava(1, false);
            }
            else
            {
                FMLLog.log.error("FML has been ordered to ignore the invalid or missing minecraft certificate. This is very likely to cause a problem!");
                FMLLog.log.error("Technical information: ClientBrandRetriever was at {}, there were {} certificates for it", codeSource.getLocation(), certCount);
            }
        }
        if (!goodFML)
        {
            FMLLog.log.error("FML appears to be missing any signature data. This is not a good thing");
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
