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

package net.minecraftforge.fml.client.config;

import static net.minecraftforge.fml.client.config.GuiUtils.RESET_CHAR;
import static net.minecraftforge.fml.client.config.GuiUtils.UNDO_CHAR;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.config.GuiConfigEntries.IConfigEntry;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.PostConfigChangedEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;

/**
 * This class is the base GuiScreen for all config GUI screens. It can be extended by mods to provide the top-level config screen
 * that will be called when the Config button is clicked from the Main Menu Mods list.
 *
 * @author bspkrs
 */
public class GuiConfig extends GuiScreen
{
    /**
     * A reference to the screen object that created this. Used for navigating between screens.
     */
    public final GuiScreen parentScreen;
    public String title = "Config GUI";
    @Nullable
    public String titleLine2;
    public final List<IConfigElement> configElements;
    public final List<IConfigEntry> initEntries;
    public GuiConfigEntries entryList;
    protected GuiButtonExt btnDefaultAll;
    protected GuiButtonExt btnUndoAll;
    protected GuiCheckBox chkApplyGlobally;
    public final String modID;
    /**
     * When set to a non-null value the OnConfigChanged and PostConfigChanged events will be posted when the Done button is pressed
     * if any configElements were changed (includes child screens). If not defined, the events will be posted if the parent gui is null
     * or if the parent gui is not an instance of GuiConfig.
     */
    @Nullable
    public final String configID;
    public final boolean isWorldRunning;
    public final boolean allRequireWorldRestart;
    public final boolean allRequireMcRestart;
    public boolean needsRefresh = true;
    protected HoverChecker undoHoverChecker;
    protected HoverChecker resetHoverChecker;
    protected HoverChecker checkBoxHoverChecker;
    
    /**
     * This constructor handles the {@code @Config} configuration classes
     * @param parentScreen the parent GuiScreen object
     * @param mod the mod for which to create a screen
     */
    public GuiConfig(GuiScreen parentScreen, String modid, String title)
    {
        this(parentScreen, modid, false, false, title, ConfigManager.getModConfigClasses(modid)); 
    }
    
    /**
     * 
     * @param parentScreen the parrent GuiScreen object
     * @param modID the mod ID for the mod whose config settings will be editted
     * @param allRequireWorldRestart whether all config elements on this screen require a world restart
     * @param allRequireMcRestart whether all config elements on this screen require a game restart
     * @param title the desired title for this screen. For consistency it is recommended that you pass the path of the config file being
     *            edited.
     * @param configClasses an array of classes annotated with {@code @Config} providing the configuration
     */
    public GuiConfig(GuiScreen parentScreen, String modID, boolean allRequireWorldRestart, boolean allRequireMcRestart, String title,
            Class<?>... configClasses)
    {
        this(parentScreen, collectConfigElements(configClasses), modID, null, allRequireWorldRestart, allRequireMcRestart, title, null);
    }
    
    private static List<IConfigElement> collectConfigElements(Class<?>[] configClasses)
    {
        List<IConfigElement> toReturn;
        if(configClasses.length == 1)
        {
            toReturn = ConfigElement.from(configClasses[0]).getChildElements();
        }
        else
        {
            toReturn = new ArrayList<IConfigElement>();
            for(Class<?> clazz : configClasses)
            {
                toReturn.add(ConfigElement.from(clazz));
            }
        }
        toReturn.sort(Comparator.comparing(e -> I18n.format(e.getLanguageKey())));
        return toReturn;
    }

    /**
     * GuiConfig constructor that will use ConfigChangedEvent when editing is concluded. If a non-null value is passed for configID,
     * the OnConfigChanged and PostConfigChanged events will be posted when the Done button is pressed if any configElements were changed
     * (includes child screens). If configID is not defined, the events will be posted if the parent gui is null or if the parent gui
     * is not an instance of GuiConfig.
     *
     * @param parentScreen the parent GuiScreen object
     * @param configElements a List of IConfigElement objects
     * @param modID the mod ID for the mod whose config settings will be edited
     * @param configID an identifier that will be passed to the OnConfigChanged and PostConfigChanged events. Setting this value will force
     *            the save action to be called when the Done button is pressed on this screen if any configElements were changed.
     * @param allRequireWorldRestart send true if all configElements on this screen require a world restart
     * @param allRequireMcRestart send true if all configElements on this screen require MC to be restarted
     * @param title the desired title for this screen. For consistency it is recommended that you pass the path of the config file being
     *            edited.
     */
    public GuiConfig(GuiScreen parentScreen, List<IConfigElement> configElements, String modID, String configID,
            boolean allRequireWorldRestart, boolean allRequireMcRestart, String title)
    {
        this(parentScreen, configElements, modID, configID, allRequireWorldRestart, allRequireMcRestart, title, null);
    }

