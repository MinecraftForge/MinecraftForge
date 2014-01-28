package net.minecraft.client.gui.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public abstract class GuiContainer extends GuiScreen
{
    protected static final ResourceLocation field_147001_a = new ResourceLocation("textures/gui/container/inventory.png");
    protected int field_146999_f = 176;
    protected int field_147000_g = 166;
    public Container field_147002_h;
    protected int field_147003_i;
    protected int field_147009_r;
    private Slot field_147006_u;
    private Slot field_147005_v;
    private boolean field_147004_w;
    private ItemStack field_147012_x;
    private int field_147011_y;
    private int field_147010_z;
    private Slot field_146989_A;
    private long field_146990_B;
    private ItemStack field_146991_C;
    private Slot field_146985_D;
    private long field_146986_E;
    protected final Set field_147008_s = new HashSet();
    protected boolean field_147007_t;
    private int field_146987_F;
    private int field_146988_G;
    private boolean field_146995_H;
    private int field_146996_I;
    private long field_146997_J;
    private Slot field_146998_K;
    private int field_146992_L;
    private boolean field_146993_M;
    private ItemStack field_146994_N;
    private static final String __OBFID = "CL_00000737";

    public GuiContainer(Container par1Container)
    {
        this.field_147002_h = par1Container;
        this.field_146995_H = true;
    }

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        super.initGui();
        this.field_146297_k.thePlayer.openContainer = this.field_147002_h;
        this.field_147003_i = (this.field_146294_l - this.field_146999_f) / 2;
        this.field_147009_r = (this.field_146295_m - this.field_147000_g) / 2;
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        this.func_146276_q_();
        int k = this.field_147003_i;
        int l = this.field_147009_r;
        this.func_146976_a(par3, par1, par2);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        super.drawScreen(par1, par2, par3);
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glPushMatrix();
        GL11.glTranslatef((float)k, (float)l, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        this.field_147006_u = null;
        short short1 = 240;
        short short2 = 240;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)short1 / 1.0F, (float)short2 / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int k1;

        for (int i1 = 0; i1 < this.field_147002_h.inventorySlots.size(); ++i1)
        {
            Slot slot = (Slot)this.field_147002_h.inventorySlots.get(i1);
            this.func_146977_a(slot);

            if (this.func_146981_a(slot, par1, par2) && slot.func_111238_b())
            {
                this.field_147006_u = slot;
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                int j1 = slot.xDisplayPosition;
                k1 = slot.yDisplayPosition;
                GL11.glColorMask(true, true, true, false);
                this.drawGradientRect(j1, k1, j1 + 16, k1 + 16, -2130706433, -2130706433);
                GL11.glColorMask(true, true, true, true);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }
        }

        //Forge: Force lighting to be disabled as there are some issue where lighting would
        //incorrectly be applied based on items that are in the inventory.
        GL11.glDisable(GL11.GL_LIGHTING);
        this.func_146979_b(par1, par2);
        GL11.glEnable(GL11.GL_LIGHTING);
        InventoryPlayer inventoryplayer = this.field_146297_k.thePlayer.inventory;
        ItemStack itemstack = this.field_147012_x == null ? inventoryplayer.getItemStack() : this.field_147012_x;

        if (itemstack != null)
        {
            byte b0 = 8;
            k1 = this.field_147012_x == null ? 8 : 16;
            String s = null;

            if (this.field_147012_x != null && this.field_147004_w)
            {
                itemstack = itemstack.copy();
                itemstack.stackSize = MathHelper.ceiling_float_int((float)itemstack.stackSize / 2.0F);
            }
            else if (this.field_147007_t && this.field_147008_s.size() > 1)
            {
                itemstack = itemstack.copy();
                itemstack.stackSize = this.field_146996_I;

                if (itemstack.stackSize == 0)
                {
                    s = "" + EnumChatFormatting.YELLOW + "0";
                }
            }

            this.func_146982_a(itemstack, par1 - k - b0, par2 - l - k1, s);
        }

        if (this.field_146991_C != null)
        {
            float f1 = (float)(Minecraft.getSystemTime() - this.field_146990_B) / 100.0F;

            if (f1 >= 1.0F)
            {
                f1 = 1.0F;
                this.field_146991_C = null;
            }

            k1 = this.field_146989_A.xDisplayPosition - this.field_147011_y;
            int j2 = this.field_146989_A.yDisplayPosition - this.field_147010_z;
            int l1 = this.field_147011_y + (int)((float)k1 * f1);
            int i2 = this.field_147010_z + (int)((float)j2 * f1);
            this.func_146982_a(this.field_146991_C, l1, i2, (String)null);
        }

        GL11.glPopMatrix();

        if (inventoryplayer.getItemStack() == null && this.field_147006_u != null && this.field_147006_u.getHasStack())
        {
            ItemStack itemstack1 = this.field_147006_u.getStack();
            this.func_146285_a(itemstack1, par1, par2);
        }

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        RenderHelper.enableStandardItemLighting();
    }

    private void func_146982_a(ItemStack p_146982_1_, int p_146982_2_, int p_146982_3_, String p_146982_4_)
    {
        GL11.glTranslatef(0.0F, 0.0F, 32.0F);
        this.zLevel = 200.0F;
        field_146296_j.zLevel = 200.0F;
        FontRenderer font = null;
        if (p_146982_1_ != null) font = p_146982_1_.getItem().getFontRenderer(p_146982_1_);
        if (font == null) font = field_146289_q;
        field_146296_j.renderItemAndEffectIntoGUI(font, this.field_146297_k.getTextureManager(), p_146982_1_, p_146982_2_, p_146982_3_);
        field_146296_j.renderItemOverlayIntoGUI(font, this.field_146297_k.getTextureManager(), p_146982_1_, p_146982_2_, p_146982_3_ - (this.field_147012_x == null ? 0 : 8), p_146982_4_);
        this.zLevel = 0.0F;
        field_146296_j.zLevel = 0.0F;
    }

    protected void func_146979_b(int p_146979_1_, int p_146979_2_) {}

    protected abstract void func_146976_a(float var1, int var2, int var3);

    private void func_146977_a(Slot p_146977_1_)
    {
        int i = p_146977_1_.xDisplayPosition;
        int j = p_146977_1_.yDisplayPosition;
        ItemStack itemstack = p_146977_1_.getStack();
        boolean flag = false;
        boolean flag1 = p_146977_1_ == this.field_147005_v && this.field_147012_x != null && !this.field_147004_w;
        ItemStack itemstack1 = this.field_146297_k.thePlayer.inventory.getItemStack();
        String s = null;

        if (p_146977_1_ == this.field_147005_v && this.field_147012_x != null && this.field_147004_w && itemstack != null)
        {
            itemstack = itemstack.copy();
            itemstack.stackSize /= 2;
        }
        else if (this.field_147007_t && this.field_147008_s.contains(p_146977_1_) && itemstack1 != null)
        {
            if (this.field_147008_s.size() == 1)
            {
                return;
            }

            if (Container.func_94527_a(p_146977_1_, itemstack1, true) && this.field_147002_h.canDragIntoSlot(p_146977_1_))
            {
                itemstack = itemstack1.copy();
                flag = true;
                Container.func_94525_a(this.field_147008_s, this.field_146987_F, itemstack, p_146977_1_.getStack() == null ? 0 : p_146977_1_.getStack().stackSize);

                if (itemstack.stackSize > itemstack.getMaxStackSize())
                {
                    s = EnumChatFormatting.YELLOW + "" + itemstack.getMaxStackSize();
                    itemstack.stackSize = itemstack.getMaxStackSize();
                }

                if (itemstack.stackSize > p_146977_1_.getSlotStackLimit())
                {
                    s = EnumChatFormatting.YELLOW + "" + p_146977_1_.getSlotStackLimit();
                    itemstack.stackSize = p_146977_1_.getSlotStackLimit();
                }
            }
            else
            {
                this.field_147008_s.remove(p_146977_1_);
                this.func_146980_g();
            }
        }

        this.zLevel = 100.0F;
        field_146296_j.zLevel = 100.0F;

        if (itemstack == null)
        {
            IIcon iicon = p_146977_1_.getBackgroundIconIndex();

            if (iicon != null)
            {
                GL11.glDisable(GL11.GL_LIGHTING);
                this.field_146297_k.getTextureManager().bindTexture(TextureMap.locationItemsTexture);
                this.drawTexturedModelRectFromIcon(i, j, iicon, 16, 16);
                GL11.glEnable(GL11.GL_LIGHTING);
                flag1 = true;
            }
        }

        if (!flag1)
        {
            if (flag)
            {
                drawRect(i, j, i + 16, j + 16, -2130706433);
            }

            GL11.glEnable(GL11.GL_DEPTH_TEST);
            field_146296_j.renderItemAndEffectIntoGUI(this.field_146289_q, this.field_146297_k.getTextureManager(), itemstack, i, j);
            field_146296_j.renderItemOverlayIntoGUI(this.field_146289_q, this.field_146297_k.getTextureManager(), itemstack, i, j, s);
        }

        field_146296_j.zLevel = 0.0F;
        this.zLevel = 0.0F;
    }

    private void func_146980_g()
    {
        ItemStack itemstack = this.field_146297_k.thePlayer.inventory.getItemStack();

        if (itemstack != null && this.field_147007_t)
        {
            this.field_146996_I = itemstack.stackSize;
            ItemStack itemstack1;
            int i;

            for (Iterator iterator = this.field_147008_s.iterator(); iterator.hasNext(); this.field_146996_I -= itemstack1.stackSize - i)
            {
                Slot slot = (Slot)iterator.next();
                itemstack1 = itemstack.copy();
                i = slot.getStack() == null ? 0 : slot.getStack().stackSize;
                Container.func_94525_a(this.field_147008_s, this.field_146987_F, itemstack1, i);

                if (itemstack1.stackSize > itemstack1.getMaxStackSize())
                {
                    itemstack1.stackSize = itemstack1.getMaxStackSize();
                }

                if (itemstack1.stackSize > slot.getSlotStackLimit())
                {
                    itemstack1.stackSize = slot.getSlotStackLimit();
                }
            }
        }
    }

    private Slot func_146975_c(int p_146975_1_, int p_146975_2_)
    {
        for (int k = 0; k < this.field_147002_h.inventorySlots.size(); ++k)
        {
            Slot slot = (Slot)this.field_147002_h.inventorySlots.get(k);

            if (this.func_146981_a(slot, p_146975_1_, p_146975_2_))
            {
                return slot;
            }
        }

        return null;
    }

    // JAVADOC METHOD $$ func_73864_a
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        boolean flag = par3 == this.field_146297_k.gameSettings.keyBindPickBlock.func_151463_i() + 100;
        Slot slot = this.func_146975_c(par1, par2);
        long l = Minecraft.getSystemTime();
        this.field_146993_M = this.field_146998_K == slot && l - this.field_146997_J < 250L && this.field_146992_L == par3;
        this.field_146995_H = false;

        if (par3 == 0 || par3 == 1 || flag)
        {
            int i1 = this.field_147003_i;
            int j1 = this.field_147009_r;
            boolean flag1 = par1 < i1 || par2 < j1 || par1 >= i1 + this.field_146999_f || par2 >= j1 + this.field_147000_g;
            int k1 = -1;

            if (slot != null)
            {
                k1 = slot.slotNumber;
            }

            if (flag1)
            {
                k1 = -999;
            }

            if (this.field_146297_k.gameSettings.touchscreen && flag1 && this.field_146297_k.thePlayer.inventory.getItemStack() == null)
            {
                this.field_146297_k.func_147108_a((GuiScreen)null);
                return;
            }

            if (k1 != -1)
            {
                if (this.field_146297_k.gameSettings.touchscreen)
                {
                    if (slot != null && slot.getHasStack())
                    {
                        this.field_147005_v = slot;
                        this.field_147012_x = null;
                        this.field_147004_w = par3 == 1;
                    }
                    else
                    {
                        this.field_147005_v = null;
                    }
                }
                else if (!this.field_147007_t)
                {
                    if (this.field_146297_k.thePlayer.inventory.getItemStack() == null)
                    {
                        if (par3 == this.field_146297_k.gameSettings.keyBindPickBlock.func_151463_i() + 100)
                        {
                            this.func_146984_a(slot, k1, par3, 3);
                        }
                        else
                        {
                            boolean flag2 = k1 != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
                            byte b0 = 0;

                            if (flag2)
                            {
                                this.field_146994_N = slot != null && slot.getHasStack() ? slot.getStack() : null;
                                b0 = 1;
                            }
                            else if (k1 == -999)
                            {
                                b0 = 4;
                            }

                            this.func_146984_a(slot, k1, par3, b0);
                        }

                        this.field_146995_H = true;
                    }
                    else
                    {
                        this.field_147007_t = true;
                        this.field_146988_G = par3;
                        this.field_147008_s.clear();

                        if (par3 == 0)
                        {
                            this.field_146987_F = 0;
                        }
                        else if (par3 == 1)
                        {
                            this.field_146987_F = 1;
                        }
                    }
                }
            }
        }

        this.field_146998_K = slot;
        this.field_146997_J = l;
        this.field_146992_L = par3;
    }

    protected void func_146273_a(int p_146273_1_, int p_146273_2_, int p_146273_3_, long p_146273_4_)
    {
        Slot slot = this.func_146975_c(p_146273_1_, p_146273_2_);
        ItemStack itemstack = this.field_146297_k.thePlayer.inventory.getItemStack();

        if (this.field_147005_v != null && this.field_146297_k.gameSettings.touchscreen)
        {
            if (p_146273_3_ == 0 || p_146273_3_ == 1)
            {
                if (this.field_147012_x == null)
                {
                    if (slot != this.field_147005_v)
                    {
                        this.field_147012_x = this.field_147005_v.getStack().copy();
                    }
                }
                else if (this.field_147012_x.stackSize > 1 && slot != null && Container.func_94527_a(slot, this.field_147012_x, false))
                {
                    long i1 = Minecraft.getSystemTime();

                    if (this.field_146985_D == slot)
                    {
                        if (i1 - this.field_146986_E > 500L)
                        {
                            this.func_146984_a(this.field_147005_v, this.field_147005_v.slotNumber, 0, 0);
                            this.func_146984_a(slot, slot.slotNumber, 1, 0);
                            this.func_146984_a(this.field_147005_v, this.field_147005_v.slotNumber, 0, 0);
                            this.field_146986_E = i1 + 750L;
                            --this.field_147012_x.stackSize;
                        }
                    }
                    else
                    {
                        this.field_146985_D = slot;
                        this.field_146986_E = i1;
                    }
                }
            }
        }
        else if (this.field_147007_t && slot != null && itemstack != null && itemstack.stackSize > this.field_147008_s.size() && Container.func_94527_a(slot, itemstack, true) && slot.isItemValid(itemstack) && this.field_147002_h.canDragIntoSlot(slot))
        {
            this.field_147008_s.add(slot);
            this.func_146980_g();
        }
    }

    protected void func_146286_b(int p_146286_1_, int p_146286_2_, int p_146286_3_)
    {
        Slot slot = this.func_146975_c(p_146286_1_, p_146286_2_);
        int l = this.field_147003_i;
        int i1 = this.field_147009_r;
        boolean flag = p_146286_1_ < l || p_146286_2_ < i1 || p_146286_1_ >= l + this.field_146999_f || p_146286_2_ >= i1 + this.field_147000_g;
        int j1 = -1;

        if (slot != null)
        {
            j1 = slot.slotNumber;
        }

        if (flag)
        {
            j1 = -999;
        }

        Slot slot1;
        Iterator iterator;

        if (this.field_146993_M && slot != null && p_146286_3_ == 0 && this.field_147002_h.func_94530_a((ItemStack)null, slot))
        {
            if (func_146272_n())
            {
                if (slot != null && slot.inventory != null && this.field_146994_N != null)
                {
                    iterator = this.field_147002_h.inventorySlots.iterator();

                    while (iterator.hasNext())
                    {
                        slot1 = (Slot)iterator.next();

                        if (slot1 != null && slot1.canTakeStack(this.field_146297_k.thePlayer) && slot1.getHasStack() && slot1.inventory == slot.inventory && Container.func_94527_a(slot1, this.field_146994_N, true))
                        {
                            this.func_146984_a(slot1, slot1.slotNumber, p_146286_3_, 1);
                        }
                    }
                }
            }
            else
            {
                this.func_146984_a(slot, j1, p_146286_3_, 6);
            }

            this.field_146993_M = false;
            this.field_146997_J = 0L;
        }
        else
        {
            if (this.field_147007_t && this.field_146988_G != p_146286_3_)
            {
                this.field_147007_t = false;
                this.field_147008_s.clear();
                this.field_146995_H = true;
                return;
            }

            if (this.field_146995_H)
            {
                this.field_146995_H = false;
                return;
            }

            boolean flag1;

            if (this.field_147005_v != null && this.field_146297_k.gameSettings.touchscreen)
            {
                if (p_146286_3_ == 0 || p_146286_3_ == 1)
                {
                    if (this.field_147012_x == null && slot != this.field_147005_v)
                    {
                        this.field_147012_x = this.field_147005_v.getStack();
                    }

                    flag1 = Container.func_94527_a(slot, this.field_147012_x, false);

                    if (j1 != -1 && this.field_147012_x != null && flag1)
                    {
                        this.func_146984_a(this.field_147005_v, this.field_147005_v.slotNumber, p_146286_3_, 0);
                        this.func_146984_a(slot, j1, 0, 0);

                        if (this.field_146297_k.thePlayer.inventory.getItemStack() != null)
                        {
                            this.func_146984_a(this.field_147005_v, this.field_147005_v.slotNumber, p_146286_3_, 0);
                            this.field_147011_y = p_146286_1_ - l;
                            this.field_147010_z = p_146286_2_ - i1;
                            this.field_146989_A = this.field_147005_v;
                            this.field_146991_C = this.field_147012_x;
                            this.field_146990_B = Minecraft.getSystemTime();
                        }
                        else
                        {
                            this.field_146991_C = null;
                        }
                    }
                    else if (this.field_147012_x != null)
                    {
                        this.field_147011_y = p_146286_1_ - l;
                        this.field_147010_z = p_146286_2_ - i1;
                        this.field_146989_A = this.field_147005_v;
                        this.field_146991_C = this.field_147012_x;
                        this.field_146990_B = Minecraft.getSystemTime();
                    }

                    this.field_147012_x = null;
                    this.field_147005_v = null;
                }
            }
            else if (this.field_147007_t && !this.field_147008_s.isEmpty())
            {
                this.func_146984_a((Slot)null, -999, Container.func_94534_d(0, this.field_146987_F), 5);
                iterator = this.field_147008_s.iterator();

                while (iterator.hasNext())
                {
                    slot1 = (Slot)iterator.next();
                    this.func_146984_a(slot1, slot1.slotNumber, Container.func_94534_d(1, this.field_146987_F), 5);
                }

                this.func_146984_a((Slot)null, -999, Container.func_94534_d(2, this.field_146987_F), 5);
            }
            else if (this.field_146297_k.thePlayer.inventory.getItemStack() != null)
            {
                if (p_146286_3_ == this.field_146297_k.gameSettings.keyBindPickBlock.func_151463_i() + 100)
                {
                    this.func_146984_a(slot, j1, p_146286_3_, 3);
                }
                else
                {
                    flag1 = j1 != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));

                    if (flag1)
                    {
                        this.field_146994_N = slot != null && slot.getHasStack() ? slot.getStack() : null;
                    }

                    this.func_146984_a(slot, j1, p_146286_3_, flag1 ? 1 : 0);
                }
            }
        }

        if (this.field_146297_k.thePlayer.inventory.getItemStack() == null)
        {
            this.field_146997_J = 0L;
        }

        this.field_147007_t = false;
    }

    private boolean func_146981_a(Slot p_146981_1_, int p_146981_2_, int p_146981_3_)
    {
        return this.func_146978_c(p_146981_1_.xDisplayPosition, p_146981_1_.yDisplayPosition, 16, 16, p_146981_2_, p_146981_3_);
    }

    protected boolean func_146978_c(int p_146978_1_, int p_146978_2_, int p_146978_3_, int p_146978_4_, int p_146978_5_, int p_146978_6_)
    {
        int k1 = this.field_147003_i;
        int l1 = this.field_147009_r;
        p_146978_5_ -= k1;
        p_146978_6_ -= l1;
        return p_146978_5_ >= p_146978_1_ - 1 && p_146978_5_ < p_146978_1_ + p_146978_3_ + 1 && p_146978_6_ >= p_146978_2_ - 1 && p_146978_6_ < p_146978_2_ + p_146978_4_ + 1;
    }

    protected void func_146984_a(Slot p_146984_1_, int p_146984_2_, int p_146984_3_, int p_146984_4_)
    {
        if (p_146984_1_ != null)
        {
            p_146984_2_ = p_146984_1_.slotNumber;
        }

        this.field_146297_k.playerController.windowClick(this.field_147002_h.windowId, p_146984_2_, p_146984_3_, p_146984_4_, this.field_146297_k.thePlayer);
    }

    // JAVADOC METHOD $$ func_73869_a
    protected void keyTyped(char par1, int par2)
    {
        if (par2 == 1 || par2 == this.field_146297_k.gameSettings.field_151445_Q.func_151463_i())
        {
            this.field_146297_k.thePlayer.closeScreen();
        }

        this.func_146983_a(par2);

        if (this.field_147006_u != null && this.field_147006_u.getHasStack())
        {
            if (par2 == this.field_146297_k.gameSettings.keyBindPickBlock.func_151463_i())
            {
                this.func_146984_a(this.field_147006_u, this.field_147006_u.slotNumber, 0, 3);
            }
            else if (par2 == this.field_146297_k.gameSettings.keyBindDrop.func_151463_i())
            {
                this.func_146984_a(this.field_147006_u, this.field_147006_u.slotNumber, func_146271_m() ? 1 : 0, 4);
            }
        }
    }

    protected boolean func_146983_a(int p_146983_1_)
    {
        if (this.field_146297_k.thePlayer.inventory.getItemStack() == null && this.field_147006_u != null)
        {
            for (int j = 0; j < 9; ++j)
            {
                if (p_146983_1_ == this.field_146297_k.gameSettings.field_151456_ac[j].func_151463_i())
                {
                    this.func_146984_a(this.field_147006_u, this.field_147006_u.slotNumber, j, 2);
                    return true;
                }
            }
        }

        return false;
    }

    public void func_146281_b()
    {
        if (this.field_146297_k.thePlayer != null)
        {
            this.field_147002_h.onContainerClosed(this.field_146297_k.thePlayer);
        }
    }

    // JAVADOC METHOD $$ func_73868_f
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    // JAVADOC METHOD $$ func_73876_c
    public void updateScreen()
    {
        super.updateScreen();

        if (!this.field_146297_k.thePlayer.isEntityAlive() || this.field_146297_k.thePlayer.isDead)
        {
            this.field_146297_k.thePlayer.closeScreen();
        }
    }
}