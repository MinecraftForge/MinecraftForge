/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.annotation.ElementType;
import java.util.LinkedList;

public class ModMethodVisitor extends MethodVisitor {

    private final LinkedList<ModAnnotation> annotations;
    private String methodName;
    private String methodDescriptor;

    public ModMethodVisitor(String name, String desc, final LinkedList<ModAnnotation> annotations)
    {
        super(Opcodes.ASM9);
        this.methodName = name;
        this.methodDescriptor = desc;
        this.annotations = annotations;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String annotationName, boolean runtimeVisible)
    {
        ModAnnotation ann = new ModAnnotation(ElementType.METHOD, Type.getType(annotationName), methodName+methodDescriptor);
        annotations.addFirst(ann);
        return new ModAnnotationVisitor(annotations, ann);
    }

}
