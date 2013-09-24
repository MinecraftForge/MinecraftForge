package cpw.mods.fml.common.patcher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.logging.Level;
import java.util.regex.Pattern;

import net.minecraft.launchwrapper.LaunchClassLoader;

import LZMA.LzmaInputStream;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import cpw.mods.fml.common.launcher.FMLTweaker;
import cpw.mods.fml.relauncher.FMLRelaunchLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.repackage.com.nothome.delta.GDiffPatcher;

public class ClassPatchManager {
    public static final ClassPatchManager INSTANCE = new ClassPatchManager();

    public static final boolean dumpPatched = Boolean.parseBoolean(System.getProperty("fml.dumpPatchedClasses", "false"));

    private GDiffPatcher patcher = new GDiffPatcher();
    private ListMultimap<String, ClassPatch> patches;

    private Map<String,byte[]> patchedClasses = Maps.newHashMap();
    private File tempDir;
    private ClassPatchManager()
    {
        if (dumpPatched)
        {
            tempDir = Files.createTempDir();
            FMLRelaunchLog.info("Dumping patched classes to %s",tempDir.getAbsolutePath());
        }
    }


    public byte[] getPatchedResource(String name, String mappedName, LaunchClassLoader loader) throws IOException
    {
        byte[] rawClassBytes = loader.getClassBytes(name);
        return applyPatch(name, mappedName, rawClassBytes);
    }
    public byte[] applyPatch(String name, String mappedName, byte[] inputData)
    {
        if (patches == null)
        {
            return inputData;
        }
        if (patchedClasses.containsKey(name))
        {
            return patchedClasses.get(name);
        }
        List<ClassPatch> list = patches.get(name);
        if (list.isEmpty())
        {
            return inputData;
        }
        boolean ignoredError = false;
        FMLRelaunchLog.fine("Runtime patching class %s (input size %d), found %d patch%s", mappedName, (inputData == null ? 0 : inputData.length), list.size(), list.size()!=1 ? "es" : "");
        for (ClassPatch patch: list)
        {
            if (!patch.targetClassName.equals(mappedName) && !patch.sourceClassName.equals(name))
            {
                FMLRelaunchLog.warning("Binary patch found %s for wrong class %s", patch.targetClassName, mappedName);
            }
            if (!patch.existsAtTarget && (inputData == null || inputData.length == 0))
            {
                inputData = new byte[0];
            }
            else if (!patch.existsAtTarget)
            {
                FMLRelaunchLog.warning("Patcher expecting empty class data file for %s, but received non-empty", patch.targetClassName);
            }
            else
            {
                int inputChecksum = Hashing.adler32().hashBytes(inputData).asInt();
                if (patch.inputChecksum != inputChecksum)
                {
                    FMLRelaunchLog.severe("There is a binary discrepency between the expected input class %s (%s) and the actual class. Checksum on disk is %x, in patch %x. Things are probably about to go very wrong. Did you put something into the jar file?", mappedName, name, inputChecksum, patch.inputChecksum);
                    if (!Boolean.parseBoolean(System.getProperty("fml.ignorePatchDiscrepancies","false")))
                    {
                        FMLRelaunchLog.severe("The game is going to exit, because this is a critical error, and it is very improbable that the modded game will work, please obtain clean jar files.");
                        System.exit(1);
                    }
                    else
                    {
                        FMLRelaunchLog.severe("FML is going to ignore this error, note that the patch will not be applied, and there is likely to be a malfunctioning behaviour, including not running at all");
                        ignoredError = true;
                        continue;
                    }
                }
            }
            synchronized (patcher)
            {
                try
                {
                    inputData = patcher.patch(inputData, patch.patch);
                }
                catch (IOException e)
                {
                    FMLRelaunchLog.log(Level.SEVERE, e, "Encountered problem runtime patching class %s", name);
                    continue;
                }
            }
        }
        if (!ignoredError)
        {
            FMLRelaunchLog.fine("Successfully applied runtime patches for %s (new size %d)", mappedName, inputData.length);
        }
        if (dumpPatched)
        {
            try
            {
                Files.write(inputData, new File(tempDir,mappedName));
            }
            catch (IOException e)
            {
                FMLRelaunchLog.log(Level.SEVERE, e, "Failed to write %s to %s", mappedName, tempDir.getAbsolutePath());
            }
        }
        patchedClasses.put(name,inputData);
        return inputData;
    }

    public void setup(Side side)
    {
        Pattern binpatchMatcher = Pattern.compile(String.format("binpatch/%s/.*.binpatch", side.toString().toLowerCase(Locale.ENGLISH)));
        JarInputStream jis;
        try
        {
            InputStream binpatchesCompressed = getClass().getResourceAsStream("/binpatches.pack.lzma");
            if (binpatchesCompressed==null)
            {
                FMLRelaunchLog.log(Level.SEVERE, "The binary patch set is missing. Either you are in a development environment, or things are not going to work!");
                return;
            }
            LzmaInputStream binpatchesDecompressed = new LzmaInputStream(binpatchesCompressed);
            ByteArrayOutputStream jarBytes = new ByteArrayOutputStream();
            JarOutputStream jos = new JarOutputStream(jarBytes);
            Pack200.newUnpacker().unpack(binpatchesDecompressed, jos);
            jis = new JarInputStream(new ByteArrayInputStream(jarBytes.toByteArray()));
        }
        catch (Exception e)
        {
            FMLRelaunchLog.log(Level.SEVERE, e, "Error occurred reading binary patches. Expect severe problems!");
            throw Throwables.propagate(e);
        }

        patches = ArrayListMultimap.create();

        do
        {
            try
            {
                JarEntry entry = jis.getNextJarEntry();
                if (entry == null)
                {
                    break;
                }
                if (binpatchMatcher.matcher(entry.getName()).matches())
                {
                    ClassPatch cp = readPatch(entry, jis);
                    if (cp != null)
                    {
                        patches.put(cp.sourceClassName, cp);
                    }
                }
                else
                {
                    jis.closeEntry();
                }
            }
            catch (IOException e)
            {
            }
        } while (true);
        FMLRelaunchLog.fine("Read %d binary patches", patches.size());
        FMLRelaunchLog.fine("Patch list :\n\t%s", Joiner.on("\t\n").join(patches.asMap().entrySet()));
        patchedClasses.clear();
    }

    private ClassPatch readPatch(JarEntry patchEntry, JarInputStream jis)
    {
        FMLRelaunchLog.finest("Reading patch data from %s", patchEntry.getName());
        ByteArrayDataInput input;
        try
        {
            input = ByteStreams.newDataInput(ByteStreams.toByteArray(jis));
        }
        catch (IOException e)
        {
            FMLRelaunchLog.log(Level.WARNING, e, "Unable to read binpatch file %s - ignoring", patchEntry.getName());
            return null;
        }
        String name = input.readUTF();
        String sourceClassName = input.readUTF();
        String targetClassName = input.readUTF();
        boolean exists = input.readBoolean();
        int inputChecksum = 0;
        if (exists)
        {
            inputChecksum = input.readInt();
        }
        int patchLength = input.readInt();
        byte[] patchBytes = new byte[patchLength];
        input.readFully(patchBytes);

        return new ClassPatch(name, sourceClassName, targetClassName, exists, inputChecksum, patchBytes);
    }
}
