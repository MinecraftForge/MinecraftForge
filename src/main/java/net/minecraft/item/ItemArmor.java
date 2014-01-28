package net.minecraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.command.IEntitySelector;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemArmor extends Item
{
    // JAVADOC FIELD $$ field_77882_bY
    private static final int[] maxDamageArray = new int[] {11, 16, 15, 13};
    private static final String[] field_94606_cu = new String[] {"leather_helmet_overlay", "leather_chestplate_overlay", "leather_leggings_overlay", "leather_boots_overlay"};
    public static final String[] field_94603_a = new String[] {"empty_armor_slot_helmet", "empty_armor_slot_chestplate", "empty_armor_slot_leggings", "empty_armor_slot_boots"};
    private static final IBehaviorDispenseItem field_96605_cw = new BehaviorDefaultDispenseItem()
    {
        private static final String __OBFID = "CL_00001767";
        // JAVADOC METHOD $$ func_82487_b
        protected ItemStack dispenseStack(IBlockSource par1IBlockSource, ItemStack par2ItemStack)
        {
            EnumFacing enumfacing = BlockDispenser.func_149937_b(par1IBlockSource.getBlockMetadata());
            int i = par1IBlockSource.getXInt() + enumfacing.getFrontOffsetX();
            int j = par1IBlockSource.getYInt() + enumfacing.getFrontOffsetY();
            int k = par1IBlockSource.getZInt() + enumfacing.getFrontOffsetZ();
            AxisAlignedBB axisalignedbb = AxisAlignedBB.getAABBPool().getAABB((double)i, (double)j, (double)k, (double)(i + 1), (double)(j + 1), (double)(k + 1));
            List list = par1IBlockSource.getWorld().selectEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb, new IEntitySelector.ArmoredMob(par2ItemStack));

            if (list.size() > 0)
            {
                EntityLivingBase entitylivingbase = (EntityLivingBase)list.get(0);
                int l = entitylivingbase instanceof EntityPlayer ? 1 : 0;
                int i1 = EntityLiving.getArmorPosition(par2ItemStack);
                ItemStack itemstack1 = par2ItemStack.copy();
                itemstack1.stackSize = 1;
                entitylivingbase.setCurrentItemOrArmor(i1 - l, itemstack1);

                if (entitylivingbase instanceof EntityLiving)
                {
                    ((EntityLiving)entitylivingbase).setEquipmentDropChance(i1, 2.0F);
                }

                --par2ItemStack.stackSize;
                return par2ItemStack;
            }
            else
            {
                return super.dispenseStack(par1IBlockSource, par2ItemStack);
            }
        }
    };
    // JAVADOC FIELD $$ field_77881_a
    public final int armorType;
    // JAVADOC FIELD $$ field_77879_b
    public final int damageReduceAmount;
    // JAVADOC FIELD $$ field_77880_c
    public final int renderIndex;
    // JAVADOC FIELD $$ field_77878_bZ
    private final ItemArmor.ArmorMaterial material;
    @SideOnly(Side.CLIENT)
    private IIcon field_94605_cw;
    @SideOnly(Side.CLIENT)
    private IIcon field_94604_cx;
    private static final String __OBFID = "CL_00001766";

    public ItemArmor(ItemArmor.ArmorMaterial p_i45325_1_, int p_i45325_2_, int p_i45325_3_)
    {
        this.material = p_i45325_1_;
        this.armorType = p_i45325_3_;
        this.renderIndex = p_i45325_2_;
        this.damageReduceAmount = p_i45325_1_.getDamageReductionAmount(p_i45325_3_);
        this.setMaxDamage(p_i45325_1_.getDurability(p_i45325_3_));
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.tabCombat);
        BlockDispenser.field_149943_a.putObject(this, field_96605_cw);
    }

    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack par1ItemStack, int par2)
    {
        if (par2 > 0)
        {
            return 16777215;
        }
        else
        {
            int j = this.getColor(par1ItemStack);

            if (j < 0)
            {
                j = 16777215;
            }

            return j;
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return this.material == ItemArmor.ArmorMaterial.CLOTH;
    }

    // JAVADOC METHOD $$ func_77619_b
    public int getItemEnchantability()
    {
        return this.material.getEnchantability();
    }

    // JAVADOC METHOD $$ func_82812_d
    public ItemArmor.ArmorMaterial getArmorMaterial()
    {
        return this.material;
    }

    // JAVADOC METHOD $$ func_82816_b_
    public boolean hasColor(ItemStack par1ItemStack)
    {
        return this.material != ItemArmor.ArmorMaterial.CLOTH ? false : (!par1ItemStack.hasTagCompound() ? false : (!par1ItemStack.getTagCompound().func_150297_b("display", 10) ? false : par1ItemStack.getTagCompound().getCompoundTag("display").func_150297_b("color", 3)));
    }

    // JAVADOC METHOD $$ func_82814_b
    public int getColor(ItemStack par1ItemStack)
    {
        if (this.material != ItemArmor.ArmorMaterial.CLOTH)
        {
            return -1;
        }
        else
        {
            NBTTagCompound nbttagcompound = par1ItemStack.getTagCompound();

            if (nbttagcompound == null)
            {
                return 10511680;
            }
            else
            {
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");
                return nbttagcompound1 == null ? 10511680 : (nbttagcompound1.func_150297_b("color", 3) ? nbttagcompound1.getInteger("color") : 10511680);
            }
        }
    }

    // JAVADOC METHOD $$ func_77618_c
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int par1, int par2)
    {
        return par2 == 1 ? this.field_94605_cw : super.getIconFromDamageForRenderPass(par1, par2);
    }

    // JAVADOC METHOD $$ func_82815_c
    public void removeColor(ItemStack par1ItemStack)
    {
        if (this.material == ItemArmor.ArmorMaterial.CLOTH)
        {
            NBTTagCompound nbttagcompound = par1ItemStack.getTagCompound();

            if (nbttagcompound != null)
            {
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");

                if (nbttagcompound1.hasKey("color"))
                {
                    nbttagcompound1.removeTag("color");
                }
            }
        }
    }

    public void func_82813_b(ItemStack par1ItemStack, int par2)
    {
        if (this.material != ItemArmor.ArmorMaterial.CLOTH)
        {
            throw new UnsupportedOperationException("Can\'t dye non-leather!");
        }
        else
        {
            NBTTagCompound nbttagcompound = par1ItemStack.getTagCompound();

            if (nbttagcompound == null)
            {
                nbttagcompound = new NBTTagCompound();
                par1ItemStack.setTagCompound(nbttagcompound);
            }

            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");

            if (!nbttagcompound.func_150297_b("display", 10))
            {
                nbttagcompound.setTag("display", nbttagcompound1);
            }

            nbttagcompound1.setInteger("color", par2);
        }
    }

    // JAVADOC METHOD $$ func_82789_a
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack)
    {
        return this.material.func_151685_b() == par2ItemStack.getItem() ? true : super.getIsRepairable(par1ItemStack, par2ItemStack);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister)
    {
        super.registerIcons(par1IconRegister);

        if (this.material == ItemArmor.ArmorMaterial.CLOTH)
        {
            this.field_94605_cw = par1IconRegister.registerIcon(field_94606_cu[this.armorType]);
        }

        this.field_94604_cx = par1IconRegister.registerIcon(field_94603_a[this.armorType]);
    }

    // JAVADOC METHOD $$ func_77659_a
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        int i = EntityLiving.getArmorPosition(par1ItemStack) - 1;
        ItemStack itemstack1 = par3EntityPlayer.getCurrentArmor(i);

        if (itemstack1 == null)
        {
            par3EntityPlayer.setCurrentItemOrArmor(i + 1, par1ItemStack.copy());  //Forge: Vanilla bug fix associated with fixed setCurrentItemOrArmor indexs for players.
            par1ItemStack.stackSize = 0;
        }

        return par1ItemStack;
    }

    @SideOnly(Side.CLIENT)
    public static IIcon func_94602_b(int par0)
    {
        switch (par0)
        {
            case 0:
                return Items.diamond_helmet.field_94604_cx;
            case 1:
                return Items.diamond_chestplate.field_94604_cx;
            case 2:
                return Items.diamond_leggings.field_94604_cx;
            case 3:
                return Items.diamond_boots.field_94604_cx;
            default:
                return null;
        }
    }

    public static enum ArmorMaterial
    {
        CLOTH(5, new int[]{1, 3, 2, 1}, 15),
        CHAIN(15, new int[]{2, 5, 4, 1}, 12),
        IRON(15, new int[]{2, 6, 5, 2}, 9),
        GOLD(7, new int[]{2, 5, 3, 1}, 25),
        DIAMOND(33, new int[]{3, 8, 6, 3}, 10);
        // JAVADOC FIELD $$ field_78048_f
        private int maxDamageFactor;
        // JAVADOC FIELD $$ field_78049_g
        private int[] damageReductionAmountArray;
        // JAVADOC FIELD $$ field_78055_h
        private int enchantability;

        private static final String __OBFID = "CL_00001768";

        //Added by forge for custom Armor materials.
        public Item customCraftingMaterial = null;

        private ArmorMaterial(int par3, int[] par4ArrayOfInteger, int par5)
        {
            this.maxDamageFactor = par3;
            this.damageReductionAmountArray = par4ArrayOfInteger;
            this.enchantability = par5;
        }

        // JAVADOC METHOD $$ func_78046_a
        public int getDurability(int par1)
        {
            return ItemArmor.maxDamageArray[par1] * this.maxDamageFactor;
        }

        // JAVADOC METHOD $$ func_78044_b
        public int getDamageReductionAmount(int par1)
        {
            return this.damageReductionAmountArray[par1];
        }

        // JAVADOC METHOD $$ func_78045_a
        public int getEnchantability()
        {
            return this.enchantability;
        }

        public Item func_151685_b()
        {
            switch (this)
            {
                case CLOTH:   return Items.leather;
                case CHAIN:   return Items.iron_ingot;
                case GOLD:    return Items.gold_ingot;
                case IRON:    return Items.iron_ingot;
                case DIAMOND: return Items.diamond;
                default:      return customCraftingMaterial;
            }
        }
    }
}