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
import java.util.TreeMap;

import net.minecraft.src.Block;

public class Configuration {

    private boolean configBlocks[] = null;

    public enum PropertyKind {
        General, Block, Item
    }

    File file;

    public static class Property {
        public String name;
        public String value;
        public String comment;
    }

    public TreeMap<String, Property> blockProperties = new TreeMap<String, Property>();
    public TreeMap<String, Property> itemProperties = new TreeMap<String, Property>();
    public TreeMap<String, Property> generalProperties = new TreeMap<String, Property>();

    public Configuration(File file) {
        this.file = file;
    }

    public Property getOrCreateBlockIdProperty(String key, int defaultId) {
        if (configBlocks == null) {
            configBlocks = new boolean[Block.blocksList.length];

            for (int i = 0; i < configBlocks.length; ++i) {
                configBlocks[i] = false;
            }
        }

        if (blockProperties.containsKey(key)) {
            Property property = getOrCreateIntProperty(key, PropertyKind.Block,
                    defaultId);
            configBlocks[Integer.parseInt(property.value)] = true;
            return property;
        } else {
            Property property = new Property();

            blockProperties.put(key, property);
            property.name = key;

            if (Block.blocksList[defaultId] == null
                    && !configBlocks[defaultId]) {
                property.value = Integer.toString(defaultId);
                configBlocks[defaultId] = true;
                return property;
            } else {
                for (int j = Block.blocksList.length - 1; j >= 0; --j) {
                    if (Block.blocksList[j] == null && !configBlocks[j]) {
                        property.value = Integer.toString(j);
                        configBlocks[j] = true;
                        return property;
                    }
                }

                throw new RuntimeException("No more block ids available for "
                        + key);
            }
        }
    }

    public Property getOrCreateIntProperty(String key, PropertyKind kind,
            int defaultValue) {
        Property prop = getOrCreateProperty(key, kind,
                Integer.toString(defaultValue));

        try {
            Integer.parseInt(prop.value);

            return prop;
        } catch (NumberFormatException e) {
            prop.value = Integer.toString(defaultValue);
            return prop;
        }
    }

    public Property getOrCreateBooleanProperty(String key, PropertyKind kind,
            boolean defaultValue) {
        Property prop = getOrCreateProperty(key, kind,
                Boolean.toString(defaultValue));

        if ("true".equals(prop.value.toLowerCase())
                || "false".equals(prop.value.toLowerCase())) {
            return prop;
        } else {
            prop.value = Boolean.toString(defaultValue);
            return prop;
        }
    }

    public Property getOrCreateProperty(String key, PropertyKind kind,
            String defaultValue) {
        TreeMap<String, Property> source = null;

        switch (kind) {
        case General:
            source = generalProperties;
            break;
        case Block:
            source = blockProperties;
            break;
        case Item:
            source = itemProperties;
            break;
        }

        if (source.containsKey(key)) {
            return source.get(key);
        } else if (defaultValue != null) {
            Property property = new Property();

            source.put(key, property);
            property.name = key;

            property.value = defaultValue;
            return property;
        } else {
            return null;
        }
    }

    public void load() {
        try {
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }

            if (!file.exists() && !file.createNewFile()) {
                return;
            }

            if (file.canRead()) {
                FileInputStream fileinputstream = new FileInputStream(file);

                BufferedReader buffer = new BufferedReader(
                        new InputStreamReader(fileinputstream, "8859_1"));

                String line;
                TreeMap<String, Property> currentMap = null;

                while (true) {
                    line = buffer.readLine();

                    if (line == null) {
                        break;
                    }

                    int nameStart = -1, nameEnd = -1;
                    boolean skip = false;

                    for (int i = 0; i < line.length() && !skip; ++i) {
                        if (Character.isLetterOrDigit(line.charAt(i))
                                || line.charAt(i) == '.') {
                            if (nameStart == -1) {
                                nameStart = i;
                            }

                            nameEnd = i;
                        } else if (Character.isWhitespace(line.charAt(i))) {
                            // ignore space charaters
                        } else {
                            switch (line.charAt(i)) {
                            case '#':
                                skip = true;
                                continue;
                            case '{':
                                String scopeName = line.substring(nameStart,
                                        nameEnd + 1);

                                if (scopeName.equals("general")) {
                                    currentMap = generalProperties;
                                } else if (scopeName.equals("block")) {
                                    currentMap = blockProperties;
                                } else if (scopeName.equals("item")) {
                                    currentMap = itemProperties;
                                } else {
                                    throw new RuntimeException(
                                            "unknown section " + scopeName);
                                }

                                break;
                            case '}':
                                currentMap = null;
                                break;
                            case '=':
                                String propertyName = line.substring(nameStart,
                                        nameEnd + 1);

                                if (currentMap == null) {
                                    throw new RuntimeException("property "
                                            + propertyName + " has no scope");
                                }

                                Property prop = new Property();
                                prop.name = propertyName;
                                prop.value = line.substring(i + 1);
                                i = line.length();

                                currentMap.put(propertyName, prop);

                                break;
                            default:
                                throw new RuntimeException("unknown character "
                                        + line.charAt(i));
                            }
                        }
                    }
                }

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }

            if (!file.exists() && !file.createNewFile()) {
                return;
            }

            if (file.canWrite()) {
                FileOutputStream fileoutputstream = new FileOutputStream(file);

                BufferedWriter buffer = new BufferedWriter(
                        new OutputStreamWriter(fileoutputstream, "8859_1"));

                buffer.write("# Configuration file\r\n");
                buffer.write("# Generated on "
                        + DateFormat.getInstance().format(new Date()) + "\r\n");
                buffer.write("\r\n");
                buffer.write("###########\r\n");
                buffer.write("# General #\r\n");
                buffer.write("###########\r\n\r\n");

                buffer.write("general {\r\n");
                writeProperties(buffer, generalProperties.values());
                buffer.write("}\r\n\r\n");

                buffer.write("#########\r\n");
                buffer.write("# Block #\r\n");
                buffer.write("#########\r\n\r\n");

                buffer.write("block {\r\n");
                writeProperties(buffer, blockProperties.values());
                buffer.write("}\r\n\r\n");

                buffer.write("########\r\n");
                buffer.write("# Item #\r\n");
                buffer.write("########\r\n\r\n");

                buffer.write("item {\r\n");
                writeProperties(buffer, itemProperties.values());
                buffer.write("}\r\n\r\n");

                buffer.close();
                fileoutputstream.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void writeProperties(BufferedWriter buffer,
            Collection<Property> props) throws IOException {
        for (Property property : props) {
            if (property.comment != null) {
                buffer.write("   # " + property.comment + "\r\n");
            }

            buffer.write("   " + property.name + "=" + property.value);
            buffer.write("\r\n");
        }
    }

}
