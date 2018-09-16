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
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.annotation.ElementType;
import java.util.LinkedList;

public class ModMethodVisitor extends MethodVisitor {

    private final LinkedList<ModAnnotation> annotations;
    private String methodName;
    private String methodDescriptor;

    public ModMethodVisitor(String name, String desc, final LinkedList<ModAnnotation> annotations)
    {
        super(Opcodes.ASM5);
        this.methodName = name;
        this.methodDescriptor = desc;
        this.annotations = annotations;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String annotationName, boolean runtimeVisible)
    {
        ModAnnotation ann = new ModAnnotation(ElementType.METHOD, Type.getType(annotationName), methodName+methodDescriptor);
        annotations.addFirst(ann);
        return new ModAnnotationVisitor(annotations, ann);
    }

}
