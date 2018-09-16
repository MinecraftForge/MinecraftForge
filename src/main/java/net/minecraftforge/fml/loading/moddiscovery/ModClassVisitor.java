/*
 * Minecraft Forge
 * Copyright (c) 2018.
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

import net.minecraftforge.fml.language.ModFileScanData;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.annotation.ElementType;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModClassVisitor extends ClassVisitor
{
    private Type asmType;
    private Type asmSuperType;
    private Set<Type> interfaces;
    private final LinkedList<ModAnnotation> annotations = new LinkedList<>();
    public ModClassVisitor()
    {
        super(Opcodes.ASM5);
    }


    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
    {
        this.asmType = Type.getObjectType(name);
        this.asmSuperType = superName != null && superName.length() > 0 ? Type.getObjectType(superName) : null;
        this.interfaces = Stream.of(interfaces).map(Type::getObjectType).collect(Collectors.toSet());
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String annotationName, final boolean runtimeVisible)
    {
        ModAnnotation ann = new ModAnnotation(ElementType.TYPE, Type.getType(annotationName), this.asmType.getClassName());
        annotations.addFirst(ann);
        return new ModAnnotationVisitor(annotations, ann);
    }


    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value)
    {
        return new ModFieldVisitor(name, annotations);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
    {
        return new ModMethodVisitor(name, desc, annotations);
    }

    public void buildData(final List<ModFileScanData.ClassData> classes, final List<ModFileScanData.AnnotationData> annotations) {
        classes.add(new ModFileScanData.ClassData(this.asmType, this.asmSuperType, this.interfaces));
        final List<ModFileScanData.AnnotationData> collect = this.annotations.stream().
                filter(ma->ModFileScanData.interestingAnnotations().test(ma.asmType)).
                map(a -> ModAnnotation.fromModAnnotation(this.asmType, a)).collect(Collectors.toList());
        annotations.addAll(collect);
    }

}
