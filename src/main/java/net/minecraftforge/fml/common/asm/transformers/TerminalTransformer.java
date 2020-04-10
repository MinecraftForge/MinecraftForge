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

package net.minecraftforge.fml.common.asm.transformers;

import org.objectweb.asm.*;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.FMLSecurityManager.ExitTrappedException;

public class TerminalTransformer implements IClassTransformer
{
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        if (basicClass == null) return null;
        ClassReader reader = new ClassReader(basicClass);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        ExitVisitor visitor = new ExitVisitor(writer);

        reader.accept(visitor, 0);

        if (!visitor.dirty) return basicClass;
        return writer.toByteArray();
    }

    public static class ExitVisitor extends ClassVisitor
    {
        private String clsName = null;
        private boolean dirty;
        private static final String callbackOwner = org.objectweb.asm.Type.getInternalName(ExitVisitor.class);

        private ExitVisitor(ClassVisitor cv)
        {
            super(Opcodes.ASM5, cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
        {
            super.visit(version, access, name, signature, superName, interfaces);
            this.clsName = name;
        }

        @Override
        public MethodVisitor visitMethod(int mAccess, final String mName, final String mDesc, String mSignature, String[] mExceptions)
        {
            final boolean warn = !(clsName.equals("net/minecraft/client/Minecraft") ||
                                   clsName.equals("net/minecraft/server/dedicated/DedicatedServer") ||
                                   clsName.equals("net/minecraft/server/dedicated/ServerHangWatchdog") ||
                                   clsName.equals("net/minecraft/server/dedicated/ServerHangWatchdog$1") ||
                                   clsName.equals("net/minecraftforge/fml/common/FMLCommonHandler") ||
                                   clsName.startsWith("com/jcraft/jogg/") ||
                                   clsName.startsWith("scala/sys/") ||
                                   clsName.startsWith("net/minecraft/server/gui/MinecraftServerGui") ||
                                   clsName.startsWith("com/sun/jna/")
                                   );

            return new MethodVisitor(Opcodes.ASM5, super.visitMethod(mAccess, mName, mDesc, mSignature, mExceptions))
            {
                @Override
                public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean isIntf)
                {
                    if (opcode == Opcodes.INVOKESTATIC && owner.equals("java/lang/System") && name.equals("exit") && desc.equals("(I)V"))
                    {
                        if (warn)
                        {
                            FMLLog.log.warn("=============================================================");
                            FMLLog.log.warn("MOD HAS DIRECT REFERENCE System.exit() THIS IS NOT ALLOWED REROUTING TO FML!");
                            FMLLog.log.warn("Offender: {}.{}{}", ExitVisitor.this.clsName, mName, mDesc);
                            FMLLog.log.warn("Use FMLCommonHandler.exitJava instead");
                            FMLLog.log.warn("=============================================================");
                        }
                        owner = ExitVisitor.callbackOwner;
                        name = "systemExitCalled";
                        dirty = true;
                    }
                    else if (opcode == Opcodes.INVOKEVIRTUAL && owner.equals("java/lang/Runtime") && name.equals("exit") && desc.equals("(I)V"))
                    {
                        if (warn)
                        {
                            FMLLog.log.warn("=============================================================");
                            FMLLog.log.warn("MOD HAS DIRECT REFERENCE Runtime.exit() THIS IS NOT ALLOWED REROUTING TO FML!");
                            FMLLog.log.warn("Offender: {}.{}{}", ExitVisitor.this.clsName, mName, mDesc);
                            FMLLog.log.warn("Use FMLCommonHandler.exitJava instead");
                            FMLLog.log.warn("=============================================================");
                        }
                        opcode = Opcodes.INVOKESTATIC;
                        owner = ExitVisitor.callbackOwner;
                        name = "runtimeExitCalled";
                        desc = "(Ljava/lang/Runtime;I)V";
                        dirty = true;
                    }
                    else if (opcode == Opcodes.INVOKEVIRTUAL && owner.equals("java/lang/Runtime") && name.equals("halt") && desc.equals("(I)V"))
                    {
                        if (warn)
                        {
                            FMLLog.log.warn("=============================================================");
                            FMLLog.log.warn("MOD HAS DIRECT REFERENCE Runtime.halt() THIS IS NOT ALLOWED REROUTING TO FML!");
                            FMLLog.log.warn("Offendor: {}.{}{}", ExitVisitor.this.clsName, mName, mDesc);
                            FMLLog.log.warn("Use FMLCommonHandler.exitJava instead");
                            FMLLog.log.warn("=============================================================");
                        }
                        opcode = Opcodes.INVOKESTATIC;
                        owner = ExitVisitor.callbackOwner;
                        name = "runtimeHaltCalled";
                        desc = "(Ljava/lang/Runtime;I)V";
                        dirty = true;
                    }

                    super.visitMethodInsn(opcode, owner, name, desc, isIntf);
                }
            };
        }

        // Intercept System.exit, and check if the caller is allowed to use it, if not wrap it in a ExitTrappedException
        public static void systemExitCalled(int status)
        {
            ExitVisitor.checkAccess();
            System.exit(status);
        }
        // Intercept Runtime.getRuntime().exit, and check if the caller is allowed to use it, if not wrap it in a ExitTrappedException
        public static void runtimeExitCalled(Runtime runtime, int status)
        {
            ExitVisitor.checkAccess();
            runtime.exit(status);
        }

        // Intercept Runtime.getRuntime().halt, and check if the caller is allowed to use it, if not wrap it in a ExitTrappedException
        public static void runtimeHaltCalled(Runtime runtime, int status)
        {
            ExitVisitor.checkAccess();
            runtime.halt(status);
        }

        private static void checkAccess()
        {
            StackTraceElement[] cause = Thread.currentThread().getStackTrace();

            String callingClass = cause.length > 2 ? cause[3].getClassName() : "none";
            String callingParent = cause.length > 3 ? cause[4].getClassName() : "none";
            // FML is allowed to call system exit and the Minecraft applet (from the quit button), and the dedicated server (from itself)
            boolean allowed = false;
            allowed |= callingClass.startsWith("net.minecraftforge.fml.");
            allowed |= callingClass.equals("net.minecraft.client.Minecraft")                 && callingParent.equals("net.minecraft.client.Minecraft");
            allowed |= callingClass.equals("net.minecraft.server.gui.MinecraftServerGui$1")  && callingParent.equals("java.awt.AWTEventMulticaster");
            allowed |= callingClass.equals("net.minecraft.server.dedicated.DedicatedServer") && callingParent.equals("net.minecraft.server.MinecraftServer");
            allowed |= callingClass.equals("net.minecraft.server.dedicated.ServerHangWatchdog");
            allowed |= callingClass.equals("net.minecraft.server.dedicated.ServerHangWatchdog$1");

            if (!allowed)
            {
                throw new ExitTrappedException();
            }
        }
    }
}
