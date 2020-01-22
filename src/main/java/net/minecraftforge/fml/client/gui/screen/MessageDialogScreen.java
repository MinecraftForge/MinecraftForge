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

package net.minecraftforge.fml.client.gui.screen;

import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Copy of {@link DisconnectedScreen} but with custom button text.
 */
public class MessageDialogScreen extends Screen {

	/**
	 * The screen to navigate to once the button is clicked.
	 */
	protected final Screen nextScreen;
	/**
	 * The long message to display.
	 */
	protected final ITextComponent message;
	/**
	 * The text of the button.
	 */
	protected final String buttonText;
	/**
	 * The message split into multiple lines, calculated in {@link #init()}
	 */
	protected List<String> multilineMessage;
	/**
	 * The height of the {@link #multilineMessage}, calculated in {@link #init()}
	 */
	protected int messageHeight;

	public MessageDialogScreen(@Nullable final Screen nextScreen, final ITextComponent title, final ITextComponent message, final ITextComponent buttonMessage) {
		super(title);
		this.nextScreen = nextScreen;
		this.message = message;
		this.buttonText = buttonMessage.getFormattedText();
	}

	public MessageDialogScreen(@Nullable final Screen nextScreen, final String rawTitle, final String rawMessage, final String rawButtonMessage) {
		super(new StringTextComponent(rawTitle));
		this.nextScreen = nextScreen;
		this.message = new StringTextComponent(rawMessage);
		this.buttonText = rawButtonMessage;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.renderBackground();

		final int halfWidth = this.width / 2;
		final int startYPos = this.height / 2 - this.messageHeight / 2;

		this.drawCenteredString(this.font, this.title.getFormattedText(), halfWidth, startYPos - this.font.FONT_HEIGHT * 2, 0xAA_AA_AA);

		int yPos = startYPos;
		if (this.multilineMessage != null) {
			for (String s : this.multilineMessage) {
				this.drawCenteredString(this.font, s, halfWidth, yPos, 0xFF_FF_FF);
				yPos += this.font.FONT_HEIGHT;
			}
		}

		super.render(mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	protected void init() {
		this.multilineMessage = this.font.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
		this.messageHeight = this.multilineMessage.size() * this.font.FONT_HEIGHT;
		this.addButton(new ExtendedButton(this.width / 2 - 100, Math.min(this.height / 2 + this.messageHeight / 2 + 9, this.height - 30), 200, 20, buttonText, button -> this.minecraft.displayGuiScreen(this.nextScreen)));
	}

}
