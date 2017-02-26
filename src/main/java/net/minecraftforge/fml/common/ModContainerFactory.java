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

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.regex.Pattern;

import net.minecraftforge.fml.common.discovery.ModCandidate;
import net.minecraftforge.fml.common.discovery.asm.ASMModParser;
import net.minecraftforge.fml.common.discovery.asm.ModAnnotation;

import org.apache.logging.log4j.Level;
import org.objectweb.asm.Type;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;

import javax.annotation.Nullable;

public class ModContainerFactory
{
    public static Map<Type, Constructor<? extends ModContainer>> modTypes = Maps.newHashMap();
    private static Pattern modClass = Pattern.compile(".*(\\.|)(mod\\_[^\\s$]+)$");
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
        try {
            Constructor<? extends ModContainer> constructor = container.getConstructor(new Class<?>[] { String.class, ModCandidate.class, Map.class });
            modTypes.put(type, constructor);
        } catch (Exception e) {
            FMLLog.log(Level.ERROR, e, "Critical error : cannot register mod container type %s, it has an invalid constructor");
            Throwables.propagate(e);
        }
    }

    @Nullable
    public ModContainer build(ASMModParser modParser, File modSource, ModCandidate container)
    {
        String className = modParser.getASMType().getClassName();
        if (modParser.isBaseMod(container.getRememberedBaseMods()) && modClass.matcher(className).find())
        {
            FMLLog.severe("Found a BaseMod type mod %s", className);
            FMLLog.severe("This will not be loaded and will be ignored. ModLoader mechanisms are no longer available.");
        }
        else if (modClass.matcher(className).find())
        {
            FMLLog.fine("Identified a class %s following modloader naming convention but not directly a BaseMod or currently seen subclass", className);
            container.rememberModCandidateType(modParser);
        }
        else if (modParser.isBaseMod(container.getRememberedBaseMods()))
        {
            FMLLog.fine("Found a basemod %s of non-standard naming format", className);
            container.rememberBaseModType(className);
        }

        for (ModAnnotation ann : modParser.getAnnotations())
        {
            if (modTypes.containsKey(ann.getASMType()))
            {
                FMLLog.fine("Identified a mod of type %s (%s) - loading", ann.getASMType(), className);
                try {
                    ModContainer ret = modTypes.get(ann.getASMType()).newInstance(className, container, ann.getValues());
                    if (!ret.shouldLoadInEnvironment())
                    {
                        FMLLog.fine("Skipping mod %s, container opted to not load.", className);
                        return null;
                    }
                    return ret;
                } catch (Exception e) {
                    FMLLog.log(Level.ERROR, e, "Unable to construct %s container", ann.getASMType().getClassName());
                    return null;
                }
            }
        }

        return null;
    }
}
