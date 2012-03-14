package net.minecraft.src;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.DigestException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Map.Entry;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.imageio.ImageIO;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.forge.DimensionManager;
import net.minecraft.src.forge.ForgeHooks;
import net.minecraft.src.forge.NetworkMod;

public final class ModLoader
{
    private static final List animList = new LinkedList();
    private static final Map blockModels = new HashMap();
    private static final Map blockSpecialInv = new HashMap();
    private static File cfgdir;
    private static File cfgfile;
    public static Level cfgLoggingLevel = Level.FINER;
    private static Map classMap = null;
    private static long clock = 0L;
    public static final boolean DEBUG = false;
    private static Field field_animList = null;
    private static Field field_armorList = null;
    private static Field field_modifiers = null;
    private static Field field_TileEntityRenderers = null;
    private static boolean hasInit = false;
    private static int highestEntityId = 3000;
    private static final Map inGameHooks = new HashMap();
    private static final Map inGUIHooks = new HashMap();
    private static MinecraftServer instance = null;
    private static int itemSpriteIndex = 0;
    private static int itemSpritesLeft = 0;
    private static final Map keyList = new HashMap();
    private static String langPack = null;
    private static Map localizedStrings = new HashMap();
    private static File logfile;
    private static final Logger logger = Logger.getLogger("ModLoader");
    private static FileHandler logHandler = null;
    private static Method method_RegisterEntityID = null;
    private static Method method_RegisterTileEntity = null;
    private static File modDir;
    private static final LinkedList modList = new LinkedList();
    private static int nextBlockModelID = 1000;
    private static final Map overrides = new HashMap();
    public static final Properties props = new Properties();
    private static BiomeGenBase[] standardBiomes;
    private static int terrainSpriteIndex = 0;
    private static int terrainSpritesLeft = 0;
    private static final boolean[] usedItemSprites = new boolean[256];
    private static final boolean[] usedTerrainSprites = new boolean[256];
    public static final String VERSION = "ModLoader Server 1.2.3v3";
    private static Method method_getNextWindowId;
    private static Field field_currentWindowId;

    public static void addAchievementDesc(Achievement var0, String var1, String var2)
    {
        try
        {
            String name = (String)getPrivateValue(StatBase.class, var0, 1);
            if (name.contains("."))
            {
                String[] var3 = name.split("\\.");

                if (var3.length == 2)
                {
                    String var4 = var3[1];
                    setPrivateValue(StatBase.class, var0, 1, StatCollector.translateToLocal("achievement." + var4));
                    setPrivateValue(Achievement.class, var0, 3, StatCollector.translateToLocal("achievement." + var4 + ".desc"));
                }
                else
                {
                    setPrivateValue(StatBase.class, var0, 1, var1);
                    setPrivateValue(Achievement.class, var0, 3, var2);
                }
            }
            else
            {
                setPrivateValue(StatBase.class, var0, 1, var1);
                setPrivateValue(Achievement.class, var0, 3, var2);
            }
        }
        catch (IllegalArgumentException var5)
        {
            logger.throwing("ModLoader", "AddAchievementDesc", var5);
            throwException(var5);
        }
        catch (SecurityException var6)
        {
            logger.throwing("ModLoader", "AddAchievementDesc", var6);
            throwException(var6);
        }
        catch (NoSuchFieldException var7)
        {
            logger.throwing("ModLoader", "AddAchievementDesc", var7);
            throwException(var7);
        }
    }

    public static int addAllFuel(int var0, int var1)
    {
        logger.finest("Finding fuel for " + var0);
        int var2 = 0;

        for (Iterator var3 = modList.iterator(); var3.hasNext() && var2 == 0; var2 = ((BaseMod)var3.next()).addFuel(var0, var1))
        {
            ;
        }

        if (var2 != 0)
        {
            logger.finest("Returned " + var2);
        }

        return var2;
    }

    public static int addArmor(String var0)
    {
        return -1;
    }

    private static void addMod(ClassLoader var0, String var1)
    {
        try
        {
            String var2 = var1.split("\\.")[0];

            if (var2.contains("$"))
            {
                return;
            }

            if (props.containsKey(var2) && (props.getProperty(var2).equalsIgnoreCase("no") || props.getProperty(var2).equalsIgnoreCase("off")))
            {
                return;
            }

            Package var3 = ModLoader.class.getPackage();

            if (var3 != null)
            {
                var2 = var3.getName() + "." + var2;
            }

            Class var4 = var0.loadClass(var2);

            if (!BaseMod.class.isAssignableFrom(var4))
            {
                return;
            }

            setupProperties(var4);
            BaseMod var5 = (BaseMod)var4.newInstance();

            if (var5 != null)
            {
                modList.add(var5);
                logger.fine("Mod Initialized: \"" + var5.toString() + "\" from " + var1);
                System.out.println("Mod Initialized: " + var5.toString());
                MinecraftServer.logger.info("Mod Initialized: " + var5.toString());
            }
        }
        catch (Throwable var6)
        {
            logger.fine("Failed to load mod from \"" + var1 + "\"");
            System.out.println("Failed to load mod from \"" + var1 + "\"");
            logger.throwing("ModLoader", "addMod", var6);
            throwException(var6);
        }
    }

    public static int addOverride(String var0, String var1)
    {
        return 0;
    }

    public static void addOverride(String var0, String var1, int var2)
    {
        boolean var3 = true;
        boolean var4 = false;
        byte var6;
        int var7;

        if (var0.equals("/terrain.png"))
        {
            var6 = 0;
            var7 = terrainSpritesLeft;
        }
        else
        {
            if (!var0.equals("/gui/items.png"))
            {
                return;
            }

            var6 = 1;
            var7 = itemSpritesLeft;
        }

        System.out.println("Overriding " + var0 + " with " + var1 + " @ " + var2 + ". " + var7 + " left.");
        logger.finer("addOverride(" + var0 + "," + var1 + "," + var2 + "). " + var7 + " left.");
        Object var5 = (Map)overrides.get(Integer.valueOf(var6));

        if (var5 == null)
        {
            var5 = new HashMap();
            overrides.put(Integer.valueOf(var6), var5);
        }

        ((Map)((Map)var5)).put(var1, Integer.valueOf(var2));
    }

