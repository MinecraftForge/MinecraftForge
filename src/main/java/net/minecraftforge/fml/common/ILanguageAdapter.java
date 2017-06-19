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

package net.minecraftforge.fml.common;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraftforge.fml.relauncher.Side;

import org.apache.logging.log4j.Level;

public interface ILanguageAdapter {
    public Object getNewInstance(FMLModContainer container, Class<?> objectClass, ClassLoader classLoader, Method factoryMarkedAnnotation) throws Exception;
    public boolean supportsStatics();
    public void setProxy(Field target, Class<?> proxyTarget, Object proxy) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException;
    public void setInternalProxies(ModContainer mod, Side side, ClassLoader loader);

    public static class JavaAdapter implements ILanguageAdapter {
        @Override
        public Object getNewInstance(FMLModContainer container, Class<?> objectClass, ClassLoader classLoader, Method factoryMarkedMethod) throws Exception
        {
            if (factoryMarkedMethod != null)
            {
                return factoryMarkedMethod.invoke(null);
            }
            else
            {
                return objectClass.newInstance();
            }
        }

        @Override
        public boolean supportsStatics()
        {
            return true;
        }

        @Override
        public void setProxy(Field target, Class<?> proxyTarget, Object proxy) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException,
                SecurityException
        {
            target.set(null, proxy);
        }

        @Override
        public void setInternalProxies(ModContainer mod, Side side, ClassLoader loader)
        {
            // Nothing to do here.
        }
    }
}
