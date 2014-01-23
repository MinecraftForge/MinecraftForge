package net.minecraftforge.event.entity.player;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityEvent;

public class PlayerAquireXPEvent extends PlayerEvent
{
    public EntityXPOrb lOrb;

    public PlayerAquireXPEvent(EntityPlayer par1EntityPlayer, EntityXPOrb par2EntityXPOrb)
    {
        super(par1EntityPlayer);
        lOrb = par2EntityXPOrb;
    }
    
    public void setXP(short i)
    {
        System.out.println(lOrb);
        NBTTagCompound nbt = new NBTTagCompound();
        lOrb.writeEntityToNBT(nbt);
        
        nbt.setShort("Value", i);
        lOrb.readEntityFromNBT(nbt);
    }
    public short getXP()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        lOrb.writeEntityToNBT(nbt);
        
        return nbt.getShort("Value");
    }

}
