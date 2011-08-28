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

import java.util.*;

public class ForgeHooks {
	// TODO: move all app-side hooks from MinecraftForge
	//
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

	public static EnumStatus sleepInBedAt(EntityPlayer player, int i, int j, int k) {
		for (ISleepHandler handler : sleepHandlers) {
			EnumStatus status = handler.sleepInBedAt(player, i, j, k);
			if (status != null)
				return status;
		}
		return null;
	}

	static LinkedList<ISleepHandler> sleepHandlers = new LinkedList<ISleepHandler>();
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
		return itemstack.canHarvestBlock(bl);
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
		List tc=(List)toolClasses.get(ist.itemID);
		if(tc==null) return false;
		Object[] ta=tc.toArray();
		String cls=(String)ta[0];

		return toolEffectiveness.contains(Arrays.asList(
			bl.blockID,md,cls));
	}

	static void initTools() {
		if(toolInit) return;
		toolInit=true;

		MinecraftForge.setToolClass(Item.pickaxeWood,"pickaxe",0);
		MinecraftForge.setToolClass(Item.pickaxeStone,"pickaxe",1);
		MinecraftForge.setToolClass(Item.pickaxeSteel,"pickaxe",2);
		MinecraftForge.setToolClass(Item.pickaxeGold,"pickaxe",0);
		MinecraftForge.setToolClass(Item.pickaxeDiamond,"pickaxe",3);

		MinecraftForge.setToolClass(Item.axeWood,"axe",0);
		MinecraftForge.setToolClass(Item.axeStone,"axe",1);
		MinecraftForge.setToolClass(Item.axeSteel,"axe",2);
		MinecraftForge.setToolClass(Item.axeGold,"axe",0);
		MinecraftForge.setToolClass(Item.axeDiamond,"axe",3);

		MinecraftForge.setToolClass(Item.shovelWood,"shovel",0);
		MinecraftForge.setToolClass(Item.shovelStone,"shovel",1);
		MinecraftForge.setToolClass(Item.shovelSteel,"shovel",2);
		MinecraftForge.setToolClass(Item.shovelGold,"shovel",0);
		MinecraftForge.setToolClass(Item.shovelDiamond,"shovel",3);

		MinecraftForge.setBlockHarvestLevel(Block.obsidian,"pickaxe",3);
		MinecraftForge.setBlockHarvestLevel(Block.oreDiamond,"pickaxe",2);
		MinecraftForge.setBlockHarvestLevel(Block.blockDiamond,"pickaxe",2);
		MinecraftForge.setBlockHarvestLevel(Block.oreGold,"pickaxe",2);
		MinecraftForge.setBlockHarvestLevel(Block.blockGold,"pickaxe",2);
		MinecraftForge.setBlockHarvestLevel(Block.oreIron,"pickaxe",1);
		MinecraftForge.setBlockHarvestLevel(Block.blockSteel,"pickaxe",1);
		MinecraftForge.setBlockHarvestLevel(Block.oreLapis,"pickaxe",1);
		MinecraftForge.setBlockHarvestLevel(Block.blockLapis,"pickaxe",1);
		MinecraftForge.setBlockHarvestLevel(Block.oreRedstone,"pickaxe",2);
		MinecraftForge.setBlockHarvestLevel(Block.oreRedstoneGlowing,"pickaxe",2);
		MinecraftForge.removeBlockEffectiveness(Block.oreRedstone,"pickaxe");
		MinecraftForge.removeBlockEffectiveness(Block.oreRedstoneGlowing,"pickaxe");

		Block[] pickeff ={
			Block.cobblestone, Block.stairDouble,
			Block.stairSingle, Block.stone, Block.sandStone,
			Block.cobblestoneMossy, Block.oreIron,
			Block.blockSteel, Block.oreCoal, Block.blockGold, 
			Block.oreGold, Block.oreDiamond, Block.blockDiamond,
			Block.ice, Block.netherrack, Block.oreLapis,
			Block.blockLapis
				};
		for(Block bl : pickeff) {
			MinecraftForge.setBlockHarvestLevel(bl,"pickaxe",0);
		}

		// TODO: add other tool tables.
	}

	public static final int majorVersion=1;
	public static final int minorVersion=0;
	public static final int revisionVersion=6;
	static {
		System.out.printf("MinecraftForge V%d.%d.%d Initialized\n",majorVersion,minorVersion,revisionVersion);
	}

	static boolean toolInit=false;
	static HashMap toolClasses=new HashMap();
	static HashMap toolHarvestLevels=new HashMap();
	static HashSet toolEffectiveness=new HashSet();
}

