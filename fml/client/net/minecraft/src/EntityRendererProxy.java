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

package net.minecraft.src;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;

public class EntityRendererProxy extends EntityRenderer
{
    public static final String fmlMarker = "This is an FML marker";
    private Minecraft game;

    public EntityRendererProxy(Minecraft minecraft)
    {
        super(minecraft);
        game = minecraft;
    }

    @Override
    public void func_78480_b(float tick)
    {
        super.func_78480_b(tick);
        //This is where ModLoader does all of it's ticking
    }
}