    public static void addRecipe(ItemStack var0, Object[] var1)
    {
        CraftingManager.getInstance().addRecipe(var0, var1);
    }

    public static void addShapelessRecipe(ItemStack var0, Object[] var1)
    {
        CraftingManager.getInstance().addShapelessRecipe(var0, var1);
    }

    public static void addSmelting(int var0, ItemStack var1)
    {
        FurnaceRecipes.smelting().addSmelting(var0, var1);
    }

    public static void addSpawn(Class var0, int var1, int var2, int var3, EnumCreatureType var4)
    {
        addSpawn(var0, var1, var2, var3, var4, (BiomeGenBase[])null);
    }

    public static void addSpawn(Class var0, int var1, int var2, int var3, EnumCreatureType var4, BiomeGenBase[] var5)
    {
        if (var0 == null)
        {
            throw new IllegalArgumentException("entityClass cannot be null");
        }
        else if (var4 == null)
        {
            throw new IllegalArgumentException("spawnList cannot be null");
        }
        else
        {
            if (var5 == null)
            {
                var5 = standardBiomes;
            }

            for (int var6 = 0; var6 < var5.length; ++var6)
            {
                List var7 = var5[var6].getSpawnableList(var4);

                if (var7 != null)
                {
                    boolean var8 = false;
                    Iterator var9 = var7.iterator();

                    while (var9.hasNext())
                    {
                        SpawnListEntry var10 = (SpawnListEntry)var9.next();

                        if (var10.entityClass == var0)
                        {
                            var10.itemWeight = var1;
                            var10.minGroupCount = var2;
                            var10.maxGroupCount = var3;
                            var8 = true;
                            break;
                        }
                    }

                    if (!var8)
                    {
                        var7.add(new SpawnListEntry(var0, var1, var2, var3));
                    }
                }
            }
        }
    }

    public static void addSpawn(String var0, int var1, int var2, int var3, EnumCreatureType var4)
    {
        addSpawn(var0, var1, var2, var3, var4, (BiomeGenBase[])null);
    }

    public static void addSpawn(String var0, int var1, int var2, int var3, EnumCreatureType var4, BiomeGenBase[] var5)
    {
        Class var6 = (Class)classMap.get(var0);

        if (var6 != null && EntityLiving.class.isAssignableFrom(var6))
        {
            addSpawn(var6, var1, var2, var3, var4, var5);
        }
    }

    public static boolean dispenseEntity(World var0, double var1, double var3, double var5, int var7, int var8, ItemStack var9)
    {
        boolean var10 = false;

        for (Iterator var11 = modList.iterator(); var11.hasNext() && !var10; var10 = ((BaseMod)var11.next()).dispenseEntity(var0, var1, var3, var5, var7, var8, var9))
        {
            ;
        }

        return var10;
    }

    public static void genericContainerRemoval(World var0, int var1, int var2, int var3)
    {
        IInventory var4 = (IInventory)var0.getBlockTileEntity(var1, var2, var3);

        if (var4 != null)
        {
            for (int var5 = 0; var5 < var4.getSizeInventory(); ++var5)
            {
                ItemStack var6 = var4.getStackInSlot(var5);

                if (var6 != null)
                {
                    double var7 = var0.rand.nextDouble() * 0.8D + 0.1D;
                    double var9 = var0.rand.nextDouble() * 0.8D + 0.1D;
                    EntityItem var14;

                    for (double var11 = var0.rand.nextDouble() * 0.8D + 0.1D; var6.stackSize > 0; var0.spawnEntityInWorld(var14))
                    {
                        int var13 = var0.rand.nextInt(21) + 10;

                        if (var13 > var6.stackSize)
                        {
                            var13 = var6.stackSize;
                        }

                        var6.stackSize -= var13;
                        var14 = new EntityItem(var0, (double)var1 + var7, (double)var2 + var9, (double)var3 + var11, new ItemStack(var6.itemID, var13, var6.getItemDamage()));
                        double var15 = 0.05D;
                        var14.motionX = var0.rand.nextGaussian() * var15;
                        var14.motionY = var0.rand.nextGaussian() * var15 + 0.2D;
                        var14.motionZ = var0.rand.nextGaussian() * var15;

                        if (var6.hasTagCompound())
                        {
                            var14.item.setTagCompound((NBTTagCompound)var6.getTagCompound().copy());
                        }
                    }

                    var4.setInventorySlotContents(var5, (ItemStack)null);
                }
            }
        }
    }

    public static List getLoadedMods()
    {
        return Collections.unmodifiableList(modList);
    }

    public static Logger getLogger()
    {
        return logger;
    }

    public static MinecraftServer getMinecraftServerInstance()
    {
        return instance;
    }
    
    public static Object getPrivateValue(Class var0, Object var1, int var2) throws IllegalArgumentException, SecurityException, NoSuchFieldException
    {
        try
        {
            Field var3 = var0.getDeclaredFields()[var2];
            var3.setAccessible(true);
            return var3.get(var1);
        }
        catch (IllegalAccessException var4)
        {
            logger.throwing("ModLoader", "getPrivateValue", var4);
            throwException("An impossible error has occured!", var4);
            return null;
        }
    }

    public static Object getPrivateValue(Class var0, Object var1, String var2) throws IllegalArgumentException, SecurityException, NoSuchFieldException
    {
        try
        {
            Field var3 = var0.getDeclaredField(var2);
            var3.setAccessible(true);
            return var3.get(var1);
        }
        catch (IllegalAccessException var4)
        {
            logger.throwing("ModLoader", "getPrivateValue", var4);
            throwException("An impossible error has occured!", var4);
            return null;
        }
    }

