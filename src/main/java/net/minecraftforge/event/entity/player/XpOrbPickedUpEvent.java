package net.minecraftforge.event.entity.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityEvent;

public class XpOrbPickedUpEvent extends EntityEvent
{

    public EntityXPOrb orb;
    
    public XpOrbPickedUpEvent(EntityXPOrb entity)
    {
        super(entity);
        orb = entity;
        System.out.println("EVENT WORKS");
        setXP((short)500);
    }
    public void setXP(short i)
    {
        NBTTagCompound nbt = new NBTTagCompound();
        orb.writeEntityToNBT(nbt);
        
        nbt.setShort("Value", i);
        orb.readEntityFromNBT(nbt);
    }
    public short getXP()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        orb.writeEntityToNBT(nbt);
        
        return nbt.getShort("Value");
    }

}
