package net.minecraft.src;

class VillageAgressor
{
    public EntityLiving agressor;
    public int agressionTime;

    final Village villageObj;

    VillageAgressor(Village par1Village, EntityLiving par2EntityLiving, int par3)
    {
        this.villageObj = par1Village;
        this.agressor = par2EntityLiving;
        this.agressionTime = par3;
    }
}
