package net.minecraft.src;

public class EntityMagmaCube extends EntitySlime
{
    public EntityMagmaCube(World par1World)
    {
        super(par1World);
        this.texture = "/mob/lava.png";
        this.isImmuneToFire = true;
        this.landMovementFactor = 0.2F;
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere()
    {
        return this.worldObj.difficultySetting > 0 && this.worldObj.checkIfAABBIsClear(this.boundingBox) && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).size() == 0 && !this.worldObj.isAnyLiquid(this.boundingBox);
    }

    /**
     * Returns the current armor value as determined by a call to InventoryPlayer.getTotalArmorValue
     */
    public int getTotalArmorValue()
    {
        return this.getSlimeSize() * 3;
    }

    /**
     * Gets how bright this entity is.
     */
    public float getEntityBrightness(float par1)
    {
        return 1.0F;
    }

    /**
     * Returns the name of a particle effect that may be randomly created by EntitySlime.onUpdate()
     */
    protected String getSlimeParticle()
    {
        return "flame";
    }

    protected EntitySlime createInstance()
    {
        return new EntityMagmaCube(this.worldObj);
    }

    /**
     * Returns the item ID for the item the mob drops on death.
     */
    protected int getDropItemId()
    {
        return Item.magmaCream.shiftedIndex;
    }

    /**
     * Drop 0-2 items of this living's type
     */
    protected void dropFewItems(boolean par1, int par2)
    {
        int var3 = this.getDropItemId();

        if (var3 > 0 && this.getSlimeSize() > 1)
        {
            int var4 = this.rand.nextInt(4) - 2;

            if (par2 > 0)
            {
                var4 += this.rand.nextInt(par2 + 1);
            }

            for (int var5 = 0; var5 < var4; ++var5)
            {
                this.dropItem(var3, 1);
            }
        }
    }

    /**
     * Returns true if the furnace is currently burning
     */
    public boolean isBurning()
    {
        return false;
    }

    protected int func_40115_A()
    {
        return super.func_40115_A() * 4;
    }

    protected void func_40116_B()
    {
        this.field_40122_a *= 0.9F;
    }

    /**
     * causes this entity to jump (or at least move upwards)
     */
    protected void jump()
    {
        this.motionY = (double)(0.42F + (float)this.getSlimeSize() * 0.1F);
        this.isAirBorne = true;
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    protected void fall(float par1) {}

    protected boolean func_40119_C()
    {
        return true;
    }

    protected int func_40113_D()
    {
        return super.func_40113_D() + 2;
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.slime";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.slime";
    }

    protected String func_40118_E()
    {
        return this.getSlimeSize() > 1 ? "mob.magmacube.big" : "mob.magmacube.small";
    }

    public boolean handleLavaMovement()
    {
        return false;
    }

    protected boolean func_40121_G()
    {
        return true;
    }
}
