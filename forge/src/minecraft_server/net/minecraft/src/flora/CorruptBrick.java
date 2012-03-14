package net.minecraft.src.flora;

import net.minecraft.src.forge.ITextureProvider;
import java.util.ArrayList;
import net.minecraft.src.*;

public class CorruptBrick extends Block
    implements ITextureProvider
{
    public CorruptBrick(int i)
    {
        super(i, 64, Material.rock);
        blockIndexInTexture = 112;
    }

    public int getBlockTextureFromSideAndMetadata(int i, int j)
    {
        if (j < 8)
        {
            return blockIndexInTexture + j;
        }
        else
        {
            return blockIndexInTexture + 8 + j;
        }
    }

    public boolean isFireSource(World world, int i, int j, int k, int l, int i1)
    {
        return l < 8;
    }

    public float getHardness(int i)
    {
        return i <= 8 ? 2.0F : 8F;
    }

    protected int damageDropped(int i)
    {
        return i;
    }

    public String getTextureFile()
    {
        return "/floratex/stone.png";
    }

    public void addCreativeItems(ArrayList arraylist)
    {
        arraylist.add(new ItemStack(mod_FloraSoma.corruptBrick, 1, 0));
        arraylist.add(new ItemStack(mod_FloraSoma.corruptBrick, 1, 1));
        arraylist.add(new ItemStack(mod_FloraSoma.corruptBrick, 1, 2));
        arraylist.add(new ItemStack(mod_FloraSoma.corruptBrick, 1, 3));
        arraylist.add(new ItemStack(mod_FloraSoma.corruptBrick, 1, 4));
        arraylist.add(new ItemStack(mod_FloraSoma.corruptBrick, 1, 5));
        arraylist.add(new ItemStack(mod_FloraSoma.corruptBrick, 1, 6));
        arraylist.add(new ItemStack(mod_FloraSoma.corruptBrick, 1, 7));
        arraylist.add(new ItemStack(mod_FloraSoma.corruptBrick, 1, 8));
        arraylist.add(new ItemStack(mod_FloraSoma.corruptBrick, 1, 9));
        arraylist.add(new ItemStack(mod_FloraSoma.corruptBrick, 1, 10));
        arraylist.add(new ItemStack(mod_FloraSoma.corruptBrick, 1, 11));
        arraylist.add(new ItemStack(mod_FloraSoma.corruptBrick, 1, 12));
        arraylist.add(new ItemStack(mod_FloraSoma.corruptBrick, 1, 13));
        arraylist.add(new ItemStack(mod_FloraSoma.corruptBrick, 1, 14));
        arraylist.add(new ItemStack(mod_FloraSoma.corruptBrick, 1, 15));
    }
}
