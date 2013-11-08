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
import net.minecraft.client.gui.GuiSmallButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.StringTranslate;
import cpw.mods.fml.common.network.ModMissingPacket;
import cpw.mods.fml.common.versioning.ArtifactVersion;

public class GuiModsMissingForServer extends GuiScreen
{
    private ModMissingPacket modsMissing;

    public GuiModsMissingForServer(ModMissingPacket modsMissing)
    {
        this.modsMissing = modsMissing;
    }

    @Override
    public void func_73866_w_()
    {
        this.field_73887_h.add(new GuiSmallButton(1, this.field_73880_f / 2 - 75, this.field_73881_g - 38, I18n.func_135053_a("gui.done")));
    }

    @Override
    protected void func_73875_a(GuiButton p_73875_1_)
    {
        if (p_73875_1_.field_73742_g && p_73875_1_.field_73741_f == 1)
        {
            FMLClientHandler.instance().getClient().func_71373_a(null);
        }
    }
    @Override
    public void func_73863_a(int p_73863_1_, int p_73863_2_, float p_73863_3_)
    {
        this.func_73873_v_();
        int offset = Math.max(85 - modsMissing.getModList().size() * 10, 10);
        this.func_73732_a(this.field_73886_k, "Forge Mod Loader could not connect to this server", this.field_73880_f / 2, offset, 0xFFFFFF);
        offset += 10;
        this.func_73732_a(this.field_73886_k, "The mods and versions listed below could not be found", this.field_73880_f / 2, offset, 0xFFFFFF);
        offset += 10;
        this.func_73732_a(this.field_73886_k, "They are required to play on this server", this.field_73880_f / 2, offset, 0xFFFFFF);
        offset += 5;
        for (ArtifactVersion v : modsMissing.getModList())
        {
            offset += 10;
            this.func_73732_a(this.field_73886_k, String.format("%s : %s", v.getLabel(), v.getRangeString()), this.field_73880_f / 2, offset, 0xEEEEEE);
        }
        super.func_73863_a(p_73863_1_, p_73863_2_, p_73863_3_);
    }
}
