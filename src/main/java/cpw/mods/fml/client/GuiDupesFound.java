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

import java.io.File;
import java.util.Map.Entry;

import net.minecraft.client.gui.GuiErrorScreen;
import cpw.mods.fml.common.DuplicateModsFoundException;
import cpw.mods.fml.common.ModContainer;

public class GuiDupesFound extends GuiErrorScreen
{

    private DuplicateModsFoundException dupes;

    public GuiDupesFound(DuplicateModsFoundException dupes)
    {
        super(null,null);
        this.dupes = dupes;
    }

    @Override
    public void func_73866_w_()
    {
        super.func_73866_w_();
    }
    @Override
    public void func_73863_a(int p_73863_1_, int p_73863_2_, float p_73863_3_)
    {
        this.func_146276_q_();
        int offset = Math.max(85 - dupes.dupes.size() * 10, 10);
        this.func_73732_a(this.field_146289_q, "Forge Mod Loader has found a problem with your minecraft installation", this.field_146294_l / 2, offset, 0xFFFFFF);
        offset+=10;
        this.func_73732_a(this.field_146289_q, "You have mod sources that are duplicate within your system", this.field_146294_l / 2, offset, 0xFFFFFF);
        offset+=10;
        this.func_73732_a(this.field_146289_q, "Mod Id : File name", this.field_146294_l / 2, offset, 0xFFFFFF);
        offset+=5;
        for (Entry<ModContainer, File> mc : dupes.dupes.entries())
        {
            offset+=10;
            this.func_73732_a(this.field_146289_q, String.format("%s : %s", mc.getKey().getModId(), mc.getValue().getName()), this.field_146294_l / 2, offset, 0xEEEEEE);
        }
    }
}
