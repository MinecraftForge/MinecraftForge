package net.minecraft.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BehaviorDefaultDispenseItem implements IBehaviorDispenseItem
{
    private static final String __OBFID = "CL_00001195";

    // JAVADOC METHOD $$ func_82482_a
    public final ItemStack dispense(IBlockSource par1IBlockSource, ItemStack par2ItemStack)
    {
        ItemStack itemstack1 = this.dispenseStack(par1IBlockSource, par2ItemStack);
        this.playDispenseSound(par1IBlockSource);
        this.spawnDispenseParticles(par1IBlockSource, BlockDispenser.func_149937_b(par1IBlockSource.getBlockMetadata()));
        return itemstack1;
    }

    // JAVADOC METHOD $$ func_82487_b
    protected ItemStack dispenseStack(IBlockSource par1IBlockSource, ItemStack par2ItemStack)
    {
        EnumFacing enumfacing = BlockDispenser.func_149937_b(par1IBlockSource.getBlockMetadata());
        IPosition iposition = BlockDispenser.func_149939_a(par1IBlockSource);
        ItemStack itemstack1 = par2ItemStack.splitStack(1);
        doDispense(par1IBlockSource.getWorld(), itemstack1, 6, enumfacing, iposition);
        return par2ItemStack;
    }

    public static void doDispense(World par0World, ItemStack par1ItemStack, int par2, EnumFacing par3EnumFacing, IPosition par4IPosition)
    {
        double d0 = par4IPosition.getX();
        double d1 = par4IPosition.getY();
        double d2 = par4IPosition.getZ();
        EntityItem entityitem = new EntityItem(par0World, d0, d1 - 0.3D, d2, par1ItemStack);
        double d3 = par0World.rand.nextDouble() * 0.1D + 0.2D;
        entityitem.motionX = (double)par3EnumFacing.getFrontOffsetX() * d3;
        entityitem.motionY = 0.20000000298023224D;
        entityitem.motionZ = (double)par3EnumFacing.getFrontOffsetZ() * d3;
        entityitem.motionX += par0World.rand.nextGaussian() * 0.007499999832361937D * (double)par2;
        entityitem.motionY += par0World.rand.nextGaussian() * 0.007499999832361937D * (double)par2;
        entityitem.motionZ += par0World.rand.nextGaussian() * 0.007499999832361937D * (double)par2;
        par0World.spawnEntityInWorld(entityitem);
    }

    // JAVADOC METHOD $$ func_82485_a
    protected void playDispenseSound(IBlockSource par1IBlockSource)
    {
        par1IBlockSource.getWorld().playAuxSFX(1000, par1IBlockSource.getXInt(), par1IBlockSource.getYInt(), par1IBlockSource.getZInt(), 0);
    }

    // JAVADOC METHOD $$ func_82489_a
    protected void spawnDispenseParticles(IBlockSource par1IBlockSource, EnumFacing par2EnumFacing)
    {
        par1IBlockSource.getWorld().playAuxSFX(2000, par1IBlockSource.getXInt(), par1IBlockSource.getYInt(), par1IBlockSource.getZInt(), this.func_82488_a(par2EnumFacing));
    }

    private int func_82488_a(EnumFacing par1EnumFacing)
    {
        return par1EnumFacing.getFrontOffsetX() + 1 + (par1EnumFacing.getFrontOffsetZ() + 1) * 3;
    }
}