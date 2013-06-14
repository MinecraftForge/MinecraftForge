package cpw.mods.fml.common.patcher;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import cpw.mods.fml.common.FMLLog;
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

    public void setup(File dirToScan)
    {
        File[] patchFiles = dirToScan.listFiles(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                return Files.getFileExtension(new File(dir,name).getPath()).equals("binpatch");
            }
        });

        if (patchFiles == null)
        {
            return;
        }
        patches = ArrayListMultimap.create();
        for (File patch : patchFiles)
        {
            FMLLog.finest("Reading patch data from %s", patch.getAbsolutePath());
            ByteArrayDataInput input;
            try
            {
                input = ByteStreams.newDataInput(Files.toByteArray(patch));
            }
            catch (IOException e)
            {
                FMLLog.log(Level.WARNING, e, "Unable to read binpatch file %s - ignoring", patch.getAbsolutePath());
                continue;
            }
            String name = input.readUTF();
            String sourceClassName = input.readUTF();
            String targetClassName = input.readUTF();
            boolean exists = input.readBoolean();
            int patchLength = input.readInt();
            byte[] patchBytes = new byte[patchLength];
            input.readFully(patchBytes);

            ClassPatch cp = new ClassPatch(name, sourceClassName, targetClassName, exists, patchBytes);
            patches.put(sourceClassName, cp);
        }

        FMLLog.fine("Read %d binary patches from %s", patches.size(), dirToScan.getAbsolutePath());
        FMLLog.fine("Patch list : %s", patches);
    }
}
