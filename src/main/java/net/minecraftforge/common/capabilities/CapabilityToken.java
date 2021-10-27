/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.common.capabilities;

/**
 * Inspired by {@link com.google.common.reflect.TypeToken TypeToken}, use a subclass to capture
 * generic types. Then uses {@link net.minecraftforge.common.asm.CapabilityTokenSubclass a transformer}
 * to convert that generic into a string returned by {@link #getType}
 * This allows us to know the generic type, without having a hard reference to the
 * class.
 *
 * Example usage:
 * <pre>{@code
 *    public static Capability<IDataHolder> DATA_HOLDER_CAPABILITY
 *    		= CapabilityManager.get(new CapabilityToken<>(){});
 *
 * </pre>
 *
 */
public abstract class CapabilityToken<T>
{
    protected final String getType()
    {
        throw new RuntimeException("This will be implemented by a transformer");
    }

    @Override
    public String toString()
    {
        return "CapabilityToken[" + getType() + "]";
    }
}
