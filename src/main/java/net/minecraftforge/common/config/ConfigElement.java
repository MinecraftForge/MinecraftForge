/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraftforge.common.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.GuiConfigEntries.IConfigEntry;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries.IArrayEntry;
import net.minecraftforge.fml.client.config.IConfigElement;

/**
 * This class bridges the gap between the FML config GUI classes and the Forge Configuration classes.
 */
public class ConfigElement implements IConfigElement
{
    private Property prop;
    private Property.Type type;
    private boolean isProperty;
    private ConfigCategory ctgy;
    private boolean categoriesFirst = true;

    public ConfigElement(ConfigCategory ctgy)
    {
        this.ctgy = ctgy;
        isProperty = false;
    }

    public ConfigElement(Property prop)
    {
        this.prop = prop;
        this.type = prop.getType();
        this.isProperty = true;
    }

    public ConfigElement listCategoriesFirst(boolean categoriesFirst)
    {
        this.categoriesFirst = categoriesFirst;
        return this;
    }

    @Override
    public List<IConfigElement> getChildElements()
    {
        if (!isProperty)
        {
            List<IConfigElement> elements = new ArrayList<IConfigElement>();
            Iterator<ConfigCategory> ccI = ctgy.getChildren().iterator();
            Iterator<Property> pI = ctgy.getOrderedValues().iterator();
            @SuppressWarnings("unused")
            int index = 0;

            if (categoriesFirst)
                while (ccI.hasNext())
                {
                    ConfigElement temp = new ConfigElement(ccI.next());
                    if (temp.showInGui()) // don't bother adding elements that shouldn't show
                        elements.add(temp);
                }

            while (pI.hasNext())
            {
                ConfigElement temp = new ConfigElement(pI.next());
                if (temp.showInGui())
                    elements.add(temp);
            }

            if (!categoriesFirst)
                while (ccI.hasNext())
                {
                    ConfigElement temp = new ConfigElement(ccI.next());
                    if (temp.showInGui())
                        elements.add(temp);
                }

            return elements;
        }
        return null;
    }

    @Override
    public String getName()
    {
        return isProperty ? prop.getName() : ctgy.getName();
    }

    @Override
    public boolean isProperty()
    {
        return isProperty;
    }

    @Override
    public Class<? extends IConfigEntry> getConfigEntryClass()
    {
        return isProperty ? prop.getConfigEntryClass() : ctgy.getConfigEntryClass();
    }

    @Override
    public Class<? extends IArrayEntry> getArrayEntryClass()
    {
        return isProperty ? prop.getArrayEntryClass() : null;
    }

    @Override
    public String getQualifiedName()
    {
        return isProperty ? prop.getName() : ctgy.getQualifiedName();
    }

    @Override
    public ConfigGuiType getType()
    {
        return isProperty ? getType(this.prop) : ConfigGuiType.CONFIG_CATEGORY;
    }

    public static ConfigGuiType getType(Property prop)
    {
        return prop.getType() == Property.Type.BOOLEAN ? ConfigGuiType.BOOLEAN : prop.getType() == Property.Type.DOUBLE ? ConfigGuiType.DOUBLE :
            prop.getType() == Property.Type.INTEGER ? ConfigGuiType.INTEGER : prop.getType() == Property.Type.COLOR ? ConfigGuiType.COLOR :
            prop.getType() == Property.Type.MOD_ID ? ConfigGuiType.MOD_ID : ConfigGuiType.STRING;
    }

    @Override
    public boolean isList()
    {
        return isProperty && prop.isList();
    }

    @Override
    public boolean isListLengthFixed()
    {
        return isProperty && prop.isListLengthFixed();
    }

    @Override
    public int getMaxListLength()
    {
        return isProperty ? prop.getMaxListLength() : -1;
    }

    @Override
    public String getComment()
    {
        return isProperty ? prop.comment : ctgy.getComment();
    }

    @Override
    public boolean isDefault()
    {
        return !isProperty || prop.isDefault();
    }

    @Override
    public void setToDefault()
    {
        if (isProperty)
            prop.setToDefault();
    }

    @Override
    public boolean requiresWorldRestart()
    {
        return isProperty ? prop.requiresWorldRestart() : ctgy.requiresWorldRestart();
    }

    @Override
    public boolean showInGui()
    {
        return isProperty ? prop.showInGui() : ctgy.showInGui();
    }

