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

package net.minecraftforge.items.templates;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IExtractionManager;

import javax.annotation.Nonnull;

public class VoidExtractionManager implements IExtractionManager
{
    public static final VoidExtractionManager INSTANCE = new VoidExtractionManager();

    private VoidExtractionManager()
    {
    }

    @Override
    public int extract(@Nonnull ItemStack stack)
    {
        return stack.getCount();
    }

    @Override
    public void extractedStack(@Nonnull ItemStack stack, int slot)
    {

    }

    @Override
    public boolean satisfied()
    {
        return false;
    }
}
