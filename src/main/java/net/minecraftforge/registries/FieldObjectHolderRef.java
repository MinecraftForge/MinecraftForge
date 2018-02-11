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

package net.minecraftforge.registries;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLLog;

/**
 * Default, field-based implementation for {@link IObjectHolderRef}.
 */
public class FieldObjectHolderRef implements IObjectHolderRef
{
    private Object instance;
    private Field field;
    private ResourceLocation injectedObject;
    private boolean isValid;
    private ForgeRegistry<?> registry;

    @SuppressWarnings("unchecked")
    FieldObjectHolderRef(@Nullable Object instance, Field field, ResourceLocation injectedObject, boolean extractFromExistingValues)
    {
        registry = IObjectHolderRef.getRegistryForType(field.getType());

        this.instance = instance;
        this.field = field;
        this.isValid = registry != null;
        if (extractFromExistingValues)
        {
            try
            {
                Object existing = field.get(null);
                // nothing is ever allowed to replace AIR
                if (existing == null || existing == registry.getDefault())
                {
                    this.injectedObject = null;
                    this.field = null;
                    this.isValid = false;
                    return;
                } else
                {
                    this.injectedObject = ((IForgeRegistryEntry) existing).getRegistryName();
                }
            } catch (IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
        } else
        {
            this.injectedObject = injectedObject;
        }

        if (this.injectedObject == null || !isValid())
        {
            throw new IllegalStateException(String.format("The ObjectHolder annotation cannot apply to a field that does not map to a registry. Ensure the registry was created during the RegistryEvent.NewRegistry event. (found : %s at %s.%s)", field.getType().getName(), field.getClass().getName(), field.getName()));
        }
        try
        {
            FinalFieldHelper.makeWritable(field);
        } catch (ReflectiveOperationException e)
        {
            throw new RuntimeException(e);
        }
    }

    public boolean isValid()
    {
        return isValid;
    }

    public void apply()
    {
        Object thing;
        if (isValid && registry.containsKey(injectedObject) && !registry.isDummied(injectedObject))
        {
            thing = registry.getValue(injectedObject);
        } else
        {
            thing = null;
        }

        if (thing == null)
        {
            FMLLog.log.debug("Unable to lookup {} for {}. This means the object wasn't registered. It's likely just mod options.", injectedObject, field);
            return;
        }
        try
        {
            FinalFieldHelper.setField(field, instance, thing);
        } catch (IllegalArgumentException | ReflectiveOperationException e)
        {
            FMLLog.log.warn("Unable to set {} with value {} ({})", this.field, thing, this.injectedObject, e);
        }
    }

    private static class FinalFieldHelper
    {
        private static Field modifiersField;
        private static Object reflectionFactory;
        private static Method newFieldAccessor;
        private static Method fieldAccessorSet;

        static Field makeWritable(Field f) throws ReflectiveOperationException
        {
            f.setAccessible(true);
            if (modifiersField == null)
            {
                Method getReflectionFactory = Class.forName("sun.reflect.ReflectionFactory").getDeclaredMethod("getReflectionFactory");
                reflectionFactory = getReflectionFactory.invoke(null);
                newFieldAccessor = Class.forName("sun.reflect.ReflectionFactory").getDeclaredMethod("newFieldAccessor", Field.class, boolean.class);
                fieldAccessorSet = Class.forName("sun.reflect.FieldAccessor").getDeclaredMethod("set", Object.class, Object.class);
                modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
            }
            modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
            return f;
        }

        static void setField(Field field, @Nullable Object instance, Object thing) throws ReflectiveOperationException
        {
            Object fieldAccessor = newFieldAccessor.invoke(reflectionFactory, field, false);
            fieldAccessorSet.invoke(fieldAccessor, instance, thing);
        }
    }
}
