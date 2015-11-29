package net.minecraftforge.fml.common.registry;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Internal registry for tracking {@link ObjectHolder} references
 * @author cpw
 *
 */
public enum ObjectHolderRegistry {
    INSTANCE;
    private List<ObjectHolderRef> objectHolders = Lists.newArrayList();

    public void findObjectHolders(ASMDataTable table)
    {
        FMLLog.info("Processing ObjectHolder annotations");
        Set<ASMData> allObjectHolders = table.getAll(GameRegistry.ObjectHolder.class.getName());
        Map<String, String> classModIds = Maps.newHashMap();
        Map<String, Class<?>> classCache = Maps.newHashMap();
        for (ASMData data : allObjectHolders)
        {
            String className = data.getClassName();
            String annotationTarget = data.getObjectName();
            String value = (String) data.getAnnotationInfo().get("value");
            boolean isClass = className.equals(annotationTarget);
            if (isClass)
            {
                scanTarget(classModIds, classCache, className, annotationTarget, value, isClass, false);
            }
        }
        // double pass - get all the class level annotations first, then the field level annotations
        for (ASMData data : allObjectHolders)
        {
            String className = data.getClassName();
            String annotationTarget = data.getObjectName();
            String value = (String) data.getAnnotationInfo().get("value");
            boolean isClass = className.equals(annotationTarget);
            if (!isClass)
            {
                scanTarget(classModIds, classCache, className, annotationTarget, value, isClass, false);
            }
        }
        scanTarget(classModIds, classCache, "net.minecraft.init.Blocks", null, "minecraft", true, true);
        scanTarget(classModIds, classCache, "net.minecraft.init.Items", null, "minecraft", true, true);
        FMLLog.info("Found %d ObjectHolder annotations", objectHolders.size());
    }

    private void scanTarget(Map<String, String> classModIds, Map<String, Class<?>> classCache, String className, String annotationTarget, String value, boolean isClass, boolean extractFromValue)
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
        if (isClass)
        {
            scanClassForFields(classModIds, className, value, clazz, extractFromValue);
        }
        else
        {
            if (value.indexOf(':') == -1)
            {
                String prefix = classModIds.get(className);
                if (prefix == null)
                {
                    FMLLog.warning("Found an unqualified ObjectHolder annotation (%s) without a modid context at %s.%s, ignoring", value, className, annotationTarget);
                    throw new IllegalStateException("Unqualified reference to ObjectHolder");
                }
                value = prefix + ":" + value;
            }
            try
            {
                Field f = clazz.getField(annotationTarget);
                addHolderReference(new ObjectHolderRef(f, new ResourceLocation(value), extractFromValue));
            }
            catch (Exception ex)
            {
                // unpossible?
                throw Throwables.propagate(ex);
            }
        }
    }

    private void scanClassForFields(Map<String, String> classModIds, String className, String value, Class<?> clazz, boolean extractFromExistingValues)
    {
        classModIds.put(className, value);
        for (Field f : clazz.getFields())
        {
            int mods = f.getModifiers();
            boolean isMatch = Modifier.isPublic(mods) && Modifier.isStatic(mods) && Modifier.isFinal(mods);
            if (!isMatch || f.isAnnotationPresent(ObjectHolder.class))
            {
                continue;
            }
            addHolderReference(new ObjectHolderRef(f, new ResourceLocation(value, f.getName()), extractFromExistingValues));
        }
    }

    private void addHolderReference(ObjectHolderRef ref)
    {
        if (ref.isValid())
        {
            objectHolders.add(ref);
        }
    }

    public void applyObjectHolders()
    {
        FMLLog.info("Applying holder lookups");
        for (ObjectHolderRef ohr : objectHolders)
        {
            ohr.apply();
        }
        FMLLog.info("Holder lookups applied");
    }

}
