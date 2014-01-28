package net.minecraft.client.renderer.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderEnchantmentTable;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TileEntityRendererDispatcher
{
    public Map field_147559_m = new HashMap();
    public static TileEntityRendererDispatcher field_147556_a = new TileEntityRendererDispatcher();
    private FontRenderer field_147557_n;
    public static double field_147554_b;
    public static double field_147555_c;
    public static double field_147552_d;
    public TextureManager field_147553_e;
    public World field_147550_f;
    public EntityLivingBase field_147551_g;
    public float field_147562_h;
    public float field_147563_i;
    public double field_147560_j;
    public double field_147561_k;
    public double field_147558_l;
    private static final String __OBFID = "CL_00000963";

    private TileEntityRendererDispatcher()
    {
        this.field_147559_m.put(TileEntitySign.class, new TileEntitySignRenderer());
        this.field_147559_m.put(TileEntityMobSpawner.class, new TileEntityMobSpawnerRenderer());
        this.field_147559_m.put(TileEntityPiston.class, new TileEntityRendererPiston());
        this.field_147559_m.put(TileEntityChest.class, new TileEntityChestRenderer());
        this.field_147559_m.put(TileEntityEnderChest.class, new TileEntityEnderChestRenderer());
        this.field_147559_m.put(TileEntityEnchantmentTable.class, new RenderEnchantmentTable());
        this.field_147559_m.put(TileEntityEndPortal.class, new RenderEndPortal());
        this.field_147559_m.put(TileEntityBeacon.class, new TileEntityBeaconRenderer());
        this.field_147559_m.put(TileEntitySkull.class, new TileEntitySkullRenderer());
        Iterator iterator = this.field_147559_m.values().iterator();

        while (iterator.hasNext())
        {
            TileEntitySpecialRenderer tileentityspecialrenderer = (TileEntitySpecialRenderer)iterator.next();
            tileentityspecialrenderer.func_147497_a(this);
        }
    }

    public TileEntitySpecialRenderer func_147546_a(Class p_147546_1_)
    {
        TileEntitySpecialRenderer tileentityspecialrenderer = (TileEntitySpecialRenderer)this.field_147559_m.get(p_147546_1_);

        if (tileentityspecialrenderer == null && p_147546_1_ != TileEntity.class)
        {
            tileentityspecialrenderer = this.func_147546_a(p_147546_1_.getSuperclass());
            this.field_147559_m.put(p_147546_1_, tileentityspecialrenderer);
        }

        return tileentityspecialrenderer;
    }

    public boolean func_147545_a(TileEntity p_147545_1_)
    {
        return this.func_147547_b(p_147545_1_) != null;
    }

    public TileEntitySpecialRenderer func_147547_b(TileEntity p_147547_1_)
    {
        return p_147547_1_ == null ? null : this.func_147546_a(p_147547_1_.getClass());
    }

    public void func_147542_a(World p_147542_1_, TextureManager p_147542_2_, FontRenderer p_147542_3_, EntityLivingBase p_147542_4_, float p_147542_5_)
    {
        if (this.field_147550_f != p_147542_1_)
        {
            this.func_147543_a(p_147542_1_);
        }

        this.field_147553_e = p_147542_2_;
        this.field_147551_g = p_147542_4_;
        this.field_147557_n = p_147542_3_;
        this.field_147562_h = p_147542_4_.prevRotationYaw + (p_147542_4_.rotationYaw - p_147542_4_.prevRotationYaw) * p_147542_5_;
        this.field_147563_i = p_147542_4_.prevRotationPitch + (p_147542_4_.rotationPitch - p_147542_4_.prevRotationPitch) * p_147542_5_;
        this.field_147560_j = p_147542_4_.lastTickPosX + (p_147542_4_.posX - p_147542_4_.lastTickPosX) * (double)p_147542_5_;
        this.field_147561_k = p_147542_4_.lastTickPosY + (p_147542_4_.posY - p_147542_4_.lastTickPosY) * (double)p_147542_5_;
        this.field_147558_l = p_147542_4_.lastTickPosZ + (p_147542_4_.posZ - p_147542_4_.lastTickPosZ) * (double)p_147542_5_;
    }

    public void func_147544_a(TileEntity p_147544_1_, float p_147544_2_)
    {
        if (p_147544_1_.func_145835_a(this.field_147560_j, this.field_147561_k, this.field_147558_l) < p_147544_1_.func_145833_n())
        {
            int i = this.field_147550_f.getLightBrightnessForSkyBlocks(p_147544_1_.field_145851_c, p_147544_1_.field_145848_d, p_147544_1_.field_145849_e, 0);
            int j = i % 65536;
            int k = i / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.func_147549_a(p_147544_1_, (double)p_147544_1_.field_145851_c - field_147554_b, (double)p_147544_1_.field_145848_d - field_147555_c, (double)p_147544_1_.field_145849_e - field_147552_d, p_147544_2_);
        }
    }

    public void func_147549_a(TileEntity p_147549_1_, double p_147549_2_, double p_147549_4_, double p_147549_6_, float p_147549_8_)
    {
        TileEntitySpecialRenderer tileentityspecialrenderer = this.func_147547_b(p_147549_1_);

        if (tileentityspecialrenderer != null)
        {
            try
            {
                tileentityspecialrenderer.func_147500_a(p_147549_1_, p_147549_2_, p_147549_4_, p_147549_6_, p_147549_8_);
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering Block Entity");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Block Entity Details");
                p_147549_1_.func_145828_a(crashreportcategory);
                throw new ReportedException(crashreport);
            }
        }
    }

    public void func_147543_a(World p_147543_1_)
    {
        this.field_147550_f = p_147543_1_;
        Iterator iterator = this.field_147559_m.values().iterator();

        while (iterator.hasNext())
        {
            TileEntitySpecialRenderer tileentityspecialrenderer = (TileEntitySpecialRenderer)iterator.next();

            if (tileentityspecialrenderer != null)
            {
                tileentityspecialrenderer.func_147496_a(p_147543_1_);
            }
        }
    }

    public FontRenderer func_147548_a()
    {
        return this.field_147557_n;
    }
}