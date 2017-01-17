package net.minecraftforge.fml.client;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import org.apache.logging.log4j.Level;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Tobias on 17.01.2017.
 */
public class GuiErrorBase extends GuiErrorScreen {
    private static final File minecraftDir = new File(Loader.instance().getConfigDir().getParent());
    private static final File clientLog = new File(minecraftDir, "logs/fml-client-latest.log");
    public GuiErrorBase()
    {
        super(null, null);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(1, 50, this.height - 38, this.width/2 -55, 20, I18n.format("fml.button.open.mods.folder")));
        String openFileText = I18n.format("fml.button.open.file", clientLog.getName());
        this.buttonList.add(new GuiButton(2, this.width / 2 + 5, this.height - 38, this.width / 2 - 55, 20, openFileText));
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.id == 1)
        {
            try
            {
                File modsDir = new File(minecraftDir, "mods");
                Desktop.getDesktop().open(modsDir);
            }
            catch (Exception e)
            {
                FMLLog.log(Level.ERROR, e, "Problem opening mods folder");
            }
        }
        else if (button.id == 2)
        {
            try
            {
                Desktop.getDesktop().open(clientLog);
            }
            catch (Exception e)
            {
                FMLLog.log(Level.ERROR, e, "Problem opening log file " + clientLog);
            }
        }
    }

    public void clearButtons()
    {
        buttonList.clear();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        for(GuiButton button : buttonList)
        {
            button.drawButton(this.mc, mouseX, mouseY);
        }
    }
}
