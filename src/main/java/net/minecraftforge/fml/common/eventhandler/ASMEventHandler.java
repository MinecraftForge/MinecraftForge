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

package net.minecraftforge.fml.common.eventhandler;

import static org.objectweb.asm.Opcodes.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;

import net.minecraftforge.fml.common.ModContainer;

import org.apache.logging.log4j.ThreadContext;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import com.google.common.collect.Maps;

public class ASMEventHandler implements IEventListener
{
    private static int IDs = 0;
    private static final String HANDLER_DESC = Type.getInternalName(IEventListener.class);
    private static final String HANDLER_FUNC_DESC = Type.getMethodDescriptor(IEventListener.class.getDeclaredMethods()[0]);
    private static final ASMClassLoader LOADER = new ASMClassLoader();
    private static final HashMap<Method, Class<?>> cache = Maps.newHashMap();
    private static final boolean GETCONTEXT = Boolean.parseBoolean(System.getProperty("fml.LogContext", "false"));

    private final IEventListener handler;
    private final SubscribeEvent subInfo;
    private ModContainer owner;
    private String readable;
    private java.lang.reflect.Type filter = null;

    @Deprecated
    public ASMEventHandler(Object target, Method method, ModContainer owner) throws Exception
    {
        this(target, method, owner, false);
    }

    public ASMEventHandler(Object target, Method method, ModContainer owner, boolean isGeneric) throws Exception
    {
        this.owner = owner;
        if (Modifier.isStatic(method.getModifiers()))
            handler = (IEventListener)createWrapper(method).newInstance();
        else
            handler = (IEventListener)createWrapper(method).getConstructor(Object.class).newInstance(target);
        subInfo = method.getAnnotation(SubscribeEvent.class);
        readable = "ASM: " + target + " " + method.getName() + Type.getMethodDescriptor(method);
        if (isGeneric)
        {
            java.lang.reflect.Type type = method.getGenericParameterTypes()[0];
            if (type instanceof ParameterizedType)
            {
                filter = ((ParameterizedType)type).getActualTypeArguments()[0];
            }
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void invoke(Event event)
    {
        if (GETCONTEXT)
            ThreadContext.put("mod", owner == null ? "" : owner.getName());
        if (handler != null)
        {
            if (!event.isCancelable() || !event.isCanceled() || subInfo.receiveCanceled())
            {
                if (filter == null || filter == ((IGenericEvent)event).getGenericType())
                {
                    handler.invoke(event);
                }
            }
        }
        if (GETCONTEXT)
            ThreadContext.remove("mod");
    }

    public EventPriority getPriority()
    {
        return subInfo.priority();
    }

    public Class<?> createWrapper(Method callback)
    {
        if (cache.containsKey(callback))
        {
            return cache.get(callback);
        }

        ClassWriter cw = new ClassWriter(0);
        MethodVisitor mv;

        boolean isStatic = Modifier.isStatic(callback.getModifiers());
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
            if (!isStatic)
                cw.visitField(ACC_PUBLIC, "instance", "Ljava/lang/Object;", null, null).visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", isStatic ? "()V" : "(Ljava/lang/Object;)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            if (!isStatic)
            {
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitFieldInsn(PUTFIELD, desc, "instance", "Ljava/lang/Object;");
            }
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "invoke", HANDLER_FUNC_DESC, null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            if (!isStatic)
            {
                mv.visitFieldInsn(GETFIELD, desc, "instance", "Ljava/lang/Object;");
                mv.visitTypeInsn(CHECKCAST, instType);
            }
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, eventType);
            mv.visitMethodInsn(isStatic ? INVOKESTATIC : INVOKEVIRTUAL, instType, callback.getName(), Type.getMethodDescriptor(callback), false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        cw.visitEnd();
        Class<?> ret = LOADER.define(name, cw.toByteArray());
        cache.put(callback, ret);
        return ret;
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

    public String toString()
    {
        return readable;
    }
}
