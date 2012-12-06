package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import cpw.mods.fml.common.FMLLog;

import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;

public class GenOreDictionary 
{
	private static boolean initialized = false;
	
    private static int maxID = 0;
    private static HashMap<String, ArrayList<Block>> ores;
    private static HashMap<Integer, String> oreKeys;
    
    static 
    {
    	initialize();
    }

	private static void initialize() 
	{
		GenOreDictionary.initialized = true;
		
		GenOreDictionary.ores = new HashMap<String, ArrayList<Block>>();
		GenOreDictionary.oreKeys = new HashMap<Integer, String>();
	}
	
	public static boolean registerOre(String group, Block ore)
	{
		if(!ores.containsKey(group))
			registerGroup(group, ore);
		else
		{
			ArrayList<Block> g = ores.get(group);
			if(g.contains(ore))
				return false;
			
			g.add(ore);
			oreKeys.put(ore.blockID, group);
		}
		
		return true;
	}
	
	public static boolean registerGroup(String key, Block masterBlock)
	{
		if(ores.containsKey(key))
			return false;
		
		ArrayList<Block> b = new ArrayList<Block>();
		b.add(masterBlock);
		
		registerBlockKey(masterBlock.blockID, key);
		
		ores.put(key, b);
		return true;
	}
	
	private static void registerBlockKey(int blockID, String key)
	{
		oreKeys.put(blockID, key);
	}
	
	public static void setGroupMaster(String group, Block masterBlock)
	{
		if(!ores.containsKey(group))
			registerGroup(group, masterBlock);
		else
		{
			ores.get(group).remove(masterBlock);
			ores.get(group).set(0, masterBlock);
		}
	}
	
	public static boolean oreHasGroup(Block ore)
	{
		return oreHasGroup(ore.blockID);
	}
	
	public static boolean oreHasGroup(int oreID)
	{
		return (oreKeys.containsKey(oreID));
	}
	
	public static int oreMasterID(Block ore)
	{
		return oreMasterID(ore.blockID);
	}
	
	public static int oreMasterID(int oreID)
	{
		if(!oreKeys.containsKey(oreID))
			return oreID;
		
		return ores.get(oreKeys.get(oreID)).get(0).blockID;
	}

	public static boolean hasInitialized()
	{
		return GenOreDictionary.initialized;
	}
	
	public static HashMap<String, Block> getOresMap()
	{
		return (HashMap<String, Block>) GenOreDictionary.ores.clone();
	}
}