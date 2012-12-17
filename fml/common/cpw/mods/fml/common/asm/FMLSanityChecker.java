package cpw.mods.fml.common.asm;

import java.io.ObjectInputStream.GetField;
import java.net.JarURLConnection;
import java.security.CodeSource;
import java.security.cert.Certificate;
import java.util.Map;

import javax.swing.JOptionPane;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

import cpw.mods.fml.common.CertificateHelper;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.RelaunchClassLoader;

public class FMLSanityChecker implements IFMLCallHook
{
    private static final String FMLFINGERPRINT = "EE:E2:73:7A:8B:90:5F:7D:C6:02:D5:B7:23:9F:B6:29:C2:18:0F:3E".toLowerCase().replace(":","");
    static class MLDetectorClassVisitor extends ClassVisitor
    {
        private boolean foundMarker = false;
        private MLDetectorClassVisitor()
        {
            super(Opcodes.ASM4);
        }

        @Override
        public FieldVisitor visitField(int arg0, String arg1, String arg2, String arg3, Object arg4)
        {
            if ("fmlMarker".equals(arg1))
            {
                foundMarker = true;
            }
            return null;
        }
    }

    private RelaunchClassLoader cl;

    @Override
    public Void call() throws Exception
    {
        CodeSource codeSource = getClass().getProtectionDomain().getCodeSource();
        boolean goodFML = false;
        if (codeSource.getLocation().getProtocol().equals("jar"))
        {
            Certificate[] certificates = codeSource.getCertificates();
            if (certificates!=null && certificates.length>0)
            {
                Certificate certificate = certificates[0];
                String fingerprint = CertificateHelper.getFingerprint(certificate);
                if (fingerprint.equals(FMLFINGERPRINT))
                {
                    FMLLog.info("Found valid fingerprint for FML: %s", fingerprint);
                    goodFML = true;
                }
                else
                {
                    FMLLog.severe("Found invalid fingerprint for FML: %s", fingerprint);
                }
            }
        }
        else
        {
            goodFML = true;
        }
        if (!goodFML)
        {
            FMLLog.severe("FML appears to be missing it's signature data. This is not a good thing");
        }
        byte[] mlClass = cl.getClassBytes("ModLoader");
        // Only care in obfuscated env
        if (mlClass == null)
        {
            return null;
        }
        MLDetectorClassVisitor mlTester = new MLDetectorClassVisitor();
        ClassReader cr = new ClassReader(mlClass);
        cr.accept(mlTester, ClassReader.SKIP_CODE);
        if (!mlTester.foundMarker)
        {
            JOptionPane.showMessageDialog(null, "<html>CRITICAL ERROR<br/>" +
            		"ModLoader was detected in this environment<br/>" +
                        "ForgeModLoader cannot be installed alongside ModLoader<br/>" +
                        "All mods should work without ModLoader being installed<br/>" +
                        "Because ForgeModLoader is 100% compatible with ModLoader<br/>" +
                        "Re-install Minecraft Forge or Forge ModLoader into a clean<br/>" +
                        "jar and try again.",
                        "ForgeModLoader critical error",
                        JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("Invalid ModLoader class detected");
        }
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data)
    {
        cl = (RelaunchClassLoader) data.get("classLoader");
    }

}
