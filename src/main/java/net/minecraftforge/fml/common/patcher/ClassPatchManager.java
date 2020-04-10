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

package net.minecraftforge.fml.common.patcher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.regex.Pattern;

import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.repackage.com.nothome.delta.GDiffPatcher;
import LZMA.LzmaInputStream;

import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import org.apache.commons.io.IOUtils;

public class ClassPatchManager {
    //Must be ABOVE INSTANCE so they get set in time for the constructor.
    public static final boolean dumpPatched = Boolean.parseBoolean(System.getProperty("fml.dumpPatchedClasses", "false"));
    public static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("fml.debugClassPatchManager", "false"));

    public static final ClassPatchManager INSTANCE = new ClassPatchManager();

    private GDiffPatcher patcher = new GDiffPatcher();
    private ListMultimap<String, ClassPatch> patches;

    private Map<String,byte[]> patchedClasses = Maps.newHashMap();
    private File tempDir;
    private ClassPatchManager()
    {
        if (dumpPatched)
        {
            tempDir = Files.createTempDir();
            FMLLog.log.info("Dumping patched classes to {}",tempDir.getAbsolutePath());
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
        if (DEBUG)
            FMLLog.log.debug("Runtime patching class {} (input size {}), found {} patch{}", mappedName, (inputData == null ? 0 : inputData.length), list.size(), list.size()!=1 ? "es" : "");
        for (ClassPatch patch: list)
        {
            if (!patch.targetClassName.equals(mappedName) && !patch.sourceClassName.equals(name))
            {
                FMLLog.log.warn("Binary patch found {} for wrong class {}", patch.targetClassName, mappedName);
            }
            if (!patch.existsAtTarget && (inputData == null || inputData.length == 0))
            {
                inputData = new byte[0];
            }
            else if (!patch.existsAtTarget)
            {
                FMLLog.log.warn("Patcher expecting empty class data file for {}, but received non-empty", patch.targetClassName);
            }
            else if (patch.existsAtTarget && (inputData == null || inputData.length == 0))
            {
                FMLLog.log.fatal("Patcher expecting non-empty class data file for {}, but received empty.", patch.targetClassName);
                throw new RuntimeException(String.format("Patcher expecting non-empty class data file for %s, but received empty, your vanilla jar may be corrupt.", patch.targetClassName));
            }
            else
            {
                int inputChecksum = Hashing.adler32().hashBytes(inputData).asInt();
                if (patch.inputChecksum != inputChecksum)
                {
                    FMLLog.log.fatal("There is a binary discrepancy between the expected input class {} ({}) and the actual class. Checksum on disk is {}, in patch {}. Things are probably about to go very wrong. Did you put something into the jar file?", mappedName, name, Integer.toHexString(inputChecksum), Integer.toHexString(patch.inputChecksum));
                    if (!Boolean.parseBoolean(System.getProperty("fml.ignorePatchDiscrepancies","false")))
                    {
                        FMLLog.log.fatal("The game is going to exit, because this is a critical error, and it is very improbable that the modded game will work, please obtain clean jar files.");
                        System.exit(1);
                    }
                    else
                    {
                        FMLLog.log.fatal("FML is going to ignore this error, note that the patch will not be applied, and there is likely to be a malfunctioning behaviour, including not running at all");
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
                    FMLLog.log.error("Encountered problem runtime patching class {}", name, e);
                    continue;
                }
            }
        }
        if (!ignoredError && DEBUG)
        {
            FMLLog.log.debug("Successfully applied runtime patches for {} (new size {})", mappedName, inputData.length);
        }
        if (dumpPatched)
        {
            try
            {
                Files.write(inputData, new File(tempDir,mappedName));
            }
            catch (IOException e)
            {
                FMLLog.log.error(FMLLog.log.getMessageFactory().newMessage("Failed to write {} to {}", mappedName, tempDir.getAbsolutePath()), e);
            }
        }
        patchedClasses.put(name,inputData);
        return inputData;
    }

    public void setup(Side side)
    {
        Pattern binpatchMatcher = Pattern.compile(String.format("binpatch/%s/.*.binpatch", side.toString().toLowerCase(Locale.ENGLISH)));
        JarInputStream jis = null;
        try
        {
            try
            {
                InputStream binpatchesCompressed = getClass().getResourceAsStream("/binpatches.pack.lzma");
                if (binpatchesCompressed==null)
                {
                    if (!FMLLaunchHandler.isDeobfuscatedEnvironment())
                    {
                        FMLLog.log.fatal("The binary patch set is missing, things are not going to work!");
                    }
                    return;
                }
                try (LzmaInputStream binpatchesDecompressed = new LzmaInputStream(binpatchesCompressed))
                {
                    ByteArrayOutputStream jarBytes = new ByteArrayOutputStream();
                    try (JarOutputStream jos = new JarOutputStream(jarBytes))
                    {
                        Pack200.newUnpacker().unpack(binpatchesDecompressed, jos);
                        jis = new JarInputStream(new ByteArrayInputStream(jarBytes.toByteArray()));
                    }
                }
            }
            catch (Exception e)
            {
                throw new RuntimeException("Error occurred reading binary patches. Expect severe problems!", e);
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
        }
        finally
        {
            IOUtils.closeQuietly(jis);
        }
        FMLLog.log.debug("Read {} binary patches", patches.size());
        if (DEBUG)
            FMLLog.log.debug("Patch list :\n\t{}", Joiner.on("\t\n").join(patches.asMap().entrySet()));
        patchedClasses.clear();
    }

    private ClassPatch readPatch(JarEntry patchEntry, JarInputStream jis)
    {
        if (DEBUG)
            FMLLog.log.trace("Reading patch data from {}", patchEntry.getName());
        ByteArrayDataInput input;
        try
        {
            input = ByteStreams.newDataInput(ByteStreams.toByteArray(jis));
        }
        catch (IOException e)
        {
            FMLLog.log.warn(FMLLog.log.getMessageFactory().newMessage("Unable to read binpatch file {} - ignoring", patchEntry.getName()), e);
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
