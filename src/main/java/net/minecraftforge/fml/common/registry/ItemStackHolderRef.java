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

package net.minecraftforge.fml.common.registry;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLLog;

/**
 * Internal class used in tracking {@link GameRegistry.ItemStackHolder} references
 *
 * @author cpw
 *
 */
class ItemStackHolderRef {
    private Field field;
    private String itemName;
    private int meta;
    private String serializednbt;


    ItemStackHolderRef(Field field, String itemName, int meta, String serializednbt)
    {
        this.field = field;
        this.itemName = itemName;
        this.meta = meta;
        this.serializednbt = serializednbt;
        makeWritable(field);
    }

    private static Field modifiersField;
    private static Object reflectionFactory;
    private static Method newFieldAccessor;
    private static Method fieldAccessorSet;
    private static void makeWritable(Field f)
    {
        try
        {
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
        }
        catch (ReflectiveOperationException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void apply()
    {
        ItemStack is;
        try
        {
            is = GameRegistry.makeItemStack(itemName, meta, 1, serializednbt);
        } catch (RuntimeException e)
        {
            FMLLog.log.error("Caught exception processing itemstack {},{},{} in annotation at {}.{}", itemName, meta, serializednbt,field.getClass().getName(),field.getName());
            throw e;
        }
        try
        {
            Object fieldAccessor = newFieldAccessor.invoke(reflectionFactory, field, false);
            fieldAccessorSet.invoke(fieldAccessor, null, is);
        }
        catch (Exception e)
        {
            FMLLog.log.warn("Unable to set {} with value {},{},{}", this.field, this.itemName, this.meta, this.serializednbt);
        }
    }
}
