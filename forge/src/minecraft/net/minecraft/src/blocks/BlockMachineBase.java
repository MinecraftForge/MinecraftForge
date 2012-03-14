package net.minecraft.src.blocks;

import java.util.ArrayList;
import net.minecraft.src.*;

public abstract class BlockMachineBase extends BlockContainer
{
    protected BlockMachineBase(int i, Material material)
    {
        super(i, material);
    }

    public boolean blockActivated(World world, int i, int j, int k, EntityPlayer entityplayer)
    {
        if (entityplayer.isSneaking())
        {
            return false;
        }
        Integer integer = getGui(world, i, j, k, entityplayer);
        if (integer == null)
        {
            return false;
        }
        else
        {
            return openGui(entityplayer, world.getBlockTileEntity(i, j, k), integer);
        }
    }

    public static boolean openGui(EntityPlayer entityplayer, TileEntity tileentity, Integer integer)
    {
        if (tileentity instanceof IInventory)
        {
            ModLoader.openGUI(entityplayer, new FurnaceGui(entityplayer.inventory, (FurnaceLogic)tileentity));
            return true;
        }
        else
        {
            return false;
        }
    }

    public abstract Integer getGui(World world, int i, int j, int k, EntityPlayer entityplayer);

    public ArrayList getBlockDropped(World world, int i, int j, int k, int l, int i1)
    {
        ArrayList arraylist = super.getBlockDropped(world, i, j, k, l, i1);
        TileEntity tileentity = world.getBlockTileEntity(i, j, k);
        if (tileentity instanceof IInventory)
        {
            IInventory iinventory = (IInventory)tileentity;
            for (int j1 = 0; j1 < iinventory.getSizeInventory(); j1++)
            {
                net.minecraft.src.ItemStack itemstack = iinventory.getStackInSlot(j1);
                if (itemstack != null)
                {
                    arraylist.add(itemstack);
                    iinventory.setInventorySlotContents(j1, null);
                }
            }
        }
        return arraylist;
    }

    public BlockLogicBase getLogicEntity()
    {
        return null;
    }

    public abstract BlockLogicBase getBlockEntity(int i);

    public void onBlockAdded(World world, int i, int j, int k)
    {
    }

    public void onBlockPlacedBy(World world, int i, int j, int k, EntityLiving entityliving)
    {
        BlockLogicBase blocklogicbase = (BlockLogicBase)world.getBlockTileEntity(i, j, k);
        if (entityliving == null)
        {
            blocklogicbase.setFacing((short)2);
        }
        else
        {
            int l = MathHelper.floor_double((double)((entityliving.rotationYaw * 4F) / 360F) + 0.5D) & 3;
            switch (l)
            {
                case 0:
                    blocklogicbase.setFacing((short)2);
                    break;

                case 1:
                    blocklogicbase.setFacing((short)5);
                    break;

                case 2:
                    blocklogicbase.setFacing((short)3);
                    break;

                case 3:
                    blocklogicbase.setFacing((short)4);
                    break;
            }
        }
    }

    public static boolean isActive(IBlockAccess iblockaccess, int i, int j, int k)
    {
        TileEntity tileentity = iblockaccess.getBlockTileEntity(i, j, k);
        if (tileentity instanceof BlockLogicBase)
        {
            return ((BlockLogicBase)tileentity).getActive();
        }
        else
        {
            return false;
        }
    }

    public TileEntity getBlockEntity()
    {
        return getLogicEntity();
    }
}
