package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.apache.commons.io.Charsets;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiRepair extends GuiContainer implements ICrafting
{
    private static final ResourceLocation field_147093_u = new ResourceLocation("textures/gui/container/anvil.png");
    private ContainerRepair field_147092_v;
    private GuiTextField field_147091_w;
    private InventoryPlayer field_147094_x;
    private static final String __OBFID = "CL_00000738";

    public GuiRepair(InventoryPlayer par1InventoryPlayer, World par2World, int par3, int par4, int par5)
    {
        super(new ContainerRepair(par1InventoryPlayer, par2World, par3, par4, par5, Minecraft.getMinecraft().thePlayer));
        this.field_147094_x = par1InventoryPlayer;
        this.field_147092_v = (ContainerRepair)this.field_147002_h;
    }

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        int i = (this.field_146294_l - this.field_146999_f) / 2;
        int j = (this.field_146295_m - this.field_147000_g) / 2;
        this.field_147091_w = new GuiTextField(this.field_146289_q, i + 62, j + 24, 103, 12);
        this.field_147091_w.func_146193_g(-1);
        this.field_147091_w.func_146204_h(-1);
        this.field_147091_w.func_146185_a(false);
        this.field_147091_w.func_146203_f(40);
        this.field_147002_h.removeCraftingFromCrafters(this);
        this.field_147002_h.addCraftingToCrafters(this);
    }

    public void func_146281_b()
    {
        super.func_146281_b();
        Keyboard.enableRepeatEvents(false);
        this.field_147002_h.removeCraftingFromCrafters(this);
    }

    protected void func_146979_b(int p_146979_1_, int p_146979_2_)
    {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        this.field_146289_q.drawString(I18n.getStringParams("container.repair", new Object[0]), 60, 6, 4210752);

        if (this.field_147092_v.maximumCost > 0)
        {
            int k = 8453920;
            boolean flag = true;
            String s = I18n.getStringParams("container.repair.cost", new Object[] {Integer.valueOf(this.field_147092_v.maximumCost)});

            if (this.field_147092_v.maximumCost >= 40 && !this.field_146297_k.thePlayer.capabilities.isCreativeMode)
            {
                s = I18n.getStringParams("container.repair.expensive", new Object[0]);
                k = 16736352;
            }
            else if (!this.field_147092_v.getSlot(2).getHasStack())
            {
                flag = false;
            }
            else if (!this.field_147092_v.getSlot(2).canTakeStack(this.field_147094_x.player))
            {
                k = 16736352;
            }

            if (flag)
            {
                int l = -16777216 | (k & 16579836) >> 2 | k & -16777216;
                int i1 = this.field_146999_f - 8 - this.field_146289_q.getStringWidth(s);
                byte b0 = 67;

                if (this.field_146289_q.getUnicodeFlag())
                {
                    drawRect(i1 - 3, b0 - 2, this.field_146999_f - 7, b0 + 10, -16777216);
                    drawRect(i1 - 2, b0 - 1, this.field_146999_f - 8, b0 + 9, -12895429);
                }
                else
                {
                    this.field_146289_q.drawString(s, i1, b0 + 1, l);
                    this.field_146289_q.drawString(s, i1 + 1, b0, l);
                    this.field_146289_q.drawString(s, i1 + 1, b0 + 1, l);
                }

                this.field_146289_q.drawString(s, i1, b0, k);
            }
        }

        GL11.glEnable(GL11.GL_LIGHTING);
    }

    // JAVADOC METHOD $$ func_73869_a
    protected void keyTyped(char par1, int par2)
    {
        if (this.field_147091_w.func_146201_a(par1, par2))
        {
            this.func_147090_g();
        }
        else
        {
            super.keyTyped(par1, par2);
        }
    }

    private void func_147090_g()
    {
        String s = this.field_147091_w.func_146179_b();
        Slot slot = this.field_147092_v.getSlot(0);

        if (slot != null && slot.getHasStack() && !slot.getStack().hasDisplayName() && s.equals(slot.getStack().getDisplayName()))
        {
            s = "";
        }

        this.field_147092_v.updateItemName(s);
        this.field_146297_k.thePlayer.sendQueue.func_147297_a(new C17PacketCustomPayload("MC|ItemName", s.getBytes(Charsets.UTF_8)));
    }

    // JAVADOC METHOD $$ func_73864_a
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        this.field_147091_w.func_146192_a(par1, par2, par3);
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        super.drawScreen(par1, par2, par3);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        this.field_147091_w.func_146194_f();
    }

    protected void func_146976_a(float p_146976_1_, int p_146976_2_, int p_146976_3_)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.field_146297_k.getTextureManager().bindTexture(field_147093_u);
        int k = (this.field_146294_l - this.field_146999_f) / 2;
        int l = (this.field_146295_m - this.field_147000_g) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.field_146999_f, this.field_147000_g);
        this.drawTexturedModalRect(k + 59, l + 20, 0, this.field_147000_g + (this.field_147092_v.getSlot(0).getHasStack() ? 0 : 16), 110, 16);

        if ((this.field_147092_v.getSlot(0).getHasStack() || this.field_147092_v.getSlot(1).getHasStack()) && !this.field_147092_v.getSlot(2).getHasStack())
        {
            this.drawTexturedModalRect(k + 99, l + 45, this.field_146999_f, 0, 28, 21);
        }
    }

    public void sendContainerAndContentsToPlayer(Container par1Container, List par2List)
    {
        this.sendSlotContents(par1Container, 0, par1Container.getSlot(0).getStack());
    }

    // JAVADOC METHOD $$ func_71111_a
    public void sendSlotContents(Container par1Container, int par2, ItemStack par3ItemStack)
    {
        if (par2 == 0)
        {
            this.field_147091_w.func_146180_a(par3ItemStack == null ? "" : par3ItemStack.getDisplayName());
            this.field_147091_w.func_146184_c(par3ItemStack != null);

            if (par3ItemStack != null)
            {
                this.func_147090_g();
            }
        }
    }

    // JAVADOC METHOD $$ func_71112_a
    public void sendProgressBarUpdate(Container par1Container, int par2, int par3) {}
}