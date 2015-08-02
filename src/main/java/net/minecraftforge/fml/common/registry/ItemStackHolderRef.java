package net.minecraftforge.fml.common.registry;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLLog;

import org.apache.logging.log4j.Level;

import com.google.common.base.Throwables;



/**
 * Internal class used in tracking {@link ItemStackHolder} references
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
        } catch (Exception e)
        {
            throw Throwables.propagate(e);
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
            FMLLog.getLogger().log(Level.ERROR, "Caught exception processing itemstack {},{},{} in annotation at {}.{}", itemName, meta, serializednbt,field.getClass().getName(),field.getName());
            throw e;
        }
        try
        {
            Object fieldAccessor = newFieldAccessor.invoke(reflectionFactory, field, false);
            fieldAccessorSet.invoke(fieldAccessor, null, is);
        }
        catch (Exception e)
        {
            FMLLog.getLogger().log(Level.WARN, "Unable to set {} with value {},{},{}", this.field, this.itemName, this.meta, this.serializednbt);
        }
    }
}
