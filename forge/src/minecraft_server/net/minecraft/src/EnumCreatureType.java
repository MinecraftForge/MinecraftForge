package net.minecraft.src;

public enum EnumCreatureType
{
    monster(IMob.class, 70, Material.air, false),
    creature(EntityAnimal.class, 15, Material.air, true),
    waterCreature(EntityWaterMob.class, 5, Material.water, true);

    /**
     * The root class of creatures associated with this EnumCreatureType (IMobs for aggressive creatures, EntityAnimals
     * for friendly ones)
     */
    private final Class creatureClass;
    private final int maxNumberOfCreature;
    private final Material creatureMaterial;
    private final boolean isPeacefulCreature;

    private EnumCreatureType(Class par3Class, int par4, Material par5Material, boolean par6)
    {
        this.creatureClass = par3Class;
        this.maxNumberOfCreature = par4;
        this.creatureMaterial = par5Material;
        this.isPeacefulCreature = par6;
    }

    public Class getCreatureClass()
    {
        return this.creatureClass;
    }

    public int getMaxNumberOfCreature()
    {
        return this.maxNumberOfCreature;
    }

    public Material getCreatureMaterial()
    {
        return this.creatureMaterial;
    }

    public boolean getPeacefulCreature()
    {
        return this.isPeacefulCreature;
    }
}
