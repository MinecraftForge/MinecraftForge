package net.minecraftforge.event;

import static org.objectweb.asm.Opcodes.*;

import java.lang.reflect.Method;


import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;


public class ASMEventHandler implements IEventListener
{
    private static int IDs = 0;
    private static final String HANDLER_DESC = Type.getInternalName(IEventListener.class);
    private static final String HANDLER_FUNC_DESC = Type.getMethodDescriptor(IEventListener.class.getDeclaredMethods()[0]);    
    private static final ASMClassLoader LOADER = new ASMClassLoader();
    
    private final IEventListener handler;
    private final ForgeSubscribe subInfo;
    public ASMEventHandler(Object target, Method method) throws Exception
    {
        handler = (IEventListener)createWrapper(method).getConstructor(Object.class).newInstance(target);
        subInfo = method.getAnnotation(ForgeSubscribe.class);
    }

    @Override
    public void invoke(Event event)
    {
        if (handler != null)
        {
            if (!event.isCancelable() || !event.isCanceled() || subInfo.receiveCanceled())
            {
                handler.invoke(event);
            }
        }
    }
    
    public EventPriority getPriority()
    {
        return subInfo.priority();
    }
    
    public Class<?> createWrapper(Method callback)
    {
        ClassWriter cw = new ClassWriter(0);
        MethodVisitor mv;
        
        String name = getUniqueName(callback);
        String desc = name.replace('.',  '/');
        String instType = Type.getInternalName(callback.getDeclaringClass());
        String eventType = Type.getInternalName(callback.getParameterTypes()[0]);
        
        /*
        System.out.println("Name:     " + name);
        System.out.println("Desc:     " + desc);
        System.out.println("InstType: " + instType);
        System.out.println("Callback: " + callback.getName() + Type.getMethodDescriptor(callback));
        System.out.println("Event:    " + eventType);
        */
        
        cw.visit(V1_6, ACC_PUBLIC | ACC_SUPER, desc, null, "java/lang/Object", new String[]{ HANDLER_DESC });

        cw.visitSource(".dynamic", null);
        {
            cw.visitField(ACC_PUBLIC, "instance", "Ljava/lang/Object;", null, null).visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/Object;)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, desc, "instance", "Ljava/lang/Object;");
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "invoke", HANDLER_FUNC_DESC, null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, desc, "instance", "Ljava/lang/Object;");
            mv.visitTypeInsn(CHECKCAST, instType);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, eventType);
            mv.visitMethodInsn(INVOKEVIRTUAL, instType, callback.getName(), Type.getMethodDescriptor(callback));
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        cw.visitEnd();
        return LOADER.define(name, cw.toByteArray());
    }
    
    private String getUniqueName(Method callback)
    {
        return String.format("%s_%d_%s_%s_%s", getClass().getName(), IDs++, 
                callback.getDeclaringClass().getSimpleName(), 
                callback.getName(), 
                callback.getParameterTypes()[0].getSimpleName());
    }
    
    private static class ASMClassLoader extends ClassLoader
    {
        private ASMClassLoader()
        {
            super(ASMClassLoader.class.getClassLoader());
        }
        
        public Class<?> define(String name, byte[] data)
        {
            return defineClass(name, data, 0, data.length);
        }
    }

}
