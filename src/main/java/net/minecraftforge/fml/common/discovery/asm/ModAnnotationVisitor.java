/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.common.discovery.asm;

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
        super(Opcodes.ASM5);
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
