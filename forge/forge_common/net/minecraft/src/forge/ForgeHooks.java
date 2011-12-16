/**
 * This software is provided under the terms of the Minecraft Forge Public 
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Item;
import net.minecraft.src.EnumStatus;
import net.minecraft.src.World;

import java.util.*;

public class ForgeHooks {
	// TODO: move all app-side hooks from MinecraftForge


	// List Handling Hooks
	// ------------------------------------------------------------
	public static void onTakenFromCrafting(EntityPlayer player, ItemStack ist,
			IInventory craftMatrix) {
		for (ICraftingHandler handler : craftingHandlers) {
			handler.onTakenFromCrafting(player,ist,craftMatrix);
		}
	}

	static LinkedList<ICraftingHandler> craftingHandlers = new LinkedList<ICraftingHandler>();

	public static void onDestroyCurrentItem(EntityPlayer player, ItemStack orig) {
		for (IDestroyToolHandler handler : destroyToolHandlers) {
			handler.onDestroyCurrentItem(player,orig);
		}
	}

	static LinkedList<IDestroyToolHandler> destroyToolHandlers = new LinkedList<IDestroyToolHandler>();

	public static boolean onUseBonemeal(World world,
			int bid, int i, int j, int k) {
		for(IBonemealHandler handler : bonemealHandlers) {
			if(handler.onUseBonemeal(world,bid,i,j,k))
				return true;
		}
		return false;
	}

	static LinkedList<IBonemealHandler> bonemealHandlers = new LinkedList<IBonemealHandler>();

	public static boolean onUseHoe(ItemStack hoe, EntityPlayer player,
			World world, int i, int j, int k) {
		for(IHoeHandler handler : hoeHandlers) {
			if(handler.onUseHoe(hoe,player,world,i,j,k))
				return true;
		}
		return false;
	}

	static LinkedList<IHoeHandler> hoeHandlers = new LinkedList<IHoeHandler>();

	public static EnumStatus sleepInBedAt(EntityPlayer player, int i, int j, int k) {
		for (ISleepHandler handler : sleepHandlers) {
			EnumStatus status = handler.sleepInBedAt(player, i, j, k);
			if (status != null)
				return status;
		}
		return null;
	}

	static LinkedList<ISleepHandler> sleepHandlers = new LinkedList<ISleepHandler>();

	// Plant Management
	// ------------------------------------------------------------
	static class ProbableItem {
		public ProbableItem(int item, int md, int q, int st, int e) {
			wstart=st; wend=e;
			itemid=item; meta=md;
			qty=q;
		}
		int wstart, wend;
		int itemid, meta;
		int qty;
	}

	static ProbableItem getRandomItem(List<ProbableItem> list, int prop) {
		int n=Collections.binarySearch(list,prop,new Comparator(){
			public int compare(Object o1, Object o2) {
				ProbableItem pi=(ProbableItem)o1;
				Integer i1=(Integer)o2;
				if(i1<pi.wstart) return 1;
				if(i1>=pi.wend) return -1;
				return 0;
			}
		});
		if(n<0) return null;
		return list.get(n);
	}

	static List<ProbableItem> plantGrassList;
	static int plantGrassWeight;

	static List<ProbableItem> seedGrassList;
	static int seedGrassWeight;

	static {
		plantGrassList=new ArrayList<ProbableItem>();
		plantGrassList.add(new ProbableItem(
			Block.plantYellow.blockID,0,1,0,20));
		plantGrassList.add(new ProbableItem(
			Block.plantRed.blockID,0,1,20,30));
		plantGrassWeight=30;

		seedGrassList=new ArrayList<ProbableItem>();
		seedGrassList.add(new ProbableItem(
			Item.seeds.shiftedIndex,0,1,0,10));
		seedGrassWeight=10;
	}

	public static void plantGrassPlant(World world, int i, int j, int k) {
		int n=world.rand.nextInt(plantGrassWeight);
		ProbableItem pi=getRandomItem(plantGrassList,n);
		if(pi==null) return;
		world.setBlockAndMetadataWithNotify(i,j,k,pi.itemid,pi.meta);
	}

	public static void addPlantGrass(int item, int md, int prop) {
		plantGrassList.add(new ProbableItem(
			item,md,1,plantGrassWeight,plantGrassWeight+prop));
		plantGrassWeight+=prop;
	}

	public static ItemStack getGrassSeed(World world) {
		int n=world.rand.nextInt(seedGrassWeight);
		ProbableItem pi=getRandomItem(seedGrassList,n);
		if(pi==null) return null;
		return new ItemStack(pi.itemid,pi.qty,pi.meta);
	}

	public static void addGrassSeed(int item, int md, int qty, int prop) {
		seedGrassList.add(new ProbableItem(
			item,md,qty,seedGrassWeight,seedGrassWeight+prop));
		seedGrassWeight+=prop;
	}

	// Tool Path
	// ------------------------------------------------------------
	public static boolean canHarvestBlock(Block bl,
			EntityPlayer player, int md) {
		if(bl.blockMaterial.getIsHarvestable())
			return true;
		ItemStack itemstack = player.inventory.getCurrentItem();
		if(itemstack == null) return false;

		List tc=(List)toolClasses.get(itemstack.itemID);
		if(tc==null) return itemstack.canHarvestBlock(bl);
		Object[] ta=tc.toArray();
		String cls=(String)ta[0]; int hvl=(Integer)ta[1];

		Integer bhl=(Integer)toolHarvestLevels.get(Arrays.asList(
			bl.blockID,md,cls));
		if(bhl==null) return itemstack.canHarvestBlock(bl);
		if(bhl>hvl) return false;
		return true;
	}

	public static float blockStrength(Block bl,
			EntityPlayer player, int md) {
		float bh=bl.getHardness(md);
		if(bh < 0.0F) return 0.0F;

		if(!canHarvestBlock(bl,player,md)) {
			return 1.0F / bh / 100F;
		} else {
			return player.getCurrentPlayerStrVsBlock(bl,md) /
				bh / 30F;
		}
	}

	public static boolean isToolEffective(ItemStack ist, Block bl, int md) {
		List tc = (List)toolClasses.get(ist.itemID);
		if (tc == null) 
		{
			return false;
		}
		Object[] ta = tc.toArray();
		String cls = (String)ta[0];
		return toolEffectiveness.contains(Arrays.asList(bl.blockID, md, cls));
	}

	static void initTools() {
		if(toolInit) 
		{
			return;
		}
		toolInit = true;

		MinecraftForge.setToolClass(Item.pickaxeWood,    "pickaxe", 0);
		MinecraftForge.setToolClass(Item.pickaxeStone,   "pickaxe", 1);
		MinecraftForge.setToolClass(Item.pickaxeSteel,   "pickaxe", 2);
		MinecraftForge.setToolClass(Item.pickaxeGold,    "pickaxe", 0);
		MinecraftForge.setToolClass(Item.pickaxeDiamond, "pickaxe", 3);

		MinecraftForge.setToolClass(Item.axeWood,    "axe", 0);
		MinecraftForge.setToolClass(Item.axeStone,   "axe", 1);
		MinecraftForge.setToolClass(Item.axeSteel,   "axe", 2);
		MinecraftForge.setToolClass(Item.axeGold,    "axe", 0);
		MinecraftForge.setToolClass(Item.axeDiamond, "axe", 3);

		MinecraftForge.setToolClass(Item.shovelWood,    "shovel", 0);
		MinecraftForge.setToolClass(Item.shovelStone,   "shovel", 1);
		MinecraftForge.setToolClass(Item.shovelSteel,   "shovel", 2);
		MinecraftForge.setToolClass(Item.shovelGold,    "shovel", 0);
		MinecraftForge.setToolClass(Item.shovelDiamond, "shovel", 3);

		MinecraftForge.setBlockHarvestLevel(Block.obsidian,     "pickaxe", 3);
		MinecraftForge.setBlockHarvestLevel(Block.oreDiamond,   "pickaxe", 2);
		MinecraftForge.setBlockHarvestLevel(Block.blockDiamond, "pickaxe", 2);
		MinecraftForge.setBlockHarvestLevel(Block.oreGold,      "pickaxe", 2);
		MinecraftForge.setBlockHarvestLevel(Block.blockGold,    "pickaxe", 2);
		MinecraftForge.setBlockHarvestLevel(Block.oreIron,      "pickaxe", 1);
		MinecraftForge.setBlockHarvestLevel(Block.blockSteel,   "pickaxe", 1);
		MinecraftForge.setBlockHarvestLevel(Block.oreLapis,     "pickaxe", 1);
		MinecraftForge.setBlockHarvestLevel(Block.blockLapis,   "pickaxe", 1);
		MinecraftForge.setBlockHarvestLevel(Block.oreRedstone,  "pickaxe", 2);
		MinecraftForge.setBlockHarvestLevel(Block.oreRedstoneGlowing, "pickaxe", 2);
		MinecraftForge.removeBlockEffectiveness(Block.oreRedstone, "pickaxe");
		MinecraftForge.removeBlockEffectiveness(Block.obsidian,    "pickaxe");
		MinecraftForge.removeBlockEffectiveness(Block.oreRedstoneGlowing, "pickaxe");

		Block[] pickeff = {
			Block.cobblestone, Block.stairDouble,
			Block.stairSingle, Block.stone, 
			Block.sandStone,   Block.cobblestoneMossy,
			Block.oreCoal,     Block.ice, 
			Block.netherrack,  Block.oreLapis,
			Block.blockLapis
		};
		for (Block bl : pickeff) 
		{
			MinecraftForge.setBlockHarvestLevel(bl, "pickaxe", 0);
		}
		
		Block[] spadeEff = {
			Block.grass,     Block.dirt, 
			Block.sand,      Block.gravel, 
			Block.snow,      Block.blockSnow, 
			Block.blockClay, Block.tilledField, 
			Block.slowSand,  Block.mycelium
		};
		for (Block bl : spadeEff) 
		{
			MinecraftForge.setBlockHarvestLevel(bl, "shovel", 0);
		}
		
		Block[] axeEff = {
			Block.planks,      Block.bookShelf, 
			Block.wood,        Block.chest, 
			Block.stairDouble, Block.stairSingle, 
			Block.pumpkin,     Block.pumpkinLantern
		};
		for (Block bl : axeEff) 
		{
			MinecraftForge.setBlockHarvestLevel(bl, "axe", 0);
		}

	}

	public static final int majorVersion=1;
	public static final int minorVersion=2;
	public static final int revisionVersion=2;
	static {
		System.out.printf("MinecraftForge V%d.%d.%d Initialized\n", majorVersion, minorVersion, revisionVersion);
	}

	static boolean toolInit=false;
	static HashMap toolClasses=new HashMap();
	static HashMap toolHarvestLevels=new HashMap();
	static HashSet toolEffectiveness=new HashSet();
}