    public static int getUniqueBlockModelID(BaseMod var0, boolean var1)
    {
        return nextBlockModelID++;
    }

    public static int getUniqueEntityId()
    {
        return highestEntityId++;
    }

    private static int getUniqueItemSpriteIndex()
    {
        while (itemSpriteIndex < usedItemSprites.length)
        {
            if (!usedItemSprites[itemSpriteIndex])
            {
                usedItemSprites[itemSpriteIndex] = true;
                --itemSpritesLeft;
                return itemSpriteIndex++;
            }

            ++itemSpriteIndex;
        }

        return itemSpriteIndex++;
    }

    public static int getUniqueSpriteIndex(String var0)
    {
        if (var0.equals("/gui/items.png"))
        {
            return getUniqueItemSpriteIndex();
        }
        else if (var0.equals("/terrain.png"))
        {
            return getUniqueTerrainSpriteIndex();
        }
        else
        {
            Exception var1 = new Exception("No registry for this texture: " + var0);
            logger.throwing("ModLoader", "getUniqueItemSpriteIndex", var1);
            throwException(var1);
            return 0;
        }
    }

    private static int getUniqueTerrainSpriteIndex()
    {
        while (terrainSpriteIndex < usedTerrainSprites.length)
        {
            if (!usedTerrainSprites[terrainSpriteIndex])
            {
                usedTerrainSprites[terrainSpriteIndex] = true;
                --terrainSpritesLeft;
                return terrainSpriteIndex++;
            }

            ++terrainSpriteIndex;
        }

        return terrainSpriteIndex++;
    }

