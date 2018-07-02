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

package net.minecraftforge.common.capabilities.context;

// Not an enum to allow mods to add more possible body parts
public class EntityPart
{
    // Generic parts
    public static final EntityPart HEAD = new EntityPart("head");
    public static final EntityPart CHEST = new EntityPart("chest");
    public static final EntityPart FEET = new EntityPart("feet");

    // Biped parts
    public static final EntityPart ARMS = new EntityPart("arms");
    public static final EntityPart LEGS = new EntityPart("legs");

    // Quadruped parts
    public static final EntityPart FRONT_LEGS = new EntityPart("front_legs");
    public static final EntityPart HIND_LEGS = new EntityPart("hind_legs");

    // Other parts
    public static final EntityPart TAIL = new EntityPart("tail");

    public final String name;

    public EntityPart(String name)
    {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof EntityPart && equals((EntityPart)obj);
    }

    public boolean equals(EntityPart other)
    {
        return (name== null && other.name == null) || (name != null && name.equals(other.name));
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    public boolean isHands()
    {
        return equals(ARMS);
    }

    public boolean isArmor()
    {
        return equals(HEAD) || equals(CHEST) || equals(LEGS) || equals(FEET);
    }
}
