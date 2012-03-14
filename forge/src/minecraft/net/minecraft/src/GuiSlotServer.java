package net.minecraft.src;

import org.lwjgl.opengl.GL11;

class GuiSlotServer extends GuiSlot
{
    final GuiMultiplayer field_35410_a;

    public GuiSlotServer(GuiMultiplayer par1GuiMultiplayer)
    {
        super(par1GuiMultiplayer.mc, par1GuiMultiplayer.width, par1GuiMultiplayer.height, 32, par1GuiMultiplayer.height - 64, 36);
        this.field_35410_a = par1GuiMultiplayer;
    }

    /**
     * Gets the size of the current slot list.
     */
    protected int getSize()
    {
        return GuiMultiplayer.getServerList(this.field_35410_a).size();
    }

    /**
     * the element in the slot that was clicked, boolean for wether it was double clicked or not
     */
    protected void elementClicked(int par1, boolean par2)
    {
        GuiMultiplayer.setSelectedServer(this.field_35410_a, par1);
        boolean var3 = GuiMultiplayer.getSelectedServer(this.field_35410_a) >= 0 && GuiMultiplayer.getSelectedServer(this.field_35410_a) < this.getSize();
        GuiMultiplayer.getButtonSelect(this.field_35410_a).enabled = var3;
        GuiMultiplayer.getButtonEdit(this.field_35410_a).enabled = var3;
        GuiMultiplayer.getButtonDelete(this.field_35410_a).enabled = var3;

        if (par2 && var3)
        {
            GuiMultiplayer.joinServer(this.field_35410_a, par1);
        }
    }

    /**
     * returns true if the element passed in is currently selected
     */
    protected boolean isSelected(int par1)
    {
        return par1 == GuiMultiplayer.getSelectedServer(this.field_35410_a);
    }

    /**
     * return the height of the content being scrolled
     */
    protected int getContentHeight()
    {
        return GuiMultiplayer.getServerList(this.field_35410_a).size() * 36;
    }

    protected void drawBackground()
    {
        this.field_35410_a.drawDefaultBackground();
    }

    protected void drawSlot(int par1, int par2, int par3, int par4, Tessellator par5Tessellator)
    {
        ServerNBTStorage var6 = (ServerNBTStorage)GuiMultiplayer.getServerList(this.field_35410_a).get(par1);

        synchronized (GuiMultiplayer.getLock())
        {
            if (GuiMultiplayer.getThreadsPending() < 5 && !var6.polled)
            {
                var6.polled = true;
                var6.lag = -2L;
                var6.motd = "";
                var6.playerCount = "";
                GuiMultiplayer.incrementThreadsPending();
                (new ThreadPollServers(this, var6)).start();
            }
        }

        this.field_35410_a.drawString(this.field_35410_a.fontRenderer, var6.name, par2 + 2, par3 + 1, 16777215);
        this.field_35410_a.drawString(this.field_35410_a.fontRenderer, var6.motd, par2 + 2, par3 + 12, 8421504);
        this.field_35410_a.drawString(this.field_35410_a.fontRenderer, var6.playerCount, par2 + 215 - this.field_35410_a.fontRenderer.getStringWidth(var6.playerCount), par3 + 12, 8421504);
        this.field_35410_a.drawString(this.field_35410_a.fontRenderer, var6.host, par2 + 2, par3 + 12 + 11, 3158064);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.field_35410_a.mc.renderEngine.bindTexture(this.field_35410_a.mc.renderEngine.getTexture("/gui/icons.png"));
        boolean var7 = false;
        boolean var8 = false;
        String var9 = "";
        byte var12;
        int var13;

        if (var6.polled && var6.lag != -2L)
        {
            var12 = 0;
            var8 = false;

            if (var6.lag < 0L)
            {
                var13 = 5;
            }
            else if (var6.lag < 150L)
            {
                var13 = 0;
            }
            else if (var6.lag < 300L)
            {
                var13 = 1;
            }
            else if (var6.lag < 600L)
            {
                var13 = 2;
            }
            else if (var6.lag < 1000L)
            {
                var13 = 3;
            }
            else
            {
                var13 = 4;
            }

            if (var6.lag < 0L)
            {
                var9 = "(no connection)";
            }
            else
            {
                var9 = var6.lag + "ms";
            }
        }
        else
        {
            var12 = 1;
            var13 = (int)(System.currentTimeMillis() / 100L + (long)(par1 * 2) & 7L);

            if (var13 > 4)
            {
                var13 = 8 - var13;
            }

            var9 = "Polling..";
        }

        this.field_35410_a.drawTexturedModalRect(par2 + 205, par3, 0 + var12 * 10, 176 + var13 * 8, 10, 8);
        byte var10 = 4;

        if (this.field_35409_k >= par2 + 205 - var10 && this.field_35408_l >= par3 - var10 && this.field_35409_k <= par2 + 205 + 10 + var10 && this.field_35408_l <= par3 + 8 + var10)
        {
            GuiMultiplayer.func_35327_a(this.field_35410_a, var9);
        }
    }
}
