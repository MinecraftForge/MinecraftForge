package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;

public class EntityPainting extends Entity
{
    private int tickCounter1;

    /** the direction the painting faces */
    public int direction;
    public int xPosition;
    public int yPosition;
    public int zPosition;
    public EnumArt art;

    public EntityPainting(World par1World)
    {
        super(par1World);
        this.tickCounter1 = 0;
        this.direction = 0;
        this.yOffset = 0.0F;
        this.setSize(0.5F, 0.5F);
    }

    public EntityPainting(World par1World, int par2, int par3, int par4, int par5)
    {
        this(par1World);
        this.xPosition = par2;
        this.yPosition = par3;
        this.zPosition = par4;
        ArrayList var6 = new ArrayList();
        EnumArt[] var7 = EnumArt.values();
        int var8 = var7.length;

        for (int var9 = 0; var9 < var8; ++var9)
        {
            EnumArt var10 = var7[var9];
            this.art = var10;
            this.func_412_b(par5);

            if (this.onValidSurface())
            {
                var6.add(var10);
            }
        }

        if (var6.size() > 0)
        {
            this.art = (EnumArt)var6.get(this.rand.nextInt(var6.size()));
        }

        this.func_412_b(par5);
    }

    public EntityPainting(World par1World, int par2, int par3, int par4, int par5, String par6Str)
    {
        this(par1World);
        this.xPosition = par2;
        this.yPosition = par3;
        this.zPosition = par4;
        EnumArt[] var7 = EnumArt.values();
        int var8 = var7.length;

        for (int var9 = 0; var9 < var8; ++var9)
        {
            EnumArt var10 = var7[var9];

            if (var10.title.equals(par6Str))
            {
                this.art = var10;
                break;
            }
        }

        this.func_412_b(par5);
    }

    protected void entityInit() {}

    public void func_412_b(int par1)
    {
        this.direction = par1;
        this.prevRotationYaw = this.rotationYaw = (float)(par1 * 90);
        float var2 = (float)this.art.sizeX;
        float var3 = (float)this.art.sizeY;
        float var4 = (float)this.art.sizeX;

        if (par1 != 0 && par1 != 2)
        {
            var2 = 0.5F;
        }
        else
        {
            var4 = 0.5F;
        }

        var2 /= 32.0F;
        var3 /= 32.0F;
        var4 /= 32.0F;
        float var5 = (float)this.xPosition + 0.5F;
        float var6 = (float)this.yPosition + 0.5F;
        float var7 = (float)this.zPosition + 0.5F;
        float var8 = 0.5625F;

        if (par1 == 0)
        {
            var7 -= var8;
        }

        if (par1 == 1)
        {
            var5 -= var8;
        }

        if (par1 == 2)
        {
            var7 += var8;
        }

        if (par1 == 3)
        {
            var5 += var8;
        }

        if (par1 == 0)
        {
            var5 -= this.func_411_c(this.art.sizeX);
        }

        if (par1 == 1)
        {
            var7 += this.func_411_c(this.art.sizeX);
        }

        if (par1 == 2)
        {
            var5 += this.func_411_c(this.art.sizeX);
        }

        if (par1 == 3)
        {
            var7 -= this.func_411_c(this.art.sizeX);
        }

        var6 += this.func_411_c(this.art.sizeY);
        this.setPosition((double)var5, (double)var6, (double)var7);
        float var9 = -0.00625F;
        this.boundingBox.setBounds((double)(var5 - var2 - var9), (double)(var6 - var3 - var9), (double)(var7 - var4 - var9), (double)(var5 + var2 + var9), (double)(var6 + var3 + var9), (double)(var7 + var4 + var9));
    }

