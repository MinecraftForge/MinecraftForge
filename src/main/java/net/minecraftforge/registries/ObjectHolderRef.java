/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Predicate;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.ResourceLocationException;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("rawtypes")
public class ObjectHolderRef implements Consumer<Predicate<ResourceLocation>>
{
    private static final Logger LOGGER  = LogManager.getLogger();
    private Field field;
    private ResourceLocation injectedObject;
    private boolean isValid;
    private ForgeRegistry<?> registry;

    public ObjectHolderRef(Field field, ResourceLocation injectedObject)
    {
        this(field, injectedObject.toString(), false);
    }

    ObjectHolderRef(Field field, String injectedObject, boolean extractFromExistingValues)
    {
        this.registry = getRegistryForType(field);
        this.field = field;
        this.isValid = registry != null;

        if (extractFromExistingValues)
        {
            try
            {
                Object existing = field.get(null);
                // nothing is ever allowed to replace AIR
                if (!isValid || (existing == null || existing == registry.getDefault()))
                {
                    this.injectedObject = null;
                    this.field = null;
                    this.isValid = false;
                    return;
                }
                else
                {
                    this.injectedObject = ((IForgeRegistryEntry)existing).getRegistryName();
                }
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
        }
        else
        {
            try
            {
                this.injectedObject = new ResourceLocation(injectedObject);
            }
            catch (ResourceLocationException e)
            {
                throw new IllegalArgumentException("Invalid @ObjectHolder annotation on \"" + field.toString() + "\"", e);
            }
        }

        if (this.injectedObject == null || !isValid())
        {
            throw new IllegalStateException(String.format("The ObjectHolder annotation cannot apply to a field that does not map to a registry. Ensure the registry was created during the RegistryEvent.NewRegistry event. (found : %s at %s.%s)", field.getType().getName(), field.getDeclaringClass().getName(), field.getName()));
        }

        field.setAccessible(true);

        if (Modifier.isFinal(field.getModifiers()))
        {
            throw new RuntimeException("@ObjectHolder on final field, our transformer did not run? " + field.getDeclaringClass().getName() + "/" + field.getName());
        }
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private ForgeRegistry<?> getRegistryForType(Field field)
    {
        Queue<Class<?>> typesToExamine = new LinkedList<Class<?>>();
        typesToExamine.add(field.getType());

        ForgeRegistry<?> registry = null;
        while (!typesToExamine.isEmpty() && registry == null)
        {
            Class<?> type = typesToExamine.remove();
            Collections.addAll(typesToExamine, type.getInterfaces());
            if (IForgeRegistryEntry.class.isAssignableFrom(type))
            {
                registry = (ForgeRegistry<?>)RegistryManager.ACTIVE.getRegistry((Class<IForgeRegistryEntry>)type);
                final Class<?> parentType = type.getSuperclass();
                if (parentType != null)
                {
                    typesToExamine.add(parentType);
                }
            }
        }
        return registry;
    }

    public boolean isValid()
    {
        return isValid;
    }

    @Override
    public void accept(Predicate<ResourceLocation> filter)
    {
        if (registry == null || !filter.test(registry.getRegistryName()))
            return;

        Object thing;
        if (isValid && registry.containsKey(injectedObject) && !registry.isDummied(injectedObject))
        {
            thing = registry.getValue(injectedObject);
        }
        else
        {
            thing = null;
        }

        if (thing == null)
        {
            LOGGER.debug("Unable to lookup {} for {}. This means the object wasn't registered. It's likely just mod options.", injectedObject, field);
            return;
        }
        try
        {
            field.set(null, thing);
        }
        catch (IllegalArgumentException | ReflectiveOperationException e)
        {
            LOGGER.warn("Unable to set {} with value {} ({})", this.field, thing, this.injectedObject, e);
        }
    }

    @Override
    public int hashCode()
    {
        return field.hashCode();
    }

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof ObjectHolderRef))
            return false;
        ObjectHolderRef o = (ObjectHolderRef)other;
        return this.field.equals(o.field);
    }
}
