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

import java.awt.Dimension;

import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.texturepacks.ITexturePack;

public interface ITextureFX
{
    public void onTexturePackChanged(RenderEngine engine, ITexturePack texturepack, Dimension dimensions);

    public void onTextureDimensionsUpdate(int width, int height);
    
    public void setErrored(boolean errored);
    
    public boolean getErrored();
}
