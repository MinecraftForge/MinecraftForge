package cpw.mods.fml.common.asm.transformers;

import org.objectweb.asm.*;

import cpw.mods.fml.relauncher.FMLRelaunchLog;
import cpw.mods.fml.relauncher.FMLSecurityManager.ExitTrappedException;
import net.minecraft.launchwrapper.IClassTransformer;

public class TerminalTransformer implements IClassTransformer
{
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
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
            super(Opcodes.ASM4, cv);
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
            final boolean warn = !clsName.equals("net/minecraft/client/Minecraft") &&
                                 !clsName.equals("net/minecraft/server/dedicated/DedicatedServer") &&
                                 !clsName.equals("cpw/mods/fml/common/FMLCommonHandler");

            return new MethodVisitor(Opcodes.ASM4, super.visitMethod(mAccess, mName, mDesc, mSignature, mExceptions))
            {
                public void visitMethodInsn(int opcode, String owner, String name, String desc)
                {
                    if (opcode == Opcodes.INVOKESTATIC && owner.equals("java/lang/System") && name.equals("exit") && desc.equals("(I)V"))
                    {
                        if (warn)
                        {
                            FMLRelaunchLog.warning("=============================================================");
                            FMLRelaunchLog.warning("MOD HAS DIRECT REFERENCE System.exit() THIS IS NOT ALLOWED REROUTING TO FMLCommonHandler!");
                            FMLRelaunchLog.warning("Offendor: %s.%s%s", ExitVisitor.this.clsName, mName, mDesc);
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
                            FMLRelaunchLog.warning("MOD HAS DIRECT REFERENCE Runtime.exit() THIS IS NOT ALLOWED REROUTING TO FMLCommonHandler!");
                            FMLRelaunchLog.warning("Offendor: %s.%s%s", ExitVisitor.this.clsName, mName, mDesc);
                            FMLRelaunchLog.warning("=============================================================");
                        }
                        opcode = Opcodes.INVOKESTATIC;
                        owner = ExitVisitor.callbackOwner;
                        name = "runtimeExitCalled";
                        desc = "(Ljava/lang/Runtime;I)V";
                    }

                    super.visitMethodInsn(opcode, owner, name, desc);
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

        private static void checkAccess()
        {
            StackTraceElement[] cause = Thread.currentThread().getStackTrace();

            String callingClass = cause.length > 2 ? cause[3].getClassName() : "none";
            String callingParent = cause.length > 3 ? cause[4].getClassName() : "none";
            // FML is allowed to call system exit and the Minecraft applet (from the quit button), and the dedicated server (from itself)
            if (!(callingClass.startsWith("cpw.mods.fml.") ||
                 ("net.minecraft.client.Minecraft".equals(callingClass) && "net.minecraft.client.Minecraft".equals(callingParent)) ||
                 ("net.minecraft.server.dedicated.DedicatedServer".equals(callingClass) && "net.minecraft.server.MinecraftServer".equals(callingParent)))
               )
            {
                throw new ExitTrappedException();
            }
        }
    }
}
