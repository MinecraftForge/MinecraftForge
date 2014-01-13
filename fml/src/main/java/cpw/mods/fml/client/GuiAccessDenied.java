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
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;

public class GuiAccessDenied extends GuiScreen
{
    private GuiScreen parent;
    private ServerData data;
    public GuiAccessDenied(GuiScreen parent, ServerData data)
    {
        this.parent = parent;
        this.data = data;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void func_73866_w_()
    {
        this.field_146292_n.add(new GuiButton(1, this.field_146294_l / 2 - 75, this.field_146295_m - 38, I18n.func_135052_a("gui.done")));
    }

    @Override
    protected void func_146284_a(GuiButton p_73875_1_)
    {
        if (p_73875_1_.field_146124_l && p_73875_1_.field_146127_k == 1)
        {
            FMLClientHandler.instance().showGuiScreen(parent);
        }
    }
    @Override
    public void func_73863_a(int p_73863_1_, int p_73863_2_, float p_73863_3_)
    {
        this.func_146276_q_();
        int offset = Math.max(85 - 2 * 10, 10);
        this.func_73732_a(this.field_146289_q, "Forge Mod Loader could not connect to this server", this.field_146294_l / 2, offset, 0xFFFFFF);
        offset += 10;
        this.func_73732_a(this.field_146289_q, String.format("The server %s has forbidden modded access", data.field_78847_a), this.field_146294_l / 2, offset, 0xFFFFFF);
        super.func_73863_a(p_73863_1_, p_73863_2_, p_73863_3_);
    }
}
