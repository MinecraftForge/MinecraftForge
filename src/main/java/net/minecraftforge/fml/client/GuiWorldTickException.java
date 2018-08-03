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

package net.minecraftforge.fml.client;

import java.io.IOException;

import org.apache.commons.lang3.text.WordUtils;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.crash.CrashReport;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ICrashCallable;

public class GuiWorldTickException extends GuiScreen
{
    
    private final CrashReport theCrashReport;
    private final boolean hasShown;
    private final boolean serverSide;
    
    public static boolean hasShownThisScreenBeforeNow = false;
    
    public GuiWorldTickException(CrashReport crash, boolean serverside)
    {
        this.theCrashReport = crash;
        this.hasShown = hasShownThisScreenBeforeNow;
        this.serverSide = serverside;
        
        if(!this.hasShown) {
            FMLCommonHandler.instance().registerCrashCallable(new ICrashCallable() { //makes sure ignorant people don't post tainted crash reports

                @Override
                public String call() throws Exception {
                    
                    return "NO! The game has already crashed before but the user chose not to restart minecraft. This is not a clean enviroment!";
                }

                @Override
                public String getLabel() {
                    return "Is this the first crash";
                }
                
            });
        }
        
        hasShownThisScreenBeforeNow = true;
    }

    public void initGui()
    {
        this.buttonList.clear();
        this.buttonList.add(new GuiOptionButton(0, this.width / 2 - 155, this.height / 4 + 120 + 12, I18n.format("gui.toTitle")));
        this.buttonList.add(new GuiOptionButton(1, this.width / 2 - 155 + 160, this.height / 4 + 120 + 12, I18n.format("menu.quit")));
    }
    
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0)
        {
            this.mc.displayGuiScreen(new GuiMainMenu());
        }
        else if (button.id == 1)
        {
            this.mc.shutdown();
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, "World Tick Exception!", this.width / 2, this.height / 4 - 60 + 20, 16777215);
        
        String eagler = this.serverSide ? "A fatal exception has been caught during world ticking:" : "A fatal exception has been caught during clientside ticking:";
        this.drawString(this.fontRenderer, eagler, this.width / 2 - 140, this.height / 4 - 70 + 60 + 0, 10526880);
        
        int offset = this.height / 4 - 70 + 60 + 18;
        String[] wrappedException = WordUtils.wrap(this.theCrashReport.getCrashCause().toString(), 55, "\n", true).split("\n");
        for(String s : wrappedException)
        {
            this.drawCenteredString(this.fontRenderer, s, this.width / 2, offset, 0xFFAAAA);
            offset += 9;
        }
        
        String remark = this.hasShown ? "(you have)" : "(you haven't)";

        this.drawString(this.fontRenderer, "The full stack traces and crash report has been printed to", this.width / 2 - 140, offset + 18, 10526880);
        this.drawString(this.fontRenderer, "your minecraft log. Please do not post them on the forums", this.width / 2 - 140, offset + 27, 10526880);
        this.drawString(this.fontRenderer, "if you've already seen this screen once before "+remark, this.width / 2 - 140, offset + 36, 10526880);
        
        this.drawString(this.fontRenderer, "It should be safe to return to the title screen and continue", this.width / 2 - 140, offset + 54, 10526880);
        this.drawString(this.fontRenderer, "playing with you legos but there's the possibility things", this.width / 2 - 140, offset + 63, 10526880);
        this.drawString(this.fontRenderer, "might still be messed up somewhere deep within minceraft.", this.width / 2 - 140, offset + 72, 10526880);
        this.drawString(this.fontRenderer, "If that's case then you should please restart your game.", this.width / 2 - 140, offset + 81, 10526880);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

}
