package net.minecraft.src.orizon;
import java.util.Random;

import net.minecraft.src.forge.*;
import net.minecraft.src.*;

public class MineralOreAlt extends Block
    implements ITextureProvider
{
    public MineralOreAlt(int i, int j)
    {
        super(i, j, Material.rock);
    }
    
    public int idDropped(int md, Random random, int fortune)
    {
        return mod_Orizon.mineralOreAltID;
    }

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
