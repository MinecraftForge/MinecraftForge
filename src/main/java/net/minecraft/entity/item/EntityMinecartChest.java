package net.minecraft.entity.item;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityMinecartChest extends EntityMinecartContainer
{
    private static final String __OBFID = "CL_00001671";

    public EntityMinecartChest(World par1World)
    {
        super(par1World);
    }

    public EntityMinecartChest(World par1World, double par2, double par4, double par6)
    {
        super(par1World, par2, par4, par6);
    }

    public void killMinecart(DamageSource par1DamageSource)
    {
        super.killMinecart(par1DamageSource);
        this.func_145778_a(Item.func_150898_a(Blocks.chest), 1, 0.0F);
    }

    // JAVADOC METHOD $$ func_70302_i_
    public int getSizeInventory()
    {
        return 27;
    }

    public int getMinecartType()
    {
        return 1;
    }

    public Block func_145817_o()
    {
        return Blocks.chest;
    }

    public int getDefaultDisplayTileOffset()
    {
        return 8;
    }
}