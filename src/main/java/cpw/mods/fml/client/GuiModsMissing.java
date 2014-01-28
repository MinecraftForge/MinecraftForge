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

import net.minecraft.client.gui.GuiErrorScreen;
import cpw.mods.fml.common.MissingModsException;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;

public class GuiModsMissing extends GuiErrorScreen
{

    private MissingModsException modsMissing;

    public GuiModsMissing(MissingModsException modsMissing)
    {
        super(null,null);
        this.modsMissing = modsMissing;
    }

    // JAVADOC METHOD $$ func_73866_w_
    @Override
    public void initGui()
    {
        super.initGui();
    }
    // JAVADOC METHOD $$ func_73863_a
    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        this.func_146276_q_();
        int offset = Math.max(85 - modsMissing.missingMods.size() * 10, 10);
        this.drawCenteredString(this.field_146289_q, "Forge Mod Loader has found a problem with your minecraft installation", this.field_146294_l / 2, offset, 0xFFFFFF);
        offset+=10;
        this.drawCenteredString(this.field_146289_q, "The mods and versions listed below could not be found", this.field_146294_l / 2, offset, 0xFFFFFF);
        offset+=5;
        for (ArtifactVersion v : modsMissing.missingMods)
        {
            offset+=10;
            if (v instanceof DefaultArtifactVersion)
            {
                DefaultArtifactVersion dav =  (DefaultArtifactVersion)v;
                if (dav.getRange() != null && dav.getRange().isUnboundedAbove())
                {
                    this.drawCenteredString(this.field_146289_q, String.format("%s : minimum version required is %s", v.getLabel(), dav.getRange().getLowerBoundString()), this.field_146294_l / 2, offset, 0xEEEEEE);
                    continue;
                }
            }
            this.drawCenteredString(this.field_146289_q, String.format("%s : %s", v.getLabel(), v.getRangeString()), this.field_146294_l / 2, offset, 0xEEEEEE);
        }
        offset+=20;
        this.drawCenteredString(this.field_146289_q, "The file 'ForgeModLoader-client-0.log' contains more information", this.field_146294_l / 2, offset, 0xFFFFFF);
    }
}