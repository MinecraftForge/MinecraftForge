package net.minecraft.src;

import java.util.HashMap;
import java.util.Map;

public class EntityList
{
    /** Provides a mapping between entity classes and a string */
    private static Map stringToClassMapping = new HashMap();

    /** Provides a mapping between a string and an entity classes */
    private static Map classToStringMapping = new HashMap();

    /** provides a mapping between an entityID and an Entity Class */
    private static Map IDtoClassMapping = new HashMap();

    /** provides a mapping between an Entity Class and an entity ID */
    private static Map classToIDMapping = new HashMap();
    private static Map field_48583_f = new HashMap();
    public static HashMap entityEggs = new HashMap();

    /**
     * adds a mapping between Entity classes and both a string representation and an ID
     */
    private static void addMapping(Class par0Class, String par1Str, int par2)
    {
        stringToClassMapping.put(par1Str, par0Class);
        classToStringMapping.put(par0Class, par1Str);
        IDtoClassMapping.put(Integer.valueOf(par2), par0Class);
        classToIDMapping.put(par0Class, Integer.valueOf(par2));
        field_48583_f.put(par1Str, Integer.valueOf(par2));
    }

    /**
     * Adds a entity mapping with egg info.
     */
    private static void addMapping(Class par0Class, String par1Str, int par2, int par3, int par4)
    {
        addMapping(par0Class, par1Str, par2);
        entityEggs.put(Integer.valueOf(par2), new EntityEggInfo(par2, par3, par4));
    }

    /**
     * Creates an entity of the given name in the world.
     */
    public static Entity createEntityInWorld(String par0Str, World par1World)
    {
        Entity var2 = null;

        try
        {
            Class var3 = (Class)stringToClassMapping.get(par0Str);

            if (var3 != null)
            {
                var2 = (Entity)var3.getConstructor(new Class[] {World.class}).newInstance(new Object[] {par1World});
            }
        }
        catch (Exception var4)
        {
            var4.printStackTrace();
        }

        return var2;
    }

    /**
     * create a new instance of an entity from NBT store
     */
    public static Entity createEntityFromNBT(NBTTagCompound par0NBTTagCompound, World par1World)
    {
        Entity var2 = null;

        try
        {
            Class var3 = (Class)stringToClassMapping.get(par0NBTTagCompound.getString("id"));

            if (var3 != null)
            {
                var2 = (Entity)var3.getConstructor(new Class[] {World.class}).newInstance(new Object[] {par1World});
            }
        }
        catch (Exception var4)
        {
            var4.printStackTrace();
        }

        if (var2 != null)
        {
            var2.readFromNBT(par0NBTTagCompound);
        }
        else
        {
            System.out.println("Skipping Entity with id " + par0NBTTagCompound.getString("id"));
        }

        return var2;
    }

    public static Entity createEntity(int par0, World par1World)
    {
        Entity var2 = null;

        try
        {
            Class var3 = (Class)IDtoClassMapping.get(Integer.valueOf(par0));

            if (var3 != null)
            {
                var2 = (Entity)var3.getConstructor(new Class[] {World.class}).newInstance(new Object[] {par1World});
            }
        }
        catch (Exception var4)
        {
            var4.printStackTrace();
        }

        if (var2 == null)
        {
            System.out.println("Skipping Entity with id " + par0);
        }

        return var2;
    }

    /**
     * gets the entityID of a specific entity
     */
    public static int getEntityID(Entity par0Entity)
    {
        return ((Integer)classToIDMapping.get(par0Entity.getClass())).intValue();
    }

    /**
     * gets the string representation of a specific entity
     */
    public static String getEntityString(Entity par0Entity)
    {
        return (String)classToStringMapping.get(par0Entity.getClass());
    }

    public static int func_48582_a(String par0Str)
    {
        Integer var1 = (Integer)field_48583_f.get(par0Str);
        return var1 == null ? 90 : var1.intValue();
    }

    static
    {
        addMapping(EntityItem.class, "Item", 1);
        addMapping(EntityXPOrb.class, "XPOrb", 2);
        addMapping(EntityPainting.class, "Painting", 9);
        addMapping(EntityArrow.class, "Arrow", 10);
        addMapping(EntitySnowball.class, "Snowball", 11);
        addMapping(EntityFireball.class, "Fireball", 12);
        addMapping(EntitySmallFireball.class, "SmallFireball", 13);
        addMapping(EntityEnderPearl.class, "ThrownEnderpearl", 14);
        addMapping(EntityEnderEye.class, "EyeOfEnderSignal", 15);
        addMapping(EntityPotion.class, "ThrownPotion", 16);
        addMapping(EntityExpBottle.class, "ThrownExpBottle", 17);
        addMapping(EntityTNTPrimed.class, "PrimedTnt", 20);
        addMapping(EntityFallingSand.class, "FallingSand", 21);
        addMapping(EntityMinecart.class, "Minecart", 40);
        addMapping(EntityBoat.class, "Boat", 41);
        addMapping(EntityLiving.class, "Mob", 48);
        addMapping(EntityMob.class, "Monster", 49);
        addMapping(EntityCreeper.class, "Creeper", 50, 894731, 0);
        addMapping(EntitySkeleton.class, "Skeleton", 51, 12698049, 4802889);
        addMapping(EntitySpider.class, "Spider", 52, 3419431, 11013646);
        addMapping(EntityGiantZombie.class, "Giant", 53);
        addMapping(EntityZombie.class, "Zombie", 54, 44975, 7969893);
        addMapping(EntitySlime.class, "Slime", 55, 5349438, 8306542);
        addMapping(EntityGhast.class, "Ghast", 56, 16382457, 12369084);
        addMapping(EntityPigZombie.class, "PigZombie", 57, 15373203, 5009705);
        addMapping(EntityEnderman.class, "Enderman", 58, 1447446, 0);
        addMapping(EntityCaveSpider.class, "CaveSpider", 59, 803406, 11013646);
        addMapping(EntitySilverfish.class, "Silverfish", 60, 7237230, 3158064);
        addMapping(EntityBlaze.class, "Blaze", 61, 16167425, 16775294);
        addMapping(EntityMagmaCube.class, "LavaSlime", 62, 3407872, 16579584);
        addMapping(EntityDragon.class, "EnderDragon", 63);
        addMapping(EntityPig.class, "Pig", 90, 15771042, 14377823);
        addMapping(EntitySheep.class, "Sheep", 91, 15198183, 16758197);
        addMapping(EntityCow.class, "Cow", 92, 4470310, 10592673);
        addMapping(EntityChicken.class, "Chicken", 93, 10592673, 16711680);
        addMapping(EntitySquid.class, "Squid", 94, 2243405, 7375001);
        addMapping(EntityWolf.class, "Wolf", 95, 14144467, 13545366);
        addMapping(EntityMooshroom.class, "MushroomCow", 96, 10489616, 12040119);
        addMapping(EntitySnowman.class, "SnowMan", 97);
        addMapping(EntityOcelot.class, "Ozelot", 98, 15720061, 5653556);
        addMapping(EntityIronGolem.class, "VillagerGolem", 99);
        addMapping(EntityVillager.class, "Villager", 120, 5651507, 12422002);
        addMapping(EntityEnderCrystal.class, "EnderCrystal", 200);
    }
}
