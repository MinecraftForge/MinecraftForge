package net.minecraft.src;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.text.Bidi;
import java.util.Random;
import javax.imageio.ImageIO;
import org.lwjgl.opengl.GL11;

public class FontRenderer
{
    /** Array of width of all the characters in default.png */
    private int[] charWidth = new int[256];
    public int fontTextureName = 0;

    /** the height in pixels of default text */
    public int FONT_HEIGHT = 8;
    public Random fontRandom = new Random();

    /**
     * Array of the start/end column (in upper/lower nibble) for every glyph in the /font directory.
     */
    private byte[] glyphWidth = new byte[65536];

    /**
     * Array of GL texture ids for loaded glyph_XX.png images. Indexed by Unicode block (group of 256 chars).
     */
    private final int[] glyphTextureName = new int[256];

    /**
     * Array of RGB triplets defining the 16 standard chat colors followed by 16 darker version of the same colors for
     * drop shadows.
     */
    private int[] colorCode = new int[32];

    /**
     * The currently bound GL texture ID. Avoids unnecessary glBindTexture() for the same texture if it's already bound.
     */
    private int boundTextureName;

    /** The RenderEngine used to load and setup glyph textures. */
    private final RenderEngine renderEngine;

    /** Current X coordinate at which to draw the next character. */
    private float posX;

    /** Current Y coordinate at which to draw the next character. */
    private float posY;

    /**
     * If true, strings should be rendered with Unicode fonts instead of the default.png font
     */
    private boolean unicodeFlag;

    /**
     * If true, the Unicode Bidirectional Algorithm should be run before rendering any string.
     */
    private boolean bidiFlag;

    public FontRenderer(GameSettings par1GameSettings, String par2Str, RenderEngine par3RenderEngine, boolean par4)
    {
        this.renderEngine = par3RenderEngine;
        this.unicodeFlag = par4;
        BufferedImage var5;

        try
        {
            var5 = ImageIO.read(RenderEngine.class.getResourceAsStream(par2Str));
            InputStream var6 = RenderEngine.class.getResourceAsStream("/font/glyph_sizes.bin");
            var6.read(this.glyphWidth);
        }
        catch (IOException var18)
        {
            throw new RuntimeException(var18);
        }

        int var19 = var5.getWidth();
        int var7 = var5.getHeight();
        int[] var8 = new int[var19 * var7];
        var5.getRGB(0, 0, var19, var7, var8, 0, var19);
        int var9 = 0;
        int var10;
        int var11;
        int var12;
        int var13;
        int var15;
        int var16;

        while (var9 < 256)
        {
            var10 = var9 % 16;
            var11 = var9 / 16;
            var12 = 7;

            while (true)
            {
                if (var12 >= 0)
                {
                    var13 = var10 * 8 + var12;
                    boolean var14 = true;

                    for (var15 = 0; var15 < 8 && var14; ++var15)
                    {
                        var16 = (var11 * 8 + var15) * var19;
                        int var17 = var8[var13 + var16] & 255;

                        if (var17 > 0)
                        {
                            var14 = false;
                        }
                    }

                    if (var14)
                    {
                        --var12;
                        continue;
                    }
                }

                if (var9 == 32)
                {
                    var12 = 2;
                }

                this.charWidth[var9] = var12 + 2;
                ++var9;
                break;
            }
        }

        this.fontTextureName = par3RenderEngine.allocateAndSetupTexture(var5);

        for (var9 = 0; var9 < 32; ++var9)
        {
            var10 = (var9 >> 3 & 1) * 85;
            var11 = (var9 >> 2 & 1) * 170 + var10;
            var12 = (var9 >> 1 & 1) * 170 + var10;
            var13 = (var9 >> 0 & 1) * 170 + var10;

            if (var9 == 6)
            {
                var11 += 85;
            }

            if (par1GameSettings.anaglyph)
            {
                int var20 = (var11 * 30 + var12 * 59 + var13 * 11) / 100;
                var15 = (var11 * 30 + var12 * 70) / 100;
                var16 = (var11 * 30 + var13 * 70) / 100;
                var11 = var20;
                var12 = var15;
                var13 = var16;
            }

            if (var9 >= 16)
            {
                var11 /= 4;
                var12 /= 4;
                var13 /= 4;
            }

            this.colorCode[var9] = (var11 & 255) << 16 | (var12 & 255) << 8 | var13 & 255;
        }
    }

