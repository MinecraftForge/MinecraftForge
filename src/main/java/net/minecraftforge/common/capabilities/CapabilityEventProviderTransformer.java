/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IModuleLayerManager;
import net.minecraftforge.eventbus.LockHelper;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Optional;

public class CapabilityEventProviderTransformer implements Opcodes {
    private static final LockHelper<String, Method> PENDING = new LockHelper<>(new HashMap<>());
    private Optional<ClassLoader> gameClassLoader = null;


    public void transform(ClassNode node) {
        // ICapabilityEventProvider
        node.methods.forEach(e -> {
            if (e.name.equals("createAttachCapabilitiesEvent")) {
                e.instructions.clear();

                MethodVisitor methodVisitor = e;
                ClassWriter classWriter = new ClassWriter(0);
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
        });
    }



    private ClassLoader getGameClassLoader() {
        if (this.gameClassLoader == null) {
            var gameLayer = Launcher.INSTANCE.findLayerManager().flatMap(lm -> lm.getLayer(IModuleLayerManager.Layer.GAME)).orElseThrow();
            this.gameClassLoader = gameLayer.modules().stream().findFirst().map(Module::getClassLoader);
        }
        return this.gameClassLoader.orElse(null);
    }
}
