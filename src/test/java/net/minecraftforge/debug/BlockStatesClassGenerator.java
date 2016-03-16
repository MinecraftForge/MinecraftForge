package net.minecraftforge.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

@SuppressWarnings("unused")
public class BlockStatesClassGenerator
{
    // List of property classes to be moved to the end of the field name
    private static final List<Class<?>> propertyNamePriority = Lists.<Class<?>>newArrayList(EnumFacing.class, Integer.class, Boolean.class);

    /**
     * Call this to automatically generate the package {@link net.minecraftforge.common.BlockStates} class code
     */
    @SuppressWarnings("unchecked")
    public static String createBlockStatesClassCode() throws IllegalAccessException
    {
        // These capture how to reference static fields/objects
        IdentityHashMap<Object, String> fieldNames = new IdentityHashMap<Object, String>();
        IdentityHashMap<Object, String> importNames = new IdentityHashMap<Object, String>();

        // Add all the public static fields from each block class
        HashSet<Class<?>> processedClasses = new HashSet<Class<?>>();
        for (Field field : Blocks.class.getFields())
        {
            Block b = (Block) field.get(null);
            Class<?> clazz = b.getClass();
            while (clazz != Object.class)
            {
                if (!processedClasses.add(clazz))
                    break;

                for (Field f2 : clazz.getFields())
                {
                    if (Modifier.isStatic(f2.getModifiers()) && Modifier.isPublic(f2.getModifiers()))
                    {
                        Object obj = f2.get(null);
                        Class<?> declaringClass = f2.getDeclaringClass();
                        fieldNames.put(obj, declaringClass.getSimpleName() + "." + f2.getName());
                        importNames.put(obj, declaringClass.getName());

                    }
                }

                clazz = clazz.getSuperclass();
            }
        }

        // Add some special cases (may need adding to in future versions)
        fieldNames.put(Blocks.red_flower.getTypeProperty(), "Blocks.red_flower.getTypeProperty()");
        importNames.put(Blocks.red_flower.getTypeProperty(), "net.minecraft.init.Blocks");
        fieldNames.put(Blocks.yellow_flower.getTypeProperty(), "Blocks.yellow_flower.getTypeProperty()");
        importNames.put(Blocks.yellow_flower.getTypeProperty(), "net.minecraft.init.Blocks");

        PropertyBool[] has_bottle = BlockBrewingStand.HAS_BOTTLE;
        for (int i = 0; i < has_bottle.length; i++)
        {
            PropertyBool property = has_bottle[i];
            fieldNames.put(property, "BlockBrewingStand.HAS_BOTTLE[" + i + "]");
            importNames.put(property, "net.minecraft.block.BlockBrewingStand");
        }

        // build code
        Set<Object> importedObjects = Sets.newIdentityHashSet();

        StringBuilder fieldListBuilder = new StringBuilder();
        StringBuilder fieldDeclarationBuilder = new StringBuilder("    static\n    {\n");
        for (Field field : Blocks.class.getFields())
        {
            final Block b = (Block) field.get(null);
            IBlockState defaultState = b.getDefaultState();

            // Some blocks have redundant states that for legacy. Get only states that are used in world.
            HashSet<IBlockState> stateSet = new HashSet<IBlockState>();
            for (IBlockState state : b.getBlockState().getValidStates())
            {
                stateSet.add(b.getStateFromMeta(b.getMetaFromState(state)));
            }

            List<IBlockState> validStates = new ArrayList<IBlockState>(stateSet);
            // Sort states in terms of their metadata
            Collections.sort(validStates, new Comparator<IBlockState>()
            {
                @Override
                public int compare(IBlockState o1, IBlockState o2)
                {
                    return b.getMetaFromState(o1) - b.getMetaFromState(o2);
                }
            });

            // Load properties and sort
            ArrayList<IProperty> properties = new ArrayList<IProperty>(b.getDefaultState().getPropertyNames());
            Collections.sort(properties, new Comparator<IProperty>()
            {
                @Override
                public int compare(IProperty o1, IProperty o2)
                {
                    int i = propertyNamePriority.indexOf(o1.getValueClass()) - propertyNamePriority.indexOf(o2.getValueClass());
                    if (i != 0)
                        return i;
                    return o1.getName().compareTo(o2.getName());
                }
            });

            // Remove properties that never change from the default state
            LinkedList<IProperty> significantProperties = new LinkedList<IProperty>(properties);
            mainLoop:
            for (Iterator<IProperty> iterator = significantProperties.iterator(); iterator.hasNext(); )
            {
                IProperty property = iterator.next();
                for (IBlockState state : validStates)
                {
                    if (state.getValue(property) != defaultState.getValue(property))
                    {
                        continue mainLoop;
                    }
                }

                iterator.remove();
            }

            for (IBlockState state : validStates)
            {
                fieldListBuilder.append("    public static final IBlockState ");

                String str = getNewFieldName(field, significantProperties, state);

                // Rename stupid names like dirt_dirt and stone_stone
                if (state == defaultState && str.equals(field.getName() + "_" + field.getName()))
                {
                    str = field.getName();
                }

                fieldListBuilder.append(str);
                fieldListBuilder.append(";\n");

                fieldDeclarationBuilder.append("        ");
                fieldDeclarationBuilder.append(str);
                fieldDeclarationBuilder.append(" = ");
                fieldDeclarationBuilder
                        .append(getBlockStateValueDefinition(fieldNames, importNames, importedObjects, field, defaultState, properties, state));
                fieldDeclarationBuilder.append(";\n");
            }
        }

        fieldDeclarationBuilder.append("    }\n");

        TreeSet<String> strings = new TreeSet<String>();

        strings.add("net.minecraft.block.state.IBlockState");
        strings.add("net.minecraft.block.*");

        for (Object o : importedObjects)
        {
            String e = importNames.get(o);
            if (e != null)
            {
                // We've already got net.minecraft.block.*
                if (e.startsWith("net.minecraft.block."))
                {
                    if (e.substring("net.minecraft.block.".length()).indexOf('.') < 0)
                    {
                        continue;
                    }
                }

                strings.add(e);
            }
            else
                throw new RuntimeException("Unable to import object " + o);
        }

        StringBuilder importBuilder = new StringBuilder();
        for (String s : strings)
        {
            importBuilder.append("import ").append(s).append(";\n");
        }

        @SuppressWarnings("StringBufferReplaceableByString")
        StringBuilder classCode = new StringBuilder("package net.minecraftforge.common;\n\n");

        classCode.append(importBuilder.toString());

        classCode.append("\n");
        classCode.append("@SuppressWarnings(\"unused\")\n");
        classCode.append("public class BlockStates {\n");
        classCode.append(fieldListBuilder.toString());
        classCode.append("\n\n");
        classCode.append(fieldDeclarationBuilder.toString());
        classCode.append("}");

        return classCode.toString();
    }

