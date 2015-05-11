/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
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
