package net.minecraftforge.common.util;

import java.lang.reflect.*;
import java.util.*;

import com.google.common.collect.Lists;

import net.minecraftforge.fml.common.EnhancedRuntimeException;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraft.block.BlockPressurePlate.Sensitivity;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityPainting.EnumArt;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.gen.structure.StructureStrongholdPieces.Stronghold.Door;
import net.minecraftforge.classloading.FMLForgePlugin;

public class EnumHelper
{
    private static Object reflectionFactory      = null;
    private static Method newConstructorAccessor = null;
    private static Method newInstance            = null;
    private static Method newFieldAccessor       = null;
    private static Method fieldAccessorSet       = null;
    private static boolean isSetup               = false;

    //Some enums are decompiled with extra arguments, so lets check for that
    private static Class<?>[][] commonTypes =
    {
        {EnumAction.class},
        {ArmorMaterial.class, String.class, int.class, int[].class, int.class, SoundEvent.class, float.class},
        {EnumArt.class, String.class, int.class, int.class, int.class, int.class},
        {EnumCreatureAttribute.class},
        {EnumCreatureType.class, Class.class, int.class, Material.class, boolean.class, boolean.class},
        {Door.class},
        {EnumEnchantmentType.class},
        {Sensitivity.class},
        {RayTraceResult.Type.class},
        {EnumSkyBlock.class, int.class},
        {SleepResult.class},
        {ToolMaterial.class, int.class, int.class, float.class, float.class, int.class},
        {EnumRarity.class, TextFormatting.class, String.class}
    };

