package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MapStorage
{
    private ISaveHandler saveHandler;

    /** Map of item data String id to loaded MapDataBases */
    private Map loadedDataMap = new HashMap();

    /** List of loaded MapDataBases. */
    private List loadedDataList = new ArrayList();

    /**
     * Map of MapDataBase id String prefixes ('map' etc) to max known unique Short id (the 0 part etc) for that prefix
     */
    private Map idCounts = new HashMap();

    public MapStorage(ISaveHandler par1ISaveHandler)
    {
        this.saveHandler = par1ISaveHandler;
        this.loadIdCounts();
    }

    /**
     * Loads an existing MapDataBase corresponding to the given String id from disk, instantiating the given Class, or
     * returns null if none such file exists. args: Class to instantiate, String dataid
     */
    public WorldSavedData loadData(Class par1Class, String par2Str)
    {
        WorldSavedData var3 = (WorldSavedData)this.loadedDataMap.get(par2Str);

        if (var3 != null)
        {
            return var3;
        }
        else
        {
            if (this.saveHandler != null)
            {
                try
                {
                    File var4 = this.saveHandler.getMapFileFromName(par2Str);

                    if (var4 != null && var4.exists())
                    {
                        try
                        {
                            var3 = (WorldSavedData)par1Class.getConstructor(new Class[] {String.class}).newInstance(new Object[] {par2Str});
                        }
                        catch (Exception var7)
                        {
                            throw new RuntimeException("Failed to instantiate " + par1Class.toString(), var7);
                        }

                        FileInputStream var5 = new FileInputStream(var4);
                        NBTTagCompound var6 = CompressedStreamTools.readCompressed(var5);
                        var5.close();
                        var3.readFromNBT(var6.getCompoundTag("data"));
                    }
                }
                catch (Exception var8)
                {
                    var8.printStackTrace();
                }
            }

            if (var3 != null)
            {
                this.loadedDataMap.put(par2Str, var3);
                this.loadedDataList.add(var3);
            }

            return var3;
        }
    }

    /**
     * Assigns the given String id to the given MapDataBase, removing any existing ones of the same id.
     */
    public void setData(String par1Str, WorldSavedData par2WorldSavedData)
    {
        if (par2WorldSavedData == null)
        {
            throw new RuntimeException("Can\'t set null data");
        }
        else
        {
            if (this.loadedDataMap.containsKey(par1Str))
            {
                this.loadedDataList.remove(this.loadedDataMap.remove(par1Str));
            }

            this.loadedDataMap.put(par1Str, par2WorldSavedData);
            this.loadedDataList.add(par2WorldSavedData);
        }
    }

    /**
     * Saves all dirty loaded MapDataBases to disk.
     */
    public void saveAllData()
    {
        for (int var1 = 0; var1 < this.loadedDataList.size(); ++var1)
        {
            WorldSavedData var2 = (WorldSavedData)this.loadedDataList.get(var1);

            if (var2.isDirty())
            {
                this.saveData(var2);
                var2.setDirty(false);
            }
        }
    }

    /**
     * Saves the given MapDataBase to disk.
     */
    private void saveData(WorldSavedData par1WorldSavedData)
    {
        if (this.saveHandler != null)
        {
            try
            {
                File var2 = this.saveHandler.getMapFileFromName(par1WorldSavedData.mapName);

                if (var2 != null)
                {
                    NBTTagCompound var3 = new NBTTagCompound();
                    par1WorldSavedData.writeToNBT(var3);
                    NBTTagCompound var4 = new NBTTagCompound();
                    var4.setCompoundTag("data", var3);
                    FileOutputStream var5 = new FileOutputStream(var2);
                    CompressedStreamTools.writeCompressed(var4, var5);
                    var5.close();
                }
            }
            catch (Exception var6)
            {
                var6.printStackTrace();
            }
        }
    }

    /**
     * Loads the idCounts Map from the 'idcounts' file.
     */
    private void loadIdCounts()
    {
        try
        {
            this.idCounts.clear();

            if (this.saveHandler == null)
            {
                return;
            }

            File var1 = this.saveHandler.getMapFileFromName("idcounts");

            if (var1 != null && var1.exists())
            {
                DataInputStream var2 = new DataInputStream(new FileInputStream(var1));
                NBTTagCompound var3 = CompressedStreamTools.read(var2);
                var2.close();
                Iterator var4 = var3.getTags().iterator();

                while (var4.hasNext())
                {
                    NBTBase var5 = (NBTBase)var4.next();

                    if (var5 instanceof NBTTagShort)
                    {
                        NBTTagShort var6 = (NBTTagShort)var5;
                        String var7 = var6.getName();
                        short var8 = var6.data;
                        this.idCounts.put(var7, Short.valueOf(var8));
                    }
                }
            }
        }
        catch (Exception var9)
        {
            var9.printStackTrace();
        }
    }

    /**
     * Returns an unique new data id for the given prefix and saves the idCounts map to the 'idcounts' file.
     */
    public int getUniqueDataId(String par1Str)
    {
        Short var2 = (Short)this.idCounts.get(par1Str);

        if (var2 == null)
        {
            var2 = Short.valueOf((short)0);
        }
        else
        {
            var2 = Short.valueOf((short)(var2.shortValue() + 1));
        }

        this.idCounts.put(par1Str, var2);

        if (this.saveHandler == null)
        {
            return var2.shortValue();
        }
        else
        {
            try
            {
                File var3 = this.saveHandler.getMapFileFromName("idcounts");

                if (var3 != null)
                {
                    NBTTagCompound var4 = new NBTTagCompound();
                    Iterator var5 = this.idCounts.keySet().iterator();

                    while (var5.hasNext())
                    {
                        String var6 = (String)var5.next();
                        short var7 = ((Short)this.idCounts.get(var6)).shortValue();
                        var4.setShort(var6, var7);
                    }

                    DataOutputStream var9 = new DataOutputStream(new FileOutputStream(var3));
                    CompressedStreamTools.write(var4, var9);
                    var9.close();
                }
            }
            catch (Exception var8)
            {
                var8.printStackTrace();
            }

            return var2.shortValue();
        }
    }
}