    /**
     * Render a single character with the default.png font at current (posX,posY) location.
     */
    private void renderDefaultChar(int par1)
    {
        float var2 = (float)(par1 % 16 * 8);
        float var3 = (float)(par1 / 16 * 8);

        if (this.boundTextureName != this.fontTextureName)
        {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.fontTextureName);
            this.boundTextureName = this.fontTextureName;
        }

        float var4 = (float)this.charWidth[par1] - 0.01F;
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
        GL11.glTexCoord2f(var2 / 128.0F, var3 / 128.0F);
        GL11.glVertex3f(this.posX, this.posY, 0.0F);
        GL11.glTexCoord2f(var2 / 128.0F, (var3 + 7.99F) / 128.0F);
        GL11.glVertex3f(this.posX, this.posY + 7.99F, 0.0F);
        GL11.glTexCoord2f((var2 + var4) / 128.0F, var3 / 128.0F);
        GL11.glVertex3f(this.posX + var4, this.posY, 0.0F);
        GL11.glTexCoord2f((var2 + var4) / 128.0F, (var3 + 7.99F) / 128.0F);
        GL11.glVertex3f(this.posX + var4, this.posY + 7.99F, 0.0F);
        GL11.glEnd();
        this.posX += (float)this.charWidth[par1];
    }

    /**
     * Load one of the /font/glyph_XX.png into a new GL texture and store the texture ID in glyphTextureName array.
     */
    private void loadGlyphTexture(int par1)
    {
        String var3 = String.format("/font/glyph_%02X.png", new Object[] {Integer.valueOf(par1)});
        BufferedImage var2;

        try
        {
            var2 = ImageIO.read(RenderEngine.class.getResourceAsStream(var3.toString()));
        }
        catch (IOException var5)
        {
            throw new RuntimeException(var5);
        }

        this.glyphTextureName[par1] = this.renderEngine.allocateAndSetupTexture(var2);
        this.boundTextureName = this.glyphTextureName[par1];
    }

    /**
     * Render a single Unicode character at current (posX,posY) location using one of the /font/glyph_XX.png files.
     */
    private void renderUnicodeChar(char par1)
    {
        if (this.glyphWidth[par1] != 0)
        {
            int var2 = par1 / 256;

            if (this.glyphTextureName[var2] == 0)
            {
                this.loadGlyphTexture(var2);
            }

            if (this.boundTextureName != this.glyphTextureName[var2])
            {
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.glyphTextureName[var2]);
                this.boundTextureName = this.glyphTextureName[var2];
            }

            int var3 = this.glyphWidth[par1] >>> 4;
            int var4 = this.glyphWidth[par1] & 15;
            float var5 = (float)var3;
            float var6 = (float)(var4 + 1);
            float var7 = (float)(par1 % 16 * 16) + var5;
            float var8 = (float)((par1 & 255) / 16 * 16);
            float var9 = var6 - var5 - 0.02F;
            GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
            GL11.glTexCoord2f(var7 / 256.0F, var8 / 256.0F);
            GL11.glVertex3f(this.posX, this.posY, 0.0F);
            GL11.glTexCoord2f(var7 / 256.0F, (var8 + 15.98F) / 256.0F);
            GL11.glVertex3f(this.posX, this.posY + 7.99F, 0.0F);
            GL11.glTexCoord2f((var7 + var9) / 256.0F, var8 / 256.0F);
            GL11.glVertex3f(this.posX + var9 / 2.0F, this.posY, 0.0F);
            GL11.glTexCoord2f((var7 + var9) / 256.0F, (var8 + 15.98F) / 256.0F);
            GL11.glVertex3f(this.posX + var9 / 2.0F, this.posY + 7.99F, 0.0F);
            GL11.glEnd();
            this.posX += (var6 - var5) / 2.0F + 1.0F;
        }
    }

    /**
     * Draws the specified string with a drop shadow.
     */
    public void drawStringWithShadow(String par1Str, int par2, int par3, int par4)
    {
        if (this.bidiFlag)
        {
            par1Str = this.bidiReorder(par1Str);
        }

        this.renderString(par1Str, par2 + 1, par3 + 1, par4, true);
        this.renderString(par1Str, par2, par3, par4, false);
    }

    /**
     * Draws the specified string.
     */
    public void drawString(String par1Str, int par2, int par3, int par4)
    {
        if (this.bidiFlag)
        {
            par1Str = this.bidiReorder(par1Str);
        }

        this.renderString(par1Str, par2, par3, par4, false);
    }

    /**
     * Apply Unicode Bidirectional Algorithm to string and return a new possibly reordered string for visual rendering.
     */
    private String bidiReorder(String par1Str)
    {
        if (par1Str != null && Bidi.requiresBidi(par1Str.toCharArray(), 0, par1Str.length()))
        {
            Bidi var2 = new Bidi(par1Str, -2);
            byte[] var3 = new byte[var2.getRunCount()];
            String[] var4 = new String[var3.length];
            int var7;

            for (int var5 = 0; var5 < var3.length; ++var5)
            {
                int var6 = var2.getRunStart(var5);
                var7 = var2.getRunLimit(var5);
                int var8 = var2.getRunLevel(var5);
                String var9 = par1Str.substring(var6, var7);
                var3[var5] = (byte)var8;
                var4[var5] = var9;
            }

            String[] var11 = (String[])var4.clone();
            Bidi.reorderVisually(var3, 0, var4, 0, var3.length);
            StringBuilder var12 = new StringBuilder();
            var7 = 0;

            while (var7 < var4.length)
            {
                byte var13 = var3[var7];
                int var14 = 0;

                while (true)
                {
                    if (var14 < var11.length)
                    {
                        if (!var11[var14].equals(var4[var7]))
                        {
                            ++var14;
                            continue;
                        }

                        var13 = var3[var14];
                    }

                    if ((var13 & 1) == 0)
                    {
                        var12.append(var4[var7]);
                    }
                    else
                    {
                        for (var14 = var4[var7].length() - 1; var14 >= 0; --var14)
                        {
                            char var10 = var4[var7].charAt(var14);

                            if (var10 == 40)
                            {
                                var10 = 41;
                            }
                            else if (var10 == 41)
                            {
                                var10 = 40;
                            }

                            var12.append(var10);
                        }
                    }

                    ++var7;
                    break;
                }
            }

            return var12.toString();
        }
        else
        {
            return par1Str;
        }
    }

    /**
     * Render a single line string at the current (posX,posY) and update posX
     */
    private void renderStringAtPos(String par1Str, boolean par2)
    {
        boolean var3 = false;

        for (int var4 = 0; var4 < par1Str.length(); ++var4)
        {
            char var5 = par1Str.charAt(var4);
            int var6;
            int var7;

            if (var5 == 167 && var4 + 1 < par1Str.length())
            {
                var6 = "0123456789abcdefk".indexOf(par1Str.toLowerCase().charAt(var4 + 1));

                if (var6 == 16)
                {
                    var3 = true;
                }
                else
                {
                    var3 = false;

                    if (var6 < 0 || var6 > 15)
                    {
                        var6 = 15;
                    }

                    if (par2)
                    {
                        var6 += 16;
                    }

                    var7 = this.colorCode[var6];
                    GL11.glColor3f((float)(var7 >> 16) / 255.0F, (float)(var7 >> 8 & 255) / 255.0F, (float)(var7 & 255) / 255.0F);
                }

                ++var4;
            }
            else
            {
                var6 = ChatAllowedCharacters.allowedCharacters.indexOf(var5);

                if (var3 && var6 > 0)
                {
                    do
                    {
                        var7 = this.fontRandom.nextInt(ChatAllowedCharacters.allowedCharacters.length());
                    }
                    while (this.charWidth[var6 + 32] != this.charWidth[var7 + 32]);

                    var6 = var7;
                }

                if (var5 == 32)
                {
                    this.posX += 4.0F;
                }
                else if (var6 > 0 && !this.unicodeFlag)
                {
                    this.renderDefaultChar(var6 + 32);
                }
                else
                {
                    this.renderUnicodeChar(var5);
                }
            }
        }
    }

    /**
     * The actual rendering takes place here.
     */
    private void renderString(String par1Str, int par2, int par3, int par4, boolean par5)
    {
        if (par1Str != null)
        {
            this.boundTextureName = 0;

            if ((par4 & -67108864) == 0)
            {
                par4 |= -16777216;
            }

            if (par5)
            {
                par4 = (par4 & 16579836) >> 2 | par4 & -16777216;
            }

            GL11.glColor4f((float)(par4 >> 16 & 255) / 255.0F, (float)(par4 >> 8 & 255) / 255.0F, (float)(par4 & 255) / 255.0F, (float)(par4 >> 24 & 255) / 255.0F);
            this.posX = (float)par2;
            this.posY = (float)par3;
            this.renderStringAtPos(par1Str, par5);
        }
    }

    /**
     * Returns the width of this string. Equivalent of FontMetrics.stringWidth(String s).
     */
    public int getStringWidth(String par1Str)
    {
        if (par1Str == null)
        {
            return 0;
        }
        else
        {
            int var2 = 0;

            for (int var3 = 0; var3 < par1Str.length(); ++var3)
            {
                char var4 = par1Str.charAt(var3);

                if (var4 == 167)
                {
                    ++var3;
                }
                else
                {
                    int var5 = ChatAllowedCharacters.allowedCharacters.indexOf(var4);

                    if (var5 >= 0 && !this.unicodeFlag)
                    {
                        var2 += this.charWidth[var5 + 32];
                    }
                    else if (this.glyphWidth[var4] != 0)
                    {
                        int var6 = this.glyphWidth[var4] >> 4;
                        int var7 = this.glyphWidth[var4] & 15;

                        if (var7 > 7)
                        {
                            var7 = 15;
                            var6 = 0;
                        }

                        ++var7;
                        var2 += (var7 - var6) / 2 + 1;
                    }
                }
            }

            return var2;
        }
    }

    /**
     * Splits and draws a String with wordwrap (maximum length is parameter k)
     */
    public void drawSplitString(String par1Str, int par2, int par3, int par4, int par5)
    {
        if (this.bidiFlag)
        {
            par1Str = this.bidiReorder(par1Str);
        }

        this.renderSplitStringNoShadow(par1Str, par2, par3, par4, par5);
    }

    /**
     * renders a multi-line string with wordwrap (maximum length is parameter k) by means of renderSplitString
     */
    private void renderSplitStringNoShadow(String par1Str, int par2, int par3, int par4, int par5)
    {
        this.renderSplitString(par1Str, par2, par3, par4, par5, false);
    }

    /**
     * Splits and draws a String with wordwrap (maximum length is parameter k) and with darker drop shadow color if flag
     * is set
     */
    public void drawSplitString(String par1Str, int par2, int par3, int par4, int par5, boolean par6)
    {
        if (this.bidiFlag)
        {
            par1Str = this.bidiReorder(par1Str);
        }

        this.renderSplitString(par1Str, par2, par3, par4, par5, par6);
    }

    /**
     * Perform actual work of rendering a multi-line string with wordwrap (maximum length is parameter k) and with
     * darkre drop shadow color if flag is set
     */
    private void renderSplitString(String par1Str, int par2, int par3, int par4, int par5, boolean par6)
    {
        String[] var7 = par1Str.split("\n");

        if (var7.length > 1)
        {
            for (int var14 = 0; var14 < var7.length; ++var14)
            {
                this.renderSplitStringNoShadow(var7[var14], par2, par3, par4, par5);
                par3 += this.splitStringWidth(var7[var14], par4);
            }
        }
        else
        {
            String[] var8 = par1Str.split(" ");
            int var9 = 0;
            String var10 = "";

            while (var9 < var8.length)
            {
                String var11;

                for (var11 = var10 + var8[var9++] + " "; var9 < var8.length && this.getStringWidth(var11 + var8[var9]) < par4; var11 = var11 + var8[var9++] + " ")
                {
                    ;
                }

                int var12;

                for (; this.getStringWidth(var11) > par4; var11 = var10 + var11.substring(var12))
                {
                    for (var12 = 0; this.getStringWidth(var11.substring(0, var12 + 1)) <= par4; ++var12)
                    {
                        ;
                    }

                    if (var11.substring(0, var12).trim().length() > 0)
                    {
                        String var13 = var11.substring(0, var12);

                        if (var13.lastIndexOf("\u00a7") >= 0)
                        {
                            var10 = "\u00a7" + var13.charAt(var13.lastIndexOf("\u00a7") + 1);
                        }

                        this.renderString(var13, par2, par3, par5, par6);
                        par3 += this.FONT_HEIGHT;
                    }
                }

                if (this.getStringWidth(var11.trim()) > 0)
                {
                    if (var11.lastIndexOf("\u00a7") >= 0)
                    {
                        var10 = "\u00a7" + var11.charAt(var11.lastIndexOf("\u00a7") + 1);
                    }

                    this.renderString(var11, par2, par3, par5, par6);
                    par3 += this.FONT_HEIGHT;
                }
            }
        }
    }

    /**
     * Returns the width of the wordwrapped String (maximum length is parameter k)
     */
    public int splitStringWidth(String par1Str, int par2)
    {
        String[] var3 = par1Str.split("\n");
        int var5;

        if (var3.length > 1)
        {
            int var9 = 0;

            for (var5 = 0; var5 < var3.length; ++var5)
            {
                var9 += this.splitStringWidth(var3[var5], par2);
            }

            return var9;
        }
        else
        {
            String[] var4 = par1Str.split(" ");
            var5 = 0;
            int var6 = 0;

            while (var5 < var4.length)
            {
                String var7;

                for (var7 = var4[var5++] + " "; var5 < var4.length && this.getStringWidth(var7 + var4[var5]) < par2; var7 = var7 + var4[var5++] + " ")
                {
                    ;
                }

                int var8;

                for (; this.getStringWidth(var7) > par2; var7 = var7.substring(var8))
                {
                    for (var8 = 0; this.getStringWidth(var7.substring(0, var8 + 1)) <= par2; ++var8)
                    {
                        ;
                    }

                    if (var7.substring(0, var8).trim().length() > 0)
                    {
                        var6 += this.FONT_HEIGHT;
                    }
                }

                if (var7.trim().length() > 0)
                {
                    var6 += this.FONT_HEIGHT;
                }
            }

            if (var6 < this.FONT_HEIGHT)
            {
                var6 += this.FONT_HEIGHT;
            }

            return var6;
        }
    }

    /**
     * Set unicodeFlag controlling whether strings should be rendered with Unicode fonts instead of the default.png
     * font.
     */
    public void setUnicodeFlag(boolean par1)
    {
        this.unicodeFlag = par1;
    }

    /**
     * Set bidiFlag to control if the Unicode Bidirectional Algorithm should be run before rendering any string.
     */
    public void setBidiFlag(boolean par1)
    {
        this.bidiFlag = par1;
    }
}
