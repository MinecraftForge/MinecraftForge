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

package net.minecraftforge.oreregistry;

/**
 * An Ore Material represents what a {@link BasicOre} is made of.
 * Examples include diamond and iron. See {@link OreRegistryConstants} for many predefined materials.
 * Create your own material with {@link OreRegistry#addMaterial(String)}.
 */
public final class OreMaterial
{
    private final String uid;

    OreMaterial(String uid)
    {
        this.uid = uid;
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof OreMaterial && uid.equals(((OreMaterial) obj).uid);
    }

    @Override
    public int hashCode()
    {
        return uid.hashCode();
    }

    @Override
    public String toString()
    {
        return uid;
    }
}

