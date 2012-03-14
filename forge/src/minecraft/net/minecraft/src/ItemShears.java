package net.minecraft.src;

import java.util.ArrayList;
import net.minecraft.src.forge.IShearable;

public class ItemShears extends Item
{
    public ItemShears(int par1)
    {
        super(par1);
        this.setMaxStackSize(1);
        this.setMaxDamage(238);
    }

    public boolean onBlockDestroyed(ItemStack par1ItemStack, int par2, int par3, int par4, int par5, EntityLiving par6EntityLiving)
    {
        if (par2 != Block.leaves.blockID && par2 != Block.web.blockID && par2 != Block.tallGrass.blockID && par2 != Block.vine.blockID && !(Block.blocksList[par2] instanceof IShearable))
        {
            return super.onBlockDestroyed(par1ItemStack, par2, par3, par4, par5, par6EntityLiving);
        }
        else
        {
            return true;
        }
    }

    /**
     * Returns if the item (tool) can harvest results from the block type.
     */
    public boolean canHarvestBlock(Block par1Block)
    {
        return par1Block.blockID == Block.web.blockID;
    }

    /**
     * Returns the strength of the stack against a given block. 1.0F base, (Quality+1)*2 if correct blocktype, 1.5F if
     * sword
     */
    public float getStrVsBlock(ItemStack par1ItemStack, Block par2Block)
    {
        return par2Block.blockID != Block.web.blockID && par2Block.blockID != Block.leaves.blockID ? (par2Block.blockID == Block.cloth.blockID ? 5.0F : super.getStrVsBlock(par1ItemStack, par2Block)) : 15.0F;
    }
    
    @Override
    public void useItemOnEntity(ItemStack itemstack, EntityLiving entity)
    {
        if (entity.worldObj.isRemote)
        {
            return;
        }
        if (entity instanceof IShearable)
        {
            IShearable target = (IShearable)entity;
            if (target.isShearable(itemstack, entity.worldObj, (int)entity.posX, (int)entity.posY, (int)entity.posZ))
            {
                ArrayList<ItemStack> drops = target.onSheared(itemstack, entity.worldObj, (int)entity.posX, (int)entity.posY, (int)entity.posZ,
                        EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemstack));
                for(ItemStack stack : drops)
                {
                    EntityItem ent = entity.entityDropItem(stack, 1.0F);
                    ent.motionY += entity.rand.nextFloat() * 0.05F;
                    ent.motionX += (entity.rand.nextFloat() - entity.rand.nextFloat()) * 0.1F;
                    ent.motionZ += (entity.rand.nextFloat() - entity.rand.nextFloat()) * 0.1F;
                }
                itemstack.damageItem(1, entity);
            }
        }
    }
    
    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player) 
    {
        if (player.worldObj.isRemote)
        {
            return false;
        }
        int id = player.worldObj.getBlockId(x, y, z);
        if (Block.blocksList[id] != null && Block.blocksList[id] instanceof IShearable)
        {
            IShearable target = (IShearable)Block.blocksList[id];
            if (target.isShearable(itemstack, player.worldObj, x, y, z))
            {
                ArrayList<ItemStack> drops = target.onSheared(itemstack, player.worldObj, x, y, z,
                        EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemstack));
                for(ItemStack stack : drops)
                {
                    float f = 0.7F;
                    double d  = (double)(player.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    double d1 = (double)(player.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    double d2 = (double)(player.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    EntityItem entityitem = new EntityItem(player.worldObj, (double)x + d, (double)y + d1, (double)z + d2, stack);
                    entityitem.delayBeforeCanPickup = 10;
                    player.worldObj.spawnEntityInWorld(entityitem);
                }
                itemstack.damageItem(1, player);
                player.addStat(StatList.mineBlockStatArray[id], 1);
            }
        }
        return false;
    }
}
