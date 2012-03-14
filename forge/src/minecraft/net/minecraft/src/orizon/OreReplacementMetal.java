package net.minecraft.src.orizon;
import java.util.ArrayList;
import java.util.Random;

import net.minecraft.src.forge.*;
import net.minecraft.src.*;

public class OreReplacementMetal extends Block
    implements ITextureProvider
{
    public OreReplacementMetal(int i, int j)
    {
        super(i, j, Material.rock);
    }
    
    @Override
    public int idDropped(int md, Random random, int fortune)
    {
        if(md < 4)
        	return Block.oreIron.idDropped(md, random, fortune);
    	
        return Block.oreGold.idDropped(md, random, fortune);
    }
    
    @Override
    public int quantityDropped(int md, int fortune, Random random)
    {
    	if(md < 4)
        	return Block.oreIron.quantityDroppedWithBonus(fortune, random);
    	
        return Block.oreGold.quantityDroppedWithBonus(fortune, random);
    }

    @Override
    public int getBlockTextureFromSideAndMetadata(int side, int md)
    {
        return blockIndexInTexture + md;
    }

    @Override
    public String getTextureFile()
    {
        return "/oretex/ores.png";
    }
}
