package net.minecraft.src;
import net.minecraft.src.*;

import java.util.*;

public class DetailManager {
	
	private static final DetailManager instance = new DetailManager();
	private List detailing;
	private List breaking;
	public static HashSet damageOnCraft = new HashSet();
	public static HashMap damageContainer = new HashMap();
	
	public static final DetailManager getInstance()
    {
        return instance;
    }
	
	private DetailManager()
	{
		detailing = new ArrayList();
		breaking = new ArrayList();
		initDetails();
	}
	
	public void addDetailing(DetailInput detailinput)
	{
		detailing.add(detailinput);
		ModLoader.addShapelessRecipe(new ItemStack(detailinput.getBlockID(), 1, detailinput.getMetadata()), 
				new Object[] { new ItemStack(mod_InfiBlocks.chiselIron, 1, -1), 
			new ItemStack(detailinput.getReplacementID(), 1, detailinput.getReplacementMetadata())
		});
		ModLoader.addShapelessRecipe(new ItemStack(detailinput.getBlockID(), 1, detailinput.getMetadata()), 
				new Object[] { new ItemStack(mod_InfiBlocks.chiselDiamond, 1, -1), 
			new ItemStack(detailinput.getReplacementID(), 1, detailinput.getReplacementMetadata())
		});
	}
	
	public boolean detail(World world, int x, int y, int z, int bID, int md)
	{
		boolean carved = false;
		for(int i = 0; i < detailing.size(); i++)
		{
			DetailInput detail = (DetailInput)detailing.get(i);
			if(bID == detail.getBlockID() && md == detail.getMetadata() && !carved) {
				world.setBlockAndMetadataWithNotify(
						x, y, z, detail.getReplacementID(), detail.getReplacementMetadata() );
				carved = true;
			}
		}
		return carved;
	}
	
	public static void addDamageOnCraft(Item item)
	{
		damageOnCraft.add(Integer.valueOf(item.shiftedIndex));
	}

	public static void addDamageContainer(Item item, Item item1)
	{
		damageOnCraft.add(Integer.valueOf(item.shiftedIndex));
		damageContainer.put(Integer.valueOf(item.shiftedIndex), Integer.valueOf(item1.shiftedIndex));
	}
	
	public void addDetailing(Block block, int md, Block blockOut, int mdOut)
	{
		addDetailing(new DetailInput(block.blockID, md, blockOut.blockID, mdOut));
	}
	
	public void addDetailing(int bID, int md, int bIDout, int mdOut)
	{
		addDetailing(new DetailInput(bID, md, bIDout, mdOut));
	}
	
	public void addDetailing(Block block, Block blockOut)
	{
		addDetailing(new DetailInput(block.blockID, 0, blockOut.blockID, 0));
	}
	
	public void addDetailing(Block block, Block blockOut, int mdOut)
	{
		addDetailing(new DetailInput(block.blockID, 0, blockOut.blockID, mdOut));
	}
	
	private void initDetails()
	{
		addDetailing(Block.stone, Block.stoneBrick); //Stone
		addDetailing(Block.stoneBrick, mod_InfiBlocks.brick, 9);
		addDetailing(mod_InfiBlocks.brick, 9, mod_InfiBlocks.fancyBrick, 9);
		addDetailing(mod_InfiBlocks.fancyBrick, 9, Block.stoneBrick, 3);
		
		addDetailing(Block.obsidian, mod_InfiBlocks.brick, 0); //Obsidian
		addDetailing(mod_InfiBlocks.brick, 0, mod_InfiBlocks.fancyBrick, 0);
		
		addDetailing(Block.blockSnow, mod_InfiBlocks.brick, 1); //Snow
		addDetailing(mod_InfiBlocks.brick, 1, mod_InfiBlocks.fancyBrick, 1);
		
		addDetailing(mod_InfiBlocks.storageBlock, 13, mod_InfiBlocks.brick, 2); //Sandstone
		addDetailing(mod_InfiBlocks.brick, 2, mod_InfiBlocks.fancyBrick, 2);
		
		addDetailing(mod_InfiBlocks.storageBlock, 15, mod_InfiBlocks.brick, 11); //Brick
		addDetailing(mod_InfiBlocks.brick, 11, mod_InfiBlocks.brick, 3);
		addDetailing(mod_InfiBlocks.brick, 3, mod_InfiBlocks.fancyBrick, 3);
		addDetailing(mod_InfiBlocks.fancyBrick, 3, mod_InfiBlocks.fancyBrick, 11);
		
		addDetailing(mod_InfiBlocks.storageBlock, 12, mod_InfiBlocks.brick, 4); //Netherrack
		addDetailing(mod_InfiBlocks.brick, 4, mod_InfiBlocks.fancyBrick, 4);
		
		addDetailing(mod_InfiBlocks.brick, 5, mod_InfiBlocks.fancyBrick, 5); //Diamond
		addDetailing(mod_InfiBlocks.brick, 6, mod_InfiBlocks.fancyBrick, 6); //Gold
		addDetailing(mod_InfiBlocks.brick, 7, mod_InfiBlocks.fancyBrick, 7); //Lapis
		
		addDetailing(mod_InfiBlocks.storageBlock, 14, mod_InfiBlocks.brick, 8); //Refined Stone
		addDetailing(mod_InfiBlocks.brick, 8, mod_InfiBlocks.brick, 10);
		addDetailing(mod_InfiBlocks.brick, 10, mod_InfiBlocks.fancyBrick, 8);
		
		addDetailing(Block.ice, 0, mod_InfiBlocks.iceBrick, 0); //Ice
		addDetailing(mod_InfiBlocks.iceBrick, 0, mod_InfiBlocks.iceBrick, 1);
		addDetailing(mod_InfiBlocks.iceBrick, 1, mod_InfiBlocks.iceBrick, 2);
		
		addDetailing(mod_InfiBlocks.brownstone, 0, mod_InfiBlocks.brownstone, 2); //Brownstone
		addDetailing(mod_InfiBlocks.brownstone, 1, mod_InfiBlocks.brownstone, 3);
		addDetailing(mod_InfiBlocks.brownstone, 3, mod_InfiBlocks.brownstone, 4);
		addDetailing(mod_InfiBlocks.brownstone, 4, mod_InfiBlocks.brownstone, 5);
	}
}
