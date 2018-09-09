/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.fml.common;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.IDisplayableError;
import net.minecraftforge.fml.client.gui.GuiMultipleModsErrored;

public class MultipleModsErrored extends EnhancedRuntimeException implements IDisplayableError
{
    public final List<MissingModsException> missingModsExceptions;
    public MultipleModsErrored(List<MissingModsException> missingModsExceptions)
    {
        this.missingModsExceptions = missingModsExceptions;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public GuiScreen createGui()
    {
        return new GuiMultipleModsErrored(this);
    }

    @Override
    protected void printStackTrace(WrappedPrintStream stream)
    {
        for (MissingModsException missingModsException : this.missingModsExceptions)
        {
            missingModsException.printStackTrace(stream);
        }
    }
}
