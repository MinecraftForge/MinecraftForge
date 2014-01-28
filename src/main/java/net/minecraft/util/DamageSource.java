package net.minecraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.world.Explosion;

public class DamageSource
{
    public static DamageSource inFire = (new DamageSource("inFire")).setFireDamage();
    public static DamageSource onFire = (new DamageSource("onFire")).setDamageBypassesArmor().setFireDamage();
    public static DamageSource lava = (new DamageSource("lava")).setFireDamage();
    public static DamageSource inWall = (new DamageSource("inWall")).setDamageBypassesArmor();
    public static DamageSource drown = (new DamageSource("drown")).setDamageBypassesArmor();
    public static DamageSource starve = (new DamageSource("starve")).setDamageBypassesArmor().func_151518_m();
    public static DamageSource cactus = new DamageSource("cactus");
    public static DamageSource fall = (new DamageSource("fall")).setDamageBypassesArmor();
    public static DamageSource outOfWorld = (new DamageSource("outOfWorld")).setDamageBypassesArmor().setDamageAllowedInCreativeMode();
    public static DamageSource generic = (new DamageSource("generic")).setDamageBypassesArmor();
    public static DamageSource magic = (new DamageSource("magic")).setDamageBypassesArmor().setMagicDamage();
    public static DamageSource wither = (new DamageSource("wither")).setDamageBypassesArmor();
    public static DamageSource anvil = new DamageSource("anvil");
    public static DamageSource fallingBlock = new DamageSource("fallingBlock");
    // JAVADOC FIELD $$ field_76374_o
    private boolean isUnblockable;
    private boolean isDamageAllowedInCreativeMode;
    private boolean field_151520_r;
    private float hungerDamage = 0.3F;
    // JAVADOC FIELD $$ field_76383_r
    private boolean fireDamage;
    // JAVADOC FIELD $$ field_76382_s
    private boolean projectile;
    // JAVADOC FIELD $$ field_76381_t
    private boolean difficultyScaled;
    private boolean magicDamage;
    private boolean explosion;
    public String damageType;
    private static final String __OBFID = "CL_00001521";

    public static DamageSource causeMobDamage(EntityLivingBase par0EntityLivingBase)
    {
        return new EntityDamageSource("mob", par0EntityLivingBase);
    }

    // JAVADOC METHOD $$ func_76365_a
    public static DamageSource causePlayerDamage(EntityPlayer par0EntityPlayer)
    {
        return new EntityDamageSource("player", par0EntityPlayer);
    }

    // JAVADOC METHOD $$ func_76353_a
    public static DamageSource causeArrowDamage(EntityArrow par0EntityArrow, Entity par1Entity)
    {
        return (new EntityDamageSourceIndirect("arrow", par0EntityArrow, par1Entity)).setProjectile();
    }

    // JAVADOC METHOD $$ func_76362_a
    public static DamageSource causeFireballDamage(EntityFireball par0EntityFireball, Entity par1Entity)
    {
        return par1Entity == null ? (new EntityDamageSourceIndirect("onFire", par0EntityFireball, par0EntityFireball)).setFireDamage().setProjectile() : (new EntityDamageSourceIndirect("fireball", par0EntityFireball, par1Entity)).setFireDamage().setProjectile();
    }

    public static DamageSource causeThrownDamage(Entity par0Entity, Entity par1Entity)
    {
        return (new EntityDamageSourceIndirect("thrown", par0Entity, par1Entity)).setProjectile();
    }

    public static DamageSource causeIndirectMagicDamage(Entity par0Entity, Entity par1Entity)
    {
        return (new EntityDamageSourceIndirect("indirectMagic", par0Entity, par1Entity)).setDamageBypassesArmor().setMagicDamage();
    }

    // JAVADOC METHOD $$ func_92087_a
    public static DamageSource causeThornsDamage(Entity par0Entity)
    {
        return (new EntityDamageSource("thorns", par0Entity)).setMagicDamage();
    }

    public static DamageSource setExplosionSource(Explosion par0Explosion)
    {
        return par0Explosion != null && par0Explosion.getExplosivePlacedBy() != null ? (new EntityDamageSource("explosion.player", par0Explosion.getExplosivePlacedBy())).setDifficultyScaled().setExplosion() : (new DamageSource("explosion")).setDifficultyScaled().setExplosion();
    }

    // JAVADOC METHOD $$ func_76352_a
    public boolean isProjectile()
    {
        return this.projectile;
    }

    // JAVADOC METHOD $$ func_76349_b
    public DamageSource setProjectile()
    {
        this.projectile = true;
        return this;
    }

    public boolean isExplosion()
    {
        return this.explosion;
    }

    public DamageSource setExplosion()
    {
        this.explosion = true;
        return this;
    }

    public boolean isUnblockable()
    {
        return this.isUnblockable;
    }

    // JAVADOC METHOD $$ func_76345_d
    public float getHungerDamage()
    {
        return this.hungerDamage;
    }

    public boolean canHarmInCreative()
    {
        return this.isDamageAllowedInCreativeMode;
    }

    public boolean func_151517_h()
    {
        return this.field_151520_r;
    }

    public DamageSource(String par1Str)
    {
        this.damageType = par1Str;
    }

    public Entity getSourceOfDamage()
    {
        return this.getEntity();
    }

    public Entity getEntity()
    {
        return null;
    }

    public DamageSource setDamageBypassesArmor()
    {
        this.isUnblockable = true;
        this.hungerDamage = 0.0F;
        return this;
    }

    public DamageSource setDamageAllowedInCreativeMode()
    {
        this.isDamageAllowedInCreativeMode = true;
        return this;
    }

    public DamageSource func_151518_m()
    {
        this.field_151520_r = true;
        this.hungerDamage = 0.0F;
        return this;
    }

    // JAVADOC METHOD $$ func_76361_j
    public DamageSource setFireDamage()
    {
        this.fireDamage = true;
        return this;
    }

    public IChatComponent func_151519_b(EntityLivingBase p_151519_1_)
    {
        EntityLivingBase entitylivingbase1 = p_151519_1_.func_94060_bK();
        String s = "death.attack." + this.damageType;
        String s1 = s + ".player";
        return entitylivingbase1 != null && StatCollector.func_94522_b(s1) ? new ChatComponentTranslation(s1, new Object[] {p_151519_1_.func_145748_c_(), entitylivingbase1.func_145748_c_()}): new ChatComponentTranslation(s, new Object[] {p_151519_1_.func_145748_c_()});
    }

    // JAVADOC METHOD $$ func_76347_k
    public boolean isFireDamage()
    {
        return this.fireDamage;
    }

    // JAVADOC METHOD $$ func_76355_l
    public String getDamageType()
    {
        return this.damageType;
    }

    // JAVADOC METHOD $$ func_76351_m
    public DamageSource setDifficultyScaled()
    {
        this.difficultyScaled = true;
        return this;
    }

    // JAVADOC METHOD $$ func_76350_n
    public boolean isDifficultyScaled()
    {
        return this.difficultyScaled;
    }

    // JAVADOC METHOD $$ func_82725_o
    public boolean isMagicDamage()
    {
        return this.magicDamage;
    }

    // JAVADOC METHOD $$ func_82726_p
    public DamageSource setMagicDamage()
    {
        this.magicDamage = true;
        return this;
    }
}