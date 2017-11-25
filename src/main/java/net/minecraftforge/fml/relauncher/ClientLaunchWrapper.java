package net.minecraftforge.fml.relauncher;

import net.minecraft.launchwrapper.Launch;

import javax.swing.*;

/**
 * Called before Launchwrapper, so we can check the java version first
 * TODO remove in 1.13 when ModLauncher supports J9
 */
@Deprecated
public class ClientLaunchWrapper {

    public static void main(String[] args) {
        //Check java version, as we don't support java 9 yet
        String javaVersion = System.getProperty("java.specification.version");
        if (javaVersion == null) //should never happen, but let's be safe
        {
            System.out.println("Could not determine java version, things may not work!");
        }
        else if (!javaVersion.startsWith("1.8") && !"true".equalsIgnoreCase(System.getProperty("fml.disableJavaVersionCheck")))
        {
            String msg = String.format("Your java version (%s) is not supported with this version of minecraft! Please use Java 8!", javaVersion);
            JOptionPane.showMessageDialog(null, msg, "Error launching minecraft", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(msg);
        }

        Launch.main(args);
    }
}
