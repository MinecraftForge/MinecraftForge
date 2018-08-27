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

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.language.ModFileScanData;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.moddiscovery.ModAnnotation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.minecraftforge.fml.Logging.LOADING;

/**
 * Automatic eventbus subscriber - reads {@link net.minecraftforge.fml.common.Mod.EventBusSubscriber}
 * annotations and passes the class instances to the {@link net.minecraftforge.common.MinecraftForge.EVENT_BUS}
 */
public class AutomaticEventSubscriber
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static void inject(final ModContainer mod, final ModFileScanData scanData, final ClassLoader loader)
    {
        if (scanData == null) return;
        LOGGER.debug(LOADING,"Attempting to inject @EventBusSubscriber classes into the eventbus for {}", mod.getModId());
        List<ModFileScanData.AnnotationData> ebsTargets = scanData.getAnnotations().stream().
                filter(annotationData -> Objects.equals(annotationData.getAnnotationType(), Type.getType(Mod.EventBusSubscriber.class))).
                collect(Collectors.toList());

        ebsTargets.forEach(ad -> {
            @SuppressWarnings("unchecked")
            final List<ModAnnotation.EnumHolder> sidesValue = (List<ModAnnotation.EnumHolder>)ad.getAnnotationData().
                    getOrDefault("value", Arrays.asList(new ModAnnotation.EnumHolder(null, "CLIENT"), new ModAnnotation.EnumHolder(null, "DEDICATED_SERVER")));
            final EnumSet<Dist> sides = sidesValue.stream().map(eh -> Dist.valueOf(eh.getValue())).
                    collect(Collectors.toCollection(() -> EnumSet.noneOf(Dist.class)));
            final String modId = (String)ad.getAnnotationData().getOrDefault("modId", mod.getModId());
            if (Objects.equals(mod.getModId(), modId) && sides.contains(FMLEnvironment.dist)) {
                try
                {
                    MinecraftForge.EVENT_BUS.register(Class.forName(ad.getClassType().getClassName(), true, loader));
                }
                catch (ClassNotFoundException e)
                {
                    LOGGER.error(LOADING, "Failed to load mod class {} for @EventBusSubscriber annotation", ad.getClassType(), e);
                    throw new RuntimeException(e);
                }
            }
        });
    }

}
