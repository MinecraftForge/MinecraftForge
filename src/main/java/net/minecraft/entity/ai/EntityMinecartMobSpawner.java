package net.minecraft.entity.ai;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityMinecartMobSpawner extends EntityMinecart
{
    // JAVADOC FIELD $$ field_98040_a
    private final MobSpawnerBaseLogic mobSpawnerLogic = new MobSpawnerBaseLogic()
    {
        private static final String __OBFID = "CL_00001679";
        public void func_98267_a(int par1)
        {
            EntityMinecartMobSpawner.this.worldObj.setEntityState(EntityMinecartMobSpawner.this, (byte)par1);
        }
        public World getSpawnerWorld()
        {
            return EntityMinecartMobSpawner.this.worldObj;
        }
        public int getSpawnerX()
        {
            return MathHelper.floor_double(EntityMinecartMobSpawner.this.posX);
        }
        public int getSpawnerY()
        {
            return MathHelper.floor_double(EntityMinecartMobSpawner.this.posY);
        }
        public int getSpawnerZ()
        {
            return MathHelper.floor_double(EntityMinecartMobSpawner.this.posZ);
        }
    };
    private static final String __OBFID = "CL_00001678";

    public EntityMinecartMobSpawner(World par1World)
    {
        super(par1World);
    }

    public EntityMinecartMobSpawner(World par1World, double par2, double par4, double par6)
    {
        super(par1World, par2, par4, par6);
    }

    public int getMinecartType()
    {
        return 4;
    }

    public Block func_145817_o()
    {
        return Blocks.mob_spawner;
    }

    // JAVADOC METHOD $$ func_70037_a
    protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        this.mobSpawnerLogic.readFromNBT(par1NBTTagCompound);
    }

    // JAVADOC METHOD $$ func_70014_b
    protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        this.mobSpawnerLogic.writeToNBT(par1NBTTagCompound);
    }

    @SideOnly(Side.CLIENT)
    public void handleHealthUpdate(byte par1)
    {
        this.mobSpawnerLogic.setDelayToMin(par1);
    }

    // JAVADOC METHOD $$ func_70071_h_
    public void onUpdate()
    {
        super.onUpdate();
        this.mobSpawnerLogic.updateSpawner();
    }

    @SideOnly(Side.CLIENT)
    public MobSpawnerBaseLogic func_98039_d()
    {
        return this.mobSpawnerLogic;
    }
}