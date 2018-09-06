/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraft.client.resources.I18n;

import java.awt.*;
import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiErrorBase extends GuiErrorScreen
{
    private static final Logger LOGGER = LogManager.getLogger();
    static final File minecraftDir = new File(Loader.instance().getConfigDir().getParent());
    static final File logFile = new File(minecraftDir, "logs/latest.log");
    public GuiErrorBase()
    {
        super(null, null);
    }

    private String translateOrDefault(String translateKey, String alternative, Object... format)
    {
        return I18n.hasKey(translateKey) ? I18n.format(translateKey, format) : String.format(alternative, format); //When throwing a DuplicateModsException, the translation system does not work...
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(10, 50, this.height - 38, this.width / 2 - 55, 20, translateOrDefault("fml.button.open.mods.folder", "Open Mods Folder"))
        {
            public void func_194829_a(double p_194829_1_, double p_194829_3_)
            {
                try
                {
                    File modsDir = new File(minecraftDir, "mods");
                    Desktop.getDesktop().open(modsDir);
                }
                catch (Exception e)
                {
                    LOGGER.error("Problem opening mods folder", e);
                }
            }
        });
        String openFileText = translateOrDefault("fml.button.open.file", "Open %s", logFile.getName());
        this.buttonList.add(new GuiButton(11, this.width / 2 + 5, this.height - 38, this.width / 2 - 55, 20, openFileText)
        {
            public void func_194829_a(double p_194829_1_, double p_194829_3_)
            {
                try
                {
                    Desktop.getDesktop().open(logFile);
                }
                catch (Exception e)
                {
                    LOGGER.error("Problem opening log file {}", logFile, e);
                }
            }
        });
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        for (GuiButton button : buttonList)
        {
            button.func_194828_a(mouseX, mouseY, partialTicks);
        }
    }
}
