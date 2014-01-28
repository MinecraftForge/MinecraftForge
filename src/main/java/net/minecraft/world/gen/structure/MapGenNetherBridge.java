package net.minecraft.world.gen.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class MapGenNetherBridge extends MapGenStructure
{
    private List spawnList = new ArrayList();
    private static final String __OBFID = "CL_00000451";

    public MapGenNetherBridge()
    {
        this.spawnList.add(new BiomeGenBase.SpawnListEntry(EntityBlaze.class, 10, 2, 3));
        this.spawnList.add(new BiomeGenBase.SpawnListEntry(EntityPigZombie.class, 5, 4, 4));
        this.spawnList.add(new BiomeGenBase.SpawnListEntry(EntitySkeleton.class, 10, 4, 4));
        this.spawnList.add(new BiomeGenBase.SpawnListEntry(EntityMagmaCube.class, 3, 4, 4));
    }

    public String func_143025_a()
    {
        return "Fortress";
    }

    public List getSpawnList()
    {
        return this.spawnList;
    }

    protected boolean canSpawnStructureAtCoords(int par1, int par2)
    {
        int k = par1 >> 4;
        int l = par2 >> 4;
        this.rand.setSeed((long)(k ^ l << 4) ^ this.worldObj.getSeed());
        this.rand.nextInt();
        return this.rand.nextInt(3) != 0 ? false : (par1 != (k << 4) + 4 + this.rand.nextInt(8) ? false : par2 == (l << 4) + 4 + this.rand.nextInt(8));
    }

    protected StructureStart getStructureStart(int par1, int par2)
    {
        return new MapGenNetherBridge.Start(this.worldObj, this.rand, par1, par2);
    }

    public static class Start extends StructureStart
        {
            private static final String __OBFID = "CL_00000452";

            public Start() {}

            public Start(World par1World, Random par2Random, int par3, int par4)
            {
                super(par3, par4);
                StructureNetherBridgePieces.Start start = new StructureNetherBridgePieces.Start(par2Random, (par3 << 4) + 2, (par4 << 4) + 2);
                this.components.add(start);
                start.buildComponent(start, this.components, par2Random);
                ArrayList arraylist = start.field_74967_d;

                while (!arraylist.isEmpty())
                {
                    int k = par2Random.nextInt(arraylist.size());
                    StructureComponent structurecomponent = (StructureComponent)arraylist.remove(k);
                    structurecomponent.buildComponent(start, this.components, par2Random);
                }

                this.updateBoundingBox();
                this.setRandomHeight(par1World, par2Random, 48, 70);
            }
        }
}