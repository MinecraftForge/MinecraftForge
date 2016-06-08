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

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import com.google.common.collect.Maps;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.LoaderException;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ModCandidate;
import org.apache.logging.log4j.Level;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.objectweb.asm.TypeReference;

public class ASMModParser
{
    private Type asmType;
    private int classVersion;
    private Type asmSuperType;
    private LinkedList<ModAnnotation> annotations = Lists.newLinkedList();
    private Set<String> interfaces = Sets.newHashSet();
    private String baseModProperties;

    static enum AnnotationType
    {
        CLASS, FIELD, METHOD, SUBTYPE, TYPE, PARAMETER, LOCAL_VARIABLE
    }

    public ASMModParser(InputStream stream) throws IOException
    {
        try
        {
            ClassReader reader = new ClassReader(stream);
            reader.accept(new ModClassVisitor(this), 0);
        }
        catch (Exception ex)
        {
            FMLLog.log(Level.ERROR, ex, "Unable to read a class file correctly");
            throw new LoaderException(ex);
        }
    }

    public void beginNewTypeName(String typeQName, int classVersion, String superClassQName, String[] interfaces)
    {
        this.asmType = Type.getObjectType(typeQName);
        this.classVersion = classVersion;
        this.asmSuperType = !Strings.isNullOrEmpty(superClassQName) ? Type.getObjectType(superClassQName) : null;
        for (String intf : interfaces)
            this.interfaces.add(intf);
    }

    public void startClassAnnotation(String annotationName)
    {
        ModAnnotation ann = new ModAnnotation(AnnotationType.CLASS, Type.getType(annotationName), this.asmType.getClassName());
        annotations.addFirst(ann);
    }

    public void startClassTypeAnnotation(String annotationName, String classSignature)
    {
        ModAnnotation ann = new ModAnnotation(AnnotationType.TYPE, Type.getType(annotationName), classSignature);
        annotations.addFirst(ann);
    }

    public void addAnnotationProperty(String key, Object value)
    {
        annotations.getFirst().addProperty(key, value);
    }

    public void startFieldAnnotation(String fieldName, String annotationName)
    {
        ModAnnotation ann = new ModAnnotation(AnnotationType.FIELD, Type.getType(annotationName), fieldName);
        annotations.addFirst(ann);
    }

    public void startFieldTypeAnnotation(String fieldName, String typePath, String signature, String annotationName)
    {
        ModAnnotation ann = new ModAnnotation(AnnotationType.TYPE, Type.getType(annotationName), fieldName + "$" + typePath + "(" + signature + ")");
        annotations.addFirst(ann);
    }

    public void startMethodTypeAnnotation(String methodName, String typePath, String signature, String annotationName, String additionalInfo)
    {
        ModAnnotation ann = new ModAnnotation(AnnotationType.TYPE, Type.getType(annotationName), methodName + "$" + typePath + "(" + signature + ")" + "[" + additionalInfo.toString() + "]");
        annotations.addFirst(ann);
    }

    public void startLocalVariableTypeAnnotation(String methodName, String methodDescriptor, String typePath, String annotationName, String localVariableName, String localVariableDescriptor)
    {
        ModAnnotation ann = new ModAnnotation(AnnotationType.LOCAL_VARIABLE, Type.getType(annotationName), methodName + methodDescriptor + "$" + localVariableName + "$" + typePath + "(" + localVariableDescriptor + ")");
        annotations.addFirst(ann);
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper("ASMAnnotationDiscoverer")
                .add("className", asmType.getClassName())
                .add("classVersion", classVersion)
                .add("superName", asmSuperType.getClassName())
                .add("annotations", annotations)
                .add("isBaseMod", isBaseMod(Collections.<String>emptyList()))
                .add("baseModProperties", baseModProperties)
                .toString();
    }

    public Type getASMType()
    {
        return asmType;
    }

    public int getClassVersion()
    {
        return classVersion;
    }

    public Type getASMSuperType()
    {
        return asmSuperType;
    }

    public LinkedList<ModAnnotation> getAnnotations()
    {
        return annotations;
    }

