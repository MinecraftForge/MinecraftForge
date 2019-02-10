/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Util;
import net.minecraftforge.fml.ForgeI18n;
import net.minecraftforge.fml.LoadingFailedException;
import net.minecraftforge.fml.ModLoadingException;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static net.minecraft.util.StringUtils.stripControlCodes;

public class LoadingErrorScreen extends GuiErrorScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Path modsDir;
    private final Path logFile;
    private final LoadingFailedException loadingFailedException;
    private LoadingErrorList errorList;
    public LoadingErrorScreen(LoadingFailedException loadingException)
    {
        super(null, null);
        this.loadingFailedException = loadingException;
        this.modsDir = FMLPaths.MODSDIR.get();
        this.logFile = FMLPaths.GAMEDIR.get().resolve(Paths.get("logs","latest.log"));
    }

    private double openModsDir(double mouseX, double mouseY)
    {
        Util.getOSType().openFile(modsDir.toFile());
        return 0.0;
    }

    private double openLogFile(double mouseX, double mouseY)
    {
        Util.getOSType().openFile(logFile.toFile());
        return 0.0;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.buttons.clear();
        this.children.clear();
        this.addButton(new GuiButtonClickConsumer(10, 50, this.height - 38, this.width / 2 - 55, 20,
                ForgeI18n.parseMessage("fml.button.open.mods.folder"), this::openModsDir));
        this.addButton(new GuiButtonClickConsumer(11, this.width / 2 + 5, this.height - 38, this.width / 2 - 55, 20,
                ForgeI18n.parseMessage("fml.button.open.file", logFile.getFileName()), this::openLogFile));
        this.errorList = new LoadingErrorList(this, this.loadingFailedException.getErrors());
        this.children.add(this.errorList);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.errorList.drawScreen(mouseX, mouseY, partialTicks);
        drawMultiLineCenteredString(fontRenderer, ForgeI18n.parseMessage("fml.loadingerrorscreen.header", this.loadingFailedException.getErrors().size()), this.width / 2, 10);
        this.buttons.forEach(button -> button.render(mouseX, mouseY, partialTicks));
    }

    private void drawMultiLineCenteredString(FontRenderer fr, String str, int x, int y) {
        for (String s : fr.listFormattedStringToWidth(str, this.width)) {
            fr.drawStringWithShadow(s, (float) (x - fr.getStringWidth(s) / 2.0), y, 0xFFFFFF);
            y+=fr.FONT_HEIGHT;
        }
    }
    public static class LoadingErrorList extends GuiListExtended<LoadingErrorList.ErrorEntry> {
        LoadingErrorList(final LoadingErrorScreen parent, final List<ModLoadingException> errors) {
            super(parent.mc,parent.width,parent.height,35,parent.height - 50, 2 * parent.mc.fontRenderer.FONT_HEIGHT + 8);
            errors.forEach(e->addEntry(new ErrorEntry(e)));
        }

        @Override
        protected int getScrollBarX()
        {
            return this.right - 6;
        }

        @Override
        public int getListWidth()
        {
            return this.width;
        }

        public class ErrorEntry extends GuiListExtended.IGuiListEntry<ErrorEntry> {
            private final ModLoadingException error;

            ErrorEntry(final ModLoadingException e) {
                this.error = e;
            }

            @Override
            public void drawEntry(final int entryWidth, final int entryHeight, final int mouseX, final int mouseY, final boolean p_194999_5_, final float partialTicks) {
                int top = this.getY();
                int left = this.getX();
                FontRenderer font = Minecraft.getInstance().fontRenderer;
                final List<String> strings = font.listFormattedStringToWidth(error.formatToString(), LoadingErrorList.this.width);
                float f = (float)top + 2;
                for (int i = 0; i < Math.min(strings.size(), 2); i++) {
                    font.drawString(strings.get(i), left + 5, f, 0xFFFFFF);
                    f += font.FONT_HEIGHT;
                }
            }
        }

    }
}
