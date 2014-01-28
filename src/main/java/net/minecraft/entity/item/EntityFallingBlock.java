package net.minecraft.entity.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFallingBlock extends Entity
{
    private Block field_145811_e;
    public int field_145814_a;
    public int field_145812_b;
    public boolean field_145813_c;
    private boolean field_145808_f;
    private boolean field_145809_g;
    private int field_145815_h;
    private float field_145816_i;
    public NBTTagCompound field_145810_d;
    private static final String __OBFID = "CL_00001668";

    public EntityFallingBlock(World par1World)
    {
        super(par1World);
        this.field_145813_c = true;
        this.field_145815_h = 40;
        this.field_145816_i = 2.0F;
    }

    public EntityFallingBlock(World p_i45318_1_, double p_i45318_2_, double p_i45318_4_, double p_i45318_6_, Block p_i45318_8_)
    {
        this(p_i45318_1_, p_i45318_2_, p_i45318_4_, p_i45318_6_, p_i45318_8_, 0);
    }

    public EntityFallingBlock(World p_i45319_1_, double p_i45319_2_, double p_i45319_4_, double p_i45319_6_, Block p_i45319_8_, int p_i45319_9_)
    {
        super(p_i45319_1_);
        this.field_145813_c = true;
        this.field_145815_h = 40;
        this.field_145816_i = 2.0F;
        this.field_145811_e = p_i45319_8_;
        this.field_145814_a = p_i45319_9_;
        this.preventEntitySpawning = true;
        this.setSize(0.98F, 0.98F);
        this.yOffset = this.height / 2.0F;
        this.setPosition(p_i45319_2_, p_i45319_4_, p_i45319_6_);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = p_i45319_2_;
        this.prevPosY = p_i45319_4_;
        this.prevPosZ = p_i45319_6_;
    }

    // JAVADOC METHOD $$ func_70041_e_
    protected boolean canTriggerWalking()
    {
        return false;
    }

    protected void entityInit() {}

    // JAVADOC METHOD $$ func_70067_L
    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }

    // JAVADOC METHOD $$ func_70071_h_
    public void onUpdate()
    {
        if (this.field_145811_e.func_149688_o() == Material.field_151579_a)
        {
            this.setDead();
        }
        else
        {
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            ++this.field_145812_b;
            this.motionY -= 0.03999999910593033D;
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.9800000190734863D;
            this.motionY *= 0.9800000190734863D;
            this.motionZ *= 0.9800000190734863D;

            if (!this.worldObj.isRemote)
            {
                int i = MathHelper.floor_double(this.posX);
                int j = MathHelper.floor_double(this.posY);
                int k = MathHelper.floor_double(this.posZ);

                if (this.field_145812_b == 1)
                {
                    if (this.worldObj.func_147439_a(i, j, k) != this.field_145811_e)
                    {
                        this.setDead();
                        return;
                    }

                    this.worldObj.func_147468_f(i, j, k);
                }

                if (this.onGround)
                {
                    this.motionX *= 0.699999988079071D;
                    this.motionZ *= 0.699999988079071D;
                    this.motionY *= -0.5D;

                    if (this.worldObj.func_147439_a(i, j, k) != Blocks.piston_extension)
                    {
                        this.setDead();

                        if (!this.field_145808_f && this.worldObj.func_147472_a(this.field_145811_e, i, j, k, true, 1, (Entity)null, (ItemStack)null) && !BlockFalling.func_149831_e(this.worldObj, i, j - 1, k) && this.worldObj.func_147465_d(i, j, k, this.field_145811_e, this.field_145814_a, 3))
                        {
                            if (this.field_145811_e instanceof BlockFalling)
                            {
                                ((BlockFalling)this.field_145811_e).func_149828_a(this.worldObj, i, j, k, this.field_145814_a);
                            }

                            if (this.field_145810_d != null && this.field_145811_e instanceof ITileEntityProvider)
                            {
                                TileEntity tileentity = this.worldObj.func_147438_o(i, j, k);

                                if (tileentity != null)
                                {
                                    NBTTagCompound nbttagcompound = new NBTTagCompound();
                                    tileentity.func_145841_b(nbttagcompound);
                                    Iterator iterator = this.field_145810_d.func_150296_c().iterator();

                                    while (iterator.hasNext())
                                    {
                                        String s = (String)iterator.next();
                                        NBTBase nbtbase = this.field_145810_d.getTag(s);

                                        if (!s.equals("x") && !s.equals("y") && !s.equals("z"))
                                        {
                                            nbttagcompound.setTag(s, nbtbase.copy());
                                        }
                                    }

                                    tileentity.func_145839_a(nbttagcompound);
                                    tileentity.onInventoryChanged();
                                }
                            }
                        }
                        else if (this.field_145813_c && !this.field_145808_f)
                        {
                            this.entityDropItem(new ItemStack(this.field_145811_e, 1, this.field_145811_e.func_149692_a(this.field_145814_a)), 0.0F);
                        }
                    }
                }
                else if (this.field_145812_b > 100 && !this.worldObj.isRemote && (j < 1 || j > 256) || this.field_145812_b > 600)
                {
                    if (this.field_145813_c)
                    {
                        this.entityDropItem(new ItemStack(this.field_145811_e, 1, this.field_145811_e.func_149692_a(this.field_145814_a)), 0.0F);
                    }

                    this.setDead();
                }
            }
        }
    }

    // JAVADOC METHOD $$ func_70069_a
    protected void fall(float par1)
    {
        if (this.field_145809_g)
        {
            int i = MathHelper.ceiling_float_int(par1 - 1.0F);

            if (i > 0)
            {
                ArrayList arraylist = new ArrayList(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox));
                boolean flag = this.field_145811_e == Blocks.anvil;
                DamageSource damagesource = flag ? DamageSource.anvil : DamageSource.fallingBlock;
                Iterator iterator = arraylist.iterator();

                while (iterator.hasNext())
                {
                    Entity entity = (Entity)iterator.next();
                    entity.attackEntityFrom(damagesource, (float)Math.min(MathHelper.floor_float((float)i * this.field_145816_i), this.field_145815_h));
                }

                if (flag && (double)this.rand.nextFloat() < 0.05000000074505806D + (double)i * 0.05D)
                {
                    int j = this.field_145814_a >> 2;
                    int k = this.field_145814_a & 3;
                    ++j;

                    if (j > 2)
                    {
                        this.field_145808_f = true;
                    }
                    else
                    {
                        this.field_145814_a = k | j << 2;
                    }
                }
            }
        }
    }

    // JAVADOC METHOD $$ func_70014_b
    protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setByte("Tile", (byte)Block.func_149682_b(this.field_145811_e));
        par1NBTTagCompound.setInteger("TileID", Block.func_149682_b(this.field_145811_e));
        par1NBTTagCompound.setByte("Data", (byte)this.field_145814_a);
        par1NBTTagCompound.setByte("Time", (byte)this.field_145812_b);
        par1NBTTagCompound.setBoolean("DropItem", this.field_145813_c);
        par1NBTTagCompound.setBoolean("HurtEntities", this.field_145809_g);
        par1NBTTagCompound.setFloat("FallHurtAmount", this.field_145816_i);
        par1NBTTagCompound.setInteger("FallHurtMax", this.field_145815_h);

        if (this.field_145810_d != null)
        {
            par1NBTTagCompound.setTag("TileEntityData", this.field_145810_d);
        }
    }

    // JAVADOC METHOD $$ func_70037_a
    protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        if (par1NBTTagCompound.func_150297_b("TileID", 99))
        {
            this.field_145811_e = Block.func_149729_e(par1NBTTagCompound.getInteger("TileID"));
        }
        else
        {
            this.field_145811_e = Block.func_149729_e(par1NBTTagCompound.getByte("Tile") & 255);
        }

        this.field_145814_a = par1NBTTagCompound.getByte("Data") & 255;
        this.field_145812_b = par1NBTTagCompound.getByte("Time") & 255;

        if (par1NBTTagCompound.func_150297_b("HurtEntities", 99))
        {
            this.field_145809_g = par1NBTTagCompound.getBoolean("HurtEntities");
            this.field_145816_i = par1NBTTagCompound.getFloat("FallHurtAmount");
            this.field_145815_h = par1NBTTagCompound.getInteger("FallHurtMax");
        }
        else if (this.field_145811_e == Blocks.anvil)
        {
            this.field_145809_g = true;
        }

        if (par1NBTTagCompound.func_150297_b("DropItem", 99))
        {
            this.field_145813_c = par1NBTTagCompound.getBoolean("DropItem");
        }

        if (par1NBTTagCompound.func_150297_b("TileEntityData", 10))
        {
            this.field_145810_d = par1NBTTagCompound.getCompoundTag("TileEntityData");
        }

        if (this.field_145811_e.func_149688_o() == Material.field_151579_a)
        {
            this.field_145811_e = Blocks.sand;
        }
    }

    public void func_145806_a(boolean p_145806_1_)
    {
        this.field_145809_g = p_145806_1_;
    }

    public void addEntityCrashInfo(CrashReportCategory par1CrashReportCategory)
    {
        super.addEntityCrashInfo(par1CrashReportCategory);
        par1CrashReportCategory.addCrashSection("Immitating block ID", Integer.valueOf(Block.func_149682_b(this.field_145811_e)));
        par1CrashReportCategory.addCrashSection("Immitating block data", Integer.valueOf(this.field_145814_a));
    }

    @SideOnly(Side.CLIENT)
    public float getShadowSize()
    {
        return 0.0F;
    }

    @SideOnly(Side.CLIENT)
    public World func_145807_e()
    {
        return this.worldObj;
    }

    // JAVADOC METHOD $$ func_90999_ad
    @SideOnly(Side.CLIENT)
    public boolean canRenderOnFire()
    {
        return false;
    }

    public Block func_145805_f()
    {
        return this.field_145811_e;
    }
}