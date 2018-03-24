/*
 * Minecraft Forge
 * Copyright (c) 2017.
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
package net.minecraftforge.fml.common.asm.transformers;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

/**
 * Transforms all Enum classes to expose a <code>public static T $$newInstance</code> method for each constructor,
 * with matching arguments, for use via reflection in {@link net.minecraftforge.common.util.EnumHelper}.
 * This solves the whole 'enum constructors are forcibly private' issue. Even transforming the constructors
 * to public does not work.
 * <p>
 * Also sets the $VALUES field to non-final, to prevent JVM optimisation and because at least on Java 9,
 * even with changing the Field object's modifiers, setting will still fail otherwise.
 */
public class EnumConstructorTransformer implements IClassTransformer
{

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        if (basicClass == null)
            return null;
        ClassReader cr = new ClassReader(basicClass);
        if ((cr.getAccess() & Opcodes.ACC_ENUM) == 0)
        {
            return basicClass;
        }
        Visitor visitor = new Visitor();
        cr.accept(visitor, 0);
        return visitor.toByteArray();
    }

    private static class Visitor extends ClassVisitor
    {
        private ClassWriter cw;
        private Type thisType;

        Visitor()
        {
            super(Opcodes.ASM5);
            this.cv = this.cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
        {
            super.visit(version, access, name, signature, superName, interfaces);
            this.thisType = Type.getObjectType(name);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
        {
            if (name.equals("<init>"))
            {
                Method thisConstructor = new Method(name, desc);
                Method delegate = new Method("$$newInstance", this.thisType, thisConstructor.getArgumentTypes());
                int delegateAccess = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC;
                MethodVisitor mv = super.visitMethod(delegateAccess, delegate.getName(), delegate.getDescriptor(), null, null);
                GeneratorAdapter generated = new GeneratorAdapter(mv, delegateAccess, delegate.getName(), delegate.getDescriptor());
                generated.newInstance(this.thisType);
                generated.dup();
                generated.loadArgs();
                generated.invokeConstructor(this.thisType, thisConstructor);
                generated.returnValue();
                generated.endMethod();
            }
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value)
        {
            if ((name.equals("$VALUES") || name.equals("ENUM$VALUES")) && desc.equals("[" + this.thisType.getDescriptor()))
            {
                access &= ~Opcodes.ACC_FINAL;
            }
            return super.visitField(access, name, desc, signature, value);
        }

        public byte[] toByteArray()
        {
            return this.cw.toByteArray();
        }
    }
}
