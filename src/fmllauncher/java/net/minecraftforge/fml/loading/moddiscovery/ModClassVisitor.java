/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.annotation.ElementType;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModClassVisitor extends ClassVisitor
{
    private Type asmType;
    private Type asmSuperType;
    private Set<Type> interfaces;
    private final LinkedList<ModAnnotation> annotations = new LinkedList<>();
    public ModClassVisitor()
    {
        super(Opcodes.ASM9);
    }


    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
    {
        this.asmType = Type.getObjectType(name);
        this.asmSuperType = superName != null && superName.length() > 0 ? Type.getObjectType(superName) : null;
        this.interfaces = Stream.of(interfaces).map(Type::getObjectType).collect(Collectors.toSet());
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String annotationName, final boolean runtimeVisible)
    {
        ModAnnotation ann = new ModAnnotation(ElementType.TYPE, Type.getType(annotationName), this.asmType.getClassName());
        annotations.addFirst(ann);
        return new ModAnnotationVisitor(annotations, ann);
    }


    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value)
    {
        return new ModFieldVisitor(name, annotations);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
    {
        return new ModMethodVisitor(name, desc, annotations);
    }

    public void buildData(final Set<ModFileScanData.ClassData> classes, final Set<ModFileScanData.AnnotationData> annotations) {
        classes.add(new ModFileScanData.ClassData(this.asmType, this.asmSuperType, this.interfaces));
        final List<ModFileScanData.AnnotationData> collect = this.annotations.stream().
                filter(ma->ModFileScanData.interestingAnnotations().test(ma.getASMType())).
                map(a -> ModAnnotation.fromModAnnotation(this.asmType, a)).collect(Collectors.toList());
        annotations.addAll(collect);
    }

}
