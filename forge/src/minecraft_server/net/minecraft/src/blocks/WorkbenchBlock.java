package net.minecraft.src.blocks;

import net.minecraft.src.forge.ITextureProvider;
import net.minecraft.src.*;

public class WorkbenchBlock extends Block
    implements ITextureProvider
{
    public WorkbenchBlock(int i)
    {
        super(i, 1, Material.wood);
    }

    public int getBlockTextureFromSideAndMetadata(int i, int j)
    {
        if (i == 1)
        {
            return (blockIndexInTexture - 1) + j * 4;
        }
        if (i == 0)
        {
            return blockIndexInTexture + 2 + j * 4;
        }
        if (i == 2 || i == 4)
        {
            return blockIndexInTexture + 1 + j * 4;
        }
        else
        {
            return blockIndexInTexture + j * 4;
        }
    }

    public boolean blockActivated(World world, int i, int j, int k, EntityPlayer entityplayer)
    {
        if (world.isRemote)
        {
            return true;
        }
        else
        {
        	ModLoader.openGUI(entityplayer, mod_InfiBlocks.craftingGuiID, entityplayer.inventory, 
    				new WorkbenchContainer(entityplayer.inventory, world));
            return true;
        }
    }

    protected int damageDropped(int i)
    {
        return i;
    }

    public String getTextureFile()
    {
        return "/infiblocks/infimachines.png";
    }
}
