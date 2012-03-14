package net.minecraft.src;

import java.util.HashMap;
import java.util.Map;

public class TileEntity
{
    /**
     * A HashMap storing string names of classes mapping to the actual java.lang.Class type.
     */
    private static Map nameToClassMap = new HashMap();

    /**
     * A HashMap storing the classes and mapping to the string names (reverse of nameToClassMap).
     */
    private static Map classToNameMap = new HashMap();

    /** The reference to the world. */
    public World worldObj;

    /** The x coordinate of the tile entity. */
    public int xCoord;

    /** The y coordinate of the tile entity. */
    public int yCoord;

    /** The z coordinate of the tile entity. */
    public int zCoord;
    protected boolean tileEntityInvalid;
    public int blockMetadata = -1;

    /** the Block type that this TileEntity is contained within */
    public Block blockType;

    /**
     * Adds a new two-way mapping between the class and its string name in both hashmaps.
     */
    private static void addMapping(Class par0Class, String par1Str)
    {
        if (classToNameMap.containsKey(par1Str))
        {
            throw new IllegalArgumentException("Duplicate id: " + par1Str);
        }
        else
        {
            nameToClassMap.put(par1Str, par0Class);
            classToNameMap.put(par0Class, par1Str);
        }
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        this.xCoord = par1NBTTagCompound.getInteger("x");
        this.yCoord = par1NBTTagCompound.getInteger("y");
        this.zCoord = par1NBTTagCompound.getInteger("z");
    }

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        String var2 = (String)classToNameMap.get(this.getClass());

        if (var2 == null)
        {
            throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
        }
        else
        {
            par1NBTTagCompound.setString("id", var2);
            par1NBTTagCompound.setInteger("x", this.xCoord);
            par1NBTTagCompound.setInteger("y", this.yCoord);
            par1NBTTagCompound.setInteger("z", this.zCoord);
        }
    }

    /**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    public void updateEntity() {}

    /**
     * Creates a new entity and loads its data from the specified NBT.
     */
    public static TileEntity createAndLoadEntity(NBTTagCompound par0NBTTagCompound)
    {
        TileEntity var1 = null;

        try
        {
            Class var2 = (Class)nameToClassMap.get(par0NBTTagCompound.getString("id"));

            if (var2 != null)
            {
                var1 = (TileEntity)var2.newInstance();
            }
        }
        catch (Exception var3)
        {
            var3.printStackTrace();
        }

        if (var1 != null)
        {
            var1.readFromNBT(par0NBTTagCompound);
        }
        else
        {
            System.out.println("Skipping TileEntity with id " + par0NBTTagCompound.getString("id"));
        }

        return var1;
    }

    public int getBlockMetadata()
    {
        if (this.blockMetadata == -1)
        {
            this.blockMetadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        }

        return this.blockMetadata;
    }

    /**
     * Called when an the contents of an Inventory change, usually
     */
    public void onInventoryChanged()
    {
        if (this.worldObj != null)
        {
            this.blockMetadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
            this.worldObj.updateTileEntityChunkAndDoNothing(this.xCoord, this.yCoord, this.zCoord, this);
        }
    }

    /**
     * Overriden in a sign to provide the text
     */
    public Packet getDescriptionPacket()
    {
        return null;
    }

    /**
     * returns true if tile entity is invalid, false otherwise
     */
    public boolean isInvalid()
    {
        return this.tileEntityInvalid;
    }

    /**
     * invalidates a tile entity
     */
    public void invalidate()
    {
        this.tileEntityInvalid = true;
    }

    /**
     * validates a tile entity
     */
    public void validate()
    {
        this.tileEntityInvalid = false;
    }

    public void onTileEntityPowered(int par1, int par2) {}

    /**
     * Causes the TileEntity to reset all it's cached values for it's container block, blockID, metaData and in the case
     * of chests, the adjcacent chest check
     */
    public void updateContainingBlockInfo()
    {
        this.blockType = null;
        this.blockMetadata = -1;
    }

    static
    {
        addMapping(TileEntityFurnace.class, "Furnace");
        addMapping(TileEntityChest.class, "Chest");
        addMapping(TileEntityRecordPlayer.class, "RecordPlayer");
        addMapping(TileEntityDispenser.class, "Trap");
        addMapping(TileEntitySign.class, "Sign");
        addMapping(TileEntityMobSpawner.class, "MobSpawner");
        addMapping(TileEntityNote.class, "Music");
        addMapping(TileEntityPiston.class, "Piston");
        addMapping(TileEntityBrewingStand.class, "Cauldron");
        addMapping(TileEntityEnchantmentTable.class, "EnchantTable");
        addMapping(TileEntityEndPortal.class, "Airportal");
    }
    
    /**
     * Determines if this TileEntity requires update calls.
     * @return True if you want updateEntity() to be called, false if not
     */
    public boolean canUpdate()
    {
        return true;
    }
}
