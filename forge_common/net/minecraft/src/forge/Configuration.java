/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraft.src.forge;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.src.Block;

/**
 * This class offers advanced configurations capabilities, allowing to provide
 * various categories for configuration variables.
 */
public class Configuration
{

    private boolean configBlocks[] = null;

    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_BLOCK   = "block";
    public static final String CATEGORY_ITEM    = "item";

    File file;
    
    public Map<String, Map<String, Property>> categories = new TreeMap<String, Map<String, Property>>();
    
    public TreeMap<String, Property> blockProperties   = new TreeMap<String, Property>();
    public TreeMap<String, Property> itemProperties    = new TreeMap<String, Property>();
    public TreeMap<String, Property> generalProperties = new TreeMap<String, Property>();

    /**
     * Create a configuration file for the file given in parameter.
     */
    public Configuration(File file)
    {
        this.file = file;
        categories.put(CATEGORY_GENERAL, generalProperties);
        categories.put(CATEGORY_BLOCK, blockProperties);
        categories.put(CATEGORY_ITEM, itemProperties);
    }

    /**
     * Gets or create a block id property. If the block id property key is
     * already in the configuration, then it will be used. Otherwise,
     * defaultId will be used, except if already taken, in which case this
     * will try to determine a free default id.
     */
    public Property getOrCreateBlockIdProperty(String key, int defaultId)
    {
        if (configBlocks == null)
        {
            configBlocks = new boolean[Block.blocksList.length];

            for (int i = 0; i < configBlocks.length; ++i)
            {
                configBlocks[i] = false;
            }
        }
        
        Map<String, Property> properties = categories.get(CATEGORY_BLOCK);
        if (properties.containsKey(key))
        {
            Property property = getOrCreateIntProperty(key, Configuration.CATEGORY_BLOCK, defaultId);
            configBlocks[Integer.parseInt(property.value)] = true;
            return property;
        }
        else
        {
            Property property = new Property();
            properties.put(key, property);
            property.name = key;

            if (Block.blocksList[defaultId] == null && !configBlocks[defaultId])
            {
                property.value = Integer.toString(defaultId);
                configBlocks[defaultId] = true;
                return property;
            }
            else
            {
                for (int j = configBlocks.length - 1; j >= 0; --j)
                {
                    if (Block.blocksList[j] == null && !configBlocks[j])
                    {
                        property.value = Integer.toString(j);
                        configBlocks[j] = true;
                        return property;
                    }
                }

                throw new RuntimeException("No more block ids available for " + key);
            }
        }
    }
    
    public Property getOrCreateIntProperty(String key, String category, int defaultValue)
    {
        Property prop = getOrCreateProperty(key, category, Integer.toString(defaultValue));
        try
        {
            Integer.parseInt(prop.value);
            return prop;
        }
        catch (NumberFormatException e)
        {
            prop.value = Integer.toString(defaultValue);
            return prop;
        }
    }
    
    public Property getOrCreateBooleanProperty(String key, String category, boolean defaultValue)
    {
        Property prop = getOrCreateProperty(key, category, Boolean.toString(defaultValue));
        if ("true".equals(prop.value.toLowerCase()) || "false".equals(prop.value.toLowerCase()))
        {
            return prop;
        }
        else
        {
            prop.value = Boolean.toString(defaultValue);
            return prop;
        }
    }
    
    public Property getOrCreateProperty(String key, String category, String defaultValue)
    {
        category = category.toLowerCase();
        Map<String, Property> source = categories.get(category);
        
        if(source == null)
        {
            source = new TreeMap<String, Property>();
            categories.put(category, source);                                  
        }

        if (source.containsKey(key))
        {
            return source.get(key);
        }
        else if (defaultValue != null)
        {
            Property property = new Property();

            source.put(key, property);
            property.name = key;

            property.value = defaultValue;
            return property;
        }
        else
        {
            return null;
        }
    }

    public void load()
    {
        BufferedReader buffer = null;
        try
        {
            if (file.getParentFile() != null)
            {
                file.getParentFile().mkdirs();
            }

            if (!file.exists() && !file.createNewFile())
            {
                return;
            }

            if (file.canRead())
            {
                FileInputStream fileinputstream = new FileInputStream(file);
                buffer = new BufferedReader(new InputStreamReader(fileinputstream, "8859_1"));

                String line;
                Map<String, Property> currentMap = null;

                while (true)
                {
                    line = buffer.readLine();

                    if (line == null)
                    {
                        break;
                    }

                    int nameStart = -1, nameEnd = -1;
                    boolean skip = false;

                    for (int i = 0; i < line.length() && !skip; ++i)
                    {
                        if (Character.isLetterOrDigit(line.charAt(i)) || line.charAt(i) == '.')
                        {
                            if (nameStart == -1)
                            {
                                nameStart = i;
                            }

                            nameEnd = i;
                        }
                        else if (Character.isWhitespace(line.charAt(i)))
                        {
                            // ignore space charaters
                        }
                        else
                        {
                            switch (line.charAt(i))
                            {
                                case '#':
                                    skip = true;
                                    continue;
                                case '{':
                                    String scopeName = line.substring(nameStart, nameEnd + 1);

                                    currentMap = categories.get(scopeName);
                                    if (currentMap == null)
                                    {
                                        currentMap = new TreeMap<String, Property>();
                                        categories.put(scopeName, currentMap);    
                                    }

                                    break;
                                case '}':
                                    currentMap = null;
                                    break;
                                case '=':
                                    String propertyName = line.substring(nameStart, nameEnd + 1);

                                    if (currentMap == null)
                                    {
                                        throw new RuntimeException("property " + propertyName + " has no scope");
                                    }

                                    Property prop = new Property();
                                    prop.name = propertyName;
                                    prop.value = line.substring(i + 1);
                                    i = line.length();

                                    currentMap.put(propertyName, prop);

                                    break;
                                default:
                                    throw new RuntimeException("unknown character " + line.charAt(i));
                            }
                        }
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (buffer != null)
            {
                try 
                {
                    buffer.close();
                } catch (IOException e){}
            }
        }
    }

    public void save()
    {
        try
        {
            if (file.getParentFile() != null)
            {
                file.getParentFile().mkdirs();
            }

            if (!file.exists() && !file.createNewFile())
            {
                return;
            }

            if (file.canWrite())
            {
                FileOutputStream fos = new FileOutputStream(file);
                BufferedWriter buffer = new BufferedWriter(new OutputStreamWriter(fos, "8859_1"));

                buffer.write("# Configuration file\r\n");
                buffer.write("# Generated on " + DateFormat.getInstance().format(new Date()) + "\r\n");
                buffer.write("\r\n");
                
                for(Map.Entry<String, Map<String, Property>> category : categories.entrySet())
                {
                    buffer.write("####################\r\n");
                    buffer.write("# " + category.getKey() + " \r\n");
                    buffer.write("####################\r\n\r\n");

                    buffer.write(category.getKey() + " {\r\n");
                    writeProperties(buffer, category.getValue().values());
                    buffer.write("}\r\n\r\n");
                }

                buffer.close();
                fos.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void writeProperties(BufferedWriter buffer, Collection<Property> props) throws IOException
    {
        for (Property property : props)
        {
            if (property.comment != null)
            {
                buffer.write("   # " + property.comment + "\r\n");
            }

            buffer.write("   " + property.name + "=" + property.value);
            buffer.write("\r\n");
        }
    }
}
