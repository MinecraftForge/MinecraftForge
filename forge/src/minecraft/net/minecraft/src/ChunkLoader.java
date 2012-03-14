package net.minecraft.src;

public class ChunkLoader
{
    public static AnvilConverterData load(NBTTagCompound par0NBTTagCompound)
    {
        int var1 = par0NBTTagCompound.getInteger("xPos");
        int var2 = par0NBTTagCompound.getInteger("zPos");
        AnvilConverterData var3 = new AnvilConverterData(var1, var2);
        var3.blocks = par0NBTTagCompound.getByteArray("Blocks");
        var3.data = new NibbleArrayReader(par0NBTTagCompound.getByteArray("Data"), 7);
        var3.skyLight = new NibbleArrayReader(par0NBTTagCompound.getByteArray("SkyLight"), 7);
        var3.blockLight = new NibbleArrayReader(par0NBTTagCompound.getByteArray("BlockLight"), 7);
        var3.heightmap = par0NBTTagCompound.getByteArray("HeightMap");
        var3.field_48606_b = par0NBTTagCompound.getBoolean("TerrainPopulated");
        var3.entities = par0NBTTagCompound.getTagList("Entities");
        var3.tileEntities = par0NBTTagCompound.getTagList("TileEntities");
        var3.tileTicks = par0NBTTagCompound.getTagList("TileTicks");
        var3.lastUpdated = par0NBTTagCompound.getLong("LastUpdate");
        return var3;
    }

    public static void convertToAnvilFormat(AnvilConverterData par0AnvilConverterData, NBTTagCompound par1NBTTagCompound, WorldChunkManager par2WorldChunkManager)
    {
        par1NBTTagCompound.setInteger("xPos", par0AnvilConverterData.x);
        par1NBTTagCompound.setInteger("zPos", par0AnvilConverterData.z);
        par1NBTTagCompound.setLong("LastUpdate", par0AnvilConverterData.lastUpdated);
        int[] var3 = new int[par0AnvilConverterData.heightmap.length];

        for (int var4 = 0; var4 < par0AnvilConverterData.heightmap.length; ++var4)
        {
            var3[var4] = par0AnvilConverterData.heightmap[var4];
        }

        par1NBTTagCompound.func_48183_a("HeightMap", var3);
        par1NBTTagCompound.setBoolean("TerrainPopulated", par0AnvilConverterData.field_48606_b);
        NBTTagList var16 = new NBTTagList("Sections");
        int var7;

        for (int var5 = 0; var5 < 8; ++var5)
        {
            boolean var6 = true;

            for (var7 = 0; var7 < 16 && var6; ++var7)
            {
                int var8 = 0;

                while (var8 < 16 && var6)
                {
                    int var9 = 0;

                    while (true)
                    {
                        if (var9 < 16)
                        {
                            int var10 = var7 << 11 | var9 << 7 | var8 + (var5 << 4);
                            byte var11 = par0AnvilConverterData.blocks[var10];

                            if (var11 == 0)
                            {
                                ++var9;
                                continue;
                            }

                            var6 = false;
                        }

                        ++var8;
                        break;
                    }
                }
            }

            if (!var6)
            {
                byte[] var19 = new byte[4096];
                NibbleArray var20 = new NibbleArray(var19.length, 4);
                NibbleArray var21 = new NibbleArray(var19.length, 4);
                NibbleArray var23 = new NibbleArray(var19.length, 4);

                for (int var22 = 0; var22 < 16; ++var22)
                {
                    for (int var12 = 0; var12 < 16; ++var12)
                    {
                        for (int var13 = 0; var13 < 16; ++var13)
                        {
                            int var14 = var22 << 11 | var13 << 7 | var12 + (var5 << 4);
                            byte var15 = par0AnvilConverterData.blocks[var14];
                            var19[var12 << 8 | var13 << 4 | var22] = (byte)(var15 & 255);
                            var20.set(var22, var12, var13, par0AnvilConverterData.data.get(var22, var12 + (var5 << 4), var13));
                            var21.set(var22, var12, var13, par0AnvilConverterData.skyLight.get(var22, var12 + (var5 << 4), var13));
                            var23.set(var22, var12, var13, par0AnvilConverterData.blockLight.get(var22, var12 + (var5 << 4), var13));
                        }
                    }
                }

                NBTTagCompound var24 = new NBTTagCompound();
                var24.setByte("Y", (byte)(var5 & 255));
                var24.setByteArray("Blocks", var19);
                var24.setByteArray("Data", var20.data);
                var24.setByteArray("SkyLight", var21.data);
                var24.setByteArray("BlockLight", var23.data);
                var16.appendTag(var24);
            }
        }

        par1NBTTagCompound.setTag("Sections", var16);
        byte[] var17 = new byte[256];

        for (int var18 = 0; var18 < 16; ++var18)
        {
            for (var7 = 0; var7 < 16; ++var7)
            {
                var17[var7 << 4 | var18] = (byte)(par2WorldChunkManager.getBiomeGenAt(par0AnvilConverterData.x << 4 | var18, par0AnvilConverterData.z << 4 | var7).biomeID & 255);
            }
        }

        par1NBTTagCompound.setByteArray("Biomes", var17);
        par1NBTTagCompound.setTag("Entities", par0AnvilConverterData.entities);
        par1NBTTagCompound.setTag("TileEntities", par0AnvilConverterData.tileEntities);

        if (par0AnvilConverterData.tileTicks != null)
        {
            par1NBTTagCompound.setTag("TileTicks", par0AnvilConverterData.tileTicks);
        }
    }
}
