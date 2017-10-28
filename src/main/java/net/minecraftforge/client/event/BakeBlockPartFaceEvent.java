/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.client.event;

import org.lwjgl.util.vector.Vector3f;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.model.ITransformation;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Events fired when bake methods are called on vanilla json model hierachy part element.
 * Use in conjunction with capabilities to add new functionality to vanila jsons. 
 */
public class BakeBlockPartFaceEvent extends Event
{

    private final Vector3f posFrom;
    private final Vector3f posTo;
    private final BlockPartFace face;
    private final TextureAtlasSprite sprite;
    private final EnumFacing facing;
    private final net.minecraftforge.common.model.ITransformation modelRotationIn;
    private final BlockPartRotation partRotation;
    private final boolean uvLocked;
    private final boolean shade;

    private BakedQuad quad;

    public BakeBlockPartFaceEvent(Vector3f posFrom, Vector3f posTo, BlockPartFace face, TextureAtlasSprite sprite, EnumFacing facing, ITransformation modelRotationIn, BlockPartRotation partRotation, boolean uvLocked, boolean shade, BakedQuad quad)
    {
        this.posFrom = posFrom;
        this.posTo = posTo;
        this.face = face;
        this.sprite = sprite;
        this.facing = facing;
        this.modelRotationIn = modelRotationIn;
        this.partRotation = partRotation;
        this.uvLocked = uvLocked;
        this.shade = shade;
        this.quad = quad;
    }

    public Vector3f getPosFrom()
    {
        return posFrom;
    }

    public Vector3f getPosTo()
    {
        return posTo;
    }

    public BlockPartFace getFace()
    {
        return face;
    }

    public TextureAtlasSprite getSprite()
    {
        return sprite;
    }

    public EnumFacing getFacing()
    {
        return facing;
    }

    public net.minecraftforge.common.model.ITransformation getModelRotationIn()
    {
        return modelRotationIn;
    }

    public BlockPartRotation getPartRotation()
    {
        return partRotation;
    }

    public boolean isUvLocked()
    {
        return uvLocked;
    }

    public boolean isShade()
    {
        return shade;
    }

    public BakedQuad getQuad()
    {
        return quad;
    }

    public void setQuad(BakedQuad quad)
    {
        this.quad = quad;
    }

}
