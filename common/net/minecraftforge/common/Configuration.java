/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraftforge.common;

import java.io.*;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;

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

    private Map<String,String> customCategoryComments = Maps.newHashMap();
    private boolean caseSensitiveCustomCategories;
    public static final String ALLOWED_CHARS = "._-";
    public static final String DEFAULT_ENCODING = "UTF-8";
    public String defaultEncoding = DEFAULT_ENCODING;

    private static final CharMatcher allowedProperties = CharMatcher.JAVA_LETTER_OR_DIGIT.or(CharMatcher.anyOf(ALLOWED_CHARS));

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

    public Configuration(File file, boolean caseSensitiveCustomCategories)
    {
        this(file);
        this.caseSensitiveCustomCategories = caseSensitiveCustomCategories;
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
            property.setName(key);

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
        if ("true".equals(prop.value.toLowerCase(Locale.ENGLISH)) || "false".equals(prop.value.toLowerCase(Locale.ENGLISH)))
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
        if (!caseSensitiveCustomCategories)
        {
            category = category.toLowerCase(Locale.ENGLISH);
        }
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
            property.setName(key);

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
                UnicodeInputStreamReader input = new UnicodeInputStreamReader(new FileInputStream(file), defaultEncoding);
                defaultEncoding = input.getEncoding();
                buffer = new BufferedReader(input);

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
                    boolean quoted = false;
                    for (int i = 0; i < line.length() && !skip; ++i)
                    {
                        if (Character.isLetterOrDigit(line.charAt(i)) || ALLOWED_CHARS.indexOf(line.charAt(i)) != -1 || (quoted && line.charAt(i) != '"'))
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

                                case '"':
                                    if (quoted)
                                    {
                                        quoted = false;
                                    }
                                    if (!quoted && nameStart == -1)
                                    {
                                        quoted = true;
                                    }
                                    break;

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
                                    prop.setName(propertyName);
                                    prop.value = line.substring(i + 1);
                                    i = line.length();

                                    currentMap.put(propertyName, prop);

                                    break;

                                default:
                                    throw new RuntimeException("unknown character " + line.charAt(i));
                            }
                        }
                    }
                    if (quoted)
                    {
                        throw new RuntimeException("unmatched quote");
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
                BufferedWriter buffer = new BufferedWriter(new OutputStreamWriter(fos, defaultEncoding));

                buffer.write("# Configuration file\r\n");
                buffer.write("# Generated on " + DateFormat.getInstance().format(new Date()) + "\r\n");
                buffer.write("\r\n");

                for(Map.Entry<String, Map<String, Property>> category : categories.entrySet())
                {
                    buffer.write("####################\r\n");
                    buffer.write("# " + category.getKey() + " \r\n");
                    if (customCategoryComments.containsKey(category.getKey()))
                    {
                        buffer.write("#===================\r\n");
                        String comment = customCategoryComments.get(category.getKey());
                        Splitter splitter = Splitter.onPattern("\r?\n");
                        for (String commentLine : splitter.split(comment))
                        {
                            buffer.write("# ");
                            buffer.write(commentLine+"\r\n");
                        }
                    }
                    buffer.write("####################\r\n\r\n");

                    String catKey = category.getKey();
                    if (!allowedProperties.matchesAllOf(catKey))
                    {
                    	catKey = '"'+catKey+'"';
                    }
                    buffer.write(catKey + " {\r\n");
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

    public void addCustomCategoryComment(String category, String comment)
    {
        if (!caseSensitiveCustomCategories)
            category = category.toLowerCase(Locale.ENGLISH);
        customCategoryComments.put(category, comment);
    }

    private void writeProperties(BufferedWriter buffer, Collection<Property> props) throws IOException
    {
        for (Property property : props)
        {
            if (property.comment != null)
            {
                Splitter splitter = Splitter.onPattern("\r?\n");
                for (String commentLine : splitter.split(property.comment))
                {
                    buffer.write("   # " + commentLine + "\r\n");
                }
            }
            String propName = property.getName();
            if (!allowedProperties.matchesAllOf(propName))
            {
            	propName = '"'+propName+'"';
            }
            buffer.write("   " + propName + "=" + property.value);
            buffer.write("\r\n");
        }
    }

    public static class UnicodeInputStreamReader extends Reader
    {
        private final InputStreamReader input;
        private final String defaultEnc;

        public UnicodeInputStreamReader(InputStream source, String encoding) throws IOException
        {
            defaultEnc = encoding;
            String enc = encoding;
            byte[] data = new byte[4];

            PushbackInputStream pbStream = new PushbackInputStream(source, data.length);
            int read = pbStream.read(data, 0, data.length);
            int size = 4;

            int bom16 = (data[0] & 0xFF) << 8 | (data[1] & 0xFF);
            int bom24 = bom16 << 8 | (data[2] & 0xFF);
            int bom32 = bom24 << 8 | (data[3] & 0xFF);

            if (bom24 == 0xEFBBBF)
            {
                enc = "UTF-8";
                size = 3;
            }
            else if (bom16 == 0xFEFF)
            {
                enc = "UTF-16BE";
                size = 2;
            }
            else if (bom16 == 0xFFFE)
            {
                enc = "UTF-16LE";
                size = 2;
            }
            else if (bom32 == 0x0000FEFF)
            {
                enc = "UTF-32BE";
            }
            else if (bom32 == 0xFFFE0000) //This will never happen as it'll be caught by UTF-16LE,
            {                             //but if anyone ever runs across a 32LE file, i'd like to disect it.
                enc = "UTF-32LE";
            }

            if (size < read)
            {
                pbStream.unread(data, size, read - size);
            }

            this.input = new InputStreamReader(pbStream, enc);
        }

        public String getEncoding()
        {
            return input.getEncoding();
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException
        {
            return input.read(cbuf, off, len);
        }

        @Override
        public void close() throws IOException
        {
            input.close();
        }
    }
}
