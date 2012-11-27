package net.minecraftforge.common;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.base.Splitter;

import static net.minecraftforge.common.Configuration.*;

public class ConfigCategory implements Map<String, Property>
{
    private String name;
    private String comment;
    private ArrayList<ConfigCategory> children = new ArrayList<ConfigCategory>();
    private Map<String, Property> properties = new TreeMap<String, Property>();
    public final ConfigCategory parent;

    public ConfigCategory(String name)
    {
        this(name, null);
    }

    public ConfigCategory(String name, ConfigCategory parent)
    {
        this.name = name;
        this.parent = parent;
        if (parent != null)
        {
            parent.children.add(this);
        }
    }

    public boolean equals(Object obj)
    {
        if (obj instanceof ConfigCategory)
        {
            ConfigCategory cat = (ConfigCategory)obj;
            return name.equals(cat.name) && children.equals(cat.children);  
        }
        
        return false;
    }

    public String getQualifiedName()
    {
        return getQualifiedName(name, parent);
    }

    public static String getQualifiedName(String name, ConfigCategory parent)
    {
        return (parent == null ? name : parent.getQualifiedName() + Configuration.CATEGORY_SPLITTER + name);
    }

    public ConfigCategory getFirstParent()
    {
        return (parent == null ? this : parent.getFirstParent());
    }

    public boolean isChild()
    {
        return parent != null;
    }

    public Map<String, Property> getValues()
    {
        return properties;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public boolean containsKey(String key)
    {
        return properties.containsKey(key);
    }

    public Property get(String key)
    {
        return properties.get(key);
    }

    public void set(String key, Property value)
    {
        properties.put(key, value);
    }

    public void write(BufferedWriter out, int indent) throws IOException
    {
        String pad = getIndent(indent);

        out.write(pad + "####################" + NEW_LINE);
        out.write(pad + "# " + name + NEW_LINE);

        if (comment != null)
        {
            out.write(pad + "#===================" + NEW_LINE);
            Splitter splitter = Splitter.onPattern("\r?\n");

            for (String line : splitter.split(comment))
            {
                out.write(pad + "# " + line + NEW_LINE);
            }
        }

        out.write(pad + "####################" + NEW_LINE + NEW_LINE);

        if (!allowedProperties.matchesAllOf(name))
        {
            name = '"' + name + '"';
        }

        out.write(pad + name + " {" + NEW_LINE);

        pad = getIndent(indent + 1);

        Property[] props = properties.values().toArray(new Property[properties.size()]);

        for (int x = 0; x < props.length; x++)
        {
            Property prop = props[x];

            if (prop.comment != null)
            {
                if (x != 0)
                {
                    out.newLine();
                }

                Splitter splitter = Splitter.onPattern("\r?\n");
                for (String commentLine : splitter.split(prop.comment))
                {
                    out.write(pad + "# " + commentLine + NEW_LINE);
                }
            }

            String propName = prop.getName();

            if (!allowedProperties.matchesAllOf(propName))
            {
                propName = '"' + propName + '"';
            }

            if (prop.isList())
            {
                out.write(String.format(pad + "%s:%s <" + NEW_LINE, prop.getType().getID(), propName));
                pad = getIndent(indent + 2);

                for (String line : prop.valueList)
                {
                    out.write(pad + line + NEW_LINE);
                }

                out.write(getIndent(indent + 1) + " >" + NEW_LINE);
            }
            else if (prop.getType() == null)
            {
                out.write(String.format(pad + "%s=%s" + NEW_LINE, propName, prop.value));
            }
            else
            {
                out.write(String.format(pad + "%s:%s=%s" + NEW_LINE, prop.getType().getID(), propName, prop.value));
            }
        }

        for (ConfigCategory child : children)
        {
            child.write(out, indent + 1);
        }

        out.write(getIndent(indent) + "}" + NEW_LINE + NEW_LINE);
    }

    private String getIndent(int indent)
    {
        StringBuilder buf = new StringBuilder("");
        for (int x = 0; x < indent; x++)
        {
            buf.append("    ");
        }
        return buf.toString();
    }


    //Map bouncer functions for compatibility with older mods, to be removed once all mods stop using it.
    @Override public int size(){ return properties.size(); }
    @Override public boolean isEmpty() { return properties.isEmpty(); }
    @Override public boolean containsKey(Object key) { return properties.containsKey(key); }
    @Override public boolean containsValue(Object value){ return properties.containsValue(value); }
    @Override public Property get(Object key) { return properties.get(key); }
    @Override public Property put(String key, Property value) { return properties.put(key, value); }
    @Override public Property remove(Object key) { return properties.remove(key); }
    @Override public void putAll(Map<? extends String, ? extends Property> m) { properties.putAll(m); }
    @Override public void clear() { properties.clear(); }
    @Override public Set<String> keySet() { return properties.keySet(); }
    @Override public Collection<Property> values() { return properties.values(); }
    @Override public Set<java.util.Map.Entry<String, Property>> entrySet() { return properties.entrySet(); }

}