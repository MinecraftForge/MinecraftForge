package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DataWatcher
{
    private static final HashMap dataTypes = new HashMap();
    private final Map watchedObjects = new HashMap();

    /** true if one or more object was changed */
    private boolean objectChanged;

    /**
     * adds a new object to dataWatcher to watch, to update an already existing object see updateObject. Arguments: data
     * Value Id, Object to add
     */
    public void addObject(int par1, Object par2Obj)
    {
        Integer var3 = (Integer)dataTypes.get(par2Obj.getClass());

        if (var3 == null)
        {
            throw new IllegalArgumentException("Unknown data type: " + par2Obj.getClass());
        }
        else if (par1 > 31)
        {
            throw new IllegalArgumentException("Data value id is too big with " + par1 + "! (Max is " + 31 + ")");
        }
        else if (this.watchedObjects.containsKey(Integer.valueOf(par1)))
        {
            throw new IllegalArgumentException("Duplicate id value for " + par1 + "!");
        }
        else
        {
            WatchableObject var4 = new WatchableObject(var3.intValue(), par1, par2Obj);
            this.watchedObjects.put(Integer.valueOf(par1), var4);
        }
    }

    /**
     * gets the bytevalue of a watchable object
     */
    public byte getWatchableObjectByte(int par1)
    {
        return ((Byte)((WatchableObject)this.watchedObjects.get(Integer.valueOf(par1))).getObject()).byteValue();
    }

    public short getWatchableObjectShort(int par1)
    {
        return ((Short)((WatchableObject)this.watchedObjects.get(Integer.valueOf(par1))).getObject()).shortValue();
    }

    public int getWatchableObjectInt(int par1)
    {
        return ((Integer)((WatchableObject)this.watchedObjects.get(Integer.valueOf(par1))).getObject()).intValue();
    }

    public String getWatchableObjectString(int par1)
    {
        return (String)((WatchableObject)this.watchedObjects.get(Integer.valueOf(par1))).getObject();
    }

    /**
     * updates an already existing object
     */
    public void updateObject(int par1, Object par2Obj)
    {
        WatchableObject var3 = (WatchableObject)this.watchedObjects.get(Integer.valueOf(par1));

        if (!par2Obj.equals(var3.getObject()))
        {
            var3.setObject(par2Obj);
            var3.setWatching(true);
            this.objectChanged = true;
        }
    }

    /**
     * writes every object in passed list to dataoutputstream, terminated by 0x7F
     */
    public static void writeObjectsInListToStream(List par0List, DataOutputStream par1DataOutputStream) throws IOException
    {
        if (par0List != null)
        {
            Iterator var2 = par0List.iterator();

            while (var2.hasNext())
            {
                WatchableObject var3 = (WatchableObject)var2.next();
                writeWatchableObject(par1DataOutputStream, var3);
            }
        }

        par1DataOutputStream.writeByte(127);
    }

    public void writeWatchableObjects(DataOutputStream par1DataOutputStream) throws IOException
    {
        Iterator var2 = this.watchedObjects.values().iterator();

        while (var2.hasNext())
        {
            WatchableObject var3 = (WatchableObject)var2.next();
            writeWatchableObject(par1DataOutputStream, var3);
        }

        par1DataOutputStream.writeByte(127);
    }

    private static void writeWatchableObject(DataOutputStream par0DataOutputStream, WatchableObject par1WatchableObject) throws IOException
    {
        int var2 = (par1WatchableObject.getObjectType() << 5 | par1WatchableObject.getDataValueId() & 31) & 255;
        par0DataOutputStream.writeByte(var2);

        switch (par1WatchableObject.getObjectType())
        {
            case 0:
                par0DataOutputStream.writeByte(((Byte)par1WatchableObject.getObject()).byteValue());
                break;

            case 1:
                par0DataOutputStream.writeShort(((Short)par1WatchableObject.getObject()).shortValue());
                break;

            case 2:
                par0DataOutputStream.writeInt(((Integer)par1WatchableObject.getObject()).intValue());
                break;

            case 3:
                par0DataOutputStream.writeFloat(((Float)par1WatchableObject.getObject()).floatValue());
                break;

            case 4:
                Packet.writeString((String)par1WatchableObject.getObject(), par0DataOutputStream);
                break;

            case 5:
                ItemStack var4 = (ItemStack)par1WatchableObject.getObject();
                par0DataOutputStream.writeShort(var4.getItem().shiftedIndex);
                par0DataOutputStream.writeByte(var4.stackSize);
                par0DataOutputStream.writeShort(var4.getItemDamage());
                break;

            case 6:
                ChunkCoordinates var3 = (ChunkCoordinates)par1WatchableObject.getObject();
                par0DataOutputStream.writeInt(var3.posX);
                par0DataOutputStream.writeInt(var3.posY);
                par0DataOutputStream.writeInt(var3.posZ);
        }
    }

    public static List readWatchableObjects(DataInputStream par0DataInputStream) throws IOException
    {
        ArrayList var1 = null;

        for (byte var2 = par0DataInputStream.readByte(); var2 != 127; var2 = par0DataInputStream.readByte())
        {
            if (var1 == null)
            {
                var1 = new ArrayList();
            }

            int var3 = (var2 & 224) >> 5;
            int var4 = var2 & 31;
            WatchableObject var5 = null;

            switch (var3)
            {
                case 0:
                    var5 = new WatchableObject(var3, var4, Byte.valueOf(par0DataInputStream.readByte()));
                    break;

                case 1:
                    var5 = new WatchableObject(var3, var4, Short.valueOf(par0DataInputStream.readShort()));
                    break;

                case 2:
                    var5 = new WatchableObject(var3, var4, Integer.valueOf(par0DataInputStream.readInt()));
                    break;

                case 3:
                    var5 = new WatchableObject(var3, var4, Float.valueOf(par0DataInputStream.readFloat()));
                    break;

                case 4:
                    var5 = new WatchableObject(var3, var4, Packet.readString(par0DataInputStream, 64));
                    break;

                case 5:
                    short var9 = par0DataInputStream.readShort();
                    byte var10 = par0DataInputStream.readByte();
                    short var11 = par0DataInputStream.readShort();
                    var5 = new WatchableObject(var3, var4, new ItemStack(var9, var10, var11));
                    break;

                case 6:
                    int var6 = par0DataInputStream.readInt();
                    int var7 = par0DataInputStream.readInt();
                    int var8 = par0DataInputStream.readInt();
                    var5 = new WatchableObject(var3, var4, new ChunkCoordinates(var6, var7, var8));
            }

            var1.add(var5);
        }

        return var1;
    }

    public void updateWatchedObjectsFromList(List par1List)
    {
        Iterator var2 = par1List.iterator();

        while (var2.hasNext())
        {
            WatchableObject var3 = (WatchableObject)var2.next();
            WatchableObject var4 = (WatchableObject)this.watchedObjects.get(Integer.valueOf(var3.getDataValueId()));

            if (var4 != null)
            {
                var4.setObject(var3.getObject());
            }
        }
    }

    static
    {
        dataTypes.put(Byte.class, Integer.valueOf(0));
        dataTypes.put(Short.class, Integer.valueOf(1));
        dataTypes.put(Integer.class, Integer.valueOf(2));
        dataTypes.put(Float.class, Integer.valueOf(3));
        dataTypes.put(String.class, Integer.valueOf(4));
        dataTypes.put(ItemStack.class, Integer.valueOf(5));
        dataTypes.put(ChunkCoordinates.class, Integer.valueOf(6));
    }
}
