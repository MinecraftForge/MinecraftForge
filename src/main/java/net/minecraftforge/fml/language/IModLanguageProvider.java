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

package net.minecraftforge.fml.language;

import net.minecraftforge.fml.LifecycleEventProvider;

import java.util.function.Consumer;

/**
 * Loaded as a ServiceLoader, from the classpath. ExtensionPoint are loaded from
 * the mods directory, with the FMLType META-INF of LANGPROVIDER.
 *
 * Version data is read from the manifest's implementation version.
 */
public interface IModLanguageProvider
{
    String name();

    Consumer<ModFileScanData> getFileVisitor();

    void preLifecycleEvent(LifecycleEventProvider.LifecycleEvent lifecycleEvent);
    void postLifecycleEvent(LifecycleEventProvider.LifecycleEvent lifecycleEvent);

    interface IModLanguageLoader {
        <T> T loadMod(IModInfo info, ClassLoader modClassLoader, ModFileScanData modFileScanResults);
    }
}