    @Override
    public boolean requiresMcRestart()
    {
        return isProperty ? prop.requiresMcRestart() : ctgy.requiresMcRestart();
    }

    @Override
    public String[] getValidValues()
    {
        return isProperty ? prop.getValidValues() : null;
    }

    @Override
    public String getLanguageKey()
    {
        return isProperty ? prop.getLanguageKey() : ctgy.getLanguagekey();
    }

    @Override
    public Object getDefault()
    {
        return isProperty ? prop.getDefault() : null;
    }

    @Override
    public Object[] getDefaults()
    {
        if (isProperty)
        {
            String[] aVal = prop.getDefaults();
            if (type == Property.Type.BOOLEAN)
            {
                Boolean[] ba = new Boolean[aVal.length];
                for(int i = 0; i < aVal.length; i++)
                    ba[i] = Boolean.valueOf(aVal[i]);
                return ba;
            }
            else if (type == Property.Type.DOUBLE)
            {
                Double[] da = new Double[aVal.length];
                for(int i = 0; i < aVal.length; i++)
                    da[i] = Double.valueOf(aVal[i].toString());
                return da;
            }
            else if (type == Property.Type.INTEGER)
            {
                Integer[] ia = new Integer[aVal.length];
                for(int i = 0; i < aVal.length; i++)
                    ia[i] = Integer.valueOf(aVal[i].toString());
                return ia;
            }
            else
                return aVal;
        }
        return null;
    }

    @Override
    public Pattern getValidationPattern()
    {
        return isProperty ? prop.getValidationPattern() : null;
    }

    @Override
    public Object get()
    {
        return isProperty ? prop.getString() : null;
    }

    @Override
    public Object[] getList()
    {
        if (isProperty)
        {
            String[] aVal = prop.getStringList();
            if (type == Property.Type.BOOLEAN)
            {
                Boolean[] ba = new Boolean[aVal.length];
                for(int i = 0; i < aVal.length; i++)
                    ba[i] = Boolean.valueOf(aVal[i]);
                return ba;
            }
            else if (type == Property.Type.DOUBLE)
            {
                Double[] da = new Double[aVal.length];
                for(int i = 0; i < aVal.length; i++)
                    da[i] = Double.valueOf(aVal[i].toString());
                return da;
            }
            else if (type == Property.Type.INTEGER)
            {
                Integer[] ia = new Integer[aVal.length];
                for(int i = 0; i < aVal.length; i++)
                    ia[i] = Integer.valueOf(aVal[i].toString());
                return ia;
            }
            else
                return aVal;
        }
        return null;
    }

    @Override
    public void set(Object value)
    {
        if (isProperty)
        {
            if (type == Property.Type.BOOLEAN)
                prop.set(Boolean.parseBoolean(value.toString()));
            else if (type == Property.Type.DOUBLE)
                prop.set(Double.parseDouble(value.toString()));
            else if (type == Property.Type.INTEGER)
                prop.set(Integer.parseInt(value.toString()));
            else
                prop.set(value.toString());
        }
    }

    @Override
    public void set(Object[] aVal)
    {
        if (isProperty)
        {
            if (type == Property.Type.BOOLEAN)
            {
                boolean[] ba = new boolean[aVal.length];
                for(int i = 0; i < aVal.length; i++)
                    ba[i] = Boolean.valueOf(aVal[i].toString());
                prop.set(ba);
            }
            else if (type == Property.Type.DOUBLE)
            {
                double[] da = new double[aVal.length];
                for(int i = 0; i < aVal.length; i++)
                    da[i] = Double.valueOf(aVal[i].toString());
                prop.set(da);
            }
            else if (type == Property.Type.INTEGER)
            {
                int[] ia = new int[aVal.length];
                for(int i = 0; i < aVal.length; i++)
                    ia[i] = Integer.valueOf(aVal[i].toString());
                prop.set(ia);
            }
            else
            {
                String[] is = new String[aVal.length];
                for(int i = 0; i < aVal.length; i++)
                    is[i] = aVal[i].toString();
                prop.set(is);
            }
        }
    }

    @Override
    public Object getMinValue()
    {
        return isProperty ? prop.getMinValue() : null;
    }

    @Override
    public Object getMaxValue()
    {
        return isProperty ? prop.getMaxValue() : null;
    }
}