    public void validate()
    {
//        if (classVersion > 50.0)
//        {
//
//            throw new LoaderException(new RuntimeException("Mod compiled for Java 7 detected"));
//        }
    }

    public boolean isBaseMod(List<String> rememberedTypes)
    {
        return getASMSuperType().equals(Type.getType("LBaseMod;")) || getASMSuperType().equals(Type.getType("Lnet/minecraft/src/BaseMod;"))|| rememberedTypes.contains(getASMSuperType().getClassName());
    }

    public void setBaseModProperties(String foundProperties)
    {
        this.baseModProperties = foundProperties;
    }

    public String getBaseModProperties()
    {
        return this.baseModProperties;
    }

    public void sendToTable(ASMDataTable table, ModCandidate candidate)
    {
        for (ModAnnotation ma : annotations)
        {
            table.addASMData(candidate, ma.asmType.getClassName(), this.asmType.getClassName(), ma.member, ma.values);
        }

        for (String intf : interfaces)
        {
            table.addASMData(candidate, intf, this.asmType.getInternalName(), null, null);
        }
    }

    public void addAnnotationArray(String name)
    {
        annotations.getFirst().addArray(name);
    }

    public void addAnnotationEnumProperty(String name, String desc, String value)
    {
        annotations.getFirst().addEnumProperty(name, desc, value);

    }

    public void endArray()
    {
        annotations.getFirst().endArray();

    }

    public void addSubAnnotation(String name, String desc)
    {
        ModAnnotation ma = annotations.getFirst();
        annotations.addFirst(ma.addChildAnnotation(name, desc));
    }

    public void endSubAnnotation()
    {
        // take the child and stick it at the end
        ModAnnotation child = annotations.removeFirst();
        annotations.addLast(child);
    }

    public void startMethodAnnotation(String methodName, String methodDescriptor, String annotationName)
    {
        ModAnnotation ann = new ModAnnotation(AnnotationType.METHOD, Type.getType(annotationName), methodName+methodDescriptor);
        annotations.addFirst(ann);
    }

    public void startMethodParameterAnnotation(int parameter, String methodName, String methodDescriptor, String parameterDescriptor, String annotationName)
    {
        ModAnnotation ann = new ModAnnotation(AnnotationType.PARAMETER, Type.getType(annotationName), methodName + methodDescriptor + "$" + parameter + "(" + parameterDescriptor + ")");
        annotations.addFirst(ann);
    }

    public void startLocalVariableAnnotation(String methodName, String methodDescriptor, String annotationName, String localVariableName)
    {
        ModAnnotation ann = new ModAnnotation(AnnotationType.LOCAL_VARIABLE, Type.getType(annotationName), methodName + methodDescriptor + "$" + localVariableName);
        annotations.addFirst(ann);
    }

