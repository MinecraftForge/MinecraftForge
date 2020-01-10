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

package net.minecraftforge.fml.client.config;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

import static net.minecraftforge.fml.client.config.GuiUtils.RESET_CHAR;
import static net.minecraftforge.fml.client.config.GuiUtils.UNDO_CHAR;

/**
 * This class is the base Screen for all config GUI screens.
 * It can be extended by mods to provide the top-level config screen that will be called when
 * the Config button is clicked from the Mods Menu list.
 *
 * @author bspkrs
 * @author Cadiboo
 */
public class ConfigScreen extends Screen {

	public static final int BUTTON_HEIGHT = 20;
	/**
	 * The empty space above & below the buttons at the bottom of the screen, as well as between each button.
	 */
	public static final int MARGIN = 5;
	/**
	 * The string used for separating categories in the {@link #subtitle}
	 */
	public static final String CATEGORY_DIVIDER = " > ";

	/**
	 * A reference to the screen object that created this. Used for navigating between screens.
	 * If this is an instance of ConfigScreen, the config elements on this screen will NOT be saved when this is closed.
	 */
	public final Screen parentScreen;
	/**
	 * If true then the {@link #entryList} will be re-created next time {@link #init()} is called.
	 */
	public boolean shouldRefreshEntryList = true;
	/**
	 * When clicked undoes all changes that the user made on this screen
	 * (sets all config entries on this screen to the value they had before any changes were made).
	 * If applyToSubcategoriesCheckBox is checked also undoes all changes to child screens.
	 */
	protected GuiButtonExt undoChangesButton;
	/**
	 * When clicked resets all config entries on this screen to their default values.
	 * If applyToSubcategoriesCheckBox is checked also resets all config entries on child screens.
	 */
	protected GuiButtonExt resetToDefaultButton;
	/**
	 * If the effects of clicking the undoChangesButton or the resetToDefaultButton should propagate to child screens.
	 */
	protected CheckboxButton applyToSubcategoriesCheckBox;
	/**
	 * Used to determine if specific tooltips should be rendered.
	 */
	protected HoverChecker undoChangesButtonHoverChecker, resetToDefaultButtonHoverChecker, applyToSubcategoriesCheckBoxHoverChecker;
	private ITextComponent subtitle;
	/**
	 * Displays all our elements in a scrollable list
	 */
	private ConfigEntryListWidget entryList;

	public ConfigScreen(final ITextComponent titleIn, final Screen parentScreen) {
		super(titleIn);
		this.parentScreen = parentScreen;
	}

	@Nullable
	public ConfigEntryListWidget getEntryList() {
		return entryList;
	}

	protected void onDoneButtonClicked(final Button button) {
		this.onClose();
	}

	protected boolean shouldApplyToSubcategories() {
		return this.applyToSubcategoriesCheckBox.func_212942_a();
	}

	protected void onResetToDefaultButtonClicked(final Button button) {
		this.entryList.resetAllToDefault(shouldApplyToSubcategories());
	}

	protected void onUndoChangesButtonClicked(final Button button) {
		this.entryList.undoAllChanges(shouldApplyToSubcategories());
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.renderBackground();
		this.entryList.render(mouseX, mouseY, partialTicks);

		final int halfWidth = this.width / 2;

		this.drawCenteredString(font, this.title.getFormattedText(), halfWidth, 5, 0xFFFFFF);

		if (subtitle != null) {
			final String trimmed = GuiUtils.trimStringToSize(font, subtitle.getFormattedText(), width - 6);
			this.drawCenteredString(font, trimmed, halfWidth, 20, 0x9D9D97);
		}

		super.render(mouseX, mouseY, partialTicks);
		this.entryList.postRender(mouseX, mouseY, partialTicks);

		if (this.undoChangesButtonHoverChecker.checkHover(mouseX, mouseY))
			this.drawToolTip(Arrays.asList(I18n.format("fml.configgui.undoChanges.tooltip").split("\n")), mouseX, mouseY);
		if (this.resetToDefaultButtonHoverChecker.checkHover(mouseX, mouseY))
			this.drawToolTip(Arrays.asList(I18n.format("fml.configgui.resetToDefault.tooltip").split("\n")), mouseX, mouseY);
		if (this.applyToSubcategoriesCheckBoxHoverChecker.checkHover(mouseX, mouseY))
			this.drawToolTip(Arrays.asList(I18n.format("fml.configgui.applyToSubcategories.tooltip").split("\n")), mouseX, mouseY);
	}

