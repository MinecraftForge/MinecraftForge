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

public class ModFieldVisitor extends FieldVisitor
{
    private String fieldName;
    private ASMModParser discoverer;
    private Map<String, String> fieldSignatureMap;

    public ModFieldVisitor(String name, ASMModParser discoverer, String signature)
    {
        super(Opcodes.ASM5);
        this.fieldName = name;
        this.discoverer = discoverer;
        this.fieldSignatureMap = discoverer.parseVariableOrMethodSignature(signature);
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(String annotationName, boolean runtimeVisible)
    {
        discoverer.startFieldAnnotation(fieldName, annotationName);
        return new ModAnnotationVisitor(discoverer);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String annotationName, boolean runtimeVisible)
    {
        if (typePath != null)
        {
            discoverer.startFieldTypeAnnotation(fieldName, typePath.toString(), fieldSignatureMap.get(typePath.toString()), annotationName);
            return new ModAnnotationVisitor(discoverer);
        }
        else
        {
            return null;
        }
    }
}
