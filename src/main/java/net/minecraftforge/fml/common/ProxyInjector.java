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

/**
 * @author cpw
 *
 */
public class ProxyInjector
{
    public static void inject(ModContainer mod, ASMDataTable data, Side side, ILanguageAdapter languageAdapter)
    {
        FMLLog.log.debug("Attempting to inject @SidedProxy classes into {}", mod.getModId());
        SetMultimap<String, ASMData> modData = data.getAnnotationsFor(mod);
        Set<ASMData> mods = modData.get(Mod.class.getName());
        Set<ASMData> targets = modData.get(SidedProxy.class.getName());
        ClassLoader mcl = Loader.instance().getModClassLoader();

        for (ASMData targ : targets)
        {
            try
            {
                String amodid = (String)targ.getAnnotationInfo().get("modId");
                if (Strings.isNullOrEmpty(amodid))
                {
                    amodid = ASMDataTable.getOwnerModID(mods, targ);
                    if (Strings.isNullOrEmpty(amodid))
                    {
                        FMLLog.bigWarning("Could not determine owning mod for @SidedProxy on {} for mod {}", targ.getClassName(), mod.getModId());
                        continue;
                    }
                }
                if (!mod.getModId().equals(amodid))
                {
                    FMLLog.log.debug("Skipping proxy injection for {}.{} since it is not for mod {}", targ.getClassName(), targ.getObjectName(), mod.getModId());
                    continue;
                }

                Class<?> proxyTarget = Class.forName(targ.getClassName(), true, mcl);
                Field target = proxyTarget.getDeclaredField(targ.getObjectName());
                if (target == null)
                {
                    // Impossible?
                    FMLLog.log.fatal("Attempted to load a proxy type into {}.{} but the field was not found", targ.getClassName(), targ.getObjectName());
                    throw new LoaderException(String.format("Attempted to load a proxy type into %s.%s but the field was not found", targ.getClassName(), targ.getObjectName()));
                }
                target.setAccessible(true);

                SidedProxy annotation = target.getAnnotation(SidedProxy.class);
                String targetType = side.isClient() ? annotation.clientSide() : annotation.serverSide();
                if(targetType.equals(""))
                {
                    targetType = targ.getClassName() + (side.isClient() ? "$ClientProxy" : "$ServerProxy");
                }
                Object proxy=Class.forName(targetType, true, mcl).newInstance();

                if (languageAdapter.supportsStatics() && (target.getModifiers() & Modifier.STATIC) == 0 )
                {
                    FMLLog.log.fatal("Attempted to load a proxy type {} into {}.{}, but the field is not static", targetType, targ.getClassName(), targ.getObjectName());
                    throw new LoaderException(String.format("Attempted to load a proxy type %s into %s.%s, but the field is not static", targetType, targ.getClassName(), targ.getObjectName()));
                }
                if (!target.getType().isAssignableFrom(proxy.getClass()))
                {
                    FMLLog.log.fatal("Attempted to load a proxy type {} into {}.{}, but the types don't match", targetType, targ.getClassName(), targ.getObjectName());
                    throw new LoaderException(String.format("Attempted to load a proxy type %s into %s.%s, but the types don't match", targetType, targ.getClassName(), targ.getObjectName()));
                }
                languageAdapter.setProxy(target, proxyTarget, proxy);
            }
            catch (Exception e)
            {
                FMLLog.log.error("An error occurred trying to load a proxy into {}.{}", targ.getObjectName(), e);
                throw new LoaderException(e);
            }
        }

        // Allow language specific proxy injection.
        languageAdapter.setInternalProxies(mod, side, mcl);
    }
}
