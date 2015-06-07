package net.minecraftforge.fml.common.registry;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;

import org.apache.logging.log4j.Level;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


public enum ItemStackHolderInjector
{
    INSTANCE;

    private List<ItemStackHolderRef> itemStackHolders = Lists.newArrayList();

    public void inject() {
        FMLLog.getLogger().log(Level.INFO, "Injecting itemstacks");
        for (ItemStackHolderRef ishr: itemStackHolders) {
            ishr.apply();
        }
        FMLLog.getLogger().log(Level.INFO, "Itemstack injection complete");
    }

    public void findHolders(ASMDataTable table) {
        FMLLog.info("Identifying ItemStackHolder annotations");
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
        FMLLog.info("Found %d ItemStackHolder annotations", allItemStackHolders.size());

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
            catch (Exception ex)
            {
                // unpossible?
                throw Throwables.propagate(ex);
            }
        }
        try
        {
            Field f = clazz.getField(annotationTarget);
            itemStackHolders.add(new ItemStackHolderRef(f, value, meta, nbt));
        }
        catch (Exception ex)
        {
            // unpossible?
            throw Throwables.propagate(ex);
        }
    }
}
