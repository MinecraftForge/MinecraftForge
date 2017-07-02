/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;

import net.minecraftforge.fml.common.ModContainer;

import org.apache.logging.log4j.ThreadContext;
import org.objectweb.asm.Type;

import com.google.common.collect.Maps;

public class LambdaEventHandler implements IEventListener
{
    private static final HashMap<Method, MethodHandle> cache = Maps.newHashMap();
    private static final boolean GETCONTEXT = Boolean.parseBoolean(System.getProperty("fml.LogContext", "false"));
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private final IEventListener handler;
    private final SubscribeEvent subInfo;
    private ModContainer owner;
    private String readable;
    private java.lang.reflect.Type filter = null;

    @Deprecated
    public LambdaEventHandler(Object target, Method method, ModContainer owner) throws Throwable
    {
        this(target, method, owner, false);
    }

    public LambdaEventHandler(Object target, Method method, ModContainer owner, boolean isGeneric) throws Throwable
    {
        this.owner = owner;
        handler = getHandler(target, method);
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

    private static IEventListener getHandler(Object target, Method callback) throws Throwable
    {
        boolean isStatic = Modifier.isStatic(callback.getModifiers());
        if (cache.containsKey(callback))
        {
            if (isStatic)
                return (IEventListener)cache.get(callback).invoke();
            else
                return (IEventListener)cache.get(callback).invoke(target);
        }
        else
        {
            MethodHandle constructorHandle;
            Method interfaceMethod = IEventListener.class.getDeclaredMethods()[0];
            MethodHandle callbackHandle = LOOKUP.unreflect(callback);

            MethodType callSiteSignature;
            if (isStatic)
                callSiteSignature = MethodType.methodType(IEventListener.class);
            else
                callSiteSignature = MethodType.methodType(IEventListener.class, target.getClass());

            constructorHandle = LambdaMetafactory.metafactory(
                    // Lookup used to determine context for classloading and access privileges (this class is the context)
                    LOOKUP,
                    // Name of the method to be implemented
                    interfaceMethod.getName(),
                    // Return type = interface being implemented, parameter types = constructor parameter types
                    callSiteSignature,
                    // Return type and parameters of the method being implemented
                    MethodType.methodType(interfaceMethod.getReturnType(), interfaceMethod.getParameterTypes()),
                    // DirectMethodHandle of the callback method
                    callbackHandle,
                    // Callback's return type and parameters
                    MethodType.methodType(callback.getReturnType(), callback.getParameterTypes())).getTarget();

            IEventListener newInstance;
            if (isStatic)
                newInstance = (IEventListener)constructorHandle.invoke();
            else
                newInstance = (IEventListener)constructorHandle.invoke(target);

            cache.put(callback, constructorHandle);
            return newInstance;
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

    public String toString()
    {
        return readable;
    }
}
