package net.minecraft.world.gen.structure;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class MapGenVillage extends MapGenStructure
{
    // JAVADOC FIELD $$ field_75055_e
    public static List villageSpawnBiomes = Arrays.asList(new BiomeGenBase[] {BiomeGenBase.plains, BiomeGenBase.desert, BiomeGenBase.field_150588_X});
    // JAVADOC FIELD $$ field_75054_f
    private int terrainType;
    private int field_82665_g;
    private int field_82666_h;
    private static final String __OBFID = "CL_00000514";

    public MapGenVillage()
    {
        this.field_82665_g = 32;
        this.field_82666_h = 8;
    }

    public MapGenVillage(Map par1Map)
    {
        this();
        Iterator iterator = par1Map.entrySet().iterator();

        while (iterator.hasNext())
        {
            Entry entry = (Entry)iterator.next();

            if (((String)entry.getKey()).equals("size"))
            {
                this.terrainType = MathHelper.parseIntWithDefaultAndMax((String)entry.getValue(), this.terrainType, 0);
            }
            else if (((String)entry.getKey()).equals("distance"))
            {
                this.field_82665_g = MathHelper.parseIntWithDefaultAndMax((String)entry.getValue(), this.field_82665_g, this.field_82666_h + 1);
            }
        }
    }

    public String func_143025_a()
    {
        return "Village";
    }

    protected boolean canSpawnStructureAtCoords(int par1, int par2)
    {
        int k = par1;
        int l = par2;

        if (par1 < 0)
        {
            par1 -= this.field_82665_g - 1;
        }

        if (par2 < 0)
        {
            par2 -= this.field_82665_g - 1;
        }

        int i1 = par1 / this.field_82665_g;
        int j1 = par2 / this.field_82665_g;
        Random random = this.worldObj.setRandomSeed(i1, j1, 10387312);
        i1 *= this.field_82665_g;
        j1 *= this.field_82665_g;
        i1 += random.nextInt(this.field_82665_g - this.field_82666_h);
        j1 += random.nextInt(this.field_82665_g - this.field_82666_h);

        if (k == i1 && l == j1)
        {
            boolean flag = this.worldObj.getWorldChunkManager().areBiomesViable(k * 16 + 8, l * 16 + 8, 0, villageSpawnBiomes);

            if (flag)
            {
                return true;
            }
        }

        return false;
    }

    protected StructureStart getStructureStart(int par1, int par2)
    {
        return new MapGenVillage.Start(this.worldObj, this.rand, par1, par2, this.terrainType);
    }

    public static class Start extends StructureStart
        {
            // JAVADOC FIELD $$ field_75076_c
            private boolean hasMoreThanTwoComponents;
            private static final String __OBFID = "CL_00000515";

            public Start() {}

            public Start(World par1World, Random par2Random, int par3, int par4, int par5)
            {
                super(par3, par4);
                List list = StructureVillagePieces.getStructureVillageWeightedPieceList(par2Random, par5);
                StructureVillagePieces.Start start = new StructureVillagePieces.Start(par1World.getWorldChunkManager(), 0, par2Random, (par3 << 4) + 2, (par4 << 4) + 2, list, par5);
                this.components.add(start);
                start.buildComponent(start, this.components, par2Random);
                List list1 = start.field_74930_j;
                List list2 = start.field_74932_i;
                int l;

                while (!list1.isEmpty() || !list2.isEmpty())
                {
                    StructureComponent structurecomponent;

                    if (list1.isEmpty())
                    {
                        l = par2Random.nextInt(list2.size());
                        structurecomponent = (StructureComponent)list2.remove(l);
                        structurecomponent.buildComponent(start, this.components, par2Random);
                    }
                    else
                    {
                        l = par2Random.nextInt(list1.size());
                        structurecomponent = (StructureComponent)list1.remove(l);
                        structurecomponent.buildComponent(start, this.components, par2Random);
                    }
                }

                this.updateBoundingBox();
                l = 0;
                Iterator iterator = this.components.iterator();

                while (iterator.hasNext())
                {
                    StructureComponent structurecomponent1 = (StructureComponent)iterator.next();

                    if (!(structurecomponent1 instanceof StructureVillagePieces.Road))
                    {
                        ++l;
                    }
                }

                this.hasMoreThanTwoComponents = l > 2;
            }

            // JAVADOC METHOD $$ func_75069_d
            public boolean isSizeableStructure()
            {
                return this.hasMoreThanTwoComponents;
            }

            public void func_143022_a(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143022_a(par1NBTTagCompound);
                par1NBTTagCompound.setBoolean("Valid", this.hasMoreThanTwoComponents);
            }

            public void func_143017_b(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143017_b(par1NBTTagCompound);
                this.hasMoreThanTwoComponents = par1NBTTagCompound.getBoolean("Valid");
            }
        }
}