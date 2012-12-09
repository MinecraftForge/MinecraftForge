/*
 * The FML Forge Mod Loader suite.
 * Copyright (C) 2012 cpw
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package cpw.mods.fml.client;

import java.awt.Dimension;
import java.util.List;
import java.util.logging.Logger;

import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.texturefx.TextureFX;
import net.minecraft.client.texturepacks.ITexturePack;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;

public class FMLTextureFX extends TextureFX implements ITextureFX
{
    public int tileSizeBase = 16;
    public int tileSizeSquare = 256;
    public int tileSizeMask = 15;
    public int tileSizeSquareMask = 255;
    public boolean errored = false;
    protected Logger log = FMLLog.getLogger();

    public FMLTextureFX(int icon)
    {
        super(icon);
    }

    @Override public void setErrored(boolean err){ errored = err; }
    @Override public boolean getErrored(){ return errored; }
    @Override
    public void onTexturePackChanged(RenderEngine engine, ITexturePack texturepack, Dimension dimensions)
    {
        onTextureDimensionsUpdate(dimensions.width, dimensions.height);
    }
    @Override
    public void onTextureDimensionsUpdate(int width, int height)
    {
        tileSizeBase = width >> 4;
        tileSizeSquare = tileSizeBase * tileSizeBase;
        tileSizeMask = tileSizeBase - 1;
        tileSizeSquareMask = tileSizeSquare - 1;
        setErrored(false);
        setup();
    }

    protected void setup()
    {
        field_76852_a = new byte[tileSizeSquare << 2];
    }

    public boolean unregister(RenderEngine engine, List<TextureFX> effects)
    {
        effects.remove(this);
        return true;
    }
}
