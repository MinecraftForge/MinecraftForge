/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.annotation.ElementType;
import java.util.LinkedList;

public class ModFieldVisitor extends FieldVisitor
{
    private final LinkedList<ModAnnotation> annotations;
    private final String fieldName;

    public ModFieldVisitor(String name, final LinkedList<ModAnnotation> annotations)
    {
        super(Opcodes.ASM9);
        this.fieldName = name;
        this.annotations = annotations;
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(String annotationName, boolean runtimeVisible)
    {
        ModAnnotation ann = new ModAnnotation(ElementType.FIELD, Type.getType(annotationName), fieldName);
        annotations.addFirst(ann);
        return new ModAnnotationVisitor(annotations, ann);
    }
}
