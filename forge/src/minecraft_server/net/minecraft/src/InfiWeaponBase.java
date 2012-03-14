package net.minecraft.src;

public class InfiWeaponBase extends ItemSword
{
    private static Material materialEffectiveAgainst[];
    public float efficiencyOnProperMaterial;
    public int toolHarvestLevel;
    public boolean toolIsPick;
    private int damageVsEntity;
    public int type1;
    public int type2;
    public int dur;

    public InfiWeaponBase(int i, int j, int k, int durability, float f, int i1, boolean flag,
            Material amaterial[])
    {
        //super(i);
    	super(i, EnumToolMaterial.WOOD);
        efficiencyOnProperMaterial = 4F;
        materialEffectiveAgainst = amaterial;
        toolHarvestLevel = k;
        maxStackSize = 1;
        setMaxDamage(durability);
        efficiencyOnProperMaterial = f;
        damageVsEntity = j + i1;
        toolIsPick = flag;
        type1 = 0;
        type2 = 0;
        dur = durability;
    }

    public InfiWeaponBase(int i, int j, int k, int durability, float f, int i1, boolean flag,
            Material amaterial[], int j1, int k1)
    {
        //super(i);
    	super(i, EnumToolMaterial.WOOD);
        efficiencyOnProperMaterial = 4F;
        materialEffectiveAgainst = amaterial;
        toolHarvestLevel = k;
        maxStackSize = 1;
        setMaxDamage(durability);
        efficiencyOnProperMaterial = f;
        damageVsEntity = j + i1;
        toolIsPick = flag;
        type1 = j1;
        type2 = k1;
        dur = durability;
    }

    public boolean hitEntity(ItemStack itemstack, EntityLiving entityliving, EntityLiving entityliving1)
    {
        itemstack.damageItem(2, entityliving1);
        return true;
    }

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
    
    public int getHeadType() {
    	return type1;
    }
    
    public int getHandleType() {
    	return type2;
    }
}
