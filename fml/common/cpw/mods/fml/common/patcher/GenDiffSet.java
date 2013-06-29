package cpw.mods.fml.common.patcher;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import cpw.mods.fml.repackage.com.nothome.delta.Delta;

public class GenDiffSet {

    public static void main(String[] args) throws IOException
    {
        String sourceJar = args[0]; //Clean Vanilla jar minecraft.jar or minecraft_server.jar
        String targetDir = args[1]; //Directory containing obfed output classes, typically mcp/reobf/minecraft
        String deobfData = args[2]; //Path to FML's deobfusication_data.zip
        String outputDir = args[3]; //Path to place generated .binpatch
        String killTarget = args[4]; //"true" if we should destroy the target file if it generated a successful .binpatch

        Logger.getLogger("GENDIFF").log(Level.INFO, String.format("Creating patches at %s for %s from %s", outputDir, sourceJar, targetDir));
        Delta delta = new Delta();
        FMLDeobfuscatingRemapper remapper = FMLDeobfuscatingRemapper.INSTANCE;
        remapper.setupLoadOnly(deobfData, false);
        JarFile sourceZip = new JarFile(sourceJar);
        boolean kill = killTarget.equalsIgnoreCase("true");

        File f = new File(outputDir);
        f.mkdirs();

        for (String name : remapper.getObfedClasses())
        {
//            Logger.getLogger("GENDIFF").info(String.format("Evaluating path for data :%s",name));
            File targetFile = new File(targetDir, name.replace('/', File.separatorChar) + ".class");
            if (targetFile.exists())
            {
                String sourceClassName = name.replace('/', '.');
                String targetClassName = remapper.map(name).replace('/', '.');
                JarEntry entry = sourceZip.getJarEntry(name);

                byte[] vanillaBytes = entry != null ? ByteStreams.toByteArray(sourceZip.getInputStream(entry)) : new byte[0];
                byte[] patchedBytes = Files.toByteArray(targetFile);

                byte[] diff = delta.compute(vanillaBytes, patchedBytes);

                ByteArrayDataOutput diffOut = ByteStreams.newDataOutput(diff.length + 50);
                // Original name
                diffOut.writeUTF(name);
                // Source name
                diffOut.writeUTF(sourceClassName);
                // Target name
                diffOut.writeUTF(targetClassName);
                // exists at original
                diffOut.writeBoolean(entry != null);
                // length of patch
                diffOut.writeInt(diff.length);
                // patch
                diffOut.write(diff);

                File target = new File(outputDir, targetClassName+".binpatch");
                target.getParentFile().mkdirs();
                Files.write(diffOut.toByteArray(), target);
                Logger.getLogger("GENDIFF").info(String.format("Wrote patch for %s (%s) at %s",name, targetClassName, target.getAbsolutePath()));
                if (kill)
                {
                    targetFile.delete();
                    Logger.getLogger("GENDIFF").info(String.format("  Deleted target: %s", targetFile.toString()));
                }
            }
        }
        sourceZip.close();
    }

}
