package net.minecraft.src.flora;
import net.minecraft.src.*;
import net.minecraft.src.forge.*;
import java.util.Random;

public class FloraDoor extends BlockDoor
	implements ITextureProvider
{
    private int itemID;
	
	public FloraDoor(int id, int tex, Material material, int item)
    {
        super(id, material); //146
        blockIndexInTexture = tex;
        float f = 0.5F;
        float f1 = 1.0F;
        setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
        itemID = item;
    }

    public int idDropped(int i, Random random, int j)
    {
    	return itemID;
    }

    
    public String getTextureFile()
    {
        return "/floratex/plantblocks.png";
    }
}
