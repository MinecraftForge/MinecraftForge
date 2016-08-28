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

import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;

import com.google.common.base.Throwables;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;


/**
 * Internal class used in tracking {@link ObjectHolder} references
 *
 * @author cpw
 *
 */
class ObjectHolderRef {
    private Field field;
    private ResourceLocation injectedObject;
    private boolean isValid;
    private IForgeRegistry registry;


    ObjectHolderRef(Field field, ResourceLocation injectedObject, boolean extractFromExistingValues)
    {
        Class<?> type = field.getType();
        while (IForgeRegistryEntry.class.isAssignableFrom(type) && registry == null) {
            registry = PersistentRegistryManager.findRegistryByType((Class<IForgeRegistryEntry>)type);
            type = type.getSuperclass();
        }

        this.field = field;
        this.isValid = registry != null;
        if (extractFromExistingValues)
        {
            try
            {
                Object existing = field.get(null);
                // nothing is ever allowed to replace AIR
                if (existing == null || existing == GameData.getBlockRegistry().getDefaultValue())
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
            throw new IllegalStateException(String.format("The ObjectHolder annotation cannot apply to a field that is not an Item or Block (found : %s at %s.%s)", field.getType().getName(), field.getClass().getName(), field.getName()));
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

    public boolean isValid()
    {
        return isValid;
    }
    public void apply()
    {
        Object thing;
        if (isValid && registry.containsKey(injectedObject))
        {
            thing = registry.getValue(injectedObject);
        }
        else
        {
            thing = null;
        }

        if (thing == null)
        {
            FMLLog.getLogger().log(Level.DEBUG, "Unable to lookup {} for {}. This means the object wasn't registered. It's likely just mod options.", injectedObject, field);
            return;
        }
        try
        {
            FinalFieldHelper.setField(field, null, thing);
        }
        catch (Exception e)
        {
            FMLLog.log(Level.WARN, e, "Unable to set %s with value %s (%s)", this.field, thing, this.injectedObject);
        }
    }
}
