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

import com.google.common.base.Strings;
import com.google.common.collect.SetMultimap;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.discovery.asm.ModAnnotation;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Level;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Automatic eventbus subscriber - reads {@link net.minecraftforge.fml.common.Mod.EventBusSubscriber}
 * annotations and passes the class instances to the {@link net.minecraftforge.common.MinecraftForge.EVENT_BUS}
 */
public class AutomaticEventSubscriber
{
    private static final EnumSet<Side> DEFAULT = EnumSet.allOf(Side.class);
    public static void inject(ModContainer mod, ASMDataTable data, Side side)
    {
        FMLLog.log.debug("Attempting to inject @EventBusSubscriber classes into the eventbus for {}", mod.getModId());
        SetMultimap<String, ASMData> modData = data.getAnnotationsFor(mod);
        Set<ASMDataTable.ASMData> mods = modData.get(Mod.class.getName());
        Set<ASMDataTable.ASMData> targets = modData.get(Mod.EventBusSubscriber.class.getName());
        ClassLoader mcl = Loader.instance().getModClassLoader();

        for (ASMDataTable.ASMData targ : targets)
        {
            try
            {
                //noinspection unchecked
                @SuppressWarnings("unchecked")
                List<ModAnnotation.EnumHolder> sidesEnum = (List<ModAnnotation.EnumHolder>)targ.getAnnotationInfo().get("value");
                EnumSet<Side> sides = DEFAULT;
                if (sidesEnum != null) {
                    sides = EnumSet.noneOf(Side.class);
                    for (ModAnnotation.EnumHolder h: sidesEnum) {
                        sides.add(Side.valueOf(h.getValue()));
                    }
                }
                if (sides == DEFAULT || sides.contains(side)) {
                    //FMLLog.log.debug("Found @EventBusSubscriber class {}", targ.getClassName());
                    String amodid = (String)targ.getAnnotationInfo().get("modid");
                    if (Strings.isNullOrEmpty(amodid)) {
                        amodid = ASMDataTable.getOwnerModID(mods, targ);
                        if (Strings.isNullOrEmpty(amodid)) {
                            FMLLog.bigWarning("Could not determine owning mod for @EventBusSubscriber on {} for mod {}", targ.getClassName(), mod.getModId());
                            continue;
                        }
                    }
                    if (!mod.getModId().equals(amodid))
                    {
                        FMLLog.log.debug("Skipping @EventBusSubscriber injection for {} since it is not for mod {}", targ.getClassName(), mod.getModId());
                        continue; //We're not injecting this guy
                    }
                    FMLLog.log.debug("Registering @EventBusSubscriber for {} for mod {}", targ.getClassName(), mod.getModId());
                    Class<?> subscriptionTarget = Class.forName(targ.getClassName(), false, mcl);
                    MinecraftForge.EVENT_BUS.register(subscriptionTarget);
                    FMLLog.log.debug("Injected @EventBusSubscriber class {}", targ.getClassName());
                }
            }
            catch (Throwable e)
            {
                FMLLog.log.error("An error occurred trying to load an EventBusSubscriber {} for modid {}", targ.getClassName(), mod.getModId(), e);
                throw new LoaderException(e);
            }
        }
    }

}
