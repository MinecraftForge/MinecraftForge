package net.minecraft.src.blocks;
import net.minecraft.src.*;
import net.minecraft.src.forge.*;
import java.util.Random;

public class Chisel extends Item
	implements ITextureProvider
{
	
    public Chisel(int id, int durability)
    {
        super(id);
        maxStackSize = 1;
        setMaxDamage(durability);
    }
    
    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer)
    {
        if((entityplayer.capabilities.depleteBuckets)) {
        	useItemInCreative(itemstack, world, entityplayer);
        } else {
        	entityplayer.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
        }
        return itemstack;
    }
    
    public ItemStack onFoodEaten(ItemStack itemstack, World world, EntityPlayer entityplayer)
    {
    	entityplayer.swingItem();
    	MovingObjectPosition movingobjectposition = getMovingObjectPositionFromPlayer(world, entityplayer, true);
        if (movingobjectposition == null)
        {
            return itemstack;
        }
        if (movingobjectposition.typeOfHit == EnumMovingObjectType.TILE)
        {
            int x = movingobjectposition.blockX;
            int y = movingobjectposition.blockY;
            int z = movingobjectposition.blockZ;
            int bID = world.getBlockId(x, y, z);
            int md = world.getBlockMetadata(x, y, z);
            
            boolean damageItem = DetailManager.getInstance().detail(world, x, y, z, bID, md);
            if(damageItem) {
            	itemstack.damageItem(1, entityplayer);
            	world.playAuxSFX(2001, x, y, z, bID + md * 256);
            }
        }
        
        return itemstack;
    }
    
    public ItemStack useItemInCreative(ItemStack itemstack, World world, EntityPlayer entityplayer)
    {
    	MovingObjectPosition movingobjectposition = getMovingObjectPositionFromPlayer(world, entityplayer, true);
        if (movingobjectposition == null)
        {
            return itemstack;
        }
        if (movingobjectposition.typeOfHit == EnumMovingObjectType.TILE)
        {
            int x = movingobjectposition.blockX;
            int y = movingobjectposition.blockY;
            int z = movingobjectposition.blockZ;
            int bID = world.getBlockId(x, y, z);
            int md = world.getBlockMetadata(x, y, z);
            
            boolean damageItem = DetailManager.getInstance().detail(world, x, y, z, bID, md);
            if(damageItem) {
            	world.playAuxSFX(2001, x, y, z, bID + md * 256);
            	entityplayer.swingItem();
            }
        }
        
        return itemstack;
    }
    
    public int getMaxItemUseDuration(ItemStack itemstack)
    {
        return 15;
    }
    
    public EnumAction getItemUseAction(ItemStack itemstack)
    {
        return EnumAction.bow;
    }
    
    public void onUsingItemTick(ItemStack stack, EntityPlayer player, int count) 
    {
    	
    }

    /*public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer)
    {
        MovingObjectPosition movingobjectposition = getMovingObjectPositionFromPlayer(world, entityplayer, true);
        if (movingobjectposition == null)
        {
            return itemstack;
        }
        if (movingobjectposition.typeOfHit == EnumMovingObjectType.TILE)
        {
            int x = movingobjectposition.blockX;
            int y = movingobjectposition.blockY;
            int z = movingobjectposition.blockZ;
            int bID = world.getBlockId(x, y, z);
            int md = world.getBlockMetadata(x, y, z);
            
            //boolean damageItem = replaceBlock(world, x, y, z, bID, md);
            boolean damageItem = DetailManager.getInstance().detail(world, x, y, z, bID, md);
            if(damageItem) {
            	itemstack.damageItem(1, entityplayer);
            	world.playAuxSFX(2001, x, y, z, bID + md * 256);
            	entityplayer.swingItem();
            }
        }
        return itemstack;
    }*/
    
    public String getTextureFile()
    {
        return "/infiblocks/infiblocks.png";
    }
}
