package net.minecraft.src;

import java.util.ArrayList;

import net.minecraft.src.forge.IShearable;

public class EntityMooshroom extends EntityCow implements IShearable
{
    public EntityMooshroom(World par1World)
    {
        super(par1World);
        this.texture = "/mob/redcow.png";
        this.setSize(0.9F, 1.3F);
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    public boolean interact(EntityPlayer par1EntityPlayer)
    {
        ItemStack var2 = par1EntityPlayer.inventory.getCurrentItem();

        if (var2 != null && var2.itemID == Item.bowlEmpty.shiftedIndex && this.getGrowingAge() >= 0)
        {
            par1EntityPlayer.inventory.setInventorySlotContents(par1EntityPlayer.inventory.currentItem, new ItemStack(Item.bowlSoup));
            return true;
        }
        else
        {
            return super.interact(par1EntityPlayer);
        }
    }

    /**
     * [This function is used when two same-species animals in 'love mode' breed to generate the new baby animal.]
     */
    public EntityAnimal spawnBabyAnimal(EntityAnimal par1EntityAnimal)
    {
        return new EntityMooshroom(this.worldObj);
    }
    
    @Override
    public boolean isShearable(ItemStack item, World world, int X, int Y, int Z) 
    {
        return getGrowingAge() >= 0;
    }

    @Override
    public ArrayList<ItemStack> onSheared(ItemStack item, World world, int X, int Y, int Z, int fortune) 
    {
        setEntityDead();
        EntityCow entitycow = new EntityCow(worldObj);
        entitycow.setLocationAndAngles(posX, posY, posZ, rotationYaw, rotationPitch);
        entitycow.setEntityHealth(getEntityHealth());
        entitycow.renderYawOffset = renderYawOffset;
        worldObj.spawnEntityInWorld(entitycow);
        worldObj.spawnParticle("largeexplode", posX, posY + (double)(height / 2.0F), posZ, 0.0D, 0.0D, 0.0D);
        
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        for (int x = 0; x < 5; x++)
        {
            ret.add(new ItemStack(Block.mushroomRed));
        }
        return ret;
    }
}
