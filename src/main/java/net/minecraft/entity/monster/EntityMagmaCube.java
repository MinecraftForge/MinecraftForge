package net.minecraft.entity.monster;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityMagmaCube extends EntitySlime
{
    private static final String __OBFID = "CL_00001691";

    public EntityMagmaCube(World par1World)
    {
        super(par1World);
        this.isImmuneToFire = true;
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.20000000298023224D);
    }

    // JAVADOC METHOD $$ func_70601_bi
    public boolean getCanSpawnHere()
    {
        return this.worldObj.difficultySetting != EnumDifficulty.PEACEFUL && this.worldObj.checkNoEntityCollision(this.boundingBox) && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty() && !this.worldObj.isAnyLiquid(this.boundingBox);
    }

    // JAVADOC METHOD $$ func_70658_aO
    public int getTotalArmorValue()
    {
        return this.getSlimeSize() * 3;
    }

    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float par1)
    {
        return 15728880;
    }

    // JAVADOC METHOD $$ func_70013_c
    public float getBrightness(float par1)
    {
        return 1.0F;
    }

    // JAVADOC METHOD $$ func_70801_i
    protected String getSlimeParticle()
    {
        return "flame";
    }

    protected EntitySlime createInstance()
    {
        return new EntityMagmaCube(this.worldObj);
    }

    protected Item func_146068_u()
    {
        return Items.magma_cream;
    }

    // JAVADOC METHOD $$ func_70628_a
    protected void dropFewItems(boolean par1, int par2)
    {
        Item item = this.func_146068_u();

        if (item != null && this.getSlimeSize() > 1)
        {
            int j = this.rand.nextInt(4) - 2;

            if (par2 > 0)
            {
                j += this.rand.nextInt(par2 + 1);
            }

            for (int k = 0; k < j; ++k)
            {
                this.func_145779_a(item, 1);
            }
        }
    }

    // JAVADOC METHOD $$ func_70027_ad
    public boolean isBurning()
    {
        return false;
    }

    // JAVADOC METHOD $$ func_70806_k
    protected int getJumpDelay()
    {
        return super.getJumpDelay() * 4;
    }

    protected void alterSquishAmount()
    {
        this.squishAmount *= 0.9F;
    }

    // JAVADOC METHOD $$ func_70664_aZ
    protected void jump()
    {
        this.motionY = (double)(0.42F + (float)this.getSlimeSize() * 0.1F);
        this.isAirBorne = true;
    }

    // JAVADOC METHOD $$ func_70069_a
    protected void fall(float par1) {}

    // JAVADOC METHOD $$ func_70800_m
    protected boolean canDamagePlayer()
    {
        return true;
    }

    // JAVADOC METHOD $$ func_70805_n
    protected int getAttackStrength()
    {
        return super.getAttackStrength() + 2;
    }

    // JAVADOC METHOD $$ func_70803_o
    protected String getJumpSound()
    {
        return this.getSlimeSize() > 1 ? "mob.magmacube.big" : "mob.magmacube.small";
    }

    // JAVADOC METHOD $$ func_70058_J
    public boolean handleLavaMovement()
    {
        return false;
    }

    // JAVADOC METHOD $$ func_70804_p
    protected boolean makesSoundOnLand()
    {
        return true;
    }
}