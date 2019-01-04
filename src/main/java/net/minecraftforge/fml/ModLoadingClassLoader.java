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

package net.minecraftforge.fml;

import cpw.mods.modlauncher.TransformingClassLoader;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.ModJarURLHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.function.Predicate;

import static net.minecraftforge.fml.Logging.LOADING;

public class ModLoadingClassLoader extends TransformingClassLoader
{
    private static final Logger LOGGER = LogManager.getLogger();

    static {
        ClassLoader.registerAsParallelCapable();
    }

    private final Predicate<String> classLoadingPredicate;
    private final TransformingClassLoader tcl;

    protected ModLoadingClassLoader(final TransformingClassLoader parent) {
        super(parent, path->FMLLoader.getLoadingModList().findURLForResource(path));
        this.tcl = parent;
        this.classLoadingPredicate = FMLLoader.getClassLoaderExclusions();
    }

    @Override
    protected URL locateResource(final String path) {
        return FMLLoader.getLoadingModList().findURLForResource(path);
    }

    @Override
    public URL getResource(String name)
    {
        return locateResource(name);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException
    {
        synchronized (getClassLoadingLock(name)) {
            Class<?> existingClass = tcl.getLoadedClass(name);
            if (existingClass != null) return existingClass;

            LOGGER.debug(LOADING, "Loading class {}", name);
            if (!classLoadingPredicate.test(name)) {
                LOGGER.debug(LOADING, "Delegating to parent {}", name);
                return tcl.loadClass(name);
            }
            return tcl.loadClass(name, this::locateResource);
        }
    }

    @Override
    protected URL findResource(String name)
    {
        return locateResource(name);
    }
}