    private static void init()
    {
        hasInit = true;
        String var0 = "1111111111111111111111111111111111111101111111111111111111111111111111111111111111111111111111111111110111111111111111000111111111111101111111110000000101111111000000010101111100000000000000110000000000000000000000000000000000000000000000001111111111111111";
        String var1 = "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111110000001111111111000000001111110000000111111111000000001111111110000001111111111111111111";

        for (int var2 = 0; var2 < 256; ++var2)
        {
            usedItemSprites[var2] = var0.charAt(var2) == 49;

            if (!usedItemSprites[var2])
            {
                ++itemSpritesLeft;
            }

            usedTerrainSprites[var2] = var1.charAt(var2) == 49;

            if (!usedTerrainSprites[var2])
            {
                ++terrainSpritesLeft;
            }
        }

        try
        {
            classMap = (Map)getPrivateValue(EntityList.class, (Object)null, 0);
            field_modifiers = Field.class.getDeclaredField("modifiers");
            field_modifiers.setAccessible(true);
            Field[] var15 = BiomeGenBase.class.getDeclaredFields();
            LinkedList var3 = new LinkedList();
            int var4 = 0;

            while (true)
            {
                if (var4 >= var15.length)
                {
                    standardBiomes = (BiomeGenBase[])((BiomeGenBase[])var3.toArray(new BiomeGenBase[0]));

                    try
                    {
                        method_RegisterTileEntity = TileEntity.class.getDeclaredMethod("a", new Class[] {Class.class, String.class});
                    }
                    catch (NoSuchMethodException var8)
                    {
                        method_RegisterTileEntity = TileEntity.class.getDeclaredMethod("addMapping", new Class[] {Class.class, String.class});
                    }

                    method_RegisterTileEntity.setAccessible(true);

                    try
                    {
                        method_RegisterEntityID = EntityList.class.getDeclaredMethod("a", new Class[] {Class.class, String.class, Integer.TYPE});
                    }
                    catch (NoSuchMethodException var7)
                    {
                        method_RegisterEntityID = EntityList.class.getDeclaredMethod("addMapping", new Class[] {Class.class, String.class, Integer.TYPE});
                    }

                    method_RegisterEntityID.setAccessible(true);
                    break;
                }

                Class var5 = var15[var4].getType();

                if ((var15[var4].getModifiers() & 8) != 0 && var5.isAssignableFrom(BiomeGenBase.class))
                {
                    BiomeGenBase var6 = (BiomeGenBase)var15[var4].get((Object)null);

                    if (!(var6 instanceof BiomeGenHell) && !(var6 instanceof BiomeGenEnd))
                    {
                        var3.add(var6);
                    }
                }

                ++var4;
            }
        }
        catch (SecurityException var10)
        {
            logger.throwing("ModLoader", "init", var10);
            throwException(var10);
            throw new RuntimeException(var10);
        }
        catch (NoSuchFieldException var11)
        {
            logger.throwing("ModLoader", "init", var11);
            throwException(var11);
            throw new RuntimeException(var11);
        }
        catch (NoSuchMethodException var12)
        {
            logger.throwing("ModLoader", "init", var12);
            throwException(var12);
            throw new RuntimeException(var12);
        }
        catch (IllegalArgumentException var13)
        {
            logger.throwing("ModLoader", "init", var13);
            throwException(var13);
            throw new RuntimeException(var13);
        }
        catch (IllegalAccessException var14)
        {
            logger.throwing("ModLoader", "init", var14);
            throwException(var14);
            throw new RuntimeException(var14);
        }

        try
        {
            loadConfig();

            if (props.containsKey("loggingLevel"))
            {
                cfgLoggingLevel = Level.parse(props.getProperty("loggingLevel"));
            }

            logger.setLevel(cfgLoggingLevel);

            if ((logfile.exists() || logfile.createNewFile()) && logfile.canWrite() && logHandler == null)
            {
                logHandler = new FileHandler(logfile.getPath());
                logHandler.setFormatter(new SimpleFormatter());
                logger.addHandler(logHandler);
            }

            logger.fine("ModLoader Server 1.2.3v3 Initializing...");
            System.out.println("ModLoader Server 1.2.3v3 Initializing...");
            MinecraftServer.logger.info("ModLoader Server 1.2.3v3 Initializing...");
            File var16 = new File(ModLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            modDir.mkdirs();
            readFromClassPath(var16);
            readFromModFolder(modDir);
            sortModList();
            Iterator var17 = modList.iterator();

            int x = 0;
            while (var17.hasNext())
            {
                BaseMod var19 = (BaseMod)var17.next();
                var19.load();
                logger.fine("Mod Loaded: \"" + var19.toString() + "\"");
                System.out.println("Mod Loaded: " + var19.toString());

                if (!props.containsKey(var19.getClass().getSimpleName()))
                {
                    props.setProperty(var19.getClass().getSimpleName(), "on");
                }

                /*
                 * Gather up a list of network mods and assign them an id 
                 */
                if (var19 instanceof NetworkMod)
                {
                    ForgeHooks.networkMods.put(x++, (NetworkMod)var19);
                }
            }

            Iterator var20 = modList.iterator();

            while (var20.hasNext())
            {
                BaseMod var18 = (BaseMod)var20.next();
                var18.modsLoaded();
            }

            System.out.println("Done.");
            props.setProperty("loggingLevel", cfgLoggingLevel.getName());
            initStats();
            saveConfig();
        }
        catch (Throwable var9)
        {
            logger.throwing("ModLoader", "init", var9);
            throwException("ModLoader has failed to initialize.", var9);

            if (logHandler != null)
            {
                logHandler.close();
            }

            throw new RuntimeException(var9);
        }
    }

    private static void initStats()
    {
        int var0;
        String var1;

        for (var0 = 0; var0 < Block.blocksList.length; ++var0)
        {
            if (!StatList.oneShotStats.containsKey(Integer.valueOf(16777216 + var0)) && Block.blocksList[var0] != null && Block.blocksList[var0].getEnableStats())
            {
                var1 = StringTranslate.getInstance().translateKeyFormat("stat.mineBlock", new Object[] {Boolean.valueOf(Block.blocksList[var0].func_48125_m())});
                StatList.mineBlockStatArray[var0] = (new StatCrafting(16777216 + var0, var1, var0)).registerStat();
                StatList.objectMineStats.add(StatList.mineBlockStatArray[var0]);
            }
        }

        for (var0 = 0; var0 < Item.itemsList.length; ++var0)
        {
            if (!StatList.oneShotStats.containsKey(Integer.valueOf(16908288 + var0)) && Item.itemsList[var0] != null)
            {
                var1 = StringTranslate.getInstance().translateKeyFormat("stat.useItem", new Object[] {Item.itemsList[var0].getStatName()});
                StatList.objectUseStats[var0] = (new StatCrafting(16908288 + var0, var1, var0)).registerStat();

                if (var0 >= Block.blocksList.length)
                {
                    StatList.itemStats.add(StatList.objectUseStats[var0]);
                }
            }

            if (!StatList.oneShotStats.containsKey(Integer.valueOf(16973824 + var0)) && Item.itemsList[var0] != null && Item.itemsList[var0].isDamageable())
            {
                var1 = StringTranslate.getInstance().translateKeyFormat("stat.breakItem", new Object[] {Item.itemsList[var0].getStatName()});
                StatList.objectBreakStats[var0] = (new StatCrafting(16973824 + var0, var1, var0)).registerStat();
            }
        }

        HashSet var6 = new HashSet();
        Iterator var2 = CraftingManager.getInstance().getRecipeList().iterator();

        while (var2.hasNext())
        {
            Object var7 = var2.next();
            var6.add(Integer.valueOf(((IRecipe)var7).getRecipeOutput().itemID));
        }

        Iterator var3 = FurnaceRecipes.smelting().getSmeltingList().values().iterator();

        while (var3.hasNext())
        {
            Object var8 = var3.next();
            var6.add(Integer.valueOf(((ItemStack)var8).itemID));
        }

        var3 = var6.iterator();

        while (var3.hasNext())
        {
            int var4 = ((Integer)var3.next()).intValue();

            if (!StatList.oneShotStats.containsKey(Integer.valueOf(16842752 + var4)) && Item.itemsList[var4] != null)
            {
                String var5 = StringTranslate.getInstance().translateKeyFormat("stat.craftItem", new Object[] {Item.itemsList[var4].getStatName()});
                StatList.objectCraftStats[var4] = (new StatCrafting(16842752 + var4, var5, var4)).registerStat();
            }
        }
    }

    public static boolean isModLoaded(String var0)
    {
        Class var1 = null;

        try
        {
            var1 = Class.forName(var0);
        }
        catch (ClassNotFoundException var4)
        {
            return false;
        }

        if (var1 != null)
        {
            Iterator var2 = modList.iterator();

            while (var2.hasNext())
            {
                BaseMod var3 = (BaseMod)var2.next();

                if (var1.isInstance(var3))
                {
                    return true;
                }
            }
        }

        return false;
    }

    public static void loadConfig() throws IOException
    {
        cfgdir.mkdir();

        if (cfgfile.exists() || cfgfile.createNewFile())
        {
            if (cfgfile.canRead())
            {
                FileInputStream var0 = new FileInputStream(cfgfile);
                props.load(var0);
                var0.close();
            }
        }
    }

    public static void onItemPickup(EntityPlayer var0, ItemStack var1)
    {
        Iterator var3 = modList.iterator();

        while (var3.hasNext())
        {
            BaseMod var2 = (BaseMod)var3.next();
            var2.onItemPickup(var0, var1);
        }
    }

    public static void onTick(MinecraftServer var0)
    {
        Profiler.endSection();
        Profiler.endSection();
        Profiler.startSection("modtick");

        if (!hasInit)
        {
            init();
            logger.fine("Initialized");
        }

        long var1 = 0L;

        World world = DimensionManager.getWorld(0);
        if (world != null)
        {
            var1 = world.getWorldTime();
            Iterator var3 = inGameHooks.entrySet().iterator();

            while (var3.hasNext())
            {
                Entry var4 = (Entry)var3.next();

                if (clock != var1 || !((Boolean)var4.getValue()).booleanValue())
                {
                    ((BaseMod)var4.getKey()).onTickInGame(var0);
                }
            }
        }

        clock = var1;
        Profiler.endSection();
        Profiler.startSection("render");
        Profiler.startSection("gameRenderer");
    }

    public static void openGUI(EntityPlayer var0, int var1, IInventory var2, Container var3)
    {
        if (!hasInit)
        {
            init();
            logger.fine("Initialized");
        }

        if (var0 instanceof EntityPlayerMP)
        {
            EntityPlayerMP var4 = (EntityPlayerMP)var0;

            try
            {
                method_getNextWindowId.invoke(var4, new Object[0]);
                int var5 = field_currentWindowId.getInt(var4);
                var4.closeCraftingGui();
                var4.playerNetServerHandler.sendPacket(new Packet100OpenWindow(var5, var1, var2.getInvName(), var2.getSizeInventory()));
                var4.craftingInventory = var3;
                var4.craftingInventory.windowId = var5;
                var4.craftingInventory.onCraftGuiOpened(var4);
            }
            catch (InvocationTargetException var6)
            {
                getLogger().throwing("ModLoaderMultiplayer", "OpenModGUI", var6);
                throwException("ModLoaderMultiplayer", var6);
            }
            catch (IllegalAccessException var7)
            {
                getLogger().throwing("ModLoaderMultiplayer", "OpenModGUI", var7);
                throwException("ModLoaderMultiplayer", var7);
            }
        }
    }

    public static void populateChunk(IChunkProvider var0, int var1, int var2, World var3)
    {
        if (!hasInit)
        {
            init();
            logger.fine("Initialized");
        }

        Random var4 = new Random(var3.getSeed());
        long var5 = var4.nextLong() / 2L * 2L + 1L;
        long var7 = var4.nextLong() / 2L * 2L + 1L;
        var4.setSeed((long)var1 * var5 + (long)var2 * var7 ^ var3.getSeed());
        Iterator var9 = modList.iterator();

        while (var9.hasNext())
        {
            BaseMod var10 = (BaseMod)var9.next();

            if (var0 instanceof ChunkProviderGenerate)
            {
                var10.generateSurface(var3, var4, var1 << 4, var2 << 4);
            }
            else if (var0 instanceof ChunkProviderHell)
            {
                var10.generateNether(var3, var4, var1 << 4, var2 << 4);
            }
        }
    }

    private static void readFromClassPath(File var0) throws FileNotFoundException, IOException
    {
        logger.finer("Adding mods from " + var0.getCanonicalPath());
        ClassLoader var1 = ModLoader.class.getClassLoader();
        String var5;

        if (var0.isFile() && (var0.getName().endsWith(".jar") || var0.getName().endsWith(".zip")))
        {
            logger.finer("Zip found.");
            FileInputStream var6 = new FileInputStream(var0);
            ZipInputStream var8 = new ZipInputStream(var6);

            while (true)
            {
                ZipEntry var9 = var8.getNextEntry();

                if (var9 == null)
                {
                    var6.close();
                    break;
                }

                var5 = var9.getName();

                if (!var9.isDirectory() && var5.startsWith("mod_") && var5.endsWith(".class"))
                {
                    addMod(var1, var5);
                }
            }
        }
        else if (var0.isDirectory())
        {
            Package var2 = ModLoader.class.getPackage();

            if (var2 != null)
            {
                String var3 = var2.getName().replace('.', File.separatorChar);
                var0 = new File(var0, var3);
            }

            logger.finer("Directory found.");
            File[] var7 = var0.listFiles();

            if (var7 != null)
            {
                for (int var4 = 0; var4 < var7.length; ++var4)
                {
                    var5 = var7[var4].getName();

                    if (var7[var4].isFile() && var5.startsWith("mod_") && var5.endsWith(".class"))
                    {
                        addMod(var1, var5);
                    }
                }
            }
        }
    }

    private static void readFromModFolder(File var0) throws IOException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException
    {
        ClassLoader var1 = MinecraftServer.class.getClassLoader();
        Method var2 = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] {URL.class});
        var2.setAccessible(true);

