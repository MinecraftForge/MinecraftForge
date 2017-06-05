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

package net.minecraftforge.fml.common.registry;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;

import com.google.common.base.Throwables;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.common.registry.PersistentRegistryManager.PersistentRegistry;

import javax.annotation.Nullable;


/**
 * Internal class used in tracking {@link ObjectHolder} references
 *
 * @author cpw
 *
 */
@SuppressWarnings("rawtypes")
class ObjectHolderRef {
    private Field field;
    private ResourceLocation injectedObject;
    private boolean isValid;
	private FMLControlledNamespacedRegistry registry;

    @SuppressWarnings("unchecked")
	ObjectHolderRef(Field field, ResourceLocation injectedObject, boolean extractFromExistingValues)
    {
        registry = getRegistryForType(field);

        this.field = field;
        this.isValid = registry != null;
        if (extractFromExistingValues)
        {
            try
            {
                Object existing = field.get(null);
                // nothing is ever allowed to replace AIR
                if (existing == null || existing == registry.getDefaultValue())
                {
                    this.injectedObject = null;
                    this.field = null;
                    this.isValid = false;
                    return;
                }
                else
                {
                    ResourceLocation tmp = null;
                    if (registry != null) {
                        tmp = registry.getKey((IForgeRegistryEntry) existing);
                    }
                    this.injectedObject = tmp;
                }
            } catch (Exception e)
            {
                throw Throwables.propagate(e);
            }
        }
        else
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
        }
        catch (Exception e)
        {
            throw Throwables.propagate(e);
        }
    }

	@SuppressWarnings("unchecked")
	@Nullable
	private FMLControlledNamespacedRegistry<?> getRegistryForType(Field field)
    {
        Queue<Class<?>> typesToExamine = new LinkedList<Class<?>>();
        typesToExamine.add(field.getType());
        FMLControlledNamespacedRegistry<?> registry = null;
        while (!typesToExamine.isEmpty() && registry == null)
        {
            Class<?> type = typesToExamine.remove();
            Collections.addAll(typesToExamine, type.getInterfaces());
            if (IForgeRegistryEntry.class.isAssignableFrom(type))
            {
                registry = PersistentRegistry.ACTIVE.getRegistry((Class<IForgeRegistryEntry>) type);
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
    public void apply()
    {
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
            FMLLog.log.debug("Unable to lookup {} for {}. This means the object wasn't registered. It's likely just mod options.", injectedObject, field);
            return;
        }
        try
        {
            FinalFieldHelper.setField(field, null, thing);
        }
        catch (Exception e)
        {
            FMLLog.log.warn("Unable to set {} with value {} ({})", this.field, thing, this.injectedObject, e);
        }
    }
}
