package net.minecraft.src.flora;
import net.minecraft.src.*;
import net.minecraft.src.forge.*;
import java.util.*;

public class FloraSomaBonemealHandler 
	implements IBonemealHandler{
	private Random rand = new Random();

	@Override
	public boolean onUseBonemeal(World world, int bID, int x, int y, int z) {
		if(bID == mod_FloraSoma.floraSapling.blockID)
		{
			FloraSaplingBlock sapling = (FloraSaplingBlock)Block.blocksList[world.getBlockId(x, y, z)];
			sapling.growTree(world, x, y, z, rand);
			return true;
		} else
		{
			return false;
		}
	}

}
