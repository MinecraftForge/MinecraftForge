package net.minecraft.src.forge.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import net.minecraft.src.forge.*;

public class PacketConfig extends ForgePacket {
    public Configuration config;
    public String name;
    
    public PacketConfig()
    {
    	config = null;
    	name = null;
    }
    
    public PacketConfig(String name, Configuration config)
    {
        this.config = config;
        this.name = name;
    }
    
    @Override
    public void writeData(DataOutputStream data) throws IOException
    {
        data.writeUTF(name);
        
        for(Map.Entry<String, Map<String, Property>> cat_e : config.categories.entrySet())
        {
            data.writeUTF(cat_e.getKey());
            writeCategory(data, cat_e.getValue());
        }
        data.writeUTF("");
    }
    
    private void writeCategory(DataOutputStream out, Map<String, Property> data) throws IOException
    {
        for(Map.Entry<String, Property> e : data.entrySet())
        {
            out.writeUTF(e.getKey());
            Property p = e.getValue();
            out.writeUTF(p.comment != null ? p.comment : "");
            out.writeUTF(p.value);
        }
        out.writeUTF("");
    }
    
    private void readCategory(DataInputStream in, Map<String, Property> data) throws IOException
    {
        while(true)
        {
            String name = in.readUTF();
            if(name.equals(""))
                break;
            
            String comment = in.readUTF();
            if(comment.equals(""))
                comment = null;
            
            String value = in.readUTF();
            
            Property prop = new Property();
            prop.name = name;
            prop.comment = comment;
            prop.value = value;
            data.put(name, prop);
        }
    }

    @Override
    public void readData(DataInputStream data) throws IOException
    {
        name = data.readUTF();
        config = new Configuration(null);
        while(true)
        {
            String cat_name = data.readUTF();
            if(cat_name.equals(""))
                break;
            
            if(!config.categories.containsKey(cat_name))
                config.categories.put(cat_name, new TreeMap<String, Property>());
            
            readCategory(data, config.categories.get(cat_name));
        }
    }

    @Override
    public int getID()
    {
        return ForgePacket.CONFIG;
    }

    @Override
    public String toString(boolean full)
    {
        if (full)
        {
            return config.toString();
        }
        else
        {
            return toString();
        }
    }
    
}
