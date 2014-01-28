package net.minecraft.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

import cpw.mods.fml.common.eventhandler.Event.Result;
import net.minecraftforge.event.ForgeEventFactory;

public final class SpawnerAnimals
{
    // JAVADOC FIELD $$ field_77193_b
    private HashMap eligibleChunksForSpawning = new HashMap();
    private static final String __OBFID = "CL_00000152";

    protected static ChunkPosition func_151350_a(World p_151350_0_, int p_151350_1_, int p_151350_2_)
    {
        Chunk chunk = p_151350_0_.getChunkFromChunkCoords(p_151350_1_, p_151350_2_);
        int k = p_151350_1_ * 16 + p_151350_0_.rand.nextInt(16);
        int l = p_151350_2_ * 16 + p_151350_0_.rand.nextInt(16);
        int i1 = p_151350_0_.rand.nextInt(chunk == null ? p_151350_0_.getActualHeight() : chunk.getTopFilledSegment() + 16 - 1);
        return new ChunkPosition(k, i1, l);
    }

    // JAVADOC METHOD $$ func_77192_a
    public int findChunksForSpawning(WorldServer par1WorldServer, boolean par2, boolean par3, boolean par4)
    {
        if (!par2 && !par3)
        {
            return 0;
        }
        else
        {
            this.eligibleChunksForSpawning.clear();
            int i;
            int k;

            for (i = 0; i < par1WorldServer.playerEntities.size(); ++i)
            {
                EntityPlayer entityplayer = (EntityPlayer)par1WorldServer.playerEntities.get(i);
                int j = MathHelper.floor_double(entityplayer.posX / 16.0D);
                k = MathHelper.floor_double(entityplayer.posZ / 16.0D);
                byte b0 = 8;

                for (int l = -b0; l <= b0; ++l)
                {
                    for (int i1 = -b0; i1 <= b0; ++i1)
                    {
                        boolean flag3 = l == -b0 || l == b0 || i1 == -b0 || i1 == b0;
                        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(l + j, i1 + k);

                        if (!flag3)
                        {
                            this.eligibleChunksForSpawning.put(chunkcoordintpair, Boolean.valueOf(false));
                        }
                        else if (!this.eligibleChunksForSpawning.containsKey(chunkcoordintpair))
                        {
                            this.eligibleChunksForSpawning.put(chunkcoordintpair, Boolean.valueOf(true));
                        }
                    }
                }
            }

            i = 0;
            ChunkCoordinates chunkcoordinates = par1WorldServer.getSpawnPoint();
            EnumCreatureType[] aenumcreaturetype = EnumCreatureType.values();
            k = aenumcreaturetype.length;

            for (int k3 = 0; k3 < k; ++k3)
            {
                EnumCreatureType enumcreaturetype = aenumcreaturetype[k3];

                if ((!enumcreaturetype.getPeacefulCreature() || par3) && (enumcreaturetype.getPeacefulCreature() || par2) && (!enumcreaturetype.getAnimal() || par4) && par1WorldServer.countEntities(enumcreaturetype, true) <= enumcreaturetype.getMaxNumberOfCreature() * this.eligibleChunksForSpawning.size() / 256)
                {
                    Iterator iterator = this.eligibleChunksForSpawning.keySet().iterator();
                    ArrayList<ChunkCoordIntPair> tmp = new ArrayList(eligibleChunksForSpawning.keySet());
                    Collections.shuffle(tmp);
                    iterator = tmp.iterator();
                    label110:

                    while (iterator.hasNext())
                    {
                        ChunkCoordIntPair chunkcoordintpair1 = (ChunkCoordIntPair)iterator.next();

                        if (!((Boolean)this.eligibleChunksForSpawning.get(chunkcoordintpair1)).booleanValue())
                        {
                            ChunkPosition chunkposition = func_151350_a(par1WorldServer, chunkcoordintpair1.chunkXPos, chunkcoordintpair1.chunkZPos);
                            int j1 = chunkposition.field_151329_a;
                            int k1 = chunkposition.field_151327_b;
                            int l1 = chunkposition.field_151328_c;

                            if (!par1WorldServer.func_147439_a(j1, k1, l1).func_149721_r() && par1WorldServer.func_147439_a(j1, k1, l1).func_149688_o() == enumcreaturetype.getCreatureMaterial())
                            {
                                int i2 = 0;
                                int j2 = 0;

                                while (j2 < 3)
                                {
                                    int k2 = j1;
                                    int l2 = k1;
                                    int i3 = l1;
                                    byte b1 = 6;
                                    BiomeGenBase.SpawnListEntry spawnlistentry = null;
                                    IEntityLivingData ientitylivingdata = null;
                                    int j3 = 0;

                                    while (true)
                                    {
                                        if (j3 < 4)
                                        {
                                            label103:
                                            {
                                                k2 += par1WorldServer.rand.nextInt(b1) - par1WorldServer.rand.nextInt(b1);
                                                l2 += par1WorldServer.rand.nextInt(1) - par1WorldServer.rand.nextInt(1);
                                                i3 += par1WorldServer.rand.nextInt(b1) - par1WorldServer.rand.nextInt(b1);

                                                if (canCreatureTypeSpawnAtLocation(enumcreaturetype, par1WorldServer, k2, l2, i3))
                                                {
                                                    float f = (float)k2 + 0.5F;
                                                    float f1 = (float)l2;
                                                    float f2 = (float)i3 + 0.5F;

                                                    if (par1WorldServer.getClosestPlayer((double)f, (double)f1, (double)f2, 24.0D) == null)
                                                    {
                                                        float f3 = f - (float)chunkcoordinates.posX;
                                                        float f4 = f1 - (float)chunkcoordinates.posY;
                                                        float f5 = f2 - (float)chunkcoordinates.posZ;
                                                        float f6 = f3 * f3 + f4 * f4 + f5 * f5;

                                                        if (f6 >= 576.0F)
                                                        {
                                                            if (spawnlistentry == null)
                                                            {
                                                                spawnlistentry = par1WorldServer.spawnRandomCreature(enumcreaturetype, k2, l2, i3);

                                                                if (spawnlistentry == null)
                                                                {
                                                                    break label103;
                                                                }
                                                            }

                                                            EntityLiving entityliving;

                                                            try
                                                            {
                                                                entityliving = (EntityLiving)spawnlistentry.entityClass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {par1WorldServer});
                                                            }
                                                            catch (Exception exception)
                                                            {
                                                                exception.printStackTrace();
                                                                return i;
                                                            }

                                                            entityliving.setLocationAndAngles((double)f, (double)f1, (double)f2, par1WorldServer.rand.nextFloat() * 360.0F, 0.0F);
                                                            
                                                            Result canSpawn = ForgeEventFactory.canEntitySpawn(entityliving, par1WorldServer, f, f1, f2);
                                                            if (canSpawn == Result.ALLOW || (canSpawn == Result.DEFAULT && entityliving.getCanSpawnHere()))
                                                            {
                                                                ++i2;
                                                                par1WorldServer.spawnEntityInWorld(entityliving);
                                                                if (!ForgeEventFactory.doSpecialSpawn(entityliving, par1WorldServer, f, f1, f2))
                                                                {
                                                                    ientitylivingdata = entityliving.onSpawnWithEgg(ientitylivingdata);
                                                                }

                                                                if (j2 >= ForgeEventFactory.getMaxSpawnPackSize(entityliving))
                                                                {
                                                                    continue label110;
                                                                }
                                                            }

                                                            i += i2;
                                                        }
                                                    }
                                                }

                                                ++j3;
                                                continue;
                                            }
                                        }

                                        ++j2;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return i;
        }
    }

    // JAVADOC METHOD $$ func_77190_a
    public static boolean canCreatureTypeSpawnAtLocation(EnumCreatureType par0EnumCreatureType, World par1World, int par2, int par3, int par4)
    {
        if (par0EnumCreatureType.getCreatureMaterial() == Material.field_151586_h)
        {
            return par1World.func_147439_a(par2, par3, par4).func_149688_o().isLiquid() && par1World.func_147439_a(par2, par3 - 1, par4).func_149688_o().isLiquid() && !par1World.func_147439_a(par2, par3 + 1, par4).func_149721_r();
        }
        else if (!World.func_147466_a(par1World, par2, par3 - 1, par4))
        {
            return false;
        }
        else
        {
            Block block = par1World.func_147439_a(par2, par3 - 1, par4);
            boolean spawnBlock = block.canCreatureSpawn(par0EnumCreatureType, par1World, par2, par3, par4);
            return spawnBlock && block != Blocks.bedrock && !par1World.func_147439_a(par2, par3, par4).func_149721_r() && !par1World.func_147439_a(par2, par3, par4).func_149688_o().isLiquid() && !par1World.func_147439_a(par2, par3 + 1, par4).func_149721_r();
        }
    }

    // JAVADOC METHOD $$ func_77191_a
    public static void performWorldGenSpawning(World par0World, BiomeGenBase par1BiomeGenBase, int par2, int par3, int par4, int par5, Random par6Random)
    {
        List list = par1BiomeGenBase.getSpawnableList(EnumCreatureType.creature);

        if (!list.isEmpty())
        {
            while (par6Random.nextFloat() < par1BiomeGenBase.getSpawningChance())
            {
                BiomeGenBase.SpawnListEntry spawnlistentry = (BiomeGenBase.SpawnListEntry)WeightedRandom.getRandomItem(par0World.rand, list);
                IEntityLivingData ientitylivingdata = null;
                int i1 = spawnlistentry.minGroupCount + par6Random.nextInt(1 + spawnlistentry.maxGroupCount - spawnlistentry.minGroupCount);
                int j1 = par2 + par6Random.nextInt(par4);
                int k1 = par3 + par6Random.nextInt(par5);
                int l1 = j1;
                int i2 = k1;

                for (int j2 = 0; j2 < i1; ++j2)
                {
                    boolean flag = false;

                    for (int k2 = 0; !flag && k2 < 4; ++k2)
                    {
                        int l2 = par0World.getTopSolidOrLiquidBlock(j1, k1);

                        if (canCreatureTypeSpawnAtLocation(EnumCreatureType.creature, par0World, j1, l2, k1))
                        {
                            float f = (float)j1 + 0.5F;
                            float f1 = (float)l2;
                            float f2 = (float)k1 + 0.5F;
                            EntityLiving entityliving;

                            try
                            {
                                entityliving = (EntityLiving)spawnlistentry.entityClass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {par0World});
                            }
                            catch (Exception exception)
                            {
                                exception.printStackTrace();
                                continue;
                            }

                            entityliving.setLocationAndAngles((double)f, (double)f1, (double)f2, par6Random.nextFloat() * 360.0F, 0.0F);
                            par0World.spawnEntityInWorld(entityliving);
                            ientitylivingdata = entityliving.onSpawnWithEgg(ientitylivingdata);
                            flag = true;
                        }

                        j1 += par6Random.nextInt(5) - par6Random.nextInt(5);

                        for (k1 += par6Random.nextInt(5) - par6Random.nextInt(5); j1 < par2 || j1 >= par2 + par4 || k1 < par3 || k1 >= par3 + par4; k1 = i2 + par6Random.nextInt(5) - par6Random.nextInt(5))
                        {
                            j1 = l1 + par6Random.nextInt(5) - par6Random.nextInt(5);
                        }
                    }
                }
            }
        }
    }
}