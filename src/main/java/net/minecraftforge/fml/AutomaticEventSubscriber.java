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

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.moddiscovery.ModAnnotation;
import net.minecraftforge.forgespi.language.ModFileScanData;
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
 * annotations and passes the class instances to the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}
 */
public class AutomaticEventSubscriber
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Type AUTO_SUBSCRIBER = Type.getType(Mod.EventBusSubscriber.class);

    public static void inject(final ModContainer mod, final ModFileScanData scanData, final ClassLoader loader)
    {
        if (scanData == null) return;
        LOGGER.debug(LOADING,"Attempting to inject @EventBusSubscriber classes into the eventbus for {}", mod.getModId());
        List<ModFileScanData.AnnotationData> ebsTargets = scanData.getAnnotations().stream().
                filter(annotationData -> AUTO_SUBSCRIBER.equals(annotationData.getAnnotationType())).
                collect(Collectors.toList());

        ebsTargets.forEach(ad -> {
            final EnumSet<Dist> sides = get(ad, "values", Dist.class, Dist.values());
            final EnumSet<Mod.EventBusSubscriber.Target> buses = get(ad, "bus", Mod.EventBusSubscriber.Target.class, Mod.EventBusSubscriber.Target.values());
            final String modId = (String)ad.getAnnotationData().getOrDefault("modId", mod.getModId());
            if (Objects.equals(mod.getModId(), modId) && sides.contains(FMLEnvironment.dist)) {
                try
                {
                    Class<?> clazz = Class.forName(ad.getClassType().getClassName(), true, loader);
                    if (buses.contains(Mod.EventBusSubscriber.Target.MAIN_BUS))
                    {
                        MinecraftForge.EVENT_BUS.register(clazz);
                    }
                    if (buses.contains(Mod.EventBusSubscriber.Target.MOD_LOADING_BUS))
                    {
                        mod.getLoadingEventBus().register(clazz);
                    }
                }
                catch (ClassNotFoundException e)
                {
                    LOGGER.fatal(LOADING, "Failed to load mod class {} for @EventBusSubscriber annotation", ad.getClassType(), e);
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static<T extends Enum<T>> EnumSet<T> get(ModFileScanData.AnnotationData data, String field, Class<T> clazz, Enum<T>... allowedValues)
    {
        final List<ModAnnotation.EnumHolder> list = (List<ModAnnotation.EnumHolder>) data.getAnnotationData().getOrDefault(field, Arrays.stream(allowedValues).map(s -> new ModAnnotation.EnumHolder(null, s.name())).toArray());
        return list.stream().map(eh -> Enum.valueOf(clazz, eh.getValue())).collect(Collectors.toCollection(() -> EnumSet.noneOf(clazz)));
    }

}
