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

package net.minecraftforge.fml.common.registry;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public enum ItemStackHolderInjector
{
    INSTANCE;

    private List<ItemStackHolderRef> itemStackHolders = Lists.newArrayList();

    public void inject() {
        FMLLog.log.info("Injecting itemstacks");
        for (ItemStackHolderRef ishr: itemStackHolders) {
            ishr.apply();
        }
        FMLLog.log.info("Itemstack injection complete");
    }

    public void findHolders(ASMDataTable table) {
        FMLLog.log.info("Identifying ItemStackHolder annotations");
        Set<ASMData> allItemStackHolders = table.getAll(GameRegistry.ItemStackHolder.class.getName());
        Map<String, Class<?>> classCache = Maps.newHashMap();
        for (ASMData data : allItemStackHolders)
        {
            String className = data.getClassName();
            String annotationTarget = data.getObjectName();
            String value = (String) data.getAnnotationInfo().get("value");
            int meta = data.getAnnotationInfo().containsKey("meta") ? (Integer) data.getAnnotationInfo().get("meta") : 0;
            String nbt = data.getAnnotationInfo().containsKey("nbt") ? (String) data.getAnnotationInfo().get("nbt") : "";
            addHolder(classCache, className, annotationTarget, value, meta, nbt);
        }
        FMLLog.log.info("Found {} ItemStackHolder annotations", allItemStackHolders.size());

    }

    private void addHolder(Map<String, Class<?>> classCache, String className, String annotationTarget, String value, Integer meta, String nbt)
    {
        Class<?> clazz;
        if (classCache.containsKey(className))
        {
            clazz = classCache.get(className);
        }
        else
        {
            try
            {
                clazz = Class.forName(className, true, getClass().getClassLoader());
                classCache.put(className, clazz);
            }
            catch (ClassNotFoundException ex)
            {
                // unpossible?
                throw new RuntimeException(ex);
            }
        }
        try
        {
            Field f = clazz.getField(annotationTarget);
            itemStackHolders.add(new ItemStackHolderRef(f, value, meta, nbt));
        }
        catch (NoSuchFieldException ex)
        {
            // unpossible?
            throw new RuntimeException(ex);
        }
    }
}
