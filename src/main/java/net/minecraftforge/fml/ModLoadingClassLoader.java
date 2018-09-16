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

package net.minecraftforge.fml;

import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureClassLoader;

import static net.minecraftforge.fml.Logging.LOADING;

public class ModLoadingClassLoader extends SecureClassLoader
{

    private static final Logger LOGGER = LogManager.getLogger();

    static {
        ClassLoader.registerAsParallelCapable();
    }

    protected ModLoadingClassLoader(final ClassLoader parent) {
        super(parent);
    }

    @Override
    public URL getResource(String name)
    {
        return super.getResource(name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException
    {
        LOGGER.debug(LOADING, "Loading class {}", name);
        final String className = name.replace('.','/').concat(".class");
        final Path classResource = FMLLoader.getLoadingModList().findResource(className);
        if (classResource != null) {
            try {
                final byte[] bytes = Files.readAllBytes(classResource);
                return defineClass(name, bytes, 0, bytes.length);
            }
            catch (IOException e)
            {
                throw new ClassNotFoundException("Failed to load class file " + classResource + " for "+ className, e);
            }
        } else {
            getParent().loadClass(name);
        }
        throw new ClassNotFoundException("Failed to find class file "+ className);
    }

    @Override
    protected URL findResource(String name)
    {
        return super.findResource(name);
    }
}
