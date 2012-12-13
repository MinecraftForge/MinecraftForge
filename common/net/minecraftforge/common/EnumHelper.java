package net.minecraftforge.common;

import java.lang.reflect.*;
import java.util.*;

import net.minecraft.block.EnumMobType;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.EnumEntitySize;
import net.minecraft.entity.player.EnumStatus;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.util.EnumArt;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.gen.structure.EnumDoor;

public class EnumHelper
{
    private static Object reflectionFactory      = null;
    private static Method newConstructorAccessor = null;
    private static Method newInstance            = null;
    private static Method newFieldAccessor       = null;
    private static Method fieldAccessorSet       = null;
    private static boolean isSetup               = false;

    //Some enums are decompiled with extra arguments, so lets check for that
    private static Class[][] commonTypes =
    {
        {EnumAction.class},
        {EnumArmorMaterial.class, int.class, int[].class, int.class},
        {EnumArt.class, String.class, int.class, int.class, int.class, int.class},
        {EnumCreatureAttribute.class},
        {EnumCreatureType.class, Class.class, int.class, Material.class, boolean.class},
        {EnumDoor.class},
        {EnumEnchantmentType.class},
        {EnumEntitySize.class},
        {EnumMobType.class},
        {EnumMovingObjectType.class},
        {EnumSkyBlock.class, int.class},
        {EnumStatus.class},
        {EnumToolMaterial.class, int.class, int.class, float.class, int.class, int.class}
    }; 

    public static EnumAction addAction(String name)
    {
        return addEnum(EnumAction.class, name);
    }
    public static EnumArmorMaterial addArmorMaterial(String name, int durability, int[] reductionAmounts, int enchantability)
    {
        return addEnum(EnumArmorMaterial.class, name, durability, reductionAmounts, enchantability);
    }
    public static EnumArt addArt(String name, String tile, int sizeX, int sizeY, int offsetX, int offsetY)
    {
        return addEnum(EnumArt.class, name, tile, sizeX, sizeY, offsetX, offsetY);
    }
    public static EnumCreatureAttribute addCreatureAttribute(String name)
    {
        return addEnum(EnumCreatureAttribute.class, name);
    }
    public static EnumCreatureType addCreatureType(String name, Class typeClass, int maxNumber, Material material, boolean peaceful)
    {
        return addEnum(EnumCreatureType.class, name, typeClass, maxNumber, material, peaceful);
    }
    public static EnumDoor addDoor(String name)
    {
        return addEnum(EnumDoor.class, name);
    }
    public static EnumEnchantmentType addEnchantmentType(String name)
    {
        return addEnum(EnumEnchantmentType.class, name);
    }
    public static EnumEntitySize addEntitySize(String name)
    {
        return addEnum(EnumEntitySize.class, name);
    }
    public static EnumMobType addMobType(String name)
    {
        return addEnum(EnumMobType.class, name);
    }
    public static EnumMovingObjectType addMovingObjectType(String name)
    {
        if (!isSetup)
        {
            setup();
        }

        return addEnum(EnumMovingObjectType.class, name);
    }
    public static EnumSkyBlock addSkyBlock(String name, int lightValue)
    {
        return addEnum(EnumSkyBlock.class, name, lightValue);
    }
    public static EnumStatus addStatus(String name)
    {
        return addEnum(EnumStatus.class, name);
    }
    public static EnumToolMaterial addToolMaterial(String name, int harvestLevel, int maxUses, float efficiency, int damage, int enchantability)
    {
        return addEnum(EnumToolMaterial.class, name, harvestLevel, maxUses, efficiency, damage, enchantability);
    }

