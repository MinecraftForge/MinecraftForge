package net.minecraftforge.client;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.renderer.Tessellator;

public class GuiControlsScrollPanel extends GuiSlot
{
    private GuiControls controls;
    private GameSettings options;
    private Minecraft mc;
    private String[] message;
    private int _mouseX;
    private int _mouseY;
    private int selected = -1;

    public GuiControlsScrollPanel(GuiControls controls, GameSettings options, Minecraft mc)
    {
        super(mc, controls.width, controls.height, 16, (controls.height - 32) + 4, 25);
        this.controls = controls;
        this.options = options;
        this.mc = mc;
    }

    @Override
    protected int getSize()
    {
        return options.keyBindings.length;
    }

    @Override
    protected void elementClicked(int i, boolean flag)
    {
        if (!flag)
        {
            if (selected == -1)
            {
                selected = i;
            }
            else
            {
                options.setKeyBinding(selected, -100);
                selected = -1;
                KeyBinding.resetKeyBindingArrayAndHash();
            }
        }
    }

    @Override
    protected boolean isSelected(int i)
    {
        return false;
    }

    @Override
    protected void drawBackground() {}

    @Override
    public void drawScreen(int mX, int mY, float f)
    {
        _mouseX = mX;
        _mouseY = mY;

        if (selected != -1 && !Mouse.isButtonDown(0) && Mouse.getDWheel() == 0)
        {
            if (Mouse.next() && Mouse.getEventButtonState())
            {
                System.out.println(Mouse.getEventButton());
                options.setKeyBinding(selected, -100 + Mouse.getEventButton());
                selected = -1;
                KeyBinding.resetKeyBindingArrayAndHash();
            }
        }

        super.drawScreen(mX, mY, f);
    }

    @Override
    protected void drawSlot(int index, int xPosition, int yPosition, int l, Tessellator tessellator)
    {
        int width = 70;
        int height = 20;
        xPosition -= 20;
        boolean flag = _mouseX >= xPosition && _mouseY >= yPosition && _mouseX < xPosition + width && _mouseY < yPosition + height;
        int k = (flag ? 2 : 1);

        GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, mc.renderEngine.getTexture("/gui/gui.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        controls.drawTexturedModalRect(xPosition, yPosition, 0, 46 + k * 20, width / 2, height);
        controls.drawTexturedModalRect(xPosition + width / 2, yPosition, 200 - width / 2, 46 + k * 20, width / 2, height);
        controls.drawString(mc.fontRenderer, options.getKeyBindingDescription(index), xPosition + width + 4, yPosition + 6, 0xFFFFFFFF);

        boolean conflict = false;
        for (int x = 0; x < options.keyBindings.length; x++)
        {
            if (x != index && options.keyBindings[x].keyCode == options.keyBindings[index].keyCode)
            {
                conflict = true;
                break;
            }
        }
        String str = (conflict ? "\247c" : "") + options.getOptionDisplayString(index);
        str = (index == selected ? "\247f> \247e??? \247f<" : str);
        controls.drawCenteredString(mc.fontRenderer, str, xPosition + (width / 2), yPosition + (height - 8) / 2, 0xFFFFFFFF);
    }

    public boolean keyTyped(char c, int i)
    {
        if (selected != -1)
        {
            options.setKeyBinding(selected, i);
            selected = -1;
            KeyBinding.resetKeyBindingArrayAndHash();
            return false;
        }
        return true;
    }
}
