/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.fml.loading.moddiscovery;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

import java.util.LinkedList;

public class ModAnnotationVisitor extends AnnotationVisitor
{
    private final ModAnnotation annotation;
    private LinkedList<ModAnnotation> annotations;
    private boolean array;
    private String name;
    private boolean isSubAnnotation;

    public ModAnnotationVisitor(LinkedList<ModAnnotation> annotations, ModAnnotation annotation)
    {
        super(Opcodes.ASM5);
        this.annotations = annotations;
        this.annotation = annotation;
    }

    public ModAnnotationVisitor(LinkedList<ModAnnotation> annotations, ModAnnotation annotation, String name)
    {
        this(annotations, annotation);
        this.array = true;
        this.name = name;
        annotation.addArray(name);
    }

    public ModAnnotationVisitor(LinkedList<ModAnnotation> annotations, ModAnnotation annotation, boolean isSubAnnotation)
    {
        this(annotations, annotation);
        this.isSubAnnotation = true;
    }

    @Override
    public void visit(String key, Object value)
    {
        annotation.addProperty(key, value);
    }

    @Override
    public void visitEnum(String name, String desc, String value)
    {
        annotation.addEnumProperty(name, desc, value);
    }

    @Override
    public AnnotationVisitor visitArray(String name)
    {
        return new ModAnnotationVisitor(annotations, annotation, name);
    }
    @Override
    public AnnotationVisitor visitAnnotation(String name, String desc)
    {
        ModAnnotation ma = annotations.getFirst();
        final ModAnnotation childAnnotation = ma.addChildAnnotation(name, desc);
        annotations.addFirst(childAnnotation);
        return new ModAnnotationVisitor(annotations, childAnnotation,true);
    }
    @Override
    public void visitEnd()
    {
        if (array)
        {
            annotation.endArray();
        }

        if (isSubAnnotation)
        {
            ModAnnotation child = annotations.removeFirst();
            annotations.addLast(child);
        }
    }
}
