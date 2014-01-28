package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.net.URI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
public class GuiButtonLink extends GuiButton
{
    private static final Logger field_146139_o = LogManager.getLogger();
    private static final String __OBFID = "CL_00000673";

    public GuiButtonLink(int par1, int par2, int par3, int par4, int par5, String par6Str)
    {
        super(par1, par2, par3, par4, par5, par6Str);
    }

    public void func_146138_a(String p_146138_1_)
    {
        try
        {
            URI uri = new URI(p_146138_1_);
            Class oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
            oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, new Object[] {uri});
        }
        catch (Throwable throwable)
        {
            field_146139_o.error("Couldn\'t open link", throwable);
        }
    }
}