package net.minecraft.src;

import java.util.Iterator;
import java.util.List;

public class VillageSiege
{
    private World field_48582_a;
    private boolean field_48580_b = false;
    private int field_48581_c = -1;
    private int field_48578_d;
    private int field_48579_e;
    private Village field_48576_f;
    private int field_48577_g;
    private int field_48583_h;
    private int field_48584_i;

    public VillageSiege(World par1World)
    {
        this.field_48582_a = par1World;
    }

    /**
     * Runs a single tick for the village siege
     */
    public void tick()
    {
        boolean var1 = false;

        if (var1)
        {
            if (this.field_48581_c == 2)
            {
                this.field_48578_d = 100;
                return;
            }
        }
        else
        {
            if (this.field_48582_a.isDaytime())
            {
                this.field_48581_c = 0;
                return;
            }

            if (this.field_48581_c == 2)
            {
                return;
            }

            if (this.field_48581_c == 0)
            {
                float var2 = this.field_48582_a.getCelestialAngle(0.0F);

                if ((double)var2 < 0.5D || (double)var2 > 0.501D)
                {
                    return;
                }

                this.field_48581_c = this.field_48582_a.rand.nextInt(10) == 0 ? 1 : 2;
                this.field_48580_b = false;

                if (this.field_48581_c == 2)
                {
                    return;
                }
            }
        }

        if (!this.field_48580_b)
        {
            if (!this.func_48574_b())
            {
                return;
            }

            this.field_48580_b = true;
        }

        if (this.field_48579_e > 0)
        {
            --this.field_48579_e;
        }
        else
        {
            this.field_48579_e = 2;

            if (this.field_48578_d > 0)
            {
                this.func_48575_c();
                --this.field_48578_d;
            }
            else
            {
                this.field_48581_c = 2;
            }
        }
    }

    private boolean func_48574_b()
    {
        List var1 = this.field_48582_a.playerEntities;
        Iterator var2 = var1.iterator();

        while (var2.hasNext())
        {
            EntityPlayer var3 = (EntityPlayer)var2.next();
            this.field_48576_f = this.field_48582_a.villageCollectionObj.findNearestVillage((int)var3.posX, (int)var3.posY, (int)var3.posZ, 1);

            if (this.field_48576_f != null && this.field_48576_f.getNumVillageDoors() >= 10 && this.field_48576_f.getTicksSinceLastDoorAdding() >= 20 && this.field_48576_f.getNumVillagers() >= 20)
            {
                ChunkCoordinates var4 = this.field_48576_f.getCenter();
                float var5 = (float)this.field_48576_f.getVillageRadius();
                boolean var6 = false;
                int var7 = 0;

                while (true)
                {
                    if (var7 < 10)
                    {
                        this.field_48577_g = var4.posX + (int)((double)(MathHelper.cos(this.field_48582_a.rand.nextFloat() * (float)Math.PI * 2.0F) * var5) * 0.9D);
                        this.field_48583_h = var4.posY;
                        this.field_48584_i = var4.posZ + (int)((double)(MathHelper.sin(this.field_48582_a.rand.nextFloat() * (float)Math.PI * 2.0F) * var5) * 0.9D);
                        var6 = false;
                        Iterator var8 = this.field_48582_a.villageCollectionObj.func_48554_b().iterator();

                        while (var8.hasNext())
                        {
                            Village var9 = (Village)var8.next();

                            if (var9 != this.field_48576_f && var9.isInRange(this.field_48577_g, this.field_48583_h, this.field_48584_i))
                            {
                                var6 = true;
                                break;
                            }
                        }

                        if (var6)
                        {
                            ++var7;
                            continue;
                        }
                    }

                    if (var6)
                    {
                        return false;
                    }

                    Vec3D var10 = this.func_48572_a(this.field_48577_g, this.field_48583_h, this.field_48584_i);

                    if (var10 != null)
                    {
                        this.field_48579_e = 0;
                        this.field_48578_d = 20;
                        return true;
                    }

                    break;
                }
            }
        }

        return false;
    }

    private boolean func_48575_c()
    {
        Vec3D var1 = this.func_48572_a(this.field_48577_g, this.field_48583_h, this.field_48584_i);

        if (var1 == null)
        {
            return false;
        }
        else
        {
            EntityZombie var2;

            try
            {
                var2 = new EntityZombie(this.field_48582_a);
            }
            catch (Exception var4)
            {
                var4.printStackTrace();
                return false;
            }

            var2.setLocationAndAngles(var1.xCoord, var1.yCoord, var1.zCoord, this.field_48582_a.rand.nextFloat() * 360.0F, 0.0F);
            this.field_48582_a.spawnEntityInWorld(var2);
            ChunkCoordinates var3 = this.field_48576_f.getCenter();
            var2.setHomeArea(var3.posX, var3.posY, var3.posZ, this.field_48576_f.getVillageRadius());
            return true;
        }
    }

    private Vec3D func_48572_a(int par1, int par2, int par3)
    {
        for (int var4 = 0; var4 < 10; ++var4)
        {
            int var5 = par1 + this.field_48582_a.rand.nextInt(16) - 8;
            int var6 = par2 + this.field_48582_a.rand.nextInt(6) - 3;
            int var7 = par3 + this.field_48582_a.rand.nextInt(16) - 8;

            if (this.field_48576_f.isInRange(var5, var6, var7) && SpawnerAnimals.canCreatureTypeSpawnAtLocation(EnumCreatureType.monster, this.field_48582_a, var5, var6, var7))
            {
                return Vec3D.createVector((double)var5, (double)var6, (double)var7);
            }
        }

        return null;
    }
}
