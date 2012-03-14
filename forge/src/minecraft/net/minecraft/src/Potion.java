package net.minecraft.src;

public class Potion
{
    public static final Potion[] potionTypes = new Potion[32];
    public static final Potion field_35676_b = null;
    public static final Potion moveSpeed = (new Potion(1, false, 8171462)).setPotionName("potion.moveSpeed").setIconIndex(0, 0);
    public static final Potion moveSlowdown = (new Potion(2, true, 5926017)).setPotionName("potion.moveSlowdown").setIconIndex(1, 0);
    public static final Potion digSpeed = (new Potion(3, false, 14270531)).setPotionName("potion.digSpeed").setIconIndex(2, 0).setEffectiveness(1.5D);
    public static final Potion digSlowdown = (new Potion(4, true, 4866583)).setPotionName("potion.digSlowDown").setIconIndex(3, 0);
    public static final Potion damageBoost = (new Potion(5, false, 9643043)).setPotionName("potion.damageBoost").setIconIndex(4, 0);
    public static final Potion heal = (new PotionHealth(6, false, 16262179)).setPotionName("potion.heal");
    public static final Potion harm = (new PotionHealth(7, true, 4393481)).setPotionName("potion.harm");
    public static final Potion jump = (new Potion(8, false, 7889559)).setPotionName("potion.jump").setIconIndex(2, 1);
    public static final Potion confusion = (new Potion(9, true, 5578058)).setPotionName("potion.confusion").setIconIndex(3, 1).setEffectiveness(0.25D);
    public static final Potion regeneration = (new Potion(10, false, 13458603)).setPotionName("potion.regeneration").setIconIndex(7, 0).setEffectiveness(0.25D);
    public static final Potion resistance = (new Potion(11, false, 10044730)).setPotionName("potion.resistance").setIconIndex(6, 1);
    public static final Potion fireResistance = (new Potion(12, false, 14981690)).setPotionName("potion.fireResistance").setIconIndex(7, 1);
    public static final Potion waterBreathing = (new Potion(13, false, 3035801)).setPotionName("potion.waterBreathing").setIconIndex(0, 2);
    public static final Potion invisibility = (new Potion(14, false, 8356754)).setPotionName("potion.invisibility").setIconIndex(0, 1).setPotionUnusable();
    public static final Potion blindness = (new Potion(15, true, 2039587)).setPotionName("potion.blindness").setIconIndex(5, 1).setEffectiveness(0.25D);
    public static final Potion nightVision = (new Potion(16, false, 2039713)).setPotionName("potion.nightVision").setIconIndex(4, 1).setPotionUnusable();
    public static final Potion hunger = (new Potion(17, true, 5797459)).setPotionName("potion.hunger").setIconIndex(1, 1);
    public static final Potion weakness = (new Potion(18, true, 4738376)).setPotionName("potion.weakness").setIconIndex(5, 0);
    public static final Potion poison = (new Potion(19, true, 5149489)).setPotionName("potion.poison").setIconIndex(6, 0).setEffectiveness(0.25D);
    public static final Potion field_35688_v = null;
    public static final Potion field_35687_w = null;
    public static final Potion field_35697_x = null;
    public static final Potion field_35696_y = null;
    public static final Potion field_35695_z = null;
    public static final Potion field_35667_A = null;
    public static final Potion field_35668_B = null;
    public static final Potion field_35669_C = null;
    public static final Potion field_35663_D = null;
    public static final Potion field_35664_E = null;
    public static final Potion field_35665_F = null;
    public static final Potion field_35666_G = null;
    public final int id;
    private String name = "";

    /** The index for the icon displayed when the potion effect is active. */
    private int statusIconIndex = -1;

    /**
     * This field indicated if the effect is 'bad' - negative - for the entity.
     */
    private final boolean isBadEffect;
    private double effectiveness;
    private boolean usable;

    /** Is the color of the liquid for this potion. */
    private final int liquidColor;

    protected Potion(int par1, boolean par2, int par3)
    {
        this.id = par1;
        potionTypes[par1] = this;
        this.isBadEffect = par2;

        if (par2)
        {
            this.effectiveness = 0.5D;
        }
        else
        {
            this.effectiveness = 1.0D;
        }

        this.liquidColor = par3;
    }

    /**
     * Sets the index for the icon displayed in the player's inventory when the status is active.
     */
    protected Potion setIconIndex(int par1, int par2)
    {
        this.statusIconIndex = par1 + par2 * 8;
        return this;
    }