    public static EnumAction addAction(String name)
    {
        return addEnum(EnumAction.class, name);
    }
    public static ArmorMaterial addArmorMaterial(String name, String textureName, int durability, int[] reductionAmounts, int enchantability, SoundEvent soundOnEquip, float toughness)
    {
        return addEnum(ArmorMaterial.class, name, textureName, durability, reductionAmounts, enchantability, soundOnEquip, toughness);
    }
    public static EnumArt addArt(String name, String tile, int sizeX, int sizeY, int offsetX, int offsetY)
    {
        return addEnum(EnumArt.class, name, tile, sizeX, sizeY, offsetX, offsetY);
    }
    public static EnumCreatureAttribute addCreatureAttribute(String name)
    {
        return addEnum(EnumCreatureAttribute.class, name);
    }
    public static EnumCreatureType addCreatureType(String name, Class<?> typeClass, int maxNumber, Material material, boolean peaceful, boolean animal)
    {
        return addEnum(EnumCreatureType.class, name, typeClass, maxNumber, material, peaceful, animal);
    }
    public static Door addDoor(String name)
    {
        return addEnum(Door.class, name);
    }
    public static EnumEnchantmentType addEnchantmentType(String name)
    {
        return addEnum(EnumEnchantmentType.class, name);
    }
    public static Sensitivity addSensitivity(String name)
    {
        return addEnum(Sensitivity.class, name);
    }
    public static RayTraceResult.Type addMovingObjectType(String name)
    {
        return addEnum(RayTraceResult.Type.class, name);
    }
    public static EnumSkyBlock addSkyBlock(String name, int lightValue)
    {
        return addEnum(EnumSkyBlock.class, name, lightValue);
    }
    public static SleepResult addStatus(String name)
    {
        return addEnum(SleepResult.class, name);
    }
    public static ToolMaterial addToolMaterial(String name, int harvestLevel, int maxUses, float efficiency, float damage, int enchantability)
    {
        return addEnum(ToolMaterial.class, name, harvestLevel, maxUses, efficiency, damage, enchantability);
    }
    public static EnumRarity addRarity(String name, TextFormatting color, String displayName)
    {
        return addEnum(EnumRarity.class, name, color, displayName);
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
        Object[] params = new Object[additionalValues.length + 2];
        params[0] = value;
        params[1] = ordinal;
        System.arraycopy(additionalValues, 0, params, 2, additionalValues.length);
        return enumClass.cast(newInstance.invoke(getConstructorAccessor(enumClass, additionalTypes), new Object[] {params}));
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

    private static <T extends Enum<? >> T addEnum(Class<T> enumType, String enumName, Object... paramValues)
    {
        setup();
        return addEnum(commonTypes, enumType, enumName, paramValues);
    }

    protected static <T extends Enum<? >> T addEnum(Class<?>[][] map, Class<T> enumType, String enumName, Object... paramValues)
    {
        for (Class<?>[] lookup : map)
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

    //Tests an enum is compatible with these args, throws an error if not.
    public static void testEnum(Class<? extends Enum<?>> enumType, Class<?>[] paramTypes)
    {
        addEnum(true, enumType, null, paramTypes, (Object[])null);
    }

    public static <T extends Enum<? >> T addEnum(Class<T> enumType, String enumName, Class<?>[] paramTypes, Object... paramValues)
    {
        return addEnum(false, enumType, enumName, paramTypes, paramValues);
    }

    @SuppressWarnings({ "unchecked", "serial" })
    private static <T extends Enum<? >> T addEnum(boolean test, final Class<T> enumType, String enumName, final Class<?>[] paramTypes, Object[] paramValues)
    {
        if (!isSetup)
        {
            setup();
        }

        Field valuesField = null;
        Field[] fields = enumType.getDeclaredFields();

        for (Field field : fields)
        {
            String name = field.getName();
            if (name.equals("$VALUES") || name.equals("ENUM$VALUES")) //Added 'ENUM$VALUES' because Eclipse's internal compiler doesn't follow standards
            {
                valuesField = field;
                break;
            }
        }

        int flags = (FMLForgePlugin.RUNTIME_DEOBF ? Modifier.PUBLIC : Modifier.PRIVATE) | Modifier.STATIC | Modifier.FINAL | 0x1000 /*SYNTHETIC*/;
        if (valuesField == null)
        {
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
        }

        if (valuesField == null)
        {
            final List<String> lines = Lists.newArrayList();
            lines.add(String.format("Could not find $VALUES field for enum: %s", enumType.getName()));
            lines.add(String.format("Runtime Deobf: %s", FMLForgePlugin.RUNTIME_DEOBF));
            lines.add(String.format("Flags: %s", String.format("%16s", Integer.toBinaryString(flags)).replace(' ', '0')));
            lines.add(              "Fields:");
            for (Field field : fields)
            {
                String mods = String.format("%16s", Integer.toBinaryString(field.getModifiers())).replace(' ', '0');
                lines.add(String.format("       %s %s: %s", mods, field.getName(), field.getType().getName()));
            }

            for (String line : lines)
                FMLLog.severe(line);

            if (test)
            {
                throw new EnhancedRuntimeException("Could not find $VALUES field for enum: " + enumType.getName())
                {
                    @Override
                    protected void printStackTrace(WrappedPrintStream stream)
                    {
                        for (String line : lines)
                            stream.println(line);
                    }
                };
            }
            return null;
        }

        if (test)
        {
            Object ctr = null;
            Exception ex = null;
            try
            {
                ctr = getConstructorAccessor(enumType, paramTypes);
            }
            catch (Exception e)
            {
                ex = e;
            }
            if (ctr == null || ex != null)
            {
                throw new EnhancedRuntimeException(String.format("Could not find constructor for Enum %s", enumType.getName()), ex)
                {
                    private String toString(Class<?>[] cls)
                    {
                        StringBuilder b = new StringBuilder();
                        for (int x = 0; x < cls.length; x++)
                        {
                            b.append(cls[x].getName());
                            if (x != cls.length - 1)
                                b.append(", ");
                        }
                        return b.toString();
                    }
                    @Override
                    protected void printStackTrace(WrappedPrintStream stream)
                    {
                        stream.println("Target Arguments:");
                        stream.println("    java.lang.String, int, " + toString(paramTypes));
                        stream.println("Found Constructors:");
                        for (Constructor<?> ctr : enumType.getDeclaredConstructors())
                        {
                            stream.println("    " + toString(ctr.getParameterTypes()));
                        }
                    }
                };
            }
            return null;
        }

        valuesField.setAccessible(true);

        try
        {
            T[] previousValues = (T[])valuesField.get(enumType);
            List<T> values = new ArrayList<T>(Arrays.asList(previousValues));
            T newValue = makeEnum(enumType, enumName, values.size(), paramTypes, paramValues);
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