    public Map<Integer, String> parseClassSignature(String signature)
    {
        if (signature == null)
        {
            return Maps.newHashMap();
        }
        final List<String> classSignatures = Lists.newArrayList();
        boolean isStarted = false;
        final List<Boolean> notSplitList = Lists.newArrayList();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < signature.length(); i++)
        {
            char currentChar = signature.charAt(i);
            if (currentChar == '<')
            {
                if (isStarted)
                {
                    builder.append(currentChar);
                    notSplitList.add(true);
                }
                isStarted = true;
                continue;
            }
            if (currentChar == '>')
            {
                if (!notSplitList.isEmpty())
                {
                    notSplitList.remove(0);
                    builder.append(currentChar);
                    if (notSplitList.isEmpty())
                    {
                        classSignatures.add(builder.toString());
                        builder = new StringBuilder();
                    }
                    continue;
                }
                else
                {
                    classSignatures.add(builder.toString());
                    break;
                }
            }
            if (currentChar == ';')
            {
                if (notSplitList.isEmpty())
                {
                    classSignatures.add(builder.toString());
                    builder = new StringBuilder();
                    continue;
                }
            }
            if (isStarted)
            {
                builder.append(currentChar);
            }
        }
        classSignatures.removeIf(String::isEmpty);
        Map<Integer, String> classSignatureMap = Maps.newHashMap();
        for (int i = 0; i < classSignatures.size(); i++)
        {
            classSignatureMap.put(i, classSignatures.get(i));
        }
        return classSignatureMap;
    }

    public Map<String, String> parseVariableOrMethodSignature(String signature)
    {
        if (signature == null)
        {
            return Maps.newHashMap();
        }
        final Map<String, String> fieldSignatureMap = Maps.newHashMap();
        boolean isStarted = false;
        final List<Boolean> notSplitList = Lists.newArrayList();
        final List<Integer> typePathNumber = Lists.newArrayList();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < signature.length(); i++)
        {
            char currentChar = signature.charAt(i);
            if (currentChar == '<')
            {
                if (isStarted)
                {
                    String stringKey = "";
                    for (int number : typePathNumber)
                    {
                        stringKey += number + "*";
                    }
                    stringKey = stringKey.substring(0, stringKey.length() - 1);
                    fieldSignatureMap.put(stringKey, builder.toString());
                    builder = new StringBuilder();
                    notSplitList.add(true);
                }
                isStarted = true;
                typePathNumber.add(0);
            }
            else if (currentChar == '*')
            {
                builder.append(currentChar);
                String stringKey = "";
                for (int number : typePathNumber)
                {
                    stringKey += number + "*";
                }
                stringKey = stringKey.substring(0, stringKey.length() - 1);
                fieldSignatureMap.put(stringKey, builder.toString());
                builder = new StringBuilder();
            }
            else if (currentChar == '>')
            {
                if (!notSplitList.isEmpty())
                {
                    notSplitList.remove(0);
                    builder.append(currentChar);
                    typePathNumber.remove(typePathNumber.size() - 1);
                    if (notSplitList.isEmpty())
                    {
                        builder = new StringBuilder();
                    }
                }
                else
                {
                    break;
                }
            }
            else if (currentChar == ';')
            {
                String stringKey = "";
                for (int number : typePathNumber)
                {
                    stringKey += number + "*";
                }
                if (!stringKey.isEmpty())
                {
                    stringKey = stringKey.substring(0, stringKey.length() - 1);
                }
                if (!fieldSignatureMap.containsKey(stringKey))
                {
                    fieldSignatureMap.put(stringKey, builder.toString());
                }
                builder = new StringBuilder();
                if (!typePathNumber.isEmpty())
                {
                    typePathNumber.set(typePathNumber.size() - 1, typePathNumber.get(typePathNumber.size() - 1) + 1);
                }
                if (notSplitList.isEmpty())
                {
                    builder = new StringBuilder();
                }
            }
            else if (isStarted)
            {
                if (currentChar != '+')
                {
                    builder.append(currentChar);
                }
            }
        }
        if (signature.contains("<"))
        {
            fieldSignatureMap.put("", signature.substring(0, signature.indexOf("<")));
        }
        else
        {
            fieldSignatureMap.put("", signature.substring(0, signature.length() - 1));
        }
        return fieldSignatureMap;
    }

    public Map<Integer, Map<String, String>> parseMethodSignature(String signature)
    {
        if (signature == null)
        {
            return Maps.newHashMap();
        }
        Map<Integer, Map<String, String>> methodSignatureMap = Maps.newHashMap();
        methodSignatureMap.put(TypeReference.METHOD_RETURN, parseVariableOrMethodSignature(signature.substring(signature.indexOf(")") + 1)));
        methodSignatureMap.put(TypeReference.METHOD_FORMAL_PARAMETER, parseVariableOrMethodSignature(signature.substring(0, signature.indexOf(")"))));
        return methodSignatureMap;
    }

    public Map<Integer, String> parseMethodDescriptor(String methodDescriptor)
    {
        if (methodDescriptor == null)
        {
            return Maps.newHashMap();
        }
        String processingMethodDescriptor = methodDescriptor.substring("(".length(), methodDescriptor.indexOf(")"));
        String[] splitMethodDescriptor = processingMethodDescriptor.split(";");
        Map<Integer, String> methodDescriptorMap = Maps.newHashMap();
        for (int i = 0; i < splitMethodDescriptor.length; i++)
        {
            methodDescriptorMap.put(i, splitMethodDescriptor[i]);
        }
        return methodDescriptorMap;
    }
}
