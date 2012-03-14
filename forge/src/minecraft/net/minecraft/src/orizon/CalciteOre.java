package net.minecraft.src.orizon;
import net.minecraft.src.forge.*;
import net.minecraft.src.*;

public class CalciteOre extends Block
    implements ITextureProvider
{
    public CalciteOre(int i, int j)
    {
        super(i, j, Material.rock);
        enableStats = false;
    }
    
    public boolean getEnableStats() {return false;}

    protected int damageDropped(int i)
    {
        return i;
    }

    public int getBlockTextureFromSideAndMetadata(int i, int j)
    {
        return blockIndexInTexture + j;
    }

    public String getTextureFile()
    {
        return "/oretex/ores.png";
    }
}
