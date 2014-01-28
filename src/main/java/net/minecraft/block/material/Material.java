package net.minecraft.block.material;

public class Material
{
    public static final Material field_151579_a = new MaterialTransparent(MapColor.field_151660_b);
    public static final Material field_151577_b = new Material(MapColor.field_151661_c);
    public static final Material field_151578_c = new Material(MapColor.field_151664_l);
    public static final Material field_151575_d = (new Material(MapColor.field_151663_o)).setBurning();
    public static final Material field_151576_e = (new Material(MapColor.field_151665_m)).setRequiresTool();
    public static final Material field_151573_f = (new Material(MapColor.field_151668_h)).setRequiresTool();
    public static final Material field_151574_g = (new Material(MapColor.field_151668_h)).setRequiresTool().setImmovableMobility();
    public static final Material field_151586_h = (new MaterialLiquid(MapColor.field_151662_n)).setNoPushMobility();
    public static final Material field_151587_i = (new MaterialLiquid(MapColor.field_151656_f)).setNoPushMobility();
    public static final Material field_151584_j = (new Material(MapColor.field_151669_i)).setBurning().setTranslucent().setNoPushMobility();
    public static final Material field_151585_k = (new MaterialLogic(MapColor.field_151669_i)).setNoPushMobility();
    public static final Material field_151582_l = (new MaterialLogic(MapColor.field_151669_i)).setBurning().setNoPushMobility().setReplaceable();
    public static final Material field_151583_m = new Material(MapColor.field_151659_e);
    public static final Material field_151580_n = (new Material(MapColor.field_151659_e)).setBurning();
    public static final Material field_151581_o = (new MaterialTransparent(MapColor.field_151660_b)).setNoPushMobility();
    public static final Material field_151595_p = new Material(MapColor.field_151658_d);
    public static final Material field_151594_q = (new MaterialLogic(MapColor.field_151660_b)).setNoPushMobility();
    public static final Material field_151593_r = (new MaterialLogic(MapColor.field_151659_e)).setBurning();
    public static final Material field_151592_s = (new Material(MapColor.field_151660_b)).setTranslucent().setAdventureModeExempt();
    public static final Material field_151591_t = (new Material(MapColor.field_151660_b)).setAdventureModeExempt();
    public static final Material field_151590_u = (new Material(MapColor.field_151656_f)).setBurning().setTranslucent();
    public static final Material field_151589_v = (new Material(MapColor.field_151669_i)).setNoPushMobility();
    public static final Material field_151588_w = (new Material(MapColor.field_151657_g)).setTranslucent().setAdventureModeExempt();
    public static final Material field_151598_x = (new Material(MapColor.field_151657_g)).setAdventureModeExempt();
    public static final Material field_151597_y = (new MaterialLogic(MapColor.field_151666_j)).setReplaceable().setTranslucent().setRequiresTool().setNoPushMobility();
    public static final Material field_151596_z = (new Material(MapColor.field_151666_j)).setRequiresTool();
    public static final Material field_151570_A = (new Material(MapColor.field_151669_i)).setTranslucent().setNoPushMobility();
    public static final Material field_151571_B = new Material(MapColor.field_151667_k);
    public static final Material field_151572_C = (new Material(MapColor.field_151669_i)).setNoPushMobility();
    public static final Material field_151566_D = (new Material(MapColor.field_151669_i)).setNoPushMobility();
    public static final Material field_151567_E = (new MaterialPortal(MapColor.field_151660_b)).setImmovableMobility();
    public static final Material field_151568_F = (new Material(MapColor.field_151660_b)).setNoPushMobility();
    public static final Material field_151569_G = (new Material(MapColor.field_151659_e)
    {
        private static final String __OBFID = "CL_00000543";
        // JAVADOC METHOD $$ func_76230_c
        public boolean blocksMovement()
        {
            return false;
        }
    }).setRequiresTool().setNoPushMobility();
    // JAVADOC FIELD $$ field_76233_E
    public static final Material piston = (new Material(MapColor.field_151665_m)).setImmovableMobility();
    // JAVADOC FIELD $$ field_76235_G
    private boolean canBurn;
    // JAVADOC FIELD $$ field_76239_H
    private boolean replaceable;
    // JAVADOC FIELD $$ field_76240_I
    private boolean isTranslucent;
    // JAVADOC FIELD $$ field_76234_F
    private final MapColor materialMapColor;
    // JAVADOC FIELD $$ field_76241_J
    private boolean requiresNoTool = true;
    // JAVADOC FIELD $$ field_76242_K
    private int mobilityFlag;
    private boolean isAdventureModeExempt;
    private static final String __OBFID = "CL_00000542";

    public Material(MapColor par1MapColor)
    {
        this.materialMapColor = par1MapColor;
    }

    // JAVADOC METHOD $$ func_76224_d
    public boolean isLiquid()
    {
        return false;
    }

    public boolean isSolid()
    {
        return true;
    }

    // JAVADOC METHOD $$ func_76228_b
    public boolean getCanBlockGrass()
    {
        return true;
    }

    // JAVADOC METHOD $$ func_76230_c
    public boolean blocksMovement()
    {
        return true;
    }

    // JAVADOC METHOD $$ func_76223_p
    private Material setTranslucent()
    {
        this.isTranslucent = true;
        return this;
    }

    // JAVADOC METHOD $$ func_76221_f
    protected Material setRequiresTool()
    {
        this.requiresNoTool = false;
        return this;
    }

    // JAVADOC METHOD $$ func_76226_g
    protected Material setBurning()
    {
        this.canBurn = true;
        return this;
    }

    // JAVADOC METHOD $$ func_76217_h
    public boolean getCanBurn()
    {
        return this.canBurn;
    }

    // JAVADOC METHOD $$ func_76231_i
    public Material setReplaceable()
    {
        this.replaceable = true;
        return this;
    }

    // JAVADOC METHOD $$ func_76222_j
    public boolean isReplaceable()
    {
        return this.replaceable;
    }

    // JAVADOC METHOD $$ func_76218_k
    public boolean isOpaque()
    {
        return this.isTranslucent ? false : this.blocksMovement();
    }

    // JAVADOC METHOD $$ func_76229_l
    public boolean isToolNotRequired()
    {
        return this.requiresNoTool;
    }

    // JAVADOC METHOD $$ func_76227_m
    public int getMaterialMobility()
    {
        return this.mobilityFlag;
    }

    // JAVADOC METHOD $$ func_76219_n
    protected Material setNoPushMobility()
    {
        this.mobilityFlag = 1;
        return this;
    }

    // JAVADOC METHOD $$ func_76225_o
    protected Material setImmovableMobility()
    {
        this.mobilityFlag = 2;
        return this;
    }

    // JAVADOC METHOD $$ func_85158_p
    protected Material setAdventureModeExempt()
    {
        this.isAdventureModeExempt = true;
        return this;
    }

    // JAVADOC METHOD $$ func_85157_q
    public boolean isAdventureModeExempt()
    {
        return this.isAdventureModeExempt;
    }

    public MapColor func_151565_r()
    {
        return this.materialMapColor;
    }
}