    /**
     * Get the field name for the block state.
     */
    @SuppressWarnings("unchecked")
    @Nonnull
    public static String getNewFieldName(Field field, LinkedList<IProperty> significantProperties, IBlockState state)
    {
        StringBuilder fieldBuilder = new StringBuilder(field.getName());

        for (IProperty property : significantProperties)
        {
            Comparable value = state.getValue(property);
            if (property.getValueClass() == Boolean.class)
            {
                if (value == Boolean.TRUE)
                    fieldBuilder.append("_").append(formatTextForField(property.getName()));
            }
            else if (property.getValueClass() == Integer.class)
            {
                fieldBuilder.append("_").append(formatTextForField(property.getName())).append("_").append(formatTextForField(property.getName(value)));
            }
            else
                fieldBuilder.append("_").append(formatTextForField(property.getName(value)));
        }

        return fieldBuilder.toString();
    }

    public static String formatTextForField(String name)
    {
        return name.toLowerCase().replaceAll("_", "");
    }

    @SuppressWarnings("unchecked")
    public static String getBlockStateValueDefinition(IdentityHashMap<Object, String> fieldNames, IdentityHashMap<Object, String> importNames,
            Set<Object> importedObjects, Field field, IBlockState defaultState, ArrayList<IProperty> properties, IBlockState state)
    {

        StringBuilder builder = new StringBuilder();
        builder.append("Blocks.").append(field.getName()).append(".getDefaultState()");
        for (IProperty property : properties)
        {
            Comparable value = state.getValue(property);
            if (defaultState.getValue(property).compareTo(value) == 0)
                continue;

            if (!fieldNames.containsKey(property))
            {
                throw new RuntimeException("Don't know how to handle property: " + property);
            }

            importedObjects.add(property);

            String s = fieldNames.get(property);

            builder.append(".withProperty(");
            builder.append(s);
            builder.append(", ");
            if (fieldNames.containsKey(value))
            {
                importedObjects.add(value);
                builder.append(fieldNames.get(value));
            }
            else
            {
                if (value instanceof Enum)
                {
                    Class<?> enumClazz = ((Enum) value).getDeclaringClass();
                    Class<?> parentClazz = enumClazz.getDeclaringClass();
                    if (parentClazz != null)
                    {
                        String s1 = parentClazz.getSimpleName() + "." + enumClazz.getSimpleName() + "." + ((Enum) value).name();
                        builder.append(s1);
                        fieldNames.put(value, s1);
                        importNames.put(value, parentClazz.getName());
                        importedObjects.add(value);
                    }
                    else
                    {
                        String s1 = enumClazz.getSimpleName() + "." + ((Enum) value).name();
                        builder.append(s1);
                        fieldNames.put(value, s1);
                        importNames.put(value, enumClazz.getName());
                        importedObjects.add(value);
                    }
                }
                else if (value.getClass() == Boolean.class || value.getClass() == Integer.class)
                {
                    builder.append(value);
                }
                else
                    throw new RuntimeException("Unable to handle " + value + " " + value.getClass());
            }
            builder.append(")");
        }

        return builder.toString();
    }
}
