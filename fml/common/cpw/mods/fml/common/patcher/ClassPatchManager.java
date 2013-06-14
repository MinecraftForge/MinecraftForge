package cpw.mods.fml.common.patcher;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.FMLRelaunchLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.repackage.com.nothome.delta.GDiffPatcher;

public class ClassPatchManager {
    public static final ClassPatchManager INSTANCE = new ClassPatchManager();

    private GDiffPatcher patcher = new GDiffPatcher();
    private ListMultimap<String, ClassPatch> patches;
    private ClassPatchManager()
    {

    }

    public byte[] applyPatch(String name, String mappedName, byte[] inputData)
    {
        if (patches == null)
        {
            return inputData;
        }
        List<ClassPatch> list = patches.get(name);
        if (list.isEmpty())
        {
            return inputData;
        }
        for (ClassPatch patch: list)
        {
            if (!patch.targetClassName.equals(mappedName))
            {
                FMLLog.warning("Binary patch found %s for wrong class %s", patch.targetClassName, mappedName);
            }
            if (!patch.existsAtTarget && (inputData == null || inputData.length == 0))
            {
                inputData = new byte[0];
            }
            else if (!patch.existsAtTarget)
            {
                FMLLog.warning("Patcher expecting empty class data file for %s, but received non-empty", patch.targetClassName);
            }
            synchronized (patcher)
            {
                try
                {
                    inputData = patcher.patch(inputData, patch.patch);
                }
                catch (IOException e)
                {
                    FMLLog.log(Level.SEVERE, e, "Encountered problem runtime patching class %s", name);
                    continue;
                }
            }
        }
        FMLLog.fine("Successfully applied runtime patches for %s", mappedName);
        return inputData;
    }

    public void setup(Side side, CodeSource fmlLib)
    {
        Pattern binpatchMatcher = Pattern.compile(String.format("binpatch/%s/*.binpatch", side.toString().toLowerCase(Locale.ENGLISH)));
        JarFile fmlJar;
        try
        {
            File fmlJarFile = new File(fmlLib.getLocation().toURI());
            fmlJar = new JarFile(fmlJarFile);
        }
        catch (Exception e)
        {
            FMLRelaunchLog.log(Level.SEVERE, e, "Error occurred reading binary patches. Problems may occur");
            throw Throwables.propagate(e);
        }

        patches = ArrayListMultimap.create();

        for (JarEntry entry : Collections.list(fmlJar.entries()))
        {
            if (binpatchMatcher.matcher(entry.getName()).matches())
            {
                ClassPatch cp = readPatch(entry, fmlJar);
                if (cp != null)
                {
                    patches.put(cp.sourceClassName, cp);
                }
            }
        }
        FMLLog.fine("Read %d binary patches from %s", patches.size(), fmlJar.getName());
        FMLLog.fine("Patch list :\n\t%s", Joiner.on("\t\n").join(patches.asMap().entrySet()));
    }

    private ClassPatch readPatch(JarEntry patchEntry, JarFile jarFile)
    {
        FMLLog.finest("Reading patch data from %s in file %s", patchEntry.getName(), jarFile.getName());
        ByteArrayDataInput input;
        try
        {
            input = ByteStreams.newDataInput(ByteStreams.toByteArray(jarFile.getInputStream(patchEntry)));
        }
        catch (IOException e)
        {
            FMLLog.log(Level.WARNING, e, "Unable to read binpatch file %s - ignoring", patchEntry.getName());
            return null;
        }
        String name = input.readUTF();
        String sourceClassName = input.readUTF();
        String targetClassName = input.readUTF();
        boolean exists = input.readBoolean();
        int patchLength = input.readInt();
        byte[] patchBytes = new byte[patchLength];
        input.readFully(patchBytes);

        return new ClassPatch(name, sourceClassName, targetClassName, exists, patchBytes);
    }
}
