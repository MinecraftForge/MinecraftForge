package net.minecraft.src.forge;

import net.minecraft.src.*;
import java.lang.reflect.*;
import java.util.*;

public class EnumHelper
{
    private static Object reflectionFactory      = null;
    private static Method newConstructorAccessor = null;
    private static Method newInstance            = null;
    private static Method newFieldAccessor       = null;
    private static Method fieldAccessorSet       = null;
    private static boolean isSetup               = false;

    //Some enums are decompiled with extra arguments, so lets check for that
    private static Class[][] ctrs =
    {
        {EnumAction.class},
        {EnumArmorMaterial.class, int.class, int[].class, int.class},
        {EnumArt.class, String.class, int.class, int.class, int.class, int.class},
        {EnumCreatureAttribute.class},
        {EnumCreatureType.class, Class.class, int.class, Material.class, boolean.class},
        {EnumDoor.class},
        {EnumEnchantmentType.class},
        {EnumMobType.class},
        {EnumMovingObjectType.class},
        {EnumSkyBlock.class, int.class},
        {EnumStatus.class},
        {EnumToolMaterial.class, int.class, int.class, float.class, int.class, int.class}
    };

    private static boolean[] decompiledFlags = new boolean[ctrs.length];

    public static EnumAction addAction(String name)
    {
        if (!isSetup)
        {
            setup();
        }

        return addEnum(decompiledFlags[0], EnumAction.class, name,
                new Class[] {},
                new Object[]{});
    }
    public static EnumArmorMaterial addArmorMaterial(String name, int durability, int[] reductionAmounts, int enchantability)
    {
        if (!isSetup)
        {
            setup();
        }

        return addEnum(decompiledFlags[1], EnumArmorMaterial.class, name,
                new Class[] { int.class,  int[].class,      int.class      },
                new Object[]{ durability, reductionAmounts, enchantability });
    }
    public static EnumArt addArt(String name, String tile, int sizeX, int sizeY, int offsetX, int offsetY)
    {
        if (!isSetup)
        {
            setup();
        }

        return addEnum(decompiledFlags[2], EnumArt.class, name,
                new Class[] {String.class, int.class, int.class, int.class, int.class},
                new Object[]{tile,         sizeX,     sizeY,     offsetX,   offsetY});
    }
    public static EnumCreatureAttribute addCreatureAttribute(String name)
    {
        if (!isSetup)
        {
            setup();
        }

        return addEnum(decompiledFlags[3], EnumCreatureAttribute.class, name,
                new Class[] {},
                new Object[]{});
    }
    public static EnumCreatureType addCreatureType(String name, Class typeClass, int maxNumber, Material material, boolean peaceful)
    {
        if (!isSetup)
        {
            setup();
        }

        return addEnum(decompiledFlags[4], EnumCreatureType.class, name,
                new Class[] {Class.class, int.class, Material.class, boolean.class},
                new Object[]{typeClass,   maxNumber, material,       peaceful});
    }
    public static EnumDoor addDoor(String name)
    {
        if (!isSetup)
        {
            setup();
        }

        return addEnum(decompiledFlags[5], EnumDoor.class, name,
                new Class[] {},
                new Object[]{});
    }
    public static EnumEnchantmentType addEnchantmentType(String name)
    {
        if (!isSetup)
        {
            setup();
        }

        return addEnum(decompiledFlags[6], EnumEnchantmentType.class, name,
                new Class[] {},
                new Object[]{});
    }
    public static EnumMobType addMobType(String name)
    {
        if (!isSetup)
        {
            setup();
        }

        return addEnum(decompiledFlags[7], EnumMobType.class, name,
                new Class[] {},
                new Object[]{});
    }
    public static EnumMovingObjectType addMovingObjectType(String name)
    {
        if (!isSetup)
        {
            setup();
        }

        return addEnum(decompiledFlags[8], EnumMovingObjectType.class, name,
                new Class[] {},
                new Object[]{});
    }
    public static EnumSkyBlock addSkyBlock(String name, int lightValue)
    {
        if (!isSetup)
        {
            setup();
        }

        return addEnum(decompiledFlags[9], EnumSkyBlock.class, name,
                new Class[] {int.class },
                new Object[]{lightValue});
    }
    public static EnumStatus addStatus(String name)
    {
        if (!isSetup)
        {
            setup();
        }

        return addEnum(decompiledFlags[10], EnumStatus.class, name,
                new Class[] {},
                new Object[]{});
    }
    public static EnumToolMaterial addToolMaterial(String name, int harvestLevel, int maxUses, float efficiency, int damage, int enchantability)
    {
        if (!isSetup)
        {
            setup();
        }

        return addEnum(decompiledFlags[11], EnumToolMaterial.class, name,
                new Class[] { int.class,    int.class, float.class,  int.class, int.class       },
                new Object[]{ harvestLevel, maxUses,   efficiency,   damage,    enchantability  });
    }

