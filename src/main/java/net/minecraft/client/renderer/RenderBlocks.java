package net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockBeacon;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRedstoneComparator;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStem;
import net.minecraft.block.BlockTripWire;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererChestHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.src.FMLRenderAccessLibrary;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.Direction;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import static net.minecraftforge.common.util.ForgeDirection.*;

@SideOnly(Side.CLIENT)
public class RenderBlocks
{
    public IBlockAccess field_147845_a;
    public IIcon field_147840_d;
    public boolean field_147842_e;
    public boolean field_147837_f;
    public static boolean field_147843_b = true;
    public boolean field_147844_c = true;
    public boolean field_147838_g = false;
    public double field_147859_h;
    public double field_147861_i;
    public double field_147855_j;
    public double field_147857_k;
    public double field_147851_l;
    public double field_147853_m;
    public boolean field_147847_n;
    public boolean field_147849_o;
    public final Minecraft field_147877_p;
    public int field_147875_q;
    public int field_147873_r;
    public int field_147871_s;
    public int field_147869_t;
    public int field_147867_u;
    public int field_147865_v;
    public boolean field_147863_w;
    public float field_147888_x;
    public float field_147886_y;
    public float field_147884_z;
    public float field_147814_A;
    public float field_147815_B;
    public float field_147816_C;
    public float field_147810_D;
    public float field_147811_E;
    public float field_147812_F;
    public float field_147813_G;
    public float field_147821_H;
    public float field_147822_I;
    public float field_147823_J;
    public float field_147824_K;
    public float field_147817_L;
    public float field_147818_M;
    public float field_147819_N;
    public float field_147820_O;
    public float field_147830_P;
    public float field_147829_Q;
    public int field_147832_R;
    public int field_147831_S;
    public int field_147826_T;
    public int field_147825_U;
    public int field_147828_V;
    public int field_147827_W;
    public int field_147835_X;
    public int field_147834_Y;
    public int field_147836_Z;
    public int field_147880_aa;
    public int field_147881_ab;
    public int field_147878_ac;
    public int field_147879_ad;
    public int field_147885_ae;
    public int field_147887_af;
    public int field_147882_ag;
    public int field_147883_ah;
    public int field_147866_ai;
    public int field_147868_aj;
    public int field_147862_ak;
    public int field_147864_al;
    public int field_147874_am;
    public int field_147876_an;
    public int field_147870_ao;
    public float field_147872_ap;
    public float field_147852_aq;
    public float field_147850_ar;
    public float field_147848_as;
    public float field_147846_at;
    public float field_147860_au;
    public float field_147858_av;
    public float field_147856_aw;
    public float field_147854_ax;
    public float field_147841_ay;
    public float field_147839_az;
    public float field_147833_aA;
    private static final String __OBFID = "CL_00000940";

    public RenderBlocks(IBlockAccess par1IBlockAccess)
    {
        this.field_147845_a = par1IBlockAccess;
        this.field_147877_p = Minecraft.getMinecraft();
    }

    public RenderBlocks()
    {
        this.field_147877_p = Minecraft.getMinecraft();
    }

    public void func_147757_a(IIcon p_147757_1_)
    {
        this.field_147840_d = p_147757_1_;
    }

    public void func_147771_a()
    {
        this.field_147840_d = null;
    }

    public boolean func_147744_b()
    {
        return this.field_147840_d != null;
    }

    public void func_147786_a(boolean p_147786_1_)
    {
        this.field_147838_g = p_147786_1_;
    }

    public void func_147753_b(boolean p_147753_1_)
    {
        this.field_147837_f = p_147753_1_;
    }

    public void func_147782_a(double p_147782_1_, double p_147782_3_, double p_147782_5_, double p_147782_7_, double p_147782_9_, double p_147782_11_)
    {
        if (!this.field_147847_n)
        {
            this.field_147859_h = p_147782_1_;
            this.field_147861_i = p_147782_7_;
            this.field_147855_j = p_147782_3_;
            this.field_147857_k = p_147782_9_;
            this.field_147851_l = p_147782_5_;
            this.field_147853_m = p_147782_11_;
            this.field_147849_o = this.field_147877_p.gameSettings.ambientOcclusion >= 2 && (this.field_147859_h > 0.0D || this.field_147861_i < 1.0D || this.field_147855_j > 0.0D || this.field_147857_k < 1.0D || this.field_147851_l > 0.0D || this.field_147853_m < 1.0D);
        }
    }

    public void func_147775_a(Block p_147775_1_)
    {
        if (!this.field_147847_n)
        {
            this.field_147859_h = p_147775_1_.func_149704_x();
            this.field_147861_i = p_147775_1_.func_149753_y();
            this.field_147855_j = p_147775_1_.func_149665_z();
            this.field_147857_k = p_147775_1_.func_149669_A();
            this.field_147851_l = p_147775_1_.func_149706_B();
            this.field_147853_m = p_147775_1_.func_149693_C();
            this.field_147849_o = this.field_147877_p.gameSettings.ambientOcclusion >= 2 && (this.field_147859_h > 0.0D || this.field_147861_i < 1.0D || this.field_147855_j > 0.0D || this.field_147857_k < 1.0D || this.field_147851_l > 0.0D || this.field_147853_m < 1.0D);
        }
    }

    public void func_147770_b(double p_147770_1_, double p_147770_3_, double p_147770_5_, double p_147770_7_, double p_147770_9_, double p_147770_11_)
    {
        this.field_147859_h = p_147770_1_;
        this.field_147861_i = p_147770_7_;
        this.field_147855_j = p_147770_3_;
        this.field_147857_k = p_147770_9_;
        this.field_147851_l = p_147770_5_;
        this.field_147853_m = p_147770_11_;
        this.field_147847_n = true;
        this.field_147849_o = this.field_147877_p.gameSettings.ambientOcclusion >= 2 && (this.field_147859_h > 0.0D || this.field_147861_i < 1.0D || this.field_147855_j > 0.0D || this.field_147857_k < 1.0D || this.field_147851_l > 0.0D || this.field_147853_m < 1.0D);
    }

    public void func_147762_c()
    {
        this.field_147847_n = false;
    }

    public void func_147792_a(Block p_147792_1_, int p_147792_2_, int p_147792_3_, int p_147792_4_, IIcon p_147792_5_)
    {
        this.func_147757_a(p_147792_5_);
        this.func_147805_b(p_147792_1_, p_147792_2_, p_147792_3_, p_147792_4_);
        this.func_147771_a();
    }

    public void func_147769_a(Block p_147769_1_, int p_147769_2_, int p_147769_3_, int p_147769_4_)
    {
        this.field_147837_f = true;
        this.func_147805_b(p_147769_1_, p_147769_2_, p_147769_3_, p_147769_4_);
        this.field_147837_f = false;
    }

    public boolean func_147805_b(Block p_147805_1_, int p_147805_2_, int p_147805_3_, int p_147805_4_)
    {
        int l = p_147805_1_.func_149645_b();

        if (l == -1)
        {
            return false;
        }
        else
        {
            p_147805_1_.func_149719_a(this.field_147845_a, p_147805_2_, p_147805_3_, p_147805_4_);
            this.func_147775_a(p_147805_1_);

            switch (l)
            {
            //regex: ' : \(l == ([\d]+) \?' replace: ';\ncase \1: return' ::: IMPORTANT: REMEMBER THIS ON FIRST line!
            case 0 : return this.func_147784_q(p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 4: return this.func_147721_p(p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 31: return this.func_147742_r(p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 1: return this.func_147746_l(p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 40: return this.func_147774_a((BlockDoublePlant)p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 2: return this.func_147791_c(p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 20: return this.func_147726_j(p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 11: return this.func_147735_a((BlockFence)p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 39: return this.func_147779_s(p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 5: return this.func_147788_h(p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 13: return this.func_147755_t(p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 9: return this.func_147766_a((BlockRailBase)p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 19: return this.func_147724_m(p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 23: return this.func_147783_o(p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 6: return this.func_147796_n(p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 3: return this.func_147801_a((BlockFire)p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 8: return this.func_147794_i(p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 7: return this.func_147760_u(p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 10: return this.func_147722_a((BlockStairs)p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 27: return this.func_147802_a((BlockDragonEgg)p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 32: return this.func_147807_a((BlockWall)p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 12: return this.func_147790_e(p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 29: return this.func_147723_f(p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 30: return this.func_147756_g(p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 14: return this.func_147773_v(p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 15: return this.func_147759_a((BlockRedstoneRepeater)p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 36: return this.func_147748_a((BlockRedstoneDiode)p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 37: return this.func_147781_a((BlockRedstoneComparator)p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 16: return this.func_147731_b(p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_, false) ;
            case 17: return this.func_147809_c(p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_, true) ;
            case 18: return this.func_147767_a((BlockPane)p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 41: return this.func_147733_k(p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 21: return this.func_147776_a((BlockFenceGate)p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 24: return this.func_147785_a((BlockCauldron)p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 33: return this.func_147752_a((BlockFlowerPot)p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 35: return this.func_147725_a((BlockAnvil)p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 25: return this.func_147741_a((BlockBrewingStand)p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 26: return this.func_147743_a((BlockEndPortalFrame)p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 28: return this.func_147772_a((BlockCocoa)p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 34: return this.func_147797_a((BlockBeacon)p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_) ;
            case 38: return this.func_147803_a((BlockHopper)p_147805_1_, p_147805_2_, p_147805_3_, p_147805_4_);
            default: return FMLRenderAccessLibrary.renderWorldBlock(this, field_147845_a, p_147805_2_, p_147805_3_, p_147805_4_, p_147805_1_, l);
            }
        }
    }

    public boolean func_147743_a(BlockEndPortalFrame p_147743_1_, int p_147743_2_, int p_147743_3_, int p_147743_4_)
    {
        int l = this.field_147845_a.getBlockMetadata(p_147743_2_, p_147743_3_, p_147743_4_);
        int i1 = l & 3;

        if (i1 == 0)
        {
            this.field_147867_u = 3;
        }
        else if (i1 == 3)
        {
            this.field_147867_u = 1;
        }
        else if (i1 == 1)
        {
            this.field_147867_u = 2;
        }

        if (!BlockEndPortalFrame.func_150020_b(l))
        {
            this.func_147782_a(0.0D, 0.0D, 0.0D, 1.0D, 0.8125D, 1.0D);
            this.func_147784_q(p_147743_1_, p_147743_2_, p_147743_3_, p_147743_4_);
            this.field_147867_u = 0;
            return true;
        }
        else
        {
            this.field_147837_f = true;
            this.func_147782_a(0.0D, 0.0D, 0.0D, 1.0D, 0.8125D, 1.0D);
            this.func_147784_q(p_147743_1_, p_147743_2_, p_147743_3_, p_147743_4_);
            this.func_147757_a(p_147743_1_.func_150021_e());
            this.func_147782_a(0.25D, 0.8125D, 0.25D, 0.75D, 1.0D, 0.75D);
            this.func_147784_q(p_147743_1_, p_147743_2_, p_147743_3_, p_147743_4_);
            this.field_147837_f = false;
            this.func_147771_a();
            this.field_147867_u = 0;
            return true;
        }
    }

    public boolean func_147773_v(Block p_147773_1_, int p_147773_2_, int p_147773_3_, int p_147773_4_)
    {
        Tessellator tessellator = Tessellator.instance;
        Block bed = this.field_147845_a.func_147439_a(p_147773_2_, p_147773_3_, p_147773_4_);
        int i1 = bed.getBedDirection(field_147845_a, p_147773_2_, p_147773_3_, p_147773_4_);
        boolean flag = bed.isBedFoot(field_147845_a, p_147773_2_, p_147773_3_, p_147773_4_);
        float f = 0.5F;
        float f1 = 1.0F;
        float f2 = 0.8F;
        float f3 = 0.6F;
        int j1 = p_147773_1_.func_149677_c(this.field_147845_a, p_147773_2_, p_147773_3_, p_147773_4_);
        tessellator.setBrightness(j1);
        tessellator.setColorOpaque_F(f, f, f);
        IIcon iicon = this.func_147793_a(p_147773_1_, this.field_147845_a, p_147773_2_, p_147773_3_, p_147773_4_, 0);
        if (func_147744_b()) iicon = field_147840_d; //BugFix Proper breaking texture on underside
        double d0 = (double)iicon.getMinU();
        double d1 = (double)iicon.getMaxU();
        double d2 = (double)iicon.getMinV();
        double d3 = (double)iicon.getMaxV();
        double d4 = (double)p_147773_2_ + this.field_147859_h;
        double d5 = (double)p_147773_2_ + this.field_147861_i;
        double d6 = (double)p_147773_3_ + this.field_147855_j + 0.1875D;
        double d7 = (double)p_147773_4_ + this.field_147851_l;
        double d8 = (double)p_147773_4_ + this.field_147853_m;
        tessellator.addVertexWithUV(d4, d6, d8, d0, d3);
        tessellator.addVertexWithUV(d4, d6, d7, d0, d2);
        tessellator.addVertexWithUV(d5, d6, d7, d1, d2);
        tessellator.addVertexWithUV(d5, d6, d8, d1, d3);
        tessellator.setBrightness(p_147773_1_.func_149677_c(this.field_147845_a, p_147773_2_, p_147773_3_ + 1, p_147773_4_));
        tessellator.setColorOpaque_F(f1, f1, f1);
        iicon = this.func_147793_a(p_147773_1_, this.field_147845_a, p_147773_2_, p_147773_3_, p_147773_4_, 1);
        if (func_147744_b()) iicon = field_147840_d; //BugFix Proper breaking texture on underside
        d0 = (double)iicon.getMinU();
        d1 = (double)iicon.getMaxU();
        d2 = (double)iicon.getMinV();
        d3 = (double)iicon.getMaxV();
        d4 = d0;
        d5 = d1;
        d6 = d2;
        d7 = d2;
        d8 = d0;
        double d9 = d1;
        double d10 = d3;
        double d11 = d3;

        if (i1 == 0)
        {
            d5 = d0;
            d6 = d3;
            d8 = d1;
            d11 = d2;
        }
        else if (i1 == 2)
        {
            d4 = d1;
            d7 = d3;
            d9 = d0;
            d10 = d2;
        }
        else if (i1 == 3)
        {
            d4 = d1;
            d7 = d3;
            d9 = d0;
            d10 = d2;
            d5 = d0;
            d6 = d3;
            d8 = d1;
            d11 = d2;
        }

        double d12 = (double)p_147773_2_ + this.field_147859_h;
        double d13 = (double)p_147773_2_ + this.field_147861_i;
        double d14 = (double)p_147773_3_ + this.field_147857_k;
        double d15 = (double)p_147773_4_ + this.field_147851_l;
        double d16 = (double)p_147773_4_ + this.field_147853_m;
        tessellator.addVertexWithUV(d13, d14, d16, d8, d10);
        tessellator.addVertexWithUV(d13, d14, d15, d4, d6);
        tessellator.addVertexWithUV(d12, d14, d15, d5, d7);
        tessellator.addVertexWithUV(d12, d14, d16, d9, d11);
        int k1 = Direction.directionToFacing[i1];

        if (flag)
        {
            k1 = Direction.directionToFacing[Direction.rotateOpposite[i1]];
        }

        byte b0 = 4;

        switch (i1)
        {
            case 0:
                b0 = 5;
                break;
            case 1:
                b0 = 3;
            case 2:
            default:
                break;
            case 3:
                b0 = 2;
        }

        if (k1 != 2 && (this.field_147837_f || p_147773_1_.func_149646_a(this.field_147845_a, p_147773_2_, p_147773_3_, p_147773_4_ - 1, 2)))
        {
            tessellator.setBrightness(this.field_147851_l > 0.0D ? j1 : p_147773_1_.func_149677_c(this.field_147845_a, p_147773_2_, p_147773_3_, p_147773_4_ - 1));
            tessellator.setColorOpaque_F(f2, f2, f2);
            this.field_147842_e = b0 == 2;
            this.func_147761_c(p_147773_1_, (double)p_147773_2_, (double)p_147773_3_, (double)p_147773_4_, this.func_147793_a(p_147773_1_, this.field_147845_a, p_147773_2_, p_147773_3_, p_147773_4_, 2));
        }

        if (k1 != 3 && (this.field_147837_f || p_147773_1_.func_149646_a(this.field_147845_a, p_147773_2_, p_147773_3_, p_147773_4_ + 1, 3)))
        {
            tessellator.setBrightness(this.field_147853_m < 1.0D ? j1 : p_147773_1_.func_149677_c(this.field_147845_a, p_147773_2_, p_147773_3_, p_147773_4_ + 1));
            tessellator.setColorOpaque_F(f2, f2, f2);
            this.field_147842_e = b0 == 3;
            this.func_147734_d(p_147773_1_, (double)p_147773_2_, (double)p_147773_3_, (double)p_147773_4_, this.func_147793_a(p_147773_1_, this.field_147845_a, p_147773_2_, p_147773_3_, p_147773_4_, 3));
        }

        if (k1 != 4 && (this.field_147837_f || p_147773_1_.func_149646_a(this.field_147845_a, p_147773_2_ - 1, p_147773_3_, p_147773_4_, 4)))
        {
            tessellator.setBrightness(this.field_147851_l > 0.0D ? j1 : p_147773_1_.func_149677_c(this.field_147845_a, p_147773_2_ - 1, p_147773_3_, p_147773_4_));
            tessellator.setColorOpaque_F(f3, f3, f3);
            this.field_147842_e = b0 == 4;
            this.func_147798_e(p_147773_1_, (double)p_147773_2_, (double)p_147773_3_, (double)p_147773_4_, this.func_147793_a(p_147773_1_, this.field_147845_a, p_147773_2_, p_147773_3_, p_147773_4_, 4));
        }

        if (k1 != 5 && (this.field_147837_f || p_147773_1_.func_149646_a(this.field_147845_a, p_147773_2_ + 1, p_147773_3_, p_147773_4_, 5)))
        {
            tessellator.setBrightness(this.field_147853_m < 1.0D ? j1 : p_147773_1_.func_149677_c(this.field_147845_a, p_147773_2_ + 1, p_147773_3_, p_147773_4_));
            tessellator.setColorOpaque_F(f3, f3, f3);
            this.field_147842_e = b0 == 5;
            this.func_147764_f(p_147773_1_, (double)p_147773_2_, (double)p_147773_3_, (double)p_147773_4_, this.func_147793_a(p_147773_1_, this.field_147845_a, p_147773_2_, p_147773_3_, p_147773_4_, 5));
        }

        this.field_147842_e = false;
        return true;
    }

    public boolean func_147741_a(BlockBrewingStand p_147741_1_, int p_147741_2_, int p_147741_3_, int p_147741_4_)
    {
        this.func_147782_a(0.4375D, 0.0D, 0.4375D, 0.5625D, 0.875D, 0.5625D);
        this.func_147784_q(p_147741_1_, p_147741_2_, p_147741_3_, p_147741_4_);
        this.func_147757_a(p_147741_1_.func_149959_e());
        this.field_147837_f = true;
        this.func_147782_a(0.5625D, 0.0D, 0.3125D, 0.9375D, 0.125D, 0.6875D);
        this.func_147784_q(p_147741_1_, p_147741_2_, p_147741_3_, p_147741_4_);
        this.func_147782_a(0.125D, 0.0D, 0.0625D, 0.5D, 0.125D, 0.4375D);
        this.func_147784_q(p_147741_1_, p_147741_2_, p_147741_3_, p_147741_4_);
        this.func_147782_a(0.125D, 0.0D, 0.5625D, 0.5D, 0.125D, 0.9375D);
        this.func_147784_q(p_147741_1_, p_147741_2_, p_147741_3_, p_147741_4_);
        this.field_147837_f = false;
        this.func_147771_a();
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(p_147741_1_.func_149677_c(this.field_147845_a, p_147741_2_, p_147741_3_, p_147741_4_));
        int l = p_147741_1_.func_149720_d(this.field_147845_a, p_147741_2_, p_147741_3_, p_147741_4_);
        float f = (float)(l >> 16 & 255) / 255.0F;
        float f1 = (float)(l >> 8 & 255) / 255.0F;
        float f2 = (float)(l & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable)
        {
            float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
            float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            f = f3;
            f1 = f4;
            f2 = f5;
        }

        tessellator.setColorOpaque_F(f, f1, f2);
        IIcon iicon = this.func_147787_a(p_147741_1_, 0, 0);

        if (this.func_147744_b())
        {
            iicon = this.field_147840_d;
        }

        double d8 = (double)iicon.getMinV();
        double d0 = (double)iicon.getMaxV();
        int i1 = this.field_147845_a.getBlockMetadata(p_147741_2_, p_147741_3_, p_147741_4_);

        for (int j1 = 0; j1 < 3; ++j1)
        {
            double d1 = (double)j1 * Math.PI * 2.0D / 3.0D + (Math.PI / 2D);
            double d2 = (double)iicon.getInterpolatedU(8.0D);
            double d3 = (double)iicon.getMaxU();

            if ((i1 & 1 << j1) != 0)
            {
                d3 = (double)iicon.getMinU();
            }

            double d4 = (double)p_147741_2_ + 0.5D;
            double d5 = (double)p_147741_2_ + 0.5D + Math.sin(d1) * 8.0D / 16.0D;
            double d6 = (double)p_147741_4_ + 0.5D;
            double d7 = (double)p_147741_4_ + 0.5D + Math.cos(d1) * 8.0D / 16.0D;
            tessellator.addVertexWithUV(d4, (double)(p_147741_3_ + 1), d6, d2, d8);
            tessellator.addVertexWithUV(d4, (double)(p_147741_3_ + 0), d6, d2, d0);
            tessellator.addVertexWithUV(d5, (double)(p_147741_3_ + 0), d7, d3, d0);
            tessellator.addVertexWithUV(d5, (double)(p_147741_3_ + 1), d7, d3, d8);
            tessellator.addVertexWithUV(d5, (double)(p_147741_3_ + 1), d7, d3, d8);
            tessellator.addVertexWithUV(d5, (double)(p_147741_3_ + 0), d7, d3, d0);
            tessellator.addVertexWithUV(d4, (double)(p_147741_3_ + 0), d6, d2, d0);
            tessellator.addVertexWithUV(d4, (double)(p_147741_3_ + 1), d6, d2, d8);
        }

        p_147741_1_.func_149683_g();
        return true;
    }

    public boolean func_147785_a(BlockCauldron p_147785_1_, int p_147785_2_, int p_147785_3_, int p_147785_4_)
    {
        this.func_147784_q(p_147785_1_, p_147785_2_, p_147785_3_, p_147785_4_);
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(p_147785_1_.func_149677_c(this.field_147845_a, p_147785_2_, p_147785_3_, p_147785_4_));
        int l = p_147785_1_.func_149720_d(this.field_147845_a, p_147785_2_, p_147785_3_, p_147785_4_);
        float f = (float)(l >> 16 & 255) / 255.0F;
        float f1 = (float)(l >> 8 & 255) / 255.0F;
        float f2 = (float)(l & 255) / 255.0F;
        float f4;

        if (EntityRenderer.anaglyphEnable)
        {
            float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
            f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            f = f3;
            f1 = f4;
            f2 = f5;
        }

        tessellator.setColorOpaque_F(f, f1, f2);
        IIcon iicon1 = p_147785_1_.func_149733_h(2);
        f4 = 0.125F;
        this.func_147764_f(p_147785_1_, (double)((float)p_147785_2_ - 1.0F + f4), (double)p_147785_3_, (double)p_147785_4_, iicon1);
        this.func_147798_e(p_147785_1_, (double)((float)p_147785_2_ + 1.0F - f4), (double)p_147785_3_, (double)p_147785_4_, iicon1);
        this.func_147734_d(p_147785_1_, (double)p_147785_2_, (double)p_147785_3_, (double)((float)p_147785_4_ - 1.0F + f4), iicon1);
        this.func_147761_c(p_147785_1_, (double)p_147785_2_, (double)p_147785_3_, (double)((float)p_147785_4_ + 1.0F - f4), iicon1);
        IIcon iicon2 = BlockCauldron.func_150026_e("inner");
        this.func_147806_b(p_147785_1_, (double)p_147785_2_, (double)((float)p_147785_3_ - 1.0F + 0.25F), (double)p_147785_4_, iicon2);
        this.func_147768_a(p_147785_1_, (double)p_147785_2_, (double)((float)p_147785_3_ + 1.0F - 0.75F), (double)p_147785_4_, iicon2);
        int i1 = this.field_147845_a.getBlockMetadata(p_147785_2_, p_147785_3_, p_147785_4_);

        if (i1 > 0)
        {
            IIcon iicon = BlockLiquid.func_149803_e("water_still");
            this.func_147806_b(p_147785_1_, (double)p_147785_2_, (double)((float)p_147785_3_ - 1.0F + BlockCauldron.func_150025_c(i1)), (double)p_147785_4_, iicon);
        }

        return true;
    }

    public boolean func_147752_a(BlockFlowerPot p_147752_1_, int p_147752_2_, int p_147752_3_, int p_147752_4_)
    {
        this.func_147784_q(p_147752_1_, p_147752_2_, p_147752_3_, p_147752_4_);
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(p_147752_1_.func_149677_c(this.field_147845_a, p_147752_2_, p_147752_3_, p_147752_4_));
        int l = p_147752_1_.func_149720_d(this.field_147845_a, p_147752_2_, p_147752_3_, p_147752_4_);
        IIcon iicon = this.func_147777_a(p_147752_1_, 0);
        float f = (float)(l >> 16 & 255) / 255.0F;
        float f1 = (float)(l >> 8 & 255) / 255.0F;
        float f2 = (float)(l & 255) / 255.0F;
        float f3;

        if (EntityRenderer.anaglyphEnable)
        {
            f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
            float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            f = f3;
            f1 = f4;
            f2 = f5;
        }

        tessellator.setColorOpaque_F(f, f1, f2);
        f3 = 0.1865F;
        this.func_147764_f(p_147752_1_, (double)((float)p_147752_2_ - 0.5F + f3), (double)p_147752_3_, (double)p_147752_4_, iicon);
        this.func_147798_e(p_147752_1_, (double)((float)p_147752_2_ + 0.5F - f3), (double)p_147752_3_, (double)p_147752_4_, iicon);
        this.func_147734_d(p_147752_1_, (double)p_147752_2_, (double)p_147752_3_, (double)((float)p_147752_4_ - 0.5F + f3), iicon);
        this.func_147761_c(p_147752_1_, (double)p_147752_2_, (double)p_147752_3_, (double)((float)p_147752_4_ + 0.5F - f3), iicon);
        this.func_147806_b(p_147752_1_, (double)p_147752_2_, (double)((float)p_147752_3_ - 0.5F + f3 + 0.1875F), (double)p_147752_4_, this.func_147745_b(Blocks.dirt));
        TileEntity tileentity = this.field_147845_a.func_147438_o(p_147752_2_, p_147752_3_, p_147752_4_);

        if (tileentity != null && tileentity instanceof TileEntityFlowerPot)
        {
            Item item = ((TileEntityFlowerPot)tileentity).func_145965_a();
            int i1 = ((TileEntityFlowerPot)tileentity).func_145966_b();

            if (item instanceof ItemBlock)
            {
                Block block = Block.func_149634_a(item);
                int j1 = block.func_149645_b();
                float f6 = 0.0F;
                float f7 = 4.0F;
                float f8 = 0.0F;
                tessellator.addTranslation(f6 / 16.0F, f7 / 16.0F, f8 / 16.0F);
                l = block.func_149720_d(this.field_147845_a, p_147752_2_, p_147752_3_, p_147752_4_);

                if (l != 16777215)
                {
                    f = (float)(l >> 16 & 255) / 255.0F;
                    f1 = (float)(l >> 8 & 255) / 255.0F;
                    f2 = (float)(l & 255) / 255.0F;
                    tessellator.setColorOpaque_F(f, f1, f2);
                }

                if (j1 == 1)
                {
                    this.func_147765_a(this.func_147787_a(block, 0, i1), (double)p_147752_2_, (double)p_147752_3_, (double)p_147752_4_, 0.75F);
                }
                else if (j1 == 13)
                {
                    this.field_147837_f = true;
                    float f9 = 0.125F;
                    this.func_147782_a((double)(0.5F - f9), 0.0D, (double)(0.5F - f9), (double)(0.5F + f9), 0.25D, (double)(0.5F + f9));
                    this.func_147784_q(block, p_147752_2_, p_147752_3_, p_147752_4_);
                    this.func_147782_a((double)(0.5F - f9), 0.25D, (double)(0.5F - f9), (double)(0.5F + f9), 0.5D, (double)(0.5F + f9));
                    this.func_147784_q(block, p_147752_2_, p_147752_3_, p_147752_4_);
                    this.func_147782_a((double)(0.5F - f9), 0.5D, (double)(0.5F - f9), (double)(0.5F + f9), 0.75D, (double)(0.5F + f9));
                    this.func_147784_q(block, p_147752_2_, p_147752_3_, p_147752_4_);
                    this.field_147837_f = false;
                    this.func_147782_a(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
                }

                tessellator.addTranslation(-f6 / 16.0F, -f7 / 16.0F, -f8 / 16.0F);
            }
        }

        return true;
    }

    public boolean func_147725_a(BlockAnvil p_147725_1_, int p_147725_2_, int p_147725_3_, int p_147725_4_)
    {
        return this.func_147780_a(p_147725_1_, p_147725_2_, p_147725_3_, p_147725_4_, this.field_147845_a.getBlockMetadata(p_147725_2_, p_147725_3_, p_147725_4_));
    }

    public boolean func_147780_a(BlockAnvil p_147780_1_, int p_147780_2_, int p_147780_3_, int p_147780_4_, int p_147780_5_)
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(p_147780_1_.func_149677_c(this.field_147845_a, p_147780_2_, p_147780_3_, p_147780_4_));
        int i1 = p_147780_1_.func_149720_d(this.field_147845_a, p_147780_2_, p_147780_3_, p_147780_4_);
        float f = (float)(i1 >> 16 & 255) / 255.0F;
        float f1 = (float)(i1 >> 8 & 255) / 255.0F;
        float f2 = (float)(i1 & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable)
        {
            float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
            float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            f = f3;
            f1 = f4;
            f2 = f5;
        }

        tessellator.setColorOpaque_F(f, f1, f2);
        return this.func_147728_a(p_147780_1_, p_147780_2_, p_147780_3_, p_147780_4_, p_147780_5_, false);
    }

    public boolean func_147728_a(BlockAnvil p_147728_1_, int p_147728_2_, int p_147728_3_, int p_147728_4_, int p_147728_5_, boolean p_147728_6_)
    {
        int i1 = p_147728_6_ ? 0 : p_147728_5_ & 3;
        boolean flag1 = false;
        float f = 0.0F;

        switch (i1)
        {
            case 0:
                this.field_147871_s = 2;
                this.field_147869_t = 1;
                this.field_147867_u = 3;
                this.field_147865_v = 3;
                break;
            case 1:
                this.field_147875_q = 1;
                this.field_147873_r = 2;
                this.field_147867_u = 2;
                this.field_147865_v = 1;
                flag1 = true;
                break;
            case 2:
                this.field_147871_s = 1;
                this.field_147869_t = 2;
                break;
            case 3:
                this.field_147875_q = 2;
                this.field_147873_r = 1;
                this.field_147867_u = 1;
                this.field_147865_v = 2;
                flag1 = true;
        }

        f = this.func_147737_a(p_147728_1_, p_147728_2_, p_147728_3_, p_147728_4_, 0, f, 0.75F, 0.25F, 0.75F, flag1, p_147728_6_, p_147728_5_);
        f = this.func_147737_a(p_147728_1_, p_147728_2_, p_147728_3_, p_147728_4_, 1, f, 0.5F, 0.0625F, 0.625F, flag1, p_147728_6_, p_147728_5_);
        f = this.func_147737_a(p_147728_1_, p_147728_2_, p_147728_3_, p_147728_4_, 2, f, 0.25F, 0.3125F, 0.5F, flag1, p_147728_6_, p_147728_5_);
        this.func_147737_a(p_147728_1_, p_147728_2_, p_147728_3_, p_147728_4_, 3, f, 0.625F, 0.375F, 1.0F, flag1, p_147728_6_, p_147728_5_);
        this.func_147782_a(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
        this.field_147875_q = 0;
        this.field_147873_r = 0;
        this.field_147871_s = 0;
        this.field_147869_t = 0;
        this.field_147867_u = 0;
        this.field_147865_v = 0;
        return true;
    }

    public float func_147737_a(BlockAnvil p_147737_1_, int p_147737_2_, int p_147737_3_, int p_147737_4_, int p_147737_5_, float p_147737_6_, float p_147737_7_, float p_147737_8_, float p_147737_9_, boolean p_147737_10_, boolean p_147737_11_, int p_147737_12_)
    {
        if (p_147737_10_)
        {
            float f4 = p_147737_7_;
            p_147737_7_ = p_147737_9_;
            p_147737_9_ = f4;
        }

        p_147737_7_ /= 2.0F;
        p_147737_9_ /= 2.0F;
        p_147737_1_.field_149833_b = p_147737_5_;
        this.func_147782_a((double)(0.5F - p_147737_7_), (double)p_147737_6_, (double)(0.5F - p_147737_9_), (double)(0.5F + p_147737_7_), (double)(p_147737_6_ + p_147737_8_), (double)(0.5F + p_147737_9_));

        if (p_147737_11_)
        {
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, -1.0F, 0.0F);
            this.func_147768_a(p_147737_1_, 0.0D, 0.0D, 0.0D, this.func_147787_a(p_147737_1_, 0, p_147737_12_));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 1.0F, 0.0F);
            this.func_147806_b(p_147737_1_, 0.0D, 0.0D, 0.0D, this.func_147787_a(p_147737_1_, 1, p_147737_12_));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, -1.0F);
            this.func_147761_c(p_147737_1_, 0.0D, 0.0D, 0.0D, this.func_147787_a(p_147737_1_, 2, p_147737_12_));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, 1.0F);
            this.func_147734_d(p_147737_1_, 0.0D, 0.0D, 0.0D, this.func_147787_a(p_147737_1_, 3, p_147737_12_));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(-1.0F, 0.0F, 0.0F);
            this.func_147798_e(p_147737_1_, 0.0D, 0.0D, 0.0D, this.func_147787_a(p_147737_1_, 4, p_147737_12_));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(1.0F, 0.0F, 0.0F);
            this.func_147764_f(p_147737_1_, 0.0D, 0.0D, 0.0D, this.func_147787_a(p_147737_1_, 5, p_147737_12_));
            tessellator.draw();
        }
        else
        {
            this.func_147784_q(p_147737_1_, p_147737_2_, p_147737_3_, p_147737_4_);
        }

        return p_147737_6_ + p_147737_8_;
    }

    public boolean func_147791_c(Block p_147791_1_, int p_147791_2_, int p_147791_3_, int p_147791_4_)
    {
        int l = this.field_147845_a.getBlockMetadata(p_147791_2_, p_147791_3_, p_147791_4_);
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(p_147791_1_.func_149677_c(this.field_147845_a, p_147791_2_, p_147791_3_, p_147791_4_));
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        double d0 = 0.4000000059604645D;
        double d1 = 0.5D - d0;
        double d2 = 0.20000000298023224D;

        if (l == 1)
        {
            this.func_147747_a(p_147791_1_, (double)p_147791_2_ - d1, (double)p_147791_3_ + d2, (double)p_147791_4_, -d0, 0.0D, 0);
        }
        else if (l == 2)
        {
            this.func_147747_a(p_147791_1_, (double)p_147791_2_ + d1, (double)p_147791_3_ + d2, (double)p_147791_4_, d0, 0.0D, 0);
        }
        else if (l == 3)
        {
            this.func_147747_a(p_147791_1_, (double)p_147791_2_, (double)p_147791_3_ + d2, (double)p_147791_4_ - d1, 0.0D, -d0, 0);
        }
        else if (l == 4)
        {
            this.func_147747_a(p_147791_1_, (double)p_147791_2_, (double)p_147791_3_ + d2, (double)p_147791_4_ + d1, 0.0D, d0, 0);
        }
        else
        {
            this.func_147747_a(p_147791_1_, (double)p_147791_2_, (double)p_147791_3_, (double)p_147791_4_, 0.0D, 0.0D, 0);
        }

        return true;
    }

    public boolean func_147759_a(BlockRedstoneRepeater p_147759_1_, int p_147759_2_, int p_147759_3_, int p_147759_4_)
    {
        int l = this.field_147845_a.getBlockMetadata(p_147759_2_, p_147759_3_, p_147759_4_);
        int i1 = l & 3;
        int j1 = (l & 12) >> 2;
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(p_147759_1_.func_149677_c(this.field_147845_a, p_147759_2_, p_147759_3_, p_147759_4_));
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        double d0 = -0.1875D;
        boolean flag = p_147759_1_.func_149910_g(this.field_147845_a, p_147759_2_, p_147759_3_, p_147759_4_, l);
        double d1 = 0.0D;
        double d2 = 0.0D;
        double d3 = 0.0D;
        double d4 = 0.0D;

        switch (i1)
        {
            case 0:
                d4 = -0.3125D;
                d2 = BlockRedstoneRepeater.field_149973_b[j1];
                break;
            case 1:
                d3 = 0.3125D;
                d1 = -BlockRedstoneRepeater.field_149973_b[j1];
                break;
            case 2:
                d4 = 0.3125D;
                d2 = -BlockRedstoneRepeater.field_149973_b[j1];
                break;
            case 3:
                d3 = -0.3125D;
                d1 = BlockRedstoneRepeater.field_149973_b[j1];
        }

        if (!flag)
        {
            this.func_147747_a(p_147759_1_, (double)p_147759_2_ + d1, (double)p_147759_3_ + d0, (double)p_147759_4_ + d2, 0.0D, 0.0D, 0);
        }
        else
        {
            IIcon iicon = this.func_147745_b(Blocks.bedrock);
            this.func_147757_a(iicon);
            float f = 2.0F;
            float f1 = 14.0F;
            float f2 = 7.0F;
            float f3 = 9.0F;

            switch (i1)
            {
                case 1:
                case 3:
                    f = 7.0F;
                    f1 = 9.0F;
                    f2 = 2.0F;
                    f3 = 14.0F;
                case 0:
                case 2:
                default:
                    this.func_147782_a((double)(f / 16.0F + (float)d1), 0.125D, (double)(f2 / 16.0F + (float)d2), (double)(f1 / 16.0F + (float)d1), 0.25D, (double)(f3 / 16.0F + (float)d2));
                    double d5 = (double)iicon.getInterpolatedU((double)f);
                    double d6 = (double)iicon.getInterpolatedV((double)f2);
                    double d7 = (double)iicon.getInterpolatedU((double)f1);
                    double d8 = (double)iicon.getInterpolatedV((double)f3);
                    tessellator.addVertexWithUV((double)((float)p_147759_2_ + f / 16.0F) + d1, (double)((float)p_147759_3_ + 0.25F), (double)((float)p_147759_4_ + f2 / 16.0F) + d2, d5, d6);
                    tessellator.addVertexWithUV((double)((float)p_147759_2_ + f / 16.0F) + d1, (double)((float)p_147759_3_ + 0.25F), (double)((float)p_147759_4_ + f3 / 16.0F) + d2, d5, d8);
                    tessellator.addVertexWithUV((double)((float)p_147759_2_ + f1 / 16.0F) + d1, (double)((float)p_147759_3_ + 0.25F), (double)((float)p_147759_4_ + f3 / 16.0F) + d2, d7, d8);
                    tessellator.addVertexWithUV((double)((float)p_147759_2_ + f1 / 16.0F) + d1, (double)((float)p_147759_3_ + 0.25F), (double)((float)p_147759_4_ + f2 / 16.0F) + d2, d7, d6);
                    this.func_147784_q(p_147759_1_, p_147759_2_, p_147759_3_, p_147759_4_);
                    this.func_147782_a(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);
                    this.func_147771_a();
            }
        }

        tessellator.setBrightness(p_147759_1_.func_149677_c(this.field_147845_a, p_147759_2_, p_147759_3_, p_147759_4_));
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        this.func_147747_a(p_147759_1_, (double)p_147759_2_ + d3, (double)p_147759_3_ + d0, (double)p_147759_4_ + d4, 0.0D, 0.0D, 0);
        this.func_147748_a(p_147759_1_, p_147759_2_, p_147759_3_, p_147759_4_);
        return true;
    }

    public boolean func_147781_a(BlockRedstoneComparator p_147781_1_, int p_147781_2_, int p_147781_3_, int p_147781_4_)
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(p_147781_1_.func_149677_c(this.field_147845_a, p_147781_2_, p_147781_3_, p_147781_4_));
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        int l = this.field_147845_a.getBlockMetadata(p_147781_2_, p_147781_3_, p_147781_4_);
        int i1 = l & 3;
        double d0 = 0.0D;
        double d1 = -0.1875D;
        double d2 = 0.0D;
        double d3 = 0.0D;
        double d4 = 0.0D;
        IIcon iicon;

        if (p_147781_1_.func_149969_d(l))
        {
            iicon = Blocks.redstone_torch.func_149733_h(0);
        }
        else
        {
            d1 -= 0.1875D;
            iicon = Blocks.unlit_redstone_torch.func_149733_h(0);
        }

        switch (i1)
        {
            case 0:
                d2 = -0.3125D;
                d4 = 1.0D;
                break;
            case 1:
                d0 = 0.3125D;
                d3 = -1.0D;
                break;
            case 2:
                d2 = 0.3125D;
                d4 = -1.0D;
                break;
            case 3:
                d0 = -0.3125D;
                d3 = 1.0D;
        }

        this.func_147747_a(p_147781_1_, (double)p_147781_2_ + 0.25D * d3 + 0.1875D * d4, (double)((float)p_147781_3_ - 0.1875F), (double)p_147781_4_ + 0.25D * d4 + 0.1875D * d3, 0.0D, 0.0D, l);
        this.func_147747_a(p_147781_1_, (double)p_147781_2_ + 0.25D * d3 + -0.1875D * d4, (double)((float)p_147781_3_ - 0.1875F), (double)p_147781_4_ + 0.25D * d4 + -0.1875D * d3, 0.0D, 0.0D, l);
        this.func_147757_a(iicon);
        this.func_147747_a(p_147781_1_, (double)p_147781_2_ + d0, (double)p_147781_3_ + d1, (double)p_147781_4_ + d2, 0.0D, 0.0D, l);
        this.func_147771_a();
        this.func_147732_a(p_147781_1_, p_147781_2_, p_147781_3_, p_147781_4_, i1);
        return true;
    }

    public boolean func_147748_a(BlockRedstoneDiode p_147748_1_, int p_147748_2_, int p_147748_3_, int p_147748_4_)
    {
        Tessellator tessellator = Tessellator.instance;
        this.func_147732_a(p_147748_1_, p_147748_2_, p_147748_3_, p_147748_4_, this.field_147845_a.getBlockMetadata(p_147748_2_, p_147748_3_, p_147748_4_) & 3);
        return true;
    }

    public void func_147732_a(BlockRedstoneDiode p_147732_1_, int p_147732_2_, int p_147732_3_, int p_147732_4_, int p_147732_5_)
    {
        this.func_147784_q(p_147732_1_, p_147732_2_, p_147732_3_, p_147732_4_);
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(p_147732_1_.func_149677_c(this.field_147845_a, p_147732_2_, p_147732_3_, p_147732_4_));
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        int i1 = this.field_147845_a.getBlockMetadata(p_147732_2_, p_147732_3_, p_147732_4_);
        IIcon iicon = this.func_147787_a(p_147732_1_, 1, i1);
        double d0 = (double)iicon.getMinU();
        double d1 = (double)iicon.getMaxU();
        double d2 = (double)iicon.getMinV();
        double d3 = (double)iicon.getMaxV();
        double d4 = 0.125D;
        double d5 = (double)(p_147732_2_ + 1);
        double d6 = (double)(p_147732_2_ + 1);
        double d7 = (double)(p_147732_2_ + 0);
        double d8 = (double)(p_147732_2_ + 0);
        double d9 = (double)(p_147732_4_ + 0);
        double d10 = (double)(p_147732_4_ + 1);
        double d11 = (double)(p_147732_4_ + 1);
        double d12 = (double)(p_147732_4_ + 0);
        double d13 = (double)p_147732_3_ + d4;

        if (p_147732_5_ == 2)
        {
            d5 = d6 = (double)(p_147732_2_ + 0);
            d7 = d8 = (double)(p_147732_2_ + 1);
            d9 = d12 = (double)(p_147732_4_ + 1);
            d10 = d11 = (double)(p_147732_4_ + 0);
        }
        else if (p_147732_5_ == 3)
        {
            d5 = d8 = (double)(p_147732_2_ + 0);
            d6 = d7 = (double)(p_147732_2_ + 1);
            d9 = d10 = (double)(p_147732_4_ + 0);
            d11 = d12 = (double)(p_147732_4_ + 1);
        }
        else if (p_147732_5_ == 1)
        {
            d5 = d8 = (double)(p_147732_2_ + 1);
            d6 = d7 = (double)(p_147732_2_ + 0);
            d9 = d10 = (double)(p_147732_4_ + 1);
            d11 = d12 = (double)(p_147732_4_ + 0);
        }

        tessellator.addVertexWithUV(d8, d13, d12, d0, d2);
        tessellator.addVertexWithUV(d7, d13, d11, d0, d3);
        tessellator.addVertexWithUV(d6, d13, d10, d1, d3);
        tessellator.addVertexWithUV(d5, d13, d9, d1, d2);
    }

    public void func_147804_d(Block p_147804_1_, int p_147804_2_, int p_147804_3_, int p_147804_4_)
    {
        this.field_147837_f = true;
        this.func_147731_b(p_147804_1_, p_147804_2_, p_147804_3_, p_147804_4_, true);
        this.field_147837_f = false;
    }

    public boolean func_147731_b(Block p_147731_1_, int p_147731_2_, int p_147731_3_, int p_147731_4_, boolean p_147731_5_)
    {
        int l = this.field_147845_a.getBlockMetadata(p_147731_2_, p_147731_3_, p_147731_4_);
        boolean flag1 = p_147731_5_ || (l & 8) != 0;
        int i1 = BlockPistonBase.func_150076_b(l);
        float f = 0.25F;

        if (flag1)
        {
            switch (i1)
            {
                case 0:
                    this.field_147875_q = 3;
                    this.field_147873_r = 3;
                    this.field_147871_s = 3;
                    this.field_147869_t = 3;
                    this.func_147782_a(0.0D, 0.25D, 0.0D, 1.0D, 1.0D, 1.0D);
                    break;
                case 1:
                    this.func_147782_a(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D);
                    break;
                case 2:
                    this.field_147871_s = 1;
                    this.field_147869_t = 2;
                    this.func_147782_a(0.0D, 0.0D, 0.25D, 1.0D, 1.0D, 1.0D);
                    break;
                case 3:
                    this.field_147871_s = 2;
                    this.field_147869_t = 1;
                    this.field_147867_u = 3;
                    this.field_147865_v = 3;
                    this.func_147782_a(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.75D);
                    break;
                case 4:
                    this.field_147875_q = 1;
                    this.field_147873_r = 2;
                    this.field_147867_u = 2;
                    this.field_147865_v = 1;
                    this.func_147782_a(0.25D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
                    break;
                case 5:
                    this.field_147875_q = 2;
                    this.field_147873_r = 1;
                    this.field_147867_u = 1;
                    this.field_147865_v = 2;
                    this.func_147782_a(0.0D, 0.0D, 0.0D, 0.75D, 1.0D, 1.0D);
            }

            ((BlockPistonBase)p_147731_1_).func_150070_b((float)this.field_147859_h, (float)this.field_147855_j, (float)this.field_147851_l, (float)this.field_147861_i, (float)this.field_147857_k, (float)this.field_147853_m);
            this.func_147784_q(p_147731_1_, p_147731_2_, p_147731_3_, p_147731_4_);
            this.field_147875_q = 0;
            this.field_147873_r = 0;
            this.field_147871_s = 0;
            this.field_147869_t = 0;
            this.field_147867_u = 0;
            this.field_147865_v = 0;
            this.func_147782_a(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
            ((BlockPistonBase)p_147731_1_).func_150070_b((float)this.field_147859_h, (float)this.field_147855_j, (float)this.field_147851_l, (float)this.field_147861_i, (float)this.field_147857_k, (float)this.field_147853_m);
        }
        else
        {
            switch (i1)
            {
                case 0:
                    this.field_147875_q = 3;
                    this.field_147873_r = 3;
                    this.field_147871_s = 3;
                    this.field_147869_t = 3;
                case 1:
                default:
                    break;
                case 2:
                    this.field_147871_s = 1;
                    this.field_147869_t = 2;
                    break;
                case 3:
                    this.field_147871_s = 2;
                    this.field_147869_t = 1;
                    this.field_147867_u = 3;
                    this.field_147865_v = 3;
                    break;
                case 4:
                    this.field_147875_q = 1;
                    this.field_147873_r = 2;
                    this.field_147867_u = 2;
                    this.field_147865_v = 1;
                    break;
                case 5:
                    this.field_147875_q = 2;
                    this.field_147873_r = 1;
                    this.field_147867_u = 1;
                    this.field_147865_v = 2;
            }

            this.func_147784_q(p_147731_1_, p_147731_2_, p_147731_3_, p_147731_4_);
            this.field_147875_q = 0;
            this.field_147873_r = 0;
            this.field_147871_s = 0;
            this.field_147869_t = 0;
            this.field_147867_u = 0;
            this.field_147865_v = 0;
        }

        return true;
    }

    public void func_147763_a(double p_147763_1_, double p_147763_3_, double p_147763_5_, double p_147763_7_, double p_147763_9_, double p_147763_11_, float p_147763_13_, double p_147763_14_)
    {
        IIcon iicon = BlockPistonBase.func_150074_e("piston_side");

        if (this.func_147744_b())
        {
            iicon = this.field_147840_d;
        }

        Tessellator tessellator = Tessellator.instance;
        double d7 = (double)iicon.getMinU();
        double d8 = (double)iicon.getMinV();
        double d9 = (double)iicon.getInterpolatedU(p_147763_14_);
        double d10 = (double)iicon.getInterpolatedV(4.0D);
        tessellator.setColorOpaque_F(p_147763_13_, p_147763_13_, p_147763_13_);
        tessellator.addVertexWithUV(p_147763_1_, p_147763_7_, p_147763_9_, d9, d8);
        tessellator.addVertexWithUV(p_147763_1_, p_147763_5_, p_147763_9_, d7, d8);
        tessellator.addVertexWithUV(p_147763_3_, p_147763_5_, p_147763_11_, d7, d10);
        tessellator.addVertexWithUV(p_147763_3_, p_147763_7_, p_147763_11_, d9, d10);
    }

    public void func_147789_b(double p_147789_1_, double p_147789_3_, double p_147789_5_, double p_147789_7_, double p_147789_9_, double p_147789_11_, float p_147789_13_, double p_147789_14_)
    {
        IIcon iicon = BlockPistonBase.func_150074_e("piston_side");

        if (this.func_147744_b())
        {
            iicon = this.field_147840_d;
        }

        Tessellator tessellator = Tessellator.instance;
        double d7 = (double)iicon.getMinU();
        double d8 = (double)iicon.getMinV();
        double d9 = (double)iicon.getInterpolatedU(p_147789_14_);
        double d10 = (double)iicon.getInterpolatedV(4.0D);
        tessellator.setColorOpaque_F(p_147789_13_, p_147789_13_, p_147789_13_);
        tessellator.addVertexWithUV(p_147789_1_, p_147789_5_, p_147789_11_, d9, d8);
        tessellator.addVertexWithUV(p_147789_1_, p_147789_5_, p_147789_9_, d7, d8);
        tessellator.addVertexWithUV(p_147789_3_, p_147789_7_, p_147789_9_, d7, d10);
        tessellator.addVertexWithUV(p_147789_3_, p_147789_7_, p_147789_11_, d9, d10);
    }

    public void func_147738_c(double p_147738_1_, double p_147738_3_, double p_147738_5_, double p_147738_7_, double p_147738_9_, double p_147738_11_, float p_147738_13_, double p_147738_14_)
    {
        IIcon iicon = BlockPistonBase.func_150074_e("piston_side");

        if (this.func_147744_b())
        {
            iicon = this.field_147840_d;
        }

        Tessellator tessellator = Tessellator.instance;
        double d7 = (double)iicon.getMinU();
        double d8 = (double)iicon.getMinV();
        double d9 = (double)iicon.getInterpolatedU(p_147738_14_);
        double d10 = (double)iicon.getInterpolatedV(4.0D);
        tessellator.setColorOpaque_F(p_147738_13_, p_147738_13_, p_147738_13_);
        tessellator.addVertexWithUV(p_147738_3_, p_147738_5_, p_147738_9_, d9, d8);
        tessellator.addVertexWithUV(p_147738_1_, p_147738_5_, p_147738_9_, d7, d8);
        tessellator.addVertexWithUV(p_147738_1_, p_147738_7_, p_147738_11_, d7, d10);
        tessellator.addVertexWithUV(p_147738_3_, p_147738_7_, p_147738_11_, d9, d10);
    }

    public void func_147750_a(Block p_147750_1_, int p_147750_2_, int p_147750_3_, int p_147750_4_, boolean p_147750_5_)
    {
        this.field_147837_f = true;
        this.func_147809_c(p_147750_1_, p_147750_2_, p_147750_3_, p_147750_4_, p_147750_5_);
        this.field_147837_f = false;
    }

    public boolean func_147809_c(Block p_147809_1_, int p_147809_2_, int p_147809_3_, int p_147809_4_, boolean p_147809_5_)
    {
        int l = this.field_147845_a.getBlockMetadata(p_147809_2_, p_147809_3_, p_147809_4_);
        int i1 = BlockPistonExtension.func_150085_b(l);
        float f = 0.25F;
        float f1 = 0.375F;
        float f2 = 0.625F;
        float f3 = p_147809_5_ ? 1.0F : 0.5F;
        double d0 = p_147809_5_ ? 16.0D : 8.0D;

        switch (i1)
        {
            case 0:
                this.field_147875_q = 3;
                this.field_147873_r = 3;
                this.field_147871_s = 3;
                this.field_147869_t = 3;
                this.func_147782_a(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D);
                this.func_147784_q(p_147809_1_, p_147809_2_, p_147809_3_, p_147809_4_);
                this.func_147763_a((double)((float)p_147809_2_ + 0.375F), (double)((float)p_147809_2_ + 0.625F), (double)((float)p_147809_3_ + 0.25F), (double)((float)p_147809_3_ + 0.25F + f3), (double)((float)p_147809_4_ + 0.625F), (double)((float)p_147809_4_ + 0.625F), 0.8F, d0);
                this.func_147763_a((double)((float)p_147809_2_ + 0.625F), (double)((float)p_147809_2_ + 0.375F), (double)((float)p_147809_3_ + 0.25F), (double)((float)p_147809_3_ + 0.25F + f3), (double)((float)p_147809_4_ + 0.375F), (double)((float)p_147809_4_ + 0.375F), 0.8F, d0);
                this.func_147763_a((double)((float)p_147809_2_ + 0.375F), (double)((float)p_147809_2_ + 0.375F), (double)((float)p_147809_3_ + 0.25F), (double)((float)p_147809_3_ + 0.25F + f3), (double)((float)p_147809_4_ + 0.375F), (double)((float)p_147809_4_ + 0.625F), 0.6F, d0);
                this.func_147763_a((double)((float)p_147809_2_ + 0.625F), (double)((float)p_147809_2_ + 0.625F), (double)((float)p_147809_3_ + 0.25F), (double)((float)p_147809_3_ + 0.25F + f3), (double)((float)p_147809_4_ + 0.625F), (double)((float)p_147809_4_ + 0.375F), 0.6F, d0);
                break;
            case 1:
                this.func_147782_a(0.0D, 0.75D, 0.0D, 1.0D, 1.0D, 1.0D);
                this.func_147784_q(p_147809_1_, p_147809_2_, p_147809_3_, p_147809_4_);
                this.func_147763_a((double)((float)p_147809_2_ + 0.375F), (double)((float)p_147809_2_ + 0.625F), (double)((float)p_147809_3_ - 0.25F + 1.0F - f3), (double)((float)p_147809_3_ - 0.25F + 1.0F), (double)((float)p_147809_4_ + 0.625F), (double)((float)p_147809_4_ + 0.625F), 0.8F, d0);
                this.func_147763_a((double)((float)p_147809_2_ + 0.625F), (double)((float)p_147809_2_ + 0.375F), (double)((float)p_147809_3_ - 0.25F + 1.0F - f3), (double)((float)p_147809_3_ - 0.25F + 1.0F), (double)((float)p_147809_4_ + 0.375F), (double)((float)p_147809_4_ + 0.375F), 0.8F, d0);
                this.func_147763_a((double)((float)p_147809_2_ + 0.375F), (double)((float)p_147809_2_ + 0.375F), (double)((float)p_147809_3_ - 0.25F + 1.0F - f3), (double)((float)p_147809_3_ - 0.25F + 1.0F), (double)((float)p_147809_4_ + 0.375F), (double)((float)p_147809_4_ + 0.625F), 0.6F, d0);
                this.func_147763_a((double)((float)p_147809_2_ + 0.625F), (double)((float)p_147809_2_ + 0.625F), (double)((float)p_147809_3_ - 0.25F + 1.0F - f3), (double)((float)p_147809_3_ - 0.25F + 1.0F), (double)((float)p_147809_4_ + 0.625F), (double)((float)p_147809_4_ + 0.375F), 0.6F, d0);
                break;
            case 2:
                this.field_147871_s = 1;
                this.field_147869_t = 2;
                this.func_147782_a(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.25D);
                this.func_147784_q(p_147809_1_, p_147809_2_, p_147809_3_, p_147809_4_);
                this.func_147789_b((double)((float)p_147809_2_ + 0.375F), (double)((float)p_147809_2_ + 0.375F), (double)((float)p_147809_3_ + 0.625F), (double)((float)p_147809_3_ + 0.375F), (double)((float)p_147809_4_ + 0.25F), (double)((float)p_147809_4_ + 0.25F + f3), 0.6F, d0);
                this.func_147789_b((double)((float)p_147809_2_ + 0.625F), (double)((float)p_147809_2_ + 0.625F), (double)((float)p_147809_3_ + 0.375F), (double)((float)p_147809_3_ + 0.625F), (double)((float)p_147809_4_ + 0.25F), (double)((float)p_147809_4_ + 0.25F + f3), 0.6F, d0);
                this.func_147789_b((double)((float)p_147809_2_ + 0.375F), (double)((float)p_147809_2_ + 0.625F), (double)((float)p_147809_3_ + 0.375F), (double)((float)p_147809_3_ + 0.375F), (double)((float)p_147809_4_ + 0.25F), (double)((float)p_147809_4_ + 0.25F + f3), 0.5F, d0);
                this.func_147789_b((double)((float)p_147809_2_ + 0.625F), (double)((float)p_147809_2_ + 0.375F), (double)((float)p_147809_3_ + 0.625F), (double)((float)p_147809_3_ + 0.625F), (double)((float)p_147809_4_ + 0.25F), (double)((float)p_147809_4_ + 0.25F + f3), 1.0F, d0);
                break;
            case 3:
                this.field_147871_s = 2;
                this.field_147869_t = 1;
                this.field_147867_u = 3;
                this.field_147865_v = 3;
                this.func_147782_a(0.0D, 0.0D, 0.75D, 1.0D, 1.0D, 1.0D);
                this.func_147784_q(p_147809_1_, p_147809_2_, p_147809_3_, p_147809_4_);
                this.func_147789_b((double)((float)p_147809_2_ + 0.375F), (double)((float)p_147809_2_ + 0.375F), (double)((float)p_147809_3_ + 0.625F), (double)((float)p_147809_3_ + 0.375F), (double)((float)p_147809_4_ - 0.25F + 1.0F - f3), (double)((float)p_147809_4_ - 0.25F + 1.0F), 0.6F, d0);
                this.func_147789_b((double)((float)p_147809_2_ + 0.625F), (double)((float)p_147809_2_ + 0.625F), (double)((float)p_147809_3_ + 0.375F), (double)((float)p_147809_3_ + 0.625F), (double)((float)p_147809_4_ - 0.25F + 1.0F - f3), (double)((float)p_147809_4_ - 0.25F + 1.0F), 0.6F, d0);
                this.func_147789_b((double)((float)p_147809_2_ + 0.375F), (double)((float)p_147809_2_ + 0.625F), (double)((float)p_147809_3_ + 0.375F), (double)((float)p_147809_3_ + 0.375F), (double)((float)p_147809_4_ - 0.25F + 1.0F - f3), (double)((float)p_147809_4_ - 0.25F + 1.0F), 0.5F, d0);
                this.func_147789_b((double)((float)p_147809_2_ + 0.625F), (double)((float)p_147809_2_ + 0.375F), (double)((float)p_147809_3_ + 0.625F), (double)((float)p_147809_3_ + 0.625F), (double)((float)p_147809_4_ - 0.25F + 1.0F - f3), (double)((float)p_147809_4_ - 0.25F + 1.0F), 1.0F, d0);
                break;
            case 4:
                this.field_147875_q = 1;
                this.field_147873_r = 2;
                this.field_147867_u = 2;
                this.field_147865_v = 1;
                this.func_147782_a(0.0D, 0.0D, 0.0D, 0.25D, 1.0D, 1.0D);
                this.func_147784_q(p_147809_1_, p_147809_2_, p_147809_3_, p_147809_4_);
                this.func_147738_c((double)((float)p_147809_2_ + 0.25F), (double)((float)p_147809_2_ + 0.25F + f3), (double)((float)p_147809_3_ + 0.375F), (double)((float)p_147809_3_ + 0.375F), (double)((float)p_147809_4_ + 0.625F), (double)((float)p_147809_4_ + 0.375F), 0.5F, d0);
                this.func_147738_c((double)((float)p_147809_2_ + 0.25F), (double)((float)p_147809_2_ + 0.25F + f3), (double)((float)p_147809_3_ + 0.625F), (double)((float)p_147809_3_ + 0.625F), (double)((float)p_147809_4_ + 0.375F), (double)((float)p_147809_4_ + 0.625F), 1.0F, d0);
                this.func_147738_c((double)((float)p_147809_2_ + 0.25F), (double)((float)p_147809_2_ + 0.25F + f3), (double)((float)p_147809_3_ + 0.375F), (double)((float)p_147809_3_ + 0.625F), (double)((float)p_147809_4_ + 0.375F), (double)((float)p_147809_4_ + 0.375F), 0.6F, d0);
                this.func_147738_c((double)((float)p_147809_2_ + 0.25F), (double)((float)p_147809_2_ + 0.25F + f3), (double)((float)p_147809_3_ + 0.625F), (double)((float)p_147809_3_ + 0.375F), (double)((float)p_147809_4_ + 0.625F), (double)((float)p_147809_4_ + 0.625F), 0.6F, d0);
                break;
            case 5:
                this.field_147875_q = 2;
                this.field_147873_r = 1;
                this.field_147867_u = 1;
                this.field_147865_v = 2;
                this.func_147782_a(0.75D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
                this.func_147784_q(p_147809_1_, p_147809_2_, p_147809_3_, p_147809_4_);
                this.func_147738_c((double)((float)p_147809_2_ - 0.25F + 1.0F - f3), (double)((float)p_147809_2_ - 0.25F + 1.0F), (double)((float)p_147809_3_ + 0.375F), (double)((float)p_147809_3_ + 0.375F), (double)((float)p_147809_4_ + 0.625F), (double)((float)p_147809_4_ + 0.375F), 0.5F, d0);
                this.func_147738_c((double)((float)p_147809_2_ - 0.25F + 1.0F - f3), (double)((float)p_147809_2_ - 0.25F + 1.0F), (double)((float)p_147809_3_ + 0.625F), (double)((float)p_147809_3_ + 0.625F), (double)((float)p_147809_4_ + 0.375F), (double)((float)p_147809_4_ + 0.625F), 1.0F, d0);
                this.func_147738_c((double)((float)p_147809_2_ - 0.25F + 1.0F - f3), (double)((float)p_147809_2_ - 0.25F + 1.0F), (double)((float)p_147809_3_ + 0.375F), (double)((float)p_147809_3_ + 0.625F), (double)((float)p_147809_4_ + 0.375F), (double)((float)p_147809_4_ + 0.375F), 0.6F, d0);
                this.func_147738_c((double)((float)p_147809_2_ - 0.25F + 1.0F - f3), (double)((float)p_147809_2_ - 0.25F + 1.0F), (double)((float)p_147809_3_ + 0.625F), (double)((float)p_147809_3_ + 0.375F), (double)((float)p_147809_4_ + 0.625F), (double)((float)p_147809_4_ + 0.625F), 0.6F, d0);
        }

        this.field_147875_q = 0;
        this.field_147873_r = 0;
        this.field_147871_s = 0;
        this.field_147869_t = 0;
        this.field_147867_u = 0;
        this.field_147865_v = 0;
        this.func_147782_a(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
        return true;
    }

    public boolean func_147790_e(Block p_147790_1_, int p_147790_2_, int p_147790_3_, int p_147790_4_)
    {
        int l = this.field_147845_a.getBlockMetadata(p_147790_2_, p_147790_3_, p_147790_4_);
        int i1 = l & 7;
        boolean flag = (l & 8) > 0;
        Tessellator tessellator = Tessellator.instance;
        boolean flag1 = this.func_147744_b();

        if (!flag1)
        {
            this.func_147757_a(this.func_147745_b(Blocks.cobblestone));
        }

        float f = 0.25F;
        float f1 = 0.1875F;
        float f2 = 0.1875F;

        if (i1 == 5)
        {
            this.func_147782_a((double)(0.5F - f1), 0.0D, (double)(0.5F - f), (double)(0.5F + f1), (double)f2, (double)(0.5F + f));
        }
        else if (i1 == 6)
        {
            this.func_147782_a((double)(0.5F - f), 0.0D, (double)(0.5F - f1), (double)(0.5F + f), (double)f2, (double)(0.5F + f1));
        }
        else if (i1 == 4)
        {
            this.func_147782_a((double)(0.5F - f1), (double)(0.5F - f), (double)(1.0F - f2), (double)(0.5F + f1), (double)(0.5F + f), 1.0D);
        }
        else if (i1 == 3)
        {
            this.func_147782_a((double)(0.5F - f1), (double)(0.5F - f), 0.0D, (double)(0.5F + f1), (double)(0.5F + f), (double)f2);
        }
        else if (i1 == 2)
        {
            this.func_147782_a((double)(1.0F - f2), (double)(0.5F - f), (double)(0.5F - f1), 1.0D, (double)(0.5F + f), (double)(0.5F + f1));
        }
        else if (i1 == 1)
        {
            this.func_147782_a(0.0D, (double)(0.5F - f), (double)(0.5F - f1), (double)f2, (double)(0.5F + f), (double)(0.5F + f1));
        }
        else if (i1 == 0)
        {
            this.func_147782_a((double)(0.5F - f), (double)(1.0F - f2), (double)(0.5F - f1), (double)(0.5F + f), 1.0D, (double)(0.5F + f1));
        }
        else if (i1 == 7)
        {
            this.func_147782_a((double)(0.5F - f1), (double)(1.0F - f2), (double)(0.5F - f), (double)(0.5F + f1), 1.0D, (double)(0.5F + f));
        }

        this.func_147784_q(p_147790_1_, p_147790_2_, p_147790_3_, p_147790_4_);

        if (!flag1)
        {
            this.func_147771_a();
        }

        tessellator.setBrightness(p_147790_1_.func_149677_c(this.field_147845_a, p_147790_2_, p_147790_3_, p_147790_4_));
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        IIcon iicon = this.func_147777_a(p_147790_1_, 0);

        if (this.func_147744_b())
        {
            iicon = this.field_147840_d;
        }

        double d0 = (double)iicon.getMinU();
        double d1 = (double)iicon.getMinV();
        double d2 = (double)iicon.getMaxU();
        double d3 = (double)iicon.getMaxV();
        Vec3[] avec3 = new Vec3[8];
        float f3 = 0.0625F;
        float f4 = 0.0625F;
        float f5 = 0.625F;
        avec3[0] = this.field_147845_a.getWorldVec3Pool().getVecFromPool((double)(-f3), 0.0D, (double)(-f4));
        avec3[1] = this.field_147845_a.getWorldVec3Pool().getVecFromPool((double)f3, 0.0D, (double)(-f4));
        avec3[2] = this.field_147845_a.getWorldVec3Pool().getVecFromPool((double)f3, 0.0D, (double)f4);
        avec3[3] = this.field_147845_a.getWorldVec3Pool().getVecFromPool((double)(-f3), 0.0D, (double)f4);
        avec3[4] = this.field_147845_a.getWorldVec3Pool().getVecFromPool((double)(-f3), (double)f5, (double)(-f4));
        avec3[5] = this.field_147845_a.getWorldVec3Pool().getVecFromPool((double)f3, (double)f5, (double)(-f4));
        avec3[6] = this.field_147845_a.getWorldVec3Pool().getVecFromPool((double)f3, (double)f5, (double)f4);
        avec3[7] = this.field_147845_a.getWorldVec3Pool().getVecFromPool((double)(-f3), (double)f5, (double)f4);

        for (int j1 = 0; j1 < 8; ++j1)
        {
            if (flag)
            {
                avec3[j1].zCoord -= 0.0625D;
                avec3[j1].rotateAroundX(((float)Math.PI * 2F / 9F));
            }
            else
            {
                avec3[j1].zCoord += 0.0625D;
                avec3[j1].rotateAroundX(-((float)Math.PI * 2F / 9F));
            }

            if (i1 == 0 || i1 == 7)
            {
                avec3[j1].rotateAroundZ((float)Math.PI);
            }

            if (i1 == 6 || i1 == 0)
            {
                avec3[j1].rotateAroundY(((float)Math.PI / 2F));
            }

            if (i1 > 0 && i1 < 5)
            {
                avec3[j1].yCoord -= 0.375D;
                avec3[j1].rotateAroundX(((float)Math.PI / 2F));

                if (i1 == 4)
                {
                    avec3[j1].rotateAroundY(0.0F);
                }

                if (i1 == 3)
                {
                    avec3[j1].rotateAroundY((float)Math.PI);
                }

                if (i1 == 2)
                {
                    avec3[j1].rotateAroundY(((float)Math.PI / 2F));
                }

                if (i1 == 1)
                {
                    avec3[j1].rotateAroundY(-((float)Math.PI / 2F));
                }

                avec3[j1].xCoord += (double)p_147790_2_ + 0.5D;
                avec3[j1].yCoord += (double)((float)p_147790_3_ + 0.5F);
                avec3[j1].zCoord += (double)p_147790_4_ + 0.5D;
            }
            else if (i1 != 0 && i1 != 7)
            {
                avec3[j1].xCoord += (double)p_147790_2_ + 0.5D;
                avec3[j1].yCoord += (double)((float)p_147790_3_ + 0.125F);
                avec3[j1].zCoord += (double)p_147790_4_ + 0.5D;
            }
            else
            {
                avec3[j1].xCoord += (double)p_147790_2_ + 0.5D;
                avec3[j1].yCoord += (double)((float)p_147790_3_ + 0.875F);
                avec3[j1].zCoord += (double)p_147790_4_ + 0.5D;
            }
        }

        Vec3 vec33 = null;
        Vec3 vec3 = null;
        Vec3 vec31 = null;
        Vec3 vec32 = null;

        for (int k1 = 0; k1 < 6; ++k1)
        {
            if (k1 == 0)
            {
                d0 = (double)iicon.getInterpolatedU(7.0D);
                d1 = (double)iicon.getInterpolatedV(6.0D);
                d2 = (double)iicon.getInterpolatedU(9.0D);
                d3 = (double)iicon.getInterpolatedV(8.0D);
            }
            else if (k1 == 2)
            {
                d0 = (double)iicon.getInterpolatedU(7.0D);
                d1 = (double)iicon.getInterpolatedV(6.0D);
                d2 = (double)iicon.getInterpolatedU(9.0D);
                d3 = (double)iicon.getMaxV();
            }

            if (k1 == 0)
            {
                vec33 = avec3[0];
                vec3 = avec3[1];
                vec31 = avec3[2];
                vec32 = avec3[3];
            }
            else if (k1 == 1)
            {
                vec33 = avec3[7];
                vec3 = avec3[6];
                vec31 = avec3[5];
                vec32 = avec3[4];
            }
            else if (k1 == 2)
            {
                vec33 = avec3[1];
                vec3 = avec3[0];
                vec31 = avec3[4];
                vec32 = avec3[5];
            }
            else if (k1 == 3)
            {
                vec33 = avec3[2];
                vec3 = avec3[1];
                vec31 = avec3[5];
                vec32 = avec3[6];
            }
            else if (k1 == 4)
            {
                vec33 = avec3[3];
                vec3 = avec3[2];
                vec31 = avec3[6];
                vec32 = avec3[7];
            }
            else if (k1 == 5)
            {
                vec33 = avec3[0];
                vec3 = avec3[3];
                vec31 = avec3[7];
                vec32 = avec3[4];
            }

            tessellator.addVertexWithUV(vec33.xCoord, vec33.yCoord, vec33.zCoord, d0, d3);
            tessellator.addVertexWithUV(vec3.xCoord, vec3.yCoord, vec3.zCoord, d2, d3);
            tessellator.addVertexWithUV(vec31.xCoord, vec31.yCoord, vec31.zCoord, d2, d1);
            tessellator.addVertexWithUV(vec32.xCoord, vec32.yCoord, vec32.zCoord, d0, d1);
        }

        return true;
    }

    public boolean func_147723_f(Block p_147723_1_, int p_147723_2_, int p_147723_3_, int p_147723_4_)
    {
        Tessellator tessellator = Tessellator.instance;
        int l = this.field_147845_a.getBlockMetadata(p_147723_2_, p_147723_3_, p_147723_4_);
        int i1 = l & 3;
        boolean flag = (l & 4) == 4;
        boolean flag1 = (l & 8) == 8;
        boolean flag2 = !World.func_147466_a(this.field_147845_a, p_147723_2_, p_147723_3_ - 1, p_147723_4_);
        boolean flag3 = this.func_147744_b();

        if (!flag3)
        {
            this.func_147757_a(this.func_147745_b(Blocks.planks));
        }

        float f = 0.25F;
        float f1 = 0.125F;
        float f2 = 0.125F;
        float f3 = 0.3F - f;
        float f4 = 0.3F + f;

        if (i1 == 2)
        {
            this.func_147782_a((double)(0.5F - f1), (double)f3, (double)(1.0F - f2), (double)(0.5F + f1), (double)f4, 1.0D);
        }
        else if (i1 == 0)
        {
            this.func_147782_a((double)(0.5F - f1), (double)f3, 0.0D, (double)(0.5F + f1), (double)f4, (double)f2);
        }
        else if (i1 == 1)
        {
            this.func_147782_a((double)(1.0F - f2), (double)f3, (double)(0.5F - f1), 1.0D, (double)f4, (double)(0.5F + f1));
        }
        else if (i1 == 3)
        {
            this.func_147782_a(0.0D, (double)f3, (double)(0.5F - f1), (double)f2, (double)f4, (double)(0.5F + f1));
        }

        this.func_147784_q(p_147723_1_, p_147723_2_, p_147723_3_, p_147723_4_);

        if (!flag3)
        {
            this.func_147771_a();
        }

        tessellator.setBrightness(p_147723_1_.func_149677_c(this.field_147845_a, p_147723_2_, p_147723_3_, p_147723_4_));
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        IIcon iicon = this.func_147777_a(p_147723_1_, 0);

        if (this.func_147744_b())
        {
            iicon = this.field_147840_d;
        }

        double d0 = (double)iicon.getMinU();
        double d1 = (double)iicon.getMinV();
        double d2 = (double)iicon.getMaxU();
        double d3 = (double)iicon.getMaxV();
        Vec3[] avec3 = new Vec3[8];
        float f5 = 0.046875F;
        float f6 = 0.046875F;
        float f7 = 0.3125F;
        avec3[0] = this.field_147845_a.getWorldVec3Pool().getVecFromPool((double)(-f5), 0.0D, (double)(-f6));
        avec3[1] = this.field_147845_a.getWorldVec3Pool().getVecFromPool((double)f5, 0.0D, (double)(-f6));
        avec3[2] = this.field_147845_a.getWorldVec3Pool().getVecFromPool((double)f5, 0.0D, (double)f6);
        avec3[3] = this.field_147845_a.getWorldVec3Pool().getVecFromPool((double)(-f5), 0.0D, (double)f6);
        avec3[4] = this.field_147845_a.getWorldVec3Pool().getVecFromPool((double)(-f5), (double)f7, (double)(-f6));
        avec3[5] = this.field_147845_a.getWorldVec3Pool().getVecFromPool((double)f5, (double)f7, (double)(-f6));
        avec3[6] = this.field_147845_a.getWorldVec3Pool().getVecFromPool((double)f5, (double)f7, (double)f6);
        avec3[7] = this.field_147845_a.getWorldVec3Pool().getVecFromPool((double)(-f5), (double)f7, (double)f6);

        for (int j1 = 0; j1 < 8; ++j1)
        {
            avec3[j1].zCoord += 0.0625D;

            if (flag1)
            {
                avec3[j1].rotateAroundX(0.5235988F);
                avec3[j1].yCoord -= 0.4375D;
            }
            else if (flag)
            {
                avec3[j1].rotateAroundX(0.08726647F);
                avec3[j1].yCoord -= 0.4375D;
            }
            else
            {
                avec3[j1].rotateAroundX(-((float)Math.PI * 2F / 9F));
                avec3[j1].yCoord -= 0.375D;
            }

            avec3[j1].rotateAroundX(((float)Math.PI / 2F));

            if (i1 == 2)
            {
                avec3[j1].rotateAroundY(0.0F);
            }

            if (i1 == 0)
            {
                avec3[j1].rotateAroundY((float)Math.PI);
            }

            if (i1 == 1)
            {
                avec3[j1].rotateAroundY(((float)Math.PI / 2F));
            }

            if (i1 == 3)
            {
                avec3[j1].rotateAroundY(-((float)Math.PI / 2F));
            }

            avec3[j1].xCoord += (double)p_147723_2_ + 0.5D;
            avec3[j1].yCoord += (double)((float)p_147723_3_ + 0.3125F);
            avec3[j1].zCoord += (double)p_147723_4_ + 0.5D;
        }

        Vec3 vec33 = null;
        Vec3 vec3 = null;
        Vec3 vec31 = null;
        Vec3 vec32 = null;
        byte b0 = 7;
        byte b1 = 9;
        byte b2 = 9;
        byte b3 = 16;

        for (int k1 = 0; k1 < 6; ++k1)
        {
            if (k1 == 0)
            {
                vec33 = avec3[0];
                vec3 = avec3[1];
                vec31 = avec3[2];
                vec32 = avec3[3];
                d0 = (double)iicon.getInterpolatedU((double)b0);
                d1 = (double)iicon.getInterpolatedV((double)b2);
                d2 = (double)iicon.getInterpolatedU((double)b1);
                d3 = (double)iicon.getInterpolatedV((double)(b2 + 2));
            }
            else if (k1 == 1)
            {
                vec33 = avec3[7];
                vec3 = avec3[6];
                vec31 = avec3[5];
                vec32 = avec3[4];
            }
            else if (k1 == 2)
            {
                vec33 = avec3[1];
                vec3 = avec3[0];
                vec31 = avec3[4];
                vec32 = avec3[5];
                d0 = (double)iicon.getInterpolatedU((double)b0);
                d1 = (double)iicon.getInterpolatedV((double)b2);
                d2 = (double)iicon.getInterpolatedU((double)b1);
                d3 = (double)iicon.getInterpolatedV((double)b3);
            }
            else if (k1 == 3)
            {
                vec33 = avec3[2];
                vec3 = avec3[1];
                vec31 = avec3[5];
                vec32 = avec3[6];
            }
            else if (k1 == 4)
            {
                vec33 = avec3[3];
                vec3 = avec3[2];
                vec31 = avec3[6];
                vec32 = avec3[7];
            }
            else if (k1 == 5)
            {
                vec33 = avec3[0];
                vec3 = avec3[3];
                vec31 = avec3[7];
                vec32 = avec3[4];
            }

            tessellator.addVertexWithUV(vec33.xCoord, vec33.yCoord, vec33.zCoord, d0, d3);
            tessellator.addVertexWithUV(vec3.xCoord, vec3.yCoord, vec3.zCoord, d2, d3);
            tessellator.addVertexWithUV(vec31.xCoord, vec31.yCoord, vec31.zCoord, d2, d1);
            tessellator.addVertexWithUV(vec32.xCoord, vec32.yCoord, vec32.zCoord, d0, d1);
        }

        float f13 = 0.09375F;
        float f8 = 0.09375F;
        float f9 = 0.03125F;
        avec3[0] = this.field_147845_a.getWorldVec3Pool().getVecFromPool((double)(-f13), 0.0D, (double)(-f8));
        avec3[1] = this.field_147845_a.getWorldVec3Pool().getVecFromPool((double)f13, 0.0D, (double)(-f8));
        avec3[2] = this.field_147845_a.getWorldVec3Pool().getVecFromPool((double)f13, 0.0D, (double)f8);
        avec3[3] = this.field_147845_a.getWorldVec3Pool().getVecFromPool((double)(-f13), 0.0D, (double)f8);
        avec3[4] = this.field_147845_a.getWorldVec3Pool().getVecFromPool((double)(-f13), (double)f9, (double)(-f8));
        avec3[5] = this.field_147845_a.getWorldVec3Pool().getVecFromPool((double)f13, (double)f9, (double)(-f8));
        avec3[6] = this.field_147845_a.getWorldVec3Pool().getVecFromPool((double)f13, (double)f9, (double)f8);
        avec3[7] = this.field_147845_a.getWorldVec3Pool().getVecFromPool((double)(-f13), (double)f9, (double)f8);

        for (int l1 = 0; l1 < 8; ++l1)
        {
            avec3[l1].zCoord += 0.21875D;

            if (flag1)
            {
                avec3[l1].yCoord -= 0.09375D;
                avec3[l1].zCoord -= 0.1625D;
                avec3[l1].rotateAroundX(0.0F);
            }
            else if (flag)
            {
                avec3[l1].yCoord += 0.015625D;
                avec3[l1].zCoord -= 0.171875D;
                avec3[l1].rotateAroundX(0.17453294F);
            }
            else
            {
                avec3[l1].rotateAroundX(0.87266463F);
            }

            if (i1 == 2)
            {
                avec3[l1].rotateAroundY(0.0F);
            }

            if (i1 == 0)
            {
                avec3[l1].rotateAroundY((float)Math.PI);
            }

            if (i1 == 1)
            {
                avec3[l1].rotateAroundY(((float)Math.PI / 2F));
            }

            if (i1 == 3)
            {
                avec3[l1].rotateAroundY(-((float)Math.PI / 2F));
            }

            avec3[l1].xCoord += (double)p_147723_2_ + 0.5D;
            avec3[l1].yCoord += (double)((float)p_147723_3_ + 0.3125F);
            avec3[l1].zCoord += (double)p_147723_4_ + 0.5D;
        }

        byte b7 = 5;
        byte b4 = 11;
        byte b5 = 3;
        byte b6 = 9;

        for (int i2 = 0; i2 < 6; ++i2)
        {
            if (i2 == 0)
            {
                vec33 = avec3[0];
                vec3 = avec3[1];
                vec31 = avec3[2];
                vec32 = avec3[3];
                d0 = (double)iicon.getInterpolatedU((double)b7);
                d1 = (double)iicon.getInterpolatedV((double)b5);
                d2 = (double)iicon.getInterpolatedU((double)b4);
                d3 = (double)iicon.getInterpolatedV((double)b6);
            }
            else if (i2 == 1)
            {
                vec33 = avec3[7];
                vec3 = avec3[6];
                vec31 = avec3[5];
                vec32 = avec3[4];
            }
            else if (i2 == 2)
            {
                vec33 = avec3[1];
                vec3 = avec3[0];
                vec31 = avec3[4];
                vec32 = avec3[5];
                d0 = (double)iicon.getInterpolatedU((double)b7);
                d1 = (double)iicon.getInterpolatedV((double)b5);
                d2 = (double)iicon.getInterpolatedU((double)b4);
                d3 = (double)iicon.getInterpolatedV((double)(b5 + 2));
            }
            else if (i2 == 3)
            {
                vec33 = avec3[2];
                vec3 = avec3[1];
                vec31 = avec3[5];
                vec32 = avec3[6];
            }
            else if (i2 == 4)
            {
                vec33 = avec3[3];
                vec3 = avec3[2];
                vec31 = avec3[6];
                vec32 = avec3[7];
            }
            else if (i2 == 5)
            {
                vec33 = avec3[0];
                vec3 = avec3[3];
                vec31 = avec3[7];
                vec32 = avec3[4];
            }

            tessellator.addVertexWithUV(vec33.xCoord, vec33.yCoord, vec33.zCoord, d0, d3);
            tessellator.addVertexWithUV(vec3.xCoord, vec3.yCoord, vec3.zCoord, d2, d3);
            tessellator.addVertexWithUV(vec31.xCoord, vec31.yCoord, vec31.zCoord, d2, d1);
            tessellator.addVertexWithUV(vec32.xCoord, vec32.yCoord, vec32.zCoord, d0, d1);
        }

        if (flag)
        {
            double d9 = avec3[0].yCoord;
            float f10 = 0.03125F;
            float f11 = 0.5F - f10 / 2.0F;
            float f12 = f11 + f10;
            double d4 = (double)iicon.getMinU();
            double d5 = (double)iicon.getInterpolatedV(flag ? 2.0D : 0.0D);
            double d6 = (double)iicon.getMaxU();
            double d7 = (double)iicon.getInterpolatedV(flag ? 4.0D : 2.0D);
            double d8 = (double)(flag2 ? 3.5F : 1.5F) / 16.0D;
            tessellator.setColorOpaque_F(0.75F, 0.75F, 0.75F);

            if (i1 == 2)
            {
                tessellator.addVertexWithUV((double)((float)p_147723_2_ + f11), (double)p_147723_3_ + d8, (double)p_147723_4_ + 0.25D, d4, d5);
                tessellator.addVertexWithUV((double)((float)p_147723_2_ + f12), (double)p_147723_3_ + d8, (double)p_147723_4_ + 0.25D, d4, d7);
                tessellator.addVertexWithUV((double)((float)p_147723_2_ + f12), (double)p_147723_3_ + d8, (double)p_147723_4_, d6, d7);
                tessellator.addVertexWithUV((double)((float)p_147723_2_ + f11), (double)p_147723_3_ + d8, (double)p_147723_4_, d6, d5);
                tessellator.addVertexWithUV((double)((float)p_147723_2_ + f11), d9, (double)p_147723_4_ + 0.5D, d4, d5);
                tessellator.addVertexWithUV((double)((float)p_147723_2_ + f12), d9, (double)p_147723_4_ + 0.5D, d4, d7);
                tessellator.addVertexWithUV((double)((float)p_147723_2_ + f12), (double)p_147723_3_ + d8, (double)p_147723_4_ + 0.25D, d6, d7);
                tessellator.addVertexWithUV((double)((float)p_147723_2_ + f11), (double)p_147723_3_ + d8, (double)p_147723_4_ + 0.25D, d6, d5);
            }
            else if (i1 == 0)
            {
                tessellator.addVertexWithUV((double)((float)p_147723_2_ + f11), (double)p_147723_3_ + d8, (double)p_147723_4_ + 0.75D, d4, d5);
                tessellator.addVertexWithUV((double)((float)p_147723_2_ + f12), (double)p_147723_3_ + d8, (double)p_147723_4_ + 0.75D, d4, d7);
                tessellator.addVertexWithUV((double)((float)p_147723_2_ + f12), d9, (double)p_147723_4_ + 0.5D, d6, d7);
                tessellator.addVertexWithUV((double)((float)p_147723_2_ + f11), d9, (double)p_147723_4_ + 0.5D, d6, d5);
                tessellator.addVertexWithUV((double)((float)p_147723_2_ + f11), (double)p_147723_3_ + d8, (double)(p_147723_4_ + 1), d4, d5);
                tessellator.addVertexWithUV((double)((float)p_147723_2_ + f12), (double)p_147723_3_ + d8, (double)(p_147723_4_ + 1), d4, d7);
                tessellator.addVertexWithUV((double)((float)p_147723_2_ + f12), (double)p_147723_3_ + d8, (double)p_147723_4_ + 0.75D, d6, d7);
                tessellator.addVertexWithUV((double)((float)p_147723_2_ + f11), (double)p_147723_3_ + d8, (double)p_147723_4_ + 0.75D, d6, d5);
            }
            else if (i1 == 1)
            {
                tessellator.addVertexWithUV((double)p_147723_2_, (double)p_147723_3_ + d8, (double)((float)p_147723_4_ + f12), d4, d7);
                tessellator.addVertexWithUV((double)p_147723_2_ + 0.25D, (double)p_147723_3_ + d8, (double)((float)p_147723_4_ + f12), d6, d7);
                tessellator.addVertexWithUV((double)p_147723_2_ + 0.25D, (double)p_147723_3_ + d8, (double)((float)p_147723_4_ + f11), d6, d5);
                tessellator.addVertexWithUV((double)p_147723_2_, (double)p_147723_3_ + d8, (double)((float)p_147723_4_ + f11), d4, d5);
                tessellator.addVertexWithUV((double)p_147723_2_ + 0.25D, (double)p_147723_3_ + d8, (double)((float)p_147723_4_ + f12), d4, d7);
                tessellator.addVertexWithUV((double)p_147723_2_ + 0.5D, d9, (double)((float)p_147723_4_ + f12), d6, d7);
                tessellator.addVertexWithUV((double)p_147723_2_ + 0.5D, d9, (double)((float)p_147723_4_ + f11), d6, d5);
                tessellator.addVertexWithUV((double)p_147723_2_ + 0.25D, (double)p_147723_3_ + d8, (double)((float)p_147723_4_ + f11), d4, d5);
            }
            else
            {
                tessellator.addVertexWithUV((double)p_147723_2_ + 0.5D, d9, (double)((float)p_147723_4_ + f12), d4, d7);
                tessellator.addVertexWithUV((double)p_147723_2_ + 0.75D, (double)p_147723_3_ + d8, (double)((float)p_147723_4_ + f12), d6, d7);
                tessellator.addVertexWithUV((double)p_147723_2_ + 0.75D, (double)p_147723_3_ + d8, (double)((float)p_147723_4_ + f11), d6, d5);
                tessellator.addVertexWithUV((double)p_147723_2_ + 0.5D, d9, (double)((float)p_147723_4_ + f11), d4, d5);
                tessellator.addVertexWithUV((double)p_147723_2_ + 0.75D, (double)p_147723_3_ + d8, (double)((float)p_147723_4_ + f12), d4, d7);
                tessellator.addVertexWithUV((double)(p_147723_2_ + 1), (double)p_147723_3_ + d8, (double)((float)p_147723_4_ + f12), d6, d7);
                tessellator.addVertexWithUV((double)(p_147723_2_ + 1), (double)p_147723_3_ + d8, (double)((float)p_147723_4_ + f11), d6, d5);
                tessellator.addVertexWithUV((double)p_147723_2_ + 0.75D, (double)p_147723_3_ + d8, (double)((float)p_147723_4_ + f11), d4, d5);
            }
        }

        return true;
    }

    public boolean func_147756_g(Block p_147756_1_, int p_147756_2_, int p_147756_3_, int p_147756_4_)
    {
        Tessellator tessellator = Tessellator.instance;
        IIcon iicon = this.func_147777_a(p_147756_1_, 0);
        int l = this.field_147845_a.getBlockMetadata(p_147756_2_, p_147756_3_, p_147756_4_);
        boolean flag = (l & 4) == 4;
        boolean flag1 = (l & 2) == 2;

        if (this.func_147744_b())
        {
            iicon = this.field_147840_d;
        }

        tessellator.setBrightness(p_147756_1_.func_149677_c(this.field_147845_a, p_147756_2_, p_147756_3_, p_147756_4_));
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        double d0 = (double)iicon.getMinU();
        double d1 = (double)iicon.getInterpolatedV(flag ? 2.0D : 0.0D);
        double d2 = (double)iicon.getMaxU();
        double d3 = (double)iicon.getInterpolatedV(flag ? 4.0D : 2.0D);
        double d4 = (double)(flag1 ? 3.5F : 1.5F) / 16.0D;
        boolean flag2 = BlockTripWire.func_150139_a(this.field_147845_a, p_147756_2_, p_147756_3_, p_147756_4_, l, 1);
        boolean flag3 = BlockTripWire.func_150139_a(this.field_147845_a, p_147756_2_, p_147756_3_, p_147756_4_, l, 3);
        boolean flag4 = BlockTripWire.func_150139_a(this.field_147845_a, p_147756_2_, p_147756_3_, p_147756_4_, l, 2);
        boolean flag5 = BlockTripWire.func_150139_a(this.field_147845_a, p_147756_2_, p_147756_3_, p_147756_4_, l, 0);
        float f = 0.03125F;
        float f1 = 0.5F - f / 2.0F;
        float f2 = f1 + f;

        if (!flag4 && !flag3 && !flag5 && !flag2)
        {
            flag4 = true;
            flag5 = true;
        }

        if (flag4)
        {
            tessellator.addVertexWithUV((double)((float)p_147756_2_ + f1), (double)p_147756_3_ + d4, (double)p_147756_4_ + 0.25D, d0, d1);
            tessellator.addVertexWithUV((double)((float)p_147756_2_ + f2), (double)p_147756_3_ + d4, (double)p_147756_4_ + 0.25D, d0, d3);
            tessellator.addVertexWithUV((double)((float)p_147756_2_ + f2), (double)p_147756_3_ + d4, (double)p_147756_4_, d2, d3);
            tessellator.addVertexWithUV((double)((float)p_147756_2_ + f1), (double)p_147756_3_ + d4, (double)p_147756_4_, d2, d1);
            tessellator.addVertexWithUV((double)((float)p_147756_2_ + f1), (double)p_147756_3_ + d4, (double)p_147756_4_, d2, d1);
            tessellator.addVertexWithUV((double)((float)p_147756_2_ + f2), (double)p_147756_3_ + d4, (double)p_147756_4_, d2, d3);
            tessellator.addVertexWithUV((double)((float)p_147756_2_ + f2), (double)p_147756_3_ + d4, (double)p_147756_4_ + 0.25D, d0, d3);
            tessellator.addVertexWithUV((double)((float)p_147756_2_ + f1), (double)p_147756_3_ + d4, (double)p_147756_4_ + 0.25D, d0, d1);
        }

        if (flag4 || flag5 && !flag3 && !flag2)
        {
            tessellator.addVertexWithUV((double)((float)p_147756_2_ + f1), (double)p_147756_3_ + d4, (double)p_147756_4_ + 0.5D, d0, d1);
            tessellator.addVertexWithUV((double)((float)p_147756_2_ + f2), (double)p_147756_3_ + d4, (double)p_147756_4_ + 0.5D, d0, d3);
            tessellator.addVertexWithUV((double)((float)p_147756_2_ + f2), (double)p_147756_3_ + d4, (double)p_147756_4_ + 0.25D, d2, d3);
            tessellator.addVertexWithUV((double)((float)p_147756_2_ + f1), (double)p_147756_3_ + d4, (double)p_147756_4_ + 0.25D, d2, d1);
            tessellator.addVertexWithUV((double)((float)p_147756_2_ + f1), (double)p_147756_3_ + d4, (double)p_147756_4_ + 0.25D, d2, d1);
            tessellator.addVertexWithUV((double)((float)p_147756_2_ + f2), (double)p_147756_3_ + d4, (double)p_147756_4_ + 0.25D, d2, d3);
            tessellator.addVertexWithUV((double)((float)p_147756_2_ + f2), (double)p_147756_3_ + d4, (double)p_147756_4_ + 0.5D, d0, d3);
            tessellator.addVertexWithUV((double)((float)p_147756_2_ + f1), (double)p_147756_3_ + d4, (double)p_147756_4_ + 0.5D, d0, d1);
        }

        if (flag5 || flag4 && !flag3 && !flag2)
        {
            tessellator.addVertexWithUV((double)((float)p_147756_2_ + f1), (double)p_147756_3_ + d4, (double)p_147756_4_ + 0.75D, d0, d1);
            tessellator.addVertexWithUV((double)((float)p_147756_2_ + f2), (double)p_147756_3_ + d4, (double)p_147756_4_ + 0.75D, d0, d3);
            tessellator.addVertexWithUV((double)((float)p_147756_2_ + f2), (double)p_147756_3_ + d4, (double)p_147756_4_ + 0.5D, d2, d3);
            tessellator.addVertexWithUV((double)((float)p_147756_2_ + f1), (double)p_147756_3_ + d4, (double)p_147756_4_ + 0.5D, d2, d1);
            tessellator.addVertexWithUV((double)((float)p_147756_2_ + f1), (double)p_147756_3_ + d4, (double)p_147756_4_ + 0.5D, d2, d1);
            tessellator.addVertexWithUV((double)((float)p_147756_2_ + f2), (double)p_147756_3_ + d4, (double)p_147756_4_ + 0.5D, d2, d3);
            tessellator.addVertexWithUV((double)((float)p_147756_2_ + f2), (double)p_147756_3_ + d4, (double)p_147756_4_ + 0.75D, d0, d3);
            tessellator.addVertexWithUV((double)((float)p_147756_2_ + f1), (double)p_147756_3_ + d4, (double)p_147756_4_ + 0.75D, d0, d1);
        }

        if (flag5)
        {
            tessellator.addVertexWithUV((double)((float)p_147756_2_ + f1), (double)p_147756_3_ + d4, (double)(p_147756_4_ + 1), d0, d1);
            tessellator.addVertexWithUV((double)((float)p_147756_2_ + f2), (double)p_147756_3_ + d4, (double)(p_147756_4_ + 1), d0, d3);
            tessellator.addVertexWithUV((double)((float)p_147756_2_ + f2), (double)p_147756_3_ + d4, (double)p_147756_4_ + 0.75D, d2, d3);
            tessellator.addVertexWithUV((double)((float)p_147756_2_ + f1), (double)p_147756_3_ + d4, (double)p_147756_4_ + 0.75D, d2, d1);
            tessellator.addVertexWithUV((double)((float)p_147756_2_ + f1), (double)p_147756_3_ + d4, (double)p_147756_4_ + 0.75D, d2, d1);
            tessellator.addVertexWithUV((double)((float)p_147756_2_ + f2), (double)p_147756_3_ + d4, (double)p_147756_4_ + 0.75D, d2, d3);
            tessellator.addVertexWithUV((double)((float)p_147756_2_ + f2), (double)p_147756_3_ + d4, (double)(p_147756_4_ + 1), d0, d3);
            tessellator.addVertexWithUV((double)((float)p_147756_2_ + f1), (double)p_147756_3_ + d4, (double)(p_147756_4_ + 1), d0, d1);
        }

        if (flag2)
        {
            tessellator.addVertexWithUV((double)p_147756_2_, (double)p_147756_3_ + d4, (double)((float)p_147756_4_ + f2), d0, d3);
            tessellator.addVertexWithUV((double)p_147756_2_ + 0.25D, (double)p_147756_3_ + d4, (double)((float)p_147756_4_ + f2), d2, d3);
            tessellator.addVertexWithUV((double)p_147756_2_ + 0.25D, (double)p_147756_3_ + d4, (double)((float)p_147756_4_ + f1), d2, d1);
            tessellator.addVertexWithUV((double)p_147756_2_, (double)p_147756_3_ + d4, (double)((float)p_147756_4_ + f1), d0, d1);
            tessellator.addVertexWithUV((double)p_147756_2_, (double)p_147756_3_ + d4, (double)((float)p_147756_4_ + f1), d0, d1);
            tessellator.addVertexWithUV((double)p_147756_2_ + 0.25D, (double)p_147756_3_ + d4, (double)((float)p_147756_4_ + f1), d2, d1);
            tessellator.addVertexWithUV((double)p_147756_2_ + 0.25D, (double)p_147756_3_ + d4, (double)((float)p_147756_4_ + f2), d2, d3);
            tessellator.addVertexWithUV((double)p_147756_2_, (double)p_147756_3_ + d4, (double)((float)p_147756_4_ + f2), d0, d3);
        }

        if (flag2 || flag3 && !flag4 && !flag5)
        {
            tessellator.addVertexWithUV((double)p_147756_2_ + 0.25D, (double)p_147756_3_ + d4, (double)((float)p_147756_4_ + f2), d0, d3);
            tessellator.addVertexWithUV((double)p_147756_2_ + 0.5D, (double)p_147756_3_ + d4, (double)((float)p_147756_4_ + f2), d2, d3);
            tessellator.addVertexWithUV((double)p_147756_2_ + 0.5D, (double)p_147756_3_ + d4, (double)((float)p_147756_4_ + f1), d2, d1);
            tessellator.addVertexWithUV((double)p_147756_2_ + 0.25D, (double)p_147756_3_ + d4, (double)((float)p_147756_4_ + f1), d0, d1);
            tessellator.addVertexWithUV((double)p_147756_2_ + 0.25D, (double)p_147756_3_ + d4, (double)((float)p_147756_4_ + f1), d0, d1);
            tessellator.addVertexWithUV((double)p_147756_2_ + 0.5D, (double)p_147756_3_ + d4, (double)((float)p_147756_4_ + f1), d2, d1);
            tessellator.addVertexWithUV((double)p_147756_2_ + 0.5D, (double)p_147756_3_ + d4, (double)((float)p_147756_4_ + f2), d2, d3);
            tessellator.addVertexWithUV((double)p_147756_2_ + 0.25D, (double)p_147756_3_ + d4, (double)((float)p_147756_4_ + f2), d0, d3);
        }

        if (flag3 || flag2 && !flag4 && !flag5)
        {
            tessellator.addVertexWithUV((double)p_147756_2_ + 0.5D, (double)p_147756_3_ + d4, (double)((float)p_147756_4_ + f2), d0, d3);
            tessellator.addVertexWithUV((double)p_147756_2_ + 0.75D, (double)p_147756_3_ + d4, (double)((float)p_147756_4_ + f2), d2, d3);
            tessellator.addVertexWithUV((double)p_147756_2_ + 0.75D, (double)p_147756_3_ + d4, (double)((float)p_147756_4_ + f1), d2, d1);
            tessellator.addVertexWithUV((double)p_147756_2_ + 0.5D, (double)p_147756_3_ + d4, (double)((float)p_147756_4_ + f1), d0, d1);
            tessellator.addVertexWithUV((double)p_147756_2_ + 0.5D, (double)p_147756_3_ + d4, (double)((float)p_147756_4_ + f1), d0, d1);
            tessellator.addVertexWithUV((double)p_147756_2_ + 0.75D, (double)p_147756_3_ + d4, (double)((float)p_147756_4_ + f1), d2, d1);
            tessellator.addVertexWithUV((double)p_147756_2_ + 0.75D, (double)p_147756_3_ + d4, (double)((float)p_147756_4_ + f2), d2, d3);
            tessellator.addVertexWithUV((double)p_147756_2_ + 0.5D, (double)p_147756_3_ + d4, (double)((float)p_147756_4_ + f2), d0, d3);
        }

        if (flag3)
        {
            tessellator.addVertexWithUV((double)p_147756_2_ + 0.75D, (double)p_147756_3_ + d4, (double)((float)p_147756_4_ + f2), d0, d3);
            tessellator.addVertexWithUV((double)(p_147756_2_ + 1), (double)p_147756_3_ + d4, (double)((float)p_147756_4_ + f2), d2, d3);
            tessellator.addVertexWithUV((double)(p_147756_2_ + 1), (double)p_147756_3_ + d4, (double)((float)p_147756_4_ + f1), d2, d1);
            tessellator.addVertexWithUV((double)p_147756_2_ + 0.75D, (double)p_147756_3_ + d4, (double)((float)p_147756_4_ + f1), d0, d1);
            tessellator.addVertexWithUV((double)p_147756_2_ + 0.75D, (double)p_147756_3_ + d4, (double)((float)p_147756_4_ + f1), d0, d1);
            tessellator.addVertexWithUV((double)(p_147756_2_ + 1), (double)p_147756_3_ + d4, (double)((float)p_147756_4_ + f1), d2, d1);
            tessellator.addVertexWithUV((double)(p_147756_2_ + 1), (double)p_147756_3_ + d4, (double)((float)p_147756_4_ + f2), d2, d3);
            tessellator.addVertexWithUV((double)p_147756_2_ + 0.75D, (double)p_147756_3_ + d4, (double)((float)p_147756_4_ + f2), d0, d3);
        }

        return true;
    }

    public boolean func_147801_a(BlockFire p_147801_1_, int p_147801_2_, int p_147801_3_, int p_147801_4_)
    {
        Tessellator tessellator = Tessellator.instance;
        IIcon iicon = p_147801_1_.func_149840_c(0);
        IIcon iicon1 = p_147801_1_.func_149840_c(1);
        IIcon iicon2 = iicon;

        if (this.func_147744_b())
        {
            iicon2 = this.field_147840_d;
        }

        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        tessellator.setBrightness(p_147801_1_.func_149677_c(this.field_147845_a, p_147801_2_, p_147801_3_, p_147801_4_));
        double d0 = (double)iicon2.getMinU();
        double d1 = (double)iicon2.getMinV();
        double d2 = (double)iicon2.getMaxU();
        double d3 = (double)iicon2.getMaxV();
        float f = 1.4F;
        double d11;
        double d5;
        double d6;
        double d7;
        double d8;
        double d9;
        double d10;

        if (!World.func_147466_a(this.field_147845_a, p_147801_2_, p_147801_3_ - 1, p_147801_4_) && !Blocks.fire.canCatchFire(this.field_147845_a, p_147801_2_, p_147801_3_ - 1, p_147801_4_, UP))
        {
            float f2 = 0.2F;
            float f1 = 0.0625F;

            if ((p_147801_2_ + p_147801_3_ + p_147801_4_ & 1) == 1)
            {
                d0 = (double)iicon1.getMinU();
                d1 = (double)iicon1.getMinV();
                d2 = (double)iicon1.getMaxU();
                d3 = (double)iicon1.getMaxV();
            }

            if ((p_147801_2_ / 2 + p_147801_3_ / 2 + p_147801_4_ / 2 & 1) == 1)
            {
                d5 = d2;
                d2 = d0;
                d0 = d5;
            }

            if (Blocks.fire.canCatchFire(this.field_147845_a, p_147801_2_ - 1, p_147801_3_, p_147801_4_, EAST))
            {
                tessellator.addVertexWithUV((double)((float)p_147801_2_ + f2), (double)((float)p_147801_3_ + f + f1), (double)(p_147801_4_ + 1), d2, d1);
                tessellator.addVertexWithUV((double)(p_147801_2_ + 0), (double)((float)(p_147801_3_ + 0) + f1), (double)(p_147801_4_ + 1), d2, d3);
                tessellator.addVertexWithUV((double)(p_147801_2_ + 0), (double)((float)(p_147801_3_ + 0) + f1), (double)(p_147801_4_ + 0), d0, d3);
                tessellator.addVertexWithUV((double)((float)p_147801_2_ + f2), (double)((float)p_147801_3_ + f + f1), (double)(p_147801_4_ + 0), d0, d1);
                tessellator.addVertexWithUV((double)((float)p_147801_2_ + f2), (double)((float)p_147801_3_ + f + f1), (double)(p_147801_4_ + 0), d0, d1);
                tessellator.addVertexWithUV((double)(p_147801_2_ + 0), (double)((float)(p_147801_3_ + 0) + f1), (double)(p_147801_4_ + 0), d0, d3);
                tessellator.addVertexWithUV((double)(p_147801_2_ + 0), (double)((float)(p_147801_3_ + 0) + f1), (double)(p_147801_4_ + 1), d2, d3);
                tessellator.addVertexWithUV((double)((float)p_147801_2_ + f2), (double)((float)p_147801_3_ + f + f1), (double)(p_147801_4_ + 1), d2, d1);
            }

            if (Blocks.fire.canCatchFire(this.field_147845_a, p_147801_2_ + 1, p_147801_3_, p_147801_4_, WEST))
            {
                tessellator.addVertexWithUV((double)((float)(p_147801_2_ + 1) - f2), (double)((float)p_147801_3_ + f + f1), (double)(p_147801_4_ + 0), d0, d1);
                tessellator.addVertexWithUV((double)(p_147801_2_ + 1 - 0), (double)((float)(p_147801_3_ + 0) + f1), (double)(p_147801_4_ + 0), d0, d3);
                tessellator.addVertexWithUV((double)(p_147801_2_ + 1 - 0), (double)((float)(p_147801_3_ + 0) + f1), (double)(p_147801_4_ + 1), d2, d3);
                tessellator.addVertexWithUV((double)((float)(p_147801_2_ + 1) - f2), (double)((float)p_147801_3_ + f + f1), (double)(p_147801_4_ + 1), d2, d1);
                tessellator.addVertexWithUV((double)((float)(p_147801_2_ + 1) - f2), (double)((float)p_147801_3_ + f + f1), (double)(p_147801_4_ + 1), d2, d1);
                tessellator.addVertexWithUV((double)(p_147801_2_ + 1 - 0), (double)((float)(p_147801_3_ + 0) + f1), (double)(p_147801_4_ + 1), d2, d3);
                tessellator.addVertexWithUV((double)(p_147801_2_ + 1 - 0), (double)((float)(p_147801_3_ + 0) + f1), (double)(p_147801_4_ + 0), d0, d3);
                tessellator.addVertexWithUV((double)((float)(p_147801_2_ + 1) - f2), (double)((float)p_147801_3_ + f + f1), (double)(p_147801_4_ + 0), d0, d1);
            }

            if (Blocks.fire.canCatchFire(this.field_147845_a, p_147801_2_, p_147801_3_, p_147801_4_ - 1, SOUTH))
            {
                tessellator.addVertexWithUV((double)(p_147801_2_ + 0), (double)((float)p_147801_3_ + f + f1), (double)((float)p_147801_4_ + f2), d2, d1);
                tessellator.addVertexWithUV((double)(p_147801_2_ + 0), (double)((float)(p_147801_3_ + 0) + f1), (double)(p_147801_4_ + 0), d2, d3);
                tessellator.addVertexWithUV((double)(p_147801_2_ + 1), (double)((float)(p_147801_3_ + 0) + f1), (double)(p_147801_4_ + 0), d0, d3);
                tessellator.addVertexWithUV((double)(p_147801_2_ + 1), (double)((float)p_147801_3_ + f + f1), (double)((float)p_147801_4_ + f2), d0, d1);
                tessellator.addVertexWithUV((double)(p_147801_2_ + 1), (double)((float)p_147801_3_ + f + f1), (double)((float)p_147801_4_ + f2), d0, d1);
                tessellator.addVertexWithUV((double)(p_147801_2_ + 1), (double)((float)(p_147801_3_ + 0) + f1), (double)(p_147801_4_ + 0), d0, d3);
                tessellator.addVertexWithUV((double)(p_147801_2_ + 0), (double)((float)(p_147801_3_ + 0) + f1), (double)(p_147801_4_ + 0), d2, d3);
                tessellator.addVertexWithUV((double)(p_147801_2_ + 0), (double)((float)p_147801_3_ + f + f1), (double)((float)p_147801_4_ + f2), d2, d1);
            }

            if (Blocks.fire.canCatchFire(this.field_147845_a, p_147801_2_, p_147801_3_, p_147801_4_ + 1, NORTH))
            {
                tessellator.addVertexWithUV((double)(p_147801_2_ + 1), (double)((float)p_147801_3_ + f + f1), (double)((float)(p_147801_4_ + 1) - f2), d0, d1);
                tessellator.addVertexWithUV((double)(p_147801_2_ + 1), (double)((float)(p_147801_3_ + 0) + f1), (double)(p_147801_4_ + 1 - 0), d0, d3);
                tessellator.addVertexWithUV((double)(p_147801_2_ + 0), (double)((float)(p_147801_3_ + 0) + f1), (double)(p_147801_4_ + 1 - 0), d2, d3);
                tessellator.addVertexWithUV((double)(p_147801_2_ + 0), (double)((float)p_147801_3_ + f + f1), (double)((float)(p_147801_4_ + 1) - f2), d2, d1);
                tessellator.addVertexWithUV((double)(p_147801_2_ + 0), (double)((float)p_147801_3_ + f + f1), (double)((float)(p_147801_4_ + 1) - f2), d2, d1);
                tessellator.addVertexWithUV((double)(p_147801_2_ + 0), (double)((float)(p_147801_3_ + 0) + f1), (double)(p_147801_4_ + 1 - 0), d2, d3);
                tessellator.addVertexWithUV((double)(p_147801_2_ + 1), (double)((float)(p_147801_3_ + 0) + f1), (double)(p_147801_4_ + 1 - 0), d0, d3);
                tessellator.addVertexWithUV((double)(p_147801_2_ + 1), (double)((float)p_147801_3_ + f + f1), (double)((float)(p_147801_4_ + 1) - f2), d0, d1);
            }

            if (Blocks.fire.canCatchFire(this.field_147845_a, p_147801_2_, p_147801_3_ + 1, p_147801_4_, DOWN))
            {
                d5 = (double)p_147801_2_ + 0.5D + 0.5D;
                d6 = (double)p_147801_2_ + 0.5D - 0.5D;
                d7 = (double)p_147801_4_ + 0.5D + 0.5D;
                d8 = (double)p_147801_4_ + 0.5D - 0.5D;
                d9 = (double)p_147801_2_ + 0.5D - 0.5D;
                d10 = (double)p_147801_2_ + 0.5D + 0.5D;
                d11 = (double)p_147801_4_ + 0.5D - 0.5D;
                double d12 = (double)p_147801_4_ + 0.5D + 0.5D;
                d0 = (double)iicon.getMinU();
                d1 = (double)iicon.getMinV();
                d2 = (double)iicon.getMaxU();
                d3 = (double)iicon.getMaxV();
                ++p_147801_3_;
                f = -0.2F;

                if ((p_147801_2_ + p_147801_3_ + p_147801_4_ & 1) == 0)
                {
                    tessellator.addVertexWithUV(d9, (double)((float)p_147801_3_ + f), (double)(p_147801_4_ + 0), d2, d1);
                    tessellator.addVertexWithUV(d5, (double)(p_147801_3_ + 0), (double)(p_147801_4_ + 0), d2, d3);
                    tessellator.addVertexWithUV(d5, (double)(p_147801_3_ + 0), (double)(p_147801_4_ + 1), d0, d3);
                    tessellator.addVertexWithUV(d9, (double)((float)p_147801_3_ + f), (double)(p_147801_4_ + 1), d0, d1);
                    d0 = (double)iicon1.getMinU();
                    d1 = (double)iicon1.getMinV();
                    d2 = (double)iicon1.getMaxU();
                    d3 = (double)iicon1.getMaxV();
                    tessellator.addVertexWithUV(d10, (double)((float)p_147801_3_ + f), (double)(p_147801_4_ + 1), d2, d1);
                    tessellator.addVertexWithUV(d6, (double)(p_147801_3_ + 0), (double)(p_147801_4_ + 1), d2, d3);
                    tessellator.addVertexWithUV(d6, (double)(p_147801_3_ + 0), (double)(p_147801_4_ + 0), d0, d3);
                    tessellator.addVertexWithUV(d10, (double)((float)p_147801_3_ + f), (double)(p_147801_4_ + 0), d0, d1);
                }
                else
                {
                    tessellator.addVertexWithUV((double)(p_147801_2_ + 0), (double)((float)p_147801_3_ + f), d12, d2, d1);
                    tessellator.addVertexWithUV((double)(p_147801_2_ + 0), (double)(p_147801_3_ + 0), d8, d2, d3);
                    tessellator.addVertexWithUV((double)(p_147801_2_ + 1), (double)(p_147801_3_ + 0), d8, d0, d3);
                    tessellator.addVertexWithUV((double)(p_147801_2_ + 1), (double)((float)p_147801_3_ + f), d12, d0, d1);
                    d0 = (double)iicon1.getMinU();
                    d1 = (double)iicon1.getMinV();
                    d2 = (double)iicon1.getMaxU();
                    d3 = (double)iicon1.getMaxV();
                    tessellator.addVertexWithUV((double)(p_147801_2_ + 1), (double)((float)p_147801_3_ + f), d11, d2, d1);
                    tessellator.addVertexWithUV((double)(p_147801_2_ + 1), (double)(p_147801_3_ + 0), d7, d2, d3);
                    tessellator.addVertexWithUV((double)(p_147801_2_ + 0), (double)(p_147801_3_ + 0), d7, d0, d3);
                    tessellator.addVertexWithUV((double)(p_147801_2_ + 0), (double)((float)p_147801_3_ + f), d11, d0, d1);
                }
            }
        }
        else
        {
            double d4 = (double)p_147801_2_ + 0.5D + 0.2D;
            d5 = (double)p_147801_2_ + 0.5D - 0.2D;
            d6 = (double)p_147801_4_ + 0.5D + 0.2D;
            d7 = (double)p_147801_4_ + 0.5D - 0.2D;
            d8 = (double)p_147801_2_ + 0.5D - 0.3D;
            d9 = (double)p_147801_2_ + 0.5D + 0.3D;
            d10 = (double)p_147801_4_ + 0.5D - 0.3D;
            d11 = (double)p_147801_4_ + 0.5D + 0.3D;
            tessellator.addVertexWithUV(d8, (double)((float)p_147801_3_ + f), (double)(p_147801_4_ + 1), d2, d1);
            tessellator.addVertexWithUV(d4, (double)(p_147801_3_ + 0), (double)(p_147801_4_ + 1), d2, d3);
            tessellator.addVertexWithUV(d4, (double)(p_147801_3_ + 0), (double)(p_147801_4_ + 0), d0, d3);
            tessellator.addVertexWithUV(d8, (double)((float)p_147801_3_ + f), (double)(p_147801_4_ + 0), d0, d1);
            tessellator.addVertexWithUV(d9, (double)((float)p_147801_3_ + f), (double)(p_147801_4_ + 0), d2, d1);
            tessellator.addVertexWithUV(d5, (double)(p_147801_3_ + 0), (double)(p_147801_4_ + 0), d2, d3);
            tessellator.addVertexWithUV(d5, (double)(p_147801_3_ + 0), (double)(p_147801_4_ + 1), d0, d3);
            tessellator.addVertexWithUV(d9, (double)((float)p_147801_3_ + f), (double)(p_147801_4_ + 1), d0, d1);
            d0 = (double)iicon1.getMinU();
            d1 = (double)iicon1.getMinV();
            d2 = (double)iicon1.getMaxU();
            d3 = (double)iicon1.getMaxV();
            tessellator.addVertexWithUV((double)(p_147801_2_ + 1), (double)((float)p_147801_3_ + f), d11, d2, d1);
            tessellator.addVertexWithUV((double)(p_147801_2_ + 1), (double)(p_147801_3_ + 0), d7, d2, d3);
            tessellator.addVertexWithUV((double)(p_147801_2_ + 0), (double)(p_147801_3_ + 0), d7, d0, d3);
            tessellator.addVertexWithUV((double)(p_147801_2_ + 0), (double)((float)p_147801_3_ + f), d11, d0, d1);
            tessellator.addVertexWithUV((double)(p_147801_2_ + 0), (double)((float)p_147801_3_ + f), d10, d2, d1);
            tessellator.addVertexWithUV((double)(p_147801_2_ + 0), (double)(p_147801_3_ + 0), d6, d2, d3);
            tessellator.addVertexWithUV((double)(p_147801_2_ + 1), (double)(p_147801_3_ + 0), d6, d0, d3);
            tessellator.addVertexWithUV((double)(p_147801_2_ + 1), (double)((float)p_147801_3_ + f), d10, d0, d1);
            d4 = (double)p_147801_2_ + 0.5D - 0.5D;
            d5 = (double)p_147801_2_ + 0.5D + 0.5D;
            d6 = (double)p_147801_4_ + 0.5D - 0.5D;
            d7 = (double)p_147801_4_ + 0.5D + 0.5D;
            d8 = (double)p_147801_2_ + 0.5D - 0.4D;
            d9 = (double)p_147801_2_ + 0.5D + 0.4D;
            d10 = (double)p_147801_4_ + 0.5D - 0.4D;
            d11 = (double)p_147801_4_ + 0.5D + 0.4D;
            tessellator.addVertexWithUV(d8, (double)((float)p_147801_3_ + f), (double)(p_147801_4_ + 0), d0, d1);
            tessellator.addVertexWithUV(d4, (double)(p_147801_3_ + 0), (double)(p_147801_4_ + 0), d0, d3);
            tessellator.addVertexWithUV(d4, (double)(p_147801_3_ + 0), (double)(p_147801_4_ + 1), d2, d3);
            tessellator.addVertexWithUV(d8, (double)((float)p_147801_3_ + f), (double)(p_147801_4_ + 1), d2, d1);
            tessellator.addVertexWithUV(d9, (double)((float)p_147801_3_ + f), (double)(p_147801_4_ + 1), d0, d1);
            tessellator.addVertexWithUV(d5, (double)(p_147801_3_ + 0), (double)(p_147801_4_ + 1), d0, d3);
            tessellator.addVertexWithUV(d5, (double)(p_147801_3_ + 0), (double)(p_147801_4_ + 0), d2, d3);
            tessellator.addVertexWithUV(d9, (double)((float)p_147801_3_ + f), (double)(p_147801_4_ + 0), d2, d1);
            d0 = (double)iicon.getMinU();
            d1 = (double)iicon.getMinV();
            d2 = (double)iicon.getMaxU();
            d3 = (double)iicon.getMaxV();
            tessellator.addVertexWithUV((double)(p_147801_2_ + 0), (double)((float)p_147801_3_ + f), d11, d0, d1);
            tessellator.addVertexWithUV((double)(p_147801_2_ + 0), (double)(p_147801_3_ + 0), d7, d0, d3);
            tessellator.addVertexWithUV((double)(p_147801_2_ + 1), (double)(p_147801_3_ + 0), d7, d2, d3);
            tessellator.addVertexWithUV((double)(p_147801_2_ + 1), (double)((float)p_147801_3_ + f), d11, d2, d1);
            tessellator.addVertexWithUV((double)(p_147801_2_ + 1), (double)((float)p_147801_3_ + f), d10, d0, d1);
            tessellator.addVertexWithUV((double)(p_147801_2_ + 1), (double)(p_147801_3_ + 0), d6, d0, d3);
            tessellator.addVertexWithUV((double)(p_147801_2_ + 0), (double)(p_147801_3_ + 0), d6, d2, d3);
            tessellator.addVertexWithUV((double)(p_147801_2_ + 0), (double)((float)p_147801_3_ + f), d10, d2, d1);
        }

        return true;
    }

    public boolean func_147788_h(Block p_147788_1_, int p_147788_2_, int p_147788_3_, int p_147788_4_)
    {
        Tessellator tessellator = Tessellator.instance;
        int l = this.field_147845_a.getBlockMetadata(p_147788_2_, p_147788_3_, p_147788_4_);
        IIcon iicon = BlockRedstoneWire.func_150173_e("cross");
        IIcon iicon1 = BlockRedstoneWire.func_150173_e("line");
        IIcon iicon2 = BlockRedstoneWire.func_150173_e("cross_overlay");
        IIcon iicon3 = BlockRedstoneWire.func_150173_e("line_overlay");
        tessellator.setBrightness(p_147788_1_.func_149677_c(this.field_147845_a, p_147788_2_, p_147788_3_, p_147788_4_));
        float f = (float)l / 15.0F;
        float f1 = f * 0.6F + 0.4F;

        if (l == 0)
        {
            f1 = 0.3F;
        }

        float f2 = f * f * 0.7F - 0.5F;
        float f3 = f * f * 0.6F - 0.7F;

        if (f2 < 0.0F)
        {
            f2 = 0.0F;
        }

        if (f3 < 0.0F)
        {
            f3 = 0.0F;
        }

        tessellator.setColorOpaque_F(f1, f2, f3);
        double d0 = 0.015625D;
        double d1 = 0.015625D;
        boolean flag = BlockRedstoneWire.func_150174_f(this.field_147845_a, p_147788_2_ - 1, p_147788_3_, p_147788_4_, 1) || !this.field_147845_a.func_147439_a(p_147788_2_ - 1, p_147788_3_, p_147788_4_).func_149637_q() && BlockRedstoneWire.func_150174_f(this.field_147845_a, p_147788_2_ - 1, p_147788_3_ - 1, p_147788_4_, -1);
        boolean flag1 = BlockRedstoneWire.func_150174_f(this.field_147845_a, p_147788_2_ + 1, p_147788_3_, p_147788_4_, 3) || !this.field_147845_a.func_147439_a(p_147788_2_ + 1, p_147788_3_, p_147788_4_).func_149637_q() && BlockRedstoneWire.func_150174_f(this.field_147845_a, p_147788_2_ + 1, p_147788_3_ - 1, p_147788_4_, -1);
        boolean flag2 = BlockRedstoneWire.func_150174_f(this.field_147845_a, p_147788_2_, p_147788_3_, p_147788_4_ - 1, 2) || !this.field_147845_a.func_147439_a(p_147788_2_, p_147788_3_, p_147788_4_ - 1).func_149637_q() && BlockRedstoneWire.func_150174_f(this.field_147845_a, p_147788_2_, p_147788_3_ - 1, p_147788_4_ - 1, -1);
        boolean flag3 = BlockRedstoneWire.func_150174_f(this.field_147845_a, p_147788_2_, p_147788_3_, p_147788_4_ + 1, 0) || !this.field_147845_a.func_147439_a(p_147788_2_, p_147788_3_, p_147788_4_ + 1).func_149637_q() && BlockRedstoneWire.func_150174_f(this.field_147845_a, p_147788_2_, p_147788_3_ - 1, p_147788_4_ + 1, -1);

        if (!this.field_147845_a.func_147439_a(p_147788_2_, p_147788_3_ + 1, p_147788_4_).func_149637_q())
        {
            if (this.field_147845_a.func_147439_a(p_147788_2_ - 1, p_147788_3_, p_147788_4_).func_149637_q() && BlockRedstoneWire.func_150174_f(this.field_147845_a, p_147788_2_ - 1, p_147788_3_ + 1, p_147788_4_, -1))
            {
                flag = true;
            }

            if (this.field_147845_a.func_147439_a(p_147788_2_ + 1, p_147788_3_, p_147788_4_).func_149637_q() && BlockRedstoneWire.func_150174_f(this.field_147845_a, p_147788_2_ + 1, p_147788_3_ + 1, p_147788_4_, -1))
            {
                flag1 = true;
            }

            if (this.field_147845_a.func_147439_a(p_147788_2_, p_147788_3_, p_147788_4_ - 1).func_149637_q() && BlockRedstoneWire.func_150174_f(this.field_147845_a, p_147788_2_, p_147788_3_ + 1, p_147788_4_ - 1, -1))
            {
                flag2 = true;
            }

            if (this.field_147845_a.func_147439_a(p_147788_2_, p_147788_3_, p_147788_4_ + 1).func_149637_q() && BlockRedstoneWire.func_150174_f(this.field_147845_a, p_147788_2_, p_147788_3_ + 1, p_147788_4_ + 1, -1))
            {
                flag3 = true;
            }
        }

        float f4 = (float)(p_147788_2_ + 0);
        float f5 = (float)(p_147788_2_ + 1);
        float f6 = (float)(p_147788_4_ + 0);
        float f7 = (float)(p_147788_4_ + 1);
        int i1 = 0;

        if ((flag || flag1) && !flag2 && !flag3)
        {
            i1 = 1;
        }

        if ((flag2 || flag3) && !flag1 && !flag)
        {
            i1 = 2;
        }

        if (i1 == 0)
        {
            int j1 = 0;
            int k1 = 0;
            int l1 = 16;
            int i2 = 16;
            boolean flag4 = true;

            if (!flag)
            {
                f4 += 0.3125F;
            }

            if (!flag)
            {
                j1 += 5;
            }

            if (!flag1)
            {
                f5 -= 0.3125F;
            }

            if (!flag1)
            {
                l1 -= 5;
            }

            if (!flag2)
            {
                f6 += 0.3125F;
            }

            if (!flag2)
            {
                k1 += 5;
            }

            if (!flag3)
            {
                f7 -= 0.3125F;
            }

            if (!flag3)
            {
                i2 -= 5;
            }

            tessellator.addVertexWithUV((double)f5, (double)p_147788_3_ + 0.015625D, (double)f7, (double)iicon.getInterpolatedU((double)l1), (double)iicon.getInterpolatedV((double)i2));
            tessellator.addVertexWithUV((double)f5, (double)p_147788_3_ + 0.015625D, (double)f6, (double)iicon.getInterpolatedU((double)l1), (double)iicon.getInterpolatedV((double)k1));
            tessellator.addVertexWithUV((double)f4, (double)p_147788_3_ + 0.015625D, (double)f6, (double)iicon.getInterpolatedU((double)j1), (double)iicon.getInterpolatedV((double)k1));
            tessellator.addVertexWithUV((double)f4, (double)p_147788_3_ + 0.015625D, (double)f7, (double)iicon.getInterpolatedU((double)j1), (double)iicon.getInterpolatedV((double)i2));
            tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
            tessellator.addVertexWithUV((double)f5, (double)p_147788_3_ + 0.015625D, (double)f7, (double)iicon2.getInterpolatedU((double)l1), (double)iicon2.getInterpolatedV((double)i2));
            tessellator.addVertexWithUV((double)f5, (double)p_147788_3_ + 0.015625D, (double)f6, (double)iicon2.getInterpolatedU((double)l1), (double)iicon2.getInterpolatedV((double)k1));
            tessellator.addVertexWithUV((double)f4, (double)p_147788_3_ + 0.015625D, (double)f6, (double)iicon2.getInterpolatedU((double)j1), (double)iicon2.getInterpolatedV((double)k1));
            tessellator.addVertexWithUV((double)f4, (double)p_147788_3_ + 0.015625D, (double)f7, (double)iicon2.getInterpolatedU((double)j1), (double)iicon2.getInterpolatedV((double)i2));
        }
        else if (i1 == 1)
        {
            tessellator.addVertexWithUV((double)f5, (double)p_147788_3_ + 0.015625D, (double)f7, (double)iicon1.getMaxU(), (double)iicon1.getMaxV());
            tessellator.addVertexWithUV((double)f5, (double)p_147788_3_ + 0.015625D, (double)f6, (double)iicon1.getMaxU(), (double)iicon1.getMinV());
            tessellator.addVertexWithUV((double)f4, (double)p_147788_3_ + 0.015625D, (double)f6, (double)iicon1.getMinU(), (double)iicon1.getMinV());
            tessellator.addVertexWithUV((double)f4, (double)p_147788_3_ + 0.015625D, (double)f7, (double)iicon1.getMinU(), (double)iicon1.getMaxV());
            tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
            tessellator.addVertexWithUV((double)f5, (double)p_147788_3_ + 0.015625D, (double)f7, (double)iicon3.getMaxU(), (double)iicon3.getMaxV());
            tessellator.addVertexWithUV((double)f5, (double)p_147788_3_ + 0.015625D, (double)f6, (double)iicon3.getMaxU(), (double)iicon3.getMinV());
            tessellator.addVertexWithUV((double)f4, (double)p_147788_3_ + 0.015625D, (double)f6, (double)iicon3.getMinU(), (double)iicon3.getMinV());
            tessellator.addVertexWithUV((double)f4, (double)p_147788_3_ + 0.015625D, (double)f7, (double)iicon3.getMinU(), (double)iicon3.getMaxV());
        }
        else
        {
            tessellator.addVertexWithUV((double)f5, (double)p_147788_3_ + 0.015625D, (double)f7, (double)iicon1.getMaxU(), (double)iicon1.getMaxV());
            tessellator.addVertexWithUV((double)f5, (double)p_147788_3_ + 0.015625D, (double)f6, (double)iicon1.getMinU(), (double)iicon1.getMaxV());
            tessellator.addVertexWithUV((double)f4, (double)p_147788_3_ + 0.015625D, (double)f6, (double)iicon1.getMinU(), (double)iicon1.getMinV());
            tessellator.addVertexWithUV((double)f4, (double)p_147788_3_ + 0.015625D, (double)f7, (double)iicon1.getMaxU(), (double)iicon1.getMinV());
            tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
            tessellator.addVertexWithUV((double)f5, (double)p_147788_3_ + 0.015625D, (double)f7, (double)iicon3.getMaxU(), (double)iicon3.getMaxV());
            tessellator.addVertexWithUV((double)f5, (double)p_147788_3_ + 0.015625D, (double)f6, (double)iicon3.getMinU(), (double)iicon3.getMaxV());
            tessellator.addVertexWithUV((double)f4, (double)p_147788_3_ + 0.015625D, (double)f6, (double)iicon3.getMinU(), (double)iicon3.getMinV());
            tessellator.addVertexWithUV((double)f4, (double)p_147788_3_ + 0.015625D, (double)f7, (double)iicon3.getMaxU(), (double)iicon3.getMinV());
        }

        if (!this.field_147845_a.func_147439_a(p_147788_2_, p_147788_3_ + 1, p_147788_4_).func_149637_q())
        {
            float f8 = 0.021875F;

            if (this.field_147845_a.func_147439_a(p_147788_2_ - 1, p_147788_3_, p_147788_4_).func_149637_q() && this.field_147845_a.func_147439_a(p_147788_2_ - 1, p_147788_3_ + 1, p_147788_4_) == Blocks.redstone_wire)
            {
                tessellator.setColorOpaque_F(f1, f2, f3);
                tessellator.addVertexWithUV((double)p_147788_2_ + 0.015625D, (double)((float)(p_147788_3_ + 1) + 0.021875F), (double)(p_147788_4_ + 1), (double)iicon1.getMaxU(), (double)iicon1.getMinV());
                tessellator.addVertexWithUV((double)p_147788_2_ + 0.015625D, (double)(p_147788_3_ + 0), (double)(p_147788_4_ + 1), (double)iicon1.getMinU(), (double)iicon1.getMinV());
                tessellator.addVertexWithUV((double)p_147788_2_ + 0.015625D, (double)(p_147788_3_ + 0), (double)(p_147788_4_ + 0), (double)iicon1.getMinU(), (double)iicon1.getMaxV());
                tessellator.addVertexWithUV((double)p_147788_2_ + 0.015625D, (double)((float)(p_147788_3_ + 1) + 0.021875F), (double)(p_147788_4_ + 0), (double)iicon1.getMaxU(), (double)iicon1.getMaxV());
                tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
                tessellator.addVertexWithUV((double)p_147788_2_ + 0.015625D, (double)((float)(p_147788_3_ + 1) + 0.021875F), (double)(p_147788_4_ + 1), (double)iicon3.getMaxU(), (double)iicon3.getMinV());
                tessellator.addVertexWithUV((double)p_147788_2_ + 0.015625D, (double)(p_147788_3_ + 0), (double)(p_147788_4_ + 1), (double)iicon3.getMinU(), (double)iicon3.getMinV());
                tessellator.addVertexWithUV((double)p_147788_2_ + 0.015625D, (double)(p_147788_3_ + 0), (double)(p_147788_4_ + 0), (double)iicon3.getMinU(), (double)iicon3.getMaxV());
                tessellator.addVertexWithUV((double)p_147788_2_ + 0.015625D, (double)((float)(p_147788_3_ + 1) + 0.021875F), (double)(p_147788_4_ + 0), (double)iicon3.getMaxU(), (double)iicon3.getMaxV());
            }

            if (this.field_147845_a.func_147439_a(p_147788_2_ + 1, p_147788_3_, p_147788_4_).func_149637_q() && this.field_147845_a.func_147439_a(p_147788_2_ + 1, p_147788_3_ + 1, p_147788_4_) == Blocks.redstone_wire)
            {
                tessellator.setColorOpaque_F(f1, f2, f3);
                tessellator.addVertexWithUV((double)(p_147788_2_ + 1) - 0.015625D, (double)(p_147788_3_ + 0), (double)(p_147788_4_ + 1), (double)iicon1.getMinU(), (double)iicon1.getMaxV());
                tessellator.addVertexWithUV((double)(p_147788_2_ + 1) - 0.015625D, (double)((float)(p_147788_3_ + 1) + 0.021875F), (double)(p_147788_4_ + 1), (double)iicon1.getMaxU(), (double)iicon1.getMaxV());
                tessellator.addVertexWithUV((double)(p_147788_2_ + 1) - 0.015625D, (double)((float)(p_147788_3_ + 1) + 0.021875F), (double)(p_147788_4_ + 0), (double)iicon1.getMaxU(), (double)iicon1.getMinV());
                tessellator.addVertexWithUV((double)(p_147788_2_ + 1) - 0.015625D, (double)(p_147788_3_ + 0), (double)(p_147788_4_ + 0), (double)iicon1.getMinU(), (double)iicon1.getMinV());
                tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
                tessellator.addVertexWithUV((double)(p_147788_2_ + 1) - 0.015625D, (double)(p_147788_3_ + 0), (double)(p_147788_4_ + 1), (double)iicon3.getMinU(), (double)iicon3.getMaxV());
                tessellator.addVertexWithUV((double)(p_147788_2_ + 1) - 0.015625D, (double)((float)(p_147788_3_ + 1) + 0.021875F), (double)(p_147788_4_ + 1), (double)iicon3.getMaxU(), (double)iicon3.getMaxV());
                tessellator.addVertexWithUV((double)(p_147788_2_ + 1) - 0.015625D, (double)((float)(p_147788_3_ + 1) + 0.021875F), (double)(p_147788_4_ + 0), (double)iicon3.getMaxU(), (double)iicon3.getMinV());
                tessellator.addVertexWithUV((double)(p_147788_2_ + 1) - 0.015625D, (double)(p_147788_3_ + 0), (double)(p_147788_4_ + 0), (double)iicon3.getMinU(), (double)iicon3.getMinV());
            }

            if (this.field_147845_a.func_147439_a(p_147788_2_, p_147788_3_, p_147788_4_ - 1).func_149637_q() && this.field_147845_a.func_147439_a(p_147788_2_, p_147788_3_ + 1, p_147788_4_ - 1) == Blocks.redstone_wire)
            {
                tessellator.setColorOpaque_F(f1, f2, f3);
                tessellator.addVertexWithUV((double)(p_147788_2_ + 1), (double)(p_147788_3_ + 0), (double)p_147788_4_ + 0.015625D, (double)iicon1.getMinU(), (double)iicon1.getMaxV());
                tessellator.addVertexWithUV((double)(p_147788_2_ + 1), (double)((float)(p_147788_3_ + 1) + 0.021875F), (double)p_147788_4_ + 0.015625D, (double)iicon1.getMaxU(), (double)iicon1.getMaxV());
                tessellator.addVertexWithUV((double)(p_147788_2_ + 0), (double)((float)(p_147788_3_ + 1) + 0.021875F), (double)p_147788_4_ + 0.015625D, (double)iicon1.getMaxU(), (double)iicon1.getMinV());
                tessellator.addVertexWithUV((double)(p_147788_2_ + 0), (double)(p_147788_3_ + 0), (double)p_147788_4_ + 0.015625D, (double)iicon1.getMinU(), (double)iicon1.getMinV());
                tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
                tessellator.addVertexWithUV((double)(p_147788_2_ + 1), (double)(p_147788_3_ + 0), (double)p_147788_4_ + 0.015625D, (double)iicon3.getMinU(), (double)iicon3.getMaxV());
                tessellator.addVertexWithUV((double)(p_147788_2_ + 1), (double)((float)(p_147788_3_ + 1) + 0.021875F), (double)p_147788_4_ + 0.015625D, (double)iicon3.getMaxU(), (double)iicon3.getMaxV());
                tessellator.addVertexWithUV((double)(p_147788_2_ + 0), (double)((float)(p_147788_3_ + 1) + 0.021875F), (double)p_147788_4_ + 0.015625D, (double)iicon3.getMaxU(), (double)iicon3.getMinV());
                tessellator.addVertexWithUV((double)(p_147788_2_ + 0), (double)(p_147788_3_ + 0), (double)p_147788_4_ + 0.015625D, (double)iicon3.getMinU(), (double)iicon3.getMinV());
            }

            if (this.field_147845_a.func_147439_a(p_147788_2_, p_147788_3_, p_147788_4_ + 1).func_149637_q() && this.field_147845_a.func_147439_a(p_147788_2_, p_147788_3_ + 1, p_147788_4_ + 1) == Blocks.redstone_wire)
            {
                tessellator.setColorOpaque_F(f1, f2, f3);
                tessellator.addVertexWithUV((double)(p_147788_2_ + 1), (double)((float)(p_147788_3_ + 1) + 0.021875F), (double)(p_147788_4_ + 1) - 0.015625D, (double)iicon1.getMaxU(), (double)iicon1.getMinV());
                tessellator.addVertexWithUV((double)(p_147788_2_ + 1), (double)(p_147788_3_ + 0), (double)(p_147788_4_ + 1) - 0.015625D, (double)iicon1.getMinU(), (double)iicon1.getMinV());
                tessellator.addVertexWithUV((double)(p_147788_2_ + 0), (double)(p_147788_3_ + 0), (double)(p_147788_4_ + 1) - 0.015625D, (double)iicon1.getMinU(), (double)iicon1.getMaxV());
                tessellator.addVertexWithUV((double)(p_147788_2_ + 0), (double)((float)(p_147788_3_ + 1) + 0.021875F), (double)(p_147788_4_ + 1) - 0.015625D, (double)iicon1.getMaxU(), (double)iicon1.getMaxV());
                tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
                tessellator.addVertexWithUV((double)(p_147788_2_ + 1), (double)((float)(p_147788_3_ + 1) + 0.021875F), (double)(p_147788_4_ + 1) - 0.015625D, (double)iicon3.getMaxU(), (double)iicon3.getMinV());
                tessellator.addVertexWithUV((double)(p_147788_2_ + 1), (double)(p_147788_3_ + 0), (double)(p_147788_4_ + 1) - 0.015625D, (double)iicon3.getMinU(), (double)iicon3.getMinV());
                tessellator.addVertexWithUV((double)(p_147788_2_ + 0), (double)(p_147788_3_ + 0), (double)(p_147788_4_ + 1) - 0.015625D, (double)iicon3.getMinU(), (double)iicon3.getMaxV());
                tessellator.addVertexWithUV((double)(p_147788_2_ + 0), (double)((float)(p_147788_3_ + 1) + 0.021875F), (double)(p_147788_4_ + 1) - 0.015625D, (double)iicon3.getMaxU(), (double)iicon3.getMaxV());
            }
        }

        return true;
    }

    public boolean func_147766_a(BlockRailBase p_147766_1_, int p_147766_2_, int p_147766_3_, int p_147766_4_)
    {
        Tessellator tessellator = Tessellator.instance;
        int l = this.field_147845_a.getBlockMetadata(p_147766_2_, p_147766_3_, p_147766_4_);
        IIcon iicon = this.func_147787_a(p_147766_1_, 0, l);

        if (this.func_147744_b())
        {
            iicon = this.field_147840_d;
        }

        if (p_147766_1_.func_150050_e())
        {
            l &= 7;
        }

        tessellator.setBrightness(p_147766_1_.func_149677_c(this.field_147845_a, p_147766_2_, p_147766_3_, p_147766_4_));
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        double d0 = (double)iicon.getMinU();
        double d1 = (double)iicon.getMinV();
        double d2 = (double)iicon.getMaxU();
        double d3 = (double)iicon.getMaxV();
        double d4 = 0.0625D;
        double d5 = (double)(p_147766_2_ + 1);
        double d6 = (double)(p_147766_2_ + 1);
        double d7 = (double)(p_147766_2_ + 0);
        double d8 = (double)(p_147766_2_ + 0);
        double d9 = (double)(p_147766_4_ + 0);
        double d10 = (double)(p_147766_4_ + 1);
        double d11 = (double)(p_147766_4_ + 1);
        double d12 = (double)(p_147766_4_ + 0);
        double d13 = (double)p_147766_3_ + d4;
        double d14 = (double)p_147766_3_ + d4;
        double d15 = (double)p_147766_3_ + d4;
        double d16 = (double)p_147766_3_ + d4;

        if (l != 1 && l != 2 && l != 3 && l != 7)
        {
            if (l == 8)
            {
                d5 = d6 = (double)(p_147766_2_ + 0);
                d7 = d8 = (double)(p_147766_2_ + 1);
                d9 = d12 = (double)(p_147766_4_ + 1);
                d10 = d11 = (double)(p_147766_4_ + 0);
            }
            else if (l == 9)
            {
                d5 = d8 = (double)(p_147766_2_ + 0);
                d6 = d7 = (double)(p_147766_2_ + 1);
                d9 = d10 = (double)(p_147766_4_ + 0);
                d11 = d12 = (double)(p_147766_4_ + 1);
            }
        }
        else
        {
            d5 = d8 = (double)(p_147766_2_ + 1);
            d6 = d7 = (double)(p_147766_2_ + 0);
            d9 = d10 = (double)(p_147766_4_ + 1);
            d11 = d12 = (double)(p_147766_4_ + 0);
        }

        if (l != 2 && l != 4)
        {
            if (l == 3 || l == 5)
            {
                ++d14;
                ++d15;
            }
        }
        else
        {
            ++d13;
            ++d16;
        }

        tessellator.addVertexWithUV(d5, d13, d9, d2, d1);
        tessellator.addVertexWithUV(d6, d14, d10, d2, d3);
        tessellator.addVertexWithUV(d7, d15, d11, d0, d3);
        tessellator.addVertexWithUV(d8, d16, d12, d0, d1);
        tessellator.addVertexWithUV(d8, d16, d12, d0, d1);
        tessellator.addVertexWithUV(d7, d15, d11, d0, d3);
        tessellator.addVertexWithUV(d6, d14, d10, d2, d3);
        tessellator.addVertexWithUV(d5, d13, d9, d2, d1);
        return true;
    }

    public boolean func_147794_i(Block p_147794_1_, int p_147794_2_, int p_147794_3_, int p_147794_4_)
    {
        Tessellator tessellator = Tessellator.instance;
        IIcon iicon = this.func_147777_a(p_147794_1_, 0);

        if (this.func_147744_b())
        {
            iicon = this.field_147840_d;
        }

        tessellator.setBrightness(p_147794_1_.func_149677_c(this.field_147845_a, p_147794_2_, p_147794_3_, p_147794_4_));
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        double d0 = (double)iicon.getMinU();
        double d1 = (double)iicon.getMinV();
        double d2 = (double)iicon.getMaxU();
        double d3 = (double)iicon.getMaxV();
        int l = this.field_147845_a.getBlockMetadata(p_147794_2_, p_147794_3_, p_147794_4_);
        double d4 = 0.0D;
        double d5 = 0.05000000074505806D;

        if (l == 5)
        {
            tessellator.addVertexWithUV((double)p_147794_2_ + d5, (double)(p_147794_3_ + 1) + d4, (double)(p_147794_4_ + 1) + d4, d0, d1);
            tessellator.addVertexWithUV((double)p_147794_2_ + d5, (double)(p_147794_3_ + 0) - d4, (double)(p_147794_4_ + 1) + d4, d0, d3);
            tessellator.addVertexWithUV((double)p_147794_2_ + d5, (double)(p_147794_3_ + 0) - d4, (double)(p_147794_4_ + 0) - d4, d2, d3);
            tessellator.addVertexWithUV((double)p_147794_2_ + d5, (double)(p_147794_3_ + 1) + d4, (double)(p_147794_4_ + 0) - d4, d2, d1);
        }

        if (l == 4)
        {
            tessellator.addVertexWithUV((double)(p_147794_2_ + 1) - d5, (double)(p_147794_3_ + 0) - d4, (double)(p_147794_4_ + 1) + d4, d2, d3);
            tessellator.addVertexWithUV((double)(p_147794_2_ + 1) - d5, (double)(p_147794_3_ + 1) + d4, (double)(p_147794_4_ + 1) + d4, d2, d1);
            tessellator.addVertexWithUV((double)(p_147794_2_ + 1) - d5, (double)(p_147794_3_ + 1) + d4, (double)(p_147794_4_ + 0) - d4, d0, d1);
            tessellator.addVertexWithUV((double)(p_147794_2_ + 1) - d5, (double)(p_147794_3_ + 0) - d4, (double)(p_147794_4_ + 0) - d4, d0, d3);
        }

        if (l == 3)
        {
            tessellator.addVertexWithUV((double)(p_147794_2_ + 1) + d4, (double)(p_147794_3_ + 0) - d4, (double)p_147794_4_ + d5, d2, d3);
            tessellator.addVertexWithUV((double)(p_147794_2_ + 1) + d4, (double)(p_147794_3_ + 1) + d4, (double)p_147794_4_ + d5, d2, d1);
            tessellator.addVertexWithUV((double)(p_147794_2_ + 0) - d4, (double)(p_147794_3_ + 1) + d4, (double)p_147794_4_ + d5, d0, d1);
            tessellator.addVertexWithUV((double)(p_147794_2_ + 0) - d4, (double)(p_147794_3_ + 0) - d4, (double)p_147794_4_ + d5, d0, d3);
        }

        if (l == 2)
        {
            tessellator.addVertexWithUV((double)(p_147794_2_ + 1) + d4, (double)(p_147794_3_ + 1) + d4, (double)(p_147794_4_ + 1) - d5, d0, d1);
            tessellator.addVertexWithUV((double)(p_147794_2_ + 1) + d4, (double)(p_147794_3_ + 0) - d4, (double)(p_147794_4_ + 1) - d5, d0, d3);
            tessellator.addVertexWithUV((double)(p_147794_2_ + 0) - d4, (double)(p_147794_3_ + 0) - d4, (double)(p_147794_4_ + 1) - d5, d2, d3);
            tessellator.addVertexWithUV((double)(p_147794_2_ + 0) - d4, (double)(p_147794_3_ + 1) + d4, (double)(p_147794_4_ + 1) - d5, d2, d1);
        }

        return true;
    }

    public boolean func_147726_j(Block p_147726_1_, int p_147726_2_, int p_147726_3_, int p_147726_4_)
    {
        Tessellator tessellator = Tessellator.instance;
        IIcon iicon = this.func_147777_a(p_147726_1_, 0);

        if (this.func_147744_b())
        {
            iicon = this.field_147840_d;
        }

        tessellator.setBrightness(p_147726_1_.func_149677_c(this.field_147845_a, p_147726_2_, p_147726_3_, p_147726_4_));
        int l = p_147726_1_.func_149720_d(this.field_147845_a, p_147726_2_, p_147726_3_, p_147726_4_);
        float f = (float)(l >> 16 & 255) / 255.0F;
        float f1 = (float)(l >> 8 & 255) / 255.0F;
        float f2 = (float)(l & 255) / 255.0F;
        tessellator.setColorOpaque_F(f, f1, f2);
        double d3 = (double)iicon.getMinU();
        double d4 = (double)iicon.getMinV();
        double d0 = (double)iicon.getMaxU();
        double d1 = (double)iicon.getMaxV();
        double d2 = 0.05000000074505806D;
        int i1 = this.field_147845_a.getBlockMetadata(p_147726_2_, p_147726_3_, p_147726_4_);

        if ((i1 & 2) != 0)
        {
            tessellator.addVertexWithUV((double)p_147726_2_ + d2, (double)(p_147726_3_ + 1), (double)(p_147726_4_ + 1), d3, d4);
            tessellator.addVertexWithUV((double)p_147726_2_ + d2, (double)(p_147726_3_ + 0), (double)(p_147726_4_ + 1), d3, d1);
            tessellator.addVertexWithUV((double)p_147726_2_ + d2, (double)(p_147726_3_ + 0), (double)(p_147726_4_ + 0), d0, d1);
            tessellator.addVertexWithUV((double)p_147726_2_ + d2, (double)(p_147726_3_ + 1), (double)(p_147726_4_ + 0), d0, d4);
            tessellator.addVertexWithUV((double)p_147726_2_ + d2, (double)(p_147726_3_ + 1), (double)(p_147726_4_ + 0), d0, d4);
            tessellator.addVertexWithUV((double)p_147726_2_ + d2, (double)(p_147726_3_ + 0), (double)(p_147726_4_ + 0), d0, d1);
            tessellator.addVertexWithUV((double)p_147726_2_ + d2, (double)(p_147726_3_ + 0), (double)(p_147726_4_ + 1), d3, d1);
            tessellator.addVertexWithUV((double)p_147726_2_ + d2, (double)(p_147726_3_ + 1), (double)(p_147726_4_ + 1), d3, d4);
        }

        if ((i1 & 8) != 0)
        {
            tessellator.addVertexWithUV((double)(p_147726_2_ + 1) - d2, (double)(p_147726_3_ + 0), (double)(p_147726_4_ + 1), d0, d1);
            tessellator.addVertexWithUV((double)(p_147726_2_ + 1) - d2, (double)(p_147726_3_ + 1), (double)(p_147726_4_ + 1), d0, d4);
            tessellator.addVertexWithUV((double)(p_147726_2_ + 1) - d2, (double)(p_147726_3_ + 1), (double)(p_147726_4_ + 0), d3, d4);
            tessellator.addVertexWithUV((double)(p_147726_2_ + 1) - d2, (double)(p_147726_3_ + 0), (double)(p_147726_4_ + 0), d3, d1);
            tessellator.addVertexWithUV((double)(p_147726_2_ + 1) - d2, (double)(p_147726_3_ + 0), (double)(p_147726_4_ + 0), d3, d1);
            tessellator.addVertexWithUV((double)(p_147726_2_ + 1) - d2, (double)(p_147726_3_ + 1), (double)(p_147726_4_ + 0), d3, d4);
            tessellator.addVertexWithUV((double)(p_147726_2_ + 1) - d2, (double)(p_147726_3_ + 1), (double)(p_147726_4_ + 1), d0, d4);
            tessellator.addVertexWithUV((double)(p_147726_2_ + 1) - d2, (double)(p_147726_3_ + 0), (double)(p_147726_4_ + 1), d0, d1);
        }

        if ((i1 & 4) != 0)
        {
            tessellator.addVertexWithUV((double)(p_147726_2_ + 1), (double)(p_147726_3_ + 0), (double)p_147726_4_ + d2, d0, d1);
            tessellator.addVertexWithUV((double)(p_147726_2_ + 1), (double)(p_147726_3_ + 1), (double)p_147726_4_ + d2, d0, d4);
            tessellator.addVertexWithUV((double)(p_147726_2_ + 0), (double)(p_147726_3_ + 1), (double)p_147726_4_ + d2, d3, d4);
            tessellator.addVertexWithUV((double)(p_147726_2_ + 0), (double)(p_147726_3_ + 0), (double)p_147726_4_ + d2, d3, d1);
            tessellator.addVertexWithUV((double)(p_147726_2_ + 0), (double)(p_147726_3_ + 0), (double)p_147726_4_ + d2, d3, d1);
            tessellator.addVertexWithUV((double)(p_147726_2_ + 0), (double)(p_147726_3_ + 1), (double)p_147726_4_ + d2, d3, d4);
            tessellator.addVertexWithUV((double)(p_147726_2_ + 1), (double)(p_147726_3_ + 1), (double)p_147726_4_ + d2, d0, d4);
            tessellator.addVertexWithUV((double)(p_147726_2_ + 1), (double)(p_147726_3_ + 0), (double)p_147726_4_ + d2, d0, d1);
        }

        if ((i1 & 1) != 0)
        {
            tessellator.addVertexWithUV((double)(p_147726_2_ + 1), (double)(p_147726_3_ + 1), (double)(p_147726_4_ + 1) - d2, d3, d4);
            tessellator.addVertexWithUV((double)(p_147726_2_ + 1), (double)(p_147726_3_ + 0), (double)(p_147726_4_ + 1) - d2, d3, d1);
            tessellator.addVertexWithUV((double)(p_147726_2_ + 0), (double)(p_147726_3_ + 0), (double)(p_147726_4_ + 1) - d2, d0, d1);
            tessellator.addVertexWithUV((double)(p_147726_2_ + 0), (double)(p_147726_3_ + 1), (double)(p_147726_4_ + 1) - d2, d0, d4);
            tessellator.addVertexWithUV((double)(p_147726_2_ + 0), (double)(p_147726_3_ + 1), (double)(p_147726_4_ + 1) - d2, d0, d4);
            tessellator.addVertexWithUV((double)(p_147726_2_ + 0), (double)(p_147726_3_ + 0), (double)(p_147726_4_ + 1) - d2, d0, d1);
            tessellator.addVertexWithUV((double)(p_147726_2_ + 1), (double)(p_147726_3_ + 0), (double)(p_147726_4_ + 1) - d2, d3, d1);
            tessellator.addVertexWithUV((double)(p_147726_2_ + 1), (double)(p_147726_3_ + 1), (double)(p_147726_4_ + 1) - d2, d3, d4);
        }

        if (this.field_147845_a.func_147439_a(p_147726_2_, p_147726_3_ + 1, p_147726_4_).func_149637_q())
        {
            tessellator.addVertexWithUV((double)(p_147726_2_ + 1), (double)(p_147726_3_ + 1) - d2, (double)(p_147726_4_ + 0), d3, d4);
            tessellator.addVertexWithUV((double)(p_147726_2_ + 1), (double)(p_147726_3_ + 1) - d2, (double)(p_147726_4_ + 1), d3, d1);
            tessellator.addVertexWithUV((double)(p_147726_2_ + 0), (double)(p_147726_3_ + 1) - d2, (double)(p_147726_4_ + 1), d0, d1);
            tessellator.addVertexWithUV((double)(p_147726_2_ + 0), (double)(p_147726_3_ + 1) - d2, (double)(p_147726_4_ + 0), d0, d4);
        }

        return true;
    }

    public boolean func_147733_k(Block p_147733_1_, int p_147733_2_, int p_147733_3_, int p_147733_4_)
    {
        int l = this.field_147845_a.getHeight();
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(p_147733_1_.func_149677_c(this.field_147845_a, p_147733_2_, p_147733_3_, p_147733_4_));
        int i1 = p_147733_1_.func_149720_d(this.field_147845_a, p_147733_2_, p_147733_3_, p_147733_4_);
        float f = (float)(i1 >> 16 & 255) / 255.0F;
        float f1 = (float)(i1 >> 8 & 255) / 255.0F;
        float f2 = (float)(i1 & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable)
        {
            float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
            float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            f = f3;
            f1 = f4;
            f2 = f5;
        }

        tessellator.setColorOpaque_F(f, f1, f2);
        boolean flag5 = p_147733_1_ instanceof BlockStainedGlassPane;
        IIcon iicon;
        IIcon iicon1;

        if (this.func_147744_b())
        {
            iicon = this.field_147840_d;
            iicon1 = this.field_147840_d;
        }
        else
        {
            int j1 = this.field_147845_a.getBlockMetadata(p_147733_2_, p_147733_3_, p_147733_4_);
            iicon = this.func_147787_a(p_147733_1_, 0, j1);
            iicon1 = flag5 ? ((BlockStainedGlassPane)p_147733_1_).func_150104_b(j1) : ((BlockPane)p_147733_1_).func_150097_e();
        }

        double d22 = (double)iicon.getMinU();
        double d0 = (double)iicon.getInterpolatedU(7.0D);
        double d1 = (double)iicon.getInterpolatedU(9.0D);
        double d2 = (double)iicon.getMaxU();
        double d3 = (double)iicon.getMinV();
        double d4 = (double)iicon.getMaxV();
        double d5 = (double)iicon1.getInterpolatedU(7.0D);
        double d6 = (double)iicon1.getInterpolatedU(9.0D);
        double d7 = (double)iicon1.getMinV();
        double d8 = (double)iicon1.getMaxV();
        double d9 = (double)iicon1.getInterpolatedV(7.0D);
        double d10 = (double)iicon1.getInterpolatedV(9.0D);
        double d11 = (double)p_147733_2_;
        double d12 = (double)(p_147733_2_ + 1);
        double d13 = (double)p_147733_4_;
        double d14 = (double)(p_147733_4_ + 1);
        double d15 = (double)p_147733_2_ + 0.5D - 0.0625D;
        double d16 = (double)p_147733_2_ + 0.5D + 0.0625D;
        double d17 = (double)p_147733_4_ + 0.5D - 0.0625D;
        double d18 = (double)p_147733_4_ + 0.5D + 0.0625D;
        boolean flag = flag5 ? ((BlockStainedGlassPane)p_147733_1_).func_150098_a(this.field_147845_a.func_147439_a(p_147733_2_, p_147733_3_, p_147733_4_ - 1)) : ((BlockPane)p_147733_1_).func_150098_a(this.field_147845_a.func_147439_a(p_147733_2_, p_147733_3_, p_147733_4_ - 1));
        boolean flag1 = flag5 ? ((BlockStainedGlassPane)p_147733_1_).func_150098_a(this.field_147845_a.func_147439_a(p_147733_2_, p_147733_3_, p_147733_4_ + 1)) : ((BlockPane)p_147733_1_).func_150098_a(this.field_147845_a.func_147439_a(p_147733_2_, p_147733_3_, p_147733_4_ + 1));
        boolean flag2 = flag5 ? ((BlockStainedGlassPane)p_147733_1_).func_150098_a(this.field_147845_a.func_147439_a(p_147733_2_ - 1, p_147733_3_, p_147733_4_)) : ((BlockPane)p_147733_1_).func_150098_a(this.field_147845_a.func_147439_a(p_147733_2_ - 1, p_147733_3_, p_147733_4_));
        boolean flag3 = flag5 ? ((BlockStainedGlassPane)p_147733_1_).func_150098_a(this.field_147845_a.func_147439_a(p_147733_2_ + 1, p_147733_3_, p_147733_4_)) : ((BlockPane)p_147733_1_).func_150098_a(this.field_147845_a.func_147439_a(p_147733_2_ + 1, p_147733_3_, p_147733_4_));
        double d19 = 0.001D;
        double d20 = 0.999D;
        double d21 = 0.001D;
        boolean flag4 = !flag && !flag1 && !flag2 && !flag3;

        if (!flag2 && !flag4)
        {
            if (!flag && !flag1)
            {
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d17, d0, d3);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d17, d0, d4);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d18, d1, d4);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d18, d1, d3);
            }
        }
        else if (flag2 && flag3)
        {
            if (!flag)
            {
                tessellator.addVertexWithUV(d12, (double)p_147733_3_ + 0.999D, d17, d2, d3);
                tessellator.addVertexWithUV(d12, (double)p_147733_3_ + 0.001D, d17, d2, d4);
                tessellator.addVertexWithUV(d11, (double)p_147733_3_ + 0.001D, d17, d22, d4);
                tessellator.addVertexWithUV(d11, (double)p_147733_3_ + 0.999D, d17, d22, d3);
            }
            else
            {
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d17, d0, d3);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d17, d0, d4);
                tessellator.addVertexWithUV(d11, (double)p_147733_3_ + 0.001D, d17, d22, d4);
                tessellator.addVertexWithUV(d11, (double)p_147733_3_ + 0.999D, d17, d22, d3);
                tessellator.addVertexWithUV(d12, (double)p_147733_3_ + 0.999D, d17, d2, d3);
                tessellator.addVertexWithUV(d12, (double)p_147733_3_ + 0.001D, d17, d2, d4);
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d17, d1, d4);
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d17, d1, d3);
            }

            if (!flag1)
            {
                tessellator.addVertexWithUV(d11, (double)p_147733_3_ + 0.999D, d18, d22, d3);
                tessellator.addVertexWithUV(d11, (double)p_147733_3_ + 0.001D, d18, d22, d4);
                tessellator.addVertexWithUV(d12, (double)p_147733_3_ + 0.001D, d18, d2, d4);
                tessellator.addVertexWithUV(d12, (double)p_147733_3_ + 0.999D, d18, d2, d3);
            }
            else
            {
                tessellator.addVertexWithUV(d11, (double)p_147733_3_ + 0.999D, d18, d22, d3);
                tessellator.addVertexWithUV(d11, (double)p_147733_3_ + 0.001D, d18, d22, d4);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d18, d0, d4);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d18, d0, d3);
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d18, d1, d3);
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d18, d1, d4);
                tessellator.addVertexWithUV(d12, (double)p_147733_3_ + 0.001D, d18, d2, d4);
                tessellator.addVertexWithUV(d12, (double)p_147733_3_ + 0.999D, d18, d2, d3);
            }

            tessellator.addVertexWithUV(d11, (double)p_147733_3_ + 0.999D, d18, d6, d7);
            tessellator.addVertexWithUV(d12, (double)p_147733_3_ + 0.999D, d18, d6, d8);
            tessellator.addVertexWithUV(d12, (double)p_147733_3_ + 0.999D, d17, d5, d8);
            tessellator.addVertexWithUV(d11, (double)p_147733_3_ + 0.999D, d17, d5, d7);
            tessellator.addVertexWithUV(d12, (double)p_147733_3_ + 0.001D, d18, d5, d8);
            tessellator.addVertexWithUV(d11, (double)p_147733_3_ + 0.001D, d18, d5, d7);
            tessellator.addVertexWithUV(d11, (double)p_147733_3_ + 0.001D, d17, d6, d7);
            tessellator.addVertexWithUV(d12, (double)p_147733_3_ + 0.001D, d17, d6, d8);
        }
        else
        {
            if (!flag && !flag4)
            {
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d17, d1, d3);
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d17, d1, d4);
                tessellator.addVertexWithUV(d11, (double)p_147733_3_ + 0.001D, d17, d22, d4);
                tessellator.addVertexWithUV(d11, (double)p_147733_3_ + 0.999D, d17, d22, d3);
            }
            else
            {
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d17, d0, d3);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d17, d0, d4);
                tessellator.addVertexWithUV(d11, (double)p_147733_3_ + 0.001D, d17, d22, d4);
                tessellator.addVertexWithUV(d11, (double)p_147733_3_ + 0.999D, d17, d22, d3);
            }

            if (!flag1 && !flag4)
            {
                tessellator.addVertexWithUV(d11, (double)p_147733_3_ + 0.999D, d18, d22, d3);
                tessellator.addVertexWithUV(d11, (double)p_147733_3_ + 0.001D, d18, d22, d4);
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d18, d1, d4);
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d18, d1, d3);
            }
            else
            {
                tessellator.addVertexWithUV(d11, (double)p_147733_3_ + 0.999D, d18, d22, d3);
                tessellator.addVertexWithUV(d11, (double)p_147733_3_ + 0.001D, d18, d22, d4);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d18, d0, d4);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d18, d0, d3);
            }

            tessellator.addVertexWithUV(d11, (double)p_147733_3_ + 0.999D, d18, d6, d7);
            tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d18, d6, d9);
            tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d17, d5, d9);
            tessellator.addVertexWithUV(d11, (double)p_147733_3_ + 0.999D, d17, d5, d7);
            tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d18, d5, d9);
            tessellator.addVertexWithUV(d11, (double)p_147733_3_ + 0.001D, d18, d5, d7);
            tessellator.addVertexWithUV(d11, (double)p_147733_3_ + 0.001D, d17, d6, d7);
            tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d17, d6, d9);
        }

        if ((flag3 || flag4) && !flag2)
        {
            if (!flag1 && !flag4)
            {
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d18, d0, d3);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d18, d0, d4);
                tessellator.addVertexWithUV(d12, (double)p_147733_3_ + 0.001D, d18, d2, d4);
                tessellator.addVertexWithUV(d12, (double)p_147733_3_ + 0.999D, d18, d2, d3);
            }
            else
            {
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d18, d1, d3);
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d18, d1, d4);
                tessellator.addVertexWithUV(d12, (double)p_147733_3_ + 0.001D, d18, d2, d4);
                tessellator.addVertexWithUV(d12, (double)p_147733_3_ + 0.999D, d18, d2, d3);
            }

            if (!flag && !flag4)
            {
                tessellator.addVertexWithUV(d12, (double)p_147733_3_ + 0.999D, d17, d2, d3);
                tessellator.addVertexWithUV(d12, (double)p_147733_3_ + 0.001D, d17, d2, d4);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d17, d0, d4);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d17, d0, d3);
            }
            else
            {
                tessellator.addVertexWithUV(d12, (double)p_147733_3_ + 0.999D, d17, d2, d3);
                tessellator.addVertexWithUV(d12, (double)p_147733_3_ + 0.001D, d17, d2, d4);
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d17, d1, d4);
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d17, d1, d3);
            }

            tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d18, d6, d10);
            tessellator.addVertexWithUV(d12, (double)p_147733_3_ + 0.999D, d18, d6, d7);
            tessellator.addVertexWithUV(d12, (double)p_147733_3_ + 0.999D, d17, d5, d7);
            tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d17, d5, d10);
            tessellator.addVertexWithUV(d12, (double)p_147733_3_ + 0.001D, d18, d5, d8);
            tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d18, d5, d10);
            tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d17, d6, d10);
            tessellator.addVertexWithUV(d12, (double)p_147733_3_ + 0.001D, d17, d6, d8);
        }
        else if (!flag3 && !flag && !flag1)
        {
            tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d18, d0, d3);
            tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d18, d0, d4);
            tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d17, d1, d4);
            tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d17, d1, d3);
        }

        if (!flag && !flag4)
        {
            if (!flag3 && !flag2)
            {
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d17, d1, d3);
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d17, d1, d4);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d17, d0, d4);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d17, d0, d3);
            }
        }
        else if (flag && flag1)
        {
            if (!flag2)
            {
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d13, d22, d3);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d13, d22, d4);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d14, d2, d4);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d14, d2, d3);
            }
            else
            {
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d13, d22, d3);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d13, d22, d4);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d17, d0, d4);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d17, d0, d3);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d18, d1, d3);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d18, d1, d4);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d14, d2, d4);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d14, d2, d3);
            }

            if (!flag3)
            {
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d14, d2, d3);
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d14, d2, d4);
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d13, d22, d4);
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d13, d22, d3);
            }
            else
            {
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d17, d0, d3);
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d17, d0, d4);
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d13, d22, d4);
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d13, d22, d3);
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d14, d2, d3);
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d14, d2, d4);
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d18, d1, d4);
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d18, d1, d3);
            }

            tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d13, d6, d7);
            tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d13, d5, d7);
            tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d14, d5, d8);
            tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d14, d6, d8);
            tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d13, d5, d7);
            tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d13, d6, d7);
            tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d14, d6, d8);
            tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d14, d5, d8);
        }
        else
        {
            if (!flag2 && !flag4)
            {
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d13, d22, d3);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d13, d22, d4);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d18, d1, d4);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d18, d1, d3);
            }
            else
            {
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d13, d22, d3);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d13, d22, d4);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d17, d0, d4);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d17, d0, d3);
            }

            if (!flag3 && !flag4)
            {
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d18, d1, d3);
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d18, d1, d4);
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d13, d22, d4);
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d13, d22, d3);
            }
            else
            {
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d17, d0, d3);
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d17, d0, d4);
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d13, d22, d4);
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d13, d22, d3);
            }

            tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d13, d6, d7);
            tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d13, d5, d7);
            tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d17, d5, d9);
            tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d17, d6, d9);
            tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d13, d5, d7);
            tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d13, d6, d7);
            tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d17, d6, d9);
            tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d17, d5, d9);
        }

        if ((flag1 || flag4) && !flag)
        {
            if (!flag2 && !flag4)
            {
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d17, d0, d3);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d17, d0, d4);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d14, d2, d4);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d14, d2, d3);
            }
            else
            {
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d18, d1, d3);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d18, d1, d4);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d14, d2, d4);
                tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d14, d2, d3);
            }

            if (!flag3 && !flag4)
            {
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d14, d2, d3);
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d14, d2, d4);
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d17, d0, d4);
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d17, d0, d3);
            }
            else
            {
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d14, d2, d3);
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d14, d2, d4);
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d18, d1, d4);
                tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d18, d1, d3);
            }

            tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d18, d6, d10);
            tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d18, d5, d10);
            tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d14, d5, d8);
            tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d14, d6, d8);
            tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d18, d5, d10);
            tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d18, d6, d10);
            tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d14, d6, d8);
            tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d14, d5, d8);
        }
        else if (!flag1 && !flag3 && !flag2)
        {
            tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d18, d0, d3);
            tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d18, d0, d4);
            tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d18, d1, d4);
            tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d18, d1, d3);
        }

        tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d17, d6, d9);
        tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d17, d5, d9);
        tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d18, d5, d10);
        tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d18, d6, d10);
        tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d17, d5, d9);
        tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d17, d6, d9);
        tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d18, d6, d10);
        tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d18, d5, d10);

        if (flag4)
        {
            tessellator.addVertexWithUV(d11, (double)p_147733_3_ + 0.999D, d17, d0, d3);
            tessellator.addVertexWithUV(d11, (double)p_147733_3_ + 0.001D, d17, d0, d4);
            tessellator.addVertexWithUV(d11, (double)p_147733_3_ + 0.001D, d18, d1, d4);
            tessellator.addVertexWithUV(d11, (double)p_147733_3_ + 0.999D, d18, d1, d3);
            tessellator.addVertexWithUV(d12, (double)p_147733_3_ + 0.999D, d18, d0, d3);
            tessellator.addVertexWithUV(d12, (double)p_147733_3_ + 0.001D, d18, d0, d4);
            tessellator.addVertexWithUV(d12, (double)p_147733_3_ + 0.001D, d17, d1, d4);
            tessellator.addVertexWithUV(d12, (double)p_147733_3_ + 0.999D, d17, d1, d3);
            tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d13, d1, d3);
            tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d13, d1, d4);
            tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d13, d0, d4);
            tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d13, d0, d3);
            tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.999D, d14, d0, d3);
            tessellator.addVertexWithUV(d15, (double)p_147733_3_ + 0.001D, d14, d0, d4);
            tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.001D, d14, d1, d4);
            tessellator.addVertexWithUV(d16, (double)p_147733_3_ + 0.999D, d14, d1, d3);
        }

        return true;
    }

    public boolean func_147767_a(BlockPane p_147767_1_, int p_147767_2_, int p_147767_3_, int p_147767_4_)
    {
        int l = this.field_147845_a.getHeight();
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(p_147767_1_.func_149677_c(this.field_147845_a, p_147767_2_, p_147767_3_, p_147767_4_));
        int i1 = p_147767_1_.func_149720_d(this.field_147845_a, p_147767_2_, p_147767_3_, p_147767_4_);
        float f = (float)(i1 >> 16 & 255) / 255.0F;
        float f1 = (float)(i1 >> 8 & 255) / 255.0F;
        float f2 = (float)(i1 & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable)
        {
            float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
            float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            f = f3;
            f1 = f4;
            f2 = f5;
        }

        tessellator.setColorOpaque_F(f, f1, f2);
        IIcon iicon1;
        IIcon iicon;

        if (this.func_147744_b())
        {
            iicon = this.field_147840_d;
            iicon1 = this.field_147840_d;
        }
        else
        {
            int j1 = this.field_147845_a.getBlockMetadata(p_147767_2_, p_147767_3_, p_147767_4_);
            iicon = this.func_147787_a(p_147767_1_, 0, j1);
            iicon1 = p_147767_1_.func_150097_e();
        }

        double d21 = (double)iicon.getMinU();
        double d0 = (double)iicon.getInterpolatedU(8.0D);
        double d1 = (double)iicon.getMaxU();
        double d2 = (double)iicon.getMinV();
        double d3 = (double)iicon.getMaxV();
        double d4 = (double)iicon1.getInterpolatedU(7.0D);
        double d5 = (double)iicon1.getInterpolatedU(9.0D);
        double d6 = (double)iicon1.getMinV();
        double d7 = (double)iicon1.getInterpolatedV(8.0D);
        double d8 = (double)iicon1.getMaxV();
        double d9 = (double)p_147767_2_;
        double d10 = (double)p_147767_2_ + 0.5D;
        double d11 = (double)(p_147767_2_ + 1);
        double d12 = (double)p_147767_4_;
        double d13 = (double)p_147767_4_ + 0.5D;
        double d14 = (double)(p_147767_4_ + 1);
        double d15 = (double)p_147767_2_ + 0.5D - 0.0625D;
        double d16 = (double)p_147767_2_ + 0.5D + 0.0625D;
        double d17 = (double)p_147767_4_ + 0.5D - 0.0625D;
        double d18 = (double)p_147767_4_ + 0.5D + 0.0625D;
        boolean flag  = p_147767_1_.canPaneConnectTo(this.field_147845_a, p_147767_2_, p_147767_3_, p_147767_4_ - 1, NORTH);
        boolean flag1 = p_147767_1_.canPaneConnectTo(this.field_147845_a, p_147767_2_, p_147767_3_, p_147767_4_ + 1, SOUTH);
        boolean flag2 = p_147767_1_.canPaneConnectTo(this.field_147845_a, p_147767_2_ - 1, p_147767_3_, p_147767_4_, WEST );
        boolean flag3 = p_147767_1_.canPaneConnectTo(this.field_147845_a, p_147767_2_ + 1, p_147767_3_, p_147767_4_, EAST );
        boolean flag4 = p_147767_1_.func_149646_a(this.field_147845_a, p_147767_2_, p_147767_3_ + 1, p_147767_4_, 1);
        boolean flag5 = p_147767_1_.func_149646_a(this.field_147845_a, p_147767_2_, p_147767_3_ - 1, p_147767_4_, 0);
        double d19 = 0.01D;
        double d20 = 0.005D;

        if ((!flag2 || !flag3) && (flag2 || flag3 || flag || flag1))
        {
            if (flag2 && !flag3)
            {
                tessellator.addVertexWithUV(d9, (double)(p_147767_3_ + 1), d13, d21, d2);
                tessellator.addVertexWithUV(d9, (double)(p_147767_3_ + 0), d13, d21, d3);
                tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 0), d13, d0, d3);
                tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1), d13, d0, d2);
                tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1), d13, d21, d2);
                tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 0), d13, d21, d3);
                tessellator.addVertexWithUV(d9, (double)(p_147767_3_ + 0), d13, d0, d3);
                tessellator.addVertexWithUV(d9, (double)(p_147767_3_ + 1), d13, d0, d2);

                if (!flag1 && !flag)
                {
                    tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1), d18, d4, d6);
                    tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 0), d18, d4, d8);
                    tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 0), d17, d5, d8);
                    tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1), d17, d5, d6);
                    tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1), d17, d4, d6);
                    tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 0), d17, d4, d8);
                    tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 0), d18, d5, d8);
                    tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1), d18, d5, d6);
                }

                if (flag4 || p_147767_3_ < l - 1 && this.field_147845_a.func_147437_c(p_147767_2_ - 1, p_147767_3_ + 1, p_147767_4_))
                {
                    tessellator.addVertexWithUV(d9, (double)(p_147767_3_ + 1) + 0.01D, d18, d5, d7);
                    tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1) + 0.01D, d18, d5, d8);
                    tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1) + 0.01D, d17, d4, d8);
                    tessellator.addVertexWithUV(d9, (double)(p_147767_3_ + 1) + 0.01D, d17, d4, d7);
                    tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1) + 0.01D, d18, d5, d7);
                    tessellator.addVertexWithUV(d9, (double)(p_147767_3_ + 1) + 0.01D, d18, d5, d8);
                    tessellator.addVertexWithUV(d9, (double)(p_147767_3_ + 1) + 0.01D, d17, d4, d8);
                    tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1) + 0.01D, d17, d4, d7);
                }

                if (flag5 || p_147767_3_ > 1 && this.field_147845_a.func_147437_c(p_147767_2_ - 1, p_147767_3_ - 1, p_147767_4_))
                {
                    tessellator.addVertexWithUV(d9, (double)p_147767_3_ - 0.01D, d18, d5, d7);
                    tessellator.addVertexWithUV(d10, (double)p_147767_3_ - 0.01D, d18, d5, d8);
                    tessellator.addVertexWithUV(d10, (double)p_147767_3_ - 0.01D, d17, d4, d8);
                    tessellator.addVertexWithUV(d9, (double)p_147767_3_ - 0.01D, d17, d4, d7);
                    tessellator.addVertexWithUV(d10, (double)p_147767_3_ - 0.01D, d18, d5, d7);
                    tessellator.addVertexWithUV(d9, (double)p_147767_3_ - 0.01D, d18, d5, d8);
                    tessellator.addVertexWithUV(d9, (double)p_147767_3_ - 0.01D, d17, d4, d8);
                    tessellator.addVertexWithUV(d10, (double)p_147767_3_ - 0.01D, d17, d4, d7);
                }
            }
            else if (!flag2 && flag3)
            {
                tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1), d13, d0, d2);
                tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 0), d13, d0, d3);
                tessellator.addVertexWithUV(d11, (double)(p_147767_3_ + 0), d13, d1, d3);
                tessellator.addVertexWithUV(d11, (double)(p_147767_3_ + 1), d13, d1, d2);
                tessellator.addVertexWithUV(d11, (double)(p_147767_3_ + 1), d13, d0, d2);
                tessellator.addVertexWithUV(d11, (double)(p_147767_3_ + 0), d13, d0, d3);
                tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 0), d13, d1, d3);
                tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1), d13, d1, d2);

                if (!flag1 && !flag)
                {
                    tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1), d17, d4, d6);
                    tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 0), d17, d4, d8);
                    tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 0), d18, d5, d8);
                    tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1), d18, d5, d6);
                    tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1), d18, d4, d6);
                    tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 0), d18, d4, d8);
                    tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 0), d17, d5, d8);
                    tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1), d17, d5, d6);
                }

                if (flag4 || p_147767_3_ < l - 1 && this.field_147845_a.func_147437_c(p_147767_2_ + 1, p_147767_3_ + 1, p_147767_4_))
                {
                    tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1) + 0.01D, d18, d5, d6);
                    tessellator.addVertexWithUV(d11, (double)(p_147767_3_ + 1) + 0.01D, d18, d5, d7);
                    tessellator.addVertexWithUV(d11, (double)(p_147767_3_ + 1) + 0.01D, d17, d4, d7);
                    tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1) + 0.01D, d17, d4, d6);
                    tessellator.addVertexWithUV(d11, (double)(p_147767_3_ + 1) + 0.01D, d18, d5, d6);
                    tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1) + 0.01D, d18, d5, d7);
                    tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1) + 0.01D, d17, d4, d7);
                    tessellator.addVertexWithUV(d11, (double)(p_147767_3_ + 1) + 0.01D, d17, d4, d6);
                }

                if (flag5 || p_147767_3_ > 1 && this.field_147845_a.func_147437_c(p_147767_2_ + 1, p_147767_3_ - 1, p_147767_4_))
                {
                    tessellator.addVertexWithUV(d10, (double)p_147767_3_ - 0.01D, d18, d5, d6);
                    tessellator.addVertexWithUV(d11, (double)p_147767_3_ - 0.01D, d18, d5, d7);
                    tessellator.addVertexWithUV(d11, (double)p_147767_3_ - 0.01D, d17, d4, d7);
                    tessellator.addVertexWithUV(d10, (double)p_147767_3_ - 0.01D, d17, d4, d6);
                    tessellator.addVertexWithUV(d11, (double)p_147767_3_ - 0.01D, d18, d5, d6);
                    tessellator.addVertexWithUV(d10, (double)p_147767_3_ - 0.01D, d18, d5, d7);
                    tessellator.addVertexWithUV(d10, (double)p_147767_3_ - 0.01D, d17, d4, d7);
                    tessellator.addVertexWithUV(d11, (double)p_147767_3_ - 0.01D, d17, d4, d6);
                }
            }
        }
        else
        {
            tessellator.addVertexWithUV(d9, (double)(p_147767_3_ + 1), d13, d21, d2);
            tessellator.addVertexWithUV(d9, (double)(p_147767_3_ + 0), d13, d21, d3);
            tessellator.addVertexWithUV(d11, (double)(p_147767_3_ + 0), d13, d1, d3);
            tessellator.addVertexWithUV(d11, (double)(p_147767_3_ + 1), d13, d1, d2);
            tessellator.addVertexWithUV(d11, (double)(p_147767_3_ + 1), d13, d21, d2);
            tessellator.addVertexWithUV(d11, (double)(p_147767_3_ + 0), d13, d21, d3);
            tessellator.addVertexWithUV(d9, (double)(p_147767_3_ + 0), d13, d1, d3);
            tessellator.addVertexWithUV(d9, (double)(p_147767_3_ + 1), d13, d1, d2);

            if (flag4)
            {
                tessellator.addVertexWithUV(d9, (double)(p_147767_3_ + 1) + 0.01D, d18, d5, d8);
                tessellator.addVertexWithUV(d11, (double)(p_147767_3_ + 1) + 0.01D, d18, d5, d6);
                tessellator.addVertexWithUV(d11, (double)(p_147767_3_ + 1) + 0.01D, d17, d4, d6);
                tessellator.addVertexWithUV(d9, (double)(p_147767_3_ + 1) + 0.01D, d17, d4, d8);
                tessellator.addVertexWithUV(d11, (double)(p_147767_3_ + 1) + 0.01D, d18, d5, d8);
                tessellator.addVertexWithUV(d9, (double)(p_147767_3_ + 1) + 0.01D, d18, d5, d6);
                tessellator.addVertexWithUV(d9, (double)(p_147767_3_ + 1) + 0.01D, d17, d4, d6);
                tessellator.addVertexWithUV(d11, (double)(p_147767_3_ + 1) + 0.01D, d17, d4, d8);
            }
            else
            {
                if (p_147767_3_ < l - 1 && this.field_147845_a.func_147437_c(p_147767_2_ - 1, p_147767_3_ + 1, p_147767_4_))
                {
                    tessellator.addVertexWithUV(d9, (double)(p_147767_3_ + 1) + 0.01D, d18, d5, d7);
                    tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1) + 0.01D, d18, d5, d8);
                    tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1) + 0.01D, d17, d4, d8);
                    tessellator.addVertexWithUV(d9, (double)(p_147767_3_ + 1) + 0.01D, d17, d4, d7);
                    tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1) + 0.01D, d18, d5, d7);
                    tessellator.addVertexWithUV(d9, (double)(p_147767_3_ + 1) + 0.01D, d18, d5, d8);
                    tessellator.addVertexWithUV(d9, (double)(p_147767_3_ + 1) + 0.01D, d17, d4, d8);
                    tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1) + 0.01D, d17, d4, d7);
                }

                if (p_147767_3_ < l - 1 && this.field_147845_a.func_147437_c(p_147767_2_ + 1, p_147767_3_ + 1, p_147767_4_))
                {
                    tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1) + 0.01D, d18, d5, d6);
                    tessellator.addVertexWithUV(d11, (double)(p_147767_3_ + 1) + 0.01D, d18, d5, d7);
                    tessellator.addVertexWithUV(d11, (double)(p_147767_3_ + 1) + 0.01D, d17, d4, d7);
                    tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1) + 0.01D, d17, d4, d6);
                    tessellator.addVertexWithUV(d11, (double)(p_147767_3_ + 1) + 0.01D, d18, d5, d6);
                    tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1) + 0.01D, d18, d5, d7);
                    tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1) + 0.01D, d17, d4, d7);
                    tessellator.addVertexWithUV(d11, (double)(p_147767_3_ + 1) + 0.01D, d17, d4, d6);
                }
            }

            if (flag5)
            {
                tessellator.addVertexWithUV(d9, (double)p_147767_3_ - 0.01D, d18, d5, d8);
                tessellator.addVertexWithUV(d11, (double)p_147767_3_ - 0.01D, d18, d5, d6);
                tessellator.addVertexWithUV(d11, (double)p_147767_3_ - 0.01D, d17, d4, d6);
                tessellator.addVertexWithUV(d9, (double)p_147767_3_ - 0.01D, d17, d4, d8);
                tessellator.addVertexWithUV(d11, (double)p_147767_3_ - 0.01D, d18, d5, d8);
                tessellator.addVertexWithUV(d9, (double)p_147767_3_ - 0.01D, d18, d5, d6);
                tessellator.addVertexWithUV(d9, (double)p_147767_3_ - 0.01D, d17, d4, d6);
                tessellator.addVertexWithUV(d11, (double)p_147767_3_ - 0.01D, d17, d4, d8);
            }
            else
            {
                if (p_147767_3_ > 1 && this.field_147845_a.func_147437_c(p_147767_2_ - 1, p_147767_3_ - 1, p_147767_4_))
                {
                    tessellator.addVertexWithUV(d9, (double)p_147767_3_ - 0.01D, d18, d5, d7);
                    tessellator.addVertexWithUV(d10, (double)p_147767_3_ - 0.01D, d18, d5, d8);
                    tessellator.addVertexWithUV(d10, (double)p_147767_3_ - 0.01D, d17, d4, d8);
                    tessellator.addVertexWithUV(d9, (double)p_147767_3_ - 0.01D, d17, d4, d7);
                    tessellator.addVertexWithUV(d10, (double)p_147767_3_ - 0.01D, d18, d5, d7);
                    tessellator.addVertexWithUV(d9, (double)p_147767_3_ - 0.01D, d18, d5, d8);
                    tessellator.addVertexWithUV(d9, (double)p_147767_3_ - 0.01D, d17, d4, d8);
                    tessellator.addVertexWithUV(d10, (double)p_147767_3_ - 0.01D, d17, d4, d7);
                }

                if (p_147767_3_ > 1 && this.field_147845_a.func_147437_c(p_147767_2_ + 1, p_147767_3_ - 1, p_147767_4_))
                {
                    tessellator.addVertexWithUV(d10, (double)p_147767_3_ - 0.01D, d18, d5, d6);
                    tessellator.addVertexWithUV(d11, (double)p_147767_3_ - 0.01D, d18, d5, d7);
                    tessellator.addVertexWithUV(d11, (double)p_147767_3_ - 0.01D, d17, d4, d7);
                    tessellator.addVertexWithUV(d10, (double)p_147767_3_ - 0.01D, d17, d4, d6);
                    tessellator.addVertexWithUV(d11, (double)p_147767_3_ - 0.01D, d18, d5, d6);
                    tessellator.addVertexWithUV(d10, (double)p_147767_3_ - 0.01D, d18, d5, d7);
                    tessellator.addVertexWithUV(d10, (double)p_147767_3_ - 0.01D, d17, d4, d7);
                    tessellator.addVertexWithUV(d11, (double)p_147767_3_ - 0.01D, d17, d4, d6);
                }
            }
        }

        if ((!flag || !flag1) && (flag2 || flag3 || flag || flag1))
        {
            if (flag && !flag1)
            {
                tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1), d12, d21, d2);
                tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 0), d12, d21, d3);
                tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 0), d13, d0, d3);
                tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1), d13, d0, d2);
                tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1), d13, d21, d2);
                tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 0), d13, d21, d3);
                tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 0), d12, d0, d3);
                tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1), d12, d0, d2);

                if (!flag3 && !flag2)
                {
                    tessellator.addVertexWithUV(d15, (double)(p_147767_3_ + 1), d13, d4, d6);
                    tessellator.addVertexWithUV(d15, (double)(p_147767_3_ + 0), d13, d4, d8);
                    tessellator.addVertexWithUV(d16, (double)(p_147767_3_ + 0), d13, d5, d8);
                    tessellator.addVertexWithUV(d16, (double)(p_147767_3_ + 1), d13, d5, d6);
                    tessellator.addVertexWithUV(d16, (double)(p_147767_3_ + 1), d13, d4, d6);
                    tessellator.addVertexWithUV(d16, (double)(p_147767_3_ + 0), d13, d4, d8);
                    tessellator.addVertexWithUV(d15, (double)(p_147767_3_ + 0), d13, d5, d8);
                    tessellator.addVertexWithUV(d15, (double)(p_147767_3_ + 1), d13, d5, d6);
                }

                if (flag4 || p_147767_3_ < l - 1 && this.field_147845_a.func_147437_c(p_147767_2_, p_147767_3_ + 1, p_147767_4_ - 1))
                {
                    tessellator.addVertexWithUV(d15, (double)(p_147767_3_ + 1) + 0.005D, d12, d5, d6);
                    tessellator.addVertexWithUV(d15, (double)(p_147767_3_ + 1) + 0.005D, d13, d5, d7);
                    tessellator.addVertexWithUV(d16, (double)(p_147767_3_ + 1) + 0.005D, d13, d4, d7);
                    tessellator.addVertexWithUV(d16, (double)(p_147767_3_ + 1) + 0.005D, d12, d4, d6);
                    tessellator.addVertexWithUV(d15, (double)(p_147767_3_ + 1) + 0.005D, d13, d5, d6);
                    tessellator.addVertexWithUV(d15, (double)(p_147767_3_ + 1) + 0.005D, d12, d5, d7);
                    tessellator.addVertexWithUV(d16, (double)(p_147767_3_ + 1) + 0.005D, d12, d4, d7);
                    tessellator.addVertexWithUV(d16, (double)(p_147767_3_ + 1) + 0.005D, d13, d4, d6);
                }

                if (flag5 || p_147767_3_ > 1 && this.field_147845_a.func_147437_c(p_147767_2_, p_147767_3_ - 1, p_147767_4_ - 1))
                {
                    tessellator.addVertexWithUV(d15, (double)p_147767_3_ - 0.005D, d12, d5, d6);
                    tessellator.addVertexWithUV(d15, (double)p_147767_3_ - 0.005D, d13, d5, d7);
                    tessellator.addVertexWithUV(d16, (double)p_147767_3_ - 0.005D, d13, d4, d7);
                    tessellator.addVertexWithUV(d16, (double)p_147767_3_ - 0.005D, d12, d4, d6);
                    tessellator.addVertexWithUV(d15, (double)p_147767_3_ - 0.005D, d13, d5, d6);
                    tessellator.addVertexWithUV(d15, (double)p_147767_3_ - 0.005D, d12, d5, d7);
                    tessellator.addVertexWithUV(d16, (double)p_147767_3_ - 0.005D, d12, d4, d7);
                    tessellator.addVertexWithUV(d16, (double)p_147767_3_ - 0.005D, d13, d4, d6);
                }
            }
            else if (!flag && flag1)
            {
                tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1), d13, d0, d2);
                tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 0), d13, d0, d3);
                tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 0), d14, d1, d3);
                tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1), d14, d1, d2);
                tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1), d14, d0, d2);
                tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 0), d14, d0, d3);
                tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 0), d13, d1, d3);
                tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1), d13, d1, d2);

                if (!flag3 && !flag2)
                {
                    tessellator.addVertexWithUV(d16, (double)(p_147767_3_ + 1), d13, d4, d6);
                    tessellator.addVertexWithUV(d16, (double)(p_147767_3_ + 0), d13, d4, d8);
                    tessellator.addVertexWithUV(d15, (double)(p_147767_3_ + 0), d13, d5, d8);
                    tessellator.addVertexWithUV(d15, (double)(p_147767_3_ + 1), d13, d5, d6);
                    tessellator.addVertexWithUV(d15, (double)(p_147767_3_ + 1), d13, d4, d6);
                    tessellator.addVertexWithUV(d15, (double)(p_147767_3_ + 0), d13, d4, d8);
                    tessellator.addVertexWithUV(d16, (double)(p_147767_3_ + 0), d13, d5, d8);
                    tessellator.addVertexWithUV(d16, (double)(p_147767_3_ + 1), d13, d5, d6);
                }

                if (flag4 || p_147767_3_ < l - 1 && this.field_147845_a.func_147437_c(p_147767_2_, p_147767_3_ + 1, p_147767_4_ + 1))
                {
                    tessellator.addVertexWithUV(d15, (double)(p_147767_3_ + 1) + 0.005D, d13, d4, d7);
                    tessellator.addVertexWithUV(d15, (double)(p_147767_3_ + 1) + 0.005D, d14, d4, d8);
                    tessellator.addVertexWithUV(d16, (double)(p_147767_3_ + 1) + 0.005D, d14, d5, d8);
                    tessellator.addVertexWithUV(d16, (double)(p_147767_3_ + 1) + 0.005D, d13, d5, d7);
                    tessellator.addVertexWithUV(d15, (double)(p_147767_3_ + 1) + 0.005D, d14, d4, d7);
                    tessellator.addVertexWithUV(d15, (double)(p_147767_3_ + 1) + 0.005D, d13, d4, d8);
                    tessellator.addVertexWithUV(d16, (double)(p_147767_3_ + 1) + 0.005D, d13, d5, d8);
                    tessellator.addVertexWithUV(d16, (double)(p_147767_3_ + 1) + 0.005D, d14, d5, d7);
                }

                if (flag5 || p_147767_3_ > 1 && this.field_147845_a.func_147437_c(p_147767_2_, p_147767_3_ - 1, p_147767_4_ + 1))
                {
                    tessellator.addVertexWithUV(d15, (double)p_147767_3_ - 0.005D, d13, d4, d7);
                    tessellator.addVertexWithUV(d15, (double)p_147767_3_ - 0.005D, d14, d4, d8);
                    tessellator.addVertexWithUV(d16, (double)p_147767_3_ - 0.005D, d14, d5, d8);
                    tessellator.addVertexWithUV(d16, (double)p_147767_3_ - 0.005D, d13, d5, d7);
                    tessellator.addVertexWithUV(d15, (double)p_147767_3_ - 0.005D, d14, d4, d7);
                    tessellator.addVertexWithUV(d15, (double)p_147767_3_ - 0.005D, d13, d4, d8);
                    tessellator.addVertexWithUV(d16, (double)p_147767_3_ - 0.005D, d13, d5, d8);
                    tessellator.addVertexWithUV(d16, (double)p_147767_3_ - 0.005D, d14, d5, d7);
                }
            }
        }
        else
        {
            tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1), d14, d21, d2);
            tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 0), d14, d21, d3);
            tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 0), d12, d1, d3);
            tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1), d12, d1, d2);
            tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1), d12, d21, d2);
            tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 0), d12, d21, d3);
            tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 0), d14, d1, d3);
            tessellator.addVertexWithUV(d10, (double)(p_147767_3_ + 1), d14, d1, d2);

            if (flag4)
            {
                tessellator.addVertexWithUV(d16, (double)(p_147767_3_ + 1) + 0.005D, d14, d5, d8);
                tessellator.addVertexWithUV(d16, (double)(p_147767_3_ + 1) + 0.005D, d12, d5, d6);
                tessellator.addVertexWithUV(d15, (double)(p_147767_3_ + 1) + 0.005D, d12, d4, d6);
                tessellator.addVertexWithUV(d15, (double)(p_147767_3_ + 1) + 0.005D, d14, d4, d8);
                tessellator.addVertexWithUV(d16, (double)(p_147767_3_ + 1) + 0.005D, d12, d5, d8);
                tessellator.addVertexWithUV(d16, (double)(p_147767_3_ + 1) + 0.005D, d14, d5, d6);
                tessellator.addVertexWithUV(d15, (double)(p_147767_3_ + 1) + 0.005D, d14, d4, d6);
                tessellator.addVertexWithUV(d15, (double)(p_147767_3_ + 1) + 0.005D, d12, d4, d8);
            }
            else
            {
                if (p_147767_3_ < l - 1 && this.field_147845_a.func_147437_c(p_147767_2_, p_147767_3_ + 1, p_147767_4_ - 1))
                {
                    tessellator.addVertexWithUV(d15, (double)(p_147767_3_ + 1) + 0.005D, d12, d5, d6);
                    tessellator.addVertexWithUV(d15, (double)(p_147767_3_ + 1) + 0.005D, d13, d5, d7);
                    tessellator.addVertexWithUV(d16, (double)(p_147767_3_ + 1) + 0.005D, d13, d4, d7);
                    tessellator.addVertexWithUV(d16, (double)(p_147767_3_ + 1) + 0.005D, d12, d4, d6);
                    tessellator.addVertexWithUV(d15, (double)(p_147767_3_ + 1) + 0.005D, d13, d5, d6);
                    tessellator.addVertexWithUV(d15, (double)(p_147767_3_ + 1) + 0.005D, d12, d5, d7);
                    tessellator.addVertexWithUV(d16, (double)(p_147767_3_ + 1) + 0.005D, d12, d4, d7);
                    tessellator.addVertexWithUV(d16, (double)(p_147767_3_ + 1) + 0.005D, d13, d4, d6);
                }

                if (p_147767_3_ < l - 1 && this.field_147845_a.func_147437_c(p_147767_2_, p_147767_3_ + 1, p_147767_4_ + 1))
                {
                    tessellator.addVertexWithUV(d15, (double)(p_147767_3_ + 1) + 0.005D, d13, d4, d7);
                    tessellator.addVertexWithUV(d15, (double)(p_147767_3_ + 1) + 0.005D, d14, d4, d8);
                    tessellator.addVertexWithUV(d16, (double)(p_147767_3_ + 1) + 0.005D, d14, d5, d8);
                    tessellator.addVertexWithUV(d16, (double)(p_147767_3_ + 1) + 0.005D, d13, d5, d7);
                    tessellator.addVertexWithUV(d15, (double)(p_147767_3_ + 1) + 0.005D, d14, d4, d7);
                    tessellator.addVertexWithUV(d15, (double)(p_147767_3_ + 1) + 0.005D, d13, d4, d8);
                    tessellator.addVertexWithUV(d16, (double)(p_147767_3_ + 1) + 0.005D, d13, d5, d8);
                    tessellator.addVertexWithUV(d16, (double)(p_147767_3_ + 1) + 0.005D, d14, d5, d7);
                }
            }

            if (flag5)
            {
                tessellator.addVertexWithUV(d16, (double)p_147767_3_ - 0.005D, d14, d5, d8);
                tessellator.addVertexWithUV(d16, (double)p_147767_3_ - 0.005D, d12, d5, d6);
                tessellator.addVertexWithUV(d15, (double)p_147767_3_ - 0.005D, d12, d4, d6);
                tessellator.addVertexWithUV(d15, (double)p_147767_3_ - 0.005D, d14, d4, d8);
                tessellator.addVertexWithUV(d16, (double)p_147767_3_ - 0.005D, d12, d5, d8);
                tessellator.addVertexWithUV(d16, (double)p_147767_3_ - 0.005D, d14, d5, d6);
                tessellator.addVertexWithUV(d15, (double)p_147767_3_ - 0.005D, d14, d4, d6);
                tessellator.addVertexWithUV(d15, (double)p_147767_3_ - 0.005D, d12, d4, d8);
            }
            else
            {
                if (p_147767_3_ > 1 && this.field_147845_a.func_147437_c(p_147767_2_, p_147767_3_ - 1, p_147767_4_ - 1))
                {
                    tessellator.addVertexWithUV(d15, (double)p_147767_3_ - 0.005D, d12, d5, d6);
                    tessellator.addVertexWithUV(d15, (double)p_147767_3_ - 0.005D, d13, d5, d7);
                    tessellator.addVertexWithUV(d16, (double)p_147767_3_ - 0.005D, d13, d4, d7);
                    tessellator.addVertexWithUV(d16, (double)p_147767_3_ - 0.005D, d12, d4, d6);
                    tessellator.addVertexWithUV(d15, (double)p_147767_3_ - 0.005D, d13, d5, d6);
                    tessellator.addVertexWithUV(d15, (double)p_147767_3_ - 0.005D, d12, d5, d7);
                    tessellator.addVertexWithUV(d16, (double)p_147767_3_ - 0.005D, d12, d4, d7);
                    tessellator.addVertexWithUV(d16, (double)p_147767_3_ - 0.005D, d13, d4, d6);
                }

                if (p_147767_3_ > 1 && this.field_147845_a.func_147437_c(p_147767_2_, p_147767_3_ - 1, p_147767_4_ + 1))
                {
                    tessellator.addVertexWithUV(d15, (double)p_147767_3_ - 0.005D, d13, d4, d7);
                    tessellator.addVertexWithUV(d15, (double)p_147767_3_ - 0.005D, d14, d4, d8);
                    tessellator.addVertexWithUV(d16, (double)p_147767_3_ - 0.005D, d14, d5, d8);
                    tessellator.addVertexWithUV(d16, (double)p_147767_3_ - 0.005D, d13, d5, d7);
                    tessellator.addVertexWithUV(d15, (double)p_147767_3_ - 0.005D, d14, d4, d7);
                    tessellator.addVertexWithUV(d15, (double)p_147767_3_ - 0.005D, d13, d4, d8);
                    tessellator.addVertexWithUV(d16, (double)p_147767_3_ - 0.005D, d13, d5, d8);
                    tessellator.addVertexWithUV(d16, (double)p_147767_3_ - 0.005D, d14, d5, d7);
                }
            }
        }

        return true;
    }

    public boolean func_147746_l(Block p_147746_1_, int p_147746_2_, int p_147746_3_, int p_147746_4_)
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(p_147746_1_.func_149677_c(this.field_147845_a, p_147746_2_, p_147746_3_, p_147746_4_));
        int l = p_147746_1_.func_149720_d(this.field_147845_a, p_147746_2_, p_147746_3_, p_147746_4_);
        float f = (float)(l >> 16 & 255) / 255.0F;
        float f1 = (float)(l >> 8 & 255) / 255.0F;
        float f2 = (float)(l & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable)
        {
            float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
            float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            f = f3;
            f1 = f4;
            f2 = f5;
        }

        tessellator.setColorOpaque_F(f, f1, f2);
        double d1 = (double)p_147746_2_;
        double d2 = (double)p_147746_3_;
        double d0 = (double)p_147746_4_;
        long i1;

        if (p_147746_1_ == Blocks.tallgrass)
        {
            i1 = (long)(p_147746_2_ * 3129871) ^ (long)p_147746_4_ * 116129781L ^ (long)p_147746_3_;
            i1 = i1 * i1 * 42317861L + i1 * 11L;
            d1 += ((double)((float)(i1 >> 16 & 15L) / 15.0F) - 0.5D) * 0.5D;
            d2 += ((double)((float)(i1 >> 20 & 15L) / 15.0F) - 1.0D) * 0.2D;
            d0 += ((double)((float)(i1 >> 24 & 15L) / 15.0F) - 0.5D) * 0.5D;
        }
        else if (p_147746_1_ == Blocks.red_flower || p_147746_1_ == Blocks.yellow_flower)
        {
            i1 = (long)(p_147746_2_ * 3129871) ^ (long)p_147746_4_ * 116129781L ^ (long)p_147746_3_;
            i1 = i1 * i1 * 42317861L + i1 * 11L;
            d1 += ((double)((float)(i1 >> 16 & 15L) / 15.0F) - 0.5D) * 0.3D;
            d0 += ((double)((float)(i1 >> 24 & 15L) / 15.0F) - 0.5D) * 0.3D;
        }

        IIcon iicon = this.func_147787_a(p_147746_1_, 0, this.field_147845_a.getBlockMetadata(p_147746_2_, p_147746_3_, p_147746_4_));
        this.func_147765_a(iicon, d1, d2, d0, 1.0F);
        return true;
    }

    public boolean func_147774_a(BlockDoublePlant p_147774_1_, int p_147774_2_, int p_147774_3_, int p_147774_4_)
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(p_147774_1_.func_149677_c(this.field_147845_a, p_147774_2_, p_147774_3_, p_147774_4_));
        int l = p_147774_1_.func_149720_d(this.field_147845_a, p_147774_2_, p_147774_3_, p_147774_4_);
        float f = (float)(l >> 16 & 255) / 255.0F;
        float f1 = (float)(l >> 8 & 255) / 255.0F;
        float f2 = (float)(l & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable)
        {
            float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
            float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            f = f3;
            f1 = f4;
            f2 = f5;
        }

        tessellator.setColorOpaque_F(f, f1, f2);
        long j1 = (long)(p_147774_2_ * 3129871) ^ (long)p_147774_4_ * 116129781L;
        j1 = j1 * j1 * 42317861L + j1 * 11L;
        double d19 = (double)p_147774_2_;
        double d0 = (double)p_147774_3_;
        double d1 = (double)p_147774_4_;
        d19 += ((double)((float)(j1 >> 16 & 15L) / 15.0F) - 0.5D) * 0.3D;
        d1 += ((double)((float)(j1 >> 24 & 15L) / 15.0F) - 0.5D) * 0.3D;
        int i1 = this.field_147845_a.getBlockMetadata(p_147774_2_, p_147774_3_, p_147774_4_);
        boolean flag = false;
        boolean flag1 = BlockDoublePlant.func_149887_c(i1);
        int k1;

        if (flag1)
        {
            if (this.field_147845_a.func_147439_a(p_147774_2_, p_147774_3_ - 1, p_147774_4_) != p_147774_1_)
            {
                return false;
            }

            k1 = BlockDoublePlant.func_149890_d(this.field_147845_a.getBlockMetadata(p_147774_2_, p_147774_3_ - 1, p_147774_4_));
        }
        else
        {
            k1 = BlockDoublePlant.func_149890_d(i1);
        }

        IIcon iicon = p_147774_1_.func_149888_a(flag1, k1);
        this.func_147765_a(iicon, d19, d0, d1, 1.0F);

        if (flag1 && k1 == 0)
        {
            IIcon iicon1 = p_147774_1_.field_149891_b[0];
            double d2 = Math.cos((double)j1 * 0.8D) * Math.PI * 0.1D;
            double d3 = Math.cos(d2);
            double d4 = Math.sin(d2);
            double d5 = (double)iicon1.getMinU();
            double d6 = (double)iicon1.getMinV();
            double d7 = (double)iicon1.getMaxU();
            double d8 = (double)iicon1.getMaxV();
            double d9 = 0.3D;
            double d10 = -0.05D;
            double d11 = 0.5D + 0.3D * d3 - 0.5D * d4;
            double d12 = 0.5D + 0.5D * d3 + 0.3D * d4;
            double d13 = 0.5D + 0.3D * d3 + 0.5D * d4;
            double d14 = 0.5D + -0.5D * d3 + 0.3D * d4;
            double d15 = 0.5D + -0.05D * d3 + 0.5D * d4;
            double d16 = 0.5D + -0.5D * d3 + -0.05D * d4;
            double d17 = 0.5D + -0.05D * d3 - 0.5D * d4;
            double d18 = 0.5D + 0.5D * d3 + -0.05D * d4;
            tessellator.addVertexWithUV(d19 + d15, d0 + 1.0D, d1 + d16, d5, d8);
            tessellator.addVertexWithUV(d19 + d17, d0 + 1.0D, d1 + d18, d7, d8);
            tessellator.addVertexWithUV(d19 + d11, d0 + 0.0D, d1 + d12, d7, d6);
            tessellator.addVertexWithUV(d19 + d13, d0 + 0.0D, d1 + d14, d5, d6);
            IIcon iicon2 = p_147774_1_.field_149891_b[1];
            d5 = (double)iicon2.getMinU();
            d6 = (double)iicon2.getMinV();
            d7 = (double)iicon2.getMaxU();
            d8 = (double)iicon2.getMaxV();
            tessellator.addVertexWithUV(d19 + d17, d0 + 1.0D, d1 + d18, d5, d8);
            tessellator.addVertexWithUV(d19 + d15, d0 + 1.0D, d1 + d16, d7, d8);
            tessellator.addVertexWithUV(d19 + d13, d0 + 0.0D, d1 + d14, d7, d6);
            tessellator.addVertexWithUV(d19 + d11, d0 + 0.0D, d1 + d12, d5, d6);
        }

        return true;
    }

    public boolean func_147724_m(Block p_147724_1_, int p_147724_2_, int p_147724_3_, int p_147724_4_)
    {
        BlockStem blockstem = (BlockStem)p_147724_1_;
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(blockstem.func_149677_c(this.field_147845_a, p_147724_2_, p_147724_3_, p_147724_4_));
        int l = blockstem.func_149720_d(this.field_147845_a, p_147724_2_, p_147724_3_, p_147724_4_);
        float f = (float)(l >> 16 & 255) / 255.0F;
        float f1 = (float)(l >> 8 & 255) / 255.0F;
        float f2 = (float)(l & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable)
        {
            float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
            float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            f = f3;
            f1 = f4;
            f2 = f5;
        }

        tessellator.setColorOpaque_F(f, f1, f2);
        blockstem.func_149719_a(this.field_147845_a, p_147724_2_, p_147724_3_, p_147724_4_);
        int i1 = blockstem.func_149873_e(this.field_147845_a, p_147724_2_, p_147724_3_, p_147724_4_);

        if (i1 < 0)
        {
            this.func_147730_a(blockstem, this.field_147845_a.getBlockMetadata(p_147724_2_, p_147724_3_, p_147724_4_), this.field_147857_k, (double)p_147724_2_, (double)((float)p_147724_3_ - 0.0625F), (double)p_147724_4_);
        }
        else
        {
            this.func_147730_a(blockstem, this.field_147845_a.getBlockMetadata(p_147724_2_, p_147724_3_, p_147724_4_), 0.5D, (double)p_147724_2_, (double)((float)p_147724_3_ - 0.0625F), (double)p_147724_4_);
            this.func_147740_a(blockstem, this.field_147845_a.getBlockMetadata(p_147724_2_, p_147724_3_, p_147724_4_), i1, this.field_147857_k, (double)p_147724_2_, (double)((float)p_147724_3_ - 0.0625F), (double)p_147724_4_);
        }

        return true;
    }

    public boolean func_147796_n(Block p_147796_1_, int p_147796_2_, int p_147796_3_, int p_147796_4_)
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(p_147796_1_.func_149677_c(this.field_147845_a, p_147796_2_, p_147796_3_, p_147796_4_));
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        this.func_147795_a(p_147796_1_, this.field_147845_a.getBlockMetadata(p_147796_2_, p_147796_3_, p_147796_4_), (double)p_147796_2_, (double)((float)p_147796_3_ - 0.0625F), (double)p_147796_4_);
        return true;
    }

    public void func_147747_a(Block p_147747_1_, double p_147747_2_, double p_147747_4_, double p_147747_6_, double p_147747_8_, double p_147747_10_, int p_147747_12_)
    {
        Tessellator tessellator = Tessellator.instance;
        IIcon iicon = this.func_147787_a(p_147747_1_, 0, p_147747_12_);

        if (this.func_147744_b())
        {
            iicon = this.field_147840_d;
        }

        double d5 = (double)iicon.getMinU();
        double d6 = (double)iicon.getMinV();
        double d7 = (double)iicon.getMaxU();
        double d8 = (double)iicon.getMaxV();
        double d9 = (double)iicon.getInterpolatedU(7.0D);
        double d10 = (double)iicon.getInterpolatedV(6.0D);
        double d11 = (double)iicon.getInterpolatedU(9.0D);
        double d12 = (double)iicon.getInterpolatedV(8.0D);
        double d13 = (double)iicon.getInterpolatedU(7.0D);
        double d14 = (double)iicon.getInterpolatedV(13.0D);
        double d15 = (double)iicon.getInterpolatedU(9.0D);
        double d16 = (double)iicon.getInterpolatedV(15.0D);
        p_147747_2_ += 0.5D;
        p_147747_6_ += 0.5D;
        double d17 = p_147747_2_ - 0.5D;
        double d18 = p_147747_2_ + 0.5D;
        double d19 = p_147747_6_ - 0.5D;
        double d20 = p_147747_6_ + 0.5D;
        double d21 = 0.0625D;
        double d22 = 0.625D;
        tessellator.addVertexWithUV(p_147747_2_ + p_147747_8_ * (1.0D - d22) - d21, p_147747_4_ + d22, p_147747_6_ + p_147747_10_ * (1.0D - d22) - d21, d9, d10);
        tessellator.addVertexWithUV(p_147747_2_ + p_147747_8_ * (1.0D - d22) - d21, p_147747_4_ + d22, p_147747_6_ + p_147747_10_ * (1.0D - d22) + d21, d9, d12);
        tessellator.addVertexWithUV(p_147747_2_ + p_147747_8_ * (1.0D - d22) + d21, p_147747_4_ + d22, p_147747_6_ + p_147747_10_ * (1.0D - d22) + d21, d11, d12);
        tessellator.addVertexWithUV(p_147747_2_ + p_147747_8_ * (1.0D - d22) + d21, p_147747_4_ + d22, p_147747_6_ + p_147747_10_ * (1.0D - d22) - d21, d11, d10);
        tessellator.addVertexWithUV(p_147747_2_ + d21 + p_147747_8_, p_147747_4_, p_147747_6_ - d21 + p_147747_10_, d15, d14);
        tessellator.addVertexWithUV(p_147747_2_ + d21 + p_147747_8_, p_147747_4_, p_147747_6_ + d21 + p_147747_10_, d15, d16);
        tessellator.addVertexWithUV(p_147747_2_ - d21 + p_147747_8_, p_147747_4_, p_147747_6_ + d21 + p_147747_10_, d13, d16);
        tessellator.addVertexWithUV(p_147747_2_ - d21 + p_147747_8_, p_147747_4_, p_147747_6_ - d21 + p_147747_10_, d13, d14);
        tessellator.addVertexWithUV(p_147747_2_ - d21, p_147747_4_ + 1.0D, d19, d5, d6);
        tessellator.addVertexWithUV(p_147747_2_ - d21 + p_147747_8_, p_147747_4_ + 0.0D, d19 + p_147747_10_, d5, d8);
        tessellator.addVertexWithUV(p_147747_2_ - d21 + p_147747_8_, p_147747_4_ + 0.0D, d20 + p_147747_10_, d7, d8);
        tessellator.addVertexWithUV(p_147747_2_ - d21, p_147747_4_ + 1.0D, d20, d7, d6);
        tessellator.addVertexWithUV(p_147747_2_ + d21, p_147747_4_ + 1.0D, d20, d5, d6);
        tessellator.addVertexWithUV(p_147747_2_ + p_147747_8_ + d21, p_147747_4_ + 0.0D, d20 + p_147747_10_, d5, d8);
        tessellator.addVertexWithUV(p_147747_2_ + p_147747_8_ + d21, p_147747_4_ + 0.0D, d19 + p_147747_10_, d7, d8);
        tessellator.addVertexWithUV(p_147747_2_ + d21, p_147747_4_ + 1.0D, d19, d7, d6);
        tessellator.addVertexWithUV(d17, p_147747_4_ + 1.0D, p_147747_6_ + d21, d5, d6);
        tessellator.addVertexWithUV(d17 + p_147747_8_, p_147747_4_ + 0.0D, p_147747_6_ + d21 + p_147747_10_, d5, d8);
        tessellator.addVertexWithUV(d18 + p_147747_8_, p_147747_4_ + 0.0D, p_147747_6_ + d21 + p_147747_10_, d7, d8);
        tessellator.addVertexWithUV(d18, p_147747_4_ + 1.0D, p_147747_6_ + d21, d7, d6);
        tessellator.addVertexWithUV(d18, p_147747_4_ + 1.0D, p_147747_6_ - d21, d5, d6);
        tessellator.addVertexWithUV(d18 + p_147747_8_, p_147747_4_ + 0.0D, p_147747_6_ - d21 + p_147747_10_, d5, d8);
        tessellator.addVertexWithUV(d17 + p_147747_8_, p_147747_4_ + 0.0D, p_147747_6_ - d21 + p_147747_10_, d7, d8);
        tessellator.addVertexWithUV(d17, p_147747_4_ + 1.0D, p_147747_6_ - d21, d7, d6);
    }

    public void func_147765_a(IIcon p_147765_1_, double p_147765_2_, double p_147765_4_, double p_147765_6_, float p_147765_8_)
    {
        Tessellator tessellator = Tessellator.instance;

        if (this.func_147744_b())
        {
            p_147765_1_ = this.field_147840_d;
        }

        double d3 = (double)p_147765_1_.getMinU();
        double d4 = (double)p_147765_1_.getMinV();
        double d5 = (double)p_147765_1_.getMaxU();
        double d6 = (double)p_147765_1_.getMaxV();
        double d7 = 0.45D * (double)p_147765_8_;
        double d8 = p_147765_2_ + 0.5D - d7;
        double d9 = p_147765_2_ + 0.5D + d7;
        double d10 = p_147765_6_ + 0.5D - d7;
        double d11 = p_147765_6_ + 0.5D + d7;
        tessellator.addVertexWithUV(d8, p_147765_4_ + (double)p_147765_8_, d10, d3, d4);
        tessellator.addVertexWithUV(d8, p_147765_4_ + 0.0D, d10, d3, d6);
        tessellator.addVertexWithUV(d9, p_147765_4_ + 0.0D, d11, d5, d6);
        tessellator.addVertexWithUV(d9, p_147765_4_ + (double)p_147765_8_, d11, d5, d4);
        tessellator.addVertexWithUV(d9, p_147765_4_ + (double)p_147765_8_, d11, d3, d4);
        tessellator.addVertexWithUV(d9, p_147765_4_ + 0.0D, d11, d3, d6);
        tessellator.addVertexWithUV(d8, p_147765_4_ + 0.0D, d10, d5, d6);
        tessellator.addVertexWithUV(d8, p_147765_4_ + (double)p_147765_8_, d10, d5, d4);
        tessellator.addVertexWithUV(d8, p_147765_4_ + (double)p_147765_8_, d11, d3, d4);
        tessellator.addVertexWithUV(d8, p_147765_4_ + 0.0D, d11, d3, d6);
        tessellator.addVertexWithUV(d9, p_147765_4_ + 0.0D, d10, d5, d6);
        tessellator.addVertexWithUV(d9, p_147765_4_ + (double)p_147765_8_, d10, d5, d4);
        tessellator.addVertexWithUV(d9, p_147765_4_ + (double)p_147765_8_, d10, d3, d4);
        tessellator.addVertexWithUV(d9, p_147765_4_ + 0.0D, d10, d3, d6);
        tessellator.addVertexWithUV(d8, p_147765_4_ + 0.0D, d11, d5, d6);
        tessellator.addVertexWithUV(d8, p_147765_4_ + (double)p_147765_8_, d11, d5, d4);
    }

    public void func_147730_a(Block p_147730_1_, int p_147730_2_, double p_147730_3_, double p_147730_5_, double p_147730_7_, double p_147730_9_)
    {
        Tessellator tessellator = Tessellator.instance;
        IIcon iicon = this.func_147787_a(p_147730_1_, 0, p_147730_2_);

        if (this.func_147744_b())
        {
            iicon = this.field_147840_d;
        }

        double d4 = (double)iicon.getMinU();
        double d5 = (double)iicon.getMinV();
        double d6 = (double)iicon.getMaxU();
        double d7 = (double)iicon.getInterpolatedV(p_147730_3_ * 16.0D);
        double d8 = p_147730_5_ + 0.5D - 0.44999998807907104D;
        double d9 = p_147730_5_ + 0.5D + 0.44999998807907104D;
        double d10 = p_147730_9_ + 0.5D - 0.44999998807907104D;
        double d11 = p_147730_9_ + 0.5D + 0.44999998807907104D;
        tessellator.addVertexWithUV(d8, p_147730_7_ + p_147730_3_, d10, d4, d5);
        tessellator.addVertexWithUV(d8, p_147730_7_ + 0.0D, d10, d4, d7);
        tessellator.addVertexWithUV(d9, p_147730_7_ + 0.0D, d11, d6, d7);
        tessellator.addVertexWithUV(d9, p_147730_7_ + p_147730_3_, d11, d6, d5);
        tessellator.addVertexWithUV(d9, p_147730_7_ + p_147730_3_, d11, d6, d5);
        tessellator.addVertexWithUV(d9, p_147730_7_ + 0.0D, d11, d6, d7);
        tessellator.addVertexWithUV(d8, p_147730_7_ + 0.0D, d10, d4, d7);
        tessellator.addVertexWithUV(d8, p_147730_7_ + p_147730_3_, d10, d4, d5);
        tessellator.addVertexWithUV(d8, p_147730_7_ + p_147730_3_, d11, d4, d5);
        tessellator.addVertexWithUV(d8, p_147730_7_ + 0.0D, d11, d4, d7);
        tessellator.addVertexWithUV(d9, p_147730_7_ + 0.0D, d10, d6, d7);
        tessellator.addVertexWithUV(d9, p_147730_7_ + p_147730_3_, d10, d6, d5);
        tessellator.addVertexWithUV(d9, p_147730_7_ + p_147730_3_, d10, d6, d5);
        tessellator.addVertexWithUV(d9, p_147730_7_ + 0.0D, d10, d6, d7);
        tessellator.addVertexWithUV(d8, p_147730_7_ + 0.0D, d11, d4, d7);
        tessellator.addVertexWithUV(d8, p_147730_7_ + p_147730_3_, d11, d4, d5);
    }

    public boolean func_147783_o(Block p_147783_1_, int p_147783_2_, int p_147783_3_, int p_147783_4_)
    {
        Tessellator tessellator = Tessellator.instance;
        IIcon iicon = this.func_147777_a(p_147783_1_, 1);

        if (this.func_147744_b())
        {
            iicon = this.field_147840_d;
        }

        float f = 0.015625F;
        double d0 = (double)iicon.getMinU();
        double d1 = (double)iicon.getMinV();
        double d2 = (double)iicon.getMaxU();
        double d3 = (double)iicon.getMaxV();
        long l = (long)(p_147783_2_ * 3129871) ^ (long)p_147783_4_ * 116129781L ^ (long)p_147783_3_;
        l = l * l * 42317861L + l * 11L;
        int i1 = (int)(l >> 16 & 3L);
        tessellator.setBrightness(p_147783_1_.func_149677_c(this.field_147845_a, p_147783_2_, p_147783_3_, p_147783_4_));
        float f1 = (float)p_147783_2_ + 0.5F;
        float f2 = (float)p_147783_4_ + 0.5F;
        float f3 = (float)(i1 & 1) * 0.5F * (float)(1 - i1 / 2 % 2 * 2);
        float f4 = (float)(i1 + 1 & 1) * 0.5F * (float)(1 - (i1 + 1) / 2 % 2 * 2);
        tessellator.setColorOpaque_I(p_147783_1_.func_149635_D());
        tessellator.addVertexWithUV((double)(f1 + f3 - f4), (double)((float)p_147783_3_ + f), (double)(f2 + f3 + f4), d0, d1);
        tessellator.addVertexWithUV((double)(f1 + f3 + f4), (double)((float)p_147783_3_ + f), (double)(f2 - f3 + f4), d2, d1);
        tessellator.addVertexWithUV((double)(f1 - f3 + f4), (double)((float)p_147783_3_ + f), (double)(f2 - f3 - f4), d2, d3);
        tessellator.addVertexWithUV((double)(f1 - f3 - f4), (double)((float)p_147783_3_ + f), (double)(f2 + f3 - f4), d0, d3);
        tessellator.setColorOpaque_I((p_147783_1_.func_149635_D() & 16711422) >> 1);
        tessellator.addVertexWithUV((double)(f1 - f3 - f4), (double)((float)p_147783_3_ + f), (double)(f2 + f3 - f4), d0, d3);
        tessellator.addVertexWithUV((double)(f1 - f3 + f4), (double)((float)p_147783_3_ + f), (double)(f2 - f3 - f4), d2, d3);
        tessellator.addVertexWithUV((double)(f1 + f3 + f4), (double)((float)p_147783_3_ + f), (double)(f2 - f3 + f4), d2, d1);
        tessellator.addVertexWithUV((double)(f1 + f3 - f4), (double)((float)p_147783_3_ + f), (double)(f2 + f3 + f4), d0, d1);
        return true;
    }

    public void func_147740_a(BlockStem p_147740_1_, int p_147740_2_, int p_147740_3_, double p_147740_4_, double p_147740_6_, double p_147740_8_, double p_147740_10_)
    {
        Tessellator tessellator = Tessellator.instance;
        IIcon iicon = p_147740_1_.func_149872_i();

        if (this.func_147744_b())
        {
            iicon = this.field_147840_d;
        }

        double d4 = (double)iicon.getMinU();
        double d5 = (double)iicon.getMinV();
        double d6 = (double)iicon.getMaxU();
        double d7 = (double)iicon.getMaxV();
        double d8 = p_147740_6_ + 0.5D - 0.5D;
        double d9 = p_147740_6_ + 0.5D + 0.5D;
        double d10 = p_147740_10_ + 0.5D - 0.5D;
        double d11 = p_147740_10_ + 0.5D + 0.5D;
        double d12 = p_147740_6_ + 0.5D;
        double d13 = p_147740_10_ + 0.5D;

        if ((p_147740_3_ + 1) / 2 % 2 == 1)
        {
            double d14 = d6;
            d6 = d4;
            d4 = d14;
        }

        if (p_147740_3_ < 2)
        {
            tessellator.addVertexWithUV(d8, p_147740_8_ + p_147740_4_, d13, d4, d5);
            tessellator.addVertexWithUV(d8, p_147740_8_ + 0.0D, d13, d4, d7);
            tessellator.addVertexWithUV(d9, p_147740_8_ + 0.0D, d13, d6, d7);
            tessellator.addVertexWithUV(d9, p_147740_8_ + p_147740_4_, d13, d6, d5);
            tessellator.addVertexWithUV(d9, p_147740_8_ + p_147740_4_, d13, d6, d5);
            tessellator.addVertexWithUV(d9, p_147740_8_ + 0.0D, d13, d6, d7);
            tessellator.addVertexWithUV(d8, p_147740_8_ + 0.0D, d13, d4, d7);
            tessellator.addVertexWithUV(d8, p_147740_8_ + p_147740_4_, d13, d4, d5);
        }
        else
        {
            tessellator.addVertexWithUV(d12, p_147740_8_ + p_147740_4_, d11, d4, d5);
            tessellator.addVertexWithUV(d12, p_147740_8_ + 0.0D, d11, d4, d7);
            tessellator.addVertexWithUV(d12, p_147740_8_ + 0.0D, d10, d6, d7);
            tessellator.addVertexWithUV(d12, p_147740_8_ + p_147740_4_, d10, d6, d5);
            tessellator.addVertexWithUV(d12, p_147740_8_ + p_147740_4_, d10, d6, d5);
            tessellator.addVertexWithUV(d12, p_147740_8_ + 0.0D, d10, d6, d7);
            tessellator.addVertexWithUV(d12, p_147740_8_ + 0.0D, d11, d4, d7);
            tessellator.addVertexWithUV(d12, p_147740_8_ + p_147740_4_, d11, d4, d5);
        }
    }

    public void func_147795_a(Block p_147795_1_, int p_147795_2_, double p_147795_3_, double p_147795_5_, double p_147795_7_)
    {
        Tessellator tessellator = Tessellator.instance;
        IIcon iicon = this.func_147787_a(p_147795_1_, 0, p_147795_2_);

        if (this.func_147744_b())
        {
            iicon = this.field_147840_d;
        }

        double d3 = (double)iicon.getMinU();
        double d4 = (double)iicon.getMinV();
        double d5 = (double)iicon.getMaxU();
        double d6 = (double)iicon.getMaxV();
        double d7 = p_147795_3_ + 0.5D - 0.25D;
        double d8 = p_147795_3_ + 0.5D + 0.25D;
        double d9 = p_147795_7_ + 0.5D - 0.5D;
        double d10 = p_147795_7_ + 0.5D + 0.5D;
        tessellator.addVertexWithUV(d7, p_147795_5_ + 1.0D, d9, d3, d4);
        tessellator.addVertexWithUV(d7, p_147795_5_ + 0.0D, d9, d3, d6);
        tessellator.addVertexWithUV(d7, p_147795_5_ + 0.0D, d10, d5, d6);
        tessellator.addVertexWithUV(d7, p_147795_5_ + 1.0D, d10, d5, d4);
        tessellator.addVertexWithUV(d7, p_147795_5_ + 1.0D, d10, d3, d4);
        tessellator.addVertexWithUV(d7, p_147795_5_ + 0.0D, d10, d3, d6);
        tessellator.addVertexWithUV(d7, p_147795_5_ + 0.0D, d9, d5, d6);
        tessellator.addVertexWithUV(d7, p_147795_5_ + 1.0D, d9, d5, d4);
        tessellator.addVertexWithUV(d8, p_147795_5_ + 1.0D, d10, d3, d4);
        tessellator.addVertexWithUV(d8, p_147795_5_ + 0.0D, d10, d3, d6);
        tessellator.addVertexWithUV(d8, p_147795_5_ + 0.0D, d9, d5, d6);
        tessellator.addVertexWithUV(d8, p_147795_5_ + 1.0D, d9, d5, d4);
        tessellator.addVertexWithUV(d8, p_147795_5_ + 1.0D, d9, d3, d4);
        tessellator.addVertexWithUV(d8, p_147795_5_ + 0.0D, d9, d3, d6);
        tessellator.addVertexWithUV(d8, p_147795_5_ + 0.0D, d10, d5, d6);
        tessellator.addVertexWithUV(d8, p_147795_5_ + 1.0D, d10, d5, d4);
        d7 = p_147795_3_ + 0.5D - 0.5D;
        d8 = p_147795_3_ + 0.5D + 0.5D;
        d9 = p_147795_7_ + 0.5D - 0.25D;
        d10 = p_147795_7_ + 0.5D + 0.25D;
        tessellator.addVertexWithUV(d7, p_147795_5_ + 1.0D, d9, d3, d4);
        tessellator.addVertexWithUV(d7, p_147795_5_ + 0.0D, d9, d3, d6);
        tessellator.addVertexWithUV(d8, p_147795_5_ + 0.0D, d9, d5, d6);
        tessellator.addVertexWithUV(d8, p_147795_5_ + 1.0D, d9, d5, d4);
        tessellator.addVertexWithUV(d8, p_147795_5_ + 1.0D, d9, d3, d4);
        tessellator.addVertexWithUV(d8, p_147795_5_ + 0.0D, d9, d3, d6);
        tessellator.addVertexWithUV(d7, p_147795_5_ + 0.0D, d9, d5, d6);
        tessellator.addVertexWithUV(d7, p_147795_5_ + 1.0D, d9, d5, d4);
        tessellator.addVertexWithUV(d8, p_147795_5_ + 1.0D, d10, d3, d4);
        tessellator.addVertexWithUV(d8, p_147795_5_ + 0.0D, d10, d3, d6);
        tessellator.addVertexWithUV(d7, p_147795_5_ + 0.0D, d10, d5, d6);
        tessellator.addVertexWithUV(d7, p_147795_5_ + 1.0D, d10, d5, d4);
        tessellator.addVertexWithUV(d7, p_147795_5_ + 1.0D, d10, d3, d4);
        tessellator.addVertexWithUV(d7, p_147795_5_ + 0.0D, d10, d3, d6);
        tessellator.addVertexWithUV(d8, p_147795_5_ + 0.0D, d10, d5, d6);
        tessellator.addVertexWithUV(d8, p_147795_5_ + 1.0D, d10, d5, d4);
    }

    public boolean func_147721_p(Block p_147721_1_, int p_147721_2_, int p_147721_3_, int p_147721_4_)
    {
        Tessellator tessellator = Tessellator.instance;
        int l = p_147721_1_.func_149720_d(this.field_147845_a, p_147721_2_, p_147721_3_, p_147721_4_);
        float f = (float)(l >> 16 & 255) / 255.0F;
        float f1 = (float)(l >> 8 & 255) / 255.0F;
        float f2 = (float)(l & 255) / 255.0F;
        boolean flag = p_147721_1_.func_149646_a(this.field_147845_a, p_147721_2_, p_147721_3_ + 1, p_147721_4_, 1);
        boolean flag1 = p_147721_1_.func_149646_a(this.field_147845_a, p_147721_2_, p_147721_3_ - 1, p_147721_4_, 0);
        boolean[] aboolean = new boolean[] {p_147721_1_.func_149646_a(this.field_147845_a, p_147721_2_, p_147721_3_, p_147721_4_ - 1, 2), p_147721_1_.func_149646_a(this.field_147845_a, p_147721_2_, p_147721_3_, p_147721_4_ + 1, 3), p_147721_1_.func_149646_a(this.field_147845_a, p_147721_2_ - 1, p_147721_3_, p_147721_4_, 4), p_147721_1_.func_149646_a(this.field_147845_a, p_147721_2_ + 1, p_147721_3_, p_147721_4_, 5)};

        if (!flag && !flag1 && !aboolean[0] && !aboolean[1] && !aboolean[2] && !aboolean[3])
        {
            return false;
        }
        else
        {
            boolean flag2 = false;
            float f3 = 0.5F;
            float f4 = 1.0F;
            float f5 = 0.8F;
            float f6 = 0.6F;
            double d0 = 0.0D;
            double d1 = 1.0D;
            Material material = p_147721_1_.func_149688_o();
            int i1 = this.field_147845_a.getBlockMetadata(p_147721_2_, p_147721_3_, p_147721_4_);
            double d2 = (double)this.func_147729_a(p_147721_2_, p_147721_3_, p_147721_4_, material);
            double d3 = (double)this.func_147729_a(p_147721_2_, p_147721_3_, p_147721_4_ + 1, material);
            double d4 = (double)this.func_147729_a(p_147721_2_ + 1, p_147721_3_, p_147721_4_ + 1, material);
            double d5 = (double)this.func_147729_a(p_147721_2_ + 1, p_147721_3_, p_147721_4_, material);
            double d6 = 0.0010000000474974513D;
            float f11;
            float f10;
            float f9;

            if (this.field_147837_f || flag)
            {
                flag2 = true;
                IIcon iicon = this.func_147787_a(p_147721_1_, 1, i1);
                float f7 = (float)BlockLiquid.func_149802_a(this.field_147845_a, p_147721_2_, p_147721_3_, p_147721_4_, material);

                if (f7 > -999.0F)
                {
                    iicon = this.func_147787_a(p_147721_1_, 2, i1);
                }

                d2 -= d6;
                d3 -= d6;
                d4 -= d6;
                d5 -= d6;
                double d8;
                double d7;
                double d12;
                double d10;
                double d16;
                double d14;
                double d20;
                double d18;

                if (f7 < -999.0F)
                {
                    d7 = (double)iicon.getInterpolatedU(0.0D);
                    d14 = (double)iicon.getInterpolatedV(0.0D);
                    d8 = d7;
                    d16 = (double)iicon.getInterpolatedV(16.0D);
                    d10 = (double)iicon.getInterpolatedU(16.0D);
                    d18 = d16;
                    d12 = d10;
                    d20 = d14;
                }
                else
                {
                    f9 = MathHelper.sin(f7) * 0.25F;
                    f10 = MathHelper.cos(f7) * 0.25F;
                    f11 = 8.0F;
                    d7 = (double)iicon.getInterpolatedU((double)(8.0F + (-f10 - f9) * 16.0F));
                    d14 = (double)iicon.getInterpolatedV((double)(8.0F + (-f10 + f9) * 16.0F));
                    d8 = (double)iicon.getInterpolatedU((double)(8.0F + (-f10 + f9) * 16.0F));
                    d16 = (double)iicon.getInterpolatedV((double)(8.0F + (f10 + f9) * 16.0F));
                    d10 = (double)iicon.getInterpolatedU((double)(8.0F + (f10 + f9) * 16.0F));
                    d18 = (double)iicon.getInterpolatedV((double)(8.0F + (f10 - f9) * 16.0F));
                    d12 = (double)iicon.getInterpolatedU((double)(8.0F + (f10 - f9) * 16.0F));
                    d20 = (double)iicon.getInterpolatedV((double)(8.0F + (-f10 - f9) * 16.0F));
                }

                tessellator.setBrightness(p_147721_1_.func_149677_c(this.field_147845_a, p_147721_2_, p_147721_3_, p_147721_4_));
                tessellator.setColorOpaque_F(f4 * f, f4 * f1, f4 * f2);
                tessellator.addVertexWithUV((double)(p_147721_2_ + 0), (double)p_147721_3_ + d2, (double)(p_147721_4_ + 0), d7, d14);
                tessellator.addVertexWithUV((double)(p_147721_2_ + 0), (double)p_147721_3_ + d3, (double)(p_147721_4_ + 1), d8, d16);
                tessellator.addVertexWithUV((double)(p_147721_2_ + 1), (double)p_147721_3_ + d4, (double)(p_147721_4_ + 1), d10, d18);
                tessellator.addVertexWithUV((double)(p_147721_2_ + 1), (double)p_147721_3_ + d5, (double)(p_147721_4_ + 0), d12, d20);
            }

            if (this.field_147837_f || flag1)
            {
                tessellator.setBrightness(p_147721_1_.func_149677_c(this.field_147845_a, p_147721_2_, p_147721_3_ - 1, p_147721_4_));
                tessellator.setColorOpaque_F(f3, f3, f3);
                this.func_147768_a(p_147721_1_, (double)p_147721_2_, (double)p_147721_3_ + d6, (double)p_147721_4_, this.func_147777_a(p_147721_1_, 0));
                flag2 = true;
            }

            for (int k1 = 0; k1 < 4; ++k1)
            {
                int l1 = p_147721_2_;
                int j1 = p_147721_4_;

                if (k1 == 0)
                {
                    j1 = p_147721_4_ - 1;
                }

                if (k1 == 1)
                {
                    ++j1;
                }

                if (k1 == 2)
                {
                    l1 = p_147721_2_ - 1;
                }

                if (k1 == 3)
                {
                    ++l1;
                }

                IIcon iicon1 = this.func_147787_a(p_147721_1_, k1 + 2, i1);

                if (this.field_147837_f || aboolean[k1])
                {
                    double d9;
                    double d13;
                    double d11;
                    double d17;
                    double d15;
                    double d19;

                    if (k1 == 0)
                    {
                        d9 = d2;
                        d11 = d5;
                        d13 = (double)p_147721_2_;
                        d17 = (double)(p_147721_2_ + 1);
                        d15 = (double)p_147721_4_ + d6;
                        d19 = (double)p_147721_4_ + d6;
                    }
                    else if (k1 == 1)
                    {
                        d9 = d4;
                        d11 = d3;
                        d13 = (double)(p_147721_2_ + 1);
                        d17 = (double)p_147721_2_;
                        d15 = (double)(p_147721_4_ + 1) - d6;
                        d19 = (double)(p_147721_4_ + 1) - d6;
                    }
                    else if (k1 == 2)
                    {
                        d9 = d3;
                        d11 = d2;
                        d13 = (double)p_147721_2_ + d6;
                        d17 = (double)p_147721_2_ + d6;
                        d15 = (double)(p_147721_4_ + 1);
                        d19 = (double)p_147721_4_;
                    }
                    else
                    {
                        d9 = d5;
                        d11 = d4;
                        d13 = (double)(p_147721_2_ + 1) - d6;
                        d17 = (double)(p_147721_2_ + 1) - d6;
                        d15 = (double)p_147721_4_;
                        d19 = (double)(p_147721_4_ + 1);
                    }

                    flag2 = true;
                    float f8 = iicon1.getInterpolatedU(0.0D);
                    f9 = iicon1.getInterpolatedU(8.0D);
                    f10 = iicon1.getInterpolatedV((1.0D - d9) * 16.0D * 0.5D);
                    f11 = iicon1.getInterpolatedV((1.0D - d11) * 16.0D * 0.5D);
                    float f12 = iicon1.getInterpolatedV(8.0D);
                    tessellator.setBrightness(p_147721_1_.func_149677_c(this.field_147845_a, l1, p_147721_3_, j1));
                    float f13 = 1.0F;
                    f13 *= k1 < 2 ? f5 : f6;
                    tessellator.setColorOpaque_F(f4 * f13 * f, f4 * f13 * f1, f4 * f13 * f2);
                    tessellator.addVertexWithUV(d13, (double)p_147721_3_ + d9, d15, (double)f8, (double)f10);
                    tessellator.addVertexWithUV(d17, (double)p_147721_3_ + d11, d19, (double)f9, (double)f11);
                    tessellator.addVertexWithUV(d17, (double)(p_147721_3_ + 0), d19, (double)f9, (double)f12);
                    tessellator.addVertexWithUV(d13, (double)(p_147721_3_ + 0), d15, (double)f8, (double)f12);
                }
            }

            this.field_147855_j = d0;
            this.field_147857_k = d1;
            return flag2;
        }
    }

    public float func_147729_a(int p_147729_1_, int p_147729_2_, int p_147729_3_, Material p_147729_4_)
    {
        int l = 0;
        float f = 0.0F;

        for (int i1 = 0; i1 < 4; ++i1)
        {
            int j1 = p_147729_1_ - (i1 & 1);
            int k1 = p_147729_3_ - (i1 >> 1 & 1);

            if (this.field_147845_a.func_147439_a(j1, p_147729_2_ + 1, k1).func_149688_o() == p_147729_4_)
            {
                return 1.0F;
            }

            Material material1 = this.field_147845_a.func_147439_a(j1, p_147729_2_, k1).func_149688_o();

            if (material1 == p_147729_4_)
            {
                int l1 = this.field_147845_a.getBlockMetadata(j1, p_147729_2_, k1);

                if (l1 >= 8 || l1 == 0)
                {
                    f += BlockLiquid.func_149801_b(l1) * 10.0F;
                    l += 10;
                }

                f += BlockLiquid.func_149801_b(l1);
                ++l;
            }
            else if (!material1.isSolid())
            {
                ++f;
                ++l;
            }
        }

        return 1.0F - f / (float)l;
    }

    public void func_147749_a(Block p_147749_1_, World p_147749_2_, int p_147749_3_, int p_147749_4_, int p_147749_5_, int p_147749_6_)
    {
        float f = 0.5F;
        float f1 = 1.0F;
        float f2 = 0.8F;
        float f3 = 0.6F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setBrightness(p_147749_1_.func_149677_c(p_147749_2_, p_147749_3_, p_147749_4_, p_147749_5_));
        tessellator.setColorOpaque_F(f, f, f);
        this.func_147768_a(p_147749_1_, -0.5D, -0.5D, -0.5D, this.func_147787_a(p_147749_1_, 0, p_147749_6_));
        tessellator.setColorOpaque_F(f1, f1, f1);
        this.func_147806_b(p_147749_1_, -0.5D, -0.5D, -0.5D, this.func_147787_a(p_147749_1_, 1, p_147749_6_));
        tessellator.setColorOpaque_F(f2, f2, f2);
        this.func_147761_c(p_147749_1_, -0.5D, -0.5D, -0.5D, this.func_147787_a(p_147749_1_, 2, p_147749_6_));
        tessellator.setColorOpaque_F(f2, f2, f2);
        this.func_147734_d(p_147749_1_, -0.5D, -0.5D, -0.5D, this.func_147787_a(p_147749_1_, 3, p_147749_6_));
        tessellator.setColorOpaque_F(f3, f3, f3);
        this.func_147798_e(p_147749_1_, -0.5D, -0.5D, -0.5D, this.func_147787_a(p_147749_1_, 4, p_147749_6_));
        tessellator.setColorOpaque_F(f3, f3, f3);
        this.func_147764_f(p_147749_1_, -0.5D, -0.5D, -0.5D, this.func_147787_a(p_147749_1_, 5, p_147749_6_));
        tessellator.draw();
    }

    public boolean func_147784_q(Block p_147784_1_, int p_147784_2_, int p_147784_3_, int p_147784_4_)
    {
        int l = p_147784_1_.func_149720_d(this.field_147845_a, p_147784_2_, p_147784_3_, p_147784_4_);
        float f = (float)(l >> 16 & 255) / 255.0F;
        float f1 = (float)(l >> 8 & 255) / 255.0F;
        float f2 = (float)(l & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable)
        {
            float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
            float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            f = f3;
            f1 = f4;
            f2 = f5;
        }

        return Minecraft.isAmbientOcclusionEnabled() && p_147784_1_.func_149750_m() == 0 ? (this.field_147849_o ? this.func_147808_b(p_147784_1_, p_147784_2_, p_147784_3_, p_147784_4_, f, f1, f2) : this.func_147751_a(p_147784_1_, p_147784_2_, p_147784_3_, p_147784_4_, f, f1, f2)) : this.func_147736_d(p_147784_1_, p_147784_2_, p_147784_3_, p_147784_4_, f, f1, f2);
    }

    public boolean func_147742_r(Block p_147742_1_, int p_147742_2_, int p_147742_3_, int p_147742_4_)
    {
        int l = this.field_147845_a.getBlockMetadata(p_147742_2_, p_147742_3_, p_147742_4_);
        int i1 = l & 12;

        if (i1 == 4)
        {
            this.field_147875_q = 1;
            this.field_147873_r = 1;
            this.field_147867_u = 1;
            this.field_147865_v = 1;
        }
        else if (i1 == 8)
        {
            this.field_147871_s = 1;
            this.field_147869_t = 1;
        }

        boolean flag = this.func_147784_q(p_147742_1_, p_147742_2_, p_147742_3_, p_147742_4_);
        this.field_147871_s = 0;
        this.field_147875_q = 0;
        this.field_147873_r = 0;
        this.field_147869_t = 0;
        this.field_147867_u = 0;
        this.field_147865_v = 0;
        return flag;
    }

    public boolean func_147779_s(Block p_147779_1_, int p_147779_2_, int p_147779_3_, int p_147779_4_)
    {
        int l = this.field_147845_a.getBlockMetadata(p_147779_2_, p_147779_3_, p_147779_4_);

        if (l == 3)
        {
            this.field_147875_q = 1;
            this.field_147873_r = 1;
            this.field_147867_u = 1;
            this.field_147865_v = 1;
        }
        else if (l == 4)
        {
            this.field_147871_s = 1;
            this.field_147869_t = 1;
        }

        boolean flag = this.func_147784_q(p_147779_1_, p_147779_2_, p_147779_3_, p_147779_4_);
        this.field_147871_s = 0;
        this.field_147875_q = 0;
        this.field_147873_r = 0;
        this.field_147869_t = 0;
        this.field_147867_u = 0;
        this.field_147865_v = 0;
        return flag;
    }

    public boolean func_147751_a(Block p_147751_1_, int p_147751_2_, int p_147751_3_, int p_147751_4_, float p_147751_5_, float p_147751_6_, float p_147751_7_)
    {
        this.field_147863_w = true;
        boolean flag = false;
        float f3 = 0.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        boolean flag1 = true;
        int l = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_, p_147751_3_, p_147751_4_);
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(983055);

        if (this.func_147745_b(p_147751_1_).getIconName().equals("grass_top"))
        {
            flag1 = false;
        }
        else if (this.func_147744_b())
        {
            flag1 = false;
        }

        boolean flag3;
        boolean flag2;
        boolean flag5;
        boolean flag4;
        float f7;
        int i1;

        if (this.field_147837_f || p_147751_1_.func_149646_a(this.field_147845_a, p_147751_2_, p_147751_3_ - 1, p_147751_4_, 0))
        {
            if (this.field_147855_j <= 0.0D)
            {
                --p_147751_3_;
            }

            this.field_147831_S = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_ - 1, p_147751_3_, p_147751_4_);
            this.field_147825_U = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_, p_147751_3_, p_147751_4_ - 1);
            this.field_147828_V = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_, p_147751_3_, p_147751_4_ + 1);
            this.field_147835_X = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_ + 1, p_147751_3_, p_147751_4_);
            this.field_147886_y = this.field_147845_a.func_147439_a(p_147751_2_ - 1, p_147751_3_, p_147751_4_).func_149685_I();
            this.field_147814_A = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_, p_147751_4_ - 1).func_149685_I();
            this.field_147815_B = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_, p_147751_4_ + 1).func_149685_I();
            this.field_147810_D = this.field_147845_a.func_147439_a(p_147751_2_ + 1, p_147751_3_, p_147751_4_).func_149685_I();
            flag2 = this.field_147845_a.func_147439_a(p_147751_2_ + 1, p_147751_3_ - 1, p_147751_4_).func_149751_l();
            flag3 = this.field_147845_a.func_147439_a(p_147751_2_ - 1, p_147751_3_ - 1, p_147751_4_).func_149751_l();
            flag4 = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_ - 1, p_147751_4_ + 1).func_149751_l();
            flag5 = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_ - 1, p_147751_4_ - 1).func_149751_l();

            if (!flag5 && !flag3)
            {
                this.field_147888_x = this.field_147886_y;
                this.field_147832_R = this.field_147831_S;
            }
            else
            {
                this.field_147888_x = this.field_147845_a.func_147439_a(p_147751_2_ - 1, p_147751_3_, p_147751_4_ - 1).func_149685_I();
                this.field_147832_R = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_ - 1, p_147751_3_, p_147751_4_ - 1);
            }

            if (!flag4 && !flag3)
            {
                this.field_147884_z = this.field_147886_y;
                this.field_147826_T = this.field_147831_S;
            }
            else
            {
                this.field_147884_z = this.field_147845_a.func_147439_a(p_147751_2_ - 1, p_147751_3_, p_147751_4_ + 1).func_149685_I();
                this.field_147826_T = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_ - 1, p_147751_3_, p_147751_4_ + 1);
            }

            if (!flag5 && !flag2)
            {
                this.field_147816_C = this.field_147810_D;
                this.field_147827_W = this.field_147835_X;
            }
            else
            {
                this.field_147816_C = this.field_147845_a.func_147439_a(p_147751_2_ + 1, p_147751_3_, p_147751_4_ - 1).func_149685_I();
                this.field_147827_W = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_ + 1, p_147751_3_, p_147751_4_ - 1);
            }

            if (!flag4 && !flag2)
            {
                this.field_147811_E = this.field_147810_D;
                this.field_147834_Y = this.field_147835_X;
            }
            else
            {
                this.field_147811_E = this.field_147845_a.func_147439_a(p_147751_2_ + 1, p_147751_3_, p_147751_4_ + 1).func_149685_I();
                this.field_147834_Y = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_ + 1, p_147751_3_, p_147751_4_ + 1);
            }

            if (this.field_147855_j <= 0.0D)
            {
                ++p_147751_3_;
            }

            i1 = l;

            if (this.field_147855_j <= 0.0D || !this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_ - 1, p_147751_4_).func_149662_c())
            {
                i1 = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_, p_147751_3_ - 1, p_147751_4_);
            }

            f7 = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_ - 1, p_147751_4_).func_149685_I();
            f3 = (this.field_147884_z + this.field_147886_y + this.field_147815_B + f7) / 4.0F;
            f6 = (this.field_147815_B + f7 + this.field_147811_E + this.field_147810_D) / 4.0F;
            f5 = (f7 + this.field_147814_A + this.field_147810_D + this.field_147816_C) / 4.0F;
            f4 = (this.field_147886_y + this.field_147888_x + f7 + this.field_147814_A) / 4.0F;
            this.field_147864_al = this.func_147778_a(this.field_147826_T, this.field_147831_S, this.field_147828_V, i1);
            this.field_147870_ao = this.func_147778_a(this.field_147828_V, this.field_147834_Y, this.field_147835_X, i1);
            this.field_147876_an = this.func_147778_a(this.field_147825_U, this.field_147835_X, this.field_147827_W, i1);
            this.field_147874_am = this.func_147778_a(this.field_147831_S, this.field_147832_R, this.field_147825_U, i1);

            if (flag1)
            {
                this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = p_147751_5_ * 0.5F;
                this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = p_147751_6_ * 0.5F;
                this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = p_147751_7_ * 0.5F;
            }
            else
            {
                this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = 0.5F;
                this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = 0.5F;
                this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = 0.5F;
            }

            this.field_147872_ap *= f3;
            this.field_147846_at *= f3;
            this.field_147854_ax *= f3;
            this.field_147852_aq *= f4;
            this.field_147860_au *= f4;
            this.field_147841_ay *= f4;
            this.field_147850_ar *= f5;
            this.field_147858_av *= f5;
            this.field_147839_az *= f5;
            this.field_147848_as *= f6;
            this.field_147856_aw *= f6;
            this.field_147833_aA *= f6;
            this.func_147768_a(p_147751_1_, (double)p_147751_2_, (double)p_147751_3_, (double)p_147751_4_, this.func_147793_a(p_147751_1_, this.field_147845_a, p_147751_2_, p_147751_3_, p_147751_4_, 0));
            flag = true;
        }

        if (this.field_147837_f || p_147751_1_.func_149646_a(this.field_147845_a, p_147751_2_, p_147751_3_ + 1, p_147751_4_, 1))
        {
            if (this.field_147857_k >= 1.0D)
            {
                ++p_147751_3_;
            }

            this.field_147880_aa = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_ - 1, p_147751_3_, p_147751_4_);
            this.field_147885_ae = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_ + 1, p_147751_3_, p_147751_4_);
            this.field_147878_ac = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_, p_147751_3_, p_147751_4_ - 1);
            this.field_147887_af = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_, p_147751_3_, p_147751_4_ + 1);
            this.field_147813_G = this.field_147845_a.func_147439_a(p_147751_2_ - 1, p_147751_3_, p_147751_4_).func_149685_I();
            this.field_147824_K = this.field_147845_a.func_147439_a(p_147751_2_ + 1, p_147751_3_, p_147751_4_).func_149685_I();
            this.field_147822_I = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_, p_147751_4_ - 1).func_149685_I();
            this.field_147817_L = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_, p_147751_4_ + 1).func_149685_I();
            flag2 = this.field_147845_a.func_147439_a(p_147751_2_ + 1, p_147751_3_ + 1, p_147751_4_).func_149751_l();
            flag3 = this.field_147845_a.func_147439_a(p_147751_2_ - 1, p_147751_3_ + 1, p_147751_4_).func_149751_l();
            flag4 = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_ + 1, p_147751_4_ + 1).func_149751_l();
            flag5 = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_ + 1, p_147751_4_ - 1).func_149751_l();

            if (!flag5 && !flag3)
            {
                this.field_147812_F = this.field_147813_G;
                this.field_147836_Z = this.field_147880_aa;
            }
            else
            {
                this.field_147812_F = this.field_147845_a.func_147439_a(p_147751_2_ - 1, p_147751_3_, p_147751_4_ - 1).func_149685_I();
                this.field_147836_Z = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_ - 1, p_147751_3_, p_147751_4_ - 1);
            }

            if (!flag5 && !flag2)
            {
                this.field_147823_J = this.field_147824_K;
                this.field_147879_ad = this.field_147885_ae;
            }
            else
            {
                this.field_147823_J = this.field_147845_a.func_147439_a(p_147751_2_ + 1, p_147751_3_, p_147751_4_ - 1).func_149685_I();
                this.field_147879_ad = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_ + 1, p_147751_3_, p_147751_4_ - 1);
            }

            if (!flag4 && !flag3)
            {
                this.field_147821_H = this.field_147813_G;
                this.field_147881_ab = this.field_147880_aa;
            }
            else
            {
                this.field_147821_H = this.field_147845_a.func_147439_a(p_147751_2_ - 1, p_147751_3_, p_147751_4_ + 1).func_149685_I();
                this.field_147881_ab = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_ - 1, p_147751_3_, p_147751_4_ + 1);
            }

            if (!flag4 && !flag2)
            {
                this.field_147818_M = this.field_147824_K;
                this.field_147882_ag = this.field_147885_ae;
            }
            else
            {
                this.field_147818_M = this.field_147845_a.func_147439_a(p_147751_2_ + 1, p_147751_3_, p_147751_4_ + 1).func_149685_I();
                this.field_147882_ag = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_ + 1, p_147751_3_, p_147751_4_ + 1);
            }

            if (this.field_147857_k >= 1.0D)
            {
                --p_147751_3_;
            }

            i1 = l;

            if (this.field_147857_k >= 1.0D || !this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_ + 1, p_147751_4_).func_149662_c())
            {
                i1 = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_, p_147751_3_ + 1, p_147751_4_);
            }

            f7 = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_ + 1, p_147751_4_).func_149685_I();
            f6 = (this.field_147821_H + this.field_147813_G + this.field_147817_L + f7) / 4.0F;
            f3 = (this.field_147817_L + f7 + this.field_147818_M + this.field_147824_K) / 4.0F;
            f4 = (f7 + this.field_147822_I + this.field_147824_K + this.field_147823_J) / 4.0F;
            f5 = (this.field_147813_G + this.field_147812_F + f7 + this.field_147822_I) / 4.0F;
            this.field_147870_ao = this.func_147778_a(this.field_147881_ab, this.field_147880_aa, this.field_147887_af, i1);
            this.field_147864_al = this.func_147778_a(this.field_147887_af, this.field_147882_ag, this.field_147885_ae, i1);
            this.field_147874_am = this.func_147778_a(this.field_147878_ac, this.field_147885_ae, this.field_147879_ad, i1);
            this.field_147876_an = this.func_147778_a(this.field_147880_aa, this.field_147836_Z, this.field_147878_ac, i1);
            this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = p_147751_5_;
            this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = p_147751_6_;
            this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = p_147751_7_;
            this.field_147872_ap *= f3;
            this.field_147846_at *= f3;
            this.field_147854_ax *= f3;
            this.field_147852_aq *= f4;
            this.field_147860_au *= f4;
            this.field_147841_ay *= f4;
            this.field_147850_ar *= f5;
            this.field_147858_av *= f5;
            this.field_147839_az *= f5;
            this.field_147848_as *= f6;
            this.field_147856_aw *= f6;
            this.field_147833_aA *= f6;
            this.func_147806_b(p_147751_1_, (double)p_147751_2_, (double)p_147751_3_, (double)p_147751_4_, this.func_147793_a(p_147751_1_, this.field_147845_a, p_147751_2_, p_147751_3_, p_147751_4_, 1));
            flag = true;
        }

        IIcon iicon;

        if (this.field_147837_f || p_147751_1_.func_149646_a(this.field_147845_a, p_147751_2_, p_147751_3_, p_147751_4_ - 1, 2))
        {
            if (this.field_147851_l <= 0.0D)
            {
                --p_147751_4_;
            }

            this.field_147819_N = this.field_147845_a.func_147439_a(p_147751_2_ - 1, p_147751_3_, p_147751_4_).func_149685_I();
            this.field_147814_A = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_ - 1, p_147751_4_).func_149685_I();
            this.field_147822_I = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_ + 1, p_147751_4_).func_149685_I();
            this.field_147820_O = this.field_147845_a.func_147439_a(p_147751_2_ + 1, p_147751_3_, p_147751_4_).func_149685_I();
            this.field_147883_ah = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_ - 1, p_147751_3_, p_147751_4_);
            this.field_147825_U = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_, p_147751_3_ - 1, p_147751_4_);
            this.field_147878_ac = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_, p_147751_3_ + 1, p_147751_4_);
            this.field_147866_ai = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_ + 1, p_147751_3_, p_147751_4_);
            flag2 = this.field_147845_a.func_147439_a(p_147751_2_ + 1, p_147751_3_, p_147751_4_ - 1).func_149751_l();
            flag3 = this.field_147845_a.func_147439_a(p_147751_2_ - 1, p_147751_3_, p_147751_4_ - 1).func_149751_l();
            flag4 = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_ + 1, p_147751_4_ - 1).func_149751_l();
            flag5 = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_ - 1, p_147751_4_ - 1).func_149751_l();

            if (!flag3 && !flag5)
            {
                this.field_147888_x = this.field_147819_N;
                this.field_147832_R = this.field_147883_ah;
            }
            else
            {
                this.field_147888_x = this.field_147845_a.func_147439_a(p_147751_2_ - 1, p_147751_3_ - 1, p_147751_4_).func_149685_I();
                this.field_147832_R = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_ - 1, p_147751_3_ - 1, p_147751_4_);
            }

            if (!flag3 && !flag4)
            {
                this.field_147812_F = this.field_147819_N;
                this.field_147836_Z = this.field_147883_ah;
            }
            else
            {
                this.field_147812_F = this.field_147845_a.func_147439_a(p_147751_2_ - 1, p_147751_3_ + 1, p_147751_4_).func_149685_I();
                this.field_147836_Z = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_ - 1, p_147751_3_ + 1, p_147751_4_);
            }

            if (!flag2 && !flag5)
            {
                this.field_147816_C = this.field_147820_O;
                this.field_147827_W = this.field_147866_ai;
            }
            else
            {
                this.field_147816_C = this.field_147845_a.func_147439_a(p_147751_2_ + 1, p_147751_3_ - 1, p_147751_4_).func_149685_I();
                this.field_147827_W = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_ + 1, p_147751_3_ - 1, p_147751_4_);
            }

            if (!flag2 && !flag4)
            {
                this.field_147823_J = this.field_147820_O;
                this.field_147879_ad = this.field_147866_ai;
            }
            else
            {
                this.field_147823_J = this.field_147845_a.func_147439_a(p_147751_2_ + 1, p_147751_3_ + 1, p_147751_4_).func_149685_I();
                this.field_147879_ad = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_ + 1, p_147751_3_ + 1, p_147751_4_);
            }

            if (this.field_147851_l <= 0.0D)
            {
                ++p_147751_4_;
            }

            i1 = l;

            if (this.field_147851_l <= 0.0D || !this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_, p_147751_4_ - 1).func_149662_c())
            {
                i1 = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_, p_147751_3_, p_147751_4_ - 1);
            }

            f7 = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_, p_147751_4_ - 1).func_149685_I();
            f3 = (this.field_147819_N + this.field_147812_F + f7 + this.field_147822_I) / 4.0F;
            f4 = (f7 + this.field_147822_I + this.field_147820_O + this.field_147823_J) / 4.0F;
            f5 = (this.field_147814_A + f7 + this.field_147816_C + this.field_147820_O) / 4.0F;
            f6 = (this.field_147888_x + this.field_147819_N + this.field_147814_A + f7) / 4.0F;
            this.field_147864_al = this.func_147778_a(this.field_147883_ah, this.field_147836_Z, this.field_147878_ac, i1);
            this.field_147874_am = this.func_147778_a(this.field_147878_ac, this.field_147866_ai, this.field_147879_ad, i1);
            this.field_147876_an = this.func_147778_a(this.field_147825_U, this.field_147827_W, this.field_147866_ai, i1);
            this.field_147870_ao = this.func_147778_a(this.field_147832_R, this.field_147883_ah, this.field_147825_U, i1);

            if (flag1)
            {
                this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = p_147751_5_ * 0.8F;
                this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = p_147751_6_ * 0.8F;
                this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = p_147751_7_ * 0.8F;
            }
            else
            {
                this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = 0.8F;
                this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = 0.8F;
                this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = 0.8F;
            }

            this.field_147872_ap *= f3;
            this.field_147846_at *= f3;
            this.field_147854_ax *= f3;
            this.field_147852_aq *= f4;
            this.field_147860_au *= f4;
            this.field_147841_ay *= f4;
            this.field_147850_ar *= f5;
            this.field_147858_av *= f5;
            this.field_147839_az *= f5;
            this.field_147848_as *= f6;
            this.field_147856_aw *= f6;
            this.field_147833_aA *= f6;
            iicon = this.func_147793_a(p_147751_1_, this.field_147845_a, p_147751_2_, p_147751_3_, p_147751_4_, 2);
            this.func_147761_c(p_147751_1_, (double)p_147751_2_, (double)p_147751_3_, (double)p_147751_4_, iicon);

            if (field_147843_b && iicon.getIconName().equals("grass_side") && !this.func_147744_b())
            {
                this.field_147872_ap *= p_147751_5_;
                this.field_147852_aq *= p_147751_5_;
                this.field_147850_ar *= p_147751_5_;
                this.field_147848_as *= p_147751_5_;
                this.field_147846_at *= p_147751_6_;
                this.field_147860_au *= p_147751_6_;
                this.field_147858_av *= p_147751_6_;
                this.field_147856_aw *= p_147751_6_;
                this.field_147854_ax *= p_147751_7_;
                this.field_147841_ay *= p_147751_7_;
                this.field_147839_az *= p_147751_7_;
                this.field_147833_aA *= p_147751_7_;
                this.func_147761_c(p_147751_1_, (double)p_147751_2_, (double)p_147751_3_, (double)p_147751_4_, BlockGrass.func_149990_e());
            }

            flag = true;
        }

        if (this.field_147837_f || p_147751_1_.func_149646_a(this.field_147845_a, p_147751_2_, p_147751_3_, p_147751_4_ + 1, 3))
        {
            if (this.field_147853_m >= 1.0D)
            {
                ++p_147751_4_;
            }

            this.field_147830_P = this.field_147845_a.func_147439_a(p_147751_2_ - 1, p_147751_3_, p_147751_4_).func_149685_I();
            this.field_147829_Q = this.field_147845_a.func_147439_a(p_147751_2_ + 1, p_147751_3_, p_147751_4_).func_149685_I();
            this.field_147815_B = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_ - 1, p_147751_4_).func_149685_I();
            this.field_147817_L = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_ + 1, p_147751_4_).func_149685_I();
            this.field_147868_aj = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_ - 1, p_147751_3_, p_147751_4_);
            this.field_147862_ak = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_ + 1, p_147751_3_, p_147751_4_);
            this.field_147828_V = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_, p_147751_3_ - 1, p_147751_4_);
            this.field_147887_af = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_, p_147751_3_ + 1, p_147751_4_);
            flag2 = this.field_147845_a.func_147439_a(p_147751_2_ + 1, p_147751_3_, p_147751_4_ + 1).func_149751_l();
            flag3 = this.field_147845_a.func_147439_a(p_147751_2_ - 1, p_147751_3_, p_147751_4_ + 1).func_149751_l();
            flag4 = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_ + 1, p_147751_4_ + 1).func_149751_l();
            flag5 = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_ - 1, p_147751_4_ + 1).func_149751_l();

            if (!flag3 && !flag5)
            {
                this.field_147884_z = this.field_147830_P;
                this.field_147826_T = this.field_147868_aj;
            }
            else
            {
                this.field_147884_z = this.field_147845_a.func_147439_a(p_147751_2_ - 1, p_147751_3_ - 1, p_147751_4_).func_149685_I();
                this.field_147826_T = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_ - 1, p_147751_3_ - 1, p_147751_4_);
            }

            if (!flag3 && !flag4)
            {
                this.field_147821_H = this.field_147830_P;
                this.field_147881_ab = this.field_147868_aj;
            }
            else
            {
                this.field_147821_H = this.field_147845_a.func_147439_a(p_147751_2_ - 1, p_147751_3_ + 1, p_147751_4_).func_149685_I();
                this.field_147881_ab = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_ - 1, p_147751_3_ + 1, p_147751_4_);
            }

            if (!flag2 && !flag5)
            {
                this.field_147811_E = this.field_147829_Q;
                this.field_147834_Y = this.field_147862_ak;
            }
            else
            {
                this.field_147811_E = this.field_147845_a.func_147439_a(p_147751_2_ + 1, p_147751_3_ - 1, p_147751_4_).func_149685_I();
                this.field_147834_Y = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_ + 1, p_147751_3_ - 1, p_147751_4_);
            }

            if (!flag2 && !flag4)
            {
                this.field_147818_M = this.field_147829_Q;
                this.field_147882_ag = this.field_147862_ak;
            }
            else
            {
                this.field_147818_M = this.field_147845_a.func_147439_a(p_147751_2_ + 1, p_147751_3_ + 1, p_147751_4_).func_149685_I();
                this.field_147882_ag = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_ + 1, p_147751_3_ + 1, p_147751_4_);
            }

            if (this.field_147853_m >= 1.0D)
            {
                --p_147751_4_;
            }

            i1 = l;

            if (this.field_147853_m >= 1.0D || !this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_, p_147751_4_ + 1).func_149662_c())
            {
                i1 = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_, p_147751_3_, p_147751_4_ + 1);
            }

            f7 = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_, p_147751_4_ + 1).func_149685_I();
            f3 = (this.field_147830_P + this.field_147821_H + f7 + this.field_147817_L) / 4.0F;
            f6 = (f7 + this.field_147817_L + this.field_147829_Q + this.field_147818_M) / 4.0F;
            f5 = (this.field_147815_B + f7 + this.field_147811_E + this.field_147829_Q) / 4.0F;
            f4 = (this.field_147884_z + this.field_147830_P + this.field_147815_B + f7) / 4.0F;
            this.field_147864_al = this.func_147778_a(this.field_147868_aj, this.field_147881_ab, this.field_147887_af, i1);
            this.field_147870_ao = this.func_147778_a(this.field_147887_af, this.field_147862_ak, this.field_147882_ag, i1);
            this.field_147876_an = this.func_147778_a(this.field_147828_V, this.field_147834_Y, this.field_147862_ak, i1);
            this.field_147874_am = this.func_147778_a(this.field_147826_T, this.field_147868_aj, this.field_147828_V, i1);

            if (flag1)
            {
                this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = p_147751_5_ * 0.8F;
                this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = p_147751_6_ * 0.8F;
                this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = p_147751_7_ * 0.8F;
            }
            else
            {
                this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = 0.8F;
                this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = 0.8F;
                this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = 0.8F;
            }

            this.field_147872_ap *= f3;
            this.field_147846_at *= f3;
            this.field_147854_ax *= f3;
            this.field_147852_aq *= f4;
            this.field_147860_au *= f4;
            this.field_147841_ay *= f4;
            this.field_147850_ar *= f5;
            this.field_147858_av *= f5;
            this.field_147839_az *= f5;
            this.field_147848_as *= f6;
            this.field_147856_aw *= f6;
            this.field_147833_aA *= f6;
            iicon = this.func_147793_a(p_147751_1_, this.field_147845_a, p_147751_2_, p_147751_3_, p_147751_4_, 3);
            this.func_147734_d(p_147751_1_, (double)p_147751_2_, (double)p_147751_3_, (double)p_147751_4_, this.func_147793_a(p_147751_1_, this.field_147845_a, p_147751_2_, p_147751_3_, p_147751_4_, 3));

            if (field_147843_b && iicon.getIconName().equals("grass_side") && !this.func_147744_b())
            {
                this.field_147872_ap *= p_147751_5_;
                this.field_147852_aq *= p_147751_5_;
                this.field_147850_ar *= p_147751_5_;
                this.field_147848_as *= p_147751_5_;
                this.field_147846_at *= p_147751_6_;
                this.field_147860_au *= p_147751_6_;
                this.field_147858_av *= p_147751_6_;
                this.field_147856_aw *= p_147751_6_;
                this.field_147854_ax *= p_147751_7_;
                this.field_147841_ay *= p_147751_7_;
                this.field_147839_az *= p_147751_7_;
                this.field_147833_aA *= p_147751_7_;
                this.func_147734_d(p_147751_1_, (double)p_147751_2_, (double)p_147751_3_, (double)p_147751_4_, BlockGrass.func_149990_e());
            }

            flag = true;
        }

        if (this.field_147837_f || p_147751_1_.func_149646_a(this.field_147845_a, p_147751_2_ - 1, p_147751_3_, p_147751_4_, 4))
        {
            if (this.field_147859_h <= 0.0D)
            {
                --p_147751_2_;
            }

            this.field_147886_y = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_ - 1, p_147751_4_).func_149685_I();
            this.field_147819_N = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_, p_147751_4_ - 1).func_149685_I();
            this.field_147830_P = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_, p_147751_4_ + 1).func_149685_I();
            this.field_147813_G = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_ + 1, p_147751_4_).func_149685_I();
            this.field_147831_S = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_, p_147751_3_ - 1, p_147751_4_);
            this.field_147883_ah = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_, p_147751_3_, p_147751_4_ - 1);
            this.field_147868_aj = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_, p_147751_3_, p_147751_4_ + 1);
            this.field_147880_aa = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_, p_147751_3_ + 1, p_147751_4_);
            flag2 = this.field_147845_a.func_147439_a(p_147751_2_ - 1, p_147751_3_ + 1, p_147751_4_).func_149751_l();
            flag3 = this.field_147845_a.func_147439_a(p_147751_2_ - 1, p_147751_3_ - 1, p_147751_4_).func_149751_l();
            flag4 = this.field_147845_a.func_147439_a(p_147751_2_ - 1, p_147751_3_, p_147751_4_ - 1).func_149751_l();
            flag5 = this.field_147845_a.func_147439_a(p_147751_2_ - 1, p_147751_3_, p_147751_4_ + 1).func_149751_l();

            if (!flag4 && !flag3)
            {
                this.field_147888_x = this.field_147819_N;
                this.field_147832_R = this.field_147883_ah;
            }
            else
            {
                this.field_147888_x = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_ - 1, p_147751_4_ - 1).func_149685_I();
                this.field_147832_R = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_, p_147751_3_ - 1, p_147751_4_ - 1);
            }

            if (!flag5 && !flag3)
            {
                this.field_147884_z = this.field_147830_P;
                this.field_147826_T = this.field_147868_aj;
            }
            else
            {
                this.field_147884_z = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_ - 1, p_147751_4_ + 1).func_149685_I();
                this.field_147826_T = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_, p_147751_3_ - 1, p_147751_4_ + 1);
            }

            if (!flag4 && !flag2)
            {
                this.field_147812_F = this.field_147819_N;
                this.field_147836_Z = this.field_147883_ah;
            }
            else
            {
                this.field_147812_F = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_ + 1, p_147751_4_ - 1).func_149685_I();
                this.field_147836_Z = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_, p_147751_3_ + 1, p_147751_4_ - 1);
            }

            if (!flag5 && !flag2)
            {
                this.field_147821_H = this.field_147830_P;
                this.field_147881_ab = this.field_147868_aj;
            }
            else
            {
                this.field_147821_H = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_ + 1, p_147751_4_ + 1).func_149685_I();
                this.field_147881_ab = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_, p_147751_3_ + 1, p_147751_4_ + 1);
            }

            if (this.field_147859_h <= 0.0D)
            {
                ++p_147751_2_;
            }

            i1 = l;

            if (this.field_147859_h <= 0.0D || !this.field_147845_a.func_147439_a(p_147751_2_ - 1, p_147751_3_, p_147751_4_).func_149662_c())
            {
                i1 = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_ - 1, p_147751_3_, p_147751_4_);
            }

            f7 = this.field_147845_a.func_147439_a(p_147751_2_ - 1, p_147751_3_, p_147751_4_).func_149685_I();
            f6 = (this.field_147886_y + this.field_147884_z + f7 + this.field_147830_P) / 4.0F;
            f3 = (f7 + this.field_147830_P + this.field_147813_G + this.field_147821_H) / 4.0F;
            f4 = (this.field_147819_N + f7 + this.field_147812_F + this.field_147813_G) / 4.0F;
            f5 = (this.field_147888_x + this.field_147886_y + this.field_147819_N + f7) / 4.0F;
            this.field_147870_ao = this.func_147778_a(this.field_147831_S, this.field_147826_T, this.field_147868_aj, i1);
            this.field_147864_al = this.func_147778_a(this.field_147868_aj, this.field_147880_aa, this.field_147881_ab, i1);
            this.field_147874_am = this.func_147778_a(this.field_147883_ah, this.field_147836_Z, this.field_147880_aa, i1);
            this.field_147876_an = this.func_147778_a(this.field_147832_R, this.field_147831_S, this.field_147883_ah, i1);

            if (flag1)
            {
                this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = p_147751_5_ * 0.6F;
                this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = p_147751_6_ * 0.6F;
                this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = p_147751_7_ * 0.6F;
            }
            else
            {
                this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = 0.6F;
                this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = 0.6F;
                this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = 0.6F;
            }

            this.field_147872_ap *= f3;
            this.field_147846_at *= f3;
            this.field_147854_ax *= f3;
            this.field_147852_aq *= f4;
            this.field_147860_au *= f4;
            this.field_147841_ay *= f4;
            this.field_147850_ar *= f5;
            this.field_147858_av *= f5;
            this.field_147839_az *= f5;
            this.field_147848_as *= f6;
            this.field_147856_aw *= f6;
            this.field_147833_aA *= f6;
            iicon = this.func_147793_a(p_147751_1_, this.field_147845_a, p_147751_2_, p_147751_3_, p_147751_4_, 4);
            this.func_147798_e(p_147751_1_, (double)p_147751_2_, (double)p_147751_3_, (double)p_147751_4_, iicon);

            if (field_147843_b && iicon.getIconName().equals("grass_side") && !this.func_147744_b())
            {
                this.field_147872_ap *= p_147751_5_;
                this.field_147852_aq *= p_147751_5_;
                this.field_147850_ar *= p_147751_5_;
                this.field_147848_as *= p_147751_5_;
                this.field_147846_at *= p_147751_6_;
                this.field_147860_au *= p_147751_6_;
                this.field_147858_av *= p_147751_6_;
                this.field_147856_aw *= p_147751_6_;
                this.field_147854_ax *= p_147751_7_;
                this.field_147841_ay *= p_147751_7_;
                this.field_147839_az *= p_147751_7_;
                this.field_147833_aA *= p_147751_7_;
                this.func_147798_e(p_147751_1_, (double)p_147751_2_, (double)p_147751_3_, (double)p_147751_4_, BlockGrass.func_149990_e());
            }

            flag = true;
        }

        if (this.field_147837_f || p_147751_1_.func_149646_a(this.field_147845_a, p_147751_2_ + 1, p_147751_3_, p_147751_4_, 5))
        {
            if (this.field_147861_i >= 1.0D)
            {
                ++p_147751_2_;
            }

            this.field_147810_D = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_ - 1, p_147751_4_).func_149685_I();
            this.field_147820_O = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_, p_147751_4_ - 1).func_149685_I();
            this.field_147829_Q = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_, p_147751_4_ + 1).func_149685_I();
            this.field_147824_K = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_ + 1, p_147751_4_).func_149685_I();
            this.field_147835_X = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_, p_147751_3_ - 1, p_147751_4_);
            this.field_147866_ai = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_, p_147751_3_, p_147751_4_ - 1);
            this.field_147862_ak = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_, p_147751_3_, p_147751_4_ + 1);
            this.field_147885_ae = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_, p_147751_3_ + 1, p_147751_4_);
            flag2 = this.field_147845_a.func_147439_a(p_147751_2_ + 1, p_147751_3_ + 1, p_147751_4_).func_149751_l();
            flag3 = this.field_147845_a.func_147439_a(p_147751_2_ + 1, p_147751_3_ - 1, p_147751_4_).func_149751_l();
            flag4 = this.field_147845_a.func_147439_a(p_147751_2_ + 1, p_147751_3_, p_147751_4_ + 1).func_149751_l();
            flag5 = this.field_147845_a.func_147439_a(p_147751_2_ + 1, p_147751_3_, p_147751_4_ - 1).func_149751_l();

            if (!flag3 && !flag5)
            {
                this.field_147816_C = this.field_147820_O;
                this.field_147827_W = this.field_147866_ai;
            }
            else
            {
                this.field_147816_C = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_ - 1, p_147751_4_ - 1).func_149685_I();
                this.field_147827_W = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_, p_147751_3_ - 1, p_147751_4_ - 1);
            }

            if (!flag3 && !flag4)
            {
                this.field_147811_E = this.field_147829_Q;
                this.field_147834_Y = this.field_147862_ak;
            }
            else
            {
                this.field_147811_E = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_ - 1, p_147751_4_ + 1).func_149685_I();
                this.field_147834_Y = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_, p_147751_3_ - 1, p_147751_4_ + 1);
            }

            if (!flag2 && !flag5)
            {
                this.field_147823_J = this.field_147820_O;
                this.field_147879_ad = this.field_147866_ai;
            }
            else
            {
                this.field_147823_J = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_ + 1, p_147751_4_ - 1).func_149685_I();
                this.field_147879_ad = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_, p_147751_3_ + 1, p_147751_4_ - 1);
            }

            if (!flag2 && !flag4)
            {
                this.field_147818_M = this.field_147829_Q;
                this.field_147882_ag = this.field_147862_ak;
            }
            else
            {
                this.field_147818_M = this.field_147845_a.func_147439_a(p_147751_2_, p_147751_3_ + 1, p_147751_4_ + 1).func_149685_I();
                this.field_147882_ag = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_, p_147751_3_ + 1, p_147751_4_ + 1);
            }

            if (this.field_147861_i >= 1.0D)
            {
                --p_147751_2_;
            }

            i1 = l;

            if (this.field_147861_i >= 1.0D || !this.field_147845_a.func_147439_a(p_147751_2_ + 1, p_147751_3_, p_147751_4_).func_149662_c())
            {
                i1 = p_147751_1_.func_149677_c(this.field_147845_a, p_147751_2_ + 1, p_147751_3_, p_147751_4_);
            }

            f7 = this.field_147845_a.func_147439_a(p_147751_2_ + 1, p_147751_3_, p_147751_4_).func_149685_I();
            f3 = (this.field_147810_D + this.field_147811_E + f7 + this.field_147829_Q) / 4.0F;
            f4 = (this.field_147816_C + this.field_147810_D + this.field_147820_O + f7) / 4.0F;
            f5 = (this.field_147820_O + f7 + this.field_147823_J + this.field_147824_K) / 4.0F;
            f6 = (f7 + this.field_147829_Q + this.field_147824_K + this.field_147818_M) / 4.0F;
            this.field_147864_al = this.func_147778_a(this.field_147835_X, this.field_147834_Y, this.field_147862_ak, i1);
            this.field_147870_ao = this.func_147778_a(this.field_147862_ak, this.field_147885_ae, this.field_147882_ag, i1);
            this.field_147876_an = this.func_147778_a(this.field_147866_ai, this.field_147879_ad, this.field_147885_ae, i1);
            this.field_147874_am = this.func_147778_a(this.field_147827_W, this.field_147835_X, this.field_147866_ai, i1);

            if (flag1)
            {
                this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = p_147751_5_ * 0.6F;
                this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = p_147751_6_ * 0.6F;
                this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = p_147751_7_ * 0.6F;
            }
            else
            {
                this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = 0.6F;
                this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = 0.6F;
                this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = 0.6F;
            }

            this.field_147872_ap *= f3;
            this.field_147846_at *= f3;
            this.field_147854_ax *= f3;
            this.field_147852_aq *= f4;
            this.field_147860_au *= f4;
            this.field_147841_ay *= f4;
            this.field_147850_ar *= f5;
            this.field_147858_av *= f5;
            this.field_147839_az *= f5;
            this.field_147848_as *= f6;
            this.field_147856_aw *= f6;
            this.field_147833_aA *= f6;
            iicon = this.func_147793_a(p_147751_1_, this.field_147845_a, p_147751_2_, p_147751_3_, p_147751_4_, 5);
            this.func_147764_f(p_147751_1_, (double)p_147751_2_, (double)p_147751_3_, (double)p_147751_4_, iicon);

            if (field_147843_b && iicon.getIconName().equals("grass_side") && !this.func_147744_b())
            {
                this.field_147872_ap *= p_147751_5_;
                this.field_147852_aq *= p_147751_5_;
                this.field_147850_ar *= p_147751_5_;
                this.field_147848_as *= p_147751_5_;
                this.field_147846_at *= p_147751_6_;
                this.field_147860_au *= p_147751_6_;
                this.field_147858_av *= p_147751_6_;
                this.field_147856_aw *= p_147751_6_;
                this.field_147854_ax *= p_147751_7_;
                this.field_147841_ay *= p_147751_7_;
                this.field_147839_az *= p_147751_7_;
                this.field_147833_aA *= p_147751_7_;
                this.func_147764_f(p_147751_1_, (double)p_147751_2_, (double)p_147751_3_, (double)p_147751_4_, BlockGrass.func_149990_e());
            }

            flag = true;
        }

        this.field_147863_w = false;
        return flag;
    }

    public boolean func_147808_b(Block p_147808_1_, int p_147808_2_, int p_147808_3_, int p_147808_4_, float p_147808_5_, float p_147808_6_, float p_147808_7_)
    {
        this.field_147863_w = true;
        boolean flag = false;
        float f3 = 0.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        boolean flag1 = true;
        int l = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_, p_147808_3_, p_147808_4_);
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(983055);

        if (this.func_147745_b(p_147808_1_).getIconName().equals("grass_top"))
        {
            flag1 = false;
        }
        else if (this.func_147744_b())
        {
            flag1 = false;
        }

        boolean flag3;
        boolean flag2;
        boolean flag5;
        boolean flag4;
        float f7;
        int i1;

        if (this.field_147837_f || p_147808_1_.func_149646_a(this.field_147845_a, p_147808_2_, p_147808_3_ - 1, p_147808_4_, 0))
        {
            if (this.field_147855_j <= 0.0D)
            {
                --p_147808_3_;
            }

            this.field_147831_S = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_ - 1, p_147808_3_, p_147808_4_);
            this.field_147825_U = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_, p_147808_3_, p_147808_4_ - 1);
            this.field_147828_V = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_, p_147808_3_, p_147808_4_ + 1);
            this.field_147835_X = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_ + 1, p_147808_3_, p_147808_4_);
            this.field_147886_y = this.field_147845_a.func_147439_a(p_147808_2_ - 1, p_147808_3_, p_147808_4_).func_149685_I();
            this.field_147814_A = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_, p_147808_4_ - 1).func_149685_I();
            this.field_147815_B = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_, p_147808_4_ + 1).func_149685_I();
            this.field_147810_D = this.field_147845_a.func_147439_a(p_147808_2_ + 1, p_147808_3_, p_147808_4_).func_149685_I();
            flag2 = this.field_147845_a.func_147439_a(p_147808_2_ + 1, p_147808_3_ - 1, p_147808_4_).func_149751_l();
            flag3 = this.field_147845_a.func_147439_a(p_147808_2_ - 1, p_147808_3_ - 1, p_147808_4_).func_149751_l();
            flag4 = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_ - 1, p_147808_4_ + 1).func_149751_l();
            flag5 = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_ - 1, p_147808_4_ - 1).func_149751_l();

            if (!flag5 && !flag3)
            {
                this.field_147888_x = this.field_147886_y;
                this.field_147832_R = this.field_147831_S;
            }
            else
            {
                this.field_147888_x = this.field_147845_a.func_147439_a(p_147808_2_ - 1, p_147808_3_, p_147808_4_ - 1).func_149685_I();
                this.field_147832_R = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_ - 1, p_147808_3_, p_147808_4_ - 1);
            }

            if (!flag4 && !flag3)
            {
                this.field_147884_z = this.field_147886_y;
                this.field_147826_T = this.field_147831_S;
            }
            else
            {
                this.field_147884_z = this.field_147845_a.func_147439_a(p_147808_2_ - 1, p_147808_3_, p_147808_4_ + 1).func_149685_I();
                this.field_147826_T = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_ - 1, p_147808_3_, p_147808_4_ + 1);
            }

            if (!flag5 && !flag2)
            {
                this.field_147816_C = this.field_147810_D;
                this.field_147827_W = this.field_147835_X;
            }
            else
            {
                this.field_147816_C = this.field_147845_a.func_147439_a(p_147808_2_ + 1, p_147808_3_, p_147808_4_ - 1).func_149685_I();
                this.field_147827_W = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_ + 1, p_147808_3_, p_147808_4_ - 1);
            }

            if (!flag4 && !flag2)
            {
                this.field_147811_E = this.field_147810_D;
                this.field_147834_Y = this.field_147835_X;
            }
            else
            {
                this.field_147811_E = this.field_147845_a.func_147439_a(p_147808_2_ + 1, p_147808_3_, p_147808_4_ + 1).func_149685_I();
                this.field_147834_Y = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_ + 1, p_147808_3_, p_147808_4_ + 1);
            }

            if (this.field_147855_j <= 0.0D)
            {
                ++p_147808_3_;
            }

            i1 = l;

            if (this.field_147855_j <= 0.0D || !this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_ - 1, p_147808_4_).func_149662_c())
            {
                i1 = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_, p_147808_3_ - 1, p_147808_4_);
            }

            f7 = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_ - 1, p_147808_4_).func_149685_I();
            f3 = (this.field_147884_z + this.field_147886_y + this.field_147815_B + f7) / 4.0F;
            f6 = (this.field_147815_B + f7 + this.field_147811_E + this.field_147810_D) / 4.0F;
            f5 = (f7 + this.field_147814_A + this.field_147810_D + this.field_147816_C) / 4.0F;
            f4 = (this.field_147886_y + this.field_147888_x + f7 + this.field_147814_A) / 4.0F;
            this.field_147864_al = this.func_147778_a(this.field_147826_T, this.field_147831_S, this.field_147828_V, i1);
            this.field_147870_ao = this.func_147778_a(this.field_147828_V, this.field_147834_Y, this.field_147835_X, i1);
            this.field_147876_an = this.func_147778_a(this.field_147825_U, this.field_147835_X, this.field_147827_W, i1);
            this.field_147874_am = this.func_147778_a(this.field_147831_S, this.field_147832_R, this.field_147825_U, i1);

            if (flag1)
            {
                this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = p_147808_5_ * 0.5F;
                this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = p_147808_6_ * 0.5F;
                this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = p_147808_7_ * 0.5F;
            }
            else
            {
                this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = 0.5F;
                this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = 0.5F;
                this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = 0.5F;
            }

            this.field_147872_ap *= f3;
            this.field_147846_at *= f3;
            this.field_147854_ax *= f3;
            this.field_147852_aq *= f4;
            this.field_147860_au *= f4;
            this.field_147841_ay *= f4;
            this.field_147850_ar *= f5;
            this.field_147858_av *= f5;
            this.field_147839_az *= f5;
            this.field_147848_as *= f6;
            this.field_147856_aw *= f6;
            this.field_147833_aA *= f6;
            this.func_147768_a(p_147808_1_, (double)p_147808_2_, (double)p_147808_3_, (double)p_147808_4_, this.func_147793_a(p_147808_1_, this.field_147845_a, p_147808_2_, p_147808_3_, p_147808_4_, 0));
            flag = true;
        }

        if (this.field_147837_f || p_147808_1_.func_149646_a(this.field_147845_a, p_147808_2_, p_147808_3_ + 1, p_147808_4_, 1))
        {
            if (this.field_147857_k >= 1.0D)
            {
                ++p_147808_3_;
            }

            this.field_147880_aa = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_ - 1, p_147808_3_, p_147808_4_);
            this.field_147885_ae = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_ + 1, p_147808_3_, p_147808_4_);
            this.field_147878_ac = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_, p_147808_3_, p_147808_4_ - 1);
            this.field_147887_af = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_, p_147808_3_, p_147808_4_ + 1);
            this.field_147813_G = this.field_147845_a.func_147439_a(p_147808_2_ - 1, p_147808_3_, p_147808_4_).func_149685_I();
            this.field_147824_K = this.field_147845_a.func_147439_a(p_147808_2_ + 1, p_147808_3_, p_147808_4_).func_149685_I();
            this.field_147822_I = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_, p_147808_4_ - 1).func_149685_I();
            this.field_147817_L = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_, p_147808_4_ + 1).func_149685_I();
            flag2 = this.field_147845_a.func_147439_a(p_147808_2_ + 1, p_147808_3_ + 1, p_147808_4_).func_149751_l();
            flag3 = this.field_147845_a.func_147439_a(p_147808_2_ - 1, p_147808_3_ + 1, p_147808_4_).func_149751_l();
            flag4 = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_ + 1, p_147808_4_ + 1).func_149751_l();
            flag5 = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_ + 1, p_147808_4_ - 1).func_149751_l();

            if (!flag5 && !flag3)
            {
                this.field_147812_F = this.field_147813_G;
                this.field_147836_Z = this.field_147880_aa;
            }
            else
            {
                this.field_147812_F = this.field_147845_a.func_147439_a(p_147808_2_ - 1, p_147808_3_, p_147808_4_ - 1).func_149685_I();
                this.field_147836_Z = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_ - 1, p_147808_3_, p_147808_4_ - 1);
            }

            if (!flag5 && !flag2)
            {
                this.field_147823_J = this.field_147824_K;
                this.field_147879_ad = this.field_147885_ae;
            }
            else
            {
                this.field_147823_J = this.field_147845_a.func_147439_a(p_147808_2_ + 1, p_147808_3_, p_147808_4_ - 1).func_149685_I();
                this.field_147879_ad = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_ + 1, p_147808_3_, p_147808_4_ - 1);
            }

            if (!flag4 && !flag3)
            {
                this.field_147821_H = this.field_147813_G;
                this.field_147881_ab = this.field_147880_aa;
            }
            else
            {
                this.field_147821_H = this.field_147845_a.func_147439_a(p_147808_2_ - 1, p_147808_3_, p_147808_4_ + 1).func_149685_I();
                this.field_147881_ab = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_ - 1, p_147808_3_, p_147808_4_ + 1);
            }

            if (!flag4 && !flag2)
            {
                this.field_147818_M = this.field_147824_K;
                this.field_147882_ag = this.field_147885_ae;
            }
            else
            {
                this.field_147818_M = this.field_147845_a.func_147439_a(p_147808_2_ + 1, p_147808_3_, p_147808_4_ + 1).func_149685_I();
                this.field_147882_ag = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_ + 1, p_147808_3_, p_147808_4_ + 1);
            }

            if (this.field_147857_k >= 1.0D)
            {
                --p_147808_3_;
            }

            i1 = l;

            if (this.field_147857_k >= 1.0D || !this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_ + 1, p_147808_4_).func_149662_c())
            {
                i1 = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_, p_147808_3_ + 1, p_147808_4_);
            }

            f7 = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_ + 1, p_147808_4_).func_149685_I();
            f6 = (this.field_147821_H + this.field_147813_G + this.field_147817_L + f7) / 4.0F;
            f3 = (this.field_147817_L + f7 + this.field_147818_M + this.field_147824_K) / 4.0F;
            f4 = (f7 + this.field_147822_I + this.field_147824_K + this.field_147823_J) / 4.0F;
            f5 = (this.field_147813_G + this.field_147812_F + f7 + this.field_147822_I) / 4.0F;
            this.field_147870_ao = this.func_147778_a(this.field_147881_ab, this.field_147880_aa, this.field_147887_af, i1);
            this.field_147864_al = this.func_147778_a(this.field_147887_af, this.field_147882_ag, this.field_147885_ae, i1);
            this.field_147874_am = this.func_147778_a(this.field_147878_ac, this.field_147885_ae, this.field_147879_ad, i1);
            this.field_147876_an = this.func_147778_a(this.field_147880_aa, this.field_147836_Z, this.field_147878_ac, i1);
            this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = p_147808_5_;
            this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = p_147808_6_;
            this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = p_147808_7_;
            this.field_147872_ap *= f3;
            this.field_147846_at *= f3;
            this.field_147854_ax *= f3;
            this.field_147852_aq *= f4;
            this.field_147860_au *= f4;
            this.field_147841_ay *= f4;
            this.field_147850_ar *= f5;
            this.field_147858_av *= f5;
            this.field_147839_az *= f5;
            this.field_147848_as *= f6;
            this.field_147856_aw *= f6;
            this.field_147833_aA *= f6;
            this.func_147806_b(p_147808_1_, (double)p_147808_2_, (double)p_147808_3_, (double)p_147808_4_, this.func_147793_a(p_147808_1_, this.field_147845_a, p_147808_2_, p_147808_3_, p_147808_4_, 1));
            flag = true;
        }

        float f9;
        float f8;
        float f11;
        float f10;
        int k1;
        int j1;
        int i2;
        int l1;
        IIcon iicon;

        if (this.field_147837_f || p_147808_1_.func_149646_a(this.field_147845_a, p_147808_2_, p_147808_3_, p_147808_4_ - 1, 2))
        {
            if (this.field_147851_l <= 0.0D)
            {
                --p_147808_4_;
            }

            this.field_147819_N = this.field_147845_a.func_147439_a(p_147808_2_ - 1, p_147808_3_, p_147808_4_).func_149685_I();
            this.field_147814_A = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_ - 1, p_147808_4_).func_149685_I();
            this.field_147822_I = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_ + 1, p_147808_4_).func_149685_I();
            this.field_147820_O = this.field_147845_a.func_147439_a(p_147808_2_ + 1, p_147808_3_, p_147808_4_).func_149685_I();
            this.field_147883_ah = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_ - 1, p_147808_3_, p_147808_4_);
            this.field_147825_U = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_, p_147808_3_ - 1, p_147808_4_);
            this.field_147878_ac = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_, p_147808_3_ + 1, p_147808_4_);
            this.field_147866_ai = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_ + 1, p_147808_3_, p_147808_4_);
            flag2 = this.field_147845_a.func_147439_a(p_147808_2_ + 1, p_147808_3_, p_147808_4_ - 1).func_149751_l();
            flag3 = this.field_147845_a.func_147439_a(p_147808_2_ - 1, p_147808_3_, p_147808_4_ - 1).func_149751_l();
            flag4 = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_ + 1, p_147808_4_ - 1).func_149751_l();
            flag5 = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_ - 1, p_147808_4_ - 1).func_149751_l();

            if (!flag3 && !flag5)
            {
                this.field_147888_x = this.field_147819_N;
                this.field_147832_R = this.field_147883_ah;
            }
            else
            {
                this.field_147888_x = this.field_147845_a.func_147439_a(p_147808_2_ - 1, p_147808_3_ - 1, p_147808_4_).func_149685_I();
                this.field_147832_R = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_ - 1, p_147808_3_ - 1, p_147808_4_);
            }

            if (!flag3 && !flag4)
            {
                this.field_147812_F = this.field_147819_N;
                this.field_147836_Z = this.field_147883_ah;
            }
            else
            {
                this.field_147812_F = this.field_147845_a.func_147439_a(p_147808_2_ - 1, p_147808_3_ + 1, p_147808_4_).func_149685_I();
                this.field_147836_Z = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_ - 1, p_147808_3_ + 1, p_147808_4_);
            }

            if (!flag2 && !flag5)
            {
                this.field_147816_C = this.field_147820_O;
                this.field_147827_W = this.field_147866_ai;
            }
            else
            {
                this.field_147816_C = this.field_147845_a.func_147439_a(p_147808_2_ + 1, p_147808_3_ - 1, p_147808_4_).func_149685_I();
                this.field_147827_W = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_ + 1, p_147808_3_ - 1, p_147808_4_);
            }

            if (!flag2 && !flag4)
            {
                this.field_147823_J = this.field_147820_O;
                this.field_147879_ad = this.field_147866_ai;
            }
            else
            {
                this.field_147823_J = this.field_147845_a.func_147439_a(p_147808_2_ + 1, p_147808_3_ + 1, p_147808_4_).func_149685_I();
                this.field_147879_ad = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_ + 1, p_147808_3_ + 1, p_147808_4_);
            }

            if (this.field_147851_l <= 0.0D)
            {
                ++p_147808_4_;
            }

            i1 = l;

            if (this.field_147851_l <= 0.0D || !this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_, p_147808_4_ - 1).func_149662_c())
            {
                i1 = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_, p_147808_3_, p_147808_4_ - 1);
            }

            f7 = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_, p_147808_4_ - 1).func_149685_I();
            f8 = (this.field_147819_N + this.field_147812_F + f7 + this.field_147822_I) / 4.0F;
            f9 = (f7 + this.field_147822_I + this.field_147820_O + this.field_147823_J) / 4.0F;
            f10 = (this.field_147814_A + f7 + this.field_147816_C + this.field_147820_O) / 4.0F;
            f11 = (this.field_147888_x + this.field_147819_N + this.field_147814_A + f7) / 4.0F;
            f3 = (float)((double)f8 * this.field_147857_k * (1.0D - this.field_147859_h) + (double)f9 * this.field_147857_k * this.field_147859_h + (double)f10 * (1.0D - this.field_147857_k) * this.field_147859_h + (double)f11 * (1.0D - this.field_147857_k) * (1.0D - this.field_147859_h));
            f4 = (float)((double)f8 * this.field_147857_k * (1.0D - this.field_147861_i) + (double)f9 * this.field_147857_k * this.field_147861_i + (double)f10 * (1.0D - this.field_147857_k) * this.field_147861_i + (double)f11 * (1.0D - this.field_147857_k) * (1.0D - this.field_147861_i));
            f5 = (float)((double)f8 * this.field_147855_j * (1.0D - this.field_147861_i) + (double)f9 * this.field_147855_j * this.field_147861_i + (double)f10 * (1.0D - this.field_147855_j) * this.field_147861_i + (double)f11 * (1.0D - this.field_147855_j) * (1.0D - this.field_147861_i));
            f6 = (float)((double)f8 * this.field_147855_j * (1.0D - this.field_147859_h) + (double)f9 * this.field_147855_j * this.field_147859_h + (double)f10 * (1.0D - this.field_147855_j) * this.field_147859_h + (double)f11 * (1.0D - this.field_147855_j) * (1.0D - this.field_147859_h));
            j1 = this.func_147778_a(this.field_147883_ah, this.field_147836_Z, this.field_147878_ac, i1);
            k1 = this.func_147778_a(this.field_147878_ac, this.field_147866_ai, this.field_147879_ad, i1);
            l1 = this.func_147778_a(this.field_147825_U, this.field_147827_W, this.field_147866_ai, i1);
            i2 = this.func_147778_a(this.field_147832_R, this.field_147883_ah, this.field_147825_U, i1);
            this.field_147864_al = this.func_147727_a(j1, k1, l1, i2, this.field_147857_k * (1.0D - this.field_147859_h), this.field_147857_k * this.field_147859_h, (1.0D - this.field_147857_k) * this.field_147859_h, (1.0D - this.field_147857_k) * (1.0D - this.field_147859_h));
            this.field_147874_am = this.func_147727_a(j1, k1, l1, i2, this.field_147857_k * (1.0D - this.field_147861_i), this.field_147857_k * this.field_147861_i, (1.0D - this.field_147857_k) * this.field_147861_i, (1.0D - this.field_147857_k) * (1.0D - this.field_147861_i));
            this.field_147876_an = this.func_147727_a(j1, k1, l1, i2, this.field_147855_j * (1.0D - this.field_147861_i), this.field_147855_j * this.field_147861_i, (1.0D - this.field_147855_j) * this.field_147861_i, (1.0D - this.field_147855_j) * (1.0D - this.field_147861_i));
            this.field_147870_ao = this.func_147727_a(j1, k1, l1, i2, this.field_147855_j * (1.0D - this.field_147859_h), this.field_147855_j * this.field_147859_h, (1.0D - this.field_147855_j) * this.field_147859_h, (1.0D - this.field_147855_j) * (1.0D - this.field_147859_h));

            if (flag1)
            {
                this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = p_147808_5_ * 0.8F;
                this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = p_147808_6_ * 0.8F;
                this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = p_147808_7_ * 0.8F;
            }
            else
            {
                this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = 0.8F;
                this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = 0.8F;
                this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = 0.8F;
            }

            this.field_147872_ap *= f3;
            this.field_147846_at *= f3;
            this.field_147854_ax *= f3;
            this.field_147852_aq *= f4;
            this.field_147860_au *= f4;
            this.field_147841_ay *= f4;
            this.field_147850_ar *= f5;
            this.field_147858_av *= f5;
            this.field_147839_az *= f5;
            this.field_147848_as *= f6;
            this.field_147856_aw *= f6;
            this.field_147833_aA *= f6;
            iicon = this.func_147793_a(p_147808_1_, this.field_147845_a, p_147808_2_, p_147808_3_, p_147808_4_, 2);
            this.func_147761_c(p_147808_1_, (double)p_147808_2_, (double)p_147808_3_, (double)p_147808_4_, iicon);

            if (field_147843_b && iicon.getIconName().equals("grass_side") && !this.func_147744_b())
            {
                this.field_147872_ap *= p_147808_5_;
                this.field_147852_aq *= p_147808_5_;
                this.field_147850_ar *= p_147808_5_;
                this.field_147848_as *= p_147808_5_;
                this.field_147846_at *= p_147808_6_;
                this.field_147860_au *= p_147808_6_;
                this.field_147858_av *= p_147808_6_;
                this.field_147856_aw *= p_147808_6_;
                this.field_147854_ax *= p_147808_7_;
                this.field_147841_ay *= p_147808_7_;
                this.field_147839_az *= p_147808_7_;
                this.field_147833_aA *= p_147808_7_;
                this.func_147761_c(p_147808_1_, (double)p_147808_2_, (double)p_147808_3_, (double)p_147808_4_, BlockGrass.func_149990_e());
            }

            flag = true;
        }

        if (this.field_147837_f || p_147808_1_.func_149646_a(this.field_147845_a, p_147808_2_, p_147808_3_, p_147808_4_ + 1, 3))
        {
            if (this.field_147853_m >= 1.0D)
            {
                ++p_147808_4_;
            }

            this.field_147830_P = this.field_147845_a.func_147439_a(p_147808_2_ - 1, p_147808_3_, p_147808_4_).func_149685_I();
            this.field_147829_Q = this.field_147845_a.func_147439_a(p_147808_2_ + 1, p_147808_3_, p_147808_4_).func_149685_I();
            this.field_147815_B = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_ - 1, p_147808_4_).func_149685_I();
            this.field_147817_L = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_ + 1, p_147808_4_).func_149685_I();
            this.field_147868_aj = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_ - 1, p_147808_3_, p_147808_4_);
            this.field_147862_ak = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_ + 1, p_147808_3_, p_147808_4_);
            this.field_147828_V = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_, p_147808_3_ - 1, p_147808_4_);
            this.field_147887_af = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_, p_147808_3_ + 1, p_147808_4_);
            flag2 = this.field_147845_a.func_147439_a(p_147808_2_ + 1, p_147808_3_, p_147808_4_ + 1).func_149751_l();
            flag3 = this.field_147845_a.func_147439_a(p_147808_2_ - 1, p_147808_3_, p_147808_4_ + 1).func_149751_l();
            flag4 = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_ + 1, p_147808_4_ + 1).func_149751_l();
            flag5 = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_ - 1, p_147808_4_ + 1).func_149751_l();

            if (!flag3 && !flag5)
            {
                this.field_147884_z = this.field_147830_P;
                this.field_147826_T = this.field_147868_aj;
            }
            else
            {
                this.field_147884_z = this.field_147845_a.func_147439_a(p_147808_2_ - 1, p_147808_3_ - 1, p_147808_4_).func_149685_I();
                this.field_147826_T = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_ - 1, p_147808_3_ - 1, p_147808_4_);
            }

            if (!flag3 && !flag4)
            {
                this.field_147821_H = this.field_147830_P;
                this.field_147881_ab = this.field_147868_aj;
            }
            else
            {
                this.field_147821_H = this.field_147845_a.func_147439_a(p_147808_2_ - 1, p_147808_3_ + 1, p_147808_4_).func_149685_I();
                this.field_147881_ab = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_ - 1, p_147808_3_ + 1, p_147808_4_);
            }

            if (!flag2 && !flag5)
            {
                this.field_147811_E = this.field_147829_Q;
                this.field_147834_Y = this.field_147862_ak;
            }
            else
            {
                this.field_147811_E = this.field_147845_a.func_147439_a(p_147808_2_ + 1, p_147808_3_ - 1, p_147808_4_).func_149685_I();
                this.field_147834_Y = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_ + 1, p_147808_3_ - 1, p_147808_4_);
            }

            if (!flag2 && !flag4)
            {
                this.field_147818_M = this.field_147829_Q;
                this.field_147882_ag = this.field_147862_ak;
            }
            else
            {
                this.field_147818_M = this.field_147845_a.func_147439_a(p_147808_2_ + 1, p_147808_3_ + 1, p_147808_4_).func_149685_I();
                this.field_147882_ag = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_ + 1, p_147808_3_ + 1, p_147808_4_);
            }

            if (this.field_147853_m >= 1.0D)
            {
                --p_147808_4_;
            }

            i1 = l;

            if (this.field_147853_m >= 1.0D || !this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_, p_147808_4_ + 1).func_149662_c())
            {
                i1 = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_, p_147808_3_, p_147808_4_ + 1);
            }

            f7 = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_, p_147808_4_ + 1).func_149685_I();
            f8 = (this.field_147830_P + this.field_147821_H + f7 + this.field_147817_L) / 4.0F;
            f9 = (f7 + this.field_147817_L + this.field_147829_Q + this.field_147818_M) / 4.0F;
            f10 = (this.field_147815_B + f7 + this.field_147811_E + this.field_147829_Q) / 4.0F;
            f11 = (this.field_147884_z + this.field_147830_P + this.field_147815_B + f7) / 4.0F;
            f3 = (float)((double)f8 * this.field_147857_k * (1.0D - this.field_147859_h) + (double)f9 * this.field_147857_k * this.field_147859_h + (double)f10 * (1.0D - this.field_147857_k) * this.field_147859_h + (double)f11 * (1.0D - this.field_147857_k) * (1.0D - this.field_147859_h));
            f4 = (float)((double)f8 * this.field_147855_j * (1.0D - this.field_147859_h) + (double)f9 * this.field_147855_j * this.field_147859_h + (double)f10 * (1.0D - this.field_147855_j) * this.field_147859_h + (double)f11 * (1.0D - this.field_147855_j) * (1.0D - this.field_147859_h));
            f5 = (float)((double)f8 * this.field_147855_j * (1.0D - this.field_147861_i) + (double)f9 * this.field_147855_j * this.field_147861_i + (double)f10 * (1.0D - this.field_147855_j) * this.field_147861_i + (double)f11 * (1.0D - this.field_147855_j) * (1.0D - this.field_147861_i));
            f6 = (float)((double)f8 * this.field_147857_k * (1.0D - this.field_147861_i) + (double)f9 * this.field_147857_k * this.field_147861_i + (double)f10 * (1.0D - this.field_147857_k) * this.field_147861_i + (double)f11 * (1.0D - this.field_147857_k) * (1.0D - this.field_147861_i));
            j1 = this.func_147778_a(this.field_147868_aj, this.field_147881_ab, this.field_147887_af, i1);
            k1 = this.func_147778_a(this.field_147887_af, this.field_147862_ak, this.field_147882_ag, i1);
            l1 = this.func_147778_a(this.field_147828_V, this.field_147834_Y, this.field_147862_ak, i1);
            i2 = this.func_147778_a(this.field_147826_T, this.field_147868_aj, this.field_147828_V, i1);
            this.field_147864_al = this.func_147727_a(j1, i2, l1, k1, this.field_147857_k * (1.0D - this.field_147859_h), (1.0D - this.field_147857_k) * (1.0D - this.field_147859_h), (1.0D - this.field_147857_k) * this.field_147859_h, this.field_147857_k * this.field_147859_h);
            this.field_147874_am = this.func_147727_a(j1, i2, l1, k1, this.field_147855_j * (1.0D - this.field_147859_h), (1.0D - this.field_147855_j) * (1.0D - this.field_147859_h), (1.0D - this.field_147855_j) * this.field_147859_h, this.field_147855_j * this.field_147859_h);
            this.field_147876_an = this.func_147727_a(j1, i2, l1, k1, this.field_147855_j * (1.0D - this.field_147861_i), (1.0D - this.field_147855_j) * (1.0D - this.field_147861_i), (1.0D - this.field_147855_j) * this.field_147861_i, this.field_147855_j * this.field_147861_i);
            this.field_147870_ao = this.func_147727_a(j1, i2, l1, k1, this.field_147857_k * (1.0D - this.field_147861_i), (1.0D - this.field_147857_k) * (1.0D - this.field_147861_i), (1.0D - this.field_147857_k) * this.field_147861_i, this.field_147857_k * this.field_147861_i);

            if (flag1)
            {
                this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = p_147808_5_ * 0.8F;
                this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = p_147808_6_ * 0.8F;
                this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = p_147808_7_ * 0.8F;
            }
            else
            {
                this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = 0.8F;
                this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = 0.8F;
                this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = 0.8F;
            }

            this.field_147872_ap *= f3;
            this.field_147846_at *= f3;
            this.field_147854_ax *= f3;
            this.field_147852_aq *= f4;
            this.field_147860_au *= f4;
            this.field_147841_ay *= f4;
            this.field_147850_ar *= f5;
            this.field_147858_av *= f5;
            this.field_147839_az *= f5;
            this.field_147848_as *= f6;
            this.field_147856_aw *= f6;
            this.field_147833_aA *= f6;
            iicon = this.func_147793_a(p_147808_1_, this.field_147845_a, p_147808_2_, p_147808_3_, p_147808_4_, 3);
            this.func_147734_d(p_147808_1_, (double)p_147808_2_, (double)p_147808_3_, (double)p_147808_4_, iicon);

            if (field_147843_b && iicon.getIconName().equals("grass_side") && !this.func_147744_b())
            {
                this.field_147872_ap *= p_147808_5_;
                this.field_147852_aq *= p_147808_5_;
                this.field_147850_ar *= p_147808_5_;
                this.field_147848_as *= p_147808_5_;
                this.field_147846_at *= p_147808_6_;
                this.field_147860_au *= p_147808_6_;
                this.field_147858_av *= p_147808_6_;
                this.field_147856_aw *= p_147808_6_;
                this.field_147854_ax *= p_147808_7_;
                this.field_147841_ay *= p_147808_7_;
                this.field_147839_az *= p_147808_7_;
                this.field_147833_aA *= p_147808_7_;
                this.func_147734_d(p_147808_1_, (double)p_147808_2_, (double)p_147808_3_, (double)p_147808_4_, BlockGrass.func_149990_e());
            }

            flag = true;
        }

        if (this.field_147837_f || p_147808_1_.func_149646_a(this.field_147845_a, p_147808_2_ - 1, p_147808_3_, p_147808_4_, 4))
        {
            if (this.field_147859_h <= 0.0D)
            {
                --p_147808_2_;
            }

            this.field_147886_y = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_ - 1, p_147808_4_).func_149685_I();
            this.field_147819_N = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_, p_147808_4_ - 1).func_149685_I();
            this.field_147830_P = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_, p_147808_4_ + 1).func_149685_I();
            this.field_147813_G = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_ + 1, p_147808_4_).func_149685_I();
            this.field_147831_S = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_, p_147808_3_ - 1, p_147808_4_);
            this.field_147883_ah = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_, p_147808_3_, p_147808_4_ - 1);
            this.field_147868_aj = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_, p_147808_3_, p_147808_4_ + 1);
            this.field_147880_aa = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_, p_147808_3_ + 1, p_147808_4_);
            flag2 = this.field_147845_a.func_147439_a(p_147808_2_ - 1, p_147808_3_ + 1, p_147808_4_).func_149751_l();
            flag3 = this.field_147845_a.func_147439_a(p_147808_2_ - 1, p_147808_3_ - 1, p_147808_4_).func_149751_l();
            flag4 = this.field_147845_a.func_147439_a(p_147808_2_ - 1, p_147808_3_, p_147808_4_ - 1).func_149751_l();
            flag5 = this.field_147845_a.func_147439_a(p_147808_2_ - 1, p_147808_3_, p_147808_4_ + 1).func_149751_l();

            if (!flag4 && !flag3)
            {
                this.field_147888_x = this.field_147819_N;
                this.field_147832_R = this.field_147883_ah;
            }
            else
            {
                this.field_147888_x = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_ - 1, p_147808_4_ - 1).func_149685_I();
                this.field_147832_R = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_, p_147808_3_ - 1, p_147808_4_ - 1);
            }

            if (!flag5 && !flag3)
            {
                this.field_147884_z = this.field_147830_P;
                this.field_147826_T = this.field_147868_aj;
            }
            else
            {
                this.field_147884_z = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_ - 1, p_147808_4_ + 1).func_149685_I();
                this.field_147826_T = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_, p_147808_3_ - 1, p_147808_4_ + 1);
            }

            if (!flag4 && !flag2)
            {
                this.field_147812_F = this.field_147819_N;
                this.field_147836_Z = this.field_147883_ah;
            }
            else
            {
                this.field_147812_F = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_ + 1, p_147808_4_ - 1).func_149685_I();
                this.field_147836_Z = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_, p_147808_3_ + 1, p_147808_4_ - 1);
            }

            if (!flag5 && !flag2)
            {
                this.field_147821_H = this.field_147830_P;
                this.field_147881_ab = this.field_147868_aj;
            }
            else
            {
                this.field_147821_H = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_ + 1, p_147808_4_ + 1).func_149685_I();
                this.field_147881_ab = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_, p_147808_3_ + 1, p_147808_4_ + 1);
            }

            if (this.field_147859_h <= 0.0D)
            {
                ++p_147808_2_;
            }

            i1 = l;

            if (this.field_147859_h <= 0.0D || !this.field_147845_a.func_147439_a(p_147808_2_ - 1, p_147808_3_, p_147808_4_).func_149662_c())
            {
                i1 = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_ - 1, p_147808_3_, p_147808_4_);
            }

            f7 = this.field_147845_a.func_147439_a(p_147808_2_ - 1, p_147808_3_, p_147808_4_).func_149685_I();
            f8 = (this.field_147886_y + this.field_147884_z + f7 + this.field_147830_P) / 4.0F;
            f9 = (f7 + this.field_147830_P + this.field_147813_G + this.field_147821_H) / 4.0F;
            f10 = (this.field_147819_N + f7 + this.field_147812_F + this.field_147813_G) / 4.0F;
            f11 = (this.field_147888_x + this.field_147886_y + this.field_147819_N + f7) / 4.0F;
            f3 = (float)((double)f9 * this.field_147857_k * this.field_147853_m + (double)f10 * this.field_147857_k * (1.0D - this.field_147853_m) + (double)f11 * (1.0D - this.field_147857_k) * (1.0D - this.field_147853_m) + (double)f8 * (1.0D - this.field_147857_k) * this.field_147853_m);
            f4 = (float)((double)f9 * this.field_147857_k * this.field_147851_l + (double)f10 * this.field_147857_k * (1.0D - this.field_147851_l) + (double)f11 * (1.0D - this.field_147857_k) * (1.0D - this.field_147851_l) + (double)f8 * (1.0D - this.field_147857_k) * this.field_147851_l);
            f5 = (float)((double)f9 * this.field_147855_j * this.field_147851_l + (double)f10 * this.field_147855_j * (1.0D - this.field_147851_l) + (double)f11 * (1.0D - this.field_147855_j) * (1.0D - this.field_147851_l) + (double)f8 * (1.0D - this.field_147855_j) * this.field_147851_l);
            f6 = (float)((double)f9 * this.field_147855_j * this.field_147853_m + (double)f10 * this.field_147855_j * (1.0D - this.field_147853_m) + (double)f11 * (1.0D - this.field_147855_j) * (1.0D - this.field_147853_m) + (double)f8 * (1.0D - this.field_147855_j) * this.field_147853_m);
            j1 = this.func_147778_a(this.field_147831_S, this.field_147826_T, this.field_147868_aj, i1);
            k1 = this.func_147778_a(this.field_147868_aj, this.field_147880_aa, this.field_147881_ab, i1);
            l1 = this.func_147778_a(this.field_147883_ah, this.field_147836_Z, this.field_147880_aa, i1);
            i2 = this.func_147778_a(this.field_147832_R, this.field_147831_S, this.field_147883_ah, i1);
            this.field_147864_al = this.func_147727_a(k1, l1, i2, j1, this.field_147857_k * this.field_147853_m, this.field_147857_k * (1.0D - this.field_147853_m), (1.0D - this.field_147857_k) * (1.0D - this.field_147853_m), (1.0D - this.field_147857_k) * this.field_147853_m);
            this.field_147874_am = this.func_147727_a(k1, l1, i2, j1, this.field_147857_k * this.field_147851_l, this.field_147857_k * (1.0D - this.field_147851_l), (1.0D - this.field_147857_k) * (1.0D - this.field_147851_l), (1.0D - this.field_147857_k) * this.field_147851_l);
            this.field_147876_an = this.func_147727_a(k1, l1, i2, j1, this.field_147855_j * this.field_147851_l, this.field_147855_j * (1.0D - this.field_147851_l), (1.0D - this.field_147855_j) * (1.0D - this.field_147851_l), (1.0D - this.field_147855_j) * this.field_147851_l);
            this.field_147870_ao = this.func_147727_a(k1, l1, i2, j1, this.field_147855_j * this.field_147853_m, this.field_147855_j * (1.0D - this.field_147853_m), (1.0D - this.field_147855_j) * (1.0D - this.field_147853_m), (1.0D - this.field_147855_j) * this.field_147853_m);

            if (flag1)
            {
                this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = p_147808_5_ * 0.6F;
                this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = p_147808_6_ * 0.6F;
                this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = p_147808_7_ * 0.6F;
            }
            else
            {
                this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = 0.6F;
                this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = 0.6F;
                this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = 0.6F;
            }

            this.field_147872_ap *= f3;
            this.field_147846_at *= f3;
            this.field_147854_ax *= f3;
            this.field_147852_aq *= f4;
            this.field_147860_au *= f4;
            this.field_147841_ay *= f4;
            this.field_147850_ar *= f5;
            this.field_147858_av *= f5;
            this.field_147839_az *= f5;
            this.field_147848_as *= f6;
            this.field_147856_aw *= f6;
            this.field_147833_aA *= f6;
            iicon = this.func_147793_a(p_147808_1_, this.field_147845_a, p_147808_2_, p_147808_3_, p_147808_4_, 4);
            this.func_147798_e(p_147808_1_, (double)p_147808_2_, (double)p_147808_3_, (double)p_147808_4_, iicon);

            if (field_147843_b && iicon.getIconName().equals("grass_side") && !this.func_147744_b())
            {
                this.field_147872_ap *= p_147808_5_;
                this.field_147852_aq *= p_147808_5_;
                this.field_147850_ar *= p_147808_5_;
                this.field_147848_as *= p_147808_5_;
                this.field_147846_at *= p_147808_6_;
                this.field_147860_au *= p_147808_6_;
                this.field_147858_av *= p_147808_6_;
                this.field_147856_aw *= p_147808_6_;
                this.field_147854_ax *= p_147808_7_;
                this.field_147841_ay *= p_147808_7_;
                this.field_147839_az *= p_147808_7_;
                this.field_147833_aA *= p_147808_7_;
                this.func_147798_e(p_147808_1_, (double)p_147808_2_, (double)p_147808_3_, (double)p_147808_4_, BlockGrass.func_149990_e());
            }

            flag = true;
        }

        if (this.field_147837_f || p_147808_1_.func_149646_a(this.field_147845_a, p_147808_2_ + 1, p_147808_3_, p_147808_4_, 5))
        {
            if (this.field_147861_i >= 1.0D)
            {
                ++p_147808_2_;
            }

            this.field_147810_D = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_ - 1, p_147808_4_).func_149685_I();
            this.field_147820_O = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_, p_147808_4_ - 1).func_149685_I();
            this.field_147829_Q = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_, p_147808_4_ + 1).func_149685_I();
            this.field_147824_K = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_ + 1, p_147808_4_).func_149685_I();
            this.field_147835_X = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_, p_147808_3_ - 1, p_147808_4_);
            this.field_147866_ai = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_, p_147808_3_, p_147808_4_ - 1);
            this.field_147862_ak = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_, p_147808_3_, p_147808_4_ + 1);
            this.field_147885_ae = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_, p_147808_3_ + 1, p_147808_4_);
            flag2 = this.field_147845_a.func_147439_a(p_147808_2_ + 1, p_147808_3_ + 1, p_147808_4_).func_149751_l();
            flag3 = this.field_147845_a.func_147439_a(p_147808_2_ + 1, p_147808_3_ - 1, p_147808_4_).func_149751_l();
            flag4 = this.field_147845_a.func_147439_a(p_147808_2_ + 1, p_147808_3_, p_147808_4_ + 1).func_149751_l();
            flag5 = this.field_147845_a.func_147439_a(p_147808_2_ + 1, p_147808_3_, p_147808_4_ - 1).func_149751_l();

            if (!flag3 && !flag5)
            {
                this.field_147816_C = this.field_147820_O;
                this.field_147827_W = this.field_147866_ai;
            }
            else
            {
                this.field_147816_C = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_ - 1, p_147808_4_ - 1).func_149685_I();
                this.field_147827_W = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_, p_147808_3_ - 1, p_147808_4_ - 1);
            }

            if (!flag3 && !flag4)
            {
                this.field_147811_E = this.field_147829_Q;
                this.field_147834_Y = this.field_147862_ak;
            }
            else
            {
                this.field_147811_E = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_ - 1, p_147808_4_ + 1).func_149685_I();
                this.field_147834_Y = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_, p_147808_3_ - 1, p_147808_4_ + 1);
            }

            if (!flag2 && !flag5)
            {
                this.field_147823_J = this.field_147820_O;
                this.field_147879_ad = this.field_147866_ai;
            }
            else
            {
                this.field_147823_J = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_ + 1, p_147808_4_ - 1).func_149685_I();
                this.field_147879_ad = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_, p_147808_3_ + 1, p_147808_4_ - 1);
            }

            if (!flag2 && !flag4)
            {
                this.field_147818_M = this.field_147829_Q;
                this.field_147882_ag = this.field_147862_ak;
            }
            else
            {
                this.field_147818_M = this.field_147845_a.func_147439_a(p_147808_2_, p_147808_3_ + 1, p_147808_4_ + 1).func_149685_I();
                this.field_147882_ag = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_, p_147808_3_ + 1, p_147808_4_ + 1);
            }

            if (this.field_147861_i >= 1.0D)
            {
                --p_147808_2_;
            }

            i1 = l;

            if (this.field_147861_i >= 1.0D || !this.field_147845_a.func_147439_a(p_147808_2_ + 1, p_147808_3_, p_147808_4_).func_149662_c())
            {
                i1 = p_147808_1_.func_149677_c(this.field_147845_a, p_147808_2_ + 1, p_147808_3_, p_147808_4_);
            }

            f7 = this.field_147845_a.func_147439_a(p_147808_2_ + 1, p_147808_3_, p_147808_4_).func_149685_I();
            f8 = (this.field_147810_D + this.field_147811_E + f7 + this.field_147829_Q) / 4.0F;
            f9 = (this.field_147816_C + this.field_147810_D + this.field_147820_O + f7) / 4.0F;
            f10 = (this.field_147820_O + f7 + this.field_147823_J + this.field_147824_K) / 4.0F;
            f11 = (f7 + this.field_147829_Q + this.field_147824_K + this.field_147818_M) / 4.0F;
            f3 = (float)((double)f8 * (1.0D - this.field_147855_j) * this.field_147853_m + (double)f9 * (1.0D - this.field_147855_j) * (1.0D - this.field_147853_m) + (double)f10 * this.field_147855_j * (1.0D - this.field_147853_m) + (double)f11 * this.field_147855_j * this.field_147853_m);
            f4 = (float)((double)f8 * (1.0D - this.field_147855_j) * this.field_147851_l + (double)f9 * (1.0D - this.field_147855_j) * (1.0D - this.field_147851_l) + (double)f10 * this.field_147855_j * (1.0D - this.field_147851_l) + (double)f11 * this.field_147855_j * this.field_147851_l);
            f5 = (float)((double)f8 * (1.0D - this.field_147857_k) * this.field_147851_l + (double)f9 * (1.0D - this.field_147857_k) * (1.0D - this.field_147851_l) + (double)f10 * this.field_147857_k * (1.0D - this.field_147851_l) + (double)f11 * this.field_147857_k * this.field_147851_l);
            f6 = (float)((double)f8 * (1.0D - this.field_147857_k) * this.field_147853_m + (double)f9 * (1.0D - this.field_147857_k) * (1.0D - this.field_147853_m) + (double)f10 * this.field_147857_k * (1.0D - this.field_147853_m) + (double)f11 * this.field_147857_k * this.field_147853_m);
            j1 = this.func_147778_a(this.field_147835_X, this.field_147834_Y, this.field_147862_ak, i1);
            k1 = this.func_147778_a(this.field_147862_ak, this.field_147885_ae, this.field_147882_ag, i1);
            l1 = this.func_147778_a(this.field_147866_ai, this.field_147879_ad, this.field_147885_ae, i1);
            i2 = this.func_147778_a(this.field_147827_W, this.field_147835_X, this.field_147866_ai, i1);
            this.field_147864_al = this.func_147727_a(j1, i2, l1, k1, (1.0D - this.field_147855_j) * this.field_147853_m, (1.0D - this.field_147855_j) * (1.0D - this.field_147853_m), this.field_147855_j * (1.0D - this.field_147853_m), this.field_147855_j * this.field_147853_m);
            this.field_147874_am = this.func_147727_a(j1, i2, l1, k1, (1.0D - this.field_147855_j) * this.field_147851_l, (1.0D - this.field_147855_j) * (1.0D - this.field_147851_l), this.field_147855_j * (1.0D - this.field_147851_l), this.field_147855_j * this.field_147851_l);
            this.field_147876_an = this.func_147727_a(j1, i2, l1, k1, (1.0D - this.field_147857_k) * this.field_147851_l, (1.0D - this.field_147857_k) * (1.0D - this.field_147851_l), this.field_147857_k * (1.0D - this.field_147851_l), this.field_147857_k * this.field_147851_l);
            this.field_147870_ao = this.func_147727_a(j1, i2, l1, k1, (1.0D - this.field_147857_k) * this.field_147853_m, (1.0D - this.field_147857_k) * (1.0D - this.field_147853_m), this.field_147857_k * (1.0D - this.field_147853_m), this.field_147857_k * this.field_147853_m);

            if (flag1)
            {
                this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = p_147808_5_ * 0.6F;
                this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = p_147808_6_ * 0.6F;
                this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = p_147808_7_ * 0.6F;
            }
            else
            {
                this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = 0.6F;
                this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = 0.6F;
                this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = 0.6F;
            }

            this.field_147872_ap *= f3;
            this.field_147846_at *= f3;
            this.field_147854_ax *= f3;
            this.field_147852_aq *= f4;
            this.field_147860_au *= f4;
            this.field_147841_ay *= f4;
            this.field_147850_ar *= f5;
            this.field_147858_av *= f5;
            this.field_147839_az *= f5;
            this.field_147848_as *= f6;
            this.field_147856_aw *= f6;
            this.field_147833_aA *= f6;
            iicon = this.func_147793_a(p_147808_1_, this.field_147845_a, p_147808_2_, p_147808_3_, p_147808_4_, 5);
            this.func_147764_f(p_147808_1_, (double)p_147808_2_, (double)p_147808_3_, (double)p_147808_4_, iicon);

            if (field_147843_b && iicon.getIconName().equals("grass_side") && !this.func_147744_b())
            {
                this.field_147872_ap *= p_147808_5_;
                this.field_147852_aq *= p_147808_5_;
                this.field_147850_ar *= p_147808_5_;
                this.field_147848_as *= p_147808_5_;
                this.field_147846_at *= p_147808_6_;
                this.field_147860_au *= p_147808_6_;
                this.field_147858_av *= p_147808_6_;
                this.field_147856_aw *= p_147808_6_;
                this.field_147854_ax *= p_147808_7_;
                this.field_147841_ay *= p_147808_7_;
                this.field_147839_az *= p_147808_7_;
                this.field_147833_aA *= p_147808_7_;
                this.func_147764_f(p_147808_1_, (double)p_147808_2_, (double)p_147808_3_, (double)p_147808_4_, BlockGrass.func_149990_e());
            }

            flag = true;
        }

        this.field_147863_w = false;
        return flag;
    }

    public int func_147778_a(int p_147778_1_, int p_147778_2_, int p_147778_3_, int p_147778_4_)
    {
        if (p_147778_1_ == 0)
        {
            p_147778_1_ = p_147778_4_;
        }

        if (p_147778_2_ == 0)
        {
            p_147778_2_ = p_147778_4_;
        }

        if (p_147778_3_ == 0)
        {
            p_147778_3_ = p_147778_4_;
        }

        return p_147778_1_ + p_147778_2_ + p_147778_3_ + p_147778_4_ >> 2 & 16711935;
    }

    public int func_147727_a(int p_147727_1_, int p_147727_2_, int p_147727_3_, int p_147727_4_, double p_147727_5_, double p_147727_7_, double p_147727_9_, double p_147727_11_)
    {
        int i1 = (int)((double)(p_147727_1_ >> 16 & 255) * p_147727_5_ + (double)(p_147727_2_ >> 16 & 255) * p_147727_7_ + (double)(p_147727_3_ >> 16 & 255) * p_147727_9_ + (double)(p_147727_4_ >> 16 & 255) * p_147727_11_) & 255;
        int j1 = (int)((double)(p_147727_1_ & 255) * p_147727_5_ + (double)(p_147727_2_ & 255) * p_147727_7_ + (double)(p_147727_3_ & 255) * p_147727_9_ + (double)(p_147727_4_ & 255) * p_147727_11_) & 255;
        return i1 << 16 | j1;
    }

    public boolean func_147736_d(Block p_147736_1_, int p_147736_2_, int p_147736_3_, int p_147736_4_, float p_147736_5_, float p_147736_6_, float p_147736_7_)
    {
        this.field_147863_w = false;
        Tessellator tessellator = Tessellator.instance;
        boolean flag = false;
        float f3 = 0.5F;
        float f4 = 1.0F;
        float f5 = 0.8F;
        float f6 = 0.6F;
        float f7 = f4 * p_147736_5_;
        float f8 = f4 * p_147736_6_;
        float f9 = f4 * p_147736_7_;
        float f10 = f3;
        float f11 = f5;
        float f12 = f6;
        float f13 = f3;
        float f14 = f5;
        float f15 = f6;
        float f16 = f3;
        float f17 = f5;
        float f18 = f6;

        if (p_147736_1_ != Blocks.grass)
        {
            f10 = f3 * p_147736_5_;
            f11 = f5 * p_147736_5_;
            f12 = f6 * p_147736_5_;
            f13 = f3 * p_147736_6_;
            f14 = f5 * p_147736_6_;
            f15 = f6 * p_147736_6_;
            f16 = f3 * p_147736_7_;
            f17 = f5 * p_147736_7_;
            f18 = f6 * p_147736_7_;
        }

        int l = p_147736_1_.func_149677_c(this.field_147845_a, p_147736_2_, p_147736_3_, p_147736_4_);

        if (this.field_147837_f || p_147736_1_.func_149646_a(this.field_147845_a, p_147736_2_, p_147736_3_ - 1, p_147736_4_, 0))
        {
            tessellator.setBrightness(this.field_147855_j > 0.0D ? l : p_147736_1_.func_149677_c(this.field_147845_a, p_147736_2_, p_147736_3_ - 1, p_147736_4_));
            tessellator.setColorOpaque_F(f10, f13, f16);
            this.func_147768_a(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, this.func_147793_a(p_147736_1_, this.field_147845_a, p_147736_2_, p_147736_3_, p_147736_4_, 0));
            flag = true;
        }

        if (this.field_147837_f || p_147736_1_.func_149646_a(this.field_147845_a, p_147736_2_, p_147736_3_ + 1, p_147736_4_, 1))
        {
            tessellator.setBrightness(this.field_147857_k < 1.0D ? l : p_147736_1_.func_149677_c(this.field_147845_a, p_147736_2_, p_147736_3_ + 1, p_147736_4_));
            tessellator.setColorOpaque_F(f7, f8, f9);
            this.func_147806_b(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, this.func_147793_a(p_147736_1_, this.field_147845_a, p_147736_2_, p_147736_3_, p_147736_4_, 1));
            flag = true;
        }

        IIcon iicon;

        if (this.field_147837_f || p_147736_1_.func_149646_a(this.field_147845_a, p_147736_2_, p_147736_3_, p_147736_4_ - 1, 2))
        {
            tessellator.setBrightness(this.field_147851_l > 0.0D ? l : p_147736_1_.func_149677_c(this.field_147845_a, p_147736_2_, p_147736_3_, p_147736_4_ - 1));
            tessellator.setColorOpaque_F(f11, f14, f17);
            iicon = this.func_147793_a(p_147736_1_, this.field_147845_a, p_147736_2_, p_147736_3_, p_147736_4_, 2);
            this.func_147761_c(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, iicon);

            if (field_147843_b && iicon.getIconName().equals("grass_side") && !this.func_147744_b())
            {
                tessellator.setColorOpaque_F(f11 * p_147736_5_, f14 * p_147736_6_, f17 * p_147736_7_);
                this.func_147761_c(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, BlockGrass.func_149990_e());
            }

            flag = true;
        }

        if (this.field_147837_f || p_147736_1_.func_149646_a(this.field_147845_a, p_147736_2_, p_147736_3_, p_147736_4_ + 1, 3))
        {
            tessellator.setBrightness(this.field_147853_m < 1.0D ? l : p_147736_1_.func_149677_c(this.field_147845_a, p_147736_2_, p_147736_3_, p_147736_4_ + 1));
            tessellator.setColorOpaque_F(f11, f14, f17);
            iicon = this.func_147793_a(p_147736_1_, this.field_147845_a, p_147736_2_, p_147736_3_, p_147736_4_, 3);
            this.func_147734_d(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, iicon);

            if (field_147843_b && iicon.getIconName().equals("grass_side") && !this.func_147744_b())
            {
                tessellator.setColorOpaque_F(f11 * p_147736_5_, f14 * p_147736_6_, f17 * p_147736_7_);
                this.func_147734_d(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, BlockGrass.func_149990_e());
            }

            flag = true;
        }

        if (this.field_147837_f || p_147736_1_.func_149646_a(this.field_147845_a, p_147736_2_ - 1, p_147736_3_, p_147736_4_, 4))
        {
            tessellator.setBrightness(this.field_147859_h > 0.0D ? l : p_147736_1_.func_149677_c(this.field_147845_a, p_147736_2_ - 1, p_147736_3_, p_147736_4_));
            tessellator.setColorOpaque_F(f12, f15, f18);
            iicon = this.func_147793_a(p_147736_1_, this.field_147845_a, p_147736_2_, p_147736_3_, p_147736_4_, 4);
            this.func_147798_e(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, iicon);

            if (field_147843_b && iicon.getIconName().equals("grass_side") && !this.func_147744_b())
            {
                tessellator.setColorOpaque_F(f12 * p_147736_5_, f15 * p_147736_6_, f18 * p_147736_7_);
                this.func_147798_e(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, BlockGrass.func_149990_e());
            }

            flag = true;
        }

        if (this.field_147837_f || p_147736_1_.func_149646_a(this.field_147845_a, p_147736_2_ + 1, p_147736_3_, p_147736_4_, 5))
        {
            tessellator.setBrightness(this.field_147861_i < 1.0D ? l : p_147736_1_.func_149677_c(this.field_147845_a, p_147736_2_ + 1, p_147736_3_, p_147736_4_));
            tessellator.setColorOpaque_F(f12, f15, f18);
            iicon = this.func_147793_a(p_147736_1_, this.field_147845_a, p_147736_2_, p_147736_3_, p_147736_4_, 5);
            this.func_147764_f(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, iicon);

            if (field_147843_b && iicon.getIconName().equals("grass_side") && !this.func_147744_b())
            {
                tessellator.setColorOpaque_F(f12 * p_147736_5_, f15 * p_147736_6_, f18 * p_147736_7_);
                this.func_147764_f(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, BlockGrass.func_149990_e());
            }

            flag = true;
        }

        return flag;
    }

    public boolean func_147772_a(BlockCocoa p_147772_1_, int p_147772_2_, int p_147772_3_, int p_147772_4_)
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(p_147772_1_.func_149677_c(this.field_147845_a, p_147772_2_, p_147772_3_, p_147772_4_));
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        int l = this.field_147845_a.getBlockMetadata(p_147772_2_, p_147772_3_, p_147772_4_);
        int i1 = BlockDirectional.func_149895_l(l);
        int j1 = BlockCocoa.func_149987_c(l);
        IIcon iicon = p_147772_1_.func_149988_b(j1);
        int k1 = 4 + j1 * 2;
        int l1 = 5 + j1 * 2;
        double d0 = 15.0D - (double)k1;
        double d1 = 15.0D;
        double d2 = 4.0D;
        double d3 = 4.0D + (double)l1;
        double d4 = (double)iicon.getInterpolatedU(d0);
        double d5 = (double)iicon.getInterpolatedU(d1);
        double d6 = (double)iicon.getInterpolatedV(d2);
        double d7 = (double)iicon.getInterpolatedV(d3);
        double d8 = 0.0D;
        double d9 = 0.0D;

        switch (i1)
        {
            case 0:
                d8 = 8.0D - (double)(k1 / 2);
                d9 = 15.0D - (double)k1;
                break;
            case 1:
                d8 = 1.0D;
                d9 = 8.0D - (double)(k1 / 2);
                break;
            case 2:
                d8 = 8.0D - (double)(k1 / 2);
                d9 = 1.0D;
                break;
            case 3:
                d8 = 15.0D - (double)k1;
                d9 = 8.0D - (double)(k1 / 2);
        }

        double d10 = (double)p_147772_2_ + d8 / 16.0D;
        double d11 = (double)p_147772_2_ + (d8 + (double)k1) / 16.0D;
        double d12 = (double)p_147772_3_ + (12.0D - (double)l1) / 16.0D;
        double d13 = (double)p_147772_3_ + 0.75D;
        double d14 = (double)p_147772_4_ + d9 / 16.0D;
        double d15 = (double)p_147772_4_ + (d9 + (double)k1) / 16.0D;
        tessellator.addVertexWithUV(d10, d12, d14, d4, d7);
        tessellator.addVertexWithUV(d10, d12, d15, d5, d7);
        tessellator.addVertexWithUV(d10, d13, d15, d5, d6);
        tessellator.addVertexWithUV(d10, d13, d14, d4, d6);
        tessellator.addVertexWithUV(d11, d12, d15, d4, d7);
        tessellator.addVertexWithUV(d11, d12, d14, d5, d7);
        tessellator.addVertexWithUV(d11, d13, d14, d5, d6);
        tessellator.addVertexWithUV(d11, d13, d15, d4, d6);
        tessellator.addVertexWithUV(d11, d12, d14, d4, d7);
        tessellator.addVertexWithUV(d10, d12, d14, d5, d7);
        tessellator.addVertexWithUV(d10, d13, d14, d5, d6);
        tessellator.addVertexWithUV(d11, d13, d14, d4, d6);
        tessellator.addVertexWithUV(d10, d12, d15, d4, d7);
        tessellator.addVertexWithUV(d11, d12, d15, d5, d7);
        tessellator.addVertexWithUV(d11, d13, d15, d5, d6);
        tessellator.addVertexWithUV(d10, d13, d15, d4, d6);
        int i2 = k1;

        if (j1 >= 2)
        {
            i2 = k1 - 1;
        }

        d4 = (double)iicon.getMinU();
        d5 = (double)iicon.getInterpolatedU((double)i2);
        d6 = (double)iicon.getMinV();
        d7 = (double)iicon.getInterpolatedV((double)i2);
        tessellator.addVertexWithUV(d10, d13, d15, d4, d7);
        tessellator.addVertexWithUV(d11, d13, d15, d5, d7);
        tessellator.addVertexWithUV(d11, d13, d14, d5, d6);
        tessellator.addVertexWithUV(d10, d13, d14, d4, d6);
        tessellator.addVertexWithUV(d10, d12, d14, d4, d6);
        tessellator.addVertexWithUV(d11, d12, d14, d5, d6);
        tessellator.addVertexWithUV(d11, d12, d15, d5, d7);
        tessellator.addVertexWithUV(d10, d12, d15, d4, d7);
        d4 = (double)iicon.getInterpolatedU(12.0D);
        d5 = (double)iicon.getMaxU();
        d6 = (double)iicon.getMinV();
        d7 = (double)iicon.getInterpolatedV(4.0D);
        d8 = 8.0D;
        d9 = 0.0D;
        double d16;

        switch (i1)
        {
            case 0:
                d8 = 8.0D;
                d9 = 12.0D;
                d16 = d4;
                d4 = d5;
                d5 = d16;
                break;
            case 1:
                d8 = 0.0D;
                d9 = 8.0D;
                break;
            case 2:
                d8 = 8.0D;
                d9 = 0.0D;
                break;
            case 3:
                d8 = 12.0D;
                d9 = 8.0D;
                d16 = d4;
                d4 = d5;
                d5 = d16;
        }

        d10 = (double)p_147772_2_ + d8 / 16.0D;
        d11 = (double)p_147772_2_ + (d8 + 4.0D) / 16.0D;
        d12 = (double)p_147772_3_ + 0.75D;
        d13 = (double)p_147772_3_ + 1.0D;
        d14 = (double)p_147772_4_ + d9 / 16.0D;
        d15 = (double)p_147772_4_ + (d9 + 4.0D) / 16.0D;

        if (i1 != 2 && i1 != 0)
        {
            if (i1 == 1 || i1 == 3)
            {
                tessellator.addVertexWithUV(d11, d12, d14, d4, d7);
                tessellator.addVertexWithUV(d10, d12, d14, d5, d7);
                tessellator.addVertexWithUV(d10, d13, d14, d5, d6);
                tessellator.addVertexWithUV(d11, d13, d14, d4, d6);
                tessellator.addVertexWithUV(d10, d12, d14, d5, d7);
                tessellator.addVertexWithUV(d11, d12, d14, d4, d7);
                tessellator.addVertexWithUV(d11, d13, d14, d4, d6);
                tessellator.addVertexWithUV(d10, d13, d14, d5, d6);
            }
        }
        else
        {
            tessellator.addVertexWithUV(d10, d12, d14, d5, d7);
            tessellator.addVertexWithUV(d10, d12, d15, d4, d7);
            tessellator.addVertexWithUV(d10, d13, d15, d4, d6);
            tessellator.addVertexWithUV(d10, d13, d14, d5, d6);
            tessellator.addVertexWithUV(d10, d12, d15, d4, d7);
            tessellator.addVertexWithUV(d10, d12, d14, d5, d7);
            tessellator.addVertexWithUV(d10, d13, d14, d5, d6);
            tessellator.addVertexWithUV(d10, d13, d15, d4, d6);
        }

        return true;
    }

    public boolean func_147797_a(BlockBeacon p_147797_1_, int p_147797_2_, int p_147797_3_, int p_147797_4_)
    {
        float f = 0.1875F;
        this.func_147757_a(this.func_147745_b(Blocks.glass));
        this.func_147782_a(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
        this.func_147784_q(p_147797_1_, p_147797_2_, p_147797_3_, p_147797_4_);
        this.field_147837_f = true;
        this.func_147757_a(this.func_147745_b(Blocks.obsidian));
        this.func_147782_a(0.125D, 0.0062500000931322575D, 0.125D, 0.875D, (double)f, 0.875D);
        this.func_147784_q(p_147797_1_, p_147797_2_, p_147797_3_, p_147797_4_);
        this.func_147757_a(this.func_147745_b(Blocks.beacon));
        this.func_147782_a(0.1875D, (double)f, 0.1875D, 0.8125D, 0.875D, 0.8125D);
        this.func_147784_q(p_147797_1_, p_147797_2_, p_147797_3_, p_147797_4_);
        this.field_147837_f = false;
        this.func_147771_a();
        return true;
    }

    public boolean func_147755_t(Block p_147755_1_, int p_147755_2_, int p_147755_3_, int p_147755_4_)
    {
        int l = p_147755_1_.func_149720_d(this.field_147845_a, p_147755_2_, p_147755_3_, p_147755_4_);
        float f = (float)(l >> 16 & 255) / 255.0F;
        float f1 = (float)(l >> 8 & 255) / 255.0F;
        float f2 = (float)(l & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable)
        {
            float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
            float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            f = f3;
            f1 = f4;
            f2 = f5;
        }

        return this.func_147754_e(p_147755_1_, p_147755_2_, p_147755_3_, p_147755_4_, f, f1, f2);
    }

    public boolean func_147754_e(Block p_147754_1_, int p_147754_2_, int p_147754_3_, int p_147754_4_, float p_147754_5_, float p_147754_6_, float p_147754_7_)
    {
        Tessellator tessellator = Tessellator.instance;
        boolean flag = false;
        float f3 = 0.5F;
        float f4 = 1.0F;
        float f5 = 0.8F;
        float f6 = 0.6F;
        float f7 = f3 * p_147754_5_;
        float f8 = f4 * p_147754_5_;
        float f9 = f5 * p_147754_5_;
        float f10 = f6 * p_147754_5_;
        float f11 = f3 * p_147754_6_;
        float f12 = f4 * p_147754_6_;
        float f13 = f5 * p_147754_6_;
        float f14 = f6 * p_147754_6_;
        float f15 = f3 * p_147754_7_;
        float f16 = f4 * p_147754_7_;
        float f17 = f5 * p_147754_7_;
        float f18 = f6 * p_147754_7_;
        float f19 = 0.0625F;
        int l = p_147754_1_.func_149677_c(this.field_147845_a, p_147754_2_, p_147754_3_, p_147754_4_);

        if (this.field_147837_f || p_147754_1_.func_149646_a(this.field_147845_a, p_147754_2_, p_147754_3_ - 1, p_147754_4_, 0))
        {
            tessellator.setBrightness(this.field_147855_j > 0.0D ? l : p_147754_1_.func_149677_c(this.field_147845_a, p_147754_2_, p_147754_3_ - 1, p_147754_4_));
            tessellator.setColorOpaque_F(f7, f11, f15);
            this.func_147768_a(p_147754_1_, (double)p_147754_2_, (double)p_147754_3_, (double)p_147754_4_, this.func_147793_a(p_147754_1_, this.field_147845_a, p_147754_2_, p_147754_3_, p_147754_4_, 0));
        }

        if (this.field_147837_f || p_147754_1_.func_149646_a(this.field_147845_a, p_147754_2_, p_147754_3_ + 1, p_147754_4_, 1))
        {
            tessellator.setBrightness(this.field_147857_k < 1.0D ? l : p_147754_1_.func_149677_c(this.field_147845_a, p_147754_2_, p_147754_3_ + 1, p_147754_4_));
            tessellator.setColorOpaque_F(f8, f12, f16);
            this.func_147806_b(p_147754_1_, (double)p_147754_2_, (double)p_147754_3_, (double)p_147754_4_, this.func_147793_a(p_147754_1_, this.field_147845_a, p_147754_2_, p_147754_3_, p_147754_4_, 1));
        }

        tessellator.setBrightness(l);
        tessellator.setColorOpaque_F(f9, f13, f17);
        tessellator.addTranslation(0.0F, 0.0F, f19);
        this.func_147761_c(p_147754_1_, (double)p_147754_2_, (double)p_147754_3_, (double)p_147754_4_, this.func_147793_a(p_147754_1_, this.field_147845_a, p_147754_2_, p_147754_3_, p_147754_4_, 2));
        tessellator.addTranslation(0.0F, 0.0F, -f19);
        tessellator.addTranslation(0.0F, 0.0F, -f19);
        this.func_147734_d(p_147754_1_, (double)p_147754_2_, (double)p_147754_3_, (double)p_147754_4_, this.func_147793_a(p_147754_1_, this.field_147845_a, p_147754_2_, p_147754_3_, p_147754_4_, 3));
        tessellator.addTranslation(0.0F, 0.0F, f19);
        tessellator.setColorOpaque_F(f10, f14, f18);
        tessellator.addTranslation(f19, 0.0F, 0.0F);
        this.func_147798_e(p_147754_1_, (double)p_147754_2_, (double)p_147754_3_, (double)p_147754_4_, this.func_147793_a(p_147754_1_, this.field_147845_a, p_147754_2_, p_147754_3_, p_147754_4_, 4));
        tessellator.addTranslation(-f19, 0.0F, 0.0F);
        tessellator.addTranslation(-f19, 0.0F, 0.0F);
        this.func_147764_f(p_147754_1_, (double)p_147754_2_, (double)p_147754_3_, (double)p_147754_4_, this.func_147793_a(p_147754_1_, this.field_147845_a, p_147754_2_, p_147754_3_, p_147754_4_, 5));
        tessellator.addTranslation(f19, 0.0F, 0.0F);
        return true;
    }

    public boolean func_147735_a(BlockFence p_147735_1_, int p_147735_2_, int p_147735_3_, int p_147735_4_)
    {
        boolean flag = false;
        float f = 0.375F;
        float f1 = 0.625F;
        this.func_147782_a((double)f, 0.0D, (double)f, (double)f1, 1.0D, (double)f1);
        this.func_147784_q(p_147735_1_, p_147735_2_, p_147735_3_, p_147735_4_);
        flag = true;
        boolean flag1 = false;
        boolean flag2 = false;

        if (p_147735_1_.func_149826_e(this.field_147845_a, p_147735_2_ - 1, p_147735_3_, p_147735_4_) || p_147735_1_.func_149826_e(this.field_147845_a, p_147735_2_ + 1, p_147735_3_, p_147735_4_))
        {
            flag1 = true;
        }

        if (p_147735_1_.func_149826_e(this.field_147845_a, p_147735_2_, p_147735_3_, p_147735_4_ - 1) || p_147735_1_.func_149826_e(this.field_147845_a, p_147735_2_, p_147735_3_, p_147735_4_ + 1))
        {
            flag2 = true;
        }

        boolean flag3 = p_147735_1_.func_149826_e(this.field_147845_a, p_147735_2_ - 1, p_147735_3_, p_147735_4_);
        boolean flag4 = p_147735_1_.func_149826_e(this.field_147845_a, p_147735_2_ + 1, p_147735_3_, p_147735_4_);
        boolean flag5 = p_147735_1_.func_149826_e(this.field_147845_a, p_147735_2_, p_147735_3_, p_147735_4_ - 1);
        boolean flag6 = p_147735_1_.func_149826_e(this.field_147845_a, p_147735_2_, p_147735_3_, p_147735_4_ + 1);

        if (!flag1 && !flag2)
        {
            flag1 = true;
        }

        f = 0.4375F;
        f1 = 0.5625F;
        float f2 = 0.75F;
        float f3 = 0.9375F;
        float f4 = flag3 ? 0.0F : f;
        float f5 = flag4 ? 1.0F : f1;
        float f6 = flag5 ? 0.0F : f;
        float f7 = flag6 ? 1.0F : f1;

        if (flag1)
        {
            this.func_147782_a((double)f4, (double)f2, (double)f, (double)f5, (double)f3, (double)f1);
            this.func_147784_q(p_147735_1_, p_147735_2_, p_147735_3_, p_147735_4_);
            flag = true;
        }

        if (flag2)
        {
            this.func_147782_a((double)f, (double)f2, (double)f6, (double)f1, (double)f3, (double)f7);
            this.func_147784_q(p_147735_1_, p_147735_2_, p_147735_3_, p_147735_4_);
            flag = true;
        }

        f2 = 0.375F;
        f3 = 0.5625F;

        if (flag1)
        {
            this.func_147782_a((double)f4, (double)f2, (double)f, (double)f5, (double)f3, (double)f1);
            this.func_147784_q(p_147735_1_, p_147735_2_, p_147735_3_, p_147735_4_);
            flag = true;
        }

        if (flag2)
        {
            this.func_147782_a((double)f, (double)f2, (double)f6, (double)f1, (double)f3, (double)f7);
            this.func_147784_q(p_147735_1_, p_147735_2_, p_147735_3_, p_147735_4_);
            flag = true;
        }

        p_147735_1_.func_149719_a(this.field_147845_a, p_147735_2_, p_147735_3_, p_147735_4_);
        return flag;
    }

    public boolean func_147807_a(BlockWall p_147807_1_, int p_147807_2_, int p_147807_3_, int p_147807_4_)
    {
        boolean flag = p_147807_1_.func_150091_e(this.field_147845_a, p_147807_2_ - 1, p_147807_3_, p_147807_4_);
        boolean flag1 = p_147807_1_.func_150091_e(this.field_147845_a, p_147807_2_ + 1, p_147807_3_, p_147807_4_);
        boolean flag2 = p_147807_1_.func_150091_e(this.field_147845_a, p_147807_2_, p_147807_3_, p_147807_4_ - 1);
        boolean flag3 = p_147807_1_.func_150091_e(this.field_147845_a, p_147807_2_, p_147807_3_, p_147807_4_ + 1);
        boolean flag4 = flag2 && flag3 && !flag && !flag1;
        boolean flag5 = !flag2 && !flag3 && flag && flag1;
        boolean flag6 = this.field_147845_a.func_147437_c(p_147807_2_, p_147807_3_ + 1, p_147807_4_);

        if ((flag4 || flag5) && flag6)
        {
            if (flag4)
            {
                this.func_147782_a(0.3125D, 0.0D, 0.0D, 0.6875D, 0.8125D, 1.0D);
                this.func_147784_q(p_147807_1_, p_147807_2_, p_147807_3_, p_147807_4_);
            }
            else
            {
                this.func_147782_a(0.0D, 0.0D, 0.3125D, 1.0D, 0.8125D, 0.6875D);
                this.func_147784_q(p_147807_1_, p_147807_2_, p_147807_3_, p_147807_4_);
            }
        }
        else
        {
            this.func_147782_a(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);
            this.func_147784_q(p_147807_1_, p_147807_2_, p_147807_3_, p_147807_4_);

            if (flag)
            {
                this.func_147782_a(0.0D, 0.0D, 0.3125D, 0.25D, 0.8125D, 0.6875D);
                this.func_147784_q(p_147807_1_, p_147807_2_, p_147807_3_, p_147807_4_);
            }

            if (flag1)
            {
                this.func_147782_a(0.75D, 0.0D, 0.3125D, 1.0D, 0.8125D, 0.6875D);
                this.func_147784_q(p_147807_1_, p_147807_2_, p_147807_3_, p_147807_4_);
            }

            if (flag2)
            {
                this.func_147782_a(0.3125D, 0.0D, 0.0D, 0.6875D, 0.8125D, 0.25D);
                this.func_147784_q(p_147807_1_, p_147807_2_, p_147807_3_, p_147807_4_);
            }

            if (flag3)
            {
                this.func_147782_a(0.3125D, 0.0D, 0.75D, 0.6875D, 0.8125D, 1.0D);
                this.func_147784_q(p_147807_1_, p_147807_2_, p_147807_3_, p_147807_4_);
            }
        }

        p_147807_1_.func_149719_a(this.field_147845_a, p_147807_2_, p_147807_3_, p_147807_4_);
        return true;
    }

    public boolean func_147802_a(BlockDragonEgg p_147802_1_, int p_147802_2_, int p_147802_3_, int p_147802_4_)
    {
        boolean flag = false;
        int l = 0;

        for (int i1 = 0; i1 < 8; ++i1)
        {
            byte b0 = 0;
            byte b1 = 1;

            if (i1 == 0)
            {
                b0 = 2;
            }

            if (i1 == 1)
            {
                b0 = 3;
            }

            if (i1 == 2)
            {
                b0 = 4;
            }

            if (i1 == 3)
            {
                b0 = 5;
                b1 = 2;
            }

            if (i1 == 4)
            {
                b0 = 6;
                b1 = 3;
            }

            if (i1 == 5)
            {
                b0 = 7;
                b1 = 5;
            }

            if (i1 == 6)
            {
                b0 = 6;
                b1 = 2;
            }

            if (i1 == 7)
            {
                b0 = 3;
            }

            float f = (float)b0 / 16.0F;
            float f1 = 1.0F - (float)l / 16.0F;
            float f2 = 1.0F - (float)(l + b1) / 16.0F;
            l += b1;
            this.func_147782_a((double)(0.5F - f), (double)f2, (double)(0.5F - f), (double)(0.5F + f), (double)f1, (double)(0.5F + f));
            this.func_147784_q(p_147802_1_, p_147802_2_, p_147802_3_, p_147802_4_);
        }

        flag = true;
        this.func_147782_a(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
        return flag;
    }

    public boolean func_147776_a(BlockFenceGate p_147776_1_, int p_147776_2_, int p_147776_3_, int p_147776_4_)
    {
        boolean flag = true;
        int l = this.field_147845_a.getBlockMetadata(p_147776_2_, p_147776_3_, p_147776_4_);
        boolean flag1 = BlockFenceGate.func_149896_b(l);
        int i1 = BlockDirectional.func_149895_l(l);
        float f = 0.375F;
        float f1 = 0.5625F;
        float f2 = 0.75F;
        float f3 = 0.9375F;
        float f4 = 0.3125F;
        float f5 = 1.0F;

        if ((i1 == 2 || i1 == 0) && this.field_147845_a.func_147439_a(p_147776_2_ - 1, p_147776_3_, p_147776_4_) == Blocks.cobblestone_wall && this.field_147845_a.func_147439_a(p_147776_2_ + 1, p_147776_3_, p_147776_4_) == Blocks.cobblestone_wall || (i1 == 3 || i1 == 1) && this.field_147845_a.func_147439_a(p_147776_2_, p_147776_3_, p_147776_4_ - 1) == Blocks.cobblestone_wall && this.field_147845_a.func_147439_a(p_147776_2_, p_147776_3_, p_147776_4_ + 1) == Blocks.cobblestone_wall)
        {
            f -= 0.1875F;
            f1 -= 0.1875F;
            f2 -= 0.1875F;
            f3 -= 0.1875F;
            f4 -= 0.1875F;
            f5 -= 0.1875F;
        }

        this.field_147837_f = true;
        float f6;
        float f8;
        float f7;
        float f9;

        if (i1 != 3 && i1 != 1)
        {
            f6 = 0.0F;
            f7 = 0.125F;
            f8 = 0.4375F;
            f9 = 0.5625F;
            this.func_147782_a((double)f6, (double)f4, (double)f8, (double)f7, (double)f5, (double)f9);
            this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
            f6 = 0.875F;
            f7 = 1.0F;
            this.func_147782_a((double)f6, (double)f4, (double)f8, (double)f7, (double)f5, (double)f9);
            this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
        }
        else
        {
            this.field_147867_u = 1;
            f6 = 0.4375F;
            f7 = 0.5625F;
            f8 = 0.0F;
            f9 = 0.125F;
            this.func_147782_a((double)f6, (double)f4, (double)f8, (double)f7, (double)f5, (double)f9);
            this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
            f8 = 0.875F;
            f9 = 1.0F;
            this.func_147782_a((double)f6, (double)f4, (double)f8, (double)f7, (double)f5, (double)f9);
            this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
            this.field_147867_u = 0;
        }

        if (flag1)
        {
            if (i1 == 2 || i1 == 0)
            {
                this.field_147867_u = 1;
            }

            float f10;
            float f12;
            float f11;

            if (i1 == 3)
            {
                f6 = 0.0F;
                f7 = 0.125F;
                f8 = 0.875F;
                f9 = 1.0F;
                f10 = 0.5625F;
                f11 = 0.8125F;
                f12 = 0.9375F;
                this.func_147782_a(0.8125D, (double)f, 0.0D, 0.9375D, (double)f3, 0.125D);
                this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
                this.func_147782_a(0.8125D, (double)f, 0.875D, 0.9375D, (double)f3, 1.0D);
                this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
                this.func_147782_a(0.5625D, (double)f, 0.0D, 0.8125D, (double)f1, 0.125D);
                this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
                this.func_147782_a(0.5625D, (double)f, 0.875D, 0.8125D, (double)f1, 1.0D);
                this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
                this.func_147782_a(0.5625D, (double)f2, 0.0D, 0.8125D, (double)f3, 0.125D);
                this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
                this.func_147782_a(0.5625D, (double)f2, 0.875D, 0.8125D, (double)f3, 1.0D);
                this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
            }
            else if (i1 == 1)
            {
                f6 = 0.0F;
                f7 = 0.125F;
                f8 = 0.875F;
                f9 = 1.0F;
                f10 = 0.0625F;
                f11 = 0.1875F;
                f12 = 0.4375F;
                this.func_147782_a(0.0625D, (double)f, 0.0D, 0.1875D, (double)f3, 0.125D);
                this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
                this.func_147782_a(0.0625D, (double)f, 0.875D, 0.1875D, (double)f3, 1.0D);
                this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
                this.func_147782_a(0.1875D, (double)f, 0.0D, 0.4375D, (double)f1, 0.125D);
                this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
                this.func_147782_a(0.1875D, (double)f, 0.875D, 0.4375D, (double)f1, 1.0D);
                this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
                this.func_147782_a(0.1875D, (double)f2, 0.0D, 0.4375D, (double)f3, 0.125D);
                this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
                this.func_147782_a(0.1875D, (double)f2, 0.875D, 0.4375D, (double)f3, 1.0D);
                this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
            }
            else if (i1 == 0)
            {
                f6 = 0.0F;
                f7 = 0.125F;
                f8 = 0.875F;
                f9 = 1.0F;
                f10 = 0.5625F;
                f11 = 0.8125F;
                f12 = 0.9375F;
                this.func_147782_a(0.0D, (double)f, 0.8125D, 0.125D, (double)f3, 0.9375D);
                this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
                this.func_147782_a(0.875D, (double)f, 0.8125D, 1.0D, (double)f3, 0.9375D);
                this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
                this.func_147782_a(0.0D, (double)f, 0.5625D, 0.125D, (double)f1, 0.8125D);
                this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
                this.func_147782_a(0.875D, (double)f, 0.5625D, 1.0D, (double)f1, 0.8125D);
                this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
                this.func_147782_a(0.0D, (double)f2, 0.5625D, 0.125D, (double)f3, 0.8125D);
                this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
                this.func_147782_a(0.875D, (double)f2, 0.5625D, 1.0D, (double)f3, 0.8125D);
                this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
            }
            else if (i1 == 2)
            {
                f6 = 0.0F;
                f7 = 0.125F;
                f8 = 0.875F;
                f9 = 1.0F;
                f10 = 0.0625F;
                f11 = 0.1875F;
                f12 = 0.4375F;
                this.func_147782_a(0.0D, (double)f, 0.0625D, 0.125D, (double)f3, 0.1875D);
                this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
                this.func_147782_a(0.875D, (double)f, 0.0625D, 1.0D, (double)f3, 0.1875D);
                this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
                this.func_147782_a(0.0D, (double)f, 0.1875D, 0.125D, (double)f1, 0.4375D);
                this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
                this.func_147782_a(0.875D, (double)f, 0.1875D, 1.0D, (double)f1, 0.4375D);
                this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
                this.func_147782_a(0.0D, (double)f2, 0.1875D, 0.125D, (double)f3, 0.4375D);
                this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
                this.func_147782_a(0.875D, (double)f2, 0.1875D, 1.0D, (double)f3, 0.4375D);
                this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
            }
        }
        else if (i1 != 3 && i1 != 1)
        {
            f6 = 0.375F;
            f7 = 0.5F;
            f8 = 0.4375F;
            f9 = 0.5625F;
            this.func_147782_a((double)f6, (double)f, (double)f8, (double)f7, (double)f3, (double)f9);
            this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
            f6 = 0.5F;
            f7 = 0.625F;
            this.func_147782_a((double)f6, (double)f, (double)f8, (double)f7, (double)f3, (double)f9);
            this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
            f6 = 0.625F;
            f7 = 0.875F;
            this.func_147782_a((double)f6, (double)f, (double)f8, (double)f7, (double)f1, (double)f9);
            this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
            this.func_147782_a((double)f6, (double)f2, (double)f8, (double)f7, (double)f3, (double)f9);
            this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
            f6 = 0.125F;
            f7 = 0.375F;
            this.func_147782_a((double)f6, (double)f, (double)f8, (double)f7, (double)f1, (double)f9);
            this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
            this.func_147782_a((double)f6, (double)f2, (double)f8, (double)f7, (double)f3, (double)f9);
            this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
        }
        else
        {
            this.field_147867_u = 1;
            f6 = 0.4375F;
            f7 = 0.5625F;
            f8 = 0.375F;
            f9 = 0.5F;
            this.func_147782_a((double)f6, (double)f, (double)f8, (double)f7, (double)f3, (double)f9);
            this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
            f8 = 0.5F;
            f9 = 0.625F;
            this.func_147782_a((double)f6, (double)f, (double)f8, (double)f7, (double)f3, (double)f9);
            this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
            f8 = 0.625F;
            f9 = 0.875F;
            this.func_147782_a((double)f6, (double)f, (double)f8, (double)f7, (double)f1, (double)f9);
            this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
            this.func_147782_a((double)f6, (double)f2, (double)f8, (double)f7, (double)f3, (double)f9);
            this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
            f8 = 0.125F;
            f9 = 0.375F;
            this.func_147782_a((double)f6, (double)f, (double)f8, (double)f7, (double)f1, (double)f9);
            this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
            this.func_147782_a((double)f6, (double)f2, (double)f8, (double)f7, (double)f3, (double)f9);
            this.func_147784_q(p_147776_1_, p_147776_2_, p_147776_3_, p_147776_4_);
        }

        this.field_147837_f = false;
        this.field_147867_u = 0;
        this.func_147782_a(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
        return flag;
    }

    public boolean func_147803_a(BlockHopper p_147803_1_, int p_147803_2_, int p_147803_3_, int p_147803_4_)
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(p_147803_1_.func_149677_c(this.field_147845_a, p_147803_2_, p_147803_3_, p_147803_4_));
        int l = p_147803_1_.func_149720_d(this.field_147845_a, p_147803_2_, p_147803_3_, p_147803_4_);
        float f = (float)(l >> 16 & 255) / 255.0F;
        float f1 = (float)(l >> 8 & 255) / 255.0F;
        float f2 = (float)(l & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable)
        {
            float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
            float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            f = f3;
            f1 = f4;
            f2 = f5;
        }

        tessellator.setColorOpaque_F(f, f1, f2);
        return this.func_147799_a(p_147803_1_, p_147803_2_, p_147803_3_, p_147803_4_, this.field_147845_a.getBlockMetadata(p_147803_2_, p_147803_3_, p_147803_4_), false);
    }

    public boolean func_147799_a(BlockHopper p_147799_1_, int p_147799_2_, int p_147799_3_, int p_147799_4_, int p_147799_5_, boolean p_147799_6_)
    {
        Tessellator tessellator = Tessellator.instance;
        int i1 = BlockHopper.func_149918_b(p_147799_5_);
        double d0 = 0.625D;
        this.func_147782_a(0.0D, d0, 0.0D, 1.0D, 1.0D, 1.0D);

        if (p_147799_6_)
        {
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, -1.0F, 0.0F);
            this.func_147768_a(p_147799_1_, 0.0D, 0.0D, 0.0D, this.func_147787_a(p_147799_1_, 0, p_147799_5_));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 1.0F, 0.0F);
            this.func_147806_b(p_147799_1_, 0.0D, 0.0D, 0.0D, this.func_147787_a(p_147799_1_, 1, p_147799_5_));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, -1.0F);
            this.func_147761_c(p_147799_1_, 0.0D, 0.0D, 0.0D, this.func_147787_a(p_147799_1_, 2, p_147799_5_));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, 1.0F);
            this.func_147734_d(p_147799_1_, 0.0D, 0.0D, 0.0D, this.func_147787_a(p_147799_1_, 3, p_147799_5_));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(-1.0F, 0.0F, 0.0F);
            this.func_147798_e(p_147799_1_, 0.0D, 0.0D, 0.0D, this.func_147787_a(p_147799_1_, 4, p_147799_5_));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(1.0F, 0.0F, 0.0F);
            this.func_147764_f(p_147799_1_, 0.0D, 0.0D, 0.0D, this.func_147787_a(p_147799_1_, 5, p_147799_5_));
            tessellator.draw();
        }
        else
        {
            this.func_147784_q(p_147799_1_, p_147799_2_, p_147799_3_, p_147799_4_);
        }

        float f1;

        if (!p_147799_6_)
        {
            tessellator.setBrightness(p_147799_1_.func_149677_c(this.field_147845_a, p_147799_2_, p_147799_3_, p_147799_4_));
            int j1 = p_147799_1_.func_149720_d(this.field_147845_a, p_147799_2_, p_147799_3_, p_147799_4_);
            float f = (float)(j1 >> 16 & 255) / 255.0F;
            f1 = (float)(j1 >> 8 & 255) / 255.0F;
            float f2 = (float)(j1 & 255) / 255.0F;

            if (EntityRenderer.anaglyphEnable)
            {
                float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
                float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
                float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
                f = f3;
                f1 = f4;
                f2 = f5;
            }

            tessellator.setColorOpaque_F(f, f1, f2);
        }

        IIcon iicon = BlockHopper.func_149916_e("hopper_outside");
        IIcon iicon1 = BlockHopper.func_149916_e("hopper_inside");
        f1 = 0.125F;

        if (p_147799_6_)
        {
            tessellator.startDrawingQuads();
            tessellator.setNormal(1.0F, 0.0F, 0.0F);
            this.func_147764_f(p_147799_1_, (double)(-1.0F + f1), 0.0D, 0.0D, iicon);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(-1.0F, 0.0F, 0.0F);
            this.func_147798_e(p_147799_1_, (double)(1.0F - f1), 0.0D, 0.0D, iicon);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, 1.0F);
            this.func_147734_d(p_147799_1_, 0.0D, 0.0D, (double)(-1.0F + f1), iicon);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, -1.0F);
            this.func_147761_c(p_147799_1_, 0.0D, 0.0D, (double)(1.0F - f1), iicon);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 1.0F, 0.0F);
            this.func_147806_b(p_147799_1_, 0.0D, -1.0D + d0, 0.0D, iicon1);
            tessellator.draw();
        }
        else
        {
            this.func_147764_f(p_147799_1_, (double)((float)p_147799_2_ - 1.0F + f1), (double)p_147799_3_, (double)p_147799_4_, iicon);
            this.func_147798_e(p_147799_1_, (double)((float)p_147799_2_ + 1.0F - f1), (double)p_147799_3_, (double)p_147799_4_, iicon);
            this.func_147734_d(p_147799_1_, (double)p_147799_2_, (double)p_147799_3_, (double)((float)p_147799_4_ - 1.0F + f1), iicon);
            this.func_147761_c(p_147799_1_, (double)p_147799_2_, (double)p_147799_3_, (double)((float)p_147799_4_ + 1.0F - f1), iicon);
            this.func_147806_b(p_147799_1_, (double)p_147799_2_, (double)((float)p_147799_3_ - 1.0F) + d0, (double)p_147799_4_, iicon1);
        }

        this.func_147757_a(iicon);
        double d3 = 0.25D;
        double d4 = 0.25D;
        this.func_147782_a(d3, d4, d3, 1.0D - d3, d0 - 0.002D, 1.0D - d3);

        if (p_147799_6_)
        {
            tessellator.startDrawingQuads();
            tessellator.setNormal(1.0F, 0.0F, 0.0F);
            this.func_147764_f(p_147799_1_, 0.0D, 0.0D, 0.0D, iicon);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(-1.0F, 0.0F, 0.0F);
            this.func_147798_e(p_147799_1_, 0.0D, 0.0D, 0.0D, iicon);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, 1.0F);
            this.func_147734_d(p_147799_1_, 0.0D, 0.0D, 0.0D, iicon);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, -1.0F);
            this.func_147761_c(p_147799_1_, 0.0D, 0.0D, 0.0D, iicon);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 1.0F, 0.0F);
            this.func_147806_b(p_147799_1_, 0.0D, 0.0D, 0.0D, iicon);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, -1.0F, 0.0F);
            this.func_147768_a(p_147799_1_, 0.0D, 0.0D, 0.0D, iicon);
            tessellator.draw();
        }
        else
        {
            this.func_147784_q(p_147799_1_, p_147799_2_, p_147799_3_, p_147799_4_);
        }

        if (!p_147799_6_)
        {
            double d1 = 0.375D;
            double d2 = 0.25D;
            this.func_147757_a(iicon);

            if (i1 == 0)
            {
                this.func_147782_a(d1, 0.0D, d1, 1.0D - d1, 0.25D, 1.0D - d1);
                this.func_147784_q(p_147799_1_, p_147799_2_, p_147799_3_, p_147799_4_);
            }

            if (i1 == 2)
            {
                this.func_147782_a(d1, d4, 0.0D, 1.0D - d1, d4 + d2, d3);
                this.func_147784_q(p_147799_1_, p_147799_2_, p_147799_3_, p_147799_4_);
            }

            if (i1 == 3)
            {
                this.func_147782_a(d1, d4, 1.0D - d3, 1.0D - d1, d4 + d2, 1.0D);
                this.func_147784_q(p_147799_1_, p_147799_2_, p_147799_3_, p_147799_4_);
            }

            if (i1 == 4)
            {
                this.func_147782_a(0.0D, d4, d1, d3, d4 + d2, 1.0D - d1);
                this.func_147784_q(p_147799_1_, p_147799_2_, p_147799_3_, p_147799_4_);
            }

            if (i1 == 5)
            {
                this.func_147782_a(1.0D - d3, d4, d1, 1.0D, d4 + d2, 1.0D - d1);
                this.func_147784_q(p_147799_1_, p_147799_2_, p_147799_3_, p_147799_4_);
            }
        }

        this.func_147771_a();
        return true;
    }

    public boolean func_147722_a(BlockStairs p_147722_1_, int p_147722_2_, int p_147722_3_, int p_147722_4_)
    {
        p_147722_1_.func_150147_e(this.field_147845_a, p_147722_2_, p_147722_3_, p_147722_4_);
        this.func_147775_a(p_147722_1_);
        this.func_147784_q(p_147722_1_, p_147722_2_, p_147722_3_, p_147722_4_);
        boolean flag = p_147722_1_.func_150145_f(this.field_147845_a, p_147722_2_, p_147722_3_, p_147722_4_);
        this.func_147775_a(p_147722_1_);
        this.func_147784_q(p_147722_1_, p_147722_2_, p_147722_3_, p_147722_4_);

        if (flag && p_147722_1_.func_150144_g(this.field_147845_a, p_147722_2_, p_147722_3_, p_147722_4_))
        {
            this.func_147775_a(p_147722_1_);
            this.func_147784_q(p_147722_1_, p_147722_2_, p_147722_3_, p_147722_4_);
        }

        return true;
    }

    public boolean func_147760_u(Block p_147760_1_, int p_147760_2_, int p_147760_3_, int p_147760_4_)
    {
        Tessellator tessellator = Tessellator.instance;
        int l = this.field_147845_a.getBlockMetadata(p_147760_2_, p_147760_3_, p_147760_4_);

        if ((l & 8) != 0)
        {
            if (this.field_147845_a.func_147439_a(p_147760_2_, p_147760_3_ - 1, p_147760_4_) != p_147760_1_)
            {
                return false;
            }
        }
        else if (this.field_147845_a.func_147439_a(p_147760_2_, p_147760_3_ + 1, p_147760_4_) != p_147760_1_)
        {
            return false;
        }

        boolean flag = false;
        float f = 0.5F;
        float f1 = 1.0F;
        float f2 = 0.8F;
        float f3 = 0.6F;
        int i1 = p_147760_1_.func_149677_c(this.field_147845_a, p_147760_2_, p_147760_3_, p_147760_4_);
        tessellator.setBrightness(this.field_147855_j > 0.0D ? i1 : p_147760_1_.func_149677_c(this.field_147845_a, p_147760_2_, p_147760_3_ - 1, p_147760_4_));
        tessellator.setColorOpaque_F(f, f, f);
        this.func_147768_a(p_147760_1_, (double)p_147760_2_, (double)p_147760_3_, (double)p_147760_4_, this.func_147793_a(p_147760_1_, this.field_147845_a, p_147760_2_, p_147760_3_, p_147760_4_, 0));
        flag = true;
        tessellator.setBrightness(this.field_147857_k < 1.0D ? i1 : p_147760_1_.func_149677_c(this.field_147845_a, p_147760_2_, p_147760_3_ + 1, p_147760_4_));
        tessellator.setColorOpaque_F(f1, f1, f1);
        this.func_147806_b(p_147760_1_, (double)p_147760_2_, (double)p_147760_3_, (double)p_147760_4_, this.func_147793_a(p_147760_1_, this.field_147845_a, p_147760_2_, p_147760_3_, p_147760_4_, 1));
        flag = true;
        tessellator.setBrightness(this.field_147851_l > 0.0D ? i1 : p_147760_1_.func_149677_c(this.field_147845_a, p_147760_2_, p_147760_3_, p_147760_4_ - 1));
        tessellator.setColorOpaque_F(f2, f2, f2);
        IIcon iicon = this.func_147793_a(p_147760_1_, this.field_147845_a, p_147760_2_, p_147760_3_, p_147760_4_, 2);
        this.func_147761_c(p_147760_1_, (double)p_147760_2_, (double)p_147760_3_, (double)p_147760_4_, iicon);
        flag = true;
        this.field_147842_e = false;
        tessellator.setBrightness(this.field_147853_m < 1.0D ? i1 : p_147760_1_.func_149677_c(this.field_147845_a, p_147760_2_, p_147760_3_, p_147760_4_ + 1));
        tessellator.setColorOpaque_F(f2, f2, f2);
        iicon = this.func_147793_a(p_147760_1_, this.field_147845_a, p_147760_2_, p_147760_3_, p_147760_4_, 3);
        this.func_147734_d(p_147760_1_, (double)p_147760_2_, (double)p_147760_3_, (double)p_147760_4_, iicon);
        flag = true;
        this.field_147842_e = false;
        tessellator.setBrightness(this.field_147859_h > 0.0D ? i1 : p_147760_1_.func_149677_c(this.field_147845_a, p_147760_2_ - 1, p_147760_3_, p_147760_4_));
        tessellator.setColorOpaque_F(f3, f3, f3);
        iicon = this.func_147793_a(p_147760_1_, this.field_147845_a, p_147760_2_, p_147760_3_, p_147760_4_, 4);
        this.func_147798_e(p_147760_1_, (double)p_147760_2_, (double)p_147760_3_, (double)p_147760_4_, iicon);
        flag = true;
        this.field_147842_e = false;
        tessellator.setBrightness(this.field_147861_i < 1.0D ? i1 : p_147760_1_.func_149677_c(this.field_147845_a, p_147760_2_ + 1, p_147760_3_, p_147760_4_));
        tessellator.setColorOpaque_F(f3, f3, f3);
        iicon = this.func_147793_a(p_147760_1_, this.field_147845_a, p_147760_2_, p_147760_3_, p_147760_4_, 5);
        this.func_147764_f(p_147760_1_, (double)p_147760_2_, (double)p_147760_3_, (double)p_147760_4_, iicon);
        flag = true;
        this.field_147842_e = false;
        return flag;
    }

    public void func_147768_a(Block p_147768_1_, double p_147768_2_, double p_147768_4_, double p_147768_6_, IIcon p_147768_8_)
    {
        Tessellator tessellator = Tessellator.instance;

        if (this.func_147744_b())
        {
            p_147768_8_ = this.field_147840_d;
        }

        double d3 = (double)p_147768_8_.getInterpolatedU(this.field_147859_h * 16.0D);
        double d4 = (double)p_147768_8_.getInterpolatedU(this.field_147861_i * 16.0D);
        double d5 = (double)p_147768_8_.getInterpolatedV(this.field_147851_l * 16.0D);
        double d6 = (double)p_147768_8_.getInterpolatedV(this.field_147853_m * 16.0D);

        if (this.field_147859_h < 0.0D || this.field_147861_i > 1.0D)
        {
            d3 = (double)p_147768_8_.getMinU();
            d4 = (double)p_147768_8_.getMaxU();
        }

        if (this.field_147851_l < 0.0D || this.field_147853_m > 1.0D)
        {
            d5 = (double)p_147768_8_.getMinV();
            d6 = (double)p_147768_8_.getMaxV();
        }

        double d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;

        if (this.field_147865_v == 2)
        {
            d3 = (double)p_147768_8_.getInterpolatedU(this.field_147851_l * 16.0D);
            d5 = (double)p_147768_8_.getInterpolatedV(16.0D - this.field_147861_i * 16.0D);
            d4 = (double)p_147768_8_.getInterpolatedU(this.field_147853_m * 16.0D);
            d6 = (double)p_147768_8_.getInterpolatedV(16.0D - this.field_147859_h * 16.0D);
            d9 = d5;
            d10 = d6;
            d7 = d3;
            d8 = d4;
            d5 = d6;
            d6 = d9;
        }
        else if (this.field_147865_v == 1)
        {
            d3 = (double)p_147768_8_.getInterpolatedU(16.0D - this.field_147853_m * 16.0D);
            d5 = (double)p_147768_8_.getInterpolatedV(this.field_147859_h * 16.0D);
            d4 = (double)p_147768_8_.getInterpolatedU(16.0D - this.field_147851_l * 16.0D);
            d6 = (double)p_147768_8_.getInterpolatedV(this.field_147861_i * 16.0D);
            d7 = d4;
            d8 = d3;
            d3 = d4;
            d4 = d8;
            d9 = d6;
            d10 = d5;
        }
        else if (this.field_147865_v == 3)
        {
            d3 = (double)p_147768_8_.getInterpolatedU(16.0D - this.field_147859_h * 16.0D);
            d4 = (double)p_147768_8_.getInterpolatedU(16.0D - this.field_147861_i * 16.0D);
            d5 = (double)p_147768_8_.getInterpolatedV(16.0D - this.field_147851_l * 16.0D);
            d6 = (double)p_147768_8_.getInterpolatedV(16.0D - this.field_147853_m * 16.0D);
            d7 = d4;
            d8 = d3;
            d9 = d5;
            d10 = d6;
        }

        double d11 = p_147768_2_ + this.field_147859_h;
        double d12 = p_147768_2_ + this.field_147861_i;
        double d13 = p_147768_4_ + this.field_147855_j;
        double d14 = p_147768_6_ + this.field_147851_l;
        double d15 = p_147768_6_ + this.field_147853_m;

        if (this.field_147838_g)
        {
            d11 = p_147768_2_ + this.field_147861_i;
            d12 = p_147768_2_ + this.field_147859_h;
        }

        if (this.field_147863_w)
        {
            tessellator.setColorOpaque_F(this.field_147872_ap, this.field_147846_at, this.field_147854_ax);
            tessellator.setBrightness(this.field_147864_al);
            tessellator.addVertexWithUV(d11, d13, d15, d8, d10);
            tessellator.setColorOpaque_F(this.field_147852_aq, this.field_147860_au, this.field_147841_ay);
            tessellator.setBrightness(this.field_147874_am);
            tessellator.addVertexWithUV(d11, d13, d14, d3, d5);
            tessellator.setColorOpaque_F(this.field_147850_ar, this.field_147858_av, this.field_147839_az);
            tessellator.setBrightness(this.field_147876_an);
            tessellator.addVertexWithUV(d12, d13, d14, d7, d9);
            tessellator.setColorOpaque_F(this.field_147848_as, this.field_147856_aw, this.field_147833_aA);
            tessellator.setBrightness(this.field_147870_ao);
            tessellator.addVertexWithUV(d12, d13, d15, d4, d6);
        }
        else
        {
            tessellator.addVertexWithUV(d11, d13, d15, d8, d10);
            tessellator.addVertexWithUV(d11, d13, d14, d3, d5);
            tessellator.addVertexWithUV(d12, d13, d14, d7, d9);
            tessellator.addVertexWithUV(d12, d13, d15, d4, d6);
        }
    }

    public void func_147806_b(Block p_147806_1_, double p_147806_2_, double p_147806_4_, double p_147806_6_, IIcon p_147806_8_)
    {
        Tessellator tessellator = Tessellator.instance;

        if (this.func_147744_b())
        {
            p_147806_8_ = this.field_147840_d;
        }

        double d3 = (double)p_147806_8_.getInterpolatedU(this.field_147859_h * 16.0D);
        double d4 = (double)p_147806_8_.getInterpolatedU(this.field_147861_i * 16.0D);
        double d5 = (double)p_147806_8_.getInterpolatedV(this.field_147851_l * 16.0D);
        double d6 = (double)p_147806_8_.getInterpolatedV(this.field_147853_m * 16.0D);

        if (this.field_147859_h < 0.0D || this.field_147861_i > 1.0D)
        {
            d3 = (double)p_147806_8_.getMinU();
            d4 = (double)p_147806_8_.getMaxU();
        }

        if (this.field_147851_l < 0.0D || this.field_147853_m > 1.0D)
        {
            d5 = (double)p_147806_8_.getMinV();
            d6 = (double)p_147806_8_.getMaxV();
        }

        double d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;

        if (this.field_147867_u == 1)
        {
            d3 = (double)p_147806_8_.getInterpolatedU(this.field_147851_l * 16.0D);
            d5 = (double)p_147806_8_.getInterpolatedV(16.0D - this.field_147861_i * 16.0D);
            d4 = (double)p_147806_8_.getInterpolatedU(this.field_147853_m * 16.0D);
            d6 = (double)p_147806_8_.getInterpolatedV(16.0D - this.field_147859_h * 16.0D);
            d9 = d5;
            d10 = d6;
            d7 = d3;
            d8 = d4;
            d5 = d6;
            d6 = d9;
        }
        else if (this.field_147867_u == 2)
        {
            d3 = (double)p_147806_8_.getInterpolatedU(16.0D - this.field_147853_m * 16.0D);
            d5 = (double)p_147806_8_.getInterpolatedV(this.field_147859_h * 16.0D);
            d4 = (double)p_147806_8_.getInterpolatedU(16.0D - this.field_147851_l * 16.0D);
            d6 = (double)p_147806_8_.getInterpolatedV(this.field_147861_i * 16.0D);
            d7 = d4;
            d8 = d3;
            d3 = d4;
            d4 = d8;
            d9 = d6;
            d10 = d5;
        }
        else if (this.field_147867_u == 3)
        {
            d3 = (double)p_147806_8_.getInterpolatedU(16.0D - this.field_147859_h * 16.0D);
            d4 = (double)p_147806_8_.getInterpolatedU(16.0D - this.field_147861_i * 16.0D);
            d5 = (double)p_147806_8_.getInterpolatedV(16.0D - this.field_147851_l * 16.0D);
            d6 = (double)p_147806_8_.getInterpolatedV(16.0D - this.field_147853_m * 16.0D);
            d7 = d4;
            d8 = d3;
            d9 = d5;
            d10 = d6;
        }

        double d11 = p_147806_2_ + this.field_147859_h;
        double d12 = p_147806_2_ + this.field_147861_i;
        double d13 = p_147806_4_ + this.field_147857_k;
        double d14 = p_147806_6_ + this.field_147851_l;
        double d15 = p_147806_6_ + this.field_147853_m;

        if (this.field_147838_g)
        {
            d11 = p_147806_2_ + this.field_147861_i;
            d12 = p_147806_2_ + this.field_147859_h;
        }

        if (this.field_147863_w)
        {
            tessellator.setColorOpaque_F(this.field_147872_ap, this.field_147846_at, this.field_147854_ax);
            tessellator.setBrightness(this.field_147864_al);
            tessellator.addVertexWithUV(d12, d13, d15, d4, d6);
            tessellator.setColorOpaque_F(this.field_147852_aq, this.field_147860_au, this.field_147841_ay);
            tessellator.setBrightness(this.field_147874_am);
            tessellator.addVertexWithUV(d12, d13, d14, d7, d9);
            tessellator.setColorOpaque_F(this.field_147850_ar, this.field_147858_av, this.field_147839_az);
            tessellator.setBrightness(this.field_147876_an);
            tessellator.addVertexWithUV(d11, d13, d14, d3, d5);
            tessellator.setColorOpaque_F(this.field_147848_as, this.field_147856_aw, this.field_147833_aA);
            tessellator.setBrightness(this.field_147870_ao);
            tessellator.addVertexWithUV(d11, d13, d15, d8, d10);
        }
        else
        {
            tessellator.addVertexWithUV(d12, d13, d15, d4, d6);
            tessellator.addVertexWithUV(d12, d13, d14, d7, d9);
            tessellator.addVertexWithUV(d11, d13, d14, d3, d5);
            tessellator.addVertexWithUV(d11, d13, d15, d8, d10);
        }
    }

    public void func_147761_c(Block p_147761_1_, double p_147761_2_, double p_147761_4_, double p_147761_6_, IIcon p_147761_8_)
    {
        Tessellator tessellator = Tessellator.instance;

        if (this.func_147744_b())
        {
            p_147761_8_ = this.field_147840_d;
        }

        double d3 = (double)p_147761_8_.getInterpolatedU(this.field_147861_i * 16.0D);
        double d4 = (double)p_147761_8_.getInterpolatedU(this.field_147859_h * 16.0D);
        double d5 = (double)p_147761_8_.getInterpolatedV(16.0D - this.field_147857_k * 16.0D);
        double d6 = (double)p_147761_8_.getInterpolatedV(16.0D - this.field_147855_j * 16.0D);
        double d7;

        if (this.field_147842_e)
        {
            d7 = d3;
            d3 = d4;
            d4 = d7;
        }

        if (this.field_147859_h < 0.0D || this.field_147861_i > 1.0D)
        {
            d3 = (double)p_147761_8_.getMinU();
            d4 = (double)p_147761_8_.getMaxU();
        }

        if (this.field_147855_j < 0.0D || this.field_147857_k > 1.0D)
        {
            d5 = (double)p_147761_8_.getMinV();
            d6 = (double)p_147761_8_.getMaxV();
        }

        d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;

        if (this.field_147875_q == 2)
        {
            d3 = (double)p_147761_8_.getInterpolatedU(this.field_147855_j * 16.0D);
            d5 = (double)p_147761_8_.getInterpolatedV(16.0D - this.field_147859_h * 16.0D);
            d4 = (double)p_147761_8_.getInterpolatedU(this.field_147857_k * 16.0D);
            d6 = (double)p_147761_8_.getInterpolatedV(16.0D - this.field_147861_i * 16.0D);
            d9 = d5;
            d10 = d6;
            d7 = d3;
            d8 = d4;
            d5 = d6;
            d6 = d9;
        }
        else if (this.field_147875_q == 1)
        {
            d3 = (double)p_147761_8_.getInterpolatedU(16.0D - this.field_147857_k * 16.0D);
            d5 = (double)p_147761_8_.getInterpolatedV(this.field_147861_i * 16.0D);
            d4 = (double)p_147761_8_.getInterpolatedU(16.0D - this.field_147855_j * 16.0D);
            d6 = (double)p_147761_8_.getInterpolatedV(this.field_147859_h * 16.0D);
            d7 = d4;
            d8 = d3;
            d3 = d4;
            d4 = d8;
            d9 = d6;
            d10 = d5;
        }
        else if (this.field_147875_q == 3)
        {
            d3 = (double)p_147761_8_.getInterpolatedU(16.0D - this.field_147859_h * 16.0D);
            d4 = (double)p_147761_8_.getInterpolatedU(16.0D - this.field_147861_i * 16.0D);
            d5 = (double)p_147761_8_.getInterpolatedV(this.field_147857_k * 16.0D);
            d6 = (double)p_147761_8_.getInterpolatedV(this.field_147855_j * 16.0D);
            d7 = d4;
            d8 = d3;
            d9 = d5;
            d10 = d6;
        }

        double d11 = p_147761_2_ + this.field_147859_h;
        double d12 = p_147761_2_ + this.field_147861_i;
        double d13 = p_147761_4_ + this.field_147855_j;
        double d14 = p_147761_4_ + this.field_147857_k;
        double d15 = p_147761_6_ + this.field_147851_l;

        if (this.field_147838_g)
        {
            d11 = p_147761_2_ + this.field_147861_i;
            d12 = p_147761_2_ + this.field_147859_h;
        }

        if (this.field_147863_w)
        {
            tessellator.setColorOpaque_F(this.field_147872_ap, this.field_147846_at, this.field_147854_ax);
            tessellator.setBrightness(this.field_147864_al);
            tessellator.addVertexWithUV(d11, d14, d15, d7, d9);
            tessellator.setColorOpaque_F(this.field_147852_aq, this.field_147860_au, this.field_147841_ay);
            tessellator.setBrightness(this.field_147874_am);
            tessellator.addVertexWithUV(d12, d14, d15, d3, d5);
            tessellator.setColorOpaque_F(this.field_147850_ar, this.field_147858_av, this.field_147839_az);
            tessellator.setBrightness(this.field_147876_an);
            tessellator.addVertexWithUV(d12, d13, d15, d8, d10);
            tessellator.setColorOpaque_F(this.field_147848_as, this.field_147856_aw, this.field_147833_aA);
            tessellator.setBrightness(this.field_147870_ao);
            tessellator.addVertexWithUV(d11, d13, d15, d4, d6);
        }
        else
        {
            tessellator.addVertexWithUV(d11, d14, d15, d7, d9);
            tessellator.addVertexWithUV(d12, d14, d15, d3, d5);
            tessellator.addVertexWithUV(d12, d13, d15, d8, d10);
            tessellator.addVertexWithUV(d11, d13, d15, d4, d6);
        }
    }

    public void func_147734_d(Block p_147734_1_, double p_147734_2_, double p_147734_4_, double p_147734_6_, IIcon p_147734_8_)
    {
        Tessellator tessellator = Tessellator.instance;

        if (this.func_147744_b())
        {
            p_147734_8_ = this.field_147840_d;
        }

        double d3 = (double)p_147734_8_.getInterpolatedU(this.field_147859_h * 16.0D);
        double d4 = (double)p_147734_8_.getInterpolatedU(this.field_147861_i * 16.0D);
        double d5 = (double)p_147734_8_.getInterpolatedV(16.0D - this.field_147857_k * 16.0D);
        double d6 = (double)p_147734_8_.getInterpolatedV(16.0D - this.field_147855_j * 16.0D);
        double d7;

        if (this.field_147842_e)
        {
            d7 = d3;
            d3 = d4;
            d4 = d7;
        }

        if (this.field_147859_h < 0.0D || this.field_147861_i > 1.0D)
        {
            d3 = (double)p_147734_8_.getMinU();
            d4 = (double)p_147734_8_.getMaxU();
        }

        if (this.field_147855_j < 0.0D || this.field_147857_k > 1.0D)
        {
            d5 = (double)p_147734_8_.getMinV();
            d6 = (double)p_147734_8_.getMaxV();
        }

        d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;

        if (this.field_147873_r == 1)
        {
            d3 = (double)p_147734_8_.getInterpolatedU(this.field_147855_j * 16.0D);
            d6 = (double)p_147734_8_.getInterpolatedV(16.0D - this.field_147859_h * 16.0D);
            d4 = (double)p_147734_8_.getInterpolatedU(this.field_147857_k * 16.0D);
            d5 = (double)p_147734_8_.getInterpolatedV(16.0D - this.field_147861_i * 16.0D);
            d9 = d5;
            d10 = d6;
            d7 = d3;
            d8 = d4;
            d5 = d6;
            d6 = d9;
        }
        else if (this.field_147873_r == 2)
        {
            d3 = (double)p_147734_8_.getInterpolatedU(16.0D - this.field_147857_k * 16.0D);
            d5 = (double)p_147734_8_.getInterpolatedV(this.field_147859_h * 16.0D);
            d4 = (double)p_147734_8_.getInterpolatedU(16.0D - this.field_147855_j * 16.0D);
            d6 = (double)p_147734_8_.getInterpolatedV(this.field_147861_i * 16.0D);
            d7 = d4;
            d8 = d3;
            d3 = d4;
            d4 = d8;
            d9 = d6;
            d10 = d5;
        }
        else if (this.field_147873_r == 3)
        {
            d3 = (double)p_147734_8_.getInterpolatedU(16.0D - this.field_147859_h * 16.0D);
            d4 = (double)p_147734_8_.getInterpolatedU(16.0D - this.field_147861_i * 16.0D);
            d5 = (double)p_147734_8_.getInterpolatedV(this.field_147857_k * 16.0D);
            d6 = (double)p_147734_8_.getInterpolatedV(this.field_147855_j * 16.0D);
            d7 = d4;
            d8 = d3;
            d9 = d5;
            d10 = d6;
        }

        double d11 = p_147734_2_ + this.field_147859_h;
        double d12 = p_147734_2_ + this.field_147861_i;
        double d13 = p_147734_4_ + this.field_147855_j;
        double d14 = p_147734_4_ + this.field_147857_k;
        double d15 = p_147734_6_ + this.field_147853_m;

        if (this.field_147838_g)
        {
            d11 = p_147734_2_ + this.field_147861_i;
            d12 = p_147734_2_ + this.field_147859_h;
        }

        if (this.field_147863_w)
        {
            tessellator.setColorOpaque_F(this.field_147872_ap, this.field_147846_at, this.field_147854_ax);
            tessellator.setBrightness(this.field_147864_al);
            tessellator.addVertexWithUV(d11, d14, d15, d3, d5);
            tessellator.setColorOpaque_F(this.field_147852_aq, this.field_147860_au, this.field_147841_ay);
            tessellator.setBrightness(this.field_147874_am);
            tessellator.addVertexWithUV(d11, d13, d15, d8, d10);
            tessellator.setColorOpaque_F(this.field_147850_ar, this.field_147858_av, this.field_147839_az);
            tessellator.setBrightness(this.field_147876_an);
            tessellator.addVertexWithUV(d12, d13, d15, d4, d6);
            tessellator.setColorOpaque_F(this.field_147848_as, this.field_147856_aw, this.field_147833_aA);
            tessellator.setBrightness(this.field_147870_ao);
            tessellator.addVertexWithUV(d12, d14, d15, d7, d9);
        }
        else
        {
            tessellator.addVertexWithUV(d11, d14, d15, d3, d5);
            tessellator.addVertexWithUV(d11, d13, d15, d8, d10);
            tessellator.addVertexWithUV(d12, d13, d15, d4, d6);
            tessellator.addVertexWithUV(d12, d14, d15, d7, d9);
        }
    }

    public void func_147798_e(Block p_147798_1_, double p_147798_2_, double p_147798_4_, double p_147798_6_, IIcon p_147798_8_)
    {
        Tessellator tessellator = Tessellator.instance;

        if (this.func_147744_b())
        {
            p_147798_8_ = this.field_147840_d;
        }

        double d3 = (double)p_147798_8_.getInterpolatedU(this.field_147851_l * 16.0D);
        double d4 = (double)p_147798_8_.getInterpolatedU(this.field_147853_m * 16.0D);
        double d5 = (double)p_147798_8_.getInterpolatedV(16.0D - this.field_147857_k * 16.0D);
        double d6 = (double)p_147798_8_.getInterpolatedV(16.0D - this.field_147855_j * 16.0D);
        double d7;

        if (this.field_147842_e)
        {
            d7 = d3;
            d3 = d4;
            d4 = d7;
        }

        if (this.field_147851_l < 0.0D || this.field_147853_m > 1.0D)
        {
            d3 = (double)p_147798_8_.getMinU();
            d4 = (double)p_147798_8_.getMaxU();
        }

        if (this.field_147855_j < 0.0D || this.field_147857_k > 1.0D)
        {
            d5 = (double)p_147798_8_.getMinV();
            d6 = (double)p_147798_8_.getMaxV();
        }

        d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;

        if (this.field_147869_t == 1)
        {
            d3 = (double)p_147798_8_.getInterpolatedU(this.field_147855_j * 16.0D);
            d5 = (double)p_147798_8_.getInterpolatedV(16.0D - this.field_147853_m * 16.0D);
            d4 = (double)p_147798_8_.getInterpolatedU(this.field_147857_k * 16.0D);
            d6 = (double)p_147798_8_.getInterpolatedV(16.0D - this.field_147851_l * 16.0D);
            d9 = d5;
            d10 = d6;
            d7 = d3;
            d8 = d4;
            d5 = d6;
            d6 = d9;
        }
        else if (this.field_147869_t == 2)
        {
            d3 = (double)p_147798_8_.getInterpolatedU(16.0D - this.field_147857_k * 16.0D);
            d5 = (double)p_147798_8_.getInterpolatedV(this.field_147851_l * 16.0D);
            d4 = (double)p_147798_8_.getInterpolatedU(16.0D - this.field_147855_j * 16.0D);
            d6 = (double)p_147798_8_.getInterpolatedV(this.field_147853_m * 16.0D);
            d7 = d4;
            d8 = d3;
            d3 = d4;
            d4 = d8;
            d9 = d6;
            d10 = d5;
        }
        else if (this.field_147869_t == 3)
        {
            d3 = (double)p_147798_8_.getInterpolatedU(16.0D - this.field_147851_l * 16.0D);
            d4 = (double)p_147798_8_.getInterpolatedU(16.0D - this.field_147853_m * 16.0D);
            d5 = (double)p_147798_8_.getInterpolatedV(this.field_147857_k * 16.0D);
            d6 = (double)p_147798_8_.getInterpolatedV(this.field_147855_j * 16.0D);
            d7 = d4;
            d8 = d3;
            d9 = d5;
            d10 = d6;
        }

        double d11 = p_147798_2_ + this.field_147859_h;
        double d12 = p_147798_4_ + this.field_147855_j;
        double d13 = p_147798_4_ + this.field_147857_k;
        double d14 = p_147798_6_ + this.field_147851_l;
        double d15 = p_147798_6_ + this.field_147853_m;

        if (this.field_147838_g)
        {
            d14 = p_147798_6_ + this.field_147853_m;
            d15 = p_147798_6_ + this.field_147851_l;
        }

        if (this.field_147863_w)
        {
            tessellator.setColorOpaque_F(this.field_147872_ap, this.field_147846_at, this.field_147854_ax);
            tessellator.setBrightness(this.field_147864_al);
            tessellator.addVertexWithUV(d11, d13, d15, d7, d9);
            tessellator.setColorOpaque_F(this.field_147852_aq, this.field_147860_au, this.field_147841_ay);
            tessellator.setBrightness(this.field_147874_am);
            tessellator.addVertexWithUV(d11, d13, d14, d3, d5);
            tessellator.setColorOpaque_F(this.field_147850_ar, this.field_147858_av, this.field_147839_az);
            tessellator.setBrightness(this.field_147876_an);
            tessellator.addVertexWithUV(d11, d12, d14, d8, d10);
            tessellator.setColorOpaque_F(this.field_147848_as, this.field_147856_aw, this.field_147833_aA);
            tessellator.setBrightness(this.field_147870_ao);
            tessellator.addVertexWithUV(d11, d12, d15, d4, d6);
        }
        else
        {
            tessellator.addVertexWithUV(d11, d13, d15, d7, d9);
            tessellator.addVertexWithUV(d11, d13, d14, d3, d5);
            tessellator.addVertexWithUV(d11, d12, d14, d8, d10);
            tessellator.addVertexWithUV(d11, d12, d15, d4, d6);
        }
    }

    public void func_147764_f(Block p_147764_1_, double p_147764_2_, double p_147764_4_, double p_147764_6_, IIcon p_147764_8_)
    {
        Tessellator tessellator = Tessellator.instance;

        if (this.func_147744_b())
        {
            p_147764_8_ = this.field_147840_d;
        }

        double d3 = (double)p_147764_8_.getInterpolatedU(this.field_147853_m * 16.0D);
        double d4 = (double)p_147764_8_.getInterpolatedU(this.field_147851_l * 16.0D);
        double d5 = (double)p_147764_8_.getInterpolatedV(16.0D - this.field_147857_k * 16.0D);
        double d6 = (double)p_147764_8_.getInterpolatedV(16.0D - this.field_147855_j * 16.0D);
        double d7;

        if (this.field_147842_e)
        {
            d7 = d3;
            d3 = d4;
            d4 = d7;
        }

        if (this.field_147851_l < 0.0D || this.field_147853_m > 1.0D)
        {
            d3 = (double)p_147764_8_.getMinU();
            d4 = (double)p_147764_8_.getMaxU();
        }

        if (this.field_147855_j < 0.0D || this.field_147857_k > 1.0D)
        {
            d5 = (double)p_147764_8_.getMinV();
            d6 = (double)p_147764_8_.getMaxV();
        }

        d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;

        if (this.field_147871_s == 2)
        {
            d3 = (double)p_147764_8_.getInterpolatedU(this.field_147855_j * 16.0D);
            d5 = (double)p_147764_8_.getInterpolatedV(16.0D - this.field_147851_l * 16.0D);
            d4 = (double)p_147764_8_.getInterpolatedU(this.field_147857_k * 16.0D);
            d6 = (double)p_147764_8_.getInterpolatedV(16.0D - this.field_147853_m * 16.0D);
            d9 = d5;
            d10 = d6;
            d7 = d3;
            d8 = d4;
            d5 = d6;
            d6 = d9;
        }
        else if (this.field_147871_s == 1)
        {
            d3 = (double)p_147764_8_.getInterpolatedU(16.0D - this.field_147857_k * 16.0D);
            d5 = (double)p_147764_8_.getInterpolatedV(this.field_147853_m * 16.0D);
            d4 = (double)p_147764_8_.getInterpolatedU(16.0D - this.field_147855_j * 16.0D);
            d6 = (double)p_147764_8_.getInterpolatedV(this.field_147851_l * 16.0D);
            d7 = d4;
            d8 = d3;
            d3 = d4;
            d4 = d8;
            d9 = d6;
            d10 = d5;
        }
        else if (this.field_147871_s == 3)
        {
            d3 = (double)p_147764_8_.getInterpolatedU(16.0D - this.field_147851_l * 16.0D);
            d4 = (double)p_147764_8_.getInterpolatedU(16.0D - this.field_147853_m * 16.0D);
            d5 = (double)p_147764_8_.getInterpolatedV(this.field_147857_k * 16.0D);
            d6 = (double)p_147764_8_.getInterpolatedV(this.field_147855_j * 16.0D);
            d7 = d4;
            d8 = d3;
            d9 = d5;
            d10 = d6;
        }

        double d11 = p_147764_2_ + this.field_147861_i;
        double d12 = p_147764_4_ + this.field_147855_j;
        double d13 = p_147764_4_ + this.field_147857_k;
        double d14 = p_147764_6_ + this.field_147851_l;
        double d15 = p_147764_6_ + this.field_147853_m;

        if (this.field_147838_g)
        {
            d14 = p_147764_6_ + this.field_147853_m;
            d15 = p_147764_6_ + this.field_147851_l;
        }

        if (this.field_147863_w)
        {
            tessellator.setColorOpaque_F(this.field_147872_ap, this.field_147846_at, this.field_147854_ax);
            tessellator.setBrightness(this.field_147864_al);
            tessellator.addVertexWithUV(d11, d12, d15, d8, d10);
            tessellator.setColorOpaque_F(this.field_147852_aq, this.field_147860_au, this.field_147841_ay);
            tessellator.setBrightness(this.field_147874_am);
            tessellator.addVertexWithUV(d11, d12, d14, d4, d6);
            tessellator.setColorOpaque_F(this.field_147850_ar, this.field_147858_av, this.field_147839_az);
            tessellator.setBrightness(this.field_147876_an);
            tessellator.addVertexWithUV(d11, d13, d14, d7, d9);
            tessellator.setColorOpaque_F(this.field_147848_as, this.field_147856_aw, this.field_147833_aA);
            tessellator.setBrightness(this.field_147870_ao);
            tessellator.addVertexWithUV(d11, d13, d15, d3, d5);
        }
        else
        {
            tessellator.addVertexWithUV(d11, d12, d15, d8, d10);
            tessellator.addVertexWithUV(d11, d12, d14, d4, d6);
            tessellator.addVertexWithUV(d11, d13, d14, d7, d9);
            tessellator.addVertexWithUV(d11, d13, d15, d3, d5);
        }
    }

    public void func_147800_a(Block p_147800_1_, int p_147800_2_, float p_147800_3_)
    {
        Tessellator tessellator = Tessellator.instance;
        boolean flag = p_147800_1_ == Blocks.grass;

        if (p_147800_1_ == Blocks.dispenser || p_147800_1_ == Blocks.dropper || p_147800_1_ == Blocks.furnace)
        {
            p_147800_2_ = 3;
        }

        int j;
        float f1;
        float f2;
        float f3;

        if (this.field_147844_c)
        {
            j = p_147800_1_.func_149741_i(p_147800_2_);

            if (flag)
            {
                j = 16777215;
            }

            f1 = (float)(j >> 16 & 255) / 255.0F;
            f2 = (float)(j >> 8 & 255) / 255.0F;
            f3 = (float)(j & 255) / 255.0F;
            GL11.glColor4f(f1 * p_147800_3_, f2 * p_147800_3_, f3 * p_147800_3_, 1.0F);
        }

        j = p_147800_1_.func_149645_b();
        this.func_147775_a(p_147800_1_);
        int k;

        if (j != 0 && j != 31 && j != 39 && j != 16 && j != 26)
        {
            if (j == 1)
            {
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, -1.0F, 0.0F);
                IIcon iicon = this.func_147787_a(p_147800_1_, 0, p_147800_2_);
                this.func_147765_a(iicon, -0.5D, -0.5D, -0.5D, 1.0F);
                tessellator.draw();
            }
            else if (j == 19)
            {
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, -1.0F, 0.0F);
                p_147800_1_.func_149683_g();
                this.func_147730_a(p_147800_1_, p_147800_2_, this.field_147857_k, -0.5D, -0.5D, -0.5D);
                tessellator.draw();
            }
            else if (j == 23)
            {
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, -1.0F, 0.0F);
                p_147800_1_.func_149683_g();
                tessellator.draw();
            }
            else if (j == 13)
            {
                p_147800_1_.func_149683_g();
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                f1 = 0.0625F;
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, -1.0F, 0.0F);
                this.func_147768_a(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147777_a(p_147800_1_, 0));
                tessellator.draw();
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, 1.0F, 0.0F);
                this.func_147806_b(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147777_a(p_147800_1_, 1));
                tessellator.draw();
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, 0.0F, -1.0F);
                tessellator.addTranslation(0.0F, 0.0F, f1);
                this.func_147761_c(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147777_a(p_147800_1_, 2));
                tessellator.addTranslation(0.0F, 0.0F, -f1);
                tessellator.draw();
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, 0.0F, 1.0F);
                tessellator.addTranslation(0.0F, 0.0F, -f1);
                this.func_147734_d(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147777_a(p_147800_1_, 3));
                tessellator.addTranslation(0.0F, 0.0F, f1);
                tessellator.draw();
                tessellator.startDrawingQuads();
                tessellator.setNormal(-1.0F, 0.0F, 0.0F);
                tessellator.addTranslation(f1, 0.0F, 0.0F);
                this.func_147798_e(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147777_a(p_147800_1_, 4));
                tessellator.addTranslation(-f1, 0.0F, 0.0F);
                tessellator.draw();
                tessellator.startDrawingQuads();
                tessellator.setNormal(1.0F, 0.0F, 0.0F);
                tessellator.addTranslation(-f1, 0.0F, 0.0F);
                this.func_147764_f(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147777_a(p_147800_1_, 5));
                tessellator.addTranslation(f1, 0.0F, 0.0F);
                tessellator.draw();
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            }
            else if (j == 22)
            {
                GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                TileEntityRendererChestHelper.field_147719_a.func_147715_a(p_147800_1_, p_147800_2_, p_147800_3_);
                GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            }
            else if (j == 6)
            {
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, -1.0F, 0.0F);
                this.func_147795_a(p_147800_1_, p_147800_2_, -0.5D, -0.5D, -0.5D);
                tessellator.draw();
            }
            else if (j == 2)
            {
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, -1.0F, 0.0F);
                this.func_147747_a(p_147800_1_, -0.5D, -0.5D, -0.5D, 0.0D, 0.0D, 0);
                tessellator.draw();
            }
            else if (j == 10)
            {
                for (k = 0; k < 2; ++k)
                {
                    if (k == 0)
                    {
                        this.func_147782_a(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.5D);
                    }

                    if (k == 1)
                    {
                        this.func_147782_a(0.0D, 0.0D, 0.5D, 1.0D, 0.5D, 1.0D);
                    }

                    GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, -1.0F, 0.0F);
                    this.func_147768_a(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147777_a(p_147800_1_, 0));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 1.0F, 0.0F);
                    this.func_147806_b(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147777_a(p_147800_1_, 1));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 0.0F, -1.0F);
                    this.func_147761_c(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147777_a(p_147800_1_, 2));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 0.0F, 1.0F);
                    this.func_147734_d(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147777_a(p_147800_1_, 3));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(-1.0F, 0.0F, 0.0F);
                    this.func_147798_e(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147777_a(p_147800_1_, 4));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(1.0F, 0.0F, 0.0F);
                    this.func_147764_f(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147777_a(p_147800_1_, 5));
                    tessellator.draw();
                    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                }
            }
            else if (j == 27)
            {
                k = 0;
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                tessellator.startDrawingQuads();

                for (int l = 0; l < 8; ++l)
                {
                    byte b0 = 0;
                    byte b1 = 1;

                    if (l == 0)
                    {
                        b0 = 2;
                    }

                    if (l == 1)
                    {
                        b0 = 3;
                    }

                    if (l == 2)
                    {
                        b0 = 4;
                    }

                    if (l == 3)
                    {
                        b0 = 5;
                        b1 = 2;
                    }

                    if (l == 4)
                    {
                        b0 = 6;
                        b1 = 3;
                    }

                    if (l == 5)
                    {
                        b0 = 7;
                        b1 = 5;
                    }

                    if (l == 6)
                    {
                        b0 = 6;
                        b1 = 2;
                    }

                    if (l == 7)
                    {
                        b0 = 3;
                    }

                    float f5 = (float)b0 / 16.0F;
                    float f6 = 1.0F - (float)k / 16.0F;
                    float f7 = 1.0F - (float)(k + b1) / 16.0F;
                    k += b1;
                    this.func_147782_a((double)(0.5F - f5), (double)f7, (double)(0.5F - f5), (double)(0.5F + f5), (double)f6, (double)(0.5F + f5));
                    tessellator.setNormal(0.0F, -1.0F, 0.0F);
                    this.func_147768_a(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147777_a(p_147800_1_, 0));
                    tessellator.setNormal(0.0F, 1.0F, 0.0F);
                    this.func_147806_b(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147777_a(p_147800_1_, 1));
                    tessellator.setNormal(0.0F, 0.0F, -1.0F);
                    this.func_147761_c(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147777_a(p_147800_1_, 2));
                    tessellator.setNormal(0.0F, 0.0F, 1.0F);
                    this.func_147734_d(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147777_a(p_147800_1_, 3));
                    tessellator.setNormal(-1.0F, 0.0F, 0.0F);
                    this.func_147798_e(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147777_a(p_147800_1_, 4));
                    tessellator.setNormal(1.0F, 0.0F, 0.0F);
                    this.func_147764_f(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147777_a(p_147800_1_, 5));
                }

                tessellator.draw();
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                this.func_147782_a(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
            }
            else if (j == 11)
            {
                for (k = 0; k < 4; ++k)
                {
                    f2 = 0.125F;

                    if (k == 0)
                    {
                        this.func_147782_a((double)(0.5F - f2), 0.0D, 0.0D, (double)(0.5F + f2), 1.0D, (double)(f2 * 2.0F));
                    }

                    if (k == 1)
                    {
                        this.func_147782_a((double)(0.5F - f2), 0.0D, (double)(1.0F - f2 * 2.0F), (double)(0.5F + f2), 1.0D, 1.0D);
                    }

                    f2 = 0.0625F;

                    if (k == 2)
                    {
                        this.func_147782_a((double)(0.5F - f2), (double)(1.0F - f2 * 3.0F), (double)(-f2 * 2.0F), (double)(0.5F + f2), (double)(1.0F - f2), (double)(1.0F + f2 * 2.0F));
                    }

                    if (k == 3)
                    {
                        this.func_147782_a((double)(0.5F - f2), (double)(0.5F - f2 * 3.0F), (double)(-f2 * 2.0F), (double)(0.5F + f2), (double)(0.5F - f2), (double)(1.0F + f2 * 2.0F));
                    }

                    GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, -1.0F, 0.0F);
                    this.func_147768_a(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147777_a(p_147800_1_, 0));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 1.0F, 0.0F);
                    this.func_147806_b(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147777_a(p_147800_1_, 1));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 0.0F, -1.0F);
                    this.func_147761_c(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147777_a(p_147800_1_, 2));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 0.0F, 1.0F);
                    this.func_147734_d(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147777_a(p_147800_1_, 3));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(-1.0F, 0.0F, 0.0F);
                    this.func_147798_e(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147777_a(p_147800_1_, 4));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(1.0F, 0.0F, 0.0F);
                    this.func_147764_f(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147777_a(p_147800_1_, 5));
                    tessellator.draw();
                    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                }

                this.func_147782_a(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
            }
            else if (j == 21)
            {
                for (k = 0; k < 3; ++k)
                {
                    f2 = 0.0625F;

                    if (k == 0)
                    {
                        this.func_147782_a((double)(0.5F - f2), 0.30000001192092896D, 0.0D, (double)(0.5F + f2), 1.0D, (double)(f2 * 2.0F));
                    }

                    if (k == 1)
                    {
                        this.func_147782_a((double)(0.5F - f2), 0.30000001192092896D, (double)(1.0F - f2 * 2.0F), (double)(0.5F + f2), 1.0D, 1.0D);
                    }

                    f2 = 0.0625F;

                    if (k == 2)
                    {
                        this.func_147782_a((double)(0.5F - f2), 0.5D, 0.0D, (double)(0.5F + f2), (double)(1.0F - f2), 1.0D);
                    }

                    GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, -1.0F, 0.0F);
                    this.func_147768_a(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147777_a(p_147800_1_, 0));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 1.0F, 0.0F);
                    this.func_147806_b(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147777_a(p_147800_1_, 1));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 0.0F, -1.0F);
                    this.func_147761_c(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147777_a(p_147800_1_, 2));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 0.0F, 1.0F);
                    this.func_147734_d(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147777_a(p_147800_1_, 3));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(-1.0F, 0.0F, 0.0F);
                    this.func_147798_e(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147777_a(p_147800_1_, 4));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(1.0F, 0.0F, 0.0F);
                    this.func_147764_f(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147777_a(p_147800_1_, 5));
                    tessellator.draw();
                    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                }
            }
            else if (j == 32)
            {
                for (k = 0; k < 2; ++k)
                {
                    if (k == 0)
                    {
                        this.func_147782_a(0.0D, 0.0D, 0.3125D, 1.0D, 0.8125D, 0.6875D);
                    }

                    if (k == 1)
                    {
                        this.func_147782_a(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);
                    }

                    GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, -1.0F, 0.0F);
                    this.func_147768_a(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147787_a(p_147800_1_, 0, p_147800_2_));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 1.0F, 0.0F);
                    this.func_147806_b(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147787_a(p_147800_1_, 1, p_147800_2_));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 0.0F, -1.0F);
                    this.func_147761_c(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147787_a(p_147800_1_, 2, p_147800_2_));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 0.0F, 1.0F);
                    this.func_147734_d(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147787_a(p_147800_1_, 3, p_147800_2_));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(-1.0F, 0.0F, 0.0F);
                    this.func_147798_e(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147787_a(p_147800_1_, 4, p_147800_2_));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(1.0F, 0.0F, 0.0F);
                    this.func_147764_f(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147787_a(p_147800_1_, 5, p_147800_2_));
                    tessellator.draw();
                    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                }

                this.func_147782_a(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
            }
            else if (j == 35)
            {
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                this.func_147728_a((BlockAnvil)p_147800_1_, 0, 0, 0, p_147800_2_ << 2, true);
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            }
            else if (j == 34)
            {
                for (k = 0; k < 3; ++k)
                {
                    if (k == 0)
                    {
                        this.func_147782_a(0.125D, 0.0D, 0.125D, 0.875D, 0.1875D, 0.875D);
                        this.func_147757_a(this.func_147745_b(Blocks.obsidian));
                    }
                    else if (k == 1)
                    {
                        this.func_147782_a(0.1875D, 0.1875D, 0.1875D, 0.8125D, 0.875D, 0.8125D);
                        this.func_147757_a(this.func_147745_b(Blocks.beacon));
                    }
                    else if (k == 2)
                    {
                        this.func_147782_a(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
                        this.func_147757_a(this.func_147745_b(Blocks.glass));
                    }

                    GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, -1.0F, 0.0F);
                    this.func_147768_a(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147787_a(p_147800_1_, 0, p_147800_2_));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 1.0F, 0.0F);
                    this.func_147806_b(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147787_a(p_147800_1_, 1, p_147800_2_));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 0.0F, -1.0F);
                    this.func_147761_c(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147787_a(p_147800_1_, 2, p_147800_2_));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 0.0F, 1.0F);
                    this.func_147734_d(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147787_a(p_147800_1_, 3, p_147800_2_));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(-1.0F, 0.0F, 0.0F);
                    this.func_147798_e(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147787_a(p_147800_1_, 4, p_147800_2_));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(1.0F, 0.0F, 0.0F);
                    this.func_147764_f(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147787_a(p_147800_1_, 5, p_147800_2_));
                    tessellator.draw();
                    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                }

                this.func_147782_a(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
                this.func_147771_a();
            }
            else if (j == 38)
            {
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                this.func_147799_a((BlockHopper)p_147800_1_, 0, 0, 0, 0, true);
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            }
            else
            {
                FMLRenderAccessLibrary.renderInventoryBlock(this, p_147800_1_, p_147800_2_, j);
            }
        }
        else
        {
            if (j == 16)
            {
                p_147800_2_ = 1;
            }

            p_147800_1_.func_149683_g();
            this.func_147775_a(p_147800_1_);
            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, -1.0F, 0.0F);
            this.func_147768_a(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147787_a(p_147800_1_, 0, p_147800_2_));
            tessellator.draw();

            if (flag && this.field_147844_c)
            {
                k = p_147800_1_.func_149741_i(p_147800_2_);
                f2 = (float)(k >> 16 & 255) / 255.0F;
                f3 = (float)(k >> 8 & 255) / 255.0F;
                float f4 = (float)(k & 255) / 255.0F;
                GL11.glColor4f(f2 * p_147800_3_, f3 * p_147800_3_, f4 * p_147800_3_, 1.0F);
            }

            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 1.0F, 0.0F);
            this.func_147806_b(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147787_a(p_147800_1_, 1, p_147800_2_));
            tessellator.draw();

            if (flag && this.field_147844_c)
            {
                GL11.glColor4f(p_147800_3_, p_147800_3_, p_147800_3_, 1.0F);
            }

            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, -1.0F);
            this.func_147761_c(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147787_a(p_147800_1_, 2, p_147800_2_));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, 1.0F);
            this.func_147734_d(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147787_a(p_147800_1_, 3, p_147800_2_));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(-1.0F, 0.0F, 0.0F);
            this.func_147798_e(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147787_a(p_147800_1_, 4, p_147800_2_));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(1.0F, 0.0F, 0.0F);
            this.func_147764_f(p_147800_1_, 0.0D, 0.0D, 0.0D, this.func_147787_a(p_147800_1_, 5, p_147800_2_));
            tessellator.draw();
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        }
    }

    public static boolean func_147739_a(int p_147739_0_)
    {
        switch (p_147739_0_)
        {
        case 0 : return true ;
        case 31: return true ;
        case 39: return true ;
        case 13: return true ;
        case 10: return true ;
        case 11: return true ;
        case 27: return true ;
        case 22: return true ;
        case 21: return true ;
        case 16: return true ;
        case 26: return true ;
        case 32: return true ;
        case 34: return true ;
        case 35: return true ;
        default: return FMLRenderAccessLibrary.renderItemAsFull3DBlock(p_147739_0_);
        }
    }

    public IIcon func_147793_a(Block p_147793_1_, IBlockAccess p_147793_2_, int p_147793_3_, int p_147793_4_, int p_147793_5_, int p_147793_6_)
    {
        return this.func_147758_b(p_147793_1_.func_149673_e(p_147793_2_, p_147793_3_, p_147793_4_, p_147793_5_, p_147793_6_));
    }

    public IIcon func_147787_a(Block p_147787_1_, int p_147787_2_, int p_147787_3_)
    {
        return this.func_147758_b(p_147787_1_.func_149691_a(p_147787_2_, p_147787_3_));
    }

    public IIcon func_147777_a(Block p_147777_1_, int p_147777_2_)
    {
        return this.func_147758_b(p_147777_1_.func_149733_h(p_147777_2_));
    }

    public IIcon func_147745_b(Block p_147745_1_)
    {
        return this.func_147758_b(p_147745_1_.func_149733_h(1));
    }

    public IIcon func_147758_b(IIcon p_147758_1_)
    {
        if (p_147758_1_ == null)
        {
            p_147758_1_ = ((TextureMap)Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.locationBlocksTexture)).getAtlasSprite("missingno");
        }

        return (IIcon)p_147758_1_;
    }
}