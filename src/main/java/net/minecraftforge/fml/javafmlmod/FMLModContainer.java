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
package net.minecraftforge.fml.javafmlmod;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

import net.minecraftforge.fml.LifecycleEventProvider;
import net.minecraftforge.fml.loading.ModLoadingStage;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.language.ModContainer;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.language.IModInfo;
import net.minecraftforge.fml.language.ModFileScanData;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;
import java.util.stream.Collectors;

import org.objectweb.asm.Type;

import static net.minecraftforge.fml.Logging.LOADING;

public class FMLModContainer extends ModContainer
{
    private static final Logger LOGGER = LogManager.getLogger("FML");
    private final ModFileScanData scanResults;
    private Object modInstance;
    private final Class<?> modClass;

    public FMLModContainer(IModInfo info, String className, ClassLoader modClassLoader, ModFileScanData modFileScanResults)
    {
        super(info);
        this.scanResults = modFileScanResults;
        triggerMap.put(ModLoadingStage.BEGIN, this::constructMod);
        triggerMap.put(ModLoadingStage.PREINIT, this::preinitMod);

        try
        {
            modClass = Class.forName(className, true, modClassLoader);
            LOGGER.error(LOADING,"Loaded modclass {} with {}", modClass.getName(), modClass.getClassLoader());
        }
        catch (ClassNotFoundException e)
        {
            LOGGER.error(LOADING, "Failed to load class {}", className, e);
            throw new RuntimeException(e);
        }
    }

    private void preinitMod(LifecycleEventProvider.LifecycleEvent lifecycleEvent)
    {

        List<Pair<String, String>> instanceFields = scanResults.getAnnotations().stream().
                filter(a -> Objects.equals(a.getAnnotationType(), Type.getType(Instance.class)) &&
                        Objects.equals(a.getClassType(), Type.getType(modClass))).
                map(a->Pair.of(a.getMemberName(), (String)a.getAnnotationData().get("value"))).
                collect(Collectors.toList());

        instanceFields.forEach(p->{
            try
            {
                final Field field = modClass.getDeclaredField(p.getLeft());
                field.setAccessible(true);
                field.set(modInstance, FMLLoader.getModLoader().getModList().getModObjectById(p.getRight()).get());
            }
            catch (NoSuchFieldException | IllegalAccessException e)
            {
                LOGGER.error(LOADING, "Unable to set field {} to mod with ID {}", p.getLeft(), p.getRight(), e);
            }
        });

    }

    private void constructMod(LifecycleEventProvider.LifecycleEvent event)
    {
        try
        {
            LOGGER.debug(LOADING, "Loading mod instance {} of type {}", getModId(), modClass.getName());
            this.modInstance = modClass.newInstance();
            LOGGER.debug(LOADING, "Loaded mod instance {} of type {}", getModId(), modClass.getName());
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            LOGGER.error("Failed to create mod instance. ModID: {}, class {}", getModId(), modClass.getName(), e);
            throw new RuntimeException("Failed to create mod instance", e);
        }
    }

    @Override
    public boolean matches(Object mod)
    {
        return mod == modInstance;
    }

    @Override
    public Object getMod()
    {
        return modInstance;
    }

    @Override
    public <T> T getCustomExtension(final String name)
    {
        return ((Function<String,T>)modInstance).apply(name);
    }
}