    /**
     * GuiConfig constructor that will use ConfigChangedEvent when editing is concluded. This constructor passes null for configID.
     * If configID is not defined, the events will be posted if the parent gui is null or if the parent gui is not an instance of GuiConfig.
     *
     * @param parentScreen the parent GuiScreen object
     * @param configElements a List of IConfigElement objects
     * @param modID the mod ID for the mod whose config settings will be edited
     * @param allRequireWorldRestart send true if all configElements on this screen require a world restart
     * @param allRequireMcRestart send true if all configElements on this screen require MC to be restarted
     * @param title the desired title for this screen. For consistency it is recommended that you pass the path of the config file being
     *            edited.
     */
    public GuiConfig(GuiScreen parentScreen, List<IConfigElement> configElements, String modID,
            boolean allRequireWorldRestart, boolean allRequireMcRestart, String title)
    {
        this(parentScreen, configElements, modID, null, allRequireWorldRestart, allRequireMcRestart, title, null);
    }

    /**
     * GuiConfig constructor that will use ConfigChangedEvent when editing is concluded. This constructor passes null for configID.
     * If configID is not defined, the events will be posted if the parent gui is null or if the parent gui is not an instance of GuiConfig.
     *
     * @param parentScreen the parent GuiScreen object
     * @param configElements a List of IConfigElement objects
     * @param modID the mod ID for the mod whose config settings will be edited
     * @param allRequireWorldRestart send true if all configElements on this screen require a world restart
     * @param allRequireMcRestart send true if all configElements on this screen require MC to be restarted
     * @param title the desired title for this screen. For consistency it is recommended that you pass the path of the config file being
     *            edited.
     * @param titleLine2 the desired title second line for this screen. Typically this is used to send the category name of the category
     *            currently being edited.
     */
    public GuiConfig(GuiScreen parentScreen, List<IConfigElement> configElements, String modID,
            boolean allRequireWorldRestart, boolean allRequireMcRestart, String title, String titleLine2)
    {
        this(parentScreen, configElements, modID, null, allRequireWorldRestart, allRequireMcRestart, title, titleLine2);
    }

    /**
     * GuiConfig constructor that will use ConfigChangedEvent when editing is concluded. titleLine2 is specified in this constructor.
     * If a non-null value is passed for configID, the OnConfigChanged and PostConfigChanged events will be posted when the Done button is
     * pressed if any configElements were changed (includes child screens). If configID is not defined, the events will be posted if the parent
     * gui is null or if the parent gui is not an instance of GuiConfig.
     *
     * @param parentScreen the parent GuiScreen object
     * @param configElements a List of IConfigElement objects
     * @param modID the mod ID for the mod whose config settings will be edited
     * @param configID an identifier that will be passed to the OnConfigChanged and PostConfigChanged events
     * @param allRequireWorldRestart send true if all configElements on this screen require a world restart
     * @param allRequireMcRestart send true if all configElements on this screen require MC to be restarted
     * @param title the desired title for this screen. For consistency it is recommended that you pass the path of the config file being
     *            edited.
     * @param titleLine2 the desired title second line for this screen. Typically this is used to send the category name of the category
     *            currently being edited.
     */
    public GuiConfig(GuiScreen parentScreen, List<IConfigElement> configElements, String modID, @Nullable String configID,
            boolean allRequireWorldRestart, boolean allRequireMcRestart, String title, @Nullable String titleLine2)
    {
        this.mc = Minecraft.getMinecraft();
        this.parentScreen = parentScreen;
        this.configElements = configElements;
        this.entryList = new GuiConfigEntries(this, mc);
        this.initEntries = new ArrayList<IConfigEntry>(entryList.listEntries);
        this.allRequireWorldRestart = allRequireWorldRestart;
        IF:if (!allRequireWorldRestart)
        {
            for (IConfigElement element : configElements)
            {
                if (!element.requiresWorldRestart());
                    break IF;
            }
            allRequireWorldRestart = true;
        }
        this.allRequireMcRestart = allRequireMcRestart;
        IF:if (!allRequireMcRestart)
        {
            for (IConfigElement element : configElements)
            {
                if (!element.requiresMcRestart());
                    break IF;
            }
            allRequireMcRestart = true;
        }
        this.modID = modID;
        this.configID = configID;
        this.isWorldRunning = mc.world != null;
        if (title != null)
            this.title = title;
        this.titleLine2 = titleLine2;
        if (this.titleLine2 != null && this.titleLine2.startsWith(" > "))
            this.titleLine2 = this.titleLine2.replaceFirst(" > ", "");
    }

