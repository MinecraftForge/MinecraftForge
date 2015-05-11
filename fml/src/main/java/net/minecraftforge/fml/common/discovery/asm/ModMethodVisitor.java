package net.minecraftforge.fml.common.discovery.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ModMethodVisitor extends MethodVisitor {

    private String methodName;
    private String methodDescriptor;
    private ASMModParser discoverer;

    public ModMethodVisitor(String name, String desc, ASMModParser discoverer)
    {
        super(Opcodes.ASM5);
        this.methodName = name;
        this.methodDescriptor = desc;
        this.discoverer = discoverer;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String annotationName, boolean runtimeVisible)
    {
        discoverer.startMethodAnnotation(methodName, methodDescriptor, annotationName);
        return new ModAnnotationVisitor(discoverer);
    }

}
