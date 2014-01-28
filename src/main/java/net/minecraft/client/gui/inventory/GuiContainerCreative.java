package net.minecraft.client.gui.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class GuiContainerCreative extends InventoryEffectRenderer
{
    private static final ResourceLocation field_147061_u = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
    private static InventoryBasic field_147060_v = new InventoryBasic("tmp", true, 45);
    private static int field_147058_w = CreativeTabs.tabBlock.getTabIndex();
    private float field_147067_x;
    private boolean field_147066_y;
    private boolean field_147065_z;
    private GuiTextField field_147062_A;
    private List field_147063_B;
    private Slot field_147064_C;
    private boolean field_147057_D;
    private CreativeCrafting field_147059_E;
    private static final String __OBFID = "CL_00000752";
    private static int tabPage = 0;
    private int maxPages = 0;

    public GuiContainerCreative(EntityPlayer par1EntityPlayer)
    {
        super(new GuiContainerCreative.ContainerCreative(par1EntityPlayer));
        par1EntityPlayer.openContainer = this.field_147002_h;
        this.field_146291_p = true;
        this.field_147000_g = 136;
        this.field_146999_f = 195;
    }

    // JAVADOC METHOD $$ func_73876_c
    public void updateScreen()
    {
        if (!this.field_146297_k.playerController.isInCreativeMode())
        {
            this.field_146297_k.func_147108_a(new GuiInventory(this.field_146297_k.thePlayer));
        }
    }

    protected void func_146984_a(Slot p_146984_1_, int p_146984_2_, int p_146984_3_, int p_146984_4_)
    {
        this.field_147057_D = true;
        boolean flag = p_146984_4_ == 1;
        p_146984_4_ = p_146984_2_ == -999 && p_146984_4_ == 0 ? 4 : p_146984_4_;
        ItemStack itemstack1;
        InventoryPlayer inventoryplayer;

        if (p_146984_1_ == null && field_147058_w != CreativeTabs.tabInventory.getTabIndex() && p_146984_4_ != 5)
        {
            inventoryplayer = this.field_146297_k.thePlayer.inventory;

            if (inventoryplayer.getItemStack() != null)
            {
                if (p_146984_3_ == 0)
                {
                    this.field_146297_k.thePlayer.dropPlayerItemWithRandomChoice(inventoryplayer.getItemStack(), true);
                    this.field_146297_k.playerController.func_78752_a(inventoryplayer.getItemStack());
                    inventoryplayer.setItemStack((ItemStack)null);
                }

                if (p_146984_3_ == 1)
                {
                    itemstack1 = inventoryplayer.getItemStack().splitStack(1);
                    this.field_146297_k.thePlayer.dropPlayerItemWithRandomChoice(itemstack1, true);
                    this.field_146297_k.playerController.func_78752_a(itemstack1);

                    if (inventoryplayer.getItemStack().stackSize == 0)
                    {
                        inventoryplayer.setItemStack((ItemStack)null);
                    }
                }
            }
        }
        else
        {
            int l;

            if (p_146984_1_ == this.field_147064_C && flag)
            {
                for (l = 0; l < this.field_146297_k.thePlayer.inventoryContainer.getInventory().size(); ++l)
                {
                    this.field_146297_k.playerController.sendSlotPacket((ItemStack)null, l);
                }
            }
            else
            {
                ItemStack itemstack;

                if (field_147058_w == CreativeTabs.tabInventory.getTabIndex())
                {
                    if (p_146984_1_ == this.field_147064_C)
                    {
                        this.field_146297_k.thePlayer.inventory.setItemStack((ItemStack)null);
                    }
                    else if (p_146984_4_ == 4 && p_146984_1_ != null && p_146984_1_.getHasStack())
                    {
                        itemstack = p_146984_1_.decrStackSize(p_146984_3_ == 0 ? 1 : p_146984_1_.getStack().getMaxStackSize());
                        this.field_146297_k.thePlayer.dropPlayerItemWithRandomChoice(itemstack, true);
                        this.field_146297_k.playerController.func_78752_a(itemstack);
                    }
                    else if (p_146984_4_ == 4 && this.field_146297_k.thePlayer.inventory.getItemStack() != null)
                    {
                        this.field_146297_k.thePlayer.dropPlayerItemWithRandomChoice(this.field_146297_k.thePlayer.inventory.getItemStack(), true);
                        this.field_146297_k.playerController.func_78752_a(this.field_146297_k.thePlayer.inventory.getItemStack());
                        this.field_146297_k.thePlayer.inventory.setItemStack((ItemStack)null);
                    }
                    else
                    {
                        this.field_146297_k.thePlayer.inventoryContainer.slotClick(p_146984_1_ == null ? p_146984_2_ : ((GuiContainerCreative.CreativeSlot)p_146984_1_).field_148332_b.slotNumber, p_146984_3_, p_146984_4_, this.field_146297_k.thePlayer);
                        this.field_146297_k.thePlayer.inventoryContainer.detectAndSendChanges();
                    }
                }
                else if (p_146984_4_ != 5 && p_146984_1_.inventory == field_147060_v)
                {
                    inventoryplayer = this.field_146297_k.thePlayer.inventory;
                    itemstack1 = inventoryplayer.getItemStack();
                    ItemStack itemstack2 = p_146984_1_.getStack();
                    ItemStack itemstack3;

                    if (p_146984_4_ == 2)
                    {
                        if (itemstack2 != null && p_146984_3_ >= 0 && p_146984_3_ < 9)
                        {
                            itemstack3 = itemstack2.copy();
                            itemstack3.stackSize = itemstack3.getMaxStackSize();
                            this.field_146297_k.thePlayer.inventory.setInventorySlotContents(p_146984_3_, itemstack3);
                            this.field_146297_k.thePlayer.inventoryContainer.detectAndSendChanges();
                        }

                        return;
                    }

                    if (p_146984_4_ == 3)
                    {
                        if (inventoryplayer.getItemStack() == null && p_146984_1_.getHasStack())
                        {
                            itemstack3 = p_146984_1_.getStack().copy();
                            itemstack3.stackSize = itemstack3.getMaxStackSize();
                            inventoryplayer.setItemStack(itemstack3);
                        }

                        return;
                    }

                    if (p_146984_4_ == 4)
                    {
                        if (itemstack2 != null)
                        {
                            itemstack3 = itemstack2.copy();
                            itemstack3.stackSize = p_146984_3_ == 0 ? 1 : itemstack3.getMaxStackSize();
                            this.field_146297_k.thePlayer.dropPlayerItemWithRandomChoice(itemstack3, true);
                            this.field_146297_k.playerController.func_78752_a(itemstack3);
                        }

                        return;
                    }

                    if (itemstack1 != null && itemstack2 != null && itemstack1.isItemEqual(itemstack2) && ItemStack.areItemStackTagsEqual(itemstack1, itemstack2)) //Forge: Bugfix, Compare NBT data, allow for deletion of enchanted books, MC-12770
                    {
                        if (p_146984_3_ == 0)
                        {
                            if (flag)
                            {
                                itemstack1.stackSize = itemstack1.getMaxStackSize();
                            }
                            else if (itemstack1.stackSize < itemstack1.getMaxStackSize())
                            {
                                ++itemstack1.stackSize;
                            }
                        }
                        else if (itemstack1.stackSize <= 1)
                        {
                            inventoryplayer.setItemStack((ItemStack)null);
                        }
                        else
                        {
                            --itemstack1.stackSize;
                        }
                    }
                    else if (itemstack2 != null && itemstack1 == null)
                    {
                        inventoryplayer.setItemStack(ItemStack.copyItemStack(itemstack2));
                        itemstack1 = inventoryplayer.getItemStack();

                        if (flag)
                        {
                            itemstack1.stackSize = itemstack1.getMaxStackSize();
                        }
                    }
                    else
                    {
                        inventoryplayer.setItemStack((ItemStack)null);
                    }
                }
                else
                {
                    this.field_147002_h.slotClick(p_146984_1_ == null ? p_146984_2_ : p_146984_1_.slotNumber, p_146984_3_, p_146984_4_, this.field_146297_k.thePlayer);

                    if (Container.func_94532_c(p_146984_3_) == 2)
                    {
                        for (l = 0; l < 9; ++l)
                        {
                            this.field_146297_k.playerController.sendSlotPacket(this.field_147002_h.getSlot(45 + l).getStack(), 36 + l);
                        }
                    }
                    else if (p_146984_1_ != null)
                    {
                        itemstack = this.field_147002_h.getSlot(p_146984_1_.slotNumber).getStack();
                        this.field_146297_k.playerController.sendSlotPacket(itemstack, p_146984_1_.slotNumber - this.field_147002_h.inventorySlots.size() + 9 + 36);
                    }
                }
            }
        }
    }

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        if (this.field_146297_k.playerController.isInCreativeMode())
        {
            super.initGui();
            this.field_146292_n.clear();
            Keyboard.enableRepeatEvents(true);
            this.field_147062_A = new GuiTextField(this.field_146289_q, this.field_147003_i + 82, this.field_147009_r + 6, 89, this.field_146289_q.FONT_HEIGHT);
            this.field_147062_A.func_146203_f(15);
            this.field_147062_A.func_146185_a(false);
            this.field_147062_A.func_146189_e(false);
            this.field_147062_A.func_146193_g(16777215);
            int i = field_147058_w;
            field_147058_w = -1;
            this.func_147050_b(CreativeTabs.creativeTabArray[i]);
            this.field_147059_E = new CreativeCrafting(this.field_146297_k);
            this.field_146297_k.thePlayer.inventoryContainer.addCraftingToCrafters(this.field_147059_E);
            int tabCount = CreativeTabs.creativeTabArray.length;
            if (tabCount > 12)
            {
                field_146292_n.add(new GuiButton(101, field_147003_i,                       field_147009_r - 50, 20, 20, "<"));
                field_146292_n.add(new GuiButton(102, field_147003_i + field_146999_f - 20, field_147009_r - 50, 20, 20, ">"));
                maxPages = ((tabCount - 12) / 10) + 1;
            }
        }
        else
        {
            this.field_146297_k.func_147108_a(new GuiInventory(this.field_146297_k.thePlayer));
        }
    }

    public void func_146281_b()
    {
        super.func_146281_b();

        if (this.field_146297_k.thePlayer != null && this.field_146297_k.thePlayer.inventory != null)
        {
            this.field_146297_k.thePlayer.inventoryContainer.removeCraftingFromCrafters(this.field_147059_E);
        }

        Keyboard.enableRepeatEvents(false);
    }

    // JAVADOC METHOD $$ func_73869_a
    protected void keyTyped(char par1, int par2)
    {
        if (!CreativeTabs.creativeTabArray[field_147058_w].hasSearchBar())
        {
            if (GameSettings.isKeyDown(this.field_146297_k.gameSettings.keyBindChat))
            {
                this.func_147050_b(CreativeTabs.tabAllSearch);
            }
            else
            {
                super.keyTyped(par1, par2);
            }
        }
        else
        {
            if (this.field_147057_D)
            {
                this.field_147057_D = false;
                this.field_147062_A.func_146180_a("");
            }

            if (!this.func_146983_a(par2))
            {
                if (this.field_147062_A.func_146201_a(par1, par2))
                {
                    this.func_147053_i();
                }
                else
                {
                    super.keyTyped(par1, par2);
                }
            }
        }
    }

    private void func_147053_i()
    {
        GuiContainerCreative.ContainerCreative containercreative = (GuiContainerCreative.ContainerCreative)this.field_147002_h;
        containercreative.field_148330_a.clear();

        CreativeTabs tab = CreativeTabs.creativeTabArray[field_147058_w];
        if (tab.hasSearchBar() && tab != CreativeTabs.tabAllSearch)
        {
            tab.displayAllReleventItems(containercreative.field_148330_a);
            updateFilteredItems(containercreative);
            return;
        }

        Iterator iterator = Item.field_150901_e.iterator();

        while (iterator.hasNext())
        {
            Item item = (Item)iterator.next();

            if (item != null && item.getCreativeTab() != null)
            {
                item.func_150895_a(item, (CreativeTabs)null, containercreative.field_148330_a);
            }
        }
        updateFilteredItems(containercreative);
    }

    //split from above for custom search tabs
    private void updateFilteredItems(GuiContainerCreative.ContainerCreative containercreative)
    {
        Iterator iterator;
        Enchantment[] aenchantment = Enchantment.enchantmentsList;
        int j = aenchantment.length;

        for (int i = 0; i < j; ++i)
        {
            Enchantment enchantment = aenchantment[i];

            if (enchantment != null && enchantment.type != null)
            {
                Items.enchanted_book.func_92113_a(enchantment, containercreative.field_148330_a);
            }
        }

        iterator = containercreative.field_148330_a.iterator();
        String s1 = this.field_147062_A.func_146179_b().toLowerCase();

        while (iterator.hasNext())
        {
            ItemStack itemstack = (ItemStack)iterator.next();
            boolean flag = false;
            Iterator iterator1 = itemstack.getTooltip(this.field_146297_k.thePlayer, this.field_146297_k.gameSettings.advancedItemTooltips).iterator();

            while (true)
            {
                if (iterator1.hasNext())
                {
                    String s = (String)iterator1.next();

                    if (!s.toLowerCase().contains(s1))
                    {
                        continue;
                    }

                    flag = true;
                }

                if (!flag)
                {
                    iterator.remove();
                }

                break;
            }
        }

        this.field_147067_x = 0.0F;
        containercreative.func_148329_a(0.0F);
    }

    protected void func_146979_b(int p_146979_1_, int p_146979_2_)
    {
        CreativeTabs creativetabs = CreativeTabs.creativeTabArray[field_147058_w];

        if (creativetabs != null && creativetabs.drawInForegroundOfTab())
        {
            GL11.glDisable(GL11.GL_BLEND);
            this.field_146289_q.drawString(I18n.getStringParams(creativetabs.getTranslatedTabLabel(), new Object[0]), 8, 6, 4210752);
        }
    }

    // JAVADOC METHOD $$ func_73864_a
    protected void mouseClicked(int par1, int par2, int par3)
    {
        if (par3 == 0)
        {
            int l = par1 - this.field_147003_i;
            int i1 = par2 - this.field_147009_r;
            CreativeTabs[] acreativetabs = CreativeTabs.creativeTabArray;
            int j1 = acreativetabs.length;

            for (int k1 = 0; k1 < j1; ++k1)
            {
                CreativeTabs creativetabs = acreativetabs[k1];

                if (creativetabs != null && this.func_147049_a(creativetabs, l, i1))
                {
                    return;
                }
            }
        }

        super.mouseClicked(par1, par2, par3);
    }

    protected void func_146286_b(int p_146286_1_, int p_146286_2_, int p_146286_3_)
    {
        if (p_146286_3_ == 0)
        {
            int l = p_146286_1_ - this.field_147003_i;
            int i1 = p_146286_2_ - this.field_147009_r;
            CreativeTabs[] acreativetabs = CreativeTabs.creativeTabArray;
            int j1 = acreativetabs.length;

            for (int k1 = 0; k1 < j1; ++k1)
            {
                CreativeTabs creativetabs = acreativetabs[k1];

                if (creativetabs != null && this.func_147049_a(creativetabs, l, i1))
                {
                    this.func_147050_b(creativetabs);
                    return;
                }
            }
        }

        super.func_146286_b(p_146286_1_, p_146286_2_, p_146286_3_);
    }

    private boolean func_147055_p()
    {
        if (CreativeTabs.creativeTabArray[field_147058_w] == null) return false;
        return field_147058_w != CreativeTabs.tabInventory.getTabIndex() && CreativeTabs.creativeTabArray[field_147058_w].shouldHidePlayerInventory() && ((GuiContainerCreative.ContainerCreative)this.field_147002_h).func_148328_e();
    }

    private void func_147050_b(CreativeTabs p_147050_1_)
    {
        if (p_147050_1_ == null) return;
        int i = field_147058_w;
        field_147058_w = p_147050_1_.getTabIndex();
        GuiContainerCreative.ContainerCreative containercreative = (GuiContainerCreative.ContainerCreative)this.field_147002_h;
        this.field_147008_s.clear();
        containercreative.field_148330_a.clear();
        p_147050_1_.displayAllReleventItems(containercreative.field_148330_a);

        if (p_147050_1_ == CreativeTabs.tabInventory)
        {
            Container container = this.field_146297_k.thePlayer.inventoryContainer;

            if (this.field_147063_B == null)
            {
                this.field_147063_B = containercreative.inventorySlots;
            }

            containercreative.inventorySlots = new ArrayList();

            for (int j = 0; j < container.inventorySlots.size(); ++j)
            {
                GuiContainerCreative.CreativeSlot creativeslot = new GuiContainerCreative.CreativeSlot((Slot)container.inventorySlots.get(j), j);
                containercreative.inventorySlots.add(creativeslot);
                int k;
                int l;
                int i1;

                if (j >= 5 && j < 9)
                {
                    k = j - 5;
                    l = k / 2;
                    i1 = k % 2;
                    creativeslot.xDisplayPosition = 9 + l * 54;
                    creativeslot.yDisplayPosition = 6 + i1 * 27;
                }
                else if (j >= 0 && j < 5)
                {
                    creativeslot.yDisplayPosition = -2000;
                    creativeslot.xDisplayPosition = -2000;
                }
                else if (j < container.inventorySlots.size())
                {
                    k = j - 9;
                    l = k % 9;
                    i1 = k / 9;
                    creativeslot.xDisplayPosition = 9 + l * 18;

                    if (j >= 36)
                    {
                        creativeslot.yDisplayPosition = 112;
                    }
                    else
                    {
                        creativeslot.yDisplayPosition = 54 + i1 * 18;
                    }
                }
            }

            this.field_147064_C = new Slot(field_147060_v, 0, 173, 112);
            containercreative.inventorySlots.add(this.field_147064_C);
        }
        else if (i == CreativeTabs.tabInventory.getTabIndex())
        {
            containercreative.inventorySlots = this.field_147063_B;
            this.field_147063_B = null;
        }

        if (this.field_147062_A != null)
        {
            if (p_147050_1_.hasSearchBar())
            {
                this.field_147062_A.func_146189_e(true);
                this.field_147062_A.func_146205_d(false);
                this.field_147062_A.func_146195_b(true);
                this.field_147062_A.func_146180_a("");
                this.func_147053_i();
            }
            else
            {
                this.field_147062_A.func_146189_e(false);
                this.field_147062_A.func_146205_d(true);
                this.field_147062_A.func_146195_b(false);
            }
        }

        this.field_147067_x = 0.0F;
        containercreative.func_148329_a(0.0F);
    }

    public void func_146274_d()
    {
        super.func_146274_d();
        int i = Mouse.getEventDWheel();

        if (i != 0 && this.func_147055_p())
        {
            int j = ((GuiContainerCreative.ContainerCreative)this.field_147002_h).field_148330_a.size() / 9 - 5 + 1;

            if (i > 0)
            {
                i = 1;
            }

            if (i < 0)
            {
                i = -1;
            }

            this.field_147067_x = (float)((double)this.field_147067_x - (double)i / (double)j);

            if (this.field_147067_x < 0.0F)
            {
                this.field_147067_x = 0.0F;
            }

            if (this.field_147067_x > 1.0F)
            {
                this.field_147067_x = 1.0F;
            }

            ((GuiContainerCreative.ContainerCreative)this.field_147002_h).func_148329_a(this.field_147067_x);
        }
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        boolean flag = Mouse.isButtonDown(0);
        int k = this.field_147003_i;
        int l = this.field_147009_r;
        int i1 = k + 175;
        int j1 = l + 18;
        int k1 = i1 + 14;
        int l1 = j1 + 112;

        if (!this.field_147065_z && flag && par1 >= i1 && par2 >= j1 && par1 < k1 && par2 < l1)
        {
            this.field_147066_y = this.func_147055_p();
        }

        if (!flag)
        {
            this.field_147066_y = false;
        }

        this.field_147065_z = flag;

        if (this.field_147066_y)
        {
            this.field_147067_x = ((float)(par2 - j1) - 7.5F) / ((float)(l1 - j1) - 15.0F);

            if (this.field_147067_x < 0.0F)
            {
                this.field_147067_x = 0.0F;
            }

            if (this.field_147067_x > 1.0F)
            {
                this.field_147067_x = 1.0F;
            }

            ((GuiContainerCreative.ContainerCreative)this.field_147002_h).func_148329_a(this.field_147067_x);
        }

        super.drawScreen(par1, par2, par3);
        CreativeTabs[] acreativetabs = CreativeTabs.creativeTabArray;
        int start = tabPage * 10;
        int i2 = Math.min(acreativetabs.length, ((tabPage + 1) * 10) + 2);
        if (tabPage != 0) start += 2;
        boolean rendered = false;

        for (int j2 = start; j2 < i2; ++j2)
        {
            CreativeTabs creativetabs = acreativetabs[j2];

            if (creativetabs == null) continue;
            if (this.func_147052_b(creativetabs, par1, par2))
            {
                rendered = true;
                break;
            }
        }

        if (!rendered && func_147052_b(CreativeTabs.tabAllSearch, par1, par2))
        {
            func_147052_b(CreativeTabs.tabInventory, par1, par2);
        }

        if (this.field_147064_C != null && field_147058_w == CreativeTabs.tabInventory.getTabIndex() && this.func_146978_c(this.field_147064_C.xDisplayPosition, this.field_147064_C.yDisplayPosition, 16, 16, par1, par2))
        {
            this.func_146279_a(I18n.getStringParams("inventory.binSlot", new Object[0]), par1, par2);
        }

        if (maxPages != 0)
        {
            String page = String.format("%d / %d", tabPage + 1, maxPages + 1);
            int width = field_146289_q.getStringWidth(page);
            GL11.glDisable(GL11.GL_LIGHTING);
            this.zLevel = 300.0F;
            field_146296_j.zLevel = 300.0F;
            field_146289_q.drawString(page, field_147003_i + (field_146999_f / 2) - (width / 2), field_147009_r - 44, -1);
            this.zLevel = 0.0F;
            field_146296_j.zLevel = 0.0F;
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
    }

    protected void func_146285_a(ItemStack p_146285_1_, int p_146285_2_, int p_146285_3_)
    {
        if (field_147058_w == CreativeTabs.tabAllSearch.getTabIndex())
        {
            List list = p_146285_1_.getTooltip(this.field_146297_k.thePlayer, this.field_146297_k.gameSettings.advancedItemTooltips);
            CreativeTabs creativetabs = p_146285_1_.getItem().getCreativeTab();

            if (creativetabs == null && p_146285_1_.getItem() == Items.enchanted_book)
            {
                Map map = EnchantmentHelper.getEnchantments(p_146285_1_);

                if (map.size() == 1)
                {
                    Enchantment enchantment = Enchantment.enchantmentsList[((Integer)map.keySet().iterator().next()).intValue()];
                    CreativeTabs[] acreativetabs = CreativeTabs.creativeTabArray;
                    int k = acreativetabs.length;

                    for (int l = 0; l < k; ++l)
                    {
                        CreativeTabs creativetabs1 = acreativetabs[l];

                        if (creativetabs1.func_111226_a(enchantment.type))
                        {
                            creativetabs = creativetabs1;
                            break;
                        }
                    }
                }
            }

            if (creativetabs != null)
            {
                list.add(1, "" + EnumChatFormatting.BOLD + EnumChatFormatting.BLUE + I18n.getStringParams(creativetabs.getTranslatedTabLabel(), new Object[0]));
            }

            for (int i1 = 0; i1 < list.size(); ++i1)
            {
                if (i1 == 0)
                {
                    list.set(i1, p_146285_1_.getRarity().rarityColor + (String)list.get(i1));
                }
                else
                {
                    list.set(i1, EnumChatFormatting.GRAY + (String)list.get(i1));
                }
            }

            this.func_146283_a(list, p_146285_2_, p_146285_3_);
        }
        else
        {
            super.func_146285_a(p_146285_1_, p_146285_2_, p_146285_3_);
        }
    }

    protected void func_146976_a(float p_146976_1_, int p_146976_2_, int p_146976_3_)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.enableGUIStandardItemLighting();
        CreativeTabs creativetabs = CreativeTabs.creativeTabArray[field_147058_w];
        CreativeTabs[] acreativetabs = CreativeTabs.creativeTabArray;
        int k = acreativetabs.length;
        int l;

        int start = tabPage * 10;
        k = Math.min(acreativetabs.length, ((tabPage + 1) * 10 + 2));
        if (tabPage != 0) start += 2;

        for (l = start; l < k; ++l)
        {
            CreativeTabs creativetabs1 = acreativetabs[l];
            this.field_146297_k.getTextureManager().bindTexture(field_147061_u);

            if (creativetabs1 == null) continue;

            if (creativetabs1.getTabIndex() != field_147058_w)
            {
                this.func_147051_a(creativetabs1);
            }
        }

        if (tabPage != 0)
        {
            if (creativetabs != CreativeTabs.tabAllSearch)
            {
                this.field_146297_k.getTextureManager().bindTexture(field_147061_u);
                func_147051_a(CreativeTabs.tabAllSearch);
            }
            if (creativetabs != CreativeTabs.tabInventory)
            {
                this.field_146297_k.getTextureManager().bindTexture(field_147061_u);
                func_147051_a(CreativeTabs.tabInventory);
            }
        }

        this.field_146297_k.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/creative_inventory/tab_" + creativetabs.getBackgroundImageName()));
        this.drawTexturedModalRect(this.field_147003_i, this.field_147009_r, 0, 0, this.field_146999_f, this.field_147000_g);
        this.field_147062_A.func_146194_f();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int i1 = this.field_147003_i + 175;
        k = this.field_147009_r + 18;
        l = k + 112;
        this.field_146297_k.getTextureManager().bindTexture(field_147061_u);

        if (creativetabs.shouldHidePlayerInventory())
        {
            this.drawTexturedModalRect(i1, k + (int)((float)(l - k - 17) * this.field_147067_x), 232 + (this.func_147055_p() ? 0 : 12), 0, 12, 15);
        }

        if (creativetabs == null || creativetabs.getTabPage() != tabPage)
        {
            if (creativetabs != CreativeTabs.tabAllSearch && creativetabs != CreativeTabs.tabInventory)
            {
                return;
            }
        }

        this.func_147051_a(creativetabs);

        if (creativetabs == CreativeTabs.tabInventory)
        {
            GuiInventory.func_147046_a(this.field_147003_i + 43, this.field_147009_r + 45, 20, (float)(this.field_147003_i + 43 - p_146976_2_), (float)(this.field_147009_r + 45 - 30 - p_146976_3_), this.field_146297_k.thePlayer);
        }
    }

    protected boolean func_147049_a(CreativeTabs p_147049_1_, int p_147049_2_, int p_147049_3_)
    {
        if (p_147049_1_.getTabPage() != tabPage)
        {
            if (p_147049_1_ != CreativeTabs.tabAllSearch &&
                p_147049_1_ != CreativeTabs.tabInventory)
            {
                return false;
            }
        }

        int k = p_147049_1_.getTabColumn();
        int l = 28 * k;
        byte b0 = 0;

        if (k == 5)
        {
            l = this.field_146999_f - 28 + 2;
        }
        else if (k > 0)
        {
            l += k;
        }

        int i1;

        if (p_147049_1_.isTabInFirstRow())
        {
            i1 = b0 - 32;
        }
        else
        {
            i1 = b0 + this.field_147000_g;
        }

        return p_147049_2_ >= l && p_147049_2_ <= l + 28 && p_147049_3_ >= i1 && p_147049_3_ <= i1 + 32;
    }

    protected boolean func_147052_b(CreativeTabs p_147052_1_, int p_147052_2_, int p_147052_3_)
    {
        int k = p_147052_1_.getTabColumn();
        int l = 28 * k;
        byte b0 = 0;

        if (k == 5)
        {
            l = this.field_146999_f - 28 + 2;
        }
        else if (k > 0)
        {
            l += k;
        }

        int i1;

        if (p_147052_1_.isTabInFirstRow())
        {
            i1 = b0 - 32;
        }
        else
        {
            i1 = b0 + this.field_147000_g;
        }

        if (this.func_146978_c(l + 3, i1 + 3, 23, 27, p_147052_2_, p_147052_3_))
        {
            this.func_146279_a(I18n.getStringParams(p_147052_1_.getTranslatedTabLabel(), new Object[0]), p_147052_2_, p_147052_3_);
            return true;
        }
        else
        {
            return false;
        }
    }

    protected void func_147051_a(CreativeTabs p_147051_1_)
    {
        boolean flag = p_147051_1_.getTabIndex() == field_147058_w;
        boolean flag1 = p_147051_1_.isTabInFirstRow();
        int i = p_147051_1_.getTabColumn();
        int j = i * 28;
        int k = 0;
        int l = this.field_147003_i + 28 * i;
        int i1 = this.field_147009_r;
        byte b0 = 32;

        if (flag)
        {
            k += 32;
        }

        if (i == 5)
        {
            l = this.field_147003_i + this.field_146999_f - 28;
        }
        else if (i > 0)
        {
            l += i;
        }

        if (flag1)
        {
            i1 -= 28;
        }
        else
        {
            k += 64;
            i1 += this.field_147000_g - 4;
        }

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glColor3f(1F, 1F, 1F); //Forge: Reset color in case Items change it.
        this.drawTexturedModalRect(l, i1, j, k, 28, b0);
        this.zLevel = 100.0F;
        field_146296_j.zLevel = 100.0F;
        l += 6;
        i1 += 8 + (flag1 ? 1 : -1);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        ItemStack itemstack = p_147051_1_.func_151244_d();
        field_146296_j.renderItemAndEffectIntoGUI(this.field_146289_q, this.field_146297_k.getTextureManager(), itemstack, l, i1);
        field_146296_j.renderItemOverlayIntoGUI(this.field_146289_q, this.field_146297_k.getTextureManager(), itemstack, l, i1);
        GL11.glDisable(GL11.GL_LIGHTING);
        field_146296_j.zLevel = 0.0F;
        this.zLevel = 0.0F;
    }

    protected void func_146284_a(GuiButton p_146284_1_)
    {
        if (p_146284_1_.field_146127_k == 0)
        {
            this.field_146297_k.func_147108_a(new GuiAchievements(this, this.field_146297_k.thePlayer.func_146107_m()));
        }

        if (p_146284_1_.field_146127_k == 1)
        {
            this.field_146297_k.func_147108_a(new GuiStats(this, this.field_146297_k.thePlayer.func_146107_m()));
        }

        if (p_146284_1_.field_146127_k == 101)
        {
            tabPage = Math.max(tabPage - 1, 0);
        }
        else if (p_146284_1_.field_146127_k == 102)
        {
            tabPage = Math.min(tabPage + 1, maxPages);
        }
    }

    public int func_147056_g()
    {
        return field_147058_w;
    }

    @SideOnly(Side.CLIENT)
    class CreativeSlot extends Slot
    {
        private final Slot field_148332_b;
        private static final String __OBFID = "CL_00000754";

        public CreativeSlot(Slot par2Slot, int par3)
        {
            super(par2Slot.inventory, par3, 0, 0);
            this.field_148332_b = par2Slot;
        }

        public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack par2ItemStack)
        {
            this.field_148332_b.onPickupFromSlot(par1EntityPlayer, par2ItemStack);
        }

        // JAVADOC METHOD $$ func_75214_a
        public boolean isItemValid(ItemStack par1ItemStack)
        {
            return this.field_148332_b.isItemValid(par1ItemStack);
        }

        // JAVADOC METHOD $$ func_75211_c
        public ItemStack getStack()
        {
            return this.field_148332_b.getStack();
        }

        // JAVADOC METHOD $$ func_75216_d
        public boolean getHasStack()
        {
            return this.field_148332_b.getHasStack();
        }

        // JAVADOC METHOD $$ func_75215_d
        public void putStack(ItemStack par1ItemStack)
        {
            this.field_148332_b.putStack(par1ItemStack);
        }

        // JAVADOC METHOD $$ func_75218_e
        public void onSlotChanged()
        {
            this.field_148332_b.onSlotChanged();
        }

        // JAVADOC METHOD $$ func_75219_a
        public int getSlotStackLimit()
        {
            return this.field_148332_b.getSlotStackLimit();
        }

        // JAVADOC METHOD $$ func_75212_b
        public IIcon getBackgroundIconIndex()
        {
            return this.field_148332_b.getBackgroundIconIndex();
        }

        // JAVADOC METHOD $$ func_75209_a
        public ItemStack decrStackSize(int par1)
        {
            return this.field_148332_b.decrStackSize(par1);
        }

        // JAVADOC METHOD $$ func_75217_a
        public boolean isSlotInInventory(IInventory par1IInventory, int par2)
        {
            return this.field_148332_b.isSlotInInventory(par1IInventory, par2);
        }
    }

    @SideOnly(Side.CLIENT)
    static class ContainerCreative extends Container
        {
            public List field_148330_a = new ArrayList();
            private static final String __OBFID = "CL_00000753";

            public ContainerCreative(EntityPlayer par1EntityPlayer)
            {
                InventoryPlayer inventoryplayer = par1EntityPlayer.inventory;
                int i;

                for (i = 0; i < 5; ++i)
                {
                    for (int j = 0; j < 9; ++j)
                    {
                        this.addSlotToContainer(new Slot(GuiContainerCreative.field_147060_v, i * 9 + j, 9 + j * 18, 18 + i * 18));
                    }
                }

                for (i = 0; i < 9; ++i)
                {
                    this.addSlotToContainer(new Slot(inventoryplayer, i, 9 + i * 18, 112));
                }

                this.func_148329_a(0.0F);
            }

            public boolean canInteractWith(EntityPlayer par1EntityPlayer)
            {
                return true;
            }

            public void func_148329_a(float p_148329_1_)
            {
                int i = this.field_148330_a.size() / 9 - 5 + 1;
                int j = (int)((double)(p_148329_1_ * (float)i) + 0.5D);

                if (j < 0)
                {
                    j = 0;
                }

                for (int k = 0; k < 5; ++k)
                {
                    for (int l = 0; l < 9; ++l)
                    {
                        int i1 = l + (k + j) * 9;

                        if (i1 >= 0 && i1 < this.field_148330_a.size())
                        {
                            GuiContainerCreative.field_147060_v.setInventorySlotContents(l + k * 9, (ItemStack)this.field_148330_a.get(i1));
                        }
                        else
                        {
                            GuiContainerCreative.field_147060_v.setInventorySlotContents(l + k * 9, (ItemStack)null);
                        }
                    }
                }
            }

            public boolean func_148328_e()
            {
                return this.field_148330_a.size() > 45;
            }

            protected void retrySlotClick(int par1, int par2, boolean par3, EntityPlayer par4EntityPlayer) {}

            // JAVADOC METHOD $$ func_82846_b
            public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
            {
                if (par2 >= this.inventorySlots.size() - 9 && par2 < this.inventorySlots.size())
                {
                    Slot slot = (Slot)this.inventorySlots.get(par2);

                    if (slot != null && slot.getHasStack())
                    {
                        slot.putStack((ItemStack)null);
                    }
                }

                return null;
            }

            public boolean func_94530_a(ItemStack par1ItemStack, Slot par2Slot)
            {
                return par2Slot.yDisplayPosition > 90;
            }

            // JAVADOC METHOD $$ func_94531_b
            public boolean canDragIntoSlot(Slot par1Slot)
            {
                return par1Slot.inventory instanceof InventoryPlayer || par1Slot.yDisplayPosition > 90 && par1Slot.xDisplayPosition <= 162;
            }
        }
}