    public static String getAbridgedConfigPath(String path)
    {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.mcDataDir.getAbsolutePath().endsWith("."))
            return path.replace("\\", "/").replace(mc.mcDataDir.getAbsolutePath().replace("\\", "/").substring(0, mc.mcDataDir.getAbsolutePath().length() - 1), "/.minecraft/");
        else
            return path.replace("\\", "/").replace(mc.mcDataDir.getAbsolutePath().replace("\\", "/"), "/.minecraft");
    }

    @Override
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);

        if (this.entryList == null || this.needsRefresh)
        {
            this.entryList = new GuiConfigEntries(this, mc);
            this.needsRefresh = false;
        }

        int undoGlyphWidth = mc.fontRenderer.getStringWidth(UNDO_CHAR) * 2;
        int resetGlyphWidth = mc.fontRenderer.getStringWidth(RESET_CHAR) * 2;
        int doneWidth = Math.max(mc.fontRenderer.getStringWidth(I18n.format("gui.done")) + 20, 100);
        int undoWidth = mc.fontRenderer.getStringWidth(" " + I18n.format("fml.configgui.tooltip.undoChanges")) + undoGlyphWidth + 20;
        int resetWidth = mc.fontRenderer.getStringWidth(" " + I18n.format("fml.configgui.tooltip.resetToDefault")) + resetGlyphWidth + 20;
        int checkWidth = mc.fontRenderer.getStringWidth(I18n.format("fml.configgui.applyGlobally")) + 13;
        int buttonWidthHalf = (doneWidth + 5 + undoWidth + 5 + resetWidth + 5 + checkWidth) / 2;
        this.buttonList.add(new GuiButtonExt(2000, this.width / 2 - buttonWidthHalf, this.height - 29, doneWidth, 20, I18n.format("gui.done")));
        this.buttonList.add(this.btnDefaultAll = new GuiUnicodeGlyphButton(2001, this.width / 2 - buttonWidthHalf + doneWidth + 5 + undoWidth + 5,
                this.height - 29, resetWidth, 20, " " + I18n.format("fml.configgui.tooltip.resetToDefault"), RESET_CHAR, 2.0F));
        this.buttonList.add(btnUndoAll = new GuiUnicodeGlyphButton(2002, this.width / 2 - buttonWidthHalf + doneWidth + 5,
                this.height - 29, undoWidth, 20, " " + I18n.format("fml.configgui.tooltip.undoChanges"), UNDO_CHAR, 2.0F));
        this.buttonList.add(chkApplyGlobally = new GuiCheckBox(2003, this.width / 2 - buttonWidthHalf + doneWidth + 5 + undoWidth + 5 + resetWidth + 5,
                this.height - 24, I18n.format("fml.configgui.applyGlobally"), false));

        this.undoHoverChecker = new HoverChecker(this.btnUndoAll, 800);
        this.resetHoverChecker = new HoverChecker(this.btnDefaultAll, 800);
        this.checkBoxHoverChecker = new HoverChecker(chkApplyGlobally, 800);
        this.entryList.initGui();
    }

    @Override
    public void onGuiClosed()
    {
        this.entryList.onGuiClosed();

        if (this.configID != null && this.parentScreen instanceof GuiConfig)
        {
            GuiConfig parentGuiConfig = (GuiConfig) this.parentScreen;
            parentGuiConfig.needsRefresh = true;
            parentGuiConfig.initGui();
        }

        if (!(this.parentScreen instanceof GuiConfig))
            Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.id == 2000)
        {
            boolean flag = true;
            try
            {
                if ((configID != null || this.parentScreen == null || !(this.parentScreen instanceof GuiConfig))
                        && (this.entryList.hasChangedEntry(true)))
                {
                    boolean requiresMcRestart = this.entryList.saveConfigElements();

                    if (Loader.isModLoaded(modID))
                    {
                        ConfigChangedEvent event = new OnConfigChangedEvent(modID, configID, isWorldRunning, requiresMcRestart);
                        MinecraftForge.EVENT_BUS.post(event);
                        if (!event.getResult().equals(Result.DENY))
                            MinecraftForge.EVENT_BUS.post(new PostConfigChangedEvent(modID, configID, isWorldRunning, requiresMcRestart));

                        if (requiresMcRestart)
                        {
                            flag = false;
                            mc.displayGuiScreen(new GuiMessageDialog(parentScreen, "fml.configgui.gameRestartTitle",
                                    new TextComponentString(I18n.format("fml.configgui.gameRestartRequired")), "fml.configgui.confirmRestartMessage"));
                        }

                        if (this.parentScreen instanceof GuiConfig)
                            ((GuiConfig) this.parentScreen).needsRefresh = true;
                    }
                }
            }
            catch (Throwable e)
            {
                FMLLog.log.error("Error performing GuiConfig action:", e);
            }

            if (flag)
                this.mc.displayGuiScreen(this.parentScreen);
        }
        else if (button.id == 2001)
        {
            this.entryList.setAllToDefault(this.chkApplyGlobally.isChecked());
        }
        else if (button.id == 2002)
        {
            this.entryList.undoAllChanges(this.chkApplyGlobally.isChecked());
        }
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        this.entryList.handleMouseInput();
    }

    @Override
    protected void mouseClicked(int x, int y, int mouseEvent) throws IOException
    {
        if (mouseEvent != 0 || !this.entryList.mouseClicked(x, y, mouseEvent))
        {
            this.entryList.mouseClickedPassThru(x, y, mouseEvent);
            super.mouseClicked(x, y, mouseEvent);
        }
    }

    @Override
    protected void mouseReleased(int x, int y, int mouseEvent)
    {
        if (mouseEvent != 0 || !this.entryList.mouseReleased(x, y, mouseEvent))
        {
            super.mouseReleased(x, y, mouseEvent);
        }
    }

    @Override
    protected void keyTyped(char eventChar, int eventKey)
    {
        if (eventKey == Keyboard.KEY_ESCAPE)
            this.mc.displayGuiScreen(parentScreen);
        else
            this.entryList.keyTyped(eventChar, eventKey);
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        this.entryList.updateScreen();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.entryList.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 8, 16777215);
        String title2 = this.titleLine2;

        if (title2 != null)
        {
            int strWidth = mc.fontRenderer.getStringWidth(title2);
            int ellipsisWidth = mc.fontRenderer.getStringWidth("...");
            if (strWidth > width - 6 && strWidth > ellipsisWidth)
                title2 = mc.fontRenderer.trimStringToWidth(title2, width - 6 - ellipsisWidth).trim() + "...";
            this.drawCenteredString(this.fontRenderer, title2, this.width / 2, 18, 16777215);
        }

        this.btnUndoAll.enabled = this.entryList.areAnyEntriesEnabled(this.chkApplyGlobally.isChecked()) && this.entryList.hasChangedEntry(this.chkApplyGlobally.isChecked());
        this.btnDefaultAll.enabled = this.entryList.areAnyEntriesEnabled(this.chkApplyGlobally.isChecked()) && !this.entryList.areAllEntriesDefault(this.chkApplyGlobally.isChecked());
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.entryList.drawScreenPost(mouseX, mouseY, partialTicks);
        if (this.undoHoverChecker.checkHover(mouseX, mouseY))
            this.drawToolTip(Arrays.asList(I18n.format("fml.configgui.tooltip.undoAll").split("\n")), mouseX, mouseY);
        if (this.resetHoverChecker.checkHover(mouseX, mouseY))
            this.drawToolTip(Arrays.asList(I18n.format("fml.configgui.tooltip.resetAll").split("\n")), mouseX, mouseY);
        if (this.checkBoxHoverChecker.checkHover(mouseX, mouseY))
            this.drawToolTip(Arrays.asList(I18n.format("fml.configgui.tooltip.applyGlobally").split("\n")), mouseX, mouseY);
    }

    public void drawToolTip(List<String> stringList, int x, int y)
    {
        GuiUtils.drawHoveringText(stringList, x, y, width, height, 300, fontRenderer);
    }
}
