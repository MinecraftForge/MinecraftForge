package net.minecraft.world.chunk.storage;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveFormatComparator;
import net.minecraft.world.storage.SaveFormatOld;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilSaveConverter extends SaveFormatOld
{
    private static final Logger field_151480_b = LogManager.getLogger();
    private static final String __OBFID = "CL_00000582";

    public AnvilSaveConverter(File par1File)
    {
        super(par1File);
    }

    @SideOnly(Side.CLIENT)
    public List getSaveList() throws AnvilConverterException
    {
        if (this.savesDirectory != null && this.savesDirectory.exists() && this.savesDirectory.isDirectory())
        {
            ArrayList arraylist = new ArrayList();
            File[] afile = this.savesDirectory.listFiles();
            File[] afile1 = afile;
            int i = afile.length;

            for (int j = 0; j < i; ++j)
            {
                File file1 = afile1[j];

                if (file1.isDirectory())
                {
                    String s = file1.getName();
                    WorldInfo worldinfo = this.getWorldInfo(s);

                    if (worldinfo != null && (worldinfo.getSaveVersion() == 19132 || worldinfo.getSaveVersion() == 19133))
                    {
                        boolean flag = worldinfo.getSaveVersion() != this.getSaveVersion();
                        String s1 = worldinfo.getWorldName();

                        if (s1 == null || MathHelper.stringNullOrLengthZero(s1))
                        {
                            s1 = s;
                        }

                        long k = 0L;
                        arraylist.add(new SaveFormatComparator(s, s1, worldinfo.getLastTimePlayed(), k, worldinfo.getGameType(), flag, worldinfo.isHardcoreModeEnabled(), worldinfo.areCommandsAllowed()));
                    }
                }
            }

            return arraylist;
        }
        else
        {
            throw new AnvilConverterException("Unable to read or access folder where game worlds are saved!");
        }
    }

    protected int getSaveVersion()
    {
        return 19133;
    }

    public void flushCache()
    {
        RegionFileCache.clearRegionFileReferences();
    }

    // JAVADOC METHOD $$ func_75804_a
    public ISaveHandler getSaveLoader(String par1Str, boolean par2)
    {
        return new AnvilSaveHandler(this.savesDirectory, par1Str, par2);
    }

    // JAVADOC METHOD $$ func_75801_b
    public boolean isOldMapFormat(String par1Str)
    {
        WorldInfo worldinfo = this.getWorldInfo(par1Str);
        return worldinfo != null && worldinfo.getSaveVersion() != this.getSaveVersion();
    }

    // JAVADOC METHOD $$ func_75805_a
    public boolean convertMapFormat(String par1Str, IProgressUpdate par2IProgressUpdate)
    {
        par2IProgressUpdate.setLoadingProgress(0);
        ArrayList arraylist = new ArrayList();
        ArrayList arraylist1 = new ArrayList();
        ArrayList arraylist2 = new ArrayList();
        File file1 = new File(this.savesDirectory, par1Str);
        File file2 = new File(file1, "DIM-1");
        File file3 = new File(file1, "DIM1");
        field_151480_b.info("Scanning folders...");
        this.addRegionFilesToCollection(file1, arraylist);

        if (file2.exists())
        {
            this.addRegionFilesToCollection(file2, arraylist1);
        }

        if (file3.exists())
        {
            this.addRegionFilesToCollection(file3, arraylist2);
        }

        int i = arraylist.size() + arraylist1.size() + arraylist2.size();
        field_151480_b.info("Total conversion count is " + i);
        WorldInfo worldinfo = this.getWorldInfo(par1Str);
        Object object = null;

        if (worldinfo.getTerrainType() == WorldType.FLAT)
        {
            object = new WorldChunkManagerHell(BiomeGenBase.plains, 0.5F);
        }
        else
        {
            object = new WorldChunkManager(worldinfo.getSeed(), worldinfo.getTerrainType());
        }

        this.convertFile(new File(file1, "region"), arraylist, (WorldChunkManager)object, 0, i, par2IProgressUpdate);
        this.convertFile(new File(file2, "region"), arraylist1, new WorldChunkManagerHell(BiomeGenBase.hell, 0.0F), arraylist.size(), i, par2IProgressUpdate);
        this.convertFile(new File(file3, "region"), arraylist2, new WorldChunkManagerHell(BiomeGenBase.sky, 0.0F), arraylist.size() + arraylist1.size(), i, par2IProgressUpdate);
        worldinfo.setSaveVersion(19133);

        if (worldinfo.getTerrainType() == WorldType.DEFAULT_1_1)
        {
            worldinfo.setTerrainType(WorldType.DEFAULT);
        }

        this.createFile(par1Str);
        ISaveHandler isavehandler = this.getSaveLoader(par1Str, false);
        isavehandler.saveWorldInfo(worldinfo);
        return true;
    }

    // JAVADOC METHOD $$ func_75809_f
    private void createFile(String par1Str)
    {
        File file1 = new File(this.savesDirectory, par1Str);

        if (!file1.exists())
        {
            field_151480_b.warn("Unable to create level.dat_mcr backup");
        }
        else
        {
            File file2 = new File(file1, "level.dat");

            if (!file2.exists())
            {
                field_151480_b.warn("Unable to create level.dat_mcr backup");
            }
            else
            {
                File file3 = new File(file1, "level.dat_mcr");

                if (!file2.renameTo(file3))
                {
                    field_151480_b.warn("Unable to create level.dat_mcr backup");
                }
            }
        }
    }

    private void convertFile(File par1File, Iterable par2Iterable, WorldChunkManager par3WorldChunkManager, int par4, int par5, IProgressUpdate par6IProgressUpdate)
    {
        Iterator iterator = par2Iterable.iterator();

        while (iterator.hasNext())
        {
            File file2 = (File)iterator.next();
            this.convertChunks(par1File, file2, par3WorldChunkManager, par4, par5, par6IProgressUpdate);
            ++par4;
            int k = (int)Math.round(100.0D * (double)par4 / (double)par5);
            par6IProgressUpdate.setLoadingProgress(k);
        }
    }

    // JAVADOC METHOD $$ func_75811_a
    private void convertChunks(File par1File, File par2File, WorldChunkManager par3WorldChunkManager, int par4, int par5, IProgressUpdate par6IProgressUpdate)
    {
        try
        {
            String s = par2File.getName();
            RegionFile regionfile = new RegionFile(par2File);
            RegionFile regionfile1 = new RegionFile(new File(par1File, s.substring(0, s.length() - ".mcr".length()) + ".mca"));

            for (int k = 0; k < 32; ++k)
            {
                int l;

                for (l = 0; l < 32; ++l)
                {
                    if (regionfile.isChunkSaved(k, l) && !regionfile1.isChunkSaved(k, l))
                    {
                        DataInputStream datainputstream = regionfile.getChunkDataInputStream(k, l);

                        if (datainputstream == null)
                        {
                            field_151480_b.warn("Failed to fetch input stream");
                        }
                        else
                        {
                            NBTTagCompound nbttagcompound = CompressedStreamTools.read(datainputstream);
                            datainputstream.close();
                            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("Level");
                            ChunkLoader.AnvilConverterData anvilconverterdata = ChunkLoader.load(nbttagcompound1);
                            NBTTagCompound nbttagcompound2 = new NBTTagCompound();
                            NBTTagCompound nbttagcompound3 = new NBTTagCompound();
                            nbttagcompound2.setTag("Level", nbttagcompound3);
                            ChunkLoader.convertToAnvilFormat(anvilconverterdata, nbttagcompound3, par3WorldChunkManager);
                            DataOutputStream dataoutputstream = regionfile1.getChunkDataOutputStream(k, l);
                            CompressedStreamTools.write(nbttagcompound2, dataoutputstream);
                            dataoutputstream.close();
                        }
                    }
                }

                l = (int)Math.round(100.0D * (double)(par4 * 1024) / (double)(par5 * 1024));
                int i1 = (int)Math.round(100.0D * (double)((k + 1) * 32 + par4 * 1024) / (double)(par5 * 1024));

                if (i1 > l)
                {
                    par6IProgressUpdate.setLoadingProgress(i1);
                }
            }

            regionfile.close();
            regionfile1.close();
        }
        catch (IOException ioexception)
        {
            ioexception.printStackTrace();
        }
    }

    // JAVADOC METHOD $$ func_75810_a
    private void addRegionFilesToCollection(File par1File, Collection par2Collection)
    {
        File file2 = new File(par1File, "region");
        File[] afile = file2.listFiles(new FilenameFilter()
        {
            private static final String __OBFID = "CL_00000583";
            public boolean accept(File par1File, String par2Str)
            {
                return par2Str.endsWith(".mcr");
            }
        });

        if (afile != null)
        {
            Collections.addAll(par2Collection, afile);
        }
    }
}