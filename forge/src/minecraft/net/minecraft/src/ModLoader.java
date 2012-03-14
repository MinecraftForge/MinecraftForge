package net.minecraft.src;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.DigestException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public final class ModLoader
{
    private static final List animList = new LinkedList();
    private static final Map blockModels = new HashMap();
    private static final Map blockSpecialInv = new HashMap();
    private static final File cfgdir = new File(Minecraft.getMinecraftDir(), "/config/");
    private static final File cfgfile = new File(cfgdir, "ModLoader.cfg");
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
    private static Minecraft instance = null;
    private static int itemSpriteIndex = 0;
    private static int itemSpritesLeft = 0;
    private static final Map keyList = new HashMap();
    private static String langPack = null;
    private static Map localizedStrings = new HashMap();
    private static final File logfile = new File(Minecraft.getMinecraftDir(), "ModLoader.txt");
    private static final Logger logger = Logger.getLogger("ModLoader");
    private static FileHandler logHandler = null;
    private static Method method_RegisterEntityID = null;
    private static Method method_RegisterTileEntity = null;
    private static final File modDir = new File(Minecraft.getMinecraftDir(), "/mods/");
    private static final LinkedList modList = new LinkedList();
    private static int nextBlockModelID = 1000;
    private static final Map overrides = new HashMap();
    public static final Properties props = new Properties();
    private static BiomeGenBase[] standardBiomes;
    private static int terrainSpriteIndex = 0;
    private static int terrainSpritesLeft = 0;
    private static String texPack = null;
    private static boolean texturesAdded = false;
    private static final boolean[] usedItemSprites = new boolean[256];
    private static final boolean[] usedTerrainSprites = new boolean[256];
    public static final String VERSION = "ModLoader 1.2.3";

    public static void addAchievementDesc(Achievement var0, String var1, String var2)
    {
        try
        {
            if (var0.getName().contains("."))
            {
                String[] var3 = var0.getName().split("\\.");

                if (var3.length == 2)
                {
                    String var4 = var3[1];
                    addLocalization("achievement." + var4, var1);
                    addLocalization("achievement." + var4 + ".desc", var2);
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

    public static void addAllRenderers(Map var0)
    {
        if (!hasInit)
        {
            init();
            logger.fine("Initialized");
        }

        Iterator var2 = modList.iterator();

        while (var2.hasNext())
        {
            BaseMod var1 = (BaseMod)var2.next();
            var1.addRenderer(var0);
        }
    }

    public static void addAnimation(TextureFX var0)
    {
        logger.finest("Adding animation " + var0.toString());
        Iterator var2 = animList.iterator();

        while (var2.hasNext())
        {
            TextureFX var1 = (TextureFX)var2.next();

            if (var1.iconIndex == var0.iconIndex && var1.tileImage == var0.tileImage)
            {
                animList.remove(var0);
                break;
            }
        }

        animList.add(var0);
    }

    public static int addArmor(String var0)
    {
        try
        {
            String[] var1 = (String[])field_armorList.get((Object)null);
            List var2 = Arrays.asList(var1);
            ArrayList var3 = new ArrayList();
            var3.addAll(var2);

            if (!var3.contains(var0))
            {
                var3.add(var0);
            }

            int var4 = var3.indexOf(var0);
            field_armorList.set((Object)null, var3.toArray(new String[0]));
            return var4;
        }
        catch (IllegalArgumentException var5)
        {
            logger.throwing("ModLoader", "AddArmor", var5);
            throwException("An impossible error has occured!", var5);
        }
        catch (IllegalAccessException var6)
        {
            logger.throwing("ModLoader", "AddArmor", var6);
            throwException("An impossible error has occured!", var6);
        }

        return -1;
    }

    public static void addBiome(BiomeGenBase var0)
    {
        BiomeGenBase[] var1 = GenLayerBiome.biomeArray;
        List var2 = Arrays.asList(var1);
        ArrayList var3 = new ArrayList();
        var3.addAll(var2);

        if (!var3.contains(var0))
        {
            var3.add(var0);
        }

        GenLayerBiome.biomeArray = (BiomeGenBase[])var3.toArray(new BiomeGenBase[0]);
    }

    public static void addLocalization(String var0, String var1)
    {
        addLocalization(var0, "en_US", var1);
    }

    public static void addLocalization(String var0, String var1, String var2)
    {
        Object var3;

        if (localizedStrings.containsKey(var1))
        {
            var3 = (Map)localizedStrings.get(var1);
        }
        else
        {
            var3 = new HashMap();
            localizedStrings.put(var1, var3);
        }

        ((Map)var3).put(var0, var2);
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

    public static void addName(Object var0, String var1)
    {
        addName(var0, "en_US", var1);
    }

    public static void addName(Object var0, String var1, String var2)
    {
        String var3 = null;
        Exception var8;

        if (var0 instanceof Item)
        {
            Item var4 = (Item)var0;

            if (var4.getItemName() != null)
            {
                var3 = var4.getItemName() + ".name";
            }
        }
        else if (var0 instanceof Block)
        {
            Block var6 = (Block)var0;

            if (var6.getBlockName() != null)
            {
                var3 = var6.getBlockName() + ".name";
            }
        }
        else if (var0 instanceof ItemStack)
        {
            ItemStack var7 = (ItemStack)var0;
            String var5 = Item.itemsList[var7.itemID].getItemNameIS(var7);

            if (var5 != null)
            {
                var3 = var5 + ".name";
            }
        }
        else
        {
            var8 = new Exception(var0.getClass().getName() + " cannot have name attached to it!");
            logger.throwing("ModLoader", "AddName", var8);
            throwException(var8);
        }

        if (var3 != null)
        {
            addLocalization(var3, var1, var2);
        }
        else
        {
            var8 = new Exception(var0 + " is missing name tag!");
            logger.throwing("ModLoader", "AddName", var8);
            throwException(var8);
        }
    }

    public static int addOverride(String var0, String var1)
    {
        try
        {
            int var2 = getUniqueSpriteIndex(var0);
            addOverride(var0, var1, var2);
            return var2;
        }
        catch (Throwable var3)
        {
            logger.throwing("ModLoader", "addOverride", var3);
            throwException(var3);
            throw new RuntimeException(var3);
        }
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

        ((Map)var5).put(var1, Integer.valueOf(var2));
    }

    public static void addRecipe(ItemStack var0, Object ... var1)
    {
        CraftingManager.getInstance().addRecipe(var0, var1);
    }

    public static void addShapelessRecipe(ItemStack var0, Object ... var1)
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

    public static void addSpawn(Class var0, int var1, int var2, int var3, EnumCreatureType var4, BiomeGenBase ... var5)
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
                    Iterator var10 = var7.iterator();

                    while (var10.hasNext())
                    {
                        SpawnListEntry var9 = (SpawnListEntry)var10.next();

                        if (var9.entityClass == var0)
                        {
                            var9.itemWeight = var1;
                            var9.minGroupCount = var2;
                            var9.maxGroupCount = var3;
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

    public static void addSpawn(String var0, int var1, int var2, int var3, EnumCreatureType var4, BiomeGenBase ... var5)
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

    public static Minecraft getMinecraftInstance()
    {
        if (instance == null)
        {
            try
            {
                ThreadGroup var0 = Thread.currentThread().getThreadGroup();
                int var1 = var0.activeCount();
                Thread[] var2 = new Thread[var1];
                var0.enumerate(var2);
                int var3;

                for (var3 = 0; var3 < var2.length; ++var3)
                {
                    System.out.println(var2[var3].getName());
                }

                for (var3 = 0; var3 < var2.length; ++var3)
                {
                    if (var2[var3].getName().equals("Minecraft main thread"))
                    {
                        instance = (Minecraft)getPrivateValue(Thread.class, var2[var3], "target");
                        break;
                    }
                }
            }
            catch (SecurityException var4)
            {
                logger.throwing("ModLoader", "getMinecraftInstance", var4);
                throw new RuntimeException(var4);
            }
            catch (NoSuchFieldException var5)
            {
                logger.throwing("ModLoader", "getMinecraftInstance", var5);
                throw new RuntimeException(var5);
            }
        }

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
        int var2 = nextBlockModelID++;
        blockModels.put(Integer.valueOf(var2), var0);
        blockSpecialInv.put(Integer.valueOf(var2), Boolean.valueOf(var1));
        return var2;
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

        Exception var0 = new Exception("No more empty item sprite indices left!");
        logger.throwing("ModLoader", "getUniqueItemSpriteIndex", var0);
        throwException(var0);
        return 0;
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

        Exception var0 = new Exception("No more empty terrain sprite indices left!");
        logger.throwing("ModLoader", "getUniqueItemSpriteIndex", var0);
        throwException(var0);
        return 0;
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
            instance = (Minecraft)getPrivateValue(Minecraft.class, (Object)null, 1);
            instance.entityRenderer = new EntityRendererProxy(instance);
            classMap = (Map)getPrivateValue(EntityList.class, (Object)null, 0);
            field_modifiers = Field.class.getDeclaredField("modifiers");
            field_modifiers.setAccessible(true);
            field_TileEntityRenderers = TileEntityRenderer.class.getDeclaredFields()[0];
            field_TileEntityRenderers.setAccessible(true);
            field_armorList = RenderPlayer.class.getDeclaredFields()[3];
            field_modifiers.setInt(field_armorList, field_armorList.getModifiers() & -17);
            field_armorList.setAccessible(true);
            field_animList = RenderEngine.class.getDeclaredFields()[6];
            field_animList.setAccessible(true);
            Field[] var15 = BiomeGenBase.class.getDeclaredFields();
            LinkedList var3 = new LinkedList();
            int var4 = 0;

            while (true)
            {
                if (var4 >= var15.length)
                {
                    standardBiomes = (BiomeGenBase[])var3.toArray(new BiomeGenBase[0]);

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

            if (props.containsKey("grassFix"))
            {
                RenderBlocks.cfgGrassFix = Boolean.parseBoolean(props.getProperty("grassFix"));
            }

            logger.setLevel(cfgLoggingLevel);

            if ((logfile.exists() || logfile.createNewFile()) && logfile.canWrite() && logHandler == null)
            {
                logHandler = new FileHandler(logfile.getPath());
                logHandler.setFormatter(new SimpleFormatter());
                logger.addHandler(logHandler);
            }

            logger.fine("ModLoader 1.2.3 Initializing...");
            System.out.println("ModLoader 1.2.3 Initializing...");
            File var16 = new File(ModLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            modDir.mkdirs();
            readFromClassPath(var16);
            readFromModFolder(modDir);
            sortModList();
            Iterator var18 = modList.iterator();
            BaseMod var17;

            while (var18.hasNext())
            {
                var17 = (BaseMod)var18.next();
                var17.load();
                logger.fine("Mod Loaded: \"" + var17.toString() + "\"");
                System.out.println("Mod Loaded: " + var17.toString());

                if (!props.containsKey(var17.getClass().getSimpleName()))
                {
                    props.setProperty(var17.getClass().getSimpleName(), "on");
                }
            }

            var18 = modList.iterator();

            while (var18.hasNext())
            {
                var17 = (BaseMod)var18.next();
                var17.modsLoaded();
            }

            System.out.println("Done.");
            props.setProperty("loggingLevel", cfgLoggingLevel.getName());
            props.setProperty("grassFix", Boolean.toString(RenderBlocks.cfgGrassFix));
            instance.gameSettings.keyBindings = registerAllKeys(instance.gameSettings.keyBindings);
            instance.gameSettings.loadOptions();
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
                var1 = StringTranslate.getInstance().translateKeyFormat("stat.mineBlock", new Object[] {Boolean.valueOf(Block.blocksList[var0].getTickRandomly())});
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

        HashSet var4 = new HashSet();
        Iterator var2 = CraftingManager.getInstance().getRecipeList().iterator();
        Object var5;

        while (var2.hasNext())
        {
            var5 = var2.next();
            var4.add(Integer.valueOf(((IRecipe)var5).getRecipeOutput().itemID));
        }

        var2 = FurnaceRecipes.smelting().getSmeltingList().values().iterator();

        while (var2.hasNext())
        {
            var5 = var2.next();
            var4.add(Integer.valueOf(((ItemStack)var5).itemID));
        }

        var2 = var4.iterator();

        while (var2.hasNext())
        {
            int var6 = ((Integer)var2.next()).intValue();

            if (!StatList.oneShotStats.containsKey(Integer.valueOf(16842752 + var6)) && Item.itemsList[var6] != null)
            {
                String var3 = StringTranslate.getInstance().translateKeyFormat("stat.craftItem", new Object[] {Item.itemsList[var6].getStatName()});
                StatList.objectCraftStats[var6] = (new StatCrafting(16842752 + var6, var3, var6)).registerStat();
            }
        }
    }

    public static boolean isGUIOpen(Class var0)
    {
        Minecraft var1 = getMinecraftInstance();
        return var0 == null ? var1.currentScreen == null : (var1.currentScreen == null && var0 != null ? false : var0.isInstance(var1.currentScreen));
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
            Iterator var3 = modList.iterator();

            while (var3.hasNext())
            {
                BaseMod var2 = (BaseMod)var3.next();

                if (var1.isInstance(var2))
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

    public static BufferedImage loadImage(RenderEngine var0, String var1) throws Exception
    {
        TexturePackList var2 = (TexturePackList)getPrivateValue(RenderEngine.class, var0, 11);
        InputStream var3 = var2.selectedTexturePack.getResourceAsStream(var1);

        if (var3 == null)
        {
            throw new Exception("Image not found: " + var1);
        }
        else
        {
            BufferedImage var4 = ImageIO.read(var3);

            if (var4 == null)
            {
                throw new Exception("Image corrupted: " + var1);
            }
            else
            {
                return var4;
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

    public static void onTick(float var0, Minecraft var1)
    {
        Profiler.endSection();
        Profiler.endSection();
        Profiler.startSection("modtick");

        if (!hasInit)
        {
            init();
            logger.fine("Initialized");
        }

        if (texPack == null || var1.gameSettings.skin != texPack)
        {
            texturesAdded = false;
            texPack = var1.gameSettings.skin;
        }

        if (langPack == null || StringTranslate.getInstance().getCurrentLanguage() != langPack)
        {
            Properties var2 = null;

            try
            {
                var2 = (Properties)getPrivateValue(StringTranslate.class, StringTranslate.getInstance(), 1);
            }
            catch (SecurityException var12)
            {
                logger.throwing("ModLoader", "AddLocalization", var12);
                throwException(var12);
            }
            catch (NoSuchFieldException var13)
            {
                logger.throwing("ModLoader", "AddLocalization", var13);
                throwException(var13);
            }

            langPack = StringTranslate.getInstance().getCurrentLanguage();

            if (var2 != null)
            {
                if (localizedStrings.containsKey("en_US"))
                {
                    var2.putAll((Map)localizedStrings.get("en_US"));
                }

                if (!langPack.contentEquals("en_US") && localizedStrings.containsKey(langPack))
                {
                    var2.putAll((Map)localizedStrings.get(langPack));
                }
            }
        }

        if (!texturesAdded && var1.renderEngine != null)
        {
            registerAllTextureOverrides(var1.renderEngine);
            texturesAdded = true;
        }

        long var14 = 0L;
        Iterator var4;
        Entry var5;

        if (var1.theWorld != null)
        {
            var14 = var1.theWorld.getWorldTime();
            var4 = inGameHooks.entrySet().iterator();

            while (var4.hasNext())
            {
                var5 = (Entry)var4.next();

                if ((clock != var14 || !((Boolean)var5.getValue()).booleanValue()) && !((BaseMod)var5.getKey()).onTickInGame(var0, var1))
                {
                    var4.remove();
                }
            }
        }

        if (var1.standardGalacticFontRenderer != null)
        {
            var4 = inGUIHooks.entrySet().iterator();

            while (var4.hasNext())
            {
                var5 = (Entry)var4.next();

                if ((clock != var14 || !(((Boolean)var5.getValue()).booleanValue() & var1.theWorld != null)) && !((BaseMod)var5.getKey()).onTickInGUI(var0, var1, var1.currentScreen))
                {
                    var4.remove();
                }
            }
        }

        if (clock != var14)
        {
            Iterator var16 = keyList.entrySet().iterator();

            while (var16.hasNext())
            {
                Entry var15 = (Entry)var16.next();
                Iterator var7 = ((Map)var15.getValue()).entrySet().iterator();

                while (var7.hasNext())
                {
                    Entry var6 = (Entry)var7.next();
                    int var8 = ((KeyBinding)var6.getKey()).keyCode;
                    boolean var9;

                    if (var8 < 0)
                    {
                        var8 += 100;
                        var9 = Mouse.isButtonDown(var8);
                    }
                    else
                    {
                        var9 = Keyboard.isKeyDown(var8);
                    }

                    boolean[] var10 = (boolean[])var6.getValue();
                    boolean var11 = var10[1];
                    var10[1] = var9;

                    if (var9 && (!var11 || var10[0]))
                    {
                        ((BaseMod)var15.getKey()).keyboardEvent((KeyBinding)var6.getKey());
                    }
                }
            }
        }

        clock = var14;
        Profiler.endSection();
        Profiler.startSection("render");
        Profiler.startSection("gameRenderer");
    }

    public static void openGUI(EntityPlayer var0, GuiScreen var1)
    {
        if (!hasInit)
        {
            init();
            logger.fine("Initialized");
        }

        Minecraft var2 = getMinecraftInstance();

        if (var2.renderViewEntity == var0)
        {
            if (var1 != null)
            {
                var2.displayGuiScreen(var1);
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
        Iterator var10 = modList.iterator();

        while (var10.hasNext())
        {
            BaseMod var9 = (BaseMod)var10.next();

            if (var0.makeString().equals("RandomLevelSource"))
            {
                var9.generateSurface(var3, var4, var1 << 4, var2 << 4);
            }
            else if (var0.makeString().equals("HellRandomLevelSource"))
            {
                var9.generateNether(var3, var4, var1 << 4, var2 << 4);
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
            ZipEntry var9 = null;

            while (true)
            {
                var9 = var8.getNextEntry();

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
        ClassLoader var1 = Minecraft.class.getClassLoader();
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
                    String var9;

                    if (var5.isFile())
                    {
                        logger.finer("Zip found.");
                        FileInputStream var6 = new FileInputStream(var5);
                        ZipInputStream var7 = new ZipInputStream(var6);
                        ZipEntry var8 = null;

                        while (true)
                        {
                            var8 = var7.getNextEntry();

                            if (var8 == null)
                            {
                                var7.close();
                                var6.close();
                                break;
                            }

                            var9 = var8.getName();

                            if (!var8.isDirectory() && var9.startsWith("mod_") && var9.endsWith(".class"))
                            {
                                addMod(var1, var9);
                            }
                        }
                    }
                    else if (var5.isDirectory())
                    {
                        Package var10 = ModLoader.class.getPackage();

                        if (var10 != null)
                        {
                            String var11 = var10.getName().replace('.', File.separatorChar);
                            var5 = new File(var5, var11);
                        }

                        logger.finer("Directory found.");
                        File[] var12 = var5.listFiles();

                        if (var12 != null)
                        {
                            for (int var13 = 0; var13 < var12.length; ++var13)
                            {
                                var9 = var12[var13].getName();

                                if (var12[var13].isFile() && var9.startsWith("mod_") && var9.endsWith(".class"))
                                {
                                    addMod(var1, var9);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static KeyBinding[] registerAllKeys(KeyBinding[] var0)
    {
        LinkedList var1 = new LinkedList();
        var1.addAll(Arrays.asList(var0));
        Iterator var3 = keyList.values().iterator();

        while (var3.hasNext())
        {
            Map var2 = (Map)var3.next();
            var1.addAll(var2.keySet());
        }

        return (KeyBinding[])var1.toArray(new KeyBinding[0]);
    }

    public static void registerAllTextureOverrides(RenderEngine var0)
    {
        animList.clear();
        Minecraft var1 = getMinecraftInstance();
        Iterator var3 = modList.iterator();

        while (var3.hasNext())
        {
            BaseMod var2 = (BaseMod)var3.next();
            var2.registerAnimation(var1);
        }

        var3 = animList.iterator();

        while (var3.hasNext())
        {
            TextureFX var12 = (TextureFX)var3.next();
            var0.registerTextureFX(var12);
        }

        var3 = overrides.entrySet().iterator();

        while (var3.hasNext())
        {
            Entry var13 = (Entry)var3.next();
            Iterator var5 = ((Map)var13.getValue()).entrySet().iterator();

            while (var5.hasNext())
            {
                Entry var4 = (Entry)var5.next();
                String var6 = (String)var4.getKey();
                int var7 = ((Integer)var4.getValue()).intValue();
                int var8 = ((Integer)var13.getKey()).intValue();

                try
                {
                    BufferedImage var9 = loadImage(var0, var6);
                    ModTextureStatic var10 = new ModTextureStatic(var7, var8, var9);
                    var0.registerTextureFX(var10);
                }
                catch (Exception var11)
                {
                    logger.throwing("ModLoader", "RegisterAllTextureOverrides", var11);
                    throwException(var11);
                    throw new RuntimeException(var11);
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

    public static void registerKey(BaseMod var0, KeyBinding var1, boolean var2)
    {
        Object var3 = (Map)keyList.get(var0);

        if (var3 == null)
        {
            var3 = new HashMap();
        }

        ((Map)var3).put(var1, new boolean[] {var2, false});
        keyList.put(var0, var3);
    }

    public static void registerTileEntity(Class var0, String var1)
    {
        registerTileEntity(var0, var1, (TileEntitySpecialRenderer)null);
    }

    public static void registerTileEntity(Class var0, String var1, TileEntitySpecialRenderer var2)
    {
        try
        {
            method_RegisterTileEntity.invoke((Object)null, new Object[] {var0, var1});

            if (var2 != null)
            {
                TileEntityRenderer var3 = TileEntityRenderer.instance;
                Map var4 = (Map)field_TileEntityRenderers.get(var3);
                var4.put(var0, var2);
                var2.setTileEntityRenderer(var3);
            }
        }
        catch (IllegalArgumentException var5)
        {
            logger.throwing("ModLoader", "RegisterTileEntity", var5);
            throwException(var5);
        }
        catch (IllegalAccessException var6)
        {
            logger.throwing("ModLoader", "RegisterTileEntity", var6);
            throwException(var6);
        }
        catch (InvocationTargetException var7)
        {
            logger.throwing("ModLoader", "RegisterTileEntity", var7);
            throwException(var7);
        }
    }

    public static void removeBiome(BiomeGenBase var0)
    {
        BiomeGenBase[] var1 = GenLayerBiome.biomeArray;
        List var2 = Arrays.asList(var1);
        ArrayList var3 = new ArrayList();
        var3.addAll(var2);

        if (var3.contains(var0))
        {
            var3.remove(var0);
        }

        GenLayerBiome.biomeArray = (BiomeGenBase[])var3.toArray(new BiomeGenBase[0]);
    }

    public static void removeSpawn(Class var0, EnumCreatureType var1)
    {
        removeSpawn(var0, var1, (BiomeGenBase[])null);
    }

    public static void removeSpawn(Class var0, EnumCreatureType var1, BiomeGenBase ... var2)
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

    public static void removeSpawn(String var0, EnumCreatureType var1, BiomeGenBase ... var2)
    {
        Class var3 = (Class)classMap.get(var0);

        if (var3 != null && EntityLiving.class.isAssignableFrom(var3))
        {
            removeSpawn(var3, var1, var2);
        }
    }

    public static boolean renderBlockIsItemFull3D(int var0)
    {
        return !blockSpecialInv.containsKey(Integer.valueOf(var0)) ? var0 == 16 : ((Boolean)blockSpecialInv.get(Integer.valueOf(var0))).booleanValue();
    }

    public static void renderInvBlock(RenderBlocks var0, Block var1, int var2, int var3)
    {
        BaseMod var4 = (BaseMod)blockModels.get(Integer.valueOf(var3));

        if (var4 != null)
        {
            var4.renderInvBlock(var0, var1, var2, var3);
        }
    }

    public static boolean renderWorldBlock(RenderBlocks var0, IBlockAccess var1, int var2, int var3, int var4, Block var5, int var6)
    {
        BaseMod var7 = (BaseMod)blockModels.get(Integer.valueOf(var6));
        return var7 == null ? false : var7.renderWorldBlock(var0, var1, var2, var3, var4, var5, var6);
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

        Field[] var9;
        int var8 = (var9 = var0.getDeclaredFields()).length;

        for (int var7 = 0; var7 < var8; ++var7)
        {
            Field var6 = var9[var7];

            if ((var6.getModifiers() & 8) != 0 && var6.isAnnotationPresent(MLProp.class))
            {
                var1.add(var6);
                Object var10 = var6.get((Object)null);
                var3 += var10.hashCode();
            }
        }

        StringBuilder var19 = new StringBuilder();
        Iterator var21 = var1.iterator();

        while (var21.hasNext())
        {
            Field var20 = (Field)var21.next();

            if ((var20.getModifiers() & 8) != 0 && var20.isAnnotationPresent(MLProp.class))
            {
                Class var22 = var20.getType();
                MLProp var23 = (MLProp)var20.getAnnotation(MLProp.class);
                String var11 = var23.name().length() == 0 ? var20.getName() : var23.name();
                Object var12 = var20.get((Object)null);
                StringBuilder var13 = new StringBuilder();

                if (var23.min() != Double.NEGATIVE_INFINITY)
                {
                    var13.append(String.format(",>=%.1f", new Object[] {Double.valueOf(var23.min())}));
                }

                if (var23.max() != Double.POSITIVE_INFINITY)
                {
                    var13.append(String.format(",<=%.1f", new Object[] {Double.valueOf(var23.max())}));
                }

                StringBuilder var14 = new StringBuilder();

                if (var23.info().length() > 0)
                {
                    var14.append(" -- ");
                    var14.append(var23.info());
                }

                var19.append(String.format("%s (%s:%s%s)%s\n", new Object[] {var11, var22.getName(), var12, var13, var14}));

                if (var4 == var3 && var2.containsKey(var11))
                {
                    String var15 = var2.getProperty(var11);
                    Object var16 = null;

                    if (var22.isAssignableFrom(String.class))
                    {
                        var16 = var15;
                    }
                    else if (var22.isAssignableFrom(Integer.TYPE))
                    {
                        var16 = Integer.valueOf(Integer.parseInt(var15));
                    }
                    else if (var22.isAssignableFrom(Short.TYPE))
                    {
                        var16 = Short.valueOf(Short.parseShort(var15));
                    }
                    else if (var22.isAssignableFrom(Byte.TYPE))
                    {
                        var16 = Byte.valueOf(Byte.parseByte(var15));
                    }
                    else if (var22.isAssignableFrom(Boolean.TYPE))
                    {
                        var16 = Boolean.valueOf(Boolean.parseBoolean(var15));
                    }
                    else if (var22.isAssignableFrom(Float.TYPE))
                    {
                        var16 = Float.valueOf(Float.parseFloat(var15));
                    }
                    else if (var22.isAssignableFrom(Double.TYPE))
                    {
                        var16 = Double.valueOf(Double.parseDouble(var15));
                    }

                    if (var16 != null)
                    {
                        if (var16 instanceof Number)
                        {
                            double var17 = ((Number)var16).doubleValue();

                            if (var23.min() != Double.NEGATIVE_INFINITY && var17 < var23.min() || var23.max() != Double.POSITIVE_INFINITY && var17 > var23.max())
                            {
                                continue;
                            }
                        }

                        logger.finer(var11 + " set to " + var16);

                        if (!var16.equals(var12))
                        {
                            var20.set((Object)null, var16);
                        }
                    }
                }
                else
                {
                    logger.finer(var11 + " not in config, using default: " + var12);
                    var2.setProperty(var11, var12.toString());
                }
            }
        }

        var2.put("checksum", Integer.toString(var3, 36));

        if (!var2.isEmpty() && (var5.exists() || var5.createNewFile()) && var5.canWrite())
        {
            var2.store(new FileOutputStream(var5), var19.toString());
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

        LinkedList var17 = new LinkedList();

        for (int var18 = 0; var17.size() != modList.size() && var18 <= 10; ++var18)
        {
            Iterator var4 = modList.iterator();

            while (var4.hasNext())
            {
                BaseMod var3 = (BaseMod)var4.next();

                if (!var17.contains(var3))
                {
                    String var5 = var3.getPriorities();

                    if (var5 != null && var5.length() != 0 && var5.indexOf(58) != -1)
                    {
                        if (var18 > 0)
                        {
                            int var7 = -1;
                            int var8 = Integer.MIN_VALUE;
                            int var9 = Integer.MAX_VALUE;
                            String[] var6;

                            if (var5.indexOf(59) > 0)
                            {
                                var6 = var5.split(";");
                            }
                            else
                            {
                                var6 = new String[] {var5};
                            }

                            int var10 = 0;

                            while (true)
                            {
                                if (var10 < var6.length)
                                {
                                    label141:
                                    {
                                        String var11 = var6[var10];

                                        if (var11.indexOf(58) != -1)
                                        {
                                            String[] var12 = var11.split(":");
                                            String var13 = var12[0];
                                            String var14 = var12[1];

                                            if (var13.contentEquals("required-before") || var13.contentEquals("before") || var13.contentEquals("after") || var13.contentEquals("required-after"))
                                            {
                                                if (var14.contentEquals("*"))
                                                {
                                                    if (!var13.contentEquals("required-before") && !var13.contentEquals("before"))
                                                    {
                                                        if (var13.contentEquals("required-after") || var13.contentEquals("after"))
                                                        {
                                                            var7 = var17.size();
                                                        }
                                                    }
                                                    else
                                                    {
                                                        var7 = 0;
                                                    }

                                                    break label141;
                                                }

                                                if ((var13.contentEquals("required-before") || var13.contentEquals("required-after")) && !var0.containsKey(var14))
                                                {
                                                    throw new Exception(String.format("%s is missing dependency: %s", new Object[] {var3, var14}));
                                                }

                                                BaseMod var15 = (BaseMod)var0.get(var14);

                                                if (!var17.contains(var15))
                                                {
                                                    break;
                                                }

                                                int var16 = var17.indexOf(var15);

                                                if (!var13.contentEquals("required-before") && !var13.contentEquals("before"))
                                                {
                                                    if (var13.contentEquals("required-after") || var13.contentEquals("after"))
                                                    {
                                                        var7 = var16 + 1;

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
                                                    var7 = var16;

                                                    if (var16 < var9)
                                                    {
                                                        var9 = var16;
                                                    }
                                                    else
                                                    {
                                                        var7 = var9;
                                                    }
                                                }
                                            }
                                        }

                                        ++var10;
                                        continue;
                                    }
                                }

                                if (var7 != -1)
                                {
                                    var17.add(var7, var3);
                                }

                                break;
                            }
                        }
                    }
                    else
                    {
                        var17.add(var3);
                    }
                }
            }
        }

        modList.clear();
        modList.addAll(var17);
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
        Minecraft var2 = getMinecraftInstance();

        if (var2 != null)
        {
            var2.displayUnexpectedThrowable(new UnexpectedThrowable(var0, var1));
        }
        else
        {
            throw new RuntimeException(var1);
        }
    }

    private static void throwException(Throwable var0)
    {
        throwException("Exception occured in ModLoader", var0);
    }
}
