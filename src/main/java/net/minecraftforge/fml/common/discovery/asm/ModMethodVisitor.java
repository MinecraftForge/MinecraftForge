package net.minecraftforge.fml.common.discovery.asm;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.*;

import java.util.Map;

public class ModMethodVisitor extends MethodVisitor {

    private String methodName;
    private String methodDescriptor;
    private ASMModParser discoverer;
    private Map<Integer, String> methodDescriptorMap;
    private Map<ASMModParser.EnumMethodAdditionalInfo, Map<String, String>> methodSignatureMap;
    private Map<Integer, Pair<String, String>> localVariableMap = Maps.newHashMap();
    private Map<Integer, Map<String, String>> localVariableSignatureMap = Maps.newHashMap();

    public ModMethodVisitor(String name, String desc, String signature, ASMModParser discoverer)
    {
        super(Opcodes.ASM5);
        this.methodName = name;
        this.methodDescriptor = desc;
        this.discoverer = discoverer;
        this.methodDescriptorMap = discoverer.parseMethodDescriptor(desc);
        this.methodSignatureMap = discoverer.parseMethodSignature(signature);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String annotationName, boolean runtimeVisible)
    {
        discoverer.startMethodAnnotation(methodName, methodDescriptor, annotationName);
        return new ModAnnotationVisitor(discoverer);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String annotationName, boolean runtimeVisible)
    {
        if (typePath != null)
        {
            if (typeRef == 335544320)
            {
                discoverer.startMethodTypeAnnotation(methodName, typePath.toString(), methodSignatureMap.get(ASMModParser.EnumMethodAdditionalInfo.RETURN_VALUE_TYPE).get(typePath.toString()), annotationName, ASMModParser.EnumMethodAdditionalInfo.RETURN_VALUE_TYPE);
                return new ModAnnotationVisitor(discoverer);
            }
            else if (typeRef == 369098752)
            {
                discoverer.startMethodTypeAnnotation(methodName, typePath.toString(), methodSignatureMap.get(ASMModParser.EnumMethodAdditionalInfo.PARAMETER_TYPE).get(typePath.toString()), annotationName, ASMModParser.EnumMethodAdditionalInfo.PARAMETER_TYPE);
                return new ModAnnotationVisitor(discoverer);
            }
            return null;
        }
        else
        {
            return null;
        }
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String annotationName, boolean runtimeVisible)
    {
        discoverer.startMethodParameterAnnotation(parameter, methodName, methodDescriptor, methodDescriptorMap.get(parameter), annotationName);
        return new ModAnnotationVisitor(discoverer);
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index)
    {
        localVariableMap.put(index, Pair.of(name, signature));
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String annotationName, boolean runtimeVisible)
    {
        if (localVariableSignatureMap.isEmpty())
        {
            for (int i = 0; i < localVariableMap.size(); i++)
            {
                localVariableSignatureMap.put(i, discoverer.parseVariableOrMethodSignature(localVariableMap.get(i).getRight()));
            }
        }

        if (localVariableMap.containsKey(index[0]))
        {
            Pair<String, String> localVariableInformation = localVariableMap.get(index[0]);
            if (typePath == null)
            {
                discoverer.startLocalVariableAnnotation(methodName, methodDescriptor, annotationName, localVariableInformation.getLeft());
            }
            else
            {
                discoverer.startLocalVariableTypeAnnotation(methodName, methodDescriptor, typePath.toString(), annotationName, localVariableInformation.getLeft(), localVariableSignatureMap.get(index[0]).get(typePath.toString()));
            }
            return new ModAnnotationVisitor(discoverer);
        }
        else
        {
            return null;
        }
    }
}
