package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class GuiParticle extends Gui
{
    private List particles = new ArrayList();
    private Minecraft mc;

    public GuiParticle(Minecraft par1Minecraft)
    {
        this.mc = par1Minecraft;
    }

    public void update()
    {
        for (int var1 = 0; var1 < this.particles.size(); ++var1)
        {
            Particle var2 = (Particle)this.particles.get(var1);
            var2.preUpdate();
            var2.update(this);

            if (var2.isDead)
            {
                this.particles.remove(var1--);
            }
        }
    }

    public void draw(float par1)
    {
        this.mc.renderEngine.bindTexture(this.mc.renderEngine.getTexture("/gui/particles.png"));

        for (int var2 = 0; var2 < this.particles.size(); ++var2)
        {
            Particle var3 = (Particle)this.particles.get(var2);
            int var4 = (int)(var3.prevPosX + (var3.posX - var3.prevPosX) * (double)par1 - 4.0D);
            int var5 = (int)(var3.prevPosY + (var3.posY - var3.prevPosY) * (double)par1 - 4.0D);
            float var6 = (float)(var3.prevTintAlpha + (var3.tintAlpha - var3.prevTintAlpha) * (double)par1);
            float var7 = (float)(var3.prevTintRed + (var3.tintRed - var3.prevTintRed) * (double)par1);
            float var8 = (float)(var3.prevTintGreen + (var3.tintGreen - var3.prevTintGreen) * (double)par1);
            float var9 = (float)(var3.prevTintBlue + (var3.tintBlue - var3.prevTintBlue) * (double)par1);
            GL11.glColor4f(var7, var8, var9, var6);
            this.drawTexturedModalRect(var4, var5, 40, 0, 8, 8);
        }
    }
}
