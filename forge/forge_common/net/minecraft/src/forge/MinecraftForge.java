/**
 * This software is provided under the terms of the Minecraft Forge Public 
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.BaseMod;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityMinecart;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.World;

import java.util.*;

public class MinecraftForge {

    private static LinkedList<IBucketHandler> bucketHandlers = new LinkedList<IBucketHandler>();
    
    /**
     * Register a new custom bucket handler.
     * @param handler The Handler to be registered
     */
    public static void registerCustomBucketHandler(IBucketHandler handler) 
    {
        bucketHandlers.add(handler);
    }

    /**
     * Registers a new sleeping handler.
     * @param handler The Handler to be registered
     */
    public static void registerSleepHandler(ISleepHandler handler) 
    {
        ForgeHooks.sleepHandlers.add(handler);
    }

    /**
     * Registers a new bonemeal handler.
     * @param handler The Handler to be registered
     */
    public static void registerBonemealHandler(IBonemealHandler handler) 
    {
        ForgeHooks.bonemealHandlers.add(handler);
    }

    /**
     * Registers a new hoe handler.
     * @param handler The Handler to be registered
     */
    public static void registerHoeHandler(IHoeHandler handler) 
    {
        ForgeHooks.hoeHandlers.add(handler);
    }

    /**
     * Registers a new destroy tool handler.
     * @param handler The Handler to be registered
     */
    public static void registerDestroyToolHandler(IDestroyToolHandler handler) 
    {
        ForgeHooks.destroyToolHandlers.add(handler);
    }

    /**
     * Registers a new crafting handler.
     * @param handler The Handler to be registered
     */
    public static void registerCraftingHandler(ICraftingHandler handler)
    {
        ForgeHooks.craftingHandlers.add(handler);
    }
    
    /**
     * Registers a new minecart handler
     * @param handler The Handler to be registered
     */
    public static void registerMinecartHandler(IMinecartHandler handler)
    {
    	ForgeHooks.minecartHandlers.add(handler);
    }
    
    /**
     * Registers a new Connection event handler
     * @param handler The Handler to be registered
     */
    public static void registerConnectionHandler(IConnectionHandler handler)
    {
        ForgeHooks.connectionHandlers.add(handler);
    }

    /**
     * Registers a new Item Pickup event handler
     * @param handler The Handler to be registered
     */
    public static void registerPickupHandler(IPickupHandler handler)
    {
        ForgeHooks.pickupHandlers.add(handler);
    }
    
    /**
     * Registers a new shears event handler
     */
    public static void registerShearsHandler(IShearsHandler handler)
    {
    	ForgeHooks.shearsHandlers.add(handler);
    }

    /**
     * This is not supposed to be called outside of Minecraft internals.
     */
    public static ItemStack fillCustomBucket(World w, int i, int j, int k) {
        for (IBucketHandler handler : bucketHandlers) {
            ItemStack stack = handler.fillCustomBucket(w, i, j, k);

            if (stack != null) {
                return stack;
            }
        }

        return null;
    }

    // Ore Dictionary
    // ------------------------------------------------------------
    private static LinkedList<IOreHandler> oreHandlers = new LinkedList<IOreHandler>();
    private static TreeMap<String,List<ItemStack>> oreDict=
	    new TreeMap<String,List<ItemStack>>();

    /** Register a new ore handler.  This will automatically call the handler
     * with all current ores during registration, and every time a new ore is
     * added later.
     */
    public static void registerOreHandler(IOreHandler handler) {
	    oreHandlers.add(handler);

	    for(String key : oreDict.keySet()) {
		    List<ItemStack> val=oreDict.get(key);
		    for(ItemStack ist : val) {
			    handler.registerOre(key,ist);
		    }
	    }
    }

    /** Register a new item with the ore dictionary.
     * @param oreClass The string class of the ore.
     * @param ore The ItemStack for the ore.
     */
    public static void registerOre(String oreClass, ItemStack ore) {
	    List<ItemStack> orelist=oreDict.get(oreClass);
	    if(orelist==null) {
		    orelist=new ArrayList<ItemStack>();
		    oreDict.put(oreClass,orelist);
	    }
	    orelist.add(ore);
	    for(IOreHandler ioh : oreHandlers) {
		    ioh.registerOre(oreClass,ore);
	    }
    }

    /** Get the list of ores in a given class.
     */
    public static List<ItemStack> getOreClass(String oreClass) {
	    return oreDict.get(oreClass);
    }

    public static class OreQuery implements Iterable<Object[]> {
	    Object[] proto;

	    public class OreQueryIterator implements Iterator<Object[]> {
		    LinkedList itering;
		    LinkedList output;

		    private OreQueryIterator() {
			    itering=new LinkedList();
			    output=new LinkedList();

			    for(int i=0; i<proto.length; i++) {
				    if(proto[i] instanceof Collection) {
					    Iterator it=((Collection)proto[i])
						    .iterator();
					    if(!it.hasNext()) {
						    output=null; break;
					    }
					    itering.addLast(it);
					    output.addLast(it.next());
				    } else {
					    itering.addLast(proto[i]);
					    output.addLast(proto[i]);
				    }
			    }
		    }

		    public boolean hasNext() {
			    return output!=null;
		    }

		    public Object[] next() {
			    Object[] tr=output.toArray();

			    Object to;
			    while(true) {
				    if(itering.size()==0) {
					    output=null;
					    return tr;
				    }
				    to=itering.getLast();
				    output.removeLast();
				    if(to instanceof Iterator) {
					    Iterator it=(Iterator)to;
					    if(it.hasNext()) {
						    output.addLast(it.next());
						    break;
					    }
				    }
				    itering.removeLast();
			    }
			    for(int i=itering.size(); i<proto.length; i++) {
				    if(proto[i] instanceof Collection) {
					    Iterator it=((Collection)proto[i])
						    .iterator();
					    if(!it.hasNext()) {
						    output=null; break;
					    }
					    itering.addLast(it);
					    output.addLast(it.next());
				    } else {
					    itering.addLast(proto[i]);
					    output.addLast(proto[i]);
				    }
			    }
			    return tr;
		    }

		    public void remove() {}
	    }

	    private OreQuery(Object[] pattern) {
		    proto=pattern;
	    }

	    public Iterator<Object[]> iterator() {
		    return new OreQueryIterator();
	    }
    }

    /** Generate all valid legal recipe combinations.  Any Lists in pattern
     * will be fully expanded to all valid combinations.
     */
    public static OreQuery generateRecipes(Object... pattern) {
	    return new OreQuery(pattern);
    }

    // ------------------------------------------------------------

    /** Register a new plant to be planted when bonemeal is used on grass.
     * @param bid The block ID to plant.
     * @param md The metadata to plant.
     * @param prop The relative probability of the plant, where red flowers are
     * 10 and yellow flowers are 20.
     */
    public static void addGrassPlant(int item, int md, int prop) {
	    ForgeHooks.addPlantGrass(item,md,prop);
    }

    /** Register a new seed to be dropped when breaking tall grass.
     * @param bid The item ID of the seeds.
     * @param md The metadata of the seeds.
     * @param qty The quantity of seeds to drop.
     * @param prop The relative probability of the seeds, where wheat seeds are
     * 10.
     */
    public static void addGrassSeed(int item, int md, int qty, int prop) {
	    ForgeHooks.addGrassSeed(item,md,qty,prop);
    }

    /** Register a tool as a tool class with a given harvest level.
     *
     * @param tool The custom tool to register.
     * @param tclass The tool class to register as.  The predefined tool
     * clases are "pickaxe", "shovel", "axe".  You can add others for custom
     * tools.
     * @param hlevel The harvest level of the tool.
     */
    public static void setToolClass(Item tool, String tclass, int hlevel) {
	    ForgeHooks.initTools();
	    ForgeHooks.toolClasses.put(tool.shiftedIndex,
		Arrays.asList(tclass,hlevel));
    }

    /** Register a block to be harvested by a tool class.  This is the metadata
     * sensitive version, use it if your blocks are using metadata variants.
     * By default, this sets the block class as effective against that type.
     *
     * @param bl The block to register.
     * @param md The metadata for the block subtype.
     * @param tclass The tool class to register as able to remove this block.
     * You may register the same block multiple times with different tool
     * classes, if multiple tool types can be used to harvest this block.
     * @param hlevel The minimum tool harvest level required to successfully
     * harvest the block.
     * @see setToolClass for details on tool classes.
     */
    public static void setBlockHarvestLevel(Block bl, int md, String tclass, 
		int hlevel) {
	    ForgeHooks.initTools();
	    List key=Arrays.asList(bl.blockID,md,tclass);
	    ForgeHooks.toolHarvestLevels.put(key,hlevel);
	    ForgeHooks.toolEffectiveness.add(key);
    }

    /** Remove a block effectiveness mapping.  Since setBlockHarvestLevel
     * makes the tool class effective against the block by default, this can be
     * used to remove that mapping.  This will force a block to be harvested at
     * the same speed regardless of tool quality, while still requiring a given
     * harvesting level.
     * @param bl The block to remove effectiveness from.
     * @param md The metadata for the block subtype.
     * @param tclass The tool class to remove the effectiveness mapping from.
     * @see setToolClass for details on tool classes.
     */
    public static void removeBlockEffectiveness(Block bl, int md,
		    String tclass) {
	    ForgeHooks.initTools();
	    List key=Arrays.asList(bl.blockID,md,tclass);
	    ForgeHooks.toolEffectiveness.remove(key);
    }

    /** Register a block to be harvested by a tool class.
     * By default, this sets the block class as effective against that type.
     *
     * @param bl The block to register.
     * @param tclass The tool class to register as able to remove this block.
     * You may register the same block multiple times with different tool
     * classes, if multiple tool types can be used to harvest this block.
     * @param hlevel The minimum tool harvest level required to successfully
     * harvest the block.
     * @see setToolClass for details on tool classes.
     */
    public static void setBlockHarvestLevel(Block bl, String tclass, 
		int hlevel) {
	    ForgeHooks.initTools();
	    for(int md=0; md<16; md++) {
		    List key=Arrays.asList(bl.blockID,md,tclass);
		    ForgeHooks.toolHarvestLevels.put(key,hlevel);
		    ForgeHooks.toolEffectiveness.add(key);
	    }
    }

    /** Remove a block effectiveness mapping.  Since setBlockHarvestLevel
     * makes the tool class effective against the block by default, this can be
     * used to remove that mapping.  This will force a block to be harvested at
     * the same speed regardless of tool quality, while still requiring a given
     * harvesting level.
     * @param bl The block to remove effectiveness from.
     * @param tclass The tool class to remove the effectiveness mapping from.
     * @see setToolClass for details on tool classes.
     */
    public static void removeBlockEffectiveness(Block bl, String tclass) {
	    ForgeHooks.initTools();
	    for(int md=0; md<16; md++) {
		    List key=Arrays.asList(bl.blockID,md,tclass);
		    ForgeHooks.toolEffectiveness.remove(key);
	    }
    }

    /**
     * Kill minecraft with an error message.
     */
    public static void killMinecraft(String modname, String msg) {
	    throw new RuntimeException(modname+": "+msg);
    }

    /**
     * Version checking.  Ensures that a sufficiently recent version of Forge
     * is installed.  Will result in a fatal error if the major versions
     * mismatch or if the version is too old.  Will print a warning message if
     * the minor versions don't match.
     */
    public static void versionDetect(String modname,
		    int major, int minor, int revision) {
	    if(major!=ForgeHooks.majorVersion) {
		    killMinecraft(modname,"MinecraftForge Major Version Mismatch, expecting "+major+".x.x");
	    } else if(minor!=ForgeHooks.minorVersion) {
		    if(minor>ForgeHooks.minorVersion) {
			    killMinecraft(modname,"MinecraftForge Too Old, need at least "+major+"."+minor+"."+revision);
		    } else {
			    System.out.println(modname + ": MinecraftForge minor version mismatch, expecting "+major+"."+minor+".x, may lead to unexpected behavior");
		    }
	    } else if(revision>ForgeHooks.revisionVersion) {
		    killMinecraft(modname,"MinecraftForge Too Old, need at least "+major+"."+minor+"."+revision);
	    }
    }

    /**
     * Strict version checking.  Ensures that a sufficiently recent version of
     * Forge is installed.  Will result in a fatal error if the major or minor 
     * versions mismatch or if the version is too old.  Use this function for
     * mods that use recent, new, or unstable APIs to prevent
     * incompatibilities.
     */
    public static void versionDetectStrict(String modname, int major, int minor, int revision) 
    {
	    if(major!=ForgeHooks.majorVersion) 
	    {
		    killMinecraft(modname,"MinecraftForge Major Version Mismatch, expecting "+major+".x.x");
	    } 
	    else if(minor!=ForgeHooks.minorVersion) 
	    {
		    if(minor>ForgeHooks.minorVersion) 
		    {
			    killMinecraft(modname,"MinecraftForge Too Old, need at least "+major+"."+minor+"."+revision);
		    } 
		    else 
		    {
			    killMinecraft(modname,"MinecraftForge minor version mismatch, expecting "+major+"."+minor+".x");
		    }
	    } 
	    else if(revision>ForgeHooks.revisionVersion) 
	    {
		    killMinecraft(modname,"MinecraftForge Too Old, need at least "+major+"."+minor+"."+revision);
	    }
    }

	private static int dungeonLootAttempts = 8;
	private static ArrayList<ObjectPair<Float, String>> dungeonMobs = new ArrayList<ObjectPair<Float, String>>();
	private static ArrayList<ObjectPair<Float, DungeonLoot>> dungeonLoot = new ArrayList<ObjectPair<Float, DungeonLoot>>();
    /**
     * Set the number of item stacks that will be attempted to be added to each Dungeon chest.
     * Note: Due to random number generation, you will not always get this amount per chest.
     * @param number The maximum number of item stacks to add to a chest.
     */
    public static void setDungeonLootTries(int number)
    {
    	dungeonLootAttempts = number;
    }
    
    /**
     * @return The max number of item stacks found in each dungeon chest.
     */
    public static int getDungeonLootTries()
    {
    	return dungeonLootAttempts;
    }
    
    /**
     * Adds a mob to the possible list of creatures the spawner will create.
     * If the mob is already in the spawn list, the rarity will be added to the existing one,
     * causing the mob to be more common.
     *  
     * @param name The name of the monster, use the same name used when registering the entity.
     * @param rarity The rarity of selecting this mob over others. Must be greater then 0.
     *        Vanilla Minecraft has the following mobs:
     *        Spider   1
     *        Skeleton 1
     *        Zombie   2
     *        Meaning, Zombies are twice as common as spiders or skeletons.
     * @return The new rarity of the monster,         
     */
    public static float addDungeonMob(String name, float rarity)
    {
    	if (rarity <= 0)
    	{
    		throw new IllegalArgumentException("Rarity must be greater then zero");
    	}
    	
    	for(ObjectPair<Float, String> mob : dungeonMobs)
    	{
    		if (name.equals(mob.getValue2()))
    		{
    			mob.setValue1(mob.getValue1() + rarity);
    			return mob.getValue1();
    		}
    	}

    	dungeonMobs.add(new ObjectPair<Float, String>(rarity, name));
    	return rarity;
    }
    
    /**
     * Will completely remove a Mob from the dungeon spawn list.
     * 
     * @param name The name of the mob to remove
     * @return The rarity of the removed mob, prior to being removed.
     */
    public static float removeDungeonMob(String name)
    {
    	for (ObjectPair<Float, String> mob : dungeonMobs)
    	{
    		if (name.equals(name))
    		{
    			dungeonMobs.remove(mob);
    			return mob.getValue1();
    		}
    	}
    	return 0;
    }
    
    /**
     * Gets a random mob name from the list.
     * @param rand World generation random number generator
     * @return The mob name
     */
    public static String getRandomDungeonMob(Random rand)
    {
    	float maxRarity = 0f;
    	for (ObjectPair<Float, String> mob : dungeonMobs)
    	{
    		maxRarity += mob.getValue1();
    	}
    	
    	float targetRarity = rand.nextFloat() * maxRarity;
    	for (ObjectPair<Float, String> mob : dungeonMobs)
    	{
    		if (targetRarity < mob.getValue1()) 
    		{
    			return mob.getValue2();
    		}
    		targetRarity -= mob.getValue1();
    	}
    	
    	return "";
    }
    
    /**
     * Adds a item stack to the dungeon loot list with a stack size
     * of 1.
     * 
     * @param item The ItemStack to be added to the loot list
     * @param rarity The relative chance that this item will spawn, Vanilla has 
     * 			most of its items set to 1. Like the saddle, bread, silk, wheat, etc..
     * 			Rarer items are set to lower values, EXA: Golden Apple 0.01
     */
    public static void addDungeonLoot(ItemStack item, float rarity)
    {
    	addDungeonLoot(item, rarity, 1, 1);
    }
    
    /**
     * Adds a item stack, with a range of sizes, to the dungeon loot list.
     * If a stack matching the same item, and size range, is already in the list
     * the rarities will be added together making the item more common.
     * 
     * @param item The ItemStack to be added to the loot list
     * @param rarity The relative chance that this item will spawn, Vanilla has 
     * 			most of its items set to 1. Like the saddle, bread, silk, wheat, etc..
     * 			Rarer items are set to lower values, EXA: Golden Apple 0.01
     * @param minCount When this item does generate, the minimum number that is in the stack
     * @param maxCount When this item does generate, the maximum number that can bein the stack
     * @return The new rarity of the loot. 
     */
    public static float addDungeonLoot(ItemStack item, float rarity, int minCount, int maxCount)
    {
    	for (ObjectPair<Float, DungeonLoot> loot : dungeonLoot)
    	{
    		if (loot.getValue2().equals(item, minCount, maxCount))
    		{
    			loot.setValue1(loot.getValue1() + rarity);
    			return loot.getValue1();
    		}
    	}
    	
    	dungeonLoot.add(new ObjectPair<Float, DungeonLoot>(rarity, new DungeonLoot(item, minCount, maxCount)));
    	return rarity;    	
    }
    /**
     * Removes a item stack from the dungeon loot list, this will remove all items
     * as long as the item stack matches, it will not care about matching the stack
     * size ranges perfectly.
     * 
     * @param item The item stack to remove
     * @return The total rarity of all items removed
     */
    public static float removeDungeonLoot(ItemStack item)
    {
    	return removeDungeonLoot(item, -1, 0);
    }
    
    /**
     * Removes a item stack from the dungeon loot list. If 'minCount' parameter 
     * is greater then 0, it will only remove loot items that have the same exact 
     * stack size range as passed in by parameters.
     * 
     * @param item The item stack to remove
     * @param minCount The minimum count for the match check, if less then 0, 
     * 			the size check is skipped
     * @param maxCount The max count used in match check when 'minCount' is >= 0
     * @return The total rarity of all items removed
     */
    public static float removeDungeonLoot(ItemStack item, int minCount, int maxCount)
    {
    	float rarity = 0;
    	ArrayList<ObjectPair<Float, DungeonLoot>> lootTmp = (ArrayList<ObjectPair<Float, DungeonLoot>>)dungeonLoot.clone();
    	if (minCount < 0)
    	{
	    	for (ObjectPair<Float, DungeonLoot> loot : lootTmp)
	    	{
	    		if (loot.getValue2().equals(item))
	    		{
	    			dungeonLoot.remove(loot);
	    			rarity += loot.getValue1();
	    		}
	    	}
    	}
    	else
    	{
	    	for (ObjectPair<Float, DungeonLoot> loot : lootTmp)
	    	{
	    		if (loot.getValue2().equals(item, minCount, maxCount))
	    		{
	    			dungeonLoot.remove(loot);
	    			rarity += loot.getValue1();
	    		}
	    	}
    	}
    	
    	return rarity;  
    }

    /**
     * Gets a random item stack to place in a dungeon chest during world generation
     * @param rand World generation random number generator
     * @return The item stack
     */
    public static ItemStack getRandomDungeonLoot(Random rand)
    {
    	float maxRarity = 0f;
    	for (ObjectPair<Float, DungeonLoot> loot : dungeonLoot)
    	{
    		maxRarity += loot.getValue1();
    	}
    	
    	float targetRarity = rand.nextFloat() * maxRarity;
    	for (ObjectPair<Float, DungeonLoot> loot : dungeonLoot)
    	{
    		if (targetRarity < loot.getValue1()) 
    		{
    			return loot.getValue2().generateStack(rand);
    		}
    		targetRarity -= loot.getValue1();
    	}
    	
    	return null;
    }
    

    //Minecart Dictionary --------------------------------------
    private static Map<MinecartKey, ItemStack> itemForMinecart = new HashMap<MinecartKey, ItemStack>();
    private static Map<ItemStack, MinecartKey> minecartForItem = new HashMap<ItemStack, MinecartKey>();
    /**
     * Registers a custom minecart and its corresponding item.
     * This should be the item used to place the minecart by the user,
     * not the item dropped by the cart.
     * @param cart The minecart.
     * @param item The item used to place the cart.
     */
    public static void registerMinecart(Class<? extends EntityMinecart> cart, ItemStack item)
    {
        registerMinecart(cart, 0, item);
    }

    /**
     * Registers a minecart and its corresponding item.
     * This should be the item used to place the minecart by the user,
     * not the item dropped by the cart.
     * @param minecart The minecart.
     * @param type The minecart type, used to differentiate carts that have the same class.
     * @param item The item used to place the cart.
     */
    public static void registerMinecart(Class<? extends EntityMinecart> minecart, int type, ItemStack item)
    {
        MinecartKey k = new MinecartKey(minecart, type);
        itemForMinecart.put(k, item);
        minecartForItem.put(item, k);
    }    
    
    /**
     * Removes a previously registered Minecart. Useful for replacing the vanilla minecarts.
     * @param minecart
     * @param type 
     */
    public static void removeMinecart(Class<? extends EntityMinecart> minecart, int type)
    {
        MinecartKey k = new MinecartKey(minecart, type);
        ItemStack item = itemForMinecart.remove(k);
        if(item != null) 
        {
            minecartForItem.remove(item);
        }
    }    
    
    /**
     * This function returns an ItemStack that represents this cart.
     * The player should be able to use this item to place the minecart.
     * This is the item that was registered with the cart via the registerMinecart function,
     * but is not necessary the item the cart drops when destroyed.
     * @param minecart The cart class
     * @return An ItemStack that can be used to place the cart.
     */
    public static ItemStack getItemForCart(Class<? extends EntityMinecart> minecart)
    {
        return getItemForCart(minecart, 0);
    }

    /**
     * This function returns an ItemStack that represents this cart.
     * The player should be able to use this item to place the minecart.
     * This is the item that was registered with the cart via the registerMinecart function,
     * but is not necessary the item the cart drops when destroyed.
     * @param minecart The cart class
     * @param type The minecartType value
     * @return An ItemStack that can be used to place the cart.
     */
    public static ItemStack getItemForCart(Class<? extends EntityMinecart> minecart, int type)
    {
        ItemStack item = itemForMinecart.get(new MinecartKey(minecart, type));
        if (item == null)
        {
        	return null;
        }
        return item.copy();
    }

    /**
     * This function returns an ItemStack that represents this cart.
     * The player should be able to use this item to place the minecart.
     * This is the item that was registered with the cart via the registerMinecart function,
     * but is not necessary the item the cart drops when destroyed.
     * @param cart The cart entity
     * @return An ItemStack that can be used to place the cart.
     */
    public static ItemStack getItemForCart(EntityMinecart cart)
    {
        if(cart.getClass() == EntityMinecart.class) 
        {
            return getItemForCart(cart.getClass(), cart.getMinecartType());
        }
        return getItemForCart(cart.getClass(), 0);
    }

    /**
     * The function will return the cart class for a given item.
     * If the item was not registered via the registerMinecart function it will return null.
     * @param item The item to test.
     * @return Cart if mapping exists, null if not.
     */
    public static Class<? extends EntityMinecart> getCartClassForItem(ItemStack item)
    {
        MinecartKey k = null;
    	for (ItemStack key : minecartForItem.keySet())
    	{
    		if (key.isItemEqual(item))
    		{
    			k = minecartForItem.get(k);
    			break;
    		}
    	}
        if(k != null) 
        {
            return k.minecart;
        }
        return null;
    }

    /**
     * The function will return the cart type for a given item.
     * Will return -1 if the mapping doesn't exist.
     * If the item was not registered via the registerMinecart function it will return null.
     * @param item The item to test.
     * @return the cart minecartType value.
     */
    public static int getCartTypeForItem(ItemStack item)
    {
        MinecartKey k = null;
    	for (ItemStack key : minecartForItem.keySet())
    	{
    		if (key.isItemEqual(item))
    		{
    			k = minecartForItem.get(k);
    			break;
    		}
    	}
        if(k != null) 
        {
            return k.type;
        }
        return -1;
    }

    /**
     * Will return a set of all registered minecart items.
     * @return a copy of the set of all minecart items
     */
    public static Set<ItemStack> getAllCartItems()
    {
    	Set<ItemStack> ret = new HashSet<ItemStack>();
    	for(ItemStack item : minecartForItem.keySet())
    	{
    		ret.add(item.copy());
    	}
        return ret;
    }
    
    /**
     * Registers a Entity class tracking information. Used for sendingEntity 
     * information over the network. 
     *  
     * @param entityClass The class for the Entity
     * @param mod The BaseMod that provides this Entity.
     * @param ID The ID for the Entity. Needs to be unique combination of Mod and ID.
     * @param range How close a player has to be to be informed this Entity exists.
     * @param updateFrequancy How many ticks between checking and sending information updates for this Entity.
     * @param sendVelocityInfo If velocity information should be included in the update information.
     * @return True, if successfully registered. False if the class is already registered.
     */
    public static boolean registerEntity(Class entityClass, BaseMod mod, int ID, int range, int updateFrequancy, boolean sendVelocityInfo)
    {
        if (ForgeHooks.entityTrackerMap.containsKey(entityClass))
        {
            return false;
        }
        ForgeHooks.entityTrackerMap.put(entityClass, new EntityTrackerInfo(mod, ID, range, updateFrequancy, sendVelocityInfo));
        return true;
    }
    
    /**
     * Retrieves the tracker info for input Entity.
     * 
     * @param entity The Entity to find tracking info for.
     * @param checkSupers If we should check the super-classes for a match. 
     * @return The information, or Null if not found.
     */
    public static EntityTrackerInfo getEntityTrackerInfo(Entity entity, boolean checkSupers)
    {
        for (Map.Entry<Class, EntityTrackerInfo> entry : ForgeHooks.entityTrackerMap.entrySet())
        {
            if (entry.getKey().isInstance(entity))
            {
                if (!checkSupers || entry.getKey() == entry.getClass())
                {
                    return entry.getValue();
                }
            }
        }
        return null;
    }
    
    /**
     * Retrieves the Class for a registered Entity based on ModID and Entity Type.
     * 
     * @param modID The ID of the mod (mod.toString().hashCode())
     * @param type The ID for the Entity
     * @return The entity Class, or null if not found.
     */
    public static Class getEntityClass(int modID, int type)
    {
        for (Map.Entry<Class, EntityTrackerInfo> entry : ForgeHooks.entityTrackerMap.entrySet())
        {
            EntityTrackerInfo info = entry.getValue();
            if (type == info.ID && modID == info.Mod.toString().hashCode())
            {
                return entry.getKey();
            }
        }
        return null;
    }
    
    /**
     * Retrieves a mod instance based on it's ID. (toString().hashCode())
     * 
     * @param id The mod ID
     * @return The mod, or null if not found
     */
    public static BaseMod getModByID(int id)
    {
        for(BaseMod mod : (List<BaseMod>)ModLoader.getLoadedMods())
        {
            if (mod.toString().hashCode() == id)
            {
                return mod;
            }
        }
        return null;
    }
  
        
    static 
    {
    	addDungeonMob("Skeleton", 1.0f);
    	addDungeonMob("Zombie",   2.0f);
    	addDungeonMob("Spider",   1.0f);
    	
    	addDungeonLoot(new ItemStack(Item.saddle),          1.00f      );
    	addDungeonLoot(new ItemStack(Item.ingotIron),       1.00f, 1, 4);
    	addDungeonLoot(new ItemStack(Item.bread),           1.00f      );
    	addDungeonLoot(new ItemStack(Item.wheat),           1.00f, 1, 4);
    	addDungeonLoot(new ItemStack(Item.gunpowder),       1.00f, 1, 4);
    	addDungeonLoot(new ItemStack(Item.silk),            1.00f, 1, 4);
    	addDungeonLoot(new ItemStack(Item.bucketEmpty),     1.00f      );
    	addDungeonLoot(new ItemStack(Item.appleGold),       0.01f      );
    	addDungeonLoot(new ItemStack(Item.redstone),        0.50f, 1, 4);
    	addDungeonLoot(new ItemStack(Item.record13),        0.05f      );
    	addDungeonLoot(new ItemStack(Item.recordCat),       0.05f      );
    	addDungeonLoot(new ItemStack(Item.dyePowder, 1, 3), 1.00f      );
    	
        registerMinecart(EntityMinecart.class, 0, new ItemStack(Item.minecartEmpty));
        registerMinecart(EntityMinecart.class, 1, new ItemStack(Item.minecartCrate));
        registerMinecart(EntityMinecart.class, 2, new ItemStack(Item.minecartPowered));
    }

}
