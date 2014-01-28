/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package cpw.mods.fml.client;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import cpw.mods.fml.common.MissingModsException;
import cpw.mods.fml.common.versioning.ArtifactVersion;

public class GuiModsMissingForServer extends GuiScreen
{
    private MissingModsException modsMissing;

    public GuiModsMissingForServer(MissingModsException modsMissing)
    {
        this.modsMissing = modsMissing;
    }

    // JAVADOC METHOD $$ func_73866_w_
    @SuppressWarnings("unchecked")
    @Override
    public void initGui()
    {
        this.field_146292_n.add(new GuiButton(1, this.field_146294_l / 2 - 75, this.field_146295_m - 38, I18n.getStringParams("gui.done")));
    }

    @Override
    protected void func_146284_a(GuiButton par1GuiButton)
    {
        if (par1GuiButton.field_146124_l && par1GuiButton.field_146127_k == 1)
        {
            FMLClientHandler.instance().showGuiScreen(null);
        }
    }
    // JAVADOC METHOD $$ func_73863_a
    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        this.func_146276_q_();
        int offset = Math.max(85 - modsMissing.missingMods.size() * 10, 10);
        this.drawCenteredString(this.field_146289_q, "Forge Mod Loader could not connect to this server", this.field_146294_l / 2, offset, 0xFFFFFF);
        offset += 10;
        this.drawCenteredString(this.field_146289_q, "The mods and versions listed below could not be found", this.field_146294_l / 2, offset, 0xFFFFFF);
        offset += 10;
        this.drawCenteredString(this.field_146289_q, "They are required to play on this server", this.field_146294_l / 2, offset, 0xFFFFFF);
        offset += 5;
        for (ArtifactVersion v : modsMissing.missingMods)
        {
            offset += 10;
            this.drawCenteredString(this.field_146289_q, String.format("%s : %s", v.getLabel(), v.getRangeString()), this.field_146294_l / 2, offset, 0xEEEEEE);
        }
        super.drawScreen(par1, par2, par3);
    }
}