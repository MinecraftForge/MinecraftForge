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

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.GuiWrongMinecraft;
import net.minecraftforge.fml.client.IDisplayableError;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WrongMinecraftVersionException extends EnhancedRuntimeException implements IDisplayableError
{
    private static final long serialVersionUID = 1L;
    public ModContainer mod;
    private String mcVersion;

    public WrongMinecraftVersionException(ModContainer mod, String mcver)
    {
        super(String.format("Wrong Minecraft version for %s", mod.getModId()));
        this.mod = mod;
        this.mcVersion = mcver;
    }

    @Override
    protected void printStackTrace(WrappedPrintStream stream) {
        stream.println("Wrong Minecraft Versions!");
        stream.println("Mod: " + mod.getModId());
        stream.println("Location: " + mod.getSource().toString());
        stream.println("Expected: " + mod.acceptableMinecraftVersionRange().toString());
        stream.println("Current: " + mcVersion);
        stream.println("");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen createGui()
    {
        return new GuiWrongMinecraft(this);
    }
}
