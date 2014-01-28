package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.client.resources.ResourcePackListEntryDefault;
import net.minecraft.client.resources.ResourcePackListEntryFound;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Sys;

@SideOnly(Side.CLIENT)
public class GuiScreenResourcePacks extends GuiScreen
{
    private static final Logger field_146968_a = LogManager.getLogger();
    private GuiScreen field_146965_f;
    private List field_146966_g;
    private List field_146969_h;
    private GuiResourcePackAvailable field_146970_i;
    private GuiResourcePackSelected field_146967_r;
    private static final String __OBFID = "CL_00000820";

    public GuiScreenResourcePacks(GuiScreen p_i45050_1_)
    {
        this.field_146965_f = p_i45050_1_;
    }

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        this.field_146292_n.add(new GuiOptionButton(2, this.field_146294_l / 2 - 154, this.field_146295_m - 48, I18n.getStringParams("resourcePack.openFolder", new Object[0])));
        this.field_146292_n.add(new GuiOptionButton(1, this.field_146294_l / 2 + 4, this.field_146295_m - 48, I18n.getStringParams("gui.done", new Object[0])));
        this.field_146966_g = new ArrayList();
        this.field_146969_h = new ArrayList();
        ResourcePackRepository resourcepackrepository = this.field_146297_k.getResourcePackRepository();
        resourcepackrepository.updateRepositoryEntriesAll();
        ArrayList arraylist = Lists.newArrayList(resourcepackrepository.getRepositoryEntriesAll());
        arraylist.removeAll(resourcepackrepository.getRepositoryEntries());
        Iterator iterator = arraylist.iterator();
        ResourcePackRepository.Entry entry;

        while (iterator.hasNext())
        {
            entry = (ResourcePackRepository.Entry)iterator.next();
            this.field_146966_g.add(new ResourcePackListEntryFound(this, entry));
        }

        iterator = Lists.reverse(resourcepackrepository.getRepositoryEntries()).iterator();

        while (iterator.hasNext())
        {
            entry = (ResourcePackRepository.Entry)iterator.next();
            this.field_146969_h.add(new ResourcePackListEntryFound(this, entry));
        }

        this.field_146969_h.add(new ResourcePackListEntryDefault(this));
        this.field_146970_i = new GuiResourcePackAvailable(this.field_146297_k, 200, this.field_146295_m, this.field_146966_g);
        this.field_146970_i.func_148140_g(this.field_146294_l / 2 - 4 - 200);
        this.field_146970_i.func_148134_d(7, 8);
        this.field_146967_r = new GuiResourcePackSelected(this.field_146297_k, 200, this.field_146295_m, this.field_146969_h);
        this.field_146967_r.func_148140_g(this.field_146294_l / 2 + 4);
        this.field_146967_r.func_148134_d(7, 8);
    }

    public boolean func_146961_a(ResourcePackListEntry p_146961_1_)
    {
        return this.field_146969_h.contains(p_146961_1_);
    }

    public List func_146962_b(ResourcePackListEntry p_146962_1_)
    {
        return this.func_146961_a(p_146962_1_) ? this.field_146969_h : this.field_146966_g;
    }

    public List func_146964_g()
    {
        return this.field_146966_g;
    }

    public List func_146963_h()
    {
        return this.field_146969_h;
    }

    protected void func_146284_a(GuiButton p_146284_1_)
    {
        if (p_146284_1_.field_146124_l)
        {
            if (p_146284_1_.field_146127_k == 2)
            {
                File file1 = this.field_146297_k.getResourcePackRepository().getDirResourcepacks();
                String s = file1.getAbsolutePath();

                if (Util.getOSType() == Util.EnumOS.MACOS)
                {
                    try
                    {
                        field_146968_a.info(s);
                        Runtime.getRuntime().exec(new String[] {"/usr/bin/open", s});
                        return;
                    }
                    catch (IOException ioexception1)
                    {
                        field_146968_a.error("Couldn\'t open file", ioexception1);
                    }
                }
                else if (Util.getOSType() == Util.EnumOS.WINDOWS)
                {
                    String s1 = String.format("cmd.exe /C start \"Open file\" \"%s\"", new Object[] {s});

                    try
                    {
                        Runtime.getRuntime().exec(s1);
                        return;
                    }
                    catch (IOException ioexception)
                    {
                        field_146968_a.error("Couldn\'t open file", ioexception);
                    }
                }

                boolean flag = false;

                try
                {
                    Class oclass = Class.forName("java.awt.Desktop");
                    Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
                    oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, new Object[] {file1.toURI()});
                }
                catch (Throwable throwable)
                {
                    field_146968_a.error("Couldn\'t open link", throwable);
                    flag = true;
                }

                if (flag)
                {
                    field_146968_a.info("Opening via system class!");
                    Sys.openURL("file://" + s);
                }
            }
            else if (p_146284_1_.field_146127_k == 1)
            {
                ArrayList arraylist = Lists.newArrayList();
                Iterator iterator = this.field_146969_h.iterator();

                while (iterator.hasNext())
                {
                    ResourcePackListEntry resourcepacklistentry = (ResourcePackListEntry)iterator.next();

                    if (resourcepacklistentry instanceof ResourcePackListEntryFound)
                    {
                        arraylist.add(((ResourcePackListEntryFound)resourcepacklistentry).func_148318_i());
                    }
                }

                Collections.reverse(arraylist);
                this.field_146297_k.getResourcePackRepository().func_148527_a(arraylist);
                this.field_146297_k.gameSettings.field_151453_l.clear();
                iterator = arraylist.iterator();

                while (iterator.hasNext())
                {
                    ResourcePackRepository.Entry entry = (ResourcePackRepository.Entry)iterator.next();
                    this.field_146297_k.gameSettings.field_151453_l.add(entry.getResourcePackName());
                }

                this.field_146297_k.gameSettings.saveOptions();
                this.field_146297_k.refreshResources();
                this.field_146297_k.func_147108_a(this.field_146965_f);
            }
        }
    }

    // JAVADOC METHOD $$ func_73864_a
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        this.field_146970_i.func_148179_a(par1, par2, par3);
        this.field_146967_r.func_148179_a(par1, par2, par3);
    }

    protected void func_146286_b(int p_146286_1_, int p_146286_2_, int p_146286_3_)
    {
        super.func_146286_b(p_146286_1_, p_146286_2_, p_146286_3_);
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        this.func_146278_c(0);
        this.field_146970_i.func_148128_a(par1, par2, par3);
        this.field_146967_r.func_148128_a(par1, par2, par3);
        this.drawCenteredString(this.field_146289_q, I18n.getStringParams("resourcePack.title", new Object[0]), this.field_146294_l / 2, 16, 16777215);
        this.drawCenteredString(this.field_146289_q, I18n.getStringParams("resourcePack.folderInfo", new Object[0]), this.field_146294_l / 2 - 77, this.field_146295_m - 26, 8421504);
        super.drawScreen(par1, par2, par3);
    }
}