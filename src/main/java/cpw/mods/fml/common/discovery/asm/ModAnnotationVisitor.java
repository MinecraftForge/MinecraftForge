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
import org.objectweb.asm.Opcodes;

public class ModAnnotationVisitor extends AnnotationVisitor
{
    private ASMModParser discoverer;
    private boolean array;
    private String name;
    private boolean isSubAnnotation;

    public ModAnnotationVisitor(ASMModParser discoverer)
    {
        super(Opcodes.ASM4);
        this.discoverer = discoverer;
    }
    
    public ModAnnotationVisitor(ASMModParser discoverer, String name)
    {
        this(discoverer);
        this.array = true;
        this.name = name;
        discoverer.addAnnotationArray(name);
    }

    public ModAnnotationVisitor(ASMModParser discoverer, boolean isSubAnnotation)
    {
        this(discoverer);
        this.isSubAnnotation = true;
    }

    @Override
    public void visit(String key, Object value)
    {
        discoverer.addAnnotationProperty(key, value);
    }
    
    @Override
    public void visitEnum(String name, String desc, String value)
    {
        discoverer.addAnnotationEnumProperty(name, desc, value);
    }
    
    @Override
    public AnnotationVisitor visitArray(String name)
    {
        return new ModAnnotationVisitor(discoverer, name);
    }
    @Override
    public AnnotationVisitor visitAnnotation(String name, String desc)
    {
        discoverer.addSubAnnotation(name, desc);
        return new ModAnnotationVisitor(discoverer, true);
    }
    @Override
    public void visitEnd()
    {
        if (array)
        {
            discoverer.endArray();
        }
        
        if (isSubAnnotation)
        {
            discoverer.endSubAnnotation();
        }
    }
}
