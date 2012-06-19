package net.minecraft.src.forge.adaptors;

import java.util.ArrayList;

import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.World;
import net.minecraft.src.forge.IEntityLivingHandler;

public class EntityLivingHandlerAdaptor implements IEntityLivingHandler 
{

    @Override
    public boolean onEntityLivingSpawn(EntityLiving entity, World world, float x, float y, float z) 
    {
        return false;
    }

    @Override
    public boolean onEntityLivingDeath(EntityLiving entity, DamageSource killer)
    {
        return false;
    }

    @Override
    public void onEntityLivingSetAttackTarget(EntityLiving entity, EntityLiving target) 
    {
    }

    @Override
    public boolean onEntityLivingAttacked(EntityLiving entity, DamageSource attack, int damage) 
    {
        return false;
    }

    @Override
    public void onEntityLivingJump(EntityLiving entity)
    {
    }

    @Override
    public boolean onEntityLivingFall(EntityLiving entity, float distance)
    {
        return false;
    }

    @Override
    public boolean onEntityLivingUpdate(EntityLiving entity)
    {
        return false;
    }

    @Override
    public int onEntityLivingHurt(EntityLiving entity, DamageSource source, int damage)
    {
        return damage;
    }

    @Override
    public void onEntityLivingDrops(EntityLiving entity, DamageSource source, ArrayList<EntityItem> drops, int lootingLevel, boolean recentlyHit, int specialDropValue) 
    {   
    }
}