    private float func_411_c(int par1)
    {
        return par1 == 32 ? 0.5F : (par1 == 64 ? 0.5F : 0.0F);
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        if (this.tickCounter1++ == 100 && !this.worldObj.isRemote)
        {
            this.tickCounter1 = 0;

            if (!this.onValidSurface())
            {
                this.setEntityDead();
                this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(Item.painting)));
            }
        }
    }

    public boolean onValidSurface()
    {
        if (this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).size() > 0)
        {
            return false;
        }
        else
        {
            int var1 = this.art.sizeX / 16;
            int var2 = this.art.sizeY / 16;
            int var3 = this.xPosition;
            int var4 = this.yPosition;
            int var5 = this.zPosition;

            if (this.direction == 0)
            {
                var3 = MathHelper.floor_double(this.posX - (double)((float)this.art.sizeX / 32.0F));
            }

            if (this.direction == 1)
            {
                var5 = MathHelper.floor_double(this.posZ - (double)((float)this.art.sizeX / 32.0F));
            }

            if (this.direction == 2)
            {
                var3 = MathHelper.floor_double(this.posX - (double)((float)this.art.sizeX / 32.0F));
            }

            if (this.direction == 3)
            {
                var5 = MathHelper.floor_double(this.posZ - (double)((float)this.art.sizeX / 32.0F));
            }

            var4 = MathHelper.floor_double(this.posY - (double)((float)this.art.sizeY / 32.0F));
            int var7;

            for (int var6 = 0; var6 < var1; ++var6)
            {
                for (var7 = 0; var7 < var2; ++var7)
                {
                    Material var8;

                    if (this.direction != 0 && this.direction != 2)
                    {
                        var8 = this.worldObj.getBlockMaterial(this.xPosition, var4 + var7, var5 + var6);
                    }
                    else
                    {
                        var8 = this.worldObj.getBlockMaterial(var3 + var6, var4 + var7, this.zPosition);
                    }

                    if (!var8.isSolid())
                    {
                        return false;
                    }
                }
            }

            List var9 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox);

            for (var7 = 0; var7 < var9.size(); ++var7)
            {
                if (var9.get(var7) instanceof EntityPainting)
                {
                    return false;
                }
            }

            return true;
        }
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return true;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2)
    {
        if (!this.isDead && !this.worldObj.isRemote)
        {
            this.setEntityDead();
            this.setBeenAttacked();
            this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(Item.painting)));
        }

        return true;
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setByte("Dir", (byte)this.direction);
        par1NBTTagCompound.setString("Motive", this.art.title);
        par1NBTTagCompound.setInteger("TileX", this.xPosition);
        par1NBTTagCompound.setInteger("TileY", this.yPosition);
        par1NBTTagCompound.setInteger("TileZ", this.zPosition);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        this.direction = par1NBTTagCompound.getByte("Dir");
        this.xPosition = par1NBTTagCompound.getInteger("TileX");
        this.yPosition = par1NBTTagCompound.getInteger("TileY");
        this.zPosition = par1NBTTagCompound.getInteger("TileZ");
        String var2 = par1NBTTagCompound.getString("Motive");
        EnumArt[] var3 = EnumArt.values();
        int var4 = var3.length;

        for (int var5 = 0; var5 < var4; ++var5)
        {
            EnumArt var6 = var3[var5];

            if (var6.title.equals(var2))
            {
                this.art = var6;
            }
        }

        if (this.art == null)
        {
            this.art = EnumArt.Kebab;
        }

        this.func_412_b(this.direction);
    }

    /**
     * Tries to moves the entity by the passed in displacement. Args: x, y, z
     */
    public void moveEntity(double par1, double par3, double par5)
    {
        if (!this.worldObj.isRemote && par1 * par1 + par3 * par3 + par5 * par5 > 0.0D)
        {
            this.setEntityDead();
            this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(Item.painting)));
        }
    }

    /**
     * Adds to the current velocity of the entity. Args: x, y, z
     */
    public void addVelocity(double par1, double par3, double par5)
    {
        if (!this.worldObj.isRemote && par1 * par1 + par3 * par3 + par5 * par5 > 0.0D)
        {
            this.setEntityDead();
            this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(Item.painting)));
        }
    }
}
