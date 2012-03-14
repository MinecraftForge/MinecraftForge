package net.minecraft.src;

import java.util.Arrays;
import java.util.List;

import net.minecraft.src.forge.ForgeHooks;

public class InfiToolBase extends ItemTool
{

    public InfiToolBase(int itemID, int damageBase, int harvestLevel, int durability, float speed, int damageBonus, 
		boolean flag, Material amaterial[])
    {
        //super(itemID);
    	super(itemID, damageBase, EnumToolMaterial.WOOD, null);
        //efficiencyOnProperMaterial = 4F;
        materialEffectiveAgainst = amaterial;
        toolHarvestLevel = harvestLevel;
        maxStackSize = 1;
        setMaxDamage(durability);
        efficiencyOnProperMaterial = speed;
        damageVsEntity = damageBase + damageBonus;
        toolIsPick = flag;
        type1 = 0;
        type2 = 0;
        dur = durability;
    }
	
	public InfiToolBase(int itemID, int damageBase, int harvestLevel, int durability, float speed, int damageBonus, 
		boolean flag, Material amaterial[], int mat1, int mat2)
    {
        //super(itemID);
		super(itemID, damageBase, EnumToolMaterial.WOOD, null);
        //efficiencyOnProperMaterial = 4F;
        materialEffectiveAgainst = amaterial;
        toolHarvestLevel = harvestLevel;
        maxStackSize = 1;
        setMaxDamage(durability);
        efficiencyOnProperMaterial = speed;
        damageVsEntity = damageBase + damageBonus;
        toolIsPick = flag;
		type1 = mat1;
		type2 = mat2;
		dur = durability;
    }

	@Override
    public boolean hitEntity(ItemStack itemstack, EntityLiving entityliving, EntityLiving entityliving1)
    {
        itemstack.damageItem(2, entityliving1);
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack itemstack, int i, int j, int k, int l, EntityLiving entityliving)
    {
        int i1 = itemstack.getItemDamage();
        if (i1 >= dur)
        {
            itemstack.stackSize = 0;
        }
        itemstack.damageItem(1, entityliving);
        return true;
    }
    
    public float getStrVsBlock(ItemStack itemstack, Block block, int md)
	{
		for (int i = 0; i < materialEffectiveAgainst.length; i++)
		{
			if (materialEffectiveAgainst[i] == block.blockMaterial)
			{
				if (type1 == 2 || type1 == 8 || type1 == 10 || type1 == 12 || type1 == 14 || type1 == 15 || type1 == 17)
				{
					return efficiencyOnProperMaterial + (float)itemstack.getItemDamage() / 100F;
				}
				else
				{
					return efficiencyOnProperMaterial;
				}
			}
		}

		return 1.0F;
	}

    public int getDamageVsEntity(Entity entity)
    {
        return damageVsEntity;
    }

    public boolean isFull3D()
    {
        return true;
    }
    
    @Override
    public int getItemEnchantability()
    {
        return mod_InfiTools.enchantBase(type1);
    }
    
    public int getHeadType() {
    	return type1;
    }
    
    public int getHandleType() {
    	return type2;
    }

    private static Material materialEffectiveAgainst[];
    public float efficiencyOnProperMaterial;
    public int toolHarvestLevel;
    public boolean toolIsPick;
    private int damageVsEntity;
	public int type1;
	public int type2;
	public int dur;
}