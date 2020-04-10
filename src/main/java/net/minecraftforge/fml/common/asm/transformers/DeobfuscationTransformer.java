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

package net.minecraftforge.fml.common.asm.transformers;

import java.util.Arrays;

import net.minecraft.launchwrapper.IClassNameTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;

import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLRemappingAdapter;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.RemappingClassAdapter;

public class DeobfuscationTransformer implements IClassTransformer, IClassNameTransformer {
    private static final String[] EXEMPT_LIBS = new String[] {
            "com.google.",
            "com.mojang.",
            "joptsimple.",
            "io.netty.",
            "it.unimi.dsi.fastutil.",
            "oshi.",
            "com.sun.",
            "com.ibm.",
            "paulscode.",
            "com.jcraft"
    };
    private static final String[] EXEMPT_DEV = new String[] {
            "net.minecraft.",
            "net.minecraftforge."
    };

    private static final boolean RECALC_FRAMES = Boolean.parseBoolean(System.getProperty("FORGE_FORCE_FRAME_RECALC", "false"));
    private static final int WRITER_FLAGS = ClassWriter.COMPUTE_MAXS | (RECALC_FRAMES ? ClassWriter.COMPUTE_FRAMES : 0);
    private static final int READER_FLAGS = RECALC_FRAMES ? ClassReader.SKIP_FRAMES : ClassReader.EXPAND_FRAMES;
    // COMPUTE_FRAMES causes classes to be loaded, which could cause issues if the classes do not exist.
    // However in testing this has not happened. {As we run post SideTransformer}
    // If reported we need to add a custom implementation of ClassWriter.getCommonSuperClass
    // that does not cause class loading.

    private boolean deobfuscatedEnvironment = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes)
    {
        if (bytes == null)
        {
            return null;
        }

        if (!shouldTransform(name)) return bytes;

        ClassReader classReader = new ClassReader(bytes);
        ClassWriter classWriter = new ClassWriter(WRITER_FLAGS);
        RemappingClassAdapter remapAdapter = new FMLRemappingAdapter(classWriter);
        classReader.accept(remapAdapter, READER_FLAGS);
        return classWriter.toByteArray();
    }

    private boolean shouldTransform(String name)
    {
        boolean transformLib = Arrays.stream(EXEMPT_LIBS).noneMatch(name::startsWith);

        if (deobfuscatedEnvironment)
        {
            return transformLib && Arrays.stream(EXEMPT_DEV).noneMatch(name::startsWith);
        }
        else
        {
            return transformLib;
        }
    }

    @Override
    public String remapClassName(String name)
    {
        return FMLDeobfuscatingRemapper.INSTANCE.map(name.replace('.','/')).replace('/', '.');
    }

    @Override
    public String unmapClassName(String name)
    {
        return FMLDeobfuscatingRemapper.INSTANCE.unmap(name.replace('.', '/')).replace('/','.');
    }

}
