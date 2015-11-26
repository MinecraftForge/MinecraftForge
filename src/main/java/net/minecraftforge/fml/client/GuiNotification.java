package net.minecraftforge.fml.client;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.StartupQuery;

public class GuiNotification extends GuiScreen
{
    public GuiNotification(StartupQuery query)
    {
        this.query = query;
    }

    @Override
    public void initGui()
    {
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height - 38, I18n.format("gui.done")));
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.enabled && button.id == 0)
        {
            FMLClientHandler.instance().showGuiScreen(null);
            query.finish();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();

        String[] lines = query.getText().split("\n");

        int spaceAvailable = this.height - 38 - 20;
        int spaceRequired = Math.min(spaceAvailable, 10 + 10 * lines.length);

        int offset = 10 + (spaceAvailable - spaceRequired) / 2; // vertically centered

        for (String line : lines)
        {
            if (offset >= spaceAvailable)
            {
                this.drawCenteredString(this.fontRendererObj, "...", this.width / 2, offset, 0xFFFFFF);
                break;
            }
            else
            {
                if (!line.isEmpty()) this.drawCenteredString(this.fontRendererObj, line, this.width / 2, offset, 0xFFFFFF);
                offset += 10;
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected final StartupQuery query;
}
