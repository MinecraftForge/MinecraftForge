package net.minecraftforge.server;

import com.google.common.collect.ObjectArrays;
import cpw.mods.modlauncher.Launcher;

public class ServerMain {
    public static void main(String[] args) {
        final String[] argArray = ObjectArrays.concat(new String[]{"--launchTarget", "fmlserver","--gameDir", "."}, args, String.class);
        Launcher.main(argArray);
    }
}
