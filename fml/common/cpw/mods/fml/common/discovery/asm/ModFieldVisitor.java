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

package cpw.mods.fml.common.discovery.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

public class ModFieldVisitor extends FieldVisitor
{

    private String fieldName;
    private ASMModParser discoverer;

    public ModFieldVisitor(String name, ASMModParser discoverer)
    {
        super(Opcodes.ASM4);
        this.fieldName = name;
        this.discoverer = discoverer;
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(String annotationName, boolean runtimeVisible)
    {
        discoverer.startFieldAnnotation(fieldName, annotationName);
        return new ModAnnotationVisitor(discoverer);
    }
}
