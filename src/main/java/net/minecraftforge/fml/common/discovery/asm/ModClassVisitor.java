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

package net.minecraftforge.fml.common.discovery.asm;

import org.objectweb.asm.*;

import java.util.Map;

public class ModClassVisitor extends ClassVisitor
{
    private ASMModParser discoverer;
    private Map<Integer, String> classSignatureMap;

    public ModClassVisitor(ASMModParser discoverer)
    {
        super(Opcodes.ASM5);
        this.discoverer = discoverer;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
    {
        discoverer.beginNewTypeName(name, version, superName, interfaces);
        classSignatureMap = discoverer.parseClassSignature(signature);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String annotationName, boolean runtimeVisible)
    {
        discoverer.startClassAnnotation(annotationName);
        return new ModAnnotationVisitor(discoverer);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String annotationName, boolean runtimeVisible)
    {
        if (typePath != null)
        {
            discoverer.startClassTypeAnnotation(annotationName, classSignatureMap.get(typeRef / 65536));
            return new ModAnnotationVisitor(discoverer);
        }
        else
        {
            return null;
        }
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value)
    {
        return new ModFieldVisitor(name, discoverer, signature);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
    {
        return new ModMethodVisitor(name, desc, signature, discoverer);
    }
}
