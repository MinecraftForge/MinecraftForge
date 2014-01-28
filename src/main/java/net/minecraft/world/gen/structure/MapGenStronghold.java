package net.minecraft.world.gen.structure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeManager;

public class MapGenStronghold extends MapGenStructure
{
    public final List field_151546_e;
    // JAVADOC FIELD $$ field_75056_f
    private boolean ranBiomeCheck;
    private ChunkCoordIntPair[] structureCoords;
    private double field_82671_h;
    private int field_82672_i;
    private static final String __OBFID = "CL_00000481";

    public MapGenStronghold()
    {
        this.structureCoords = new ChunkCoordIntPair[3];
        this.field_82671_h = 32.0D;
        this.field_82672_i = 3;
        this.field_151546_e = new ArrayList();
        BiomeGenBase[] abiomegenbase = BiomeGenBase.func_150565_n();
        int i = abiomegenbase.length;

        for (int j = 0; j < i; ++j)
        {
            BiomeGenBase biomegenbase = abiomegenbase[j];

            if (biomegenbase != null && biomegenbase.minHeight > 0.0F && !BiomeManager.strongHoldBiomesBlackList.contains(biomegenbase))
            {
                this.field_151546_e.add(biomegenbase);
            }
        }
        for (BiomeGenBase biome : BiomeManager.strongHoldBiomes)
        {
            if (!this.field_151546_e.contains(biome))
            {
                this.field_151546_e.add(biome);
            }
        }
    }

    public MapGenStronghold(Map par1Map)
    {
        this();
        Iterator iterator = par1Map.entrySet().iterator();

        while (iterator.hasNext())
        {
            Entry entry = (Entry)iterator.next();

            if (((String)entry.getKey()).equals("distance"))
            {
                this.field_82671_h = MathHelper.func_82713_a((String)entry.getValue(), this.field_82671_h, 1.0D);
            }
            else if (((String)entry.getKey()).equals("count"))
            {
                this.structureCoords = new ChunkCoordIntPair[MathHelper.parseIntWithDefaultAndMax((String)entry.getValue(), this.structureCoords.length, 1)];
            }
            else if (((String)entry.getKey()).equals("spread"))
            {
                this.field_82672_i = MathHelper.parseIntWithDefaultAndMax((String)entry.getValue(), this.field_82672_i, 1);
            }
        }
    }

    public String func_143025_a()
    {
        return "Stronghold";
    }

    protected boolean canSpawnStructureAtCoords(int par1, int par2)
    {
        if (!this.ranBiomeCheck)
        {
            Random random = new Random();
            random.setSeed(this.worldObj.getSeed());
            double d0 = random.nextDouble() * Math.PI * 2.0D;
            int l = 1;

            for (int i1 = 0; i1 < this.structureCoords.length; ++i1)
            {
                double d1 = (1.25D * (double)l + random.nextDouble()) * this.field_82671_h * (double)l;
                int j1 = (int)Math.round(Math.cos(d0) * d1);
                int k1 = (int)Math.round(Math.sin(d0) * d1);
                ChunkPosition chunkposition = this.worldObj.getWorldChunkManager().func_150795_a((j1 << 4) + 8, (k1 << 4) + 8, 112, this.field_151546_e, random);

                if (chunkposition != null)
                {
                    j1 = chunkposition.field_151329_a >> 4;
                    k1 = chunkposition.field_151328_c >> 4;
                }

                this.structureCoords[i1] = new ChunkCoordIntPair(j1, k1);
                d0 += (Math.PI * 2D) * (double)l / (double)this.field_82672_i;

                if (i1 == this.field_82672_i)
                {
                    l += 2 + random.nextInt(5);
                    this.field_82672_i += 1 + random.nextInt(2);
                }
            }

            this.ranBiomeCheck = true;
        }

        ChunkCoordIntPair[] achunkcoordintpair = this.structureCoords;
        int l1 = achunkcoordintpair.length;

        for (int k = 0; k < l1; ++k)
        {
            ChunkCoordIntPair chunkcoordintpair = achunkcoordintpair[k];

            if (par1 == chunkcoordintpair.chunkXPos && par2 == chunkcoordintpair.chunkZPos)
            {
                return true;
            }
        }

        return false;
    }

    // JAVADOC METHOD $$ func_75052_o_
    protected List getCoordList()
    {
        ArrayList arraylist = new ArrayList();
        ChunkCoordIntPair[] achunkcoordintpair = this.structureCoords;
        int i = achunkcoordintpair.length;

        for (int j = 0; j < i; ++j)
        {
            ChunkCoordIntPair chunkcoordintpair = achunkcoordintpair[j];

            if (chunkcoordintpair != null)
            {
                arraylist.add(chunkcoordintpair.func_151349_a(64));
            }
        }

        return arraylist;
    }

    protected StructureStart getStructureStart(int par1, int par2)
    {
        MapGenStronghold.Start start;

        for (start = new MapGenStronghold.Start(this.worldObj, this.rand, par1, par2); start.getComponents().isEmpty() || ((StructureStrongholdPieces.Stairs2)start.getComponents().get(0)).strongholdPortalRoom == null; start = new MapGenStronghold.Start(this.worldObj, this.rand, par1, par2))
        {
            ;
        }

        return start;
    }

    public static class Start extends StructureStart
        {
            private static final String __OBFID = "CL_00000482";

            public Start() {}

            public Start(World par1World, Random par2Random, int par3, int par4)
            {
                super(par3, par4);
                StructureStrongholdPieces.prepareStructurePieces();
                StructureStrongholdPieces.Stairs2 stairs2 = new StructureStrongholdPieces.Stairs2(0, par2Random, (par3 << 4) + 2, (par4 << 4) + 2);
                this.components.add(stairs2);
                stairs2.buildComponent(stairs2, this.components, par2Random);
                List list = stairs2.field_75026_c;

                while (!list.isEmpty())
                {
                    int k = par2Random.nextInt(list.size());
                    StructureComponent structurecomponent = (StructureComponent)list.remove(k);
                    structurecomponent.buildComponent(stairs2, this.components, par2Random);
                }

                this.updateBoundingBox();
                this.markAvailableHeight(par1World, par2Random, 10);
            }
        }
}