        if (!var0.isDirectory())
        {
            throw new IllegalArgumentException("folder must be a Directory.");
        }
        else
        {
            File[] var3 = var0.listFiles();
            Arrays.sort(var3);
            int var4;
            File var5;

            if (var1 instanceof URLClassLoader)
            {
                for (var4 = 0; var4 < var3.length; ++var4)
                {
                    var5 = var3[var4];

                    if (var5.isDirectory() || var5.isFile() && (var5.getName().endsWith(".jar") || var5.getName().endsWith(".zip")))
                    {
                        var2.invoke(var1, new Object[] {var5.toURI().toURL()});
                    }
                }
            }

            for (var4 = 0; var4 < var3.length; ++var4)
            {
                var5 = var3[var4];

                if (var5.isDirectory() || var5.isFile() && (var5.getName().endsWith(".jar") || var5.getName().endsWith(".zip")))
                {
                    logger.finer("Adding mods from " + var5.getCanonicalPath());

                    if (var5.isFile())
                    {
                        logger.finer("Zip found.");
                        FileInputStream var6 = new FileInputStream(var5);
                        ZipInputStream var7 = new ZipInputStream(var6);
                        Object var8 = null;

                        while (true)
                        {
                            ZipEntry var9 = var7.getNextEntry();

                            if (var9 == null)
                            {
                                var7.close();
                                var6.close();
                                break;
                            }

                            String var10 = var9.getName();

                            if (!var9.isDirectory() && var10.startsWith("mod_") && var10.endsWith(".class"))
                            {
                                addMod(var1, var10);
                            }
                        }
                    }
                    else if (var5.isDirectory())
                    {
                        Package var11 = ModLoader.class.getPackage();

                        if (var11 != null)
                        {
                            String var12 = var11.getName().replace('.', File.separatorChar);
                            var5 = new File(var5, var12);
                        }

                        logger.finer("Directory found.");
                        File[] var13 = var5.listFiles();

                        if (var13 != null)
                        {
                            for (int var14 = 0; var14 < var13.length; ++var14)
                            {
                                String var15 = var13[var14].getName();

                                if (var13[var14].isFile() && var15.startsWith("mod_") && var15.endsWith(".class"))
                                {
                                    addMod(var1, var15);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void registerBlock(Block var0)
    {
        registerBlock(var0, (Class)null);
    }

    public static void registerBlock(Block var0, Class var1)
    {
        try
        {
            if (var0 == null)
            {
                throw new IllegalArgumentException("block parameter cannot be null.");
            }

            int var2 = var0.blockID;
            ItemBlock var3 = null;

            if (var1 != null)
            {
                var3 = (ItemBlock)var1.getConstructor(new Class[] {Integer.TYPE}).newInstance(new Object[] {Integer.valueOf(var2 - 256)});
            }
            else
            {
                var3 = new ItemBlock(var2 - 256);
            }

            if (Block.blocksList[var2] != null && Item.itemsList[var2] == null)
            {
                Item.itemsList[var2] = var3;
            }
        }
        catch (IllegalArgumentException var4)
        {
            logger.throwing("ModLoader", "RegisterBlock", var4);
            throwException(var4);
        }
        catch (IllegalAccessException var5)
        {
            logger.throwing("ModLoader", "RegisterBlock", var5);
            throwException(var5);
        }
        catch (SecurityException var6)
        {
            logger.throwing("ModLoader", "RegisterBlock", var6);
            throwException(var6);
        }
        catch (InstantiationException var7)
        {
            logger.throwing("ModLoader", "RegisterBlock", var7);
            throwException(var7);
        }
        catch (InvocationTargetException var8)
        {
            logger.throwing("ModLoader", "RegisterBlock", var8);
            throwException(var8);
        }
        catch (NoSuchMethodException var9)
        {
            logger.throwing("ModLoader", "RegisterBlock", var9);
            throwException(var9);
        }
    }

    public static void registerEntityID(Class var0, String var1, int var2)
    {
        try
        {
            method_RegisterEntityID.invoke((Object)null, new Object[] {var0, var1, Integer.valueOf(var2)});
        }
        catch (IllegalArgumentException var4)
        {
            logger.throwing("ModLoader", "RegisterEntityID", var4);
            throwException(var4);
        }
        catch (IllegalAccessException var5)
        {
            logger.throwing("ModLoader", "RegisterEntityID", var5);
            throwException(var5);
        }
        catch (InvocationTargetException var6)
        {
            logger.throwing("ModLoader", "RegisterEntityID", var6);
            throwException(var6);
        }
    }

    public static void registerEntityID(Class var0, String var1, int var2, int var3, int var4)
    {
        registerEntityID(var0, var1, var2);
        EntityList.entityEggs.put(Integer.valueOf(var2), new EntityEggInfo(var2, var3, var4));
    }

    public static void registerTileEntity(Class var0, String var1)
    {
        try
        {
            method_RegisterTileEntity.invoke((Object)null, new Object[] {var0, var1});
        }
        catch (IllegalArgumentException var3)
        {
            logger.throwing("ModLoader", "RegisterTileEntity", var3);
            throwException(var3);
        }
        catch (IllegalAccessException var4)
        {
            logger.throwing("ModLoader", "RegisterTileEntity", var4);
            throwException(var4);
        }
        catch (InvocationTargetException var5)
        {
            logger.throwing("ModLoader", "RegisterTileEntity", var5);
            throwException(var5);
        }
    }

    public static void removeSpawn(Class var0, EnumCreatureType var1)
    {
        removeSpawn(var0, var1, (BiomeGenBase[])null);
    }

    public static void removeSpawn(Class var0, EnumCreatureType var1, BiomeGenBase[] var2)
    {
        if (var0 == null)
        {
            throw new IllegalArgumentException("entityClass cannot be null");
        }
        else if (var1 == null)
        {
            throw new IllegalArgumentException("spawnList cannot be null");
        }
        else
        {
            if (var2 == null)
            {
                var2 = standardBiomes;
            }

            for (int var3 = 0; var3 < var2.length; ++var3)
            {
                List var4 = var2[var3].getSpawnableList(var1);

                if (var4 != null)
                {
                    Iterator var5 = var4.iterator();

                    while (var5.hasNext())
                    {
                        SpawnListEntry var6 = (SpawnListEntry)var5.next();

                        if (var6.entityClass == var0)
                        {
                            var5.remove();
                        }
                    }
                }
            }
        }
    }

    public static void removeSpawn(String var0, EnumCreatureType var1)
    {
        removeSpawn(var0, var1, (BiomeGenBase[])null);
    }

    public static void removeSpawn(String var0, EnumCreatureType var1, BiomeGenBase[] var2)
    {
        Class var3 = (Class)classMap.get(var0);

        if (var3 != null && EntityLiving.class.isAssignableFrom(var3))
        {
            removeSpawn(var3, var1, var2);
        }
    }

    public static void saveConfig() throws IOException
    {
        cfgdir.mkdir();

        if (cfgfile.exists() || cfgfile.createNewFile())
        {
            if (cfgfile.canWrite())
            {
                FileOutputStream var0 = new FileOutputStream(cfgfile);
                props.store(var0, "ModLoader Config");
                var0.close();
            }
        }
    }

    public static void setInGameHook(BaseMod var0, boolean var1, boolean var2)
    {
        if (var1)
        {
            inGameHooks.put(var0, Boolean.valueOf(var2));
        }
        else
        {
            inGameHooks.remove(var0);
        }
    }

    public static void setInGUIHook(BaseMod var0, boolean var1, boolean var2)
    {
        if (var1)
        {
            inGUIHooks.put(var0, Boolean.valueOf(var2));
        }
        else
        {
            inGUIHooks.remove(var0);
        }
    }
    
    public static void setPrivateValue(Class var0, Object var1, int var2, Object var3) throws IllegalArgumentException, SecurityException, NoSuchFieldException
    {
        try
        {
            Field var4 = var0.getDeclaredFields()[var2];
            var4.setAccessible(true);
            int var5 = field_modifiers.getInt(var4);

            if ((var5 & 16) != 0)
            {
                field_modifiers.setInt(var4, var5 & -17);
            }

            var4.set(var1, var3);
        }
        catch (IllegalAccessException var6)
        {
            logger.throwing("ModLoader", "setPrivateValue", var6);
            throwException("An impossible error has occured!", var6);
        }
    }

    public static void setPrivateValue(Class var0, Object var1, String var2, Object var3) throws IllegalArgumentException, SecurityException, NoSuchFieldException
    {
        try
        {
            Field var4 = var0.getDeclaredField(var2);
            int var5 = field_modifiers.getInt(var4);

            if ((var5 & 16) != 0)
            {
                field_modifiers.setInt(var4, var5 & -17);
            }

            var4.setAccessible(true);
            var4.set(var1, var3);
        }
        catch (IllegalAccessException var6)
        {
            logger.throwing("ModLoader", "setPrivateValue", var6);
            throwException("An impossible error has occured!", var6);
        }
    }

    private static void setupProperties(Class var0) throws IllegalArgumentException, IllegalAccessException, IOException, SecurityException, NoSuchFieldException, NoSuchAlgorithmException, DigestException
    {
        LinkedList var1 = new LinkedList();
        Properties var2 = new Properties();
        int var3 = 0;
        int var4 = 0;
        File var5 = new File(cfgdir, var0.getSimpleName() + ".cfg");

        if (var5.exists() && var5.canRead())
        {
            var2.load(new FileInputStream(var5));
        }

        if (var2.containsKey("checksum"))
        {
            var4 = Integer.parseInt(var2.getProperty("checksum"), 36);
        }

        Field[] var6;
        int var7 = (var6 = var0.getDeclaredFields()).length;

        for (int var8 = 0; var8 < var7; ++var8)
        {
            Field var9 = var6[var8];

            if ((var9.getModifiers() & 8) != 0 && var9.isAnnotationPresent(MLProp.class))
            {
                var1.add(var9);
                Object var10 = var9.get((Object)null);
                var3 += var10.hashCode();
            }
        }

        StringBuilder var21 = new StringBuilder();
        Iterator var22 = var1.iterator();

        while (var22.hasNext())
        {
            Field var23 = (Field)var22.next();

            if ((var23.getModifiers() & 8) != 0 && var23.isAnnotationPresent(MLProp.class))
            {
                Class var11 = var23.getType();
                MLProp var12 = (MLProp)var23.getAnnotation(MLProp.class);
                String var13 = var12.name().length() != 0 ? var12.name() : var23.getName();
                Object var14 = var23.get((Object)null);
                StringBuilder var15 = new StringBuilder();

                if (var12.min() != Double.NEGATIVE_INFINITY)
                {
                    var15.append(String.format(",>=%.1f", new Object[] {Double.valueOf(var12.min())}));
                }

                if (var12.max() != Double.POSITIVE_INFINITY)
                {
                    var15.append(String.format(",<=%.1f", new Object[] {Double.valueOf(var12.max())}));
                }

                StringBuilder var16 = new StringBuilder();

                if (var12.info().length() > 0)
                {
                    var16.append(" -- ");
                    var16.append(var12.info());
                }

                var21.append(String.format("%s (%s:%s%s)%s\n", new Object[] {var13, var11.getName(), var14, var15, var16}));

                if (var4 == var3 && var2.containsKey(var13))
                {
                    String var17 = var2.getProperty(var13);
                    Object var18 = null;

                    if (var11.isAssignableFrom(String.class))
                    {
                        var18 = var17;
                    }
                    else if (var11.isAssignableFrom(Integer.TYPE))
                    {
                        var18 = Integer.valueOf(Integer.parseInt(var17));
                    }
                    else if (var11.isAssignableFrom(Short.TYPE))
                    {
                        var18 = Short.valueOf(Short.parseShort(var17));
                    }
                    else if (var11.isAssignableFrom(Byte.TYPE))
                    {
                        var18 = Byte.valueOf(Byte.parseByte(var17));
                    }
                    else if (var11.isAssignableFrom(Boolean.TYPE))
                    {
                        var18 = Boolean.valueOf(Boolean.parseBoolean(var17));
                    }
                    else if (var11.isAssignableFrom(Float.TYPE))
                    {
                        var18 = Float.valueOf(Float.parseFloat(var17));
                    }
                    else if (var11.isAssignableFrom(Double.TYPE))
                    {
                        var18 = Double.valueOf(Double.parseDouble(var17));
                    }

                    if (var18 != null)
                    {
                        if (var18 instanceof Number)
                        {
                            double var19 = ((Number)var18).doubleValue();

                            if (var12.min() != Double.NEGATIVE_INFINITY && var19 < var12.min() || var12.max() != Double.POSITIVE_INFINITY && var19 > var12.max())
                            {
                                continue;
                            }
                        }

                        logger.finer(var13 + " set to " + var18);

                        if (!var18.equals(var14))
                        {
                            var23.set((Object)null, var18);
                        }
                    }
                }
                else
                {
                    logger.finer(var13 + " not in config, using default: " + var14);
                    var2.setProperty(var13, var14.toString());
                }
            }
        }

        var2.put("checksum", Integer.toString(var3, 36));

        if (!var2.isEmpty() && (var5.exists() || var5.createNewFile()) && var5.canWrite())
        {
            var2.store(new FileOutputStream(var5), var21.toString());
        }
    }

    private static void sortModList() throws Exception
    {
        HashMap var0 = new HashMap();
        Iterator var2 = getLoadedMods().iterator();

        while (var2.hasNext())
        {
            BaseMod var1 = (BaseMod)var2.next();
            var0.put(var1.getClass().getSimpleName(), var1);
        }

        LinkedList var18 = new LinkedList();

        for (int var3 = 0; var18.size() != modList.size() && var3 <= 10; ++var3)
        {
            Iterator var4 = modList.iterator();

            while (var4.hasNext())
            {
                BaseMod var5 = (BaseMod)var4.next();

                if (!var18.contains(var5))
                {
                    String var6 = var5.getPriorities();

                    if (var6 != null && var6.length() != 0 && var6.indexOf(58) != -1)
                    {
                        if (var3 > 0)
                        {
                            int var7 = -1;
                            int var8 = Integer.MIN_VALUE;
                            int var9 = Integer.MAX_VALUE;
                            String[] var10;

                            if (var6.indexOf(59) > 0)
                            {
                                var10 = var6.split(";");
                            }
                            else
                            {
                                var10 = new String[] {var6};
                            }

                            int var11 = 0;

                            while (true)
                            {
                                if (var11 < var10.length)
                                {
                                    label143:
                                    {
                                        String var12 = var10[var11];

                                        if (var12.indexOf(58) != -1)
                                        {
                                            String[] var13 = var12.split(":");
                                            String var14 = var13[0];
                                            String var15 = var13[1];

                                            if (var14.contentEquals("required-before") || var14.contentEquals("before") || var14.contentEquals("after") || var14.contentEquals("required-after"))
                                            {
                                                if (var15.contentEquals("*"))
                                                {
                                                    if (!var14.contentEquals("required-before") && !var14.contentEquals("before"))
                                                    {
                                                        if (var14.contentEquals("required-after") || var14.contentEquals("after"))
                                                        {
                                                            var7 = var18.size();
                                                        }
                                                    }
                                                    else
                                                    {
                                                        var7 = 0;
                                                    }

                                                    break label143;
                                                }

                                                if ((var14.contentEquals("required-before") || var14.contentEquals("required-after")) && !var0.containsKey(var15))
                                                {
                                                    throw new Exception(String.format("%s is missing dependency: %s", new Object[] {var5, var15}));
                                                }

                                                BaseMod var16 = (BaseMod)var0.get(var15);

                                                if (!var18.contains(var16))
                                                {
                                                    break;
                                                }

                                                int var17 = var18.indexOf(var16);

                                                if (!var14.contentEquals("required-before") && !var14.contentEquals("before"))
                                                {
                                                    if (var14.contentEquals("required-after") || var14.contentEquals("after"))
                                                    {
                                                        var7 = var17 + 1;

                                                        if (var7 > var8)
                                                        {
                                                            var8 = var7;
                                                        }
                                                        else
                                                        {
                                                            var7 = var8;
                                                        }
                                                    }
                                                }
                                                else
                                                {
                                                    var7 = var17;

                                                    if (var17 < var9)
                                                    {
                                                        var9 = var17;
                                                    }
                                                    else
                                                    {
                                                        var7 = var9;
                                                    }
                                                }
                                            }
                                        }

                                        ++var11;
                                        continue;
                                    }
                                }

                                if (var7 != -1)
                                {
                                    var18.add(var7, var5);
                                }

                                break;
                            }
                        }
                    }
                    else
                    {
                        var18.add(var5);
                    }
                }
            }
        }

        modList.clear();
        modList.addAll(var18);
    }

    public static void takenFromCrafting(EntityPlayer var0, ItemStack var1, IInventory var2)
    {
        Iterator var4 = modList.iterator();

        while (var4.hasNext())
        {
            BaseMod var3 = (BaseMod)var4.next();
            var3.takenFromCrafting(var0, var1, var2);
        }
    }

    public static void takenFromFurnace(EntityPlayer var0, ItemStack var1)
    {
        Iterator var3 = modList.iterator();

        while (var3.hasNext())
        {
            BaseMod var2 = (BaseMod)var3.next();
            var2.takenFromFurnace(var0, var1);
        }
    }

    public static void throwException(String var0, Throwable var1)
    {
        var1.printStackTrace();
        logger.log(Level.SEVERE, "Unexpected exception", var1);
        MinecraftServer.logger.throwing("ModLoader", var0, var1);
        throw new RuntimeException(var0, var1);
    }

    private static void throwException(Throwable var0)
    {
        throwException("Exception occured in ModLoader", var0);
    }

    public static void initialize(MinecraftServer var0)
    {
        instance = var0;

        try
        {
            String var1 = ModLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            var1 = var1.substring(0, var1.lastIndexOf(47));
            cfgdir = new File(var1, "/config/");
            cfgfile = new File(var1, "/config/ModLoader.cfg");
            logfile = new File(var1, "ModLoader.txt");
            modDir = new File(var1, "/mods/");
        }
        catch (URISyntaxException var6)
        {
            getLogger().throwing("ModLoader", "Init", var6);
            throwException("ModLoader", var6);
            return;
        }

        try
        {
            try
            {
                method_getNextWindowId = EntityPlayerMP.class.getDeclaredMethod("bb", (Class[])null);
            }
            catch (NoSuchMethodException var3)
            {
                method_getNextWindowId = EntityPlayerMP.class.getDeclaredMethod("getNextWidowId", (Class[])null);
            }

            method_getNextWindowId.setAccessible(true);

            try
            {
                field_currentWindowId = EntityPlayerMP.class.getDeclaredField("cl");
            }
            catch (NoSuchFieldException var2)
            {
                field_currentWindowId = EntityPlayerMP.class.getDeclaredField("currentWindowId");
            }

            field_currentWindowId.setAccessible(true);
        }
        catch (NoSuchFieldException var4)
        {
            getLogger().throwing("ModLoader", "Init", var4);
            throwException("ModLoader", var4);
            return;
        }
        catch (NoSuchMethodException var5)
        {
            getLogger().throwing("ModLoader", "Init", var5);
            throwException("ModLoader", var5);
            return;
        }

        init();
    }
    
    public static boolean renderBlockIsItemFull3D(int var0)
    {
        return !blockSpecialInv.containsKey(Integer.valueOf(var0)) ? var0 == 16 : ((Boolean)blockSpecialInv.get(Integer.valueOf(var0))).booleanValue();
    }
}