    /**
     * returns the ID of the potion
     */
    public int getId()
    {
        return this.id;
    }

    public void performEffect(EntityLiving par1EntityLiving, int par2)
    {
        if (this.id == regeneration.id)
        {
            if (par1EntityLiving.getEntityHealth() < par1EntityLiving.getMaxHealth())
            {
                par1EntityLiving.heal(1);
            }
        }
        else if (this.id == poison.id)
        {
            if (par1EntityLiving.getEntityHealth() > 1)
            {
                par1EntityLiving.attackEntityFrom(DamageSource.magic, 1);
            }
        }
        else if (this.id == hunger.id && par1EntityLiving instanceof EntityPlayer)
        {
            ((EntityPlayer)par1EntityLiving).addExhaustion(0.025F * (float)(par2 + 1));
        }
        else if ((this.id != heal.id || par1EntityLiving.isEntityUndead()) && (this.id != harm.id || !par1EntityLiving.isEntityUndead()))
        {
            if (this.id == harm.id && !par1EntityLiving.isEntityUndead() || this.id == heal.id && par1EntityLiving.isEntityUndead())
            {
                par1EntityLiving.attackEntityFrom(DamageSource.magic, 6 << par2);
            }
        }
        else
        {
            par1EntityLiving.heal(6 << par2);
        }
    }

    /**
     * Hits the provided entity with this potion's instant effect.
     */
    public void affectEntity(EntityLiving par1EntityLiving, EntityLiving par2EntityLiving, int par3, double par4)
    {
        int var6;

        if ((this.id != heal.id || par2EntityLiving.isEntityUndead()) && (this.id != harm.id || !par2EntityLiving.isEntityUndead()))
        {
            if (this.id == harm.id && !par2EntityLiving.isEntityUndead() || this.id == heal.id && par2EntityLiving.isEntityUndead())
            {
                var6 = (int)(par4 * (double)(6 << par3) + 0.5D);

                if (par1EntityLiving == null)
                {
                    par2EntityLiving.attackEntityFrom(DamageSource.magic, var6);
                }
                else
                {
                    par2EntityLiving.attackEntityFrom(DamageSource.causeIndirectMagicDamage(par2EntityLiving, par1EntityLiving), var6);
                }
            }
        }
        else
        {
            var6 = (int)(par4 * (double)(6 << par3) + 0.5D);
            par2EntityLiving.heal(var6);
        }
    }

    /**
     * Returns true if the potion has an instant effect instead of a continuous one (eg Harming)
     */
    public boolean isInstant()
    {
        return false;
    }

    /**
     * checks if Potion effect is ready to be applied this tick.
     */
    public boolean isReady(int par1, int par2)
    {
        if (this.id != regeneration.id && this.id != poison.id)
        {
            return this.id == hunger.id;
        }
        else
        {
            int var3 = 25 >> par2;
            return var3 > 0 ? par1 % var3 == 0 : true;
        }
    }

    public Potion setPotionName(String par1Str)
    {
        this.name = par1Str;
        return this;
    }

    /**
     * returns the name of the potion
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Returns true if the potion has a associated status icon to display in then inventory when active.
     */
    public boolean hasStatusIcon()
    {
        return this.statusIconIndex >= 0;
    }

    /**
     * Returns the index for the icon to display when the potion is active.
     */
    public int getStatusIconIndex()
    {
        return this.statusIconIndex;
    }

    /**
     * This method returns true if the potion effect is bad - negative - for the entity.
     */
    public boolean isBadEffect()
    {
        return this.isBadEffect;
    }

    public static String getDurationString(PotionEffect par0PotionEffect)
    {
        int var1 = par0PotionEffect.getDuration();
        int var2 = var1 / 20;
        int var3 = var2 / 60;
        var2 %= 60;
        return var2 < 10 ? var3 + ":0" + var2 : var3 + ":" + var2;
    }

    protected Potion setEffectiveness(double par1)
    {
        this.effectiveness = par1;
        return this;
    }

    public double getEffectiveness()
    {
        return this.effectiveness;
    }

    public Potion setPotionUnusable()
    {
        this.usable = true;
        return this;
    }

    public boolean isUsable()
    {
        return this.usable;
    }

    /**
     * Returns the color of the potion liquid.
     */
    public int getLiquidColor()
    {
        return this.liquidColor;
    }
}
