package net.minecraft.src;

public class EntityDamageSource extends DamageSource
{
    protected Entity damageSourceEntity;

    public EntityDamageSource(String par1Str, Entity par2Entity)
    {
        super(par1Str);
        this.damageSourceEntity = par2Entity;
    }

    public Entity getEntity()
    {
        return this.damageSourceEntity;
    }

    /**
     * Returns the message to be displayed on player death.
     */
    public String getDeathMessage(EntityPlayer par1EntityPlayer)
    {
        return StatCollector.translateToLocalFormatted("death." + this.damageType, new Object[] {par1EntityPlayer.username, this.damageSourceEntity.getUsername()});
    }
}
