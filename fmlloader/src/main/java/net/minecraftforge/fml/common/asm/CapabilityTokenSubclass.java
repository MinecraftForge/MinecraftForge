/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.common.asm;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumSet;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;

/**
 * Implements getType() in CapabilityToken subclasses.
 *
 * Using the class's signature to determine the generic type of TypeToken, and then implements getType() using that value.
 * <pre>
 * Example:
 *
 *  <code>new CapabilityToken&lt;String>(){}</code>
 *  Has the signature <code>"CapabilityToken&lt;Ljava/lang/String;>"</code>
 *
 *  Implements the method:
 *  <code>public String getType() {
 *    return "java/lang/String";
 *  }</code>
 * </pre>
 */
public class CapabilityTokenSubclass implements ILaunchPluginService {

    private final String FUNC_NAME = "getType";
    private final String FUNC_DESC = "()Ljava/lang/String;";
    private final String CAP_INJECT = "net/minecraftforge/common/capabilities/CapabilityToken"; //Don't directly reference this to prevent class loading.

    @Override
    public String name() {
        return "capability_token_subclass";
    }

    private static final EnumSet<Phase> YAY = EnumSet.of(Phase.AFTER);
    private static final EnumSet<Phase> NAY = EnumSet.noneOf(Phase.class);

    @Override
    public EnumSet<Phase> handlesClass(Type classType, boolean isEmpty)
    {
        return isEmpty ? NAY : YAY;
    }

    @Override
    public int processClassWithFlags(final Phase phase, final ClassNode classNode, final Type classType, final String reason)
    {
        if (CAP_INJECT.equals(classNode.name))
        {
            for (MethodNode mtd : classNode.methods)
            {
                if (FUNC_NAME.equals(mtd.name) && FUNC_DESC.equals(mtd.desc))
                {
                    mtd.access &= ~Opcodes.ACC_FINAL; // We have it final in code so people don't override it, cuz that'd be stupid, and make our transformer more complicated.
                }
            }
            return ComputeFlags.SIMPLE_REWRITE;
        }
        else if (CAP_INJECT.equals(classNode.superName))
        {
            Holder cls = new Holder();

            SignatureReader reader = new SignatureReader(classNode.signature); // Having a node version of this would probably be useful.
            reader.accept(new SignatureVisitor(Opcodes.ASM9)
            {
                Deque<String> stack = new ArrayDeque<>();

                @Override
                public void visitClassType(final String name)
                {
                    stack.push(name);
                }

                @Override
                public void visitInnerClassType(final String name)
                {
                    stack.push(stack.pop() + '$' + name);
                }

                @Override
                public void visitEnd()
                {
                    var val = stack.pop();
                    if (!stack.isEmpty() && CAP_INJECT.equals(stack.peek()))
                        cls.value = val;
                }
            });

            if (cls.value == null)
                throw new IllegalStateException("Could not find signature for CapabilityToken on " + classNode.name + " from " + classNode.signature);

            var mtd = classNode.visitMethod(Opcodes.ACC_PUBLIC, FUNC_NAME, FUNC_DESC, null, new String[0]);
            mtd.visitLdcInsn(cls.value);
            mtd.visitInsn(Opcodes.ARETURN);
            mtd.visitEnd();
            return ComputeFlags.COMPUTE_MAXS;
        }
        else
        {
            return ComputeFlags.NO_REWRITE;
        }
    }

    private static class Holder {
        String value;
    }

}
