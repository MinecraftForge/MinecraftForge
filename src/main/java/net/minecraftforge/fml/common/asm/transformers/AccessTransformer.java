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

package net.minecraftforge.fml.common.asm.transformers;

import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PROTECTED;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.FMLRelaunchLog;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.io.CharSource;
import com.google.common.io.LineProcessor;
import com.google.common.io.Resources;

public class AccessTransformer implements IClassTransformer
{
    private static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("fml.debugAccessTransformer", "false"));
    class Modifier
    {
        public String name = "";
        public String desc = "";
        public int oldAccess = 0;
        public int newAccess = 0;
        public int targetAccess = 0;
        public boolean changeFinal = false;
        public boolean markFinal = false;
        protected boolean modifyClassVisibility;

        private void setTargetAccess(String name)
        {
            if (name.startsWith("public")) targetAccess = ACC_PUBLIC;
            else if (name.startsWith("private")) targetAccess = ACC_PRIVATE;
            else if (name.startsWith("protected")) targetAccess = ACC_PROTECTED;

            if (name.endsWith("-f"))
            {
                changeFinal = true;
                markFinal = false;
            }
            else if (name.endsWith("+f"))
            {
                changeFinal = true;
                markFinal = true;
            }
        }
    }

    private Multimap<String, Modifier> modifiers = ArrayListMultimap.create();

    public AccessTransformer() throws IOException
    {
        this("forge_at.cfg");
    }
    protected AccessTransformer(String rulesFile) throws IOException
    {
        readMapFile(rulesFile);
    }

    AccessTransformer(Class<? extends AccessTransformer> dummyClazz)
    {
        // This is a noop
    }
    void readMapFile(String rulesFile) throws IOException
    {
        File file = new File(rulesFile);
        URL rulesResource;
        if (file.exists())
        {
            rulesResource = file.toURI().toURL();
        }
        else
        {
            rulesResource = Resources.getResource(rulesFile);
        }
        processATFile(Resources.asCharSource(rulesResource, Charsets.UTF_8));
        FMLRelaunchLog.fine("Loaded %d rules from AccessTransformer config file %s", modifiers.size(), rulesFile);
    }
    protected void processATFile(CharSource rulesResource) throws IOException
    {
        rulesResource.readLines(new LineProcessor<Void>()
        {
            @Override
            public Void getResult()
            {
                return null;
            }

            @Override
            public boolean processLine(String input) throws IOException
            {
                String line = Iterables.getFirst(Splitter.on('#').limit(2).split(input), "").trim();
                if (line.length()==0)
                {
                    return true;
                }
                List<String> parts = Lists.newArrayList(Splitter.on(" ").trimResults().split(line));
                if (parts.size()>3)
                {
                    throw new RuntimeException("Invalid config file line "+ input);
                }
                Modifier m = new Modifier();
                m.setTargetAccess(parts.get(0));

                if (parts.size() == 2)
                {
                    m.modifyClassVisibility = true;
                }
                else
                {
                    String nameReference = parts.get(2);
                    int parenIdx = nameReference.indexOf('(');
                    if (parenIdx>0)
                    {
                        m.desc = nameReference.substring(parenIdx);
                        m.name = nameReference.substring(0,parenIdx);
                    }
                    else
                    {
                        m.name = nameReference;
                    }
                }
                String className = parts.get(1).replace('/', '.');
                modifiers.put(className, m);
                if (DEBUG) System.out.printf("AT RULE: %s %s %s (type %s)\n", toBinary(m.targetAccess), m.name, m.desc, className);
                return true;
            }
        });
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes)
    {
        if (bytes == null) { return null; }

        if (DEBUG)
        {
            FMLRelaunchLog.fine("Considering all methods and fields on %s (%s)\n", transformedName, name);
        }
        if (!modifiers.containsKey(transformedName)) { return bytes; }

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        Collection<Modifier> mods = modifiers.get(transformedName);
        for (Modifier m : mods)
        {
            if (m.modifyClassVisibility)
            {
                classNode.access = getFixedAccess(classNode.access, m);
                if (DEBUG)
                {
                    System.out.println(String.format("Class: %s %s -> %s", name, toBinary(m.oldAccess), toBinary(m.newAccess)));
                }
                continue;
            }
            if (m.desc.isEmpty())
            {
                for (FieldNode n : classNode.fields)
                {
                    if (n.name.equals(m.name) || m.name.equals("*"))
                    {
                        n.access = getFixedAccess(n.access, m);
                        if (DEBUG)
                        {
                            System.out.println(String.format("Field: %s.%s %s -> %s", name, n.name, toBinary(m.oldAccess), toBinary(m.newAccess)));
                        }

                        if (!m.name.equals("*"))
                        {
                            break;
                        }
                    }
                }
            }
            else
            {
                List<MethodNode> nowOverridable = Lists.newArrayList();
                for (MethodNode n : classNode.methods)
                {
                    if ((n.name.equals(m.name) && n.desc.equals(m.desc)) || m.name.equals("*"))
                    {
                        n.access = getFixedAccess(n.access, m);

                        // constructors always use INVOKESPECIAL
                        if (!n.name.equals("<init>"))
                        {
                            // if we changed from private to something else we need to replace all INVOKESPECIAL calls to this method with INVOKEVIRTUAL
                            // so that overridden methods will be called. Only need to scan this class, because obviously the method was private.
                            boolean wasPrivate = (m.oldAccess & ACC_PRIVATE) == ACC_PRIVATE;
                            boolean isNowPrivate = (m.newAccess & ACC_PRIVATE) == ACC_PRIVATE;

                            if (wasPrivate && !isNowPrivate)
                            {
                                nowOverridable.add(n);
                            }

                        }

                        if (DEBUG)
                        {
                            System.out.println(String.format("Method: %s.%s%s %s -> %s", name, n.name, n.desc, toBinary(m.oldAccess), toBinary(m.newAccess)));
                        }

                        if (!m.name.equals("*"))
                        {
                            break;
                        }
                    }
                }

                replaceInvokeSpecial(classNode, nowOverridable);
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    private void replaceInvokeSpecial(ClassNode clazz, List<MethodNode> toReplace)
    {
        for (MethodNode method : clazz.methods)
        {
            for (Iterator<AbstractInsnNode> it = method.instructions.iterator(); it.hasNext();)
            {
                AbstractInsnNode insn = it.next();
                if (insn.getOpcode() == INVOKESPECIAL)
                {
                    MethodInsnNode mInsn = (MethodInsnNode) insn;
                    for (MethodNode n : toReplace)
                    {
                        if (n.name.equals(mInsn.name) && n.desc.equals(mInsn.desc))
                        {
                            mInsn.setOpcode(INVOKEVIRTUAL);
                            break;
                        }
                    }
                }
            }
        }
    }

    private String toBinary(int num)
    {
        return String.format("%16s", Integer.toBinaryString(num)).replace(' ', '0');
    }

    private int getFixedAccess(int access, Modifier target)
    {
        target.oldAccess = access;
        int t = target.targetAccess;
        int ret = (access & ~7);

        switch (access & 7)
        {
        case ACC_PRIVATE:
            ret |= t;
            break;
        case 0: // default
            ret |= (t != ACC_PRIVATE ? t : 0 /* default */);
            break;
        case ACC_PROTECTED:
            ret |= (t != ACC_PRIVATE && t != 0 /* default */? t : ACC_PROTECTED);
            break;
        case ACC_PUBLIC:
            ret |= (t != ACC_PRIVATE && t != 0 /* default */&& t != ACC_PROTECTED ? t : ACC_PUBLIC);
            break;
        default:
            throw new RuntimeException("The fuck?");
        }

        // Clear the "final" marker on fields only if specified in control field
        if (target.changeFinal)
        {
            if (target.markFinal)
            {
                ret |= ACC_FINAL;
            }
            else
            {
                ret &= ~ACC_FINAL;
            }
        }
        target.newAccess = ret;
        return ret;
    }

    public static void main(String[] args)
    {
        if (args.length < 2)
        {
            System.out.println("Usage: AccessTransformer <JarPath> <MapFile> [MapFile2]... ");
            System.exit(1);
        }

        boolean hasTransformer = false;
        AccessTransformer[] trans = new AccessTransformer[args.length - 1];
        for (int x = 1; x < args.length; x++)
        {
            try
            {
                trans[x - 1] = new AccessTransformer(args[x]);
                hasTransformer = true;
            }
            catch (IOException e)
            {
                System.out.println("Could not read Transformer Map: " + args[x]);
                e.printStackTrace();
            }
        }

        if (!hasTransformer)
        {
            System.out.println("Culd not find a valid transformer to perform");
            System.exit(1);
        }

        File orig = new File(args[0]);
        File temp = new File(args[0] + ".ATBack");
        if (!orig.exists() && !temp.exists())
        {
            System.out.println("Could not find target jar: " + orig);
            System.exit(1);
        }

        if (!orig.renameTo(temp))
        {
            System.out.println("Could not rename file: " + orig + " -> " + temp);
            System.exit(1);
        }

        try
        {
            processJar(temp, orig, trans);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }

        if (!temp.delete())
        {
            System.out.println("Could not delete temp file: " + temp);
        }
    }

    private static void processJar(File inFile, File outFile, AccessTransformer[] transformers) throws IOException
    {
        ZipInputStream inJar = null;
        ZipOutputStream outJar = null;

        try
        {
            try
            {
                inJar = new ZipInputStream(new BufferedInputStream(new FileInputStream(inFile)));
            }
            catch (FileNotFoundException e)
            {
                throw new FileNotFoundException("Could not open input file: " + e.getMessage());
            }

            try
            {
                outJar = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)));
            }
            catch (FileNotFoundException e)
            {
                throw new FileNotFoundException("Could not open output file: " + e.getMessage());
            }

            ZipEntry entry;
            while ((entry = inJar.getNextEntry()) != null)
            {
                if (entry.isDirectory())
                {
                    outJar.putNextEntry(entry);
                    continue;
                }

                byte[] data = new byte[4096];
                ByteArrayOutputStream entryBuffer = new ByteArrayOutputStream();

                int len;
                do
                {
                    len = inJar.read(data);
                    if (len > 0)
                    {
                        entryBuffer.write(data, 0, len);
                    }
                }
                while (len != -1);

                byte[] entryData = entryBuffer.toByteArray();

                String entryName = entry.getName();

                if (entryName.endsWith(".class") && !entryName.startsWith("."))
                {
                    ClassNode cls = new ClassNode();
                    ClassReader rdr = new ClassReader(entryData);
                    rdr.accept(cls, 0);
                    String name = cls.name.replace('/', '.').replace('\\', '.');

                    for (AccessTransformer trans : transformers)
                    {
                        entryData = trans.transform(name, name, entryData);
                    }
                }

                ZipEntry newEntry = new ZipEntry(entryName);
                outJar.putNextEntry(newEntry);
                outJar.write(entryData);
            }
        }
        finally
        {
            if (outJar != null)
            {
                try
                {
                    outJar.close();
                }
                catch (IOException e)
                {
                }
            }

            if (inJar != null)
            {
                try
                {
                    inJar.close();
                }
                catch (IOException e)
                {
                }
            }
        }
    }
    Multimap<String, Modifier> getModifiers()
    {
        return modifiers;
    }
    boolean isEmpty()
    {
        return modifiers.isEmpty();
    }
}
