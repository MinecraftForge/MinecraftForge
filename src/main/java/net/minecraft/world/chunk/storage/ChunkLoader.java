package net.minecraft.world.chunk.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.NibbleArray;

public class ChunkLoader
{
    private static final String __OBFID = "CL_00000379";

    public static ChunkLoader.AnvilConverterData load(NBTTagCompound par0NBTTagCompound)
    {
        int i = par0NBTTagCompound.getInteger("xPos");
        int j = par0NBTTagCompound.getInteger("zPos");
        ChunkLoader.AnvilConverterData anvilconverterdata = new ChunkLoader.AnvilConverterData(i, j);
        anvilconverterdata.blocks = par0NBTTagCompound.getByteArray("Blocks");
        anvilconverterdata.data = new NibbleArrayReader(par0NBTTagCompound.getByteArray("Data"), 7);
        anvilconverterdata.skyLight = new NibbleArrayReader(par0NBTTagCompound.getByteArray("SkyLight"), 7);
        anvilconverterdata.blockLight = new NibbleArrayReader(par0NBTTagCompound.getByteArray("BlockLight"), 7);
        anvilconverterdata.heightmap = par0NBTTagCompound.getByteArray("HeightMap");
        anvilconverterdata.terrainPopulated = par0NBTTagCompound.getBoolean("TerrainPopulated");
        anvilconverterdata.entities = par0NBTTagCompound.func_150295_c("Entities", 10);
        anvilconverterdata.field_151564_i = par0NBTTagCompound.func_150295_c("TileEntities", 10);
        anvilconverterdata.field_151563_j = par0NBTTagCompound.func_150295_c("TileTicks", 10);

        try
        {
            anvilconverterdata.lastUpdated = par0NBTTagCompound.getLong("LastUpdate");
        }
        catch (ClassCastException classcastexception)
        {
            anvilconverterdata.lastUpdated = (long)par0NBTTagCompound.getInteger("LastUpdate");
        }

        return anvilconverterdata;
    }

    public static void convertToAnvilFormat(ChunkLoader.AnvilConverterData par0AnvilConverterData, NBTTagCompound par1NBTTagCompound, WorldChunkManager par2WorldChunkManager)
    {
        par1NBTTagCompound.setInteger("xPos", par0AnvilConverterData.x);
        par1NBTTagCompound.setInteger("zPos", par0AnvilConverterData.z);
        par1NBTTagCompound.setLong("LastUpdate", par0AnvilConverterData.lastUpdated);
        int[] aint = new int[par0AnvilConverterData.heightmap.length];

        for (int i = 0; i < par0AnvilConverterData.heightmap.length; ++i)
        {
            aint[i] = par0AnvilConverterData.heightmap[i];
        }

        par1NBTTagCompound.setIntArray("HeightMap", aint);
        par1NBTTagCompound.setBoolean("TerrainPopulated", par0AnvilConverterData.terrainPopulated);
        NBTTagList nbttaglist = new NBTTagList();
        int k;

        for (int j = 0; j < 8; ++j)
        {
            boolean flag = true;

            for (k = 0; k < 16 && flag; ++k)
            {
                int l = 0;

                while (l < 16 && flag)
                {
                    int i1 = 0;

                    while (true)
                    {
                        if (i1 < 16)
                        {
                            int j1 = k << 11 | i1 << 7 | l + (j << 4);
                            byte b0 = par0AnvilConverterData.blocks[j1];

                            if (b0 == 0)
                            {
                                ++i1;
                                continue;
                            }

                            flag = false;
                        }

                        ++l;
                        break;
                    }
                }
            }

            if (!flag)
            {
                byte[] abyte1 = new byte[4096];
                NibbleArray nibblearray = new NibbleArray(abyte1.length, 4);
                NibbleArray nibblearray1 = new NibbleArray(abyte1.length, 4);
                NibbleArray nibblearray2 = new NibbleArray(abyte1.length, 4);

                for (int k2 = 0; k2 < 16; ++k2)
                {
                    for (int k1 = 0; k1 < 16; ++k1)
                    {
                        for (int l1 = 0; l1 < 16; ++l1)
                        {
                            int i2 = k2 << 11 | l1 << 7 | k1 + (j << 4);
                            byte b1 = par0AnvilConverterData.blocks[i2];
                            abyte1[k1 << 8 | l1 << 4 | k2] = (byte)(b1 & 255);
                            nibblearray.set(k2, k1, l1, par0AnvilConverterData.data.get(k2, k1 + (j << 4), l1));
                            nibblearray1.set(k2, k1, l1, par0AnvilConverterData.skyLight.get(k2, k1 + (j << 4), l1));
                            nibblearray2.set(k2, k1, l1, par0AnvilConverterData.blockLight.get(k2, k1 + (j << 4), l1));
                        }
                    }
                }

                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Y", (byte)(j & 255));
                nbttagcompound1.setByteArray("Blocks", abyte1);
                nbttagcompound1.setByteArray("Data", nibblearray.data);
                nbttagcompound1.setByteArray("SkyLight", nibblearray1.data);
                nbttagcompound1.setByteArray("BlockLight", nibblearray2.data);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        par1NBTTagCompound.setTag("Sections", nbttaglist);
        byte[] abyte = new byte[256];

        for (int j2 = 0; j2 < 16; ++j2)
        {
            for (k = 0; k < 16; ++k)
            {
                abyte[k << 4 | j2] = (byte)(par2WorldChunkManager.getBiomeGenAt(par0AnvilConverterData.x << 4 | j2, par0AnvilConverterData.z << 4 | k).biomeID & 255);
            }
        }

        par1NBTTagCompound.setByteArray("Biomes", abyte);
        par1NBTTagCompound.setTag("Entities", par0AnvilConverterData.entities);
        par1NBTTagCompound.setTag("TileEntities", par0AnvilConverterData.field_151564_i);

        if (par0AnvilConverterData.field_151563_j != null)
        {
            par1NBTTagCompound.setTag("TileTicks", par0AnvilConverterData.field_151563_j);
        }
    }

    public static class AnvilConverterData
        {
            public long lastUpdated;
            public boolean terrainPopulated;
            public byte[] heightmap;
            public NibbleArrayReader blockLight;
            public NibbleArrayReader skyLight;
            public NibbleArrayReader data;
            public byte[] blocks;
            public NBTTagList entities;
            public NBTTagList field_151564_i;
            public NBTTagList field_151563_j;
            public final int x;
            public final int z;
            private static final String __OBFID = "CL_00000380";

            public AnvilConverterData(int par1, int par2)
            {
                this.x = par1;
                this.z = par2;
            }
        }
}