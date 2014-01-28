package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.net.URI;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiScreenDemo extends GuiScreen
{
    private static final Logger field_146349_a = LogManager.getLogger();
    private static final ResourceLocation field_146348_f = new ResourceLocation("textures/gui/demo_background.png");
    private static final String __OBFID = "CL_00000691";

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        this.field_146292_n.clear();
        byte b0 = -16;
        this.field_146292_n.add(new GuiButton(1, this.field_146294_l / 2 - 116, this.field_146295_m / 2 + 62 + b0, 114, 20, I18n.getStringParams("demo.help.buy", new Object[0])));
        this.field_146292_n.add(new GuiButton(2, this.field_146294_l / 2 + 2, this.field_146295_m / 2 + 62 + b0, 114, 20, I18n.getStringParams("demo.help.later", new Object[0])));
    }

    protected void func_146284_a(GuiButton p_146284_1_)
    {
        switch (p_146284_1_.field_146127_k)
        {
            case 1:
                p_146284_1_.field_146124_l = false;

                try
                {
                    Class oclass = Class.forName("java.awt.Desktop");
                    Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
                    oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, new Object[] {new URI("http://www.minecraft.net/store?source=demo")});
                }
                catch (Throwable throwable)
                {
                    field_146349_a.error("Couldn\'t open link", throwable);
                }

                break;
            case 2:
                this.field_146297_k.func_147108_a((GuiScreen)null);
                this.field_146297_k.setIngameFocus();
        }
    }

    // JAVADOC METHOD $$ func_73876_c
    public void updateScreen()
    {
        super.updateScreen();
    }

    public void func_146276_q_()
    {
        super.func_146276_q_();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.field_146297_k.getTextureManager().bindTexture(field_146348_f);
        int i = (this.field_146294_l - 248) / 2;
        int j = (this.field_146295_m - 166) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, 248, 166);
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        this.func_146276_q_();
        int k = (this.field_146294_l - 248) / 2 + 10;
        int l = (this.field_146295_m - 166) / 2 + 8;
        this.field_146289_q.drawString(I18n.getStringParams("demo.help.title", new Object[0]), k, l, 2039583);
        l += 12;
        GameSettings gamesettings = this.field_146297_k.gameSettings;
        this.field_146289_q.drawString(I18n.getStringParams("demo.help.movementShort", new Object[] {GameSettings.getKeyDisplayString(gamesettings.keyBindForward.func_151463_i()), GameSettings.getKeyDisplayString(gamesettings.keyBindLeft.func_151463_i()), GameSettings.getKeyDisplayString(gamesettings.keyBindBack.func_151463_i()), GameSettings.getKeyDisplayString(gamesettings.keyBindRight.func_151463_i())}), k, l, 5197647);
        this.field_146289_q.drawString(I18n.getStringParams("demo.help.movementMouse", new Object[0]), k, l + 12, 5197647);
        this.field_146289_q.drawString(I18n.getStringParams("demo.help.jump", new Object[] {GameSettings.getKeyDisplayString(gamesettings.keyBindJump.func_151463_i())}), k, l + 24, 5197647);
        this.field_146289_q.drawString(I18n.getStringParams("demo.help.inventory", new Object[] {GameSettings.getKeyDisplayString(gamesettings.field_151445_Q.func_151463_i())}), k, l + 36, 5197647);
        this.field_146289_q.drawSplitString(I18n.getStringParams("demo.help.fullWrapped", new Object[0]), k, l + 68, 218, 2039583);
        super.drawScreen(par1, par2, par3);
    }
}