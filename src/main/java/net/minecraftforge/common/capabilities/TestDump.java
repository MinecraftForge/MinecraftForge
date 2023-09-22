/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.RecordComponentVisitor;

public class TestDump implements Opcodes {

    public static byte[] dump () throws Exception {

        ClassWriter classWriter = new ClassWriter(0);
        FieldVisitor fieldVisitor;
        RecordComponentVisitor recordComponentVisitor;
        MethodVisitor methodVisitor;
        AnnotationVisitor annotationVisitor0;

        classWriter.visit(61, ACC_PUBLIC | ACC_SUPER, "net/minecraftforge/common/capabilities/Test", null, "java/lang/Object", new String[] { "net/minecraftforge/common/capabilities/ICapabilityEventProvider" });

        classWriter.visitSource("Test.java", null);

        classWriter.visitNestMember("net/minecraftforge/common/capabilities/Test$AttachEvent");

        classWriter.visitInnerClass("net/minecraftforge/common/capabilities/Test$AttachEvent", "net/minecraftforge/common/capabilities/Test", "AttachEvent", ACC_PUBLIC | ACC_STATIC);

        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(10, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            methodVisitor.visitInsn(RETURN);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLocalVariable("this", "Lnet/minecraftforge/common/capabilities/Test;", null, label0, label1, 0);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "createAttachCapabilitiesEvent", "(Ljava/lang/Object;)Lnet/minecraftforge/event/AttachCapabilitiesEvent;", "<T:Ljava/lang/Object;>(TT;)Lnet/minecraftforge/event/AttachCapabilitiesEvent<TT;>;", null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(13, label0);
            methodVisitor.visitTypeInsn(NEW, "net/minecraftforge/common/capabilities/Test$AttachEvent");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "net/minecraftforge/common/capabilities/Test$AttachEvent", "<init>", "(Ljava/lang/Object;)V", false);
            methodVisitor.visitInsn(ARETURN);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLocalVariable("this", "Lnet/minecraftforge/common/capabilities/Test;", null, label0, label1, 0);
            methodVisitor.visitLocalVariable("obj", "Ljava/lang/Object;", "TT;", label0, label1, 1);
            methodVisitor.visitMaxs(3, 2);
            methodVisitor.visitEnd();
        }
        classWriter.visitEnd();

        return classWriter.toByteArray();
    }
}