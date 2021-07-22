/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.fml.mclanguageprovider;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.forgespi.language.ILifecycleEvent;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.IModLanguageProvider;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.minecraftforge.fml.Logging.LOADING;

import net.minecraftforge.forgespi.language.IModLanguageProvider.IModLanguageLoader;

public class MinecraftModLanguageProvider implements IModLanguageProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    @Override
    public String name() {
        return "minecraft";
    }

    @Override
    public Consumer<ModFileScanData> getFileVisitor() {
        return (sd)->sd.addLanguageLoader(Map.of("minecraft", new MinecraftModTarget()));
    }

    @Override
    public <R extends ILifecycleEvent<R>> void consumeLifecycleEvent(final Supplier<R> consumeEvent) {

    }

    public static class MinecraftModTarget implements IModLanguageLoader {
        @SuppressWarnings("unchecked")
        @Override
        public <T> T loadMod(final IModInfo info, final ModFileScanData modFileScanResults, final ModuleLayer gameLayer) {
            try {
                var module = gameLayer.findModule("minecraft").orElseThrow();
                final Class<?> mcModClass = Class.forName(getClass().getModule(), "net.minecraftforge.fml.mclanguageprovider.MinecraftModContainer");
                return (T)mcModClass.getConstructor(IModInfo.class).newInstance(info);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                LOGGER.fatal(LOADING,"Unable to load MinecraftModContainer, wut?", e);
                throw new RuntimeException(e);
            }
        }
    }
}