    private static void setup()
    {
        if (isSetup)
        {
            return;
        }

        //System.out.println("Enum Helper Initalizing: ");
        for (int x = 0; x < ctrs.length; x++)
        {
            try
            {
                Class<?>[] enumHeaders = new Class[ctrs[x].length + 3];
                enumHeaders[0] = String.class;
                enumHeaders[1] = int.class;
                enumHeaders[2] = String.class;
                enumHeaders[3] = int.class;

                for (int y = 1; y < ctrs[x].length; y++)
                {
                    enumHeaders[3 + y] = ctrs[x][y];
                }

                ctrs[x][0].getDeclaredConstructor(enumHeaders);
                decompiledFlags[x] = true;

            }
            catch (Exception e)
            {
                //Nom Nom Nom
            }
            //System.out.format("\t%-25s %s\r\n", ctrs[x][0].getName().replace("net.minecraft.src.", ""), decompiled[x]);
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

    private static Object getConstructorAccessor(boolean decompiled, Class<?> enumClass, Class<?>[] additionalParameterTypes) throws Exception
    {
        Class<?>[] parameterTypes = null;
        if (decompiled)
        {
            parameterTypes = new Class[additionalParameterTypes.length + 4];
            parameterTypes[0] = String.class;
            parameterTypes[1] = int.class;
            parameterTypes[2] = String.class;
            parameterTypes[3] = int.class;
            System.arraycopy(additionalParameterTypes, 0, parameterTypes, 4, additionalParameterTypes.length);
        }
        else
        {
            parameterTypes = new Class[additionalParameterTypes.length + 2];
            parameterTypes[0] = String.class;
            parameterTypes[1] = int.class;
            System.arraycopy(additionalParameterTypes, 0, parameterTypes, 2, additionalParameterTypes.length);
        }
        return newConstructorAccessor.invoke(reflectionFactory, enumClass.getDeclaredConstructor(parameterTypes));
    }

    private static < T extends Enum<? >> T makeEnum(boolean decompiled, Class<T> enumClass, String value, int ordinal, Class<?>[] additionalTypes, Object[] additionalValues) throws Exception
    {
        Object[] parms = null;
        if (decompiled)
        {
            parms = new Object[additionalValues.length + 4];
            parms[0] = value;
            parms[1] = Integer.valueOf(ordinal);
            parms[2] = value;
            parms[3] = Integer.valueOf(ordinal);
            System.arraycopy(additionalValues, 0, parms, 4, additionalValues.length);
        }
        else
        {
            parms = new Object[additionalValues.length + 2];
            parms[0] = value;
            parms[1] = Integer.valueOf(ordinal);
            System.arraycopy(additionalValues, 0, parms, 2, additionalValues.length);
        }
        return enumClass.cast(newInstance.invoke(getConstructorAccessor(decompiled, enumClass, additionalTypes), new Object[] {parms}));
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

    public static < T extends Enum<? >> T addEnum(Class<T> enumType, String enumName, boolean decompiled)
    {
        return addEnum(decompiled, enumType, enumName, new Class<?>[] {}, new Object[] {});
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum<? >> T addEnum(boolean decompiled, Class<T> enumType, String enumName, Class<?>[] paramTypes, Object[] paramValues)
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
            if (decompiled)
            {
                if (field.getName().contains("$VALUES"))
                {
                    valuesField = field;
                    break;
                }
            }
            else
            {
                if ((field.getModifiers() & flags) == flags &&
                        field.getType().getName().replace('.', '/').equals(valueType)) //Apparently some JVMs return .'s and some don't..
                {
                    valuesField = field;
                    break;
                }
            }
        }
        valuesField.setAccessible(true);

        try
        {
            T[] previousValues = (T[])valuesField.get(enumType);
            List<T> values = new ArrayList<T>(Arrays.asList(previousValues));
            T newValue = (T)makeEnum(decompiled, enumType, enumName, values.size(), paramTypes, paramValues);
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