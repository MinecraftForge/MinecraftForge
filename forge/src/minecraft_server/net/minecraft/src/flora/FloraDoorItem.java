package net.minecraft.src.flora;
import net.minecraft.src.*;
import net.minecraft.src.forge.*;

public class FloraDoorItem extends Item
	implements ITextureProvider
{
    private int door;

    public FloraDoorItem(int i, int bID)
    {
        super(i);
        //door = Block.blocksList[bID];
        
        door = bID;
        //maxStackSize = 1;
    }

    public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l)
    {
        if (l != 1)
        {
            return false;
        }
        j++;

        Block block;
        if(door == mod_FloraSoma.redwoodDoor.blockID) {
        	block = mod_FloraSoma.redwoodDoor;
        } else {
        	block = Block.doorWood;
        }
        if (!entityplayer.canPlayerEdit(i, j, k) || !entityplayer.canPlayerEdit(i, j + 1, k))
        {
            return false;
        }
        if (!block.canPlaceBlockAt(world, i, j, k))
        {
            return false;
        }
        else
        {
            int i1 = MathHelper.floor_double((double)(((entityplayer.rotationYaw + 180F) * 4F) / 360F) - 0.5D) & 3;
            placeDoorBlock(world, i, j, k, i1, block);
            itemstack.stackSize--;
            return true;
        }
    }

    public static void placeDoorBlock(World world, int i, int j, int k, int l, Block block)
    {
        byte byte0 = 0;
        byte byte1 = 0;
        if (l == 0)
        {
            byte1 = 1;
        }
        if (l == 1)
        {
            byte0 = -1;
        }
        if (l == 2)
        {
            byte1 = -1;
        }
        if (l == 3)
        {
            byte0 = 1;
        }
        int i1 = (world.isBlockNormalCube(i - byte0, j, k - byte1) ? 1 : 0) + (world.isBlockNormalCube(i - byte0, j + 1, k - byte1) ? 1 : 0);
        int j1 = (world.isBlockNormalCube(i + byte0, j, k + byte1) ? 1 : 0) + (world.isBlockNormalCube(i + byte0, j + 1, k + byte1) ? 1 : 0);
        boolean flag = world.getBlockId(i - byte0, j, k - byte1) == block.blockID || world.getBlockId(i - byte0, j + 1, k - byte1) == block.blockID;
        boolean flag1 = world.getBlockId(i + byte0, j, k + byte1) == block.blockID || world.getBlockId(i + byte0, j + 1, k + byte1) == block.blockID;
        boolean flag2 = false;
        if (flag && !flag1)
        {
            flag2 = true;
        }
        else if (j1 > i1)
        {
            flag2 = true;
        }
        if (flag2)
        {
            l = l - 1 & 3;
            l += 4;
        }
        world.editingBlocks = true;
        world.setBlockAndMetadataWithNotify(i, j, k, block.blockID, l);
        world.setBlockAndMetadataWithNotify(i, j + 1, k, block.blockID, l + 8);
        world.editingBlocks = false;
        world.notifyBlocksOfNeighborChange(i, j, k, block.blockID);
        world.notifyBlocksOfNeighborChange(i, j + 1, k, block.blockID);
    }
    
    public String getTextureFile()
    {
        return "/floratex/plantblocks.png";
    }
}
