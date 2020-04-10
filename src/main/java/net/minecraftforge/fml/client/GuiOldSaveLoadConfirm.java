/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.fml.client;

import java.io.File;
import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.StartupQuery;
import net.minecraftforge.fml.common.ZipperUtil;

public class GuiOldSaveLoadConfirm extends GuiYesNo implements GuiYesNoCallback {

    private String dirName;
    private String saveName;
    private File zip;
    private GuiScreen parent;
    public GuiOldSaveLoadConfirm(String dirName, String saveName, GuiScreen parent)
    {
        super(null, "", "", 0);
        this.parent = parent;
        this.dirName = dirName;
        this.saveName = saveName;
        this.zip = new File(FMLClientHandler.instance().getClient().mcDataDir,String.format("%s-%2$td%2$tm%2$ty%2$tH%2$tM%2$tS.zip", dirName, System.currentTimeMillis()));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, String.format("The world %s contains pre-update modding data", saveName), this.width / 2, 50, 16777215);
        this.drawCenteredString(this.fontRenderer, String.format("There may be problems updating it to this version"), this.width / 2, 70, 16777215);
        this.drawCenteredString(this.fontRenderer, String.format("FML will save a zip to %s", zip.getName()), this.width / 2, 90, 16777215);
        this.drawCenteredString(this.fontRenderer, String.format("Do you wish to continue loading?"), this.width / 2, 110, 16777215);
        int k;

        for (k = 0; k < this.buttonList.size(); ++k)
        {
            this.buttonList.get(k).drawButton(this.mc, mouseX, mouseY, partialTicks);
        }

        for (k = 0; k < this.labelList.size(); ++k)
        {
            this.labelList.get(k).drawLabel(this.mc, mouseX, mouseY);
        }
    }
    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.id == 1)
        {
            FMLClientHandler.instance().showGuiScreen(parent);
        }
        else
        {
            FMLLog.log.info("Capturing current state of world {} into file {}", saveName, zip.getAbsolutePath());
            try
            {
                String skip = System.getProperty("fml.doNotBackup");
                if (skip == null || !"true".equals(skip))
                {
                    ZipperUtil.zip(new File(FMLClientHandler.instance().getSavesDir(), dirName), zip);
                }
                else
                {
                    for (int x = 0; x < 10; x++)
                        FMLLog.log.fatal("!!!!!!!!!! UPDATING WORLD WITHOUT DOING BACKUP !!!!!!!!!!!!!!!!");
                }
            } catch (IOException e)
            {
                FMLLog.log.warn("There was a problem saving the backup {}. Please fix and try again", zip.getName(), e);
                FMLClientHandler.instance().showGuiScreen(new GuiBackupFailed(parent, zip));
                return;
            }
            FMLClientHandler.instance().showGuiScreen(null);

            try
            {
                mc.launchIntegratedServer(dirName, saveName, null);
            }
            catch (StartupQuery.AbortedException e)
            {
                // ignore
            }
        }
    }
}