    private static void setup()
    {
        if (isSetup)
        {
            return;
        }

        try
        {
            Method getReflectionFactory = Class.forName("sun.reflect.ReflectionFactory").getDeclaredMethod("getReflectionFactory");
            reflectionFactory      = getReflectionFactory.invoke(null);
            newConstructorAccessor = Class.forName("sun.reflect.ReflectionFactory").getDeclaredMethod("newConstructorAccessor", Constructor.class);
            newInstance            = Class.forName("sun.reflect.ConstructorAccessor").getDeclaredMethod("newInstance", Object[].class);
            newFieldAccessor       = Class.forName("sun.reflect.ReflectionFactory").getDeclaredMethod("newFieldAccessor", Field.class, boolean.class);
            fieldAccessorSet       = Class.forName("sun.reflect.FieldAccessor").getDeclaredMethod("set", Object.class, Object.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        isSetup = true;
    }

    /*
     * Everything below this is found at the site below, and updated to be able to compile in Eclipse/Java 1.6+
     * Also modified for use in decompiled code.
     * Found at: http://niceideas.ch/roller2/badtrash/entry/java_create_enum_instances_dynamically
     */
    private static Object getConstructorAccessor(Class<?> enumClass, Class<?>[] additionalParameterTypes) throws Exception
    {
        Class<?>[] parameterTypes = new Class[additionalParameterTypes.length + 2];
        parameterTypes[0] = String.class;
        parameterTypes[1] = int.class;
        System.arraycopy(additionalParameterTypes, 0, parameterTypes, 2, additionalParameterTypes.length);
        return newConstructorAccessor.invoke(reflectionFactory, enumClass.getDeclaredConstructor(parameterTypes));
    }

    private static < T extends Enum<? >> T makeEnum(Class<T> enumClass, String value, int ordinal, Class<?>[] additionalTypes, Object[] additionalValues) throws Exception
    {
        Object[] parms = new Object[additionalValues.length + 2];
        parms[0] = value;
        parms[1] = Integer.valueOf(ordinal);
        System.arraycopy(additionalValues, 0, parms, 2, additionalValues.length);
        return enumClass.cast(newInstance.invoke(getConstructorAccessor(enumClass, additionalTypes), new Object[] {parms}));
    }

    public static void setFailsafeFieldValue(Field field, Object target, Object value) throws Exception
    {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        Object fieldAccessor = newFieldAccessor.invoke(reflectionFactory, field, false);
        fieldAccessorSet.invoke(fieldAccessor, target, value);
    }

    private static void blankField(Class<?> enumClass, String fieldName) throws Exception
    {
        for (Field field : Class.class.getDeclaredFields())
        {
            if (field.getName().contains(fieldName))
            {
                field.setAccessible(true);
                setFailsafeFieldValue(field, enumClass, null);
                break;
            }
        }
    }

    private static void cleanEnumCache(Class<?> enumClass) throws Exception
    {
        blankField(enumClass, "enumConstantDirectory");
        blankField(enumClass, "enumConstants");
    }

    public static <T extends Enum<? >> T addEnum(Class<T> enumType, String enumName, Object... paramValues)
    {
        return addEnum(commonTypes, enumType, enumName, paramValues);
    }
    
    public static <T extends Enum<? >> T addEnum(Class[][] map, Class<T> enumType, String enumName, Object... paramValues)
    {
        for (Class[] lookup : map)
        {
            if (lookup[0] == enumType)
            {
                Class<?>[] paramTypes = new Class<?>[lookup.length - 1];
                if (paramTypes.length > 0)
                {
                    System.arraycopy(lookup, 1, paramTypes, 0, paramTypes.length);
                }
                return addEnum(enumType, enumName, paramTypes, paramValues);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum<? >> T addEnum(Class<T> enumType, String enumName, Class<?>[] paramTypes, Object[] paramValues)
    {
        if (!isSetup)
        {
            setup();
        }

        Field valuesField = null;
        Field[] fields = enumType.getDeclaredFields();
        int flags = Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL | 0x1000 /*SYNTHETIC*/;
        String valueType = String.format("[L%s;", enumType.getName().replace('.', '/'));

        for (Field field : fields)
        {
            if ((field.getModifiers() & flags) == flags &&
                    field.getType().getName().replace('.', '/').equals(valueType)) //Apparently some JVMs return .'s and some don't..
            {
                valuesField = field;
                break;
            }
        }
        valuesField.setAccessible(true);

        try
        {
            T[] previousValues = (T[])valuesField.get(enumType);
            List<T> values = new ArrayList<T>(Arrays.asList(previousValues));
            T newValue = (T)makeEnum(enumType, enumName, values.size(), paramTypes, paramValues);
            values.add(newValue);
            setFailsafeFieldValue(valuesField, null, values.toArray((T[]) Array.newInstance(enumType, 0)));
            cleanEnumCache(enumType);

            return newValue;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    static
    {
        if (!isSetup)
        {
            setup();
        }
    }
}