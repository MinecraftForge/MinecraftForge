package net.minecraft.tileentity;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.world.World;

public class TileEntityMobSpawner extends TileEntity
{
    private final MobSpawnerBaseLogic field_145882_a = new MobSpawnerBaseLogic()
    {
        private static final String __OBFID = "CL_00000361";
        public void func_98267_a(int par1)
        {
            TileEntityMobSpawner.this.field_145850_b.func_147452_c(TileEntityMobSpawner.this.field_145851_c, TileEntityMobSpawner.this.field_145848_d, TileEntityMobSpawner.this.field_145849_e, Blocks.mob_spawner, par1, 0);
        }
        public World getSpawnerWorld()
        {
            return TileEntityMobSpawner.this.field_145850_b;
        }
        public int getSpawnerX()
        {
            return TileEntityMobSpawner.this.field_145851_c;
        }
        public int getSpawnerY()
        {
            return TileEntityMobSpawner.this.field_145848_d;
        }
        public int getSpawnerZ()
        {
            return TileEntityMobSpawner.this.field_145849_e;
        }
        public void setRandomMinecart(MobSpawnerBaseLogic.WeightedRandomMinecart par1WeightedRandomMinecart)
        {
            super.setRandomMinecart(par1WeightedRandomMinecart);

            if (this.getSpawnerWorld() != null)
            {
                this.getSpawnerWorld().func_147471_g(TileEntityMobSpawner.this.field_145851_c, TileEntityMobSpawner.this.field_145848_d, TileEntityMobSpawner.this.field_145849_e);
            }
        }
    };
    private static final String __OBFID = "CL_00000360";

    public void func_145839_a(NBTTagCompound p_145839_1_)
    {
        super.func_145839_a(p_145839_1_);
        this.field_145882_a.readFromNBT(p_145839_1_);
    }

    public void func_145841_b(NBTTagCompound p_145841_1_)
    {
        super.func_145841_b(p_145841_1_);
        this.field_145882_a.writeToNBT(p_145841_1_);
    }

    public void func_145845_h()
    {
        this.field_145882_a.updateSpawner();
        super.func_145845_h();
    }

    public Packet func_145844_m()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.func_145841_b(nbttagcompound);
        nbttagcompound.removeTag("SpawnPotentials");
        return new S35PacketUpdateTileEntity(this.field_145851_c, this.field_145848_d, this.field_145849_e, 1, nbttagcompound);
    }

    public boolean func_145842_c(int p_145842_1_, int p_145842_2_)
    {
        return this.field_145882_a.setDelayToMin(p_145842_1_) ? true : super.func_145842_c(p_145842_1_, p_145842_2_);
    }

    public MobSpawnerBaseLogic func_145881_a()
    {
        return this.field_145882_a;
    }
}