	/**
	 * Called when the screen is unloaded.
	 */
	@Override
	public void onClose() {
		this.entryList.onClose();
		getMinecraft().displayGuiScreen(parentScreen);
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 * Called when the GUI is displayed and when the window resizes, the buttonList is cleared beforehand.
	 */
	@Override
	public void init() {
		super.init();

		String doneText = I18n.format("gui.done");
		String undoText = I18n.format("fml.configgui.undoChanges");
		String resetText = I18n.format("fml.configgui.resetToDefault");
		String applyToSubcategoriesText = I18n.format("fml.configgui.applyToSubcategories");

		int undoGlyphWidth = font.getStringWidth(UNDO_CHAR) * 2;
		int resetGlyphWidth = font.getStringWidth(RESET_CHAR) * 2;

		int doneWidth = Math.max(MARGIN + font.getStringWidth(doneText) + MARGIN, 60); // Make the done button slightly bigger than neccesary so it doesn't look too small
		int undoWidth = MARGIN + font.getStringWidth(undoText) + undoGlyphWidth + MARGIN;
		int resetWidth = MARGIN + font.getStringWidth(resetText) + resetGlyphWidth + MARGIN;
		int applyToSubcategoriesWidth = MARGIN + font.getStringWidth(applyToSubcategoriesText) + MARGIN;
		int buttonsWidthHalf = (MARGIN + doneWidth + MARGIN + undoWidth + MARGIN + resetWidth + MARGIN + applyToSubcategoriesWidth + MARGIN) / 2;

		int xPos = this.width / 2 - buttonsWidthHalf;
		int yPos = this.height - MARGIN - BUTTON_HEIGHT;
		this.addButton(new GuiButtonExt(xPos, yPos, doneWidth, BUTTON_HEIGHT, doneText, this::onDoneButtonClicked));

		xPos += doneWidth + MARGIN;
		this.addButton(undoChangesButton = new GuiUnicodeGlyphButton(xPos, yPos, undoWidth, BUTTON_HEIGHT, undoText, UNDO_CHAR, 2.0F, this::onUndoChangesButtonClicked));

		xPos += undoWidth + MARGIN;
		this.addButton(resetToDefaultButton = new GuiUnicodeGlyphButton(xPos, yPos, resetWidth, BUTTON_HEIGHT, resetText, RESET_CHAR, 2.0F, this::onResetToDefaultButtonClicked));

		xPos += resetWidth + MARGIN;
		// Widgets are re-created each time so make the value persist if it exists.
		boolean shouldApplyToSubcategories = applyToSubcategoriesCheckBox != null && shouldApplyToSubcategories();
		this.addButton(applyToSubcategoriesCheckBox = new CheckboxButton(xPos, yPos, applyToSubcategoriesWidth, BUTTON_HEIGHT, applyToSubcategoriesText, shouldApplyToSubcategories));

		this.undoChangesButtonHoverChecker = new HoverChecker(undoChangesButton, 500);
		this.resetToDefaultButtonHoverChecker = new HoverChecker(resetToDefaultButton, 500);
		this.applyToSubcategoriesCheckBoxHoverChecker = new HoverChecker(applyToSubcategoriesCheckBox, 500);

		if (this.shouldRefreshEntryList || this.entryList == null) {
			this.entryList = new ConfigEntryListWidget(this);
			this.shouldRefreshEntryList = false;
		}
		this.children.add(entryList);

		entryList.init();

		this.setButtonsActive();
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	@Override
	public void tick() {
		super.tick();
		if (entryList != null) // Can be called before init on child screens
			this.entryList.tick();
		setButtonsActive();
	}

	public void setButtonsActive() {
		if (entryList == null)
			return;
		final boolean applyToSubcategories = shouldApplyToSubcategories();
		this.undoChangesButton.active = this.entryList.areAnyEntriesChanged(applyToSubcategories);
		this.resetToDefaultButton.active = !this.entryList.areAllEntriesDefault(applyToSubcategories);
	}

	public void drawToolTip(List<String> stringList, int x, int y) {
		GuiUtils.drawHoveringText(stringList, x, y, width, height, 300, font);
	}

	public ITextComponent getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(final ITextComponent subtitle) {
		this.subtitle = subtitle;
	}

	public int getHeaderSize() {
		return getSubtitle() != null ? 33 : 23;
	}

	public int getFooterSize() {
		return MARGIN + BUTTON_HEIGHT + MARGIN;
	}

}
