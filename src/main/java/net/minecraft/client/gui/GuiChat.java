package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.ObjectArrays;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.ClientCommandHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiChat extends GuiScreen
{
    private static final Logger field_146408_f = LogManager.getLogger();
    private String field_146410_g = "";
    private int field_146416_h = -1;
    private boolean field_146417_i;
    private boolean field_146414_r;
    private int field_146413_s;
    private List field_146412_t = new ArrayList();
    private URI field_146411_u;
    protected GuiTextField field_146415_a;
    private String field_146409_v = "";
    private static final String __OBFID = "CL_00000682";

    public GuiChat() {}

    public GuiChat(String par1Str)
    {
        this.field_146409_v = par1Str;
    }

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.field_146416_h = this.field_146297_k.ingameGUI.func_146158_b().func_146238_c().size();
        this.field_146415_a = new GuiTextField(this.field_146289_q, 4, this.field_146295_m - 12, this.field_146294_l - 4, 12);
        this.field_146415_a.func_146203_f(100);
        this.field_146415_a.func_146185_a(false);
        this.field_146415_a.func_146195_b(true);
        this.field_146415_a.func_146180_a(this.field_146409_v);
        this.field_146415_a.func_146205_d(false);
    }

    public void func_146281_b()
    {
        Keyboard.enableRepeatEvents(false);
        this.field_146297_k.ingameGUI.func_146158_b().func_146240_d();
    }

    // JAVADOC METHOD $$ func_73876_c
    public void updateScreen()
    {
        this.field_146415_a.func_146178_a();
    }

    // JAVADOC METHOD $$ func_73869_a
    protected void keyTyped(char par1, int par2)
    {
        this.field_146414_r = false;

        if (par2 == 15)
        {
            this.func_146404_p_();
        }
        else
        {
            this.field_146417_i = false;
        }

        if (par2 == 1)
        {
            this.field_146297_k.func_147108_a((GuiScreen)null);
        }
        else if (par2 != 28 && par2 != 156)
        {
            if (par2 == 200)
            {
                this.func_146402_a(-1);
            }
            else if (par2 == 208)
            {
                this.func_146402_a(1);
            }
            else if (par2 == 201)
            {
                this.field_146297_k.ingameGUI.func_146158_b().func_146229_b(this.field_146297_k.ingameGUI.func_146158_b().func_146232_i() - 1);
            }
            else if (par2 == 209)
            {
                this.field_146297_k.ingameGUI.func_146158_b().func_146229_b(-this.field_146297_k.ingameGUI.func_146158_b().func_146232_i() + 1);
            }
            else
            {
                this.field_146415_a.func_146201_a(par1, par2);
            }
        }
        else
        {
            String s = this.field_146415_a.func_146179_b().trim();

            if (s.length() > 0)
            {
                this.func_146403_a(s);
            }

            this.field_146297_k.func_147108_a((GuiScreen)null);
        }
    }

    public void func_146403_a(String p_146403_1_)
    {
        this.field_146297_k.ingameGUI.func_146158_b().func_146239_a(p_146403_1_);
        if (ClientCommandHandler.instance.executeCommand(field_146297_k.thePlayer, p_146403_1_) == 1) return;
        this.field_146297_k.thePlayer.sendChatMessage(p_146403_1_);
    }

    public void func_146274_d()
    {
        super.func_146274_d();
        int i = Mouse.getEventDWheel();

        if (i != 0)
        {
            if (i > 1)
            {
                i = 1;
            }

            if (i < -1)
            {
                i = -1;
            }

            if (!func_146272_n())
            {
                i *= 7;
            }

            this.field_146297_k.ingameGUI.func_146158_b().func_146229_b(i);
        }
    }

    // JAVADOC METHOD $$ func_73864_a
    protected void mouseClicked(int par1, int par2, int par3)
    {
        if (par3 == 0 && this.field_146297_k.gameSettings.chatLinks)
        {
            IChatComponent ichatcomponent = this.field_146297_k.ingameGUI.func_146158_b().func_146236_a(Mouse.getX(), Mouse.getY());

            if (ichatcomponent != null)
            {
                ClickEvent clickevent = ichatcomponent.func_150256_b().func_150235_h();

                if (clickevent != null)
                {
                    if (func_146272_n())
                    {
                        this.field_146415_a.func_146191_b(ichatcomponent.func_150261_e());
                    }
                    else
                    {
                        URI uri;

                        if (clickevent.func_150669_a() == ClickEvent.Action.OPEN_URL)
                        {
                            try
                            {
                                uri = new URI(clickevent.func_150668_b());

                                if (this.field_146297_k.gameSettings.chatLinksPrompt)
                                {
                                    this.field_146411_u = uri;
                                    this.field_146297_k.func_147108_a(new GuiConfirmOpenLink(this, clickevent.func_150668_b(), 0, false));
                                }
                                else
                                {
                                    this.func_146407_a(uri);
                                }
                            }
                            catch (URISyntaxException urisyntaxexception)
                            {
                                field_146408_f.error("Can\'t open url for " + clickevent, urisyntaxexception);
                            }
                        }
                        else if (clickevent.func_150669_a() == ClickEvent.Action.OPEN_FILE)
                        {
                            uri = (new File(clickevent.func_150668_b())).toURI();
                            this.func_146407_a(uri);
                        }
                        else if (clickevent.func_150669_a() == ClickEvent.Action.SUGGEST_COMMAND)
                        {
                            this.field_146415_a.func_146180_a(clickevent.func_150668_b());
                        }
                        else if (clickevent.func_150669_a() == ClickEvent.Action.RUN_COMMAND)
                        {
                            this.func_146403_a(clickevent.func_150668_b());
                        }
                        else
                        {
                            field_146408_f.error("Don\'t know how to handle " + clickevent);
                        }
                    }

                    return;
                }
            }
        }

        this.field_146415_a.func_146192_a(par1, par2, par3);
        super.mouseClicked(par1, par2, par3);
    }

    public void confirmClicked(boolean par1, int par2)
    {
        if (par2 == 0)
        {
            if (par1)
            {
                this.func_146407_a(this.field_146411_u);
            }

            this.field_146411_u = null;
            this.field_146297_k.func_147108_a(this);
        }
    }

    private void func_146407_a(URI p_146407_1_)
    {
        try
        {
            Class oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
            oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, new Object[] {p_146407_1_});
        }
        catch (Throwable throwable)
        {
            field_146408_f.error("Couldn\'t open link", throwable);
        }
    }

    public void func_146404_p_()
    {
        String s1;

        if (this.field_146417_i)
        {
            this.field_146415_a.func_146175_b(this.field_146415_a.func_146197_a(-1, this.field_146415_a.func_146198_h(), false) - this.field_146415_a.func_146198_h());

            if (this.field_146413_s >= this.field_146412_t.size())
            {
                this.field_146413_s = 0;
            }
        }
        else
        {
            int i = this.field_146415_a.func_146197_a(-1, this.field_146415_a.func_146198_h(), false);
            this.field_146412_t.clear();
            this.field_146413_s = 0;
            String s = this.field_146415_a.func_146179_b().substring(i).toLowerCase();
            s1 = this.field_146415_a.func_146179_b().substring(0, this.field_146415_a.func_146198_h());
            this.func_146405_a(s1, s);

            if (this.field_146412_t.isEmpty())
            {
                return;
            }

            this.field_146417_i = true;
            this.field_146415_a.func_146175_b(i - this.field_146415_a.func_146198_h());
        }

        if (this.field_146412_t.size() > 1)
        {
            StringBuilder stringbuilder = new StringBuilder();

            for (Iterator iterator = this.field_146412_t.iterator(); iterator.hasNext(); stringbuilder.append(s1))
            {
                s1 = (String)iterator.next();

                if (stringbuilder.length() > 0)
                {
                    stringbuilder.append(", ");
                }
            }

            this.field_146297_k.ingameGUI.func_146158_b().func_146234_a(new ChatComponentText(stringbuilder.toString()), 1);
        }

        this.field_146415_a.func_146191_b(EnumChatFormatting.func_110646_a((String)this.field_146412_t.get(this.field_146413_s++)));
    }

    private void func_146405_a(String p_146405_1_, String p_146405_2_)
    {
        if (p_146405_1_.length() >= 1)
        {
            ClientCommandHandler.instance.autoComplete(p_146405_1_, p_146405_2_);
            this.field_146297_k.thePlayer.sendQueue.func_147297_a(new C14PacketTabComplete(p_146405_1_));
            this.field_146414_r = true;
        }
    }

    public void func_146402_a(int p_146402_1_)
    {
        int j = this.field_146416_h + p_146402_1_;
        int k = this.field_146297_k.ingameGUI.func_146158_b().func_146238_c().size();

        if (j < 0)
        {
            j = 0;
        }

        if (j > k)
        {
            j = k;
        }

        if (j != this.field_146416_h)
        {
            if (j == k)
            {
                this.field_146416_h = k;
                this.field_146415_a.func_146180_a(this.field_146410_g);
            }
            else
            {
                if (this.field_146416_h == k)
                {
                    this.field_146410_g = this.field_146415_a.func_146179_b();
                }

                this.field_146415_a.func_146180_a((String)this.field_146297_k.ingameGUI.func_146158_b().func_146238_c().get(j));
                this.field_146416_h = j;
            }
        }
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        drawRect(2, this.field_146295_m - 14, this.field_146294_l - 2, this.field_146295_m - 2, Integer.MIN_VALUE);
        this.field_146415_a.func_146194_f();
        IChatComponent ichatcomponent = this.field_146297_k.ingameGUI.func_146158_b().func_146236_a(Mouse.getX(), Mouse.getY());

        if (ichatcomponent != null && ichatcomponent.func_150256_b().func_150210_i() != null)
        {
            HoverEvent hoverevent = ichatcomponent.func_150256_b().func_150210_i();

            if (hoverevent.func_150701_a() == HoverEvent.Action.SHOW_ITEM)
            {
                ItemStack itemstack = null;

                try
                {
                    NBTBase nbtbase = JsonToNBT.func_150315_a(hoverevent.func_150702_b().func_150260_c());

                    if (nbtbase != null && nbtbase instanceof NBTTagCompound)
                    {
                        itemstack = ItemStack.loadItemStackFromNBT((NBTTagCompound)nbtbase);
                    }
                }
                catch (NBTException nbtexception)
                {
                    ;
                }

                if (itemstack != null)
                {
                    this.func_146285_a(itemstack, par1, par2);
                }
                else
                {
                    this.func_146279_a(EnumChatFormatting.RED + "Invalid Item!", par1, par2);
                }
            }
            else if (hoverevent.func_150701_a() == HoverEvent.Action.SHOW_TEXT)
            {
                this.func_146279_a(hoverevent.func_150702_b().func_150254_d(), par1, par2);
            }
            else if (hoverevent.func_150701_a() == HoverEvent.Action.SHOW_ACHIEVEMENT)
            {
                StatBase statbase = StatList.func_151177_a(hoverevent.func_150702_b().func_150260_c());

                if (statbase != null)
                {
                    IChatComponent ichatcomponent1 = statbase.func_150951_e();
                    ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("stats.tooltip.type." + (statbase.isAchievement() ? "achievement" : "statistic"), new Object[0]);
                    chatcomponenttranslation.func_150256_b().func_150217_b(Boolean.valueOf(true));
                    String s = statbase instanceof Achievement ? ((Achievement)statbase).getDescription() : null;
                    ArrayList arraylist = Lists.newArrayList(new String[] {ichatcomponent1.func_150254_d(), chatcomponenttranslation.func_150254_d()});

                    if (s != null)
                    {
                        arraylist.addAll(this.field_146289_q.listFormattedStringToWidth(s, 150));
                    }

                    this.func_146283_a(arraylist, par1, par2);
                }
                else
                {
                    this.func_146279_a(EnumChatFormatting.RED + "Invalid statistic/achievement!", par1, par2);
                }
            }

            GL11.glDisable(GL11.GL_LIGHTING);
        }

        super.drawScreen(par1, par2, par3);
    }

    public void func_146406_a(String[] p_146406_1_)
    {
        if (this.field_146414_r)
        {
            this.field_146417_i = false;
            this.field_146412_t.clear();
            String[] astring1 = p_146406_1_;
            int i = p_146406_1_.length;

            String[] complete = ClientCommandHandler.instance.latestAutoComplete;
            if (complete != null)
            {
                astring1 = ObjectArrays.concat(complete, astring1, String.class);
                i = astring1.length;
            }

            for (int j = 0; j < i; ++j)
            {
                String s = astring1[j];

                if (s.length() > 0)
                {
                    this.field_146412_t.add(s);
                }
            }

            if (this.field_146412_t.size() > 0)
            {
                this.field_146417_i = true;
                this.func_146404_p_();
            }
        }
    }

    // JAVADOC METHOD $$ func_73868_f
    public boolean doesGuiPauseGame()
    {
        return false;
    }
}