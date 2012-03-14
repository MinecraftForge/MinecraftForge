package net.minecraft.src;

public class Material
{
    public static final Material air = new MaterialTransparent(MapColor.airColor);
    public static final Material grass = new Material(MapColor.grassColor);
    public static final Material ground = new Material(MapColor.dirtColor);
    public static final Material wood = (new Material(MapColor.woodColor)).setBurning();
    public static final Material rock = (new Material(MapColor.stoneColor)).setNoHarvest();
    public static final Material iron = (new Material(MapColor.ironColor)).setNoHarvest();
    public static final Material water = (new MaterialLiquid(MapColor.waterColor)).setNoPushMobility();
    public static final Material lava = (new MaterialLiquid(MapColor.tntColor)).setNoPushMobility();
    public static final Material leaves = (new Material(MapColor.foliageColor)).setBurning().setTranslucent().setNoPushMobility();
    public static final Material plants = (new MaterialLogic(MapColor.foliageColor)).setNoPushMobility();
    public static final Material vine = (new MaterialLogic(MapColor.foliageColor)).setBurning().setNoPushMobility().setGroundCover();
    public static final Material sponge = new Material(MapColor.clothColor);
    public static final Material cloth = (new Material(MapColor.clothColor)).setBurning();
    public static final Material fire = (new MaterialTransparent(MapColor.airColor)).setNoPushMobility();
    public static final Material sand = new Material(MapColor.sandColor);
    public static final Material circuits = (new MaterialLogic(MapColor.airColor)).setNoPushMobility();
    public static final Material glass = (new Material(MapColor.airColor)).setTranslucent();
    public static final Material field_48476_r = new Material(MapColor.airColor);
    public static final Material tnt = (new Material(MapColor.tntColor)).setBurning().setTranslucent();
    public static final Material field_4215_q = (new Material(MapColor.foliageColor)).setNoPushMobility();
    public static final Material ice = (new Material(MapColor.iceColor)).setTranslucent();
    public static final Material snow = (new MaterialLogic(MapColor.snowColor)).setGroundCover().setTranslucent().setNoHarvest().setNoPushMobility();
    public static final Material craftedSnow = (new Material(MapColor.snowColor)).setNoHarvest();
    public static final Material cactus = (new Material(MapColor.foliageColor)).setTranslucent().setNoPushMobility();
    public static final Material clay = new Material(MapColor.clayColor);
    public static final Material pumpkin = (new Material(MapColor.foliageColor)).setNoPushMobility();
    public static final Material dragonEgg = (new Material(MapColor.foliageColor)).setNoPushMobility();
    public static final Material portal = (new MaterialPortal(MapColor.airColor)).setImmovableMobility();

    /** Cake's material, see BlockCake */
    public static final Material cake = (new Material(MapColor.airColor)).setNoPushMobility();
    public static final Material web = (new MaterialWeb(MapColor.clothColor)).setNoHarvest().setNoPushMobility();
    public static final Material piston = (new Material(MapColor.stoneColor)).setImmovableMobility();

    /** Bool defining if the block can burn or not. */
    private boolean canBurn;
    private boolean groundCover;
    private boolean isTranslucent;
    public final MapColor materialMapColor;
    private boolean canHarvest = true;

    /**
     * 0 indicates normal, 1 indicates this block can't push other blocks, 2 indicates this block cant be pushed
     */
    private int mobilityFlag;

    public Material(MapColor par1MapColor)
    {
        this.materialMapColor = par1MapColor;
    }

    /**
     * Returns if blocks of these materials are liquids.
     */
    public boolean isLiquid()
    {
        return false;
    }

    public boolean isSolid()
    {
        return true;
    }

    /**
     * Will prevent grass from growing on dirt underneath and kill any grass below it if it returns true
     */
    public boolean getCanBlockGrass()
    {
        return true;
    }

    /**
     * Returns if this material is considered solid or not
     */
    public boolean blocksMovement()
    {
        return true;
    }

    private Material setTranslucent()
    {
        this.isTranslucent = true;
        return this;
    }

    protected Material setNoHarvest()
    {
        this.canHarvest = false;
        return this;
    }

    /**
     * Set the canBurn bool to True and return the current object.
     */
    protected Material setBurning()
    {
        this.canBurn = true;
        return this;
    }

    /**
     * Returns if the block can burn or not.
     */
    public boolean getCanBurn()
    {
        return this.canBurn;
    }

    public Material setGroundCover()
    {
        this.groundCover = true;
        return this;
    }

    public boolean isGroundCover()
    {
        return this.groundCover;
    }

    /**
     * Indicate if the material is opaque
     */
    public boolean isOpaque()
    {
        return this.isTranslucent ? false : this.blocksMovement();
    }

    public boolean isHarvestable()
    {
        return this.canHarvest;
    }

    /**
     * returns a materials mobility flag
     */
    public int getMaterialMobility()
    {
        return this.mobilityFlag;
    }

    /**
     * marks this material as being unable to push blocks
     */
    protected Material setNoPushMobility()
    {
        this.mobilityFlag = 1;
        return this;
    }

    /**
     * marks this material as being immovable
     */
    protected Material setImmovableMobility()
    {
        this.mobilityFlag = 2;
        return this;
    }
}
