/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Predicate;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.ResourceLocationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("rawtypes")
class ObjectHolderRef implements Consumer<Predicate<ResourceLocation>>
{
    private static final Logger LOGGER  = LogManager.getLogger();
    private Field field;
    private ResourceLocation injectedObject;
    private boolean isValid;
    private ForgeRegistry<?> registry;

    @SuppressWarnings("unchecked")
    ObjectHolderRef(ResourceLocation registryName, Field field, String injectedObject, boolean extractFromExistingValues)
    {
        this.registry = RegistryManager.ACTIVE.getRegistry(registryName);
        this.field = field;
        this.isValid = registry != null && canHoldRegistryValue(registry, field);

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
                    this.injectedObject = ((ForgeRegistry) registry).getKey(existing);
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
            throw new IllegalStateException(String.format(Locale.ENGLISH, "The ObjectHolder annotation cannot apply to a field that does not map to a registry. Ensure the registry was created during the RegistryEvent.NewRegistry event. (found : %s at %s.%s)", field.getType().getName(), field.getDeclaringClass().getName(), field.getName()));
        }

        field.setAccessible(true);

        if (Modifier.isFinal(field.getModifiers()))
        {
            throw new RuntimeException("@ObjectHolder on final field, our transformer did not run? " + field.getDeclaringClass().getName() + "/" + field.getName());
        }
    }

    private static boolean canHoldRegistryValue(ForgeRegistry<?> registry, Field field)
    {
        return registry.getRegistrySuperType().isAssignableFrom(field.getType());
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
