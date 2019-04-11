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

package net.minecraftforge.fml.common;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.relauncher.Side;

import org.apache.logging.log4j.Level;

import com.google.common.base.Strings;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

import org.objectweb.asm.Type;

/**
 * @author cpw
 *
 */
public class ProxyInjector
{
    public static void inject(ModContainer mod, ASMDataTable data, Side side, ILanguageAdapter languageAdapter)
    {
        String containerModId = container.getModId();
        FMLLog.log.debug("Attempting to inject @SidedProxy classes into {}", containerModId);
        SetMultimap<String, ASMData> modData = table.getAnnotationsFor(container);
        Set<ASMData> mods = Sets.newHashSet();

        for(Type type : ModContainerFactory.modTypes.keySet()) {
            mods.addAll(modData.get(type.getClassName()));
        }

        Set<ASMData> targets = modData.get(SidedProxy.class.getName());
        ClassLoader classLoader = Loader.instance().getModClassLoader();

        for(ASMData target : targets) {
            try {
                String amodid = (String)target.getAnnotationInfo().get("modId");

                if(Strings.isNullOrEmpty(amodid)) {
                    amodid = ASMDataTable.getOwnerModID(mods, target);

                    if(Strings.isNullOrEmpty(amodid)) {
                        FMLLog.bigWarning("Could not determine owning mod for @SidedProxy on {} for mod {}", target.getClassName(), containerModId);
                        continue;
                    }
                }

                if (!containerModId.equals(amodid)) {
                    FMLLog.log.debug("Skipping proxy injection for {}.{} since it is not for mod {}", target.getClassName(), target.getObjectName(), containerModId);
                    continue;
                }

                Class<?> proxyTarget = Class.forName(target.getClassName(), true, classLoader);
                Field targetField = proxyTarget.getDeclaredField(target.getObjectName());

                if(targetField == null) {
                    FMLLog.log.fatal("Attempted to load a proxy type into {}.{} but the field was not found", target.getClassName(), target.getObjectName());
                    throw new LoaderException(String.format("Attempted to load a proxy type into %s.%s but the field was not found", target.getClassName(), target.getObjectName()));
                }

                targetField.setAccessible(true);
                SidedProxy annotation = targetField.getAnnotation(SidedProxy.class);
                String targetType = side.isClient() ? annotation.clientSide() : annotation.serverSide();

                if(targetType.equals("")) {
                    targetType = target.getClassName() + (side.isClient() ? "$ClientProxy" : "$ServerProxy");
                }

                Object proxy = Class.forName(targetType, true, classLoader).newInstance();

                if (adapter.supportsStatics() && (targetField.getModifiers() & Modifier.STATIC) == 0) {
                    FMLLog.log.fatal("Attempted to load a proxy type {} into {}.{}, but the field is not static", targetType, target.getClassName(), target.getObjectName());
                    throw new LoaderException(String.format("Attempted to load a proxy type %s into %s.%s, but the field is not static", targetType, target.getClassName(), target.getObjectName()));
                }

                if (!targetField.getType().isAssignableFrom(proxy.getClass())){
                    FMLLog.log.fatal("Attempted to load a proxy type {} into {}.{}, but the types don't match", targetType, target.getClassName(), target.getObjectName());
                    throw new LoaderException(String.format("Attempted to load a proxy type %s into %s.%s, but the types don't match", targetType, target.getClassName(), target.getObjectName()));
                }

                adapter.setProxy(targetField, proxyTarget, proxy);
            }
            catch(Exception e) {
                FMLLog.log.error("An error occurred trying to load a proxy into {}.{}", target.getObjectName(), e);
                throw new LoaderException(e);
            }
        }

        adapter.setInternalProxies(container, side, classLoader);
    }
}
