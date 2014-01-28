package net.minecraft.entity;

import net.minecraft.block.material.Material;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;

public enum EnumCreatureType
{
    monster(IMob.class, 70, Material.field_151579_a, false, false),
    creature(EntityAnimal.class, 10, Material.field_151579_a, true, true),
    ambient(EntityAmbientCreature.class, 15, Material.field_151579_a, true, false),
    waterCreature(EntityWaterMob.class, 5, Material.field_151586_h, true, false);
    // JAVADOC FIELD $$ field_75605_d
    private final Class creatureClass;
    private final int maxNumberOfCreature;
    private final Material creatureMaterial;
    // JAVADOC FIELD $$ field_75604_g
    private final boolean isPeacefulCreature;
    // JAVADOC FIELD $$ field_82707_i
    private final boolean isAnimal;

    private static final String __OBFID = "CL_00001551";

    private EnumCreatureType(Class par3Class, int par4, Material par5Material, boolean par6, boolean par7)
    {
        this.creatureClass = par3Class;
        this.maxNumberOfCreature = par4;
        this.creatureMaterial = par5Material;
        this.isPeacefulCreature = par6;
        this.isAnimal = par7;
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

    // JAVADOC METHOD $$ func_75599_d
    public boolean getPeacefulCreature()
    {
        return this.isPeacefulCreature;
    }

    // JAVADOC METHOD $$ func_82705_e
    public boolean getAnimal()
    {
        return this.isAnimal;
    }
}