/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.regex.Pattern;

import net.minecraftforge.fml.common.discovery.ModCandidate;
import net.minecraftforge.fml.common.discovery.asm.ASMModParser;
import net.minecraftforge.fml.common.discovery.asm.ModAnnotation;

import org.objectweb.asm.Type;

import com.google.common.collect.Maps;

import javax.annotation.Nullable;

public class ModContainerFactory
{
    public static Map<Type, Constructor<? extends ModContainer>> modTypes = Maps.newHashMap();
    private static ModContainerFactory INSTANCE = new ModContainerFactory();

    private ModContainerFactory() {
        // We always know about Mod type
        registerContainerType(Type.getType(Mod.class), FMLModContainer.class);
    }
    public static ModContainerFactory instance() {
        return INSTANCE;
    }

    public void registerContainerType(Type type, Class<? extends ModContainer> container)
    {
        try
        {
            Constructor<? extends ModContainer> constructor = container.getConstructor(new Class<?>[] { String.class, ModCandidate.class, Map.class });
            modTypes.put(type, constructor);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Critical error : cannot register mod container type " + container.getName() + ", it has an invalid constructor", e);
        }
    }

    @Nullable
    public ModContainer build(ASMModParser modParser, File modSource, ModCandidate container)
    {
        String className = modParser.getASMType().getClassName();
        for (ModAnnotation ann : modParser.getAnnotations())
        {
            if (modTypes.containsKey(ann.getASMType()))
            {
                FMLLog.log.debug("Identified a mod of type {} ({}) - loading", ann.getASMType(), className);
                try {
                    ModContainer ret = modTypes.get(ann.getASMType()).newInstance(className, container, ann.getValues());
                    if (!ret.shouldLoadInEnvironment())
                    {
                        FMLLog.log.debug("Skipping mod {}, container opted to not load.", className);
                        return null;
                    }
                    return ret;
                } catch (Exception e) {
                    FMLLog.log.error("Unable to construct {} container", ann.getASMType().getClassName(), e);
                    return null;
                }
            }
        }

        return null;
    }
}
