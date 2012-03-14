package net.minecraft.src;

public class GuiTextField extends Gui
{
    /**
     * Have the font renderer from GuiScreen to render the textbox text into the screen.
     */
    private final FontRenderer fontRenderer;
    private final int xPos;
    private final int yPos;
    private final int width;
    private final int height;

    /** Have the current text beign edited on the textbox. */
    private String text;
    private int maxStringLength;
    private int cursorCounter;

    /**
     * If this value is true along isEnabled, keyTyped will process the keys.
     */
    public boolean isFocused = false;

    /**
     * If this value is true along isFocused, keyTyped will process the keys.
     */
    public boolean isEnabled = true;

    /**
     * Holds the GuiScreen that the textfield is attached, used for tab purposes.
     */
    private GuiScreen parentGuiScreen;

    public GuiTextField(GuiScreen par1GuiScreen, FontRenderer par2FontRenderer, int par3, int par4, int par5, int par6, String par7Str)
    {
        this.parentGuiScreen = par1GuiScreen;
        this.fontRenderer = par2FontRenderer;
        this.xPos = par3;
        this.yPos = par4;
        this.width = par5;
        this.height = par6;
        this.setText(par7Str);
    }

    /**
     * Sets the text of the textbox.
     */
    public void setText(String par1Str)
    {
        this.text = par1Str;
    }

    /**
     * Returns the text beign edited on the textbox.
     */
    public String getText()
    {
        return this.text;
    }

    /**
     * Increments the cursor counter
     */
    public void updateCursorCounter()
    {
        ++this.cursorCounter;
    }

    /**
     * Call this method from you GuiScreen to process the keys into textbox.
     */
    public void textboxKeyTyped(char par1, int par2)
    {
        if (this.isEnabled && this.isFocused)
        {
            if (par1 == 9)
            {
                this.parentGuiScreen.selectNextField();
            }

            if (par1 == 22)
            {
                String var3 = GuiScreen.getClipboardString();

                if (var3 == null)
                {
                    var3 = "";
                }

                int var4 = 32 - this.text.length();

                if (var4 > var3.length())
                {
                    var4 = var3.length();
                }

                if (var4 > 0)
                {
                    this.text = this.text + var3.substring(0, var4);
                }
            }

            if (par2 == 14 && this.text.length() > 0)
            {
                this.text = this.text.substring(0, this.text.length() - 1);
            }

            if (ChatAllowedCharacters.func_48614_a(par1) && (this.text.length() < this.maxStringLength || this.maxStringLength == 0))
            {
                this.text = this.text + par1;
            }
        }
    }

    /**
     * Args: x, y, buttonClicked
     */
    public void mouseClicked(int par1, int par2, int par3)
    {
        boolean var4 = this.isEnabled && par1 >= this.xPos && par1 < this.xPos + this.width && par2 >= this.yPos && par2 < this.yPos + this.height;
        this.setFocused(var4);
    }

    public void setFocused(boolean par1)
    {
        if (par1 && !this.isFocused)
        {
            this.cursorCounter = 0;
        }

        this.isFocused = par1;
    }

    /**
     * Draws the textbox
     */
    public void drawTextBox()
    {
        this.drawRect(this.xPos - 1, this.yPos - 1, this.xPos + this.width + 1, this.yPos + this.height + 1, -6250336);
        this.drawRect(this.xPos, this.yPos, this.xPos + this.width, this.yPos + this.height, -16777216);

        if (this.isEnabled)
        {
            boolean var1 = this.isFocused && this.cursorCounter / 6 % 2 == 0;
            this.drawString(this.fontRenderer, this.text + (var1 ? "_" : ""), this.xPos + 4, this.yPos + (this.height - 8) / 2, 14737632);
        }
        else
        {
            this.drawString(this.fontRenderer, this.text, this.xPos + 4, this.yPos + (this.height - 8) / 2, 7368816);
        }
    }

    public void setMaxStringLength(int par1)
    {
        this.maxStringLength = par1;
    }
}
