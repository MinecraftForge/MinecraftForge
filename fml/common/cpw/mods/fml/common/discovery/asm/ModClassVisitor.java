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

package cpw.mods.fml.common.discovery.asm;

import java.util.Collections;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class ModClassVisitor extends ClassVisitor
{
    private ASMModParser discoverer;

    public ModClassVisitor(ASMModParser discoverer)
    {
        super(Opcodes.ASM4);
        this.discoverer = discoverer;
    }


    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
    {
        discoverer.beginNewTypeName(name, version, superName);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String annotationName, boolean runtimeVisible)
    {
        discoverer.startClassAnnotation(annotationName);
        return new ModAnnotationVisitor(discoverer);
    }


    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value)
    {
        return new ModFieldVisitor(name, discoverer);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
    {
        if (discoverer.isBaseMod(Collections.<String>emptyList()) && name.equals("getPriorities") && desc.equals(Type.getMethodDescriptor(Type.getType(String.class))))
        {
            return new ModMethodVisitor(name, discoverer);
        }
        return null;
    }
}
