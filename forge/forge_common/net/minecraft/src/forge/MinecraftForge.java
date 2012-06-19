/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.Achievement;
import net.minecraft.src.BaseMod;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityMinecart;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.ModLoader;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet131MapData;
import net.minecraft.src.Packet132TileEntityData;
import net.minecraft.src.World;
import net.minecraft.src.forge.oredict.OreDictionary;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.Map.Entry;

public class MinecraftForge
{
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
     * Registers a new Chunk Load event handler
     * @param handler The Handler to be registered
     */
    public static void registerChunkLoadHandler(IChunkLoadHandler handler)
    {
        ForgeHooks.chunkLoadHandlers.add(handler);
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
     * Register a new entity interact handler.
     * @param handler The Handler to be registered
     */
    public static void registerEntityInteractHandler(IEntityInteractHandler handler)
    {
        ForgeHooks.entityInteractHandlers.add(handler);
    }

    /**
     * Registers a new chat handler.
     * @param handler The Handler to be registered
     */
    public static void registerChatHandler(IChatHandler handler)
    {
        ForgeHooks.chatHandlers.add(handler);
    }
    
    /**
     * Register a new Save handler
     * @param handler The handler to be registered
     */
    public static void registerSaveHandler(ISaveEventHandler handler)
    {
        ForgeHooks.saveHandlers.add(handler);
    }

    /**
     * Register a new Fuel handler
     * @param handler The handler to be registered
     */
    public static void registerFuelHandler(IFuelHandler handler)
    {
        ForgeHooks.fuelHandlers.add(handler);
    }

    /**
     * Register a new Special Mob Spawn handler
     * @param handler The handler to be registered
     */
    @Deprecated
    public static void registerSpecialMobSpawnHandler(ISpecialMobSpawnHandler handler)
    {
        if (EntityLiving.class.getPackage() != null)
        {
            throw new RuntimeException("Still using deprecated method/interface MinecraftForge.registerSpecialModSpawnHandler()");
        }
        ForgeHooks.specialMobSpawnHandlers.add(handler);
    }

    /**
     * Register a new EntityLiving Handler
     * @param handler The handler to be registered
     */
    public static void registerEntityLivingHandler(IEntityLivingHandler handler)
    {
        ForgeHooks.entityLivingHandlers.add(handler);
    }    
    

    /**
     * This is not supposed to be called outside of Minecraft internals.
     */
    public static ItemStack fillCustomBucket(World world, int X, int Y, int Z)
    {
        for (IBucketHandler handler : bucketHandlers)
        {
            ItemStack stack = handler.fillCustomBucket(world, X, Y, Z);

            if (stack != null)
            {
                return stack;
            }
        }

        return null;
    }

    // Ore Dictionary
    // ------------------------------------------------------------
    //Deprecated in favor of OreDictionary.registerOreHandler
    @Deprecated
    public static void registerOreHandler(IOreHandler handler)
    {
        OreDictionary.registerOreHandler(handler);
    }

    //Deprecated in favor of OreDictionary.registerOre
    @Deprecated
    public static void registerOre(String oreClass, ItemStack ore)
    {
        OreDictionary.registerOre(oreClass, ore);
    }

    //Deprecated in favor of OreDictionary.getOres
    @Deprecated
    public static List<ItemStack> getOreClass(String oreClass)
    {
        return OreDictionary.getOres(oreClass);
    }

    //Deprecated in favor of the Ore recipes, and because it is ugly as heck.
    @Deprecated
    public static class OreQuery implements Iterable<Object[]>
    {
        Object[] proto;

        public class OreQueryIterator implements Iterator<Object[]>
        {
            LinkedList itering = new LinkedList();
            LinkedList output = new LinkedList();

            private OreQueryIterator()
            {
                for (Object input : proto)
                {
                    if (input instanceof Collection)
                    {
                        Iterator it = ((Collection)input).iterator();
                        if (!it.hasNext())
                        {
                            output = null;
                            break;
                        }
                        itering.addLast(it);
                        output.addLast(it.next());
                    }
                    else
                    {
                        itering.addLast(input);
                        output.addLast(input);
                    }
                }
            }

            public boolean hasNext()
            {
                return output != null;
            }

            public Object[] next()
            {
                Object[] tr = output.toArray();
                Object to;
                while (true)
                {
                    if (itering.size() == 0)
                    {
                        output = null;
                        return tr;
                    }
                    to = itering.getLast();
                    output.removeLast();
                    if (to instanceof Iterator)
                    {
                        Iterator it = (Iterator)to;
                        if (it.hasNext())
                        {
                            output.addLast(it.next());
                            break;
                        }
                    }
                    itering.removeLast();
                }
                for (int i = itering.size(); i < proto.length; i++)
                {
                    if (proto[i] instanceof Collection)
                    {
                        Iterator it = ((Collection)proto[i]).iterator();
                        if (!it.hasNext())
                        {
                            output = null;
                            break;
                        }
                        itering.addLast(it);
                        output.addLast(it.next());
                    }
                    else
                    {
                        itering.addLast(proto[i]);
                        output.addLast(proto[i]);
                    }
                }
                return tr;
            }

            public void remove() {}
        }

        private OreQuery(Object[] pattern)
        {
            proto = pattern;
        }

        public Iterator<Object[]> iterator()
        {
            return new OreQueryIterator();
        }
    }

    /** Generate all valid legal recipe combinations.  Any Lists in pattern
     * will be fully expanded to all valid combinations.
     */
    //Deprecated in favor of the new Ore Recipe system
    @Deprecated
    public static OreQuery generateRecipes(Object... pattern)
    {
        return new OreQuery(pattern);
    }

    // ------------------------------------------------------------

    /** Register a new plant to be planted when bonemeal is used on grass.
     * @param bid The block ID to plant.
     * @param metadata The metadata to plant.
     * @param probability The relative probability of the plant, where red flowers are
     * 10 and yellow flowers are 20.
     */
    public static void addGrassPlant(int blockID, int metadata, int probability)
    {
        ForgeHooks.addPlantGrass(blockID, metadata, probability);
    }

    /** Register a new seed to be dropped when breaking tall grass.
     * @param bid The item ID of the seeds.
     * @param metadata The metadata of the seeds.
     * @param quantity The quantity of seeds to drop.
     * @param probability The relative probability of the seeds, where wheat seeds are
     * 10.
     */
    public static void addGrassSeed(int itemID, int metadata, int quantity, int probability)
    {
        ForgeHooks.addGrassSeed(itemID, metadata, quantity, probability);
    }

    /** Register a tool as a tool class with a given harvest level.
     *
     * @param tool The custom tool to register.
     * @param toolClass The tool class to register as.  The predefined tool
     * clases are "pickaxe", "shovel", "axe".  You can add others for custom
     * tools.
     * @param harvestLevel The harvest level of the tool.
     */
    public static void setToolClass(Item tool, String toolClass, int harvestLevel)
    {
        ForgeHooks.initTools();
        ForgeHooks.toolClasses.put(tool.shiftedIndex, Arrays.asList(toolClass, harvestLevel));
    }

    /** Register a block to be harvested by a tool class.  This is the metadata
     * sensitive version, use it if your blocks are using metadata variants.
     * By default, this sets the block class as effective against that type.
     *
     * @param block The block to register.
     * @param metadata The metadata for the block subtype.
     * @param toolClass The tool class to register as able to remove this block.
     * You may register the same block multiple times with different tool
     * classes, if multiple tool types can be used to harvest this block.
     * @param harvestLevel The minimum tool harvest level required to successfully
     * harvest the block.
     * @see setToolClass for details on tool classes.
     */
    public static void setBlockHarvestLevel(Block block, int metadata, String toolClass, int harvestLevel)
    {
        ForgeHooks.initTools();
        List key = Arrays.asList(block.blockID, metadata, toolClass);
        ForgeHooks.toolHarvestLevels.put(key, harvestLevel);
        ForgeHooks.toolEffectiveness.add(key);
    }

    /** Remove a block effectiveness mapping.  Since setBlockHarvestLevel
     * makes the tool class effective against the block by default, this can be
     * used to remove that mapping.  This will force a block to be harvested at
     * the same speed regardless of tool quality, while still requiring a given
     * harvesting level.
     * @param block The block to remove effectiveness from.
     * @param metadata The metadata for the block subtype.
     * @param toolClass The tool class to remove the effectiveness mapping from.
     * @see setToolClass for details on tool classes.
     */
    public static void removeBlockEffectiveness(Block block, int metadata, String toolClass)
    {
        ForgeHooks.initTools();
        List key = Arrays.asList(block.blockID, metadata, toolClass);
        ForgeHooks.toolEffectiveness.remove(key);
    }

    /** Register a block to be harvested by a tool class.
     * By default, this sets the block class as effective against that type.
     *
     * @param block The block to register.
     * @param toolClass The tool class to register as able to remove this block.
     * You may register the same block multiple times with different tool
     * classes, if multiple tool types can be used to harvest this block.
     * @param harvestLevel The minimum tool harvest level required to successfully
     * harvest the block.
     * @see setToolClass for details on tool classes.
     */
    public static void setBlockHarvestLevel(Block block, String toolClass, int harvestLevel)
    {
        ForgeHooks.initTools();
        for (int metadata = 0; metadata < 16; metadata++)
        {
            List key = Arrays.asList(block.blockID, metadata, toolClass);
            ForgeHooks.toolHarvestLevels.put(key, harvestLevel);
            ForgeHooks.toolEffectiveness.add(key);
        }
    }
    
    /** Returns the block harvest level for a particular tool class.
     *
     * @param block The block to check.
     * @param metadata The metadata for the block subtype.
     * @param toolClass The tool class to check as able to remove this block.
     * @see setToolClass for details on tool classes.
     * @return The harvest level or -1 if no mapping exists.
     */
    public static int getBlockHarvestLevel(Block block, int metadata, String toolClass)
    {
        ForgeHooks.initTools();
        List key = Arrays.asList(block.blockID, metadata, toolClass);
        Integer harvestLevel = (Integer)ForgeHooks.toolHarvestLevels.get(key);
        if(harvestLevel == null)
        {
            return -1;
        }
        return harvestLevel;
    }

    /** Remove a block effectiveness mapping.  Since setBlockHarvestLevel
     * makes the tool class effective against the block by default, this can be
     * used to remove that mapping.  This will force a block to be harvested at
     * the same speed regardless of tool quality, while still requiring a given
     * harvesting level.
     * @param block The block to remove effectiveness from.
     * @param toolClass The tool class to remove the effectiveness mapping from.
     * @see setToolClass for details on tool classes.
     */
    public static void removeBlockEffectiveness(Block block, String toolClass)
    {
        ForgeHooks.initTools();
        for (int metadata = 0; metadata < 16; metadata++)
        {
            List key = Arrays.asList(block.blockID, metadata, toolClass);
            ForgeHooks.toolEffectiveness.remove(key);
        }
    }

    /**
     * Kill minecraft with an error message.
     */
    public static void killMinecraft(String mod, String message)
    {
        throw new RuntimeException(mod + ": " + message);
    }

    /**
     * Version checking.  Ensures that a sufficiently recent version of Forge
     * is installed.  Will result in a fatal error if the major versions
     * mismatch or if the version is too old.  Will print a warning message if
     * the minor versions don't match.
     */
    public static void versionDetect(String mod, int major, int minor, int revision)
    {
        if (major != ForgeHooks.majorVersion)
        {
            killMinecraft(mod, "MinecraftForge Major Version Mismatch, expecting " + major + ".x.x");
        }
        else if (minor != ForgeHooks.minorVersion)
        {
            if (minor > ForgeHooks.minorVersion)
            {
                killMinecraft(mod, "MinecraftForge Too Old, need at least " + major + "." + minor + "." + revision);
            }
            else
            {
                System.out.println(mod + ": MinecraftForge minor version mismatch, expecting " + major + "." + minor + ".x, may lead to unexpected behavior");
            }
        }
        else if (revision > ForgeHooks.revisionVersion)
        {
            killMinecraft(mod, "MinecraftForge Too Old, need at least " + major + "." + minor + "." + revision);
        }
    }

    /**
     * Strict version checking.  Ensures that a sufficiently recent version of
     * Forge is installed.  Will result in a fatal error if the major or minor
     * versions mismatch or if the version is too old.  Use this function for
     * mods that use recent, new, or unstable APIs to prevent
     * incompatibilities.
     */
    public static void versionDetectStrict(String mod, int major, int minor, int revision)
    {
        if (major != ForgeHooks.majorVersion)
        {
            killMinecraft(mod, "MinecraftForge Major Version Mismatch, expecting " + major + ".x.x");
        }
        else if (minor != ForgeHooks.minorVersion)
        {
            if (minor > ForgeHooks.minorVersion)
            {
                killMinecraft(mod, "MinecraftForge Too Old, need at least " + major + "." + minor + "." + revision);
            }
            else
            {
                killMinecraft(mod, "MinecraftForge minor version mismatch, expecting " + major + "." + minor + ".x");
            }
        }
        else if (revision > ForgeHooks.revisionVersion)
        {
            killMinecraft(mod, "MinecraftForge Too Old, need at least " + major + "." + minor + "." + revision);
        }
    }
    
    /**
     * Forge Branding info used by FML to display on the client's main screen.
     * @return 'Minecraft Forge vx.x.x.x'
     */
    public static String getVersionString() 
    {
        return String.format("Minecraft Forge %d.%d.%d.%d", ForgeHooks.majorVersion, ForgeHooks.minorVersion, ForgeHooks.revisionVersion, ForgeHooks.buildVersion);
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

        for (ObjectPair<Float, String> mob : dungeonMobs)
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
     *          most of its items set to 1. Like the saddle, bread, silk, wheat, etc..
     *          Rarer items are set to lower values, EXA: Golden Apple 0.01
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
     *          most of its items set to 1. Like the saddle, bread, silk, wheat, etc..
     *          Rarer items are set to lower values, EXA: Golden Apple 0.01
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
     *          the size check is skipped
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

    //Achievement Pages ----------------------------------------
    private static LinkedList<AchievementPage> achievementPages = new LinkedList<AchievementPage>();
    
    /**
     * Registers an achievement page.
     * @param page The page.
     */
    public static void registerAchievementPage(AchievementPage page)
    {
        if (getAchievementPage(page.getName()) != null)
        {
            throw new RuntimeException("Duplicate achievement page name \"" + page.getName() + "\"!");
        }
        achievementPages.add(page);
    }
    
    /**
     * Will return an achievement page by its index on the list.
     * @param index The page's index.
     * @return the achievement page corresponding to the index or null if invalid index
     */
    public static AchievementPage getAchievementPage(int index)
    {
        return achievementPages.get(index);
    }
    
    /**
     * Will return an achievement page by its name.
     * @param name The page's name.
     * @return the achievement page with the given name or null if no such page
     */
    public static AchievementPage getAchievementPage(String name)
    {
        for (AchievementPage page : achievementPages)
        {
            if (page.getName().equals(name))
            {
                return page;
            }
        }
        return null;
    }
    
    /**
     * Will return the list of achievement pages.
     * @return the list's size
     */
    public static Set<AchievementPage> getAchievementPages()
    {
        return new HashSet<AchievementPage>(achievementPages);
    }
    
    /**
     * Will return whether an achievement is in any page or not.
     * @param achievement The achievement.
     */
    public static boolean isAchievementInPages(Achievement achievement)
    {
        for (AchievementPage page : achievementPages)
        {
            if (page.getAchievements().contains(achievement)) 
            {
                return true;
            }
        }
        return false;
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
        MinecartKey key = new MinecartKey(minecart, type);
        itemForMinecart.put(key, item);
        minecartForItem.put(item, key);
    }

    /**
     * Removes a previously registered Minecart. Useful for replacing the vanilla minecarts.
     * @param minecart
     * @param type
     */
    public static void removeMinecart(Class<? extends EntityMinecart> minecart, int type)
    {
        MinecartKey key = new MinecartKey(minecart, type);
        ItemStack item = itemForMinecart.remove(key);
        if (item != null)
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
        return getItemForCart(cart.getClass(), cart.getMinecartType());
    }

    /**
     * The function will return the cart class for a given item.
     * If the item was not registered via the registerMinecart function it will return null.
     * @param item The item to test.
     * @return Cart if mapping exists, null if not.
     */
    public static Class<? extends EntityMinecart> getCartClassForItem(ItemStack item)
    {
        MinecartKey key = null;
        for (Map.Entry<ItemStack, MinecartKey> entry : minecartForItem.entrySet())
        {
            if (entry.getKey().isItemEqual(item))
            {
                key = entry.getValue();
                break;
            }
        }
        if (key != null)
        {
            return key.minecart;
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
        MinecartKey key = null;
        for (Map.Entry<ItemStack, MinecartKey> entry : minecartForItem.entrySet())
        {
            if (entry.getKey().isItemEqual(item))
            {
                key = entry.getValue();
                break;
            }
        }
        if (key != null)
        {
            return key.type;
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
        for (ItemStack item : minecartForItem.keySet())
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
     * @param updateFrequency How many ticks between checking and sending information updates for this Entity.
     * @param sendVelocityInfo If velocity information should be included in the update information.
     * @return True, if successfully registered. False if the class is already registered.
     */
    public static boolean registerEntity(Class entityClass, NetworkMod mod, int ID, int range, int updateFrequency, boolean sendVelocityInfo)
    {
        if (ForgeHooks.entityTrackerMap.containsKey(entityClass))
        {
            return false;
        }
        ForgeHooks.entityTrackerMap.put(entityClass, new EntityTrackerInfo(mod, ID, range, updateFrequency, sendVelocityInfo));
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
                if (!checkSupers || entry.getKey() == entity.getClass())
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
            if (type == info.ID && modID == getModID(info.Mod))
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
    public static NetworkMod getModByID(int id)
    {
        return ForgeHooks.networkMods.get(id);
    }

    /**
     * Returns a unique index number for the specific mod.
     *
     * @param mod The mod to find
     * @return The index number, -1 if no index found
     */
    public static int getModID(NetworkMod mod)
    {
        for (Entry<Integer, NetworkMod> entry : ForgeHooks.networkMods.entrySet())
        {
            if (entry.getValue() == mod)
            {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * Returns a list of mods that are designed to be used over the network.
     * @return
     */
    public static NetworkMod[] getNetworkMods()
    {
        ArrayList<NetworkMod> ret = new ArrayList<NetworkMod>();
        for (BaseMod mod : (List<BaseMod>)ModLoader.getLoadedMods())
        {
            if (mod instanceof NetworkMod)
            {
                ret.add((NetworkMod)mod);
            }
        }
        return ret.toArray(new NetworkMod[0]);
    }

    /**
     * Sets the GuiHandler associated with a mod.
     *
     * @param mod The mod
     * @param handler The Gui Handler
     */
    public static void setGuiHandler(BaseMod mod, IGuiHandler handler)
    {
        ForgeHooks.guiHandlers.put(mod, handler);
    }

    /**
     * Gets the GuiHandler associated with a mod
     *
     * @param mod The mod
     * @return The handler, or null if none associated.
     */
    public static IGuiHandler getGuiHandler(BaseMod mod)
    {
        return ForgeHooks.guiHandlers.get(mod);
    }

    /**
     * Registers a new Arrow Nock event handler
     * @param handler The Handler to be registered
     */
    public static void registerArrowNockHandler(IArrowNockHandler handler)
    {
        ForgeHooks.arrowNockHandlers.add(handler);
    }

    /**
     * Registers a new Arrow Loose event handler
     * @param handler The Handler to be registered
     */
    public static void registerArrowLooseHandler(IArrowLooseHandler handler)
    {
        ForgeHooks.arrowLooseHandlers.add(handler);
    }
    
    /**
     * Sends a packet on the specified NetworkManager
     * 
     * @param net The manager to send the packet on
     * @param packet The packet to be sent
     */
    public static void sendPacket(NetworkManager net, Packet packet)
    {
        ForgeHooks.getPacketHandler().sendPacket(net, packet);
    }
    
    /**
     * Sends a 'small' payload packet to the specified manager.
     * It uses the Packet131MapData packet for it's communication
     * so things are limited.
     * 
     * @param net The manager to send the packet to
     * @param mod The mod associated with this packet
     * @param id The ID number used to identify this packet
     * @param data The data to be sent, must be no larger then 255 bytes.
     */
    public static void sendPacket(NetworkManager net, NetworkMod mod, short id, byte[] data)
    {   
        if (data == null)
        {
            data = new byte[0];
        }
        
        if (data.length > 255)
        {
            throw new IllegalArgumentException(String.format("Data argument was to long, must not be longer then 255 bytes was %d", data.length));
        }
        
        Packet131MapData pkt = new Packet131MapData();
        pkt.itemID   = (short)getModID(mod);
        pkt.uniqueID = id;
        pkt.itemData = data;
        sendPacket(net, pkt);
    }
    
    /**
     * Helper function for wrapping and sending a Packet132TileEntityData packet,
     * useful so we don't have to edit the packet class itself to add the constructor on the client side.
     * 
     * @param net The manager to send the packet to
     * @param x Position X
     * @param y Position Y
     * @param z Position Z
     * @param action Action ID
     * @param par1 Custom Parameter 1
     * @param par2 Custom Parameter 2
     * @param par3 Custom Parameter 3
     */
    public static void sendTileEntityPacket(NetworkManager net, int x, short y, int z, byte action, int par1, int par2, int par3)
    {
        Packet132TileEntityData pkt = new Packet132TileEntityData();
        pkt.xPosition    = x;
        pkt.yPosition    = y;
        pkt.zPosition    = z;
        pkt.actionType   = action;
        pkt.customParam1 = par1;
        pkt.customParam2 = par2;
        pkt.customParam3 = par3;
        sendPacket(net, pkt);
    }
    
    private static int isClient = -1;
    public static boolean isClient()
    {
        if (isClient == -1)
        {
            try 
            {
                Class.forName("net.minecraft.client.Minecraft", false, MinecraftForge.class.getClassLoader());
                isClient = 1;
            } 
            catch (ClassNotFoundException e) 
            {
                isClient = 0;
            }   
        }
        return isClient == 1;
    }
    
    /**
     * Method invoked by FML before any other mods are loaded.
     */
    public static void initialize()
    {
        //Cause the classes to initialize if they already haven't
        Block.stone.getTextureFile();
        Item.appleGold.getTextureFile();
        
        Block filler = null;
        try 
        {
            filler = Block.class.getConstructor(int.class, Material.class).newInstance(256, Material.air);
        }catch (Exception e){}
        
        if (filler == null)
        {
            throw new RuntimeException("Could not create Forge filler block");
        }
        
        for (int x = 256; x < 4096; x++)
        {
            if (Item.itemsList[x - 256] != null)
            {
                Block.blocksList[x] = filler;
            }
        }
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
