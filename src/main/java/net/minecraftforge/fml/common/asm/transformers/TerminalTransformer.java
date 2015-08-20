package net.minecraftforge.fml.common.asm.transformers;

import org.objectweb.asm.*;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.FMLRelaunchLog;
import net.minecraftforge.fml.relauncher.FMLSecurityManager.ExitTrappedException;

public class TerminalTransformer implements IClassTransformer
{
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        if (basicClass == null) return null;
        ClassReader reader = new ClassReader(basicClass);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        ClassVisitor visitor = writer;
        visitor = new ExitVisitor(visitor);

        reader.accept(visitor, 0);
        return writer.toByteArray();
    }

    public static class ExitVisitor extends ClassVisitor
    {
        private String clsName = null;
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
                                   clsName.startsWith("net/minecraft/server/gui/MinecraftServerGui")
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
                            FMLRelaunchLog.warning("=============================================================");
                            FMLRelaunchLog.warning("MOD HAS DIRECT REFERENCE System.exit() THIS IS NOT ALLOWED REROUTING TO FML!");
                            FMLRelaunchLog.warning("Offendor: %s.%s%s", ExitVisitor.this.clsName, mName, mDesc);
                            FMLRelaunchLog.warning("Use FMLCommonHandler.exitJava instead");
                            FMLRelaunchLog.warning("=============================================================");
                        }
                        owner = ExitVisitor.callbackOwner;
                        name = "systemExitCalled";
                    }
                    else if (opcode == Opcodes.INVOKEVIRTUAL && owner.equals("java/lang/Runtime") && name.equals("exit") && desc.equals("(I)V"))
                    {
                        if (warn)
                        {
                            FMLRelaunchLog.warning("=============================================================");
                            FMLRelaunchLog.warning("MOD HAS DIRECT REFERENCE Runtime.exit() THIS IS NOT ALLOWED REROUTING TO FML!");
                            FMLRelaunchLog.warning("Offendor: %s.%s%s", ExitVisitor.this.clsName, mName, mDesc);
                            FMLRelaunchLog.warning("Use FMLCommonHandler.exitJava instead");
                            FMLRelaunchLog.warning("=============================================================");
                        }
                        opcode = Opcodes.INVOKESTATIC;
                        owner = ExitVisitor.callbackOwner;
                        name = "runtimeExitCalled";
                        desc = "(Ljava/lang/Runtime;I)V";
                    }
                    else if (opcode == Opcodes.INVOKEVIRTUAL && owner.equals("java/lang/Runtime") && name.equals("halt") && desc.equals("(I)V"))
                    {
                        if (warn)
                        {
                            FMLRelaunchLog.warning("=============================================================");
                            FMLRelaunchLog.warning("MOD HAS DIRECT REFERENCE Runtime.halt() THIS IS NOT ALLOWED REROUTING TO FML!");
                            FMLRelaunchLog.warning("Offendor: %s.%s%s", ExitVisitor.this.clsName, mName, mDesc);
                            FMLRelaunchLog.warning("Use FMLCommonHandler.exitJava instead");
                            FMLRelaunchLog.warning("=============================================================");
                        }
                        opcode = Opcodes.INVOKESTATIC;
                        owner = ExitVisitor.callbackOwner;
                        name = "runtimeHaltCalled";
                        desc = "(Ljava/lang/Runtime;I)V";
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
            if (!(callingClass.startsWith("net.minecraftforge.fml.") ||
                 ("net.minecraft.client.Minecraft".equals(callingClass) && "net.minecraft.client.Minecraft".equals(callingParent)) ||
                 ("net.minecraft.server.gui.MinecraftServerGui$1".equals(callingClass) && "java.awt.AWTEventMulticaster".equals(callingParent)) ||
                 ("net.minecraft.server.dedicated.DedicatedServer".equals(callingClass) && "net.minecraft.server.MinecraftServer".equals(callingParent)))
               )
            {
                throw new ExitTrappedException();
            }
        }
    }
}
