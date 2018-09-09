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
package net.minecraftforge.common.asm;

import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.InstructionAdapter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;

/**
 * Modifies specified enums to allow runtime extension by making the $VALUES field non-final and
 * injecting constructor calls which are not valid in normal java code.
 */
public class RuntimeEnumExtender implements ILaunchPluginService {

    private static final Logger LOGGER = LogManager.getLogger();
    private final Type STRING = Type.getType(String.class);
    private final Type ARRAY_UTILS = Type.getType("Lorg/apache/commons/lang3/ArrayUtils;"); //Don't directly reference this to prevent class loading.
    private final String ADD_DESC = Type.getMethodDescriptor(Type.getType(Object[].class), Type.getType(Object[].class), Type.getType(Object.class));
    private final Type ENUM_HELPER = Type.getType("Lnet/minecraftforge/common/util/EnumHelper;"); //Again, not direct reference to prevent class loading.
    private final String CLEAN_DESC = Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(Class.class));

    @Override
    public String name() {
        return "runtime_enum_extender";
    }

    @Override public void addResource(Path resource, String name) { }

    @Override public <T> T getExtension() { return null; } // ?

    @Override
    public boolean handlesClass(Type classType, boolean isEmpty)
    {
        return !isEmpty; //Any way to determine if its a enum at this level?
    }

    @Override
    public ClassNode processClass(ClassNode classNode, Type classType)
    {
        if ((classNode.access & Opcodes.ACC_ENUM) == 0)
            return classNode;

        Type array = Type.getType("[" + classType.getInternalName());
        final int flags = Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL | Opcodes.ACC_SYNTHETIC;

        FieldNode values = classNode.fields.stream().filter(f -> f.desc.contentEquals(array.getDescriptor()) && ((f.access & flags) == flags)).findFirst().orElse(null);
        //Static methods named "create", with first argument as a string, and returning this type
        classNode.methods.stream().filter(m -> ((m.access & Opcodes.ACC_STATIC) != 0) && m.name.equals("create") && Type.getReturnType(m.desc).equals(classType)).forEach(mtd ->
        {
            Type[] args = Type.getArgumentTypes(mtd.desc);
            if (args.length == 0 || !args[0].equals(STRING))
                return;

            Type[] ctrArgs = new Type[args.length + 1];
            ctrArgs[0] = STRING;
            ctrArgs[1] = Type.INT_TYPE;
            for (int x = 1; x < args.length; x++)
                ctrArgs[1 + x] = args[x];

            String desc = Type.getMethodDescriptor(Type.VOID_TYPE, ctrArgs);

            MethodNode ctr = classNode.methods.stream().filter(m -> m.name.equals("<init>") && m.desc.equals(desc)).findFirst().orElse(null);
            if (ctr == null)
            {
                LOGGER.error("Enum has create method with no matching constructor:");
                LOGGER.error("  Target: " + desc);
                classNode.methods.stream().filter(m -> m.name.equals("<init>")).forEach(m -> LOGGER.error("        :" + m.desc));
                throw new IllegalStateException("Enum has create method with no matching constructor: " + desc);
            }

            if (values == null)
            {
                LOGGER.error("Enum has create method but we could not find $VALUES:");
                classNode.fields.stream().filter(f -> (f.access & Opcodes.ACC_STATIC) != 0).forEach(m -> LOGGER.error("  " + m.name + " " + m.desc));
                throw new IllegalStateException("Enum has create method but we could not find $VALUES");
            }

            values.access &= values.access & ~Opcodes.ACC_FINAL; //Strip the final so JITer doesn't inline things.

            mtd.instructions.clear();
            InstructionAdapter ins = new InstructionAdapter(mtd);
            //ret = new ThisType(name, VALUES.length + 1, args..)
            ins.anew(classType);
            ins.dup();
            ins.load(0, STRING);
            ins.getstatic(classType.getInternalName(), values.name, values.desc);
            ins.iconst(1);
            ins.add(Type.INT_TYPE);
            for (int x = 1; x < args.length; x++)
                ins.load(x, args[x]);
            ins.invokespecial(classType.getDescriptor(), "<init>", desc, false);
            ins.store(1, classType);
            // VALUES = ArrayUtils.add(VALUES, ret)
            ins.getstatic(classType.getInternalName(), values.name, values.desc);
            ins.load(1, classType);
            ins.invokestatic(ARRAY_UTILS.getDescriptor(), "add", ADD_DESC, false);
            ins.checkcast(array);
            ins.putstatic(classType.getInternalName(), values.name, values.desc);
            //EnumHelper.cleanEnumCache(ThisType.class)
            ins.visitLdcInsn(classType);
            ins.invokestatic(ENUM_HELPER.getInternalName(), "cleanEnumCache", CLEAN_DESC, false);
            //return ret
            ins.load(1, classType);
            ins.areturn(classType);
        });
        return classNode;
